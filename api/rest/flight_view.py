from rest.settings import db
from django.http import *
from rest.flight_radar_api import radar


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

    del flight_data['airport']

    del flight_data['level']
    del flight_data['promote']

    # remove unnecessary data from aircraft
    del flight_data['aircraft']

    # remove flight history
    del flight_data['flightHistory']

    del flight_data['ems']
    del flight_data['availability']

    del flight_data['trail']

    del flight_data['s']

    return data


# function saves data to a specified collection
# returns true if successful
def save_data(data, collection):
    collection = db[collection]
    return collection.insert_one(data)


# function returns JSON data of all of the flights above Slovenian airspace
def get_all(request):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = radar.fr.get_flight_details('301962bc')

    aircraft_data = process_aircraft_data(data)

    origin_airport_data = process_origin_airport_data(data)

    destination_airport_data = process_destination_airport_data(data)

    airline_data = process_airline_data(data)

    flight_data = process_flight_data(data)
    # flight_data_json = json.dumps(flight_data, indent=4)

    # if an error occurs when saving to the database
    if (
        not save_data(aircraft_data, collection='aircrafts') or
        not save_data(origin_airport_data, collection='airports') or
        not save_data(destination_airport_data, collection='airports') or
        not save_data(airline_data, collection='airlines') or
        not save_data(flight_data, collection='flights')
    ):
        return JsonResponse({'message': 'Error when saving to the "{collection}" collection in the database!'})

    return JsonResponse({'message': 'Data saved successfully!'})


def post(request):
    j = get_all(request)
    print(j)
