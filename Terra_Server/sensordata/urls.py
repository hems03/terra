from django.conf.urls import url

from . import views

urlpatterns = [
	url(r'^$', views.DataCreate.as_view()),
]