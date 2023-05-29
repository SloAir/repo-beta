from django.http import HttpResponseRedirect

from rest.settings import db
from django import forms
from django.contrib.auth.hashers import check_password
from django.shortcuts import render, redirect
from django.views.decorators.csrf import csrf_protect


class LoginForm(forms.Form):
    username = forms.CharField()
    password = forms.CharField(widget=forms.PasswordInput)


def login_view(request):
    form = LoginForm()

    return render(request, 'admin/login.html', {'form': form})


def find_user(username):
    user = db.admin.find_one({'username': username})

    if user is None:
        return None

    return user


def authenticate(username, password):
    user = find_user(username)

    if user is None or not check_password(password, user['password']):
        return None

    return user


def set_session(request, username, password):
    user = authenticate(username, password)

    if user is None:
        return False

    request.session['user_id'] = str(user['_id'])

    return True


@csrf_protect
def login(request):
    if request.method != 'POST':
        form = LoginForm()
        return render(request, 'admin/login.html', {'form': form})

    form = LoginForm(request.POST)

    if not form.is_valid():
        return render(request, 'admin/login.html', {'form': form})

    username = form.cleaned_data['username']
    password = form.cleaned_data['password']

    session = set_session(request, username, password)

    if not session:
        return render(request, 'admin/login.html', {'form': form})

    return redirect('/api/')


def logout(request):
    if 'user_id' not in request.session:
        return redirect('/api/login/')

    del request.session['user_id']

    return redirect('/api/login/')


def homepage(request):
    return render(request, 'admin/index.html')
