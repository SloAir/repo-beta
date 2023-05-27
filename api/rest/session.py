from django.shortcuts import redirect


class Session:
    def __init__(self, get_response):
        self.get_response = get_response
