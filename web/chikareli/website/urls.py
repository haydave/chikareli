from django.conf.urls import url
from . import views

urlpatterns = [
	url(r'^list/$', views.getMediaFile, name='list'),
	url(r'^index/$', views.index),
]
