import os
import json
import requests

from datetime import datetime
from dotenv import load_dotenv
from django.http import *
from .radar import *
from django.views.decorators.csrf import csrf_exempt

load_dotenv()


# function returns a processed JSON object of an aircraft as a dictionary
def process_aircraft_data(data):
    aircraft_data = data['aircraft']

    for k in ('countryId', 'age', 'msn'):
        if k in aircraft_data:
            # delete unnecessary fields
            del aircraft_data[k]

    if aircraft_data['images']:
        if 'large' in aircraft_data['images']:
            # move images from ['images']['large'] to ['images']
            images = aircraft_data['images']['large']
            del aircraft_data['images']['large']
            aircraft_data['images'] = images

        # if 'large' field does not exist, we try to extract 'medium' images
        elif 'medium' in aircraft_data['images']:
            # move images from ['images']['medium'] to ['images']
            images = aircraft_data['images']['medium']
            del aircraft_data['images']['medium']
            aircraft_data['images'] = images

        # if 'medium' field does not exist, we try to extract 'thumbnail' images
        elif 'thumbnails' in aircraft_data['images']:
            # move images from ['images']['thumbnails'] to ['images']
            images = aircraft_data['images']['thumbnails']
            del aircraft_data['images']['thumbnails']
            aircraft_data['images'] = images

    if aircraft_data['hex']:
        del aircraft_data['hex']

    aircraft_data['flightHistory'] = [{'flightId': data['identification']['id']}]

    return aircraft_data


# function returns a processed JSON object of the origin airport as a dictionary
def process_origin_airport_data(data):
    if not data['airport']['origin']:
        return {}

    return data['airport']['origin']


# function returns a processed JSON object of the destination airport as a dictionary
def process_destination_airport_data(data):
    if not data['airport']['destination']:
        return {}

    return data['airport']['destination']


# function returns a processed JSON object of an airline as a dictionary
def process_airline_data(data):
    if not data['airline']:
        return {}

    return data['airline']


# function returns a processed JSON object of the flight as a dictionary
def process_flight_data(data):
    flight_data = data

    # check if 'identification' field exists
    if 'identification' in flight_data:
        # check if all of the fields inside of the 'indentification' field exist
        for k in ('row', 'number'):
            if k in flight_data['identification']:
                # delete unnecessary fields
                del flight_data['identification'][k]

    # check if 'status' field exists
    if 'status' in flight_data:
        # check if all of the fields inside of the 'status' field exist
        for k in ('text', 'icon', 'estimated', 'ambiguous', 'generic'):
            if k in flight_data['status']:
                # delete unnecessary fields
                del flight_data['status'][k]

    # check if 'airport' field exists
    if flight_data['airport']:
        del flight_data['airport']

    # check if 'level' field
    if flight_data['level']:
        del flight_data['level']

    # check if 'promote' field exists
    if flight_data['promote']:
        del flight_data['promote']

    # check if 'aircraft' field exists
    if flight_data['aircraft']:
        # remove unnecessary data about aircraft
        del flight_data['aircraft']

    # check if 'flightHistory' field exists
    if flight_data['flightHistory']:
        # remove flight history
        del flight_data['flightHistory']

    # remove more unnecessary data
    if flight_data['ems']:
        del flight_data['ems']

    if flight_data['availability']:
        del flight_data['availability']

    if flight_data['s']:
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

        # send POST requests to the routes for insertion
        requests.post(os.environ.get('SERVER_URL') + 'api/aircraft/post/', json=aircraft_data, headers=headers)
        requests.post(os.environ.get('SERVER_URL') + 'api/airline/post/', json=airline_data, headers=headers)
        requests.post(os.environ.get('SERVER_URL') + 'api/airport/post/', json=origin_airport_data, headers=headers)
        requests.post(os.environ.get('SERVER_URL') + 'api/airport/post/', json=destination_airport_data, headers=headers)
        requests.post(os.environ.get('SERVER_URL') + 'api/flight/post/', json=flight_data, headers=headers)

        data_array.append(data_json)

    # safe=false -> data is NOT a dictionary
    return JsonResponse(data_array, safe=False)


