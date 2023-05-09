from FlightRadar24.api import FlightRadar24API
from rest.settings import db

fr = FlightRadar24API()


def get_data():
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
        if valid_index == trail_len:
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
                if lat_min <= details["trail"][i]['lat'] <= lat_max and lon_min <= details["trail"][i][
                    'lng'] <= lon_max:
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
                if lat_min <= details["trail"][i]['lat'] <= lat_max and lon_min <= details["trail"][i][
                    'lng'] <= lon_max:
                    if i == 0:
                        valid_trail.append(details["trail"][i])
                    else:
                        diff = details["trail"][i - 1]['ts'] - details["trail"][i]['ts']
                        if diff >= time_interval:
                            if cum_diff + diff >= cum_interval:
                                valid_trail.append(details["trail"][i - 1])
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