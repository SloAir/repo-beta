import os
import json
import requests
import time

from django.contrib.auth.decorators import login_required

from rest.settings import db
from django.http import *
from django.middleware.csrf import get_token
from django.views.decorators.csrf import csrf_protect


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
@csrf_protect
@login_required
def insert_airline(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)

    if db.airlines.find_one({'code.icao': data['code']['icao']}):
        requests.put(os.environ.get('SERVER_URL') + 'api/airline/put/', json=data)
        return JsonResponse({'message': 'Redirected to PUT.'})
    else:
        data['created'] = int(time.time())
        data['modified'] = int(time.time())
        db.airlines.insert_one(data)

    return JsonResponse({'message': 'Airline inserted successfully!'})


# function updates an airline in the database
@csrf_protect
@login_required
def update_airline(request):
    if request.method != 'PUT':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)

    data['modified'] = int(time.time())

    db.airlines.update_one(
        {'code.icao': data['code']['icao']},
        {'$set': data}
    )

    return JsonResponse({'message': 'Airline updated successfully!'})


# function deletes an airline with a matching ICAO code from the database
@csrf_protect
@login_required
def delete_airline(request, airline_icao):
    if request.method != 'DELETE':
        return JsonResponse({'error': 'Unsupported request method.'})

    if not db.airlines.delete_one({'code.icao': airline_icao}):
        return JsonResponse({'error': 'Could not delete'})

    return JsonResponse({'message': 'Airline deleted successfully.'})
