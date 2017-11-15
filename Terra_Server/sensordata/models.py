# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models

# Create your models here.

class Data(models.Model):
	created = models.DateTimeField(auto_now_add=True)
	sensor_id = models.CharField(max_length=100, blank=False)
	temperature = models.FloatField()
	moisture = models.FloatField()

	class Meta:
		ordering = ('created',)