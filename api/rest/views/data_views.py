import os
import requests
import rest.radar as radar
from rest import data_processing as data

from django.http import *
from django.middleware.csrf import get_token
from dotenv import load_dotenv

load_dotenv()


# function returns JSON data of all the flights above Slovenian airspace
async def get_all(request):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    si_flight_details = radar.get_data()

    # data = fr.get_flight_details('301962bc')
    data_array = []

    # process all of the data; clean-up and add to an array of dictionaries
    for si_flight in si_flight_details:

        aircraft_data = await data.process_aircraft_data(si_flight)
        origin_airport_data = await data.process_origin_airport_data(si_flight)
        destination_airport_data = await data.process_destination_airport_data(si_flight)
        airline_data = await data.process_airline_data(si_flight)
        flight_data = await data.process_flight_data(si_flight)

        data_json = {
            'aircraft': aircraft_data,
            'originAirport': origin_airport_data,
            'destinationAirport': destination_airport_data,
            'airline': airline_data,
            'flight': flight_data
        }

        # send POST requests to the routes for insertion
        requests.post(os.environ.get('SERVER_URL') + 'api/aircraft/post/', json=aircraft_data)
        requests.post(os.environ.get('SERVER_URL') + 'api/airline/post/', json=airline_data)
        requests.post(os.environ.get('SERVER_URL') + 'api/airport/post/', json=origin_airport_data)
        requests.post(os.environ.get('SERVER_URL') + 'api/airport/post/', json=destination_airport_data)
        requests.post(os.environ.get('SERVER_URL') + 'api/flight/post/', json=flight_data)

        data_array.append(data_json)

    # safe=false -> data is NOT a dictionary
    response = JsonResponse(data_array, safe=False)

    return response
