import os
import json
import requests
import time
import rest.radar as radar

from rest.settings import db
from dotenv import load_dotenv
from django.http import *
from django.middleware.csrf import get_token
from django.views.decorators.csrf import csrf_exempt, csrf_protect

load_dotenv()


# function returns a processed JSON object of an aircraft as a dictionary
def process_aircraft_data(data):
    aircraft_data = data['aircraft']

    # return None, if no info
    if not aircraft_data:
        return None

    # list of unnecessary fields we want to delete
    unnecessary_fields = [
        'countryId',
        'age',
        'msn',
        'hex'
    ]

    for k in unnecessary_fields:
        if k in aircraft_data:
            # delete unnecessary fields
            del aircraft_data[k]

    # check if 'images' field exists
    if aircraft_data['images']:
        image_fields = [
            'large',
            'medium',
            'thumbnails'
        ]

        for k in image_fields:
            if k in aircraft_data['images']:
                images = aircraft_data['images'][k]
                del aircraft_data['images']
                aircraft_data['images'] = images

    aircraft_data['flightHistory'] = [{'flightId': data['identification']['id']}]

    return aircraft_data


# function returns a processed JSON object of the origin airport as a dictionary
def process_origin_airport_data(data):
    # return None, if no info
    if not data['airport']['origin']:
        return None

    return data['airport']['origin']


# function returns a processed JSON object of the destination airport as a dictionary
def process_destination_airport_data(data):
    # return None, if no info
    if not data['airport']['destination']:
        return None

    return data['airport']['destination']


# function returns a processed JSON object of an airline as a dictionary
def process_airline_data(data):
    # return None, if no info
    if not data['airline']:
        return None

    return data['airline']


# function returns a processed JSON object of the flight as a dictionary
def process_flight_data(data):
    flight_data = data

    # return None, if no info
    if not flight_data:
        return None

    # check if 'identification' field exists
    if 'identification' in flight_data:
        # list of fields we don't need and want to delete
        identification_fields = [
            'row',
            'number'
        ]

        # check if all of the fields inside of the 'indentification' field exist
        for k in identification_fields:
            if k in flight_data['identification']:
                # delete unnecessary fields
                del flight_data['identification'][k]

    # check if 'status' field exists
    if 'status' in flight_data:
        # list of fields we don't need and want to delete
        status_fields = [
            'text',
            'icon',
            'estimated',
            'ambiguous',
            'generic'
        ]

        # check if all of the fields inside of the 'status' field exist
        for k in status_fields:
            if k in flight_data['status']:
                # delete unnecessary fields
                del flight_data['status'][k]

    # list of fields we don't need and want to delete
    other_fields = [
        'airport',
        'level',
        'promote',
        'aircraft',
        'flightHistory',
        'ems',
        'availability',
        's'
    ]

    for k in other_fields:
        # check if fields exist inside of the data JSON
        if k in flight_data:
            del flight_data[k]

    return data


# function returns JSON data of all the flights above Slovenian airspace
def get_all(request):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    si_flight_details = radar.get_data()

    # data = fr.get_flight_details('301962bc')
    data_array = []

    csrf_token = get_token(request)
    headers = {
        'X-CSRFToken': csrf_token,
        'Cookie': 'csrftoken={}'.format(csrf_token)
    }

    # process all of the data; clean-up and add to an array of dictionaries
    for si_flight in si_flight_details:
        print(csrf_token)

        aircraft_data = process_aircraft_data(si_flight)
        origin_airport_data = process_origin_airport_data(si_flight)
        destination_airport_data = process_destination_airport_data(si_flight)
        airline_data = process_airline_data(si_flight)
        flight_data = process_flight_data(si_flight)

        data_json = {
            'aircraft': aircraft_data,
            'originAirport': origin_airport_data,
            'destinationAirport': destination_airport_data,
            'airline': airline_data,
            'flight': flight_data
        }

        # send POST requests to the routes for insertion
        requests.post(os.environ.get('SERVER_URL') + 'api/aircraft/post/', json=aircraft_data, headers=headers)
        requests.post(os.environ.get('SERVER_URL') + 'api/airline/post/', json=airline_data, headers=headers)
        requests.post(os.environ.get('SERVER_URL') + 'api/airport/post/', json=origin_airport_data, headers=headers)
        requests.post(os.environ.get('SERVER_URL') + 'api/airport/post/', json=destination_airport_data, headers=headers)
        requests.post(os.environ.get('SERVER_URL') + 'api/flight/post/', json=flight_data, headers=headers)

        data_array.append(data_json)

    # safe=false -> data is NOT a dictionary
    response = JsonResponse(data_array, safe=False)

    return response


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
def insert_airline(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)

    csrf_token = get_token(request)
    headers = {
        'X-CSRFToken': csrf_token,
        'Cookie': 'csrftoken={}'.format(csrf_token)
    }

    if db.airlines.find_one({'code.icao': data['code']['icao']}):
        requests.put(os.environ.get('SERVER_URL') + 'api/airline/put/', json=data, headers=headers)
        return JsonResponse({'message': 'Redirected to PUT.'})
    else:
        data['created'] = int(time.time())
        data['modified'] = int(time.time())
        db.airlines.insert_one(data)

    return JsonResponse({'message': 'Airline inserted successfully!'})


# function updates an airline in the database
@csrf_protect
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
def delete_airline(request, airline_icao):
    if request.method != 'DELETE':
        return JsonResponse({'error': 'Unsupported request method.'})

    if not db.airlines.delete_one({'code.icao': airline_icao}):
        return JsonResponse({'error': 'Could not delete'})

    return JsonResponse({'message': 'Airline deleted successfully.'})


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
