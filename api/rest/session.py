from django.http import HttpResponseRedirect


class Session:
    def __init__(self, get_response):
        self.get_response = get_response

    def __call__(self, request):
        if 'user_id' not in request.session:
            # Session is not set, handle it as desired
            # Redirect to login page, raise an exception, etc.
            if not request.path.startswith('/api/login/'):
                return HttpResponseRedirect('/api/login')

        # Session is set, continue processing the request
        response = self.get_response(request)
        return response
