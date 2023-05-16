import os
import json
import requests
import time

from rest.settings import db
from django.http import *
from django.middleware.csrf import get_token
from django.views.decorators.csrf import csrf_protect


# function returns a JSON object of a flight that matches the given flight ID
def get_flight(request, flight_id):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    flight = db.flights.find_one({'identification.id': flight_id})

    if not flight:
        return JsonResponse({'message': 'Flight not found.'})

    flight['_id'] = str(flight['_id'])

    return JsonResponse(flight)


# function inserts a flight into the database
@csrf_protect
def insert_flight(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)

    csrf_token = get_token(request)
    headers = {
        'X-CSRFToken': csrf_token,
        'Cookie': 'csrftoken={}'.format(csrf_token)
    }

    if db.flights.find_one({'identification.id': data['identification']['id']}):
        requests.put(os.environ.get('SERVER_URL') + 'api/flight/put/', json=data, headers=headers)
        return JsonResponse({'message': 'Redirected to PUT'})
    else:
        data['created'] = int(time.time())
        data['modified'] = int(time.time())
        db.flights.insert_one(data)

    return JsonResponse({'message': 'Flight inserted successfully!'})


# function updates a flight in the database
@csrf_protect
def update_flight(request):
    if request.method != 'PUT':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)

    data['modified'] = int(time.time())

    # Update all fields except 'trail'
    # then add trail from the JSON
    db.flights.update_one(
        {'identification.id': data['identification']['id']},
        {
            '$set': {k: v for k, v in data.items() if k != 'trail'},
            '$push': {'trail': {'$each': [data['trail'][0]], '$position': 0}}
        },
        upsert=True
    )

    return JsonResponse({'message': 'Flight updated successfully!'})


# function deletes a flight with a matching flight ID from the database
@csrf_protect
def delete_flight(request, flight_id):
    if request.method != 'DELETE':
        return JsonResponse({'error': 'Unsupported request method.'})

    if not db.flights.delete_one({'identification.id': flight_id}):
        return JsonResponse({'error': 'Could not delete'})

    return JsonResponse({'message': 'Flight deleted successfully.'})
