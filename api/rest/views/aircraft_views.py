import os
import json
import requests
import time

from django.contrib.auth.decorators import login_required

from rest.settings import db
from django.http import *
from django.views.decorators.csrf import csrf_exempt


@csrf_exempt
def get_all(request):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    aircrafts = db.aircrafts.find({})

    if not aircrafts:
        return JsonResponse({'error': 'Aircrafts collection is empty.'})

    aircrafts_list = []

    for aircraft in aircrafts:
        aircraft['_id'] = str(aircraft['_id'])
        aircrafts_list.append(aircraft)

    return JsonResponse(aircrafts_list, safe=False)


# function gets all of the aircraft data from the database
@csrf_exempt
def get_aircraft(request, aircraft_registration):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    aircraft = db.aircrafts.find_one({'registration': aircraft_registration})

    if not aircraft:
        return JsonResponse({'error': 'Aircraft not found.'})

    aircraft['_id'] = str(aircraft['_id'])

    return JsonResponse(aircraft)


# function inserts an aircraft into the database
@csrf_exempt
def insert_aircraft(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)
    registration_number = data['registration']
    # if the aircraft already exists, redirect to PUT URL
    if db.aircrafts.find_one({'registration': registration_number}):
        requests.put(os.environ.get('SERVER_URL') + 'api/aircraft/put/', json=data)
        return JsonResponse({'message': 'Redirected to PUT'})
    else:
        data['created'] = int(time.time())
        data['modified'] = int(time.time())
        db.aircrafts.insert_one(data)

    return JsonResponse({'message': 'Aircraft inserted successfully.'})


# function updates an aircraft in the database
@csrf_exempt
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
@csrf_exempt
def delete_aircraft(request, aircraft_registration):
    if request.method != 'DELETE':
        return JsonResponse({'error': 'Unsupported request method.'})

    if not db.aircrafts.delete_one({'registration': aircraft_registration}):
        return JsonResponse({'error': 'Could not delete'})

    return JsonResponse({'message': 'Aircraft deleted successfully.'})
