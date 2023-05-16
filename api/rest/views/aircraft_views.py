import os
import json
import requests
import time

from rest.settings import db
from django.http import *
from django.middleware.csrf import get_token
from django.views.decorators.csrf import csrf_protect


def get_all(request):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    aircrafts = db.aircrafts.find({})
    print(type(aircrafts))

    if not aircrafts:
        return JsonResponse({'message': 'Aircrafts collection is empty.'})

    aircrafts_dict = {}
    for aircraft in aircrafts:
        aircraft['_id'] = str(aircraft['_id'])
        aircrafts_dict.update({aircraft['_id']: aircraft})

    print(type(aircrafts_dict))

    return JsonResponse(aircrafts_dict)


# function gets all of the aircraft data from the database
def get_aircraft(request, aircraft_registration):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    aircraft = db.aircrafts.find_one({'registration': aircraft_registration})

    if not aircraft:
        return JsonResponse({'message': 'Aircraft not found.'})

    aircraft['_id'] = str(aircraft['_id'])

    return JsonResponse(aircraft)


# function inserts an aircraft into the database
@csrf_protect
def insert_aircraft(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)
    registration_number = data['registration']

    csrf_token = get_token(request)
    headers = {
        'X-CSRFToken': csrf_token,
        'Cookie': 'csrftoken={}'.format(csrf_token)
    }

    # if the aircraft already exists, redirect to PUT URL
    if db.aircrafts.find_one({'registration': registration_number}):
        requests.put(os.environ.get('SERVER_URL') + 'api/aircraft/put/', json=data, headers=headers)
        return JsonResponse({'message': 'Redirected to PUT'})
    else:
        data['created'] = int(time.time())
        data['modified'] = int(time.time())
        db.aircrafts.insert_one(data)

    return JsonResponse({'message': 'Aircraft inserted successfully.'})


# function updates an aircraft in the database
@csrf_protect
def update_aircraft(request):
    if request.method != 'PUT':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)
    data['modified'] = int(time.time())

    db.aircrafts.update_one(
        {'registration': data['registration']},
        {
            '$set': {k: v for k, v in data.items() if k != 'flightHistory'},
            '$addToSet': {'flightHistory': {'flightId': data['flightHistory'][0]['flightId']}}
        }
    )

    return JsonResponse({'message': 'Aircraft updated successfully!'})


# function deletes an aircraft from the database that matches the aircraft's registration number
@csrf_protect
def delete_aircraft(request, aircraft_registration):
    if request.method != 'DELETE':
        return JsonResponse({'error': 'Unsupported request method.'})

    if not db.aircrafts.delete_one({'registration': aircraft_registration}):
        return JsonResponse({'error': 'Could not delete'})

    return JsonResponse({'message': 'Aircraft deleted successfully.'})
