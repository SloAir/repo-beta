from djongo import models


class AircraftModel(models.Model):
    code = models.CharField(max_length=255, blank=False)
    name = models.CharField(max_length=255, blank=False)


class Image(models.Model):
    src = models.CharField(max_length=255, blank=False)
    link = models.CharField(max_length=255, blank=False)
    copyright = models.CharField(max_length=255, blank=False)
    source = models.CharField(max_length=255, blank=False)


class Aircraft(models.Model):
    model = models.EmbeddedModelField(
        model_container=AircraftModel
    )
    registration = models.CharField(max_length=255, blank=False)
    airline = models.ForeignKey('Airline', blank=False, on_delete=models.CASCADE)
    images = models.ArrayModelField(
        model_container=Image
    )
