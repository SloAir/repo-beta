from django.db import models


class Aircraft(models.Model):
    model = {
        'code': models.CharField(max_length=255, blank=False),
        'name': models.CharField(max_length=255, blank=False)
    }
    registration = models.CharField(max_length=255, blank=False)
    airline = models.ForeignKey('Airline', blank=False, on_delete=models.CASCADE)
