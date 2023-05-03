import os
import requests
from dotenv import load_dotenv
from rest.settings import db
from django.http import *
from .radar import *
from django.middleware.csrf import CsrfViewMiddleware
from django.views.decorators.csrf import csrf_exempt
load_dotenv()


# function returns a processed JSON object of an aircraft as a dictionary
def process_aircraft_data(data):
    aircraft_data = data

    # remove unnecessary data from aircraft
    del aircraft_data['aircraft']['countryId']
    del aircraft_data['aircraft']['age']
    del aircraft_data['aircraft']['msn']
    del aircraft_data['aircraft']['images']['thumbnails']
    del aircraft_data['aircraft']['images']['medium']
    del aircraft_data['aircraft']['hex']

    # move images from ['images']['large'] to ['images']
    images = aircraft_data['aircraft']['images']['large']
    del aircraft_data['aircraft']['images']['large']
    aircraft_data['aircraft']['images'] = images

    aircraft_data['flightId'] = data['identification']['id']

    return aircraft_data['aircraft']


# function returns a processed JSON object of the origin airport as a dictionary
def process_origin_airport_data(data):
    return data['airport']['origin']


# function returns a processed JSON object of the destination airport as a dictionary
def process_destination_airport_data(data):
    return data['airport']['destination']


# function returns a processed JSON object of an airline as a dictionary
def process_airline_data(data):
    return data['airline']


# function returns a processed JSON object of the flight as a dictionary
def process_flight_data(data):
    flight_data = data

    # delete unnecessary data from identification
    del flight_data['identification']['row']
    del flight_data['identification']['number']

    # delete unnecessary data from status
    del flight_data['status']['text']
    del flight_data['status']['icon']
    del flight_data['status']['estimated']
    del flight_data['status']['ambiguous']
    del flight_data['status']['generic']

    # remove unnecessary airport data
    del flight_data['airport']

    # remove more unnecessary data
    del flight_data['level']
    del flight_data['promote']

    # remove unnecessary data from aircraft
    del flight_data['aircraft']

    # remove flight history
    del flight_data['flightHistory']

    # remove more unnecessary data
    del flight_data['ems']
    del flight_data['availability']

    del flight_data['s']

    return data


# function saves data to a specified collection
# returns true if successful
def insert_data(data, collection):
    collection = db[collection]
    return collection.insert_one(data)


# function returns JSON data of all the flights above Slovenian airspace
def get_data(request):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})
    
    # get all existing flight_id from the DB
    collection = db["Flight"]
    projection = { "flight_id" : 1}
    db_flights = collection.find({}, projection)

    # get all predefined zones -> extract Central Europe
    zones = fr.get_zones()
    ceur_bounds = fr.get_bounds(zones["europe"]["subzones"]["ceur"])

    # setup custom bounds for Slovenia
    si_bounds = '46.66,13.51,45.66,16.19'
    lat_max, lon_min, lat_min, lon_max = map(float, si_bounds.split(','))

    # get Central Europe flights
    ceur_flights = fr.get_flights(bounds = ceur_bounds)

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
        details = fr.get_flight_details(si_flight_id)
        
        # "aircraft" is a GROUND vehicle -> ignore
        if details["aircraft"]["model"]["code"]=='GRND':
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

        ## if flight exists; updated trail and add to si_flight_details
        if si_flight_id in db_flights:
            trail_len = len(details["trail"])

            # get first trail object inside bounds
            new_trail = {}
            for i in range(trail_len):
                if lat_min <= details["trail"][i]['lat'] <= lat_max and lon_min <= details["trail"][i]['lng'] <= lon_max:
                    new_trail = details["trail"][i]
                    break
            
            collection = db['Flight']
            filter = { 'flight_id' : si_flight_id}
            update = { '$push' : {"trail": {"$each": [new_trail], "$position": 0}}}
            update_result = collection.update_one(filter, update)

            updated_flight = collection.find_one(filter)
            si_flight_details.append(updated_flight)

        ## if flight does not exist
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

        response = requests.post(os.environ.get('SERVER_URL') + '/api/aircraft/post/', json=aircraft_data, headers=headers)
    
    #safe=false -> data is NOT a dictionary
    return JsonResponse(data_array, safe=False)

# function gets all of the aircraft data from the database
@csrf_exempt
def get_aircraft(request):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})


# function inserts the aircraft into the database
@csrf_exempt
def insert_aircraft(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)
    registration_number = data['registration']

    # if the aircraft already exists, redirect to PUT URL
    if db.aircrafts.find_one({'registration': registration_number}):
        return requests.put(os.environ.get('SERVER_URL') + '/api/aircraft/put', json=data)
    else:
        db.aircrafts.insert_one(data)

    return JsonResponse({'message': 'Aircraft inserted successfully.'})


# function updates the aircraft in the database
@csrf_exempt
def update_aircraft(request):
    if request.method != 'PUT':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)
    flight_id = data['flightId']

    db.aircrafts.update_one({})

    return JsonResponse({'message': 'Aircraft updated successfully.'})
