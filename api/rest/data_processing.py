# function returns a processed JSON object of an aircraft as a dictionary
async def process_aircraft_data(data):
    if not data['aircraft']:
        return None

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
async def process_origin_airport_data(data):
    # return None, if no info
    if not data['airport']['origin']:
        return None

    airport_data = data['airport']['origin']

    if airport_data['info']:
        del airport_data['info']

    return airport_data


# function returns a processed JSON object of the destination airport as a dictionary
async def process_destination_airport_data(data):
    # return None, if no info
    if not data['airport']['destination']:
        return None

    airport_data = data['airport']['destination']

    if airport_data['info']:
        del airport_data['info']

    return airport_data


# function returns a processed JSON object of an airline as a dictionary
async def process_airline_data(data):
    # return None, if no info
    if not data['airline']:
        return None

    return data['airline']


# function returns a processed JSON object of the flight as a dictionary
async def process_flight_data(data):
    # return None, if no info
    if not data:
        return None

    flight_data = data

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
