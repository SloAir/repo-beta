import os
import json
import requests
import logging

from datetime import datetime
from dotenv import load_dotenv
from django.http import *
from .radar import *
from django.views.decorators.csrf import csrf_exempt

load_dotenv()
logger = logging.getLogger(__name__)


# function returns a processed JSON object of an aircraft as a dictionary
def process_aircraft_data(data):
    aircraft_data = data['aircraft']

    if not all(aircraft_data.get(k) for k in ('countryId', 'age', 'msn')):
        logger.warning('Missing \'aircraft\' field.')

    # remove unnecessary data from aircraft
    del aircraft_data['countryId']
    del aircraft_data['age']
    del aircraft_data['msn']

    if not aircraft_data['images']:
        logger.warning('Missing \'images\' field.')

    images = aircraft_data.get('images', {})

    if not all(images.get(k) for k in ('thumbnails', 'medium', 'large')):
        logger.warning('Missing \'images\' field.')

    del aircraft_data['images']['thumbnails']
    del aircraft_data['images']['medium']

    # move images from ['images']['large'] to ['images']
    images = aircraft_data['images']['large']
    del aircraft_data['images']['large']
    aircraft_data['images'] = images

    if not aircraft_data['hex']:
        logger.warning('Missing \'hex\' field.')

    del aircraft_data['hex']

    aircraft_data['flightHistory'] = [{'flightId': data['identification']['id']}]

    return aircraft_data['aircraft']


# function returns a processed JSON object of the origin airport as a dictionary
def process_origin_airport_data(data):
    if not data['airport']['origin']:
        logger.warning('Missing \'origin\' field.')

    return data['airport']['origin']


# function returns a processed JSON object of the destination airport as a dictionary
def process_destination_airport_data(data):
    if not data['airport']['destination']:
        logger.warning('Missing \'destination\' field.')

    return data['airport']['destination']


# function returns a processed JSON object of an airline as a dictionary
def process_airline_data(data):
    if not data['airline']:
        logger.warning('Missing \'airline\' field.')

    return data['airline']


# function returns a processed JSON object of the flight as a dictionary
def process_flight_data(data):
    flight_data = data

    identification = flight_data.get('identification', {})

    # check if all of the fields we are trying to delete exist
    if not all(identification.get(k) for k in ('row', 'number')):
        logger.warning('Missing \'identification\' field.')

    # delete unnecessary data from identification
    del flight_data['identification']['row']
    del flight_data['identification']['number']

    status = flight_data.get('status', {})

    # check if all of the fields we are trying to delete exist
    if not all(status.get(k) for k in ('text', 'icon', 'estimated', 'ambiguous', 'generic')):
        logger.warning('Missing \'status\' field.')

    # delete unnecessary data from status
    del flight_data['status']['text']
    del flight_data['status']['icon']
    del flight_data['status']['estimated']
    del flight_data['status']['ambiguous']
    del flight_data['status']['generic']

    # check if 'airport' field exists
    if not flight_data['airport']:
        logger.warning('Missing \'airport\' field.')

    # remove unnecessary airport data
    del flight_data['airport']

    # remove more unnecessary data
    if not flight_data['level']:
        logger.warning('Missing \'level\' field.')

    del flight_data['level']

    if not flight_data['promote']:
        logger.warning('Missing \'promote\' field.')

    del flight_data['promote']

    if not flight_data['aircraft']:
        logger.warning('Missing \'aircraft\' field.')

    # remove unnecessary data from aircraft
    del flight_data['aircraft']

    if not flight_data['flightHistory']:
        logger.warning('Missing \'flightHistory\' field.')

    # remove flight history
    del flight_data['flightHistory']

    # remove more unnecessary data
    if not flight_data['ems']:
        logger.warning('Missing \'ems\' field.')

    del flight_data['ems']

    if not flight_data['availability']:
        logger.warning('Missing \'availability\' field.')

    del flight_data['availability']

    if not flight_data['s']:
        logger.warning('Missing \'s\' field.')

    del flight_data['s']

    return data


