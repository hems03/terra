# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.shortcuts import render
from django.http import HttpResponse, QueryDict

from rest_framework import mixins, generics

from sensordata.serializers import DataSerializer
from sensordata.models import Data

# Create your views here.
def index(request):
    return HttpResponse("Hello, world. You're at the polls index.")

class DataCreate(generics.ListCreateAPIView):
	queryset = Data.objects.all()
	serializer_class = DataSerializer

# class DataList(mixins.ListModelMixin, generics.GenericAPIView):
# 	queryset = Data.objects.all()
# 	serializer_class = DataSerializer

# 	def get(self, request, *args, **kwargs):
# 		query_dict = request.query_params
# 		