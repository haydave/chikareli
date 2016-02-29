from django.conf.urls import url
from . import views
from . import utils

urlpatterns = [
	url(r'^mediaFile/$', utils.getMediaFile),
	url(r'^index/$', views.index),
]
