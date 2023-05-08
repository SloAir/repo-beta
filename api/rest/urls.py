"""rest URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/3.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from . import views

urlpatterns = [
    path('admin/', admin.site.urls),

    # route for getting all of the data
    path('api/get/', views.get_data, name='get_data'),

    # aircraft routes
    path('api/aircraft/get/<str:aircraft_registration>/', views.get_aircraft, name='get_aircraft'),
    path('api/aircraft/post/', views.insert_aircraft, name='post_aircraft'),
    path('api/aircraft/put/', views.update_aircraft, name='put_aircraft'),
    path('api/aircraft/delete/<str:aircraft_registration>/', views.delete_aircraft, name='delete_aircraft'),

    # airline routes
    path('api/airline/get/<str:airline_icao>/', views.get_airline, name='get_airline'),
    path('api/airline/post/', views.insert_airline, name='post_airline'),
    path('api/airline/put/', views.update_airline, name='put_airline'),
    path('api/airline/delete/<str:airline_icao>/', views.delete_airline, name='delete_airline'),

    # airport routes

    # flight routes
    path('api/flight/get/<str:flight_id>/', views.get_flight, name='get_flight'),
    path('api/flight/post/', views.insert_flight, name='post_flight'),
    path('api/flight/put/', views.update_flight, name='put_flight'),
    path('api/flight/delete/<str:flight_id>/', views.delete_flight, name='delete_flight')
]
