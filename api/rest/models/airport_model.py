from mongoengine import *


# IATA and ICAO codenames of the airport
class AirportCode(EmbeddedDocument):
    iata = StringField(required=True)
    icao = StringField(required=True)


# airport's timezone
class AirportTimezone(EmbeddedDocument):
    name = StringField(required=True)
    offset = IntField(required=True)
    offsetHours = StringField(required=True)
    abbr = StringField(required=True)
    abbrName = StringField(required=True)
    isDst = BooleanField(required=True)


# airport's geological position
class AirportPosition(EmbeddedDocument):
    latitude = FloatField(required=True)
    longitude = FloatField(required=True)
    altitude = IntField(required=True)
    city = StringField(required=True)
    country = StringField(required=True)


# main structured airport data
class Airport(Document):
    name = StringField(required=True)
    airport_code = EmbeddedDocumentField(AirportCode)
    position = EmbeddedDocumentField(AirportPosition)
    timezone = EmbeddedDocumentField(AirportTimezone)
    visible = BooleanField(required=True)
    website = URLField(required=True)
