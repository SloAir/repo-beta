import requests
import json
from django.http import HttpResponse
from .config.mongo import db


def hello_world(request):
    message = {"message": "Hello World!"}
    db['aircrafts'].insert({'name': 'hello from Ruše'})
    return HttpResponse(json.dumps(message), content_type="application/json")

