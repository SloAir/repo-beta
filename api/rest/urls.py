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
from django.urls import path
from rest.views import (
    aircraft_views,
    airline_views,
    airport_views,
    flight_views,
    data_views
)
from rest import admin

urlpatterns = [
    # homepage for API administration

    # routes for API authentication
    path('api/', admin.homepage, name='homepage_admin'),
    path('api/login/', admin.authenticate_user, name='login_admin'),

    # API route for getting all of the data
    path('api/get/', data_views.get_all, name='get_data'),

    # API aircraft routes
    path('api/aircraft/get/', aircraft_views.get_all, name='get_aircrafts'),
    path('api/aircraft/get/<str:aircraft_registration>/', aircraft_views.get_aircraft, name='get_aircraft'),
    path('api/aircraft/post/', aircraft_views.insert_aircraft, name='post_aircraft'),
    path('api/aircraft/put/', aircraft_views.update_aircraft, name='put_aircraft'),
    path('api/aircraft/delete/<str:aircraft_registration>/', aircraft_views.delete_aircraft, name='delete_aircraft'),

    # API airline routes
    path('api/airline/get/<str:airline_icao>/', airline_views.get_airline, name='get_airline'),
    path('api/airline/post/', airline_views.insert_airline, name='post_airline'),
    path('api/airline/put/', airline_views.update_airline, name='put_airline'),
    path('api/airline/delete/<str:airline_icao>/', airline_views.delete_airline, name='delete_airline'),

    # API airport routes
    path('api/airport/get/<str:airport_icao>/', airport_views.get_airport, name='get_airport'),
    path('api/airport/post/', airport_views.insert_airport, name='post_airport'),
    path('api/airport/put/', airport_views.update_airport, name='put_airport'),
    path('api/airport/delete/<str:airport_icao>/', airport_views.delete_airport, name='delete_airport'),

    # API flight routes
    path('api/flight/get/<str:flight_id>/', flight_views.get_flight, name='get_flight'),
    path('api/flight/post/', flight_views.insert_flight, name='post_flight'),
    path('api/flight/put/', flight_views.update_flight, name='put_flight'),
    path('api/flight/delete/<str:flight_id>/', flight_views.delete_flight, name='delete_flight')
]
