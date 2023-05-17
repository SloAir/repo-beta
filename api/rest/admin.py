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


def authenticate(username, password):
    user = db.admin.find_one({'username': username})

    if not user or not check_password(password, user['password']):
        return None

    return user


def login(request, username, password):
    user = authenticate(username, password)

    if user is None:
        return False

    request.session['user_id'] = str(user['_id'])

    return True


@csrf_protect
def authenticate_user(request):
    if request.method != 'POST':
        form = LoginForm()
        return render(request, 'admin/login.html', {'form': form})

    form = LoginForm(request.POST)

    if not form.is_valid():
        return render(request, 'admin/login.html', {'form': form})

    username = form.cleaned_data['username']
    password = form.cleaned_data['password']

    user = login(request, username, password)

    if not user:
        return render(request, 'admin/login.html', {'form': form})

    return redirect('/api/')


def homepage(request):
    return render(request, 'admin/index.html')