# function returns JSON data of all the flights above Slovenian airspace
def get_all(request):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})
    
    # get all existing flight_id from the DB
    collection = db["flights"]
    projection = {"flight_id": 1}
    db_flights = collection.find({}, projection)

    # get all predefined zones -> extract Central Europe
    zones = fr.get_zones()
    ceur_bounds = fr.get_bounds(zones["europe"]["subzones"]["ceur"])

    # setup custom bounds for Slovenia
    si_bounds = '46.66,13.51,45.66,16.19'
    lat_max, lon_min, lat_min, lon_max = map(float, si_bounds.split(','))

    # get Central Europe flights
    ceur_flights = fr.get_flights(bounds=ceur_bounds)

    # get flight_id for flights in SLO airspace ATM
    si_flights = []
    for flight in ceur_flights:
        if lat_min <= flight.latitude <= lat_max and lon_min <= flight.longitude <= lon_max:
            si_flights.append(flight.id)

    # time_intervals for timestamps
    time_interval = 120
    cum_interval = 150

    # array of flight_details for all flight in Slo airspace ATM -> will be cleaned-up
    si_flight_details = []

    for si_flight_id in si_flights:
        # get_flight_details
        # details = fr.get_flight_details(si_flight_id)
        details = fr.get_flight_details(si_flight_id)

        # "aircraft" is a GROUND vehicle -> ignore
        if details["aircraft"]["model"]["code"] == 'GRND':
            continue
        
        # adjust time intervals to the amount of data inside the trail -> COULD CHANGE!
        trail_len = len(details["trail"])
        if trail_len > 100:
            time_interval = 180
            cum_interval = 240

        # eliminate unmatching trail if flight was close to leaving bounds; 
        # got matched a few seconds ago -> trail updates every few seconds
        valid_index = 0
        for i in range(trail_len):
            valid_index = i
            if lat_min <= details["trail"][i]['lat'] <= lat_max and lon_min <= details["trail"][i]['lng'] <= lon_max:
                break
        if valid_index==trail_len:
            details["trail"] = []
            break
        else:
            details["trail"] = details["trail"][i:]

        # if flight exists; updated trail and add to si_flight_details
        if si_flight_id in db_flights:
            trail_len = len(details["trail"])

            # get first trail object inside bounds
            new_trail = {}
            for i in range(trail_len):
                if lat_min <= details["trail"][i]['lat'] <= lat_max and lon_min <= details["trail"][i]['lng'] <= lon_max:
                    new_trail = details["trail"][i]
                    break

            # updated_flight = collection.find_one(filter)
            # si_flight_details.append(updated_flight)

        # if flight does not exist
        else:
            valid_trail = []

            # cumulative diff -> if diff < time_interval
            cum_diff = 0

            for i in range(trail_len):
                if lat_min <= details["trail"][i]['lat'] <= lat_max and lon_min <= details["trail"][i]['lng'] <= lon_max:
                    if i == 0:
                        valid_trail.append(details["trail"][i])
                    else:
                        diff = details["trail"][i-1]['ts']-details["trail"][i]['ts']
                        if diff >= time_interval:
                                if cum_diff + diff >= cum_interval:
                                    valid_trail.append(details["trail"][i-1])
                                else:
                                    valid_trail.append(details["trail"][i])
                                cum_diff = 0
                        else:
                                cum_diff += diff
                                if cum_diff >= time_interval:
                                    valid_trail.append(details["trail"][i])
                                    cum_diff = 0
                else:
                    break

            details["trail"] = valid_trail
            si_flight_details.append(details)

    # token
    csrf_token = request.COOKIES.get('csrftoken')

    headers = {
        'X-CSRFToken': csrf_token
    }

    # data = fr.get_flight_details('301962bc')
    data_array = []

    # process all of the data; clean-up and add to an array of dictionaries
    for si_flight in si_flight_details:
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

        data_array.append(data_json)

        requests.post(os.environ.get('SERVER_URL') + 'api/aircraft/post/', json=aircraft_data, headers=headers)
        requests.post(os.environ.get('SERVER_URL') + 'api/airline/post/', json=airline_data, headers=headers)
        requests.post(os.environ.get('SERVER_URL') + 'api/flight/post/', json=flight_data, headers=headers)

    # safe=false -> data is NOT a dictionary
    return JsonResponse(data_array, safe=False)


# function gets all of the aircraft data from the database
@csrf_exempt
def get_aircraft(request, aircraft_registration):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    aircraft = db.aircrafts.find_one({'registration': aircraft_registration})

    if aircraft:
        aircraft['_id'] = str(aircraft['_id'])

    return JsonResponse(aircraft)


