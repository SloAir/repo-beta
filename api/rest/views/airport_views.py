import os
import json
import requests
import time

from rest.settings import db
from django.http import *
from django.middleware.csrf import get_token
from django.views.decorators.csrf import csrf_protect


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
@csrf_protect
def insert_airport(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)

    csrf_token = get_token(request)
    headers = {
        'X-CSRFToken': csrf_token,
        'Cookie': 'csrftoken={}'.format(csrf_token)
    }

    if db.airports.find_one({'code.icao': data['code']['icao']}):
        requests.put(os.environ.get('SERVER_URL') + 'api/airport/put/', json=data, headers=headers)
        return JsonResponse({'message': 'Redirected to PUT'})
    else:
        data['created'] = int(time.time())
        data['modified'] = int(time.time())
        db.airports.insert_one(data)

    return JsonResponse({'message': 'Airport inserted successfully!'})


# function updates an existing airport in the database
@csrf_protect
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
@csrf_protect
def delete_airport(request, airport_icao):
    if request.method != 'DELETE':
        return JsonResponse({'error': 'Unsupported request method.'})

    if not db.airports.delete_one({'code.icao': airport_icao}):
        return JsonResponse({'error': 'Could not delete'})

    return JsonResponse({'message': 'Airport deleted successfully.'})
