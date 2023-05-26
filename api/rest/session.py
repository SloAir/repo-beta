from django.shortcuts import redirect


class Session:
    def __init__(self, get_response):
        self.get_response = get_response

    def __call__(self, request):
        if 'user_id' not in request.session:
            # redirect to login page, if session is not set
            if not request.path.startswith('/api/login/'):
                return redirect('/api/login/')

        # Session is set, continue processing the request
        response = self.get_response(request)
        return response
