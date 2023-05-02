from mongoengine import *


# Name and abbreviation of the aircraft model
class AircraftModel(EmbeddedDocument):
    code = StringField(required=True)
    name = StringField(required=True)


# Images of the aircraft
class AircraftImages(EmbeddedDocument):
    img_src = StringField(required=True)
    link = StringField(required=True)
    copyright = StringField(required=True)
    source = StringField(required=True)


# ID's of previous flights of the aircraft
class FlightHistory(EmbeddedDocument):
    flight = ObjectIdField(required=True)


# Main aircraft schema
class Aircraft(Document):
    aircraft_model = EmbeddedDocumentField(AircraftModel)
    registration = StringField(required=True)
    age = IntField(required=True)
    images = EmbeddedDocumentListField(AircraftImages)
    # array of previous flights' ID's
    flight_history = EmbeddedDocumentListField(FlightHistory)
