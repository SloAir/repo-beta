import json
import os
import time
import string
import secrets
import requests

from bson import json_util
from bson import ObjectId

from django.http import JsonResponse
from dotenv import load_dotenv

from rest.settings import db
from django.contrib.auth.hashers import check_password, make_password

load_dotenv()


def generate_random_string(length):
    characters = string.ascii_letters + string.digits
    random_string = ''.join(secrets.choice(characters) for _ in range(length))
    return random_string

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


def match_password(password, repeat_password):
    return password == repeat_password


def register(request):
    if request.method != 'POST':
        return JsonResponse({'error': 'Unsupported request method.'})

    data = json.loads(request.body)

    username = data['username']
    email = data['email']
    password = data['password']
    repeat_password = data['repeat_password']
    if username_exists(username) or email_exists(email):
        return JsonResponse({'error' : 'Username exists'})

    if not match_password(password, repeat_password):
        return JsonResponse({'error' : 'Pass no match'})

    user = create_user(
        username,
        email,
        password
    )

    response = requests.post(
        os.environ.get('SERVER_URL') + 'api/user/post/',
        json=user
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


def login(request):
    if request.method != 'POST':
        return JsonResponse({'message': 'Unsupported request method.'})

    data = json.loads(request.body)

    username = data['username']
    password = data['password']

    user = db.users.find_one({'username': username})

    if user is None:
        return JsonResponse({'message': 'User not found.'})

    if not check_password(password, user['password']):
        return JsonResponse({'message': 'Incorrect password.'})

    user['_id'] = str(user['_id'])

    return JsonResponse({'user': user})


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

    authentication_key = generate_random_string(64)

    user = {
        'username': username,
        'email': email,
        'password': hashed_password,
        'authenticationKey': authentication_key
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