# function inserts the aircraft into the database
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
        data['created'] = datetime.now()
        db.aircrafts.insert_one(data)

    return JsonResponse({'message': 'Aircraft inserted successfully.'})


# function updates the aircraft in the database
@csrf_exempt
def update_aircraft(request):
    if request.method != 'PUT':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)
    data['modified'] = datetime.now()
    flight_id = data['flightId']
    del data['flightId']

    db.aircrafts.update_one(
        {'registration': data['registration']},
        {
            '$set': {k: v for k, v in data.items() if k != 'flightHistory'},
            '$push': {'flightHistory': {'$each': [flight_id], '$position': 0}}
        }
    )

    return JsonResponse({'message': 'Aircraft updated successfully!'})


@csrf_exempt
def delete_aircraft(request, aircraft_registration):
    if request.method != 'DELETE':
        return JsonResponse({'error': 'Unsupported request method.'})

    if not db.aircrafts.delete_one({'registration': aircraft_registration}):
        return JsonResponse({'error': 'Could not delete'})

    return JsonResponse({'message': 'Aircraft deleted successfully.'})


# function returns a JSON object of an airline that matches the given ICAO code
@csrf_exempt
def get_airline(request, airline_icao):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    airline = db.airlines.find_one({'code.icao': airline_icao})

    if airline:
        airline['_id'] = str(airline['_id'])

    return JsonResponse(airline)


# function inserts an airline into the database
@csrf_exempt
def insert_airline(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)

    if db.airlines.find_one({'code.icao': data['code']['icao']}):
        requests.put(os.environ.get('SERVER_URL') + 'api/airline/put/', json=data)
        return JsonResponse({'message': 'Redirected to PUT.'})
    else:
        data['created'] = datetime.now()
        db.airlines.insert_one(data)

    return JsonResponse({'message': 'Airline inserted successfully!'})


@csrf_exempt
def update_airline(request):
    if request.method != 'PUT':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)

    data['modified'] = datetime.now()
    db.airlines.update_one(
        {'code.icao': data['code']['icao']},
        {'$set': data}
    )

    return JsonResponse({'message': 'Airline updated successfully!'})


@csrf_exempt
def delete_airline(request, airline_icao):
    if request.method != 'DELETE':
        return JsonResponse({'error': 'Unsupported request method.'})

    if not db.airlines.delete_one({'code.icao': airline_icao}):
        return JsonResponse({'error': 'Could not delete'})

    return JsonResponse({'message': 'Airline deleted successfully.'})


# function returns a JSON object of a flight that matches the given flight ID
@csrf_exempt
def get_flight(request, flight_id):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    flight = db.flights.find_one({'identification.id': flight_id})

    if flight:
        flight['_id'] = str(flight['_id'])

    return JsonResponse(flight)


# function inserts a flight into the database
@csrf_exempt
def insert_flight(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)
    flight_id = data['identification']['id']

    if db.flights.find_one({'identification.id': flight_id}):
        requests.put(os.environ.get('SERVER_URL') + 'api/flight/put/', json=data)
        return JsonResponse({'message': 'Redirected to put'})
    else:
        data['created'] = datetime.now()
        db.flights.insert_one(data)

    return JsonResponse({'message': 'Flight inserted successfully!'})


# function updates the flight in the database
@csrf_exempt
def update_flight(request):
    if request.method != 'PUT':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)

    data['modified'] = datetime.now()

    # Update all fields except 'trail'
    # then add trail from the 
    db.flights.update_one(
        {'identification.id': data['identification']['id']},
        {
            '$set': {k: v for k, v in data.items() if k != 'trail'},
            '$push': {'trail': {'$each': [data['trail'][0]], '$position': 0}}
        },
        upsert=True
    )

    return JsonResponse({'message': 'Flight updated successfully!'})


@csrf_exempt
def delete_flight(request, flight_id):
    if request.method != 'DELETE':
        return JsonResponse({'error': 'Unsupported request method.'})

    if not db.flights.delete_one({'identification.id': flight_id}):
        return JsonResponse({'error': 'Could not delete'})

    return JsonResponse({'message': 'Flight deleted successfully.'})
