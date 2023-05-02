from mongoengine import *


# ID data of the flight
class FlightIdentification(EmbeddedDocument):
    flight_id = StringField(required=True)


# status of the flight
class FlightStatus(EmbeddedDocument):
    live = BooleanField(required=True)


# airport metadata
class FlightAirportInfo(EmbeddedDocument):
    terminal = StringField(required=True)
    baggage = StringField(required=True)
    gate = StringField(required=True)


# data about flight's origin and destination airports
class FlightAirport(EmbeddedDocument):
    airport = ObjectIdField(required=True)
    info = EmbeddedDocument(FlightAirportInfo)


#
class FlightTime(EmbeddedDocument):
    departure = IntField(required=True)
    arrival = IntField(required=True)


#
class FlightTimeData(EmbeddedDocument):
    scheduled = EmbeddedDocumentField(FlightTime)
    real = EmbeddedDocumentField(FlightTime)
    estimated = EmbeddedDocumentField(FlightTime)


# aircraft's position
class FlightTrail(EmbeddedDocument):
    latitude = FloatField(required=True)
    longitude = FloatField(required=True)
    altitude = IntField(required=True)
    speed = IntField(required=True)
    timestamp = IntField(required=True)
    # current direction of the airplane (in degrees)
    heading = IntField(required=True)


# main flight schema
class Flight(Document):
    identification = EmbeddedDocumentField(FlightIdentification)
    status = EmbeddedDocumentField(FlightStatus)
    aircraft = ObjectIdField(required=True)
    owner = StringField(required=True)
    airspace = StringField(required=True)
    origin = EmbeddedDocumentField(FlightAirport)
    destination = EmbeddedDocumentField(FlightAirport)
    time = EmbeddedDocumentField(FlightTime)
    trail = EmbeddedDocumentListField(FlightTrail)
    first_timestamp = IntField(required=True)
