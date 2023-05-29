import os
import json
import requests
import time

from rest.settings import db
from django.http import *


def get_all(request):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    airports = db.airports.find({})

    if not airports:
        return JsonResponse({'error': 'Aircrafts collection is empty.'})

    airports_list = []

    for airport in airports:
        airport['_id'] = str(airport['_id'])
        airports_list.append(airport)

    return JsonResponse(airports_list, safe=False)


# function returns a JSON object of an airport
def get_airport(request, airport_icao):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    airport = db.airports.find_one({'code.icao': airport_icao})

    if not airport:
        return JsonResponse({'message': 'Airport not found.'})

    airport['_id'] = str(airport['_id'])

    return JsonResponse(airport)


# function inserts an airport into the database
def insert_airport(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)

    if db.airports.find_one({'code.icao': data['code']['icao']}):
        requests.put(os.environ.get('SERVER_URL') + 'api/airport/put/', json=data)
        return JsonResponse({'message': 'Redirected to PUT'})
    else:
        data['created'] = int(time.time())
        data['modified'] = int(time.time())
        db.airports.insert_one(data)

    return JsonResponse({'message': 'Airport inserted successfully!'})


# function updates an existing airport in the database
def update_airport(request):
    if request.method != 'PUT':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)

    data['modified'] = int(time.time())

    db.airports.update_one(
        {'code.icao': data['code']['icao']},
        {'$set': data},
        upsert=True
    )

    return JsonResponse({'message': 'Airport updated successfully!'})


# function deletes an airport with a matching ICAO code from the database
def delete_airport(request, airport_icao):
    if request.method != 'DELETE':
        return JsonResponse({'error': 'Unsupported request method.'})

    if not db.airports.delete_one({'code.icao': airport_icao}):
        return JsonResponse({'error': 'Could not delete'})

    return JsonResponse({'message': 'Airport deleted successfully.'})
