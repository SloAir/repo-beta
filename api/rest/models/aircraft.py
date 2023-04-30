from django.db import models
from django.core.exceptions import ValidationError

class Aircraft(models.Model):
    model = models.JSONField(validators=[lambda v: validate_model_field(v)])
    registration = models.CharField(max_length=255, blank=False)
    airline = models.ForeignKey('Airline', blank=False, on_delete=models.CASCADE)


# funkcija validira vnos ključev v 'model' polje modela letala
def validate_model_field(model):
    allowed_keys = ['code', 'name']
    for key in model.keys():
        if key not in allowed_keys:
            raise ValidationError('Invalid key \'{key}\' in aircraft model')
