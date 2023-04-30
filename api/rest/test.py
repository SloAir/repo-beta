import requests
import json
from django.http import HttpResponse

def hello_world(request):
    message = {"message": "Hello World!"}
    return HttpResponse(json.dumps(message), content_type="application/json")

