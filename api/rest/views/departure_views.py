import os
import json
import requests
import time

from rest.settings import db
from django.http import *


def get_all(request):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    departures = db.departures.find({})

    if not departures:
        return JsonResponse({'error': 'Departures collection is empty.'})

    departures_list = []

    for departure in departures:
        departure['_id'] = str(departure['_id'])
        departures_list.append(departure)

    return JsonResponse(departures_list, safe=False)


def get_departures(request, date):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    schedule = db.departures.find_one({'date': date})

    if not schedule:
        return JsonResponse({'error': 'Schedule not found.'})

    schedule['_id'] = str(schedule['_id'])

    return JsonResponse(schedule)


def insert_departures(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)

    fields = [
        'type',
        'id',
        'date'
    ]

    for index, schedule in enumerate(data['schedule']):
        for field in fields:
            if field in schedule:
                del data['schedule'][index][field]

    if db.departures.find_one({'date': data['date']}):
        requests.put(os.environ.get('SERVER_URL') + 'api/departures/put/', json=data)
        return JsonResponse({'message': 'Redirected to PUT'})
    else:
        data['created'] = int(time.time())
        data['modified'] = int(time.time())
        db.departures.insert_one(data)

    return JsonResponse({'message': 'Departures inserted successfully!'})


def update_departures(request):
    if request.method != 'PUT':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)

    data['modified'] = int(time.time())

    db.departures.update_one(
        {'date': data['date']},
        {'$set': {k: v for k, v in data.items()}},
        upsert=True
    )

    return JsonResponse({'message': 'Departures updated successfully!'})


def delete_departures(request, date):
    if request.method != 'DELETE':
        return JsonResponse({'error': 'Unsupported request method.'})

    if not db.departures.delete_one({'date': date}):
        return JsonResponse({'error': 'Could not delete'})

    return JsonResponse({'message': 'Departures deleted successfully.'})
