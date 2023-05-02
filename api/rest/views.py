import os
from dotenv import load_dotenv
from rest.settings import db
from django.http import *
from .radar import *

load_dotenv()

SERVER_URL = 'http://' + os.environ.get('SERVER_HOST') + ':' + os.environ.get('SERVER_PORT')


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

    aircraft_data['flightHistory'] = []

    return aircraft_data['aircraft']


# function returns a processed JSON object of the origin airport as a dictionary
def process_origin_airport_data(data):
    return data['airport']['origin']


# function returns a processed JSON object of the destination airport as a dictionary
def process_destination_airport_data(data):
    return data['airport']['destination']


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

    data = fr.get_flight_details('301962bc')

    # process all of the data
    aircraft_data = process_aircraft_data(data)
    origin_airport_data = process_origin_airport_data(data)
    destination_airport_data = process_destination_airport_data(data)
    airline_data = process_airline_data(data)
    flight_data = process_flight_data(data)

    return JsonResponse({
        'aircraft': aircraft_data,
        'originAirport': origin_airport_data,
        'destinationAirport': destination_airport_data,
        'airline': airline_data,
        'flight': flight_data
    })