# function gets all of the aircraft data from the database
@csrf_exempt
def get_aircraft(request, aircraft_registration):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    aircraft = db.aircrafts.find_one({'registration': aircraft_registration})

    if not aircraft:
        return JsonResponse({'message': 'Aircraft not found.'})

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
        data['created'] = datetime.now()
        data['modified'] = datetime.now()
        db.aircrafts.insert_one(data)

    return JsonResponse({'message': 'Aircraft inserted successfully.'})


# function updates an aircraft in the database
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


# function deletes an aircraft from the database that matches the aircraft's registration number
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

    if not airline:
        return JsonResponse({'message': 'Airline not found.'})

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
        data['modified'] = datetime.now()
        db.airlines.insert_one(data)

    return JsonResponse({'message': 'Airline inserted successfully!'})


# function updates an airline in the database
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


# function deletes an airline with a matching ICAO code from the database
@csrf_exempt
def delete_airline(request, airline_icao):
    if request.method != 'DELETE':
        return JsonResponse({'error': 'Unsupported request method.'})

    if not db.airlines.delete_one({'code.icao': airline_icao}):
        return JsonResponse({'error': 'Could not delete'})

    return JsonResponse({'message': 'Airline deleted successfully.'})


# function returns a JSON object of an airport
@csrf_exempt
def get_airport(request, airport_icao):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    airport = db.airports.find_one({'code.icao': airport_icao})

    if not airport:
        return JsonResponse({'message': 'Airport not found.'})

    airport['_id'] = str(airport['_id'])

    return JsonResponse(airport)


# function inserts an airport into the database
@csrf_exempt
def insert_airport(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)

    if db.airports.find_one({'code.icao': data['code']['icao']}):
        requests.put(os.environ.get('SERVER_URL') + 'api/airport/put/', json=data)
        return JsonResponse({'message': 'Redirected to PUT'})
    else:
        data['created'] = datetime.now()
        data['modified'] = datetime.now()
        db.airports.insert_one(data)

    return JsonResponse({'message': 'Airport inserted successfully!'})


# function updates an existing airport in the database
@csrf_exempt
def update_airport(request):
    if request.method != 'PUT':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)

    data['modified'] = datetime.now()

    db.airports.update_one(
        {'code.icao': data['code']['icao']},
        {'$set': data},
        upsert=True
    )

    return JsonResponse({'message': 'Airport updated successfully!'})


# function deletes an airport with a matching ICAO code from the database
@csrf_exempt
def delete_airport(request, airport_icao):
    if request.method != 'DELETE':
        return JsonResponse({'error': 'Unsupported request method.'})

    if not db.airports.delete_one({'code.icao': airport_icao}):
        return JsonResponse({'error': 'Could not delete'})

    return JsonResponse({'message': 'Airport deleted successfully.'})


# function returns a JSON object of a flight that matches the given flight ID
@csrf_exempt
def get_flight(request, flight_id):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    flight = db.flights.find_one({'identification.id': flight_id})

    if not flight:
        return JsonResponse({'message': 'Flight not found.'})

    flight['_id'] = str(flight['_id'])

    return JsonResponse(flight)


# function inserts a flight into the database
@csrf_exempt
def insert_flight(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)

    if db.flights.find_one({'identification.id': data['identification']['id']}):
        requests.put(os.environ.get('SERVER_URL') + 'api/flight/put/', json=data)
        return JsonResponse({'message': 'Redirected to PUT'})
    else:
        data['created'] = datetime.now()
        data['modified'] = datetime.now()
        db.flights.insert_one(data)

    return JsonResponse({'message': 'Flight inserted successfully!'})


# function updates a flight in the database
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


# function deletes a flight with a matching flight ID from the database
@csrf_exempt
def delete_flight(request, flight_id):
    if request.method != 'DELETE':
        return JsonResponse({'error': 'Unsupported request method.'})

    if not db.flights.delete_one({'identification.id': flight_id}):
        return JsonResponse({'error': 'Could not delete'})

    return JsonResponse({'message': 'Flight deleted successfully.'})
