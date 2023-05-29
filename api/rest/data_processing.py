def validate_aircraft_data(data):
    aircraft_data = data['aircraft']

    if not aircraft_data:
        return False

    aircraft_fields = [
        'model',
        'registration',
        'mages'
    ]

    for k in aircraft_fields:
        if k not in aircraft_data:
            return False

    model_fields = [
        'code',
        'text'
    ]

    for k in model_fields:
        if k not in aircraft_data['model']:
            return False

    return True


def validate_airline_data(data):
    airline_data = data['airline']

    if not airline_data:
        return False

    airline_fields = [
        'name',
        'short',
        'code',
        'url'
    ]

    for k in airline_fields:
        if k not in airline_data:
            return False

    code_fields = [
        'iata',
        'icao'
    ]

    for k in code_fields:
        if k not in airline_data['code']:
            return False

    return True


def validate_airport_data(data):
    airport_data = data['airport']

    if not airport_data:
        return False

    airport_fields = [
        'name',
        'code',
        'position',
        'timezone',
        'visible',
        'website',
        'info'
    ]

    for k in airport_fields:
        if k not in airport_data:
            return False

    code_fields = [
        'iata',
        'icao'
    ]

    for k in code_fields:
        if k not in airport_data['code']:
            return False

    position_fields = [
        'latitude',
        'longitude',
        'altitude',
        'country',
        'region'
    ]

    for k in position_fields:
        if k not in airport_data['position']:
            return False

        if k == 'country':
            country_fields = [
                'name',
                'code'
            ]

            for j in country_fields:
                if j not in airport_data['position']['country']:
                    return False

        if k == 'region':
            if 'city' not in airport_data['position']['region']:
                return False

    return True


def validate_flight_data(data):
    if not data:
        return False

    flight_fields = [
        'identification',
        'status',
        'owner',
        'airspace',
        'time',
        'trail',
        'firstTimestamp'
    ]

    for k in flight_fields:
        if k not in data:
            return False

        if k == 'status':
            if 'status' not in data['status']:
                return False

    identification_fields = [
        'id',
        'callsign'
    ]

    for k in identification_fields:
        if k not in data['identification']:
            return False

    time_fields = [
        'scheduled',
        'real',
        'estimated',
        'other',
        'historical'
    ]

    for k in time_fields:
        if k not in data['time']:
            return False

        if k == 'other':
            other_fields = [
                'eta',
                'updated'
            ]

            for j in other_fields:
                if j not in data['time']['other']:
                    return False

        elif k == 'historical':
            historical_fields = [
                'flighttime',
                'delay'
            ]

            for j in historical_fields:
                if j not in data['time']['historical']:
                    return False

        else:
            other_time_fields = [
                'departure',
                'arrival'
            ]

            for j in other_time_fields:
                if j not in data['time'][k]:
                    return False

    return True


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


# function returns a processed JSON object of an airline as a dictionary
async def process_airline_data(data):
    # return None, if no info
    if not data['airline']:
        return None

    return data['airline']


# function returns a processed JSON object of the origin airport as a dictionary
async def process_origin_airport_data(data):
    # return None, if no info
    if not data['airport']['origin']:
        return None

    airport_data = data['airport']['origin']

    if airport_data['position']['country']['id']:
        del airport_data['position']['country']['id']

    if airport_data['info']:
        del airport_data['info']

    return airport_data


# function returns a processed JSON object of the destination airport as a dictionary
async def process_destination_airport_data(data):
    # return None, if no info
    if not data['airport']['destination']:
        return None

    airport_data = data['airport']['destination']

    if airport_data['position']['country']['id']:
        del airport_data['position']['country']['id']

    if airport_data['info']:
        del airport_data['info']

    return airport_data


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
        'airline',
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
