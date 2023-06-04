import os
import json
import requests
import time

from bson import ObjectId
from rest.settings import db
from django.http import *


def get_all(request):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    airlines = db.airlines.find({})

    if not airlines:
        return JsonResponse({'error': 'Aircrafts collection is empty.'})

    airlines_list = []

    for airline in airlines:
        airline['_id'] = str(airline['_id'])
        airlines_list.append(airline)

    return JsonResponse(airlines_list, safe=False)


# function returns a JSON object of an airline that matches the given ICAO code
def get_airline(request, airline_icao):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    airline = db.airlines.find_one({'code.icao': airline_icao})

    if not airline:
        return JsonResponse({'message': 'Airline not found.'})

    airline['_id'] = str(airline['_id'])

    return JsonResponse(airline)


# function inserts an airline into the database
def insert_airline(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)

    print(data)

    if data['code'] is not None:
        if db.airlines.find_one({'code.icao': data['code']['icao']}):
            requests.put(os.environ.get('SERVER_URL') + 'api/airline/put/', json=data)
            return JsonResponse({'message': 'Redirected to PUT.'})
        else:
            data['created'] = int(time.time())
            data['modified'] = int(time.time())
            db.airlines.insert_one(data)

    return JsonResponse({'message': 'Airline inserted successfully!'})


# function updates an airline in the database
def update_airline(request):
    if request.method != 'PUT':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)

    data['modified'] = int(time.time())

    airline_id = data.pop('_id', None)
    if airline_id:
        airline_id = ObjectId(airline_id)

        db.airlines.update_one(
            {'_id': airline_id},
            {'$set': data}
        )

        return JsonResponse({'message': 'Airline updated successfully!'})
    else:
        return JsonResponse({'error': 'Invalid airline ID.'})


# function deletes an airline with a matching ICAO code from the database
def delete_airline(request, airline_id):
    if request.method != 'DELETE':
        return JsonResponse({'error': 'Unsupported request method.'})

    if not db.airlines.delete_one({'_id': airline_id}):
        return JsonResponse({'error': 'Could not delete'})

    return JsonResponse({'message': 'Airline deleted successfully.'})
