from mongoengine import *


class AirlineCode(EmbeddedDocument):
    iata = StringField(required=True)
    icao = StringField(required=True)


class Airline(Document):
    name = StringField(required=True)
    short = StringField(required=True)
    code = EmbeddedDocumentField(AirlineCode)
    url = StringField(required=True)
