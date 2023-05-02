from FlightRadar24.api import FlightRadar24API
import datetime
import json

fr = FlightRadar24API()

# airports = fr.get_airports()
# prettyAirports = json.dumps(airports, indent=4)
# flights = fr.get_flights()

# json_airports = json.dumps(airports)
# json_flights = json.dumps(flights)

# airports_dump = json.loads(json_airports)
# flights_dump = json.loads(json_flights)

zones = fr.get_zones()
ceur_bounds = fr.get_bounds(zones["europe"]["subzones"]["ceur"])

# setup custom bounds for Slovenia
si_bounds = '46.66,13.51,45.66,16.19'
lat_max, lon_min, lat_min, lon_max = map(float, si_bounds.split(','))

# get Central Europe flights
ceur_flights = fr.get_flights(bounds=ceur_bounds)

# Get SLO flights
si_flights = []
for flight in ceur_flights:
    if lat_min <= flight.latitude <= lat_max and lon_min <= flight.longitude <= lon_max:
        si_flights.append(flight.id)

# time_intervals for timestamps
time_interval = 90
cum_interval = 120

# Get flight details for every SLO flight
si_flight_details = []
for si_flight_id in si_flights:
    # If exists
    # ..... (si_flight_id)
    # If not exists ->
    details = fr.get_flight_details(si_flight_id)

    # "Aircraft" is a GROUND vehicle -> ignore
    if details["aircraft"]["model"]["code"] == 'GRND':
        continue

    # adjust to the amount of data inside the trail
    trail_len = len(details["trail"])
    if trail_len > 100:
        time_interval = 180
        cum_interval = 240

    # Trail that will be inserted into the db
    valid_trail = []

    # Cumulative diff -> if diff < time_interval
    cum_diff = 0

    # Eliminate unmatching trail if flight was close to leaving bounds;
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

    for i in range(trail_len):
        if lat_min <= details["trail"][i]['lat'] <= lat_max and lon_min <= details["trail"][i]['lng'] <= lon_max:
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

pretty_si_details = json.dumps(si_flight_details, indent=4)
f_si_details = open("si_flights_details.txt", "w")
f_si_details.write(str(pretty_si_details))

f_si_flights_id = open("si_flights_id.txt", "w")
f_si_flights_id.write(str(si_flights))

# f_airport = open("airport.txt", "w")
# airport = fr.get_airport('LJLJ')
# pretty_airport = json.dumps(airport, indent=4)
# f_airport.write(str(pretty_airport))

# airline = fr.get_airlines()
# airline_json = json.dumps(airline, indent=4)
# f_airlines = open("airlines.txt", "w")
# f_airlines.write(str(airline_json))

# airlines_logo = fr.get_airline_logo(icao='CTN', iata='')
# f_airlines_logo = open("airline_logo.txt", "w")
# f_airlines_logo.write(str(airlines_logo))