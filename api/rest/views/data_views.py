import os
import requests
import json
from django.views.decorators.csrf import csrf_exempt

import rest.radar as radar
from rest import data_processing as data

from django.http import *
from dotenv import load_dotenv

load_dotenv()


# function returns JSON data of all the flights above Slovenian airspace
@csrf_exempt
def get_from_fr(request):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    si_flight_details = radar.get_data()
    # data = fr.get_flight_details('301962bc')
    data_array = []

    # process all of the data; clean-up and add to an array of dictionaries
    for si_flight in si_flight_details:

        aircraft_data = data.process_aircraft_data(si_flight)
        origin_airport_data = data.process_origin_airport_data(si_flight)
        destination_airport_data = data.process_destination_airport_data(si_flight)
        airline_data = data.process_airline_data(si_flight)
        flight_data = data.process_flight_data(si_flight)

        data_json = {
            'aircraft': aircraft_data,
            'originAirport': origin_airport_data,
            'destinationAirport': destination_airport_data,
            'airline': airline_data,
            'flight': flight_data
        }

        # send POST requests to the routes for insertion
        requests.post(
            os.environ.get('SERVER_URL') + 'api/aircraft/post/',
            json=aircraft_data
        )
        
        if airline_data is not None:
            requests.post(
                os.environ.get('SERVER_URL') + 'api/airline/post/',
                json=airline_data
            )

        requests.post(
            os.environ.get('SERVER_URL') + 'api/airport/post/',
            json=origin_airport_data
        )

        requests.post(
            os.environ.get('SERVER_URL') + 'api/airport/post/',
            json=destination_airport_data
        )

        requests.post(
            os.environ.get('SERVER_URL') + 'api/flight/post/',
            json=flight_data
        )

        data_array.append(data_json)

    # safe=false -> data is NOT a dictionary
    response = JsonResponse(data_array, safe=False)

    return response


def get_scraper_aircrafts(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Unsupported request method.'})

    aircrafts = json.loads(request.body)

    for aircraft in aircrafts:
        if 'id' in aircraft:
            del aircraft['id']

        requests.post(
            os.environ.get('SERVER_URL') + 'api/aircraft/post/',
            json=aircraft
        )

    return JsonResponse({'message': 'Aircrafts inserted successfully.'})


def get_scraper_airlines(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Unsupported request method.'})

    airlines = json.loads(request.body)

    for airline in airlines:
        if 'id' in airline:
            del airline['id']

        requests.post(
            os.environ.get('SERVER_URL') + 'api/airline/post/',
            json=airline
        )

    return JsonResponse({'message': 'Airlines inserted successfully.'})


def get_scraper_airports(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Unsupported request method.'})

    airports = json.loads(request.body)

    for airport in airports:
        if 'id' in airport:
            del airport['id']

        requests.post(
            os.environ.get('SERVER_URL') + 'api/airport/post/',
            json=airport
        )

    return JsonResponse({'message': 'Airports inserted successfully.'})


def get_scraper_flights(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Unsupported request method.'})

    flights = json.loads(request.body)

    for flight in flights:
        if 'id' in flight:
            del flight['id']

        requests.post(
            os.environ.get('SERVER_URL') + 'api/flight/post/',
            json=flight
        )

    return JsonResponse({'message': 'Flights inserted successfully.'})
