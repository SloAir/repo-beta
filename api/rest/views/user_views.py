import os
import json
import requests
import time

from django.http import *
from django.middleware.csrf import get_token
from django.views.decorators.csrf import csrf_protect


# def get_user(request, username):
#     if request.method != 'GET':
#         return JsonResponse({'error': 'Unsupported request method.'})


def insert_user(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Unsupported request method.'})


def update_user(request):
    if request.method != 'PUT':
        return JsonResponse({'error': 'Unsupported request method.'})


# def delete_user(request, username):
#     if request.method != 'DELETE':
#         return JsonResponse({'error': 'Unsupported request method.'})
