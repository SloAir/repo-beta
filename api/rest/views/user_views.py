import json
import os
import time

import requests

from bson import json_util
from bson import ObjectId

from django.http import JsonResponse
from django.middleware.csrf import get_token
from dotenv import load_dotenv

from rest.settings import db
from django import forms
from django.contrib.auth.hashers import check_password, make_password
from django.shortcuts import render, redirect
from django.views.decorators.csrf import csrf_protect

load_dotenv()


def username_exists(username):
    user = db.users.find_one({'username': username})

    if user is None:
        return False

    return True


def email_exists(email):
    user = db.users.find_one({'email': email})

    if user is None:
        return False

    return True


def find_by_username(username):
    user = db.users.find_one({'username': username})

    if user is None:
        return None

    return user


class LoginForm(forms.Form):
    username = forms.CharField()
    password = forms.CharField(widget=forms.PasswordInput)


def login_view(request):
    form = LoginForm()

    return render(request, '/user/login.html', {'form': form})


class RegisterForm(forms.Form):
    username = forms.CharField()
    email = forms.CharField()
    password = forms.CharField(widget=forms.PasswordInput)
    repeat_password = forms.CharField(widget=forms.PasswordInput)


def match_password(password, repeat_password):
    return password == repeat_password


def register_view(request):
    form = RegisterForm()

    return render(request, 'user/register.html', {'form': form})


def register(request):
    if request.method != 'POST':
        form = RegisterForm()
        return render(request, 'user/register.html', {'form': form})

    form = RegisterForm(json.loads(request.body))

    if not form.is_valid():
        return JsonResponse({'error' : 'Invalid inputs'})
        #return render(request, 'user/register.html', {'form': form})

    username = form.cleaned_data['username']
    email = form.cleaned_data['email']
    password = form.cleaned_data['password']
    repeat_password = form.cleaned_data['repeat_password']

    print(username)
    print(email)
    print(password)

    if username_exists(username) or email_exists(email):
        form = RegisterForm()
        print('exists')
        return JsonResponse({'error' : 'Username exists'})
        #return render(request, 'user/register.html', {'form': form})

    if not match_password(password, repeat_password):
        form = RegisterForm()
        print('no match')
        return JsonResponse({'error' : 'Pass no match'})
        #return render(request, 'user/register.html', {'form': form})

    user = create_user(
        username,
        email,
        password
    )

    csrf_token = get_token(request)
    headers = {
        'X-CSRFToken': csrf_token
    }

    response = requests.post(
        os.environ.get('SERVER_URL') + 'api/user/post/',
        json=user,
        headers=headers
    )

    return JsonResponse(response.json())


def authenticate(username, password):
    user = find_by_username(username)

    if user is None or not check_password(password, user['password']):
        return None

    return user


def set_session(request, username, password):
    user = authenticate(username, password)

    if user is None:
        return None

    request.session['user_id'] = str(user['_id'])

    return user


#@csrf_protect
def login(request):
    if request.method != 'POST':
        form = LoginForm()
        return render(request, 'user/login.html', {'form': form})

    form = LoginForm(json.loads(request.body))

    if not form.is_valid():
        return JsonResponse({'error' : 'Invalid inputs'})
        #return render(request, 'user/login.html', {'form': form})

    username = form.cleaned_data['username']
    password = form.cleaned_data['password']

    user = set_session(request, username, password)

    if user is None:
        return JsonResponse({'error' : 'User is none error'})

    print(request.user)

    return JsonResponse({'message' : 'Successful login', 
                         "user" : json.dumps(user, default=json_util.default),
                         "session_id" : "halo"})


def logout(request):
    print(request)
    if 'user_id' not in request.session:
        return JsonResponse({"message" : "session error"})

    del request.session['user_id']

    # return redirect('user/login/')
    return JsonResponse({"message" : "Logged out"})


def create_user(username, email, password):
    if (
        username == '' or
        email == '' or
        password == ''
    ):
        return None

    hashed_password = make_password(password)

    user = {
        'username': username,
        'email': email,
        'password': hashed_password
    }

    return user


def get_user(request, username):
    if request.method != 'GET':
        return JsonResponse({'error': 'Unsupported request method.'})

    user = db.users.find_one({'username': username})

    if not user:
        return JsonResponse({'error': 'User not found.'})

    user['_id'] = str(user['_id'])

    return JsonResponse(user)


def insert_user(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)

    if db.users.find_one({'username': data['username']}):
        requests.put(os.environ.get('SERVER_URL') + 'api/user/put/', json=data)
        return JsonResponse({'message': 'Redirected to PUT'})
    else:
        data['created'] = int(time.time())
        data['modified'] = int(time.time())
        inserted_user = db.users.insert_one(data)

        inserted_id = str(inserted_user.inserted_id)

        inserted_user = db.users.find_one({'_id' : ObjectId(inserted_id)})

        inserted_user_json = json.dumps(inserted_user, default=json_util.default)

        return JsonResponse({'message': 'User inserted successfully.', 'user' : inserted_user_json})


def update_user(request):
    if request.method != 'PUT':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)

    data['modified'] = int(time.time())

    db.flights.update_one(
        {'username': data['username']},
        {'$set': {k: v for k, v in data.items()}},
        upsert=True
    )

    return JsonResponse({'message': 'User updated successfully!'})


def delete_user(request, username):
    if request.method != 'DELETE':
        return JsonResponse({'error': 'Unsupported request method.'})

    if not db.flights.delete_one({'username': username}):
        return JsonResponse({'error': 'Could not delete'})

    return JsonResponse({'message': 'User deleted successfully.'})
