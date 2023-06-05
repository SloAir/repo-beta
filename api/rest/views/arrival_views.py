import os
import json
import requests
import time

from rest.settings import db
from django.http import *


def get_all(request):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    arrivals = db.arrivals.find({})

    if not arrivals:
        return JsonResponse({'error': 'Arrivals collection is empty.'})

    arrivals_list = []

    for arrival in arrivals:
        arrival['_id'] = str(arrival['_id'])
        arrivals_list.append(arrival)

    return JsonResponse(arrivals_list, safe=False)


def get_arrivals(request, date):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    schedule = db.arrivals.find_one({'date': date})

    if not schedule:
        return JsonResponse({'error': 'Schedule not found.'})

    schedule['_id'] = str(schedule['_id'])

    return JsonResponse(schedule)


def insert_arrivals(request):
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

    if db.arrivals.find_one({'date': data['date']}):
        requests.put(os.environ.get('SERVER_URL') + 'api/arrivals/put/', json=data)
        return JsonResponse({'message': 'Redirected to PUT'})
    else:
        data['created'] = int(time.time())
        data['modified'] = int(time.time())
        db.arrivals.insert_one(data)

    return JsonResponse({'message': 'Arrivals inserted successfully!'})


def update_arrivals(request):
    if request.method != 'PUT':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)

    data['modified'] = int(time.time())

    db.arrivals.update_one(
        {'date': data['date']},
        {'$set': {k: v for k, v in data.items()}},
        upsert=True
    )

    return JsonResponse({'message': 'Arrivals updated successfully!'})


def delete_arrivals(request, date):
    if request.method != 'DELETE':
        return JsonResponse({'error': 'Unsupported request method.'})

    if not db.arrivals.delete_one({'date': date}):
        return JsonResponse({'error': 'Could not delete'})

    return JsonResponse({'message': 'Arrivals deleted successfully.'})
