from rest_framework import serializers
from sensordata.models import Data

class DataSerializer(serializers.ModelSerializer):
	class Meta:
		model = Data
		fields = ('id', 'created', 'sensor_id', 'moisture', 'temperature', )