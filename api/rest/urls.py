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
    data_views,
    user_views
)

from rest import admin

from rest_framework_simplejwt.views import (
    TokenObtainPairView,
    TokenRefreshView
)

urlpatterns = [
    path('api/token/', TokenObtainPairView.as_view(), name='token'),
    path('api/token/refresh/', TokenRefreshView.as_view(), name='refresh_token'),

    # login and register form for users
    path('register/', user_views.register, name='register_user'),
    path('login/', user_views.login, name='login_user'),
    path('logout/', user_views.logout, name='logout_user'),

    # homepage for API administration
    path('api/', admin.homepage, name='index'),

    # routes for API authentication
    path('api/login/', admin.login, name='login_admin'),
    path('api/logout/', admin.logout, name='logout_admin'),

    # API route for getting all of the FR data
    path('api/get/', data_views.get_from_fr, name='get_data'),

    # API scraper routes
    path('api/scraper/aircraft/post/', data_views.get_scraper_aircrafts, name='scraper_aircrafts'),
    path('api/scraper/airline/post/', data_views.get_scraper_airlines, name='scraper_airlines'),
    path('api/scraper/airport/post/', data_views.get_scraper_airports, name='scraper_airports'),
    path('api/scraper/flight/post/', data_views.get_scraper_flights, name='scraper_flights'),

    # API user routes
    path('api/user/get/<str:username>', user_views.get_user, name='get_user'),
    path('api/user/post/', user_views.insert_user, name='post_user'),
    path('api/user/put/', user_views.update_user, name='put_user'),
    path('api/user/delete/<str:username>', user_views.delete_user, name='delete_user'),

    # API aircraft routes
    path('api/aircraft/get/', aircraft_views.get_all, name='get_aircrafts'),
    path('api/aircraft/get/<str:aircraft_registration>/', aircraft_views.get_aircraft, name='get_aircraft'),
    path('api/aircraft/post/', aircraft_views.insert_aircraft, name='post_aircraft'),
    path('api/aircraft/put/', aircraft_views.update_aircraft, name='put_aircraft'),
    path('api/aircraft/delete/<str:aircraft_registration>/', aircraft_views.delete_aircraft, name='delete_aircraft'),

    # API airline routes
    path('api/airline/get/', airline_views.get_all, name='get_airlines'),
    path('api/airline/get/<str:airline_icao>/', airline_views.get_airline, name='get_airline'),
    path('api/airline/post/', airline_views.insert_airline, name='post_airline'),
    path('api/airline/put/', airline_views.update_airline, name='put_airline'),
    path('api/airline/delete/<str:airline_id>/', airline_views.delete_airline, name='delete_airline'),

    # API airport routes
    path('api/airport/get/', airport_views.get_all, name='get_airports'),
    path('api/airport/get/<str:airport_icao>/', airport_views.get_airport, name='get_airport'),
    path('api/airport/post/', airport_views.insert_airport, name='post_airport'),
    path('api/airport/put/', airport_views.update_airport, name='put_airport'),
    path('api/airport/delete/<str:airport_id>/', airport_views.delete_airport, name='delete_airport'),

    # API flight routes
    path('api/flight/get/', flight_views.get_all, name='get_flights'),
    path('api/flight/get/<str:flight_id>/', flight_views.get_flight, name='get_flight'),
    path('api/flight/post/', flight_views.insert_flight, name='post_flight'),
    path('api/flight/put/', flight_views.update_flight, name='put_flight'),
    path('api/flight/delete/<str:flight_id>/', flight_views.delete_flight, name='delete_flight')
]
