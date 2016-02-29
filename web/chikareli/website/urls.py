from django.conf.urls import url
from . import views
from . import utils

urlpatterns = [
	url(r'^mediaFile/$', utils.get_media_file),
	url(r'^index/$', views.index),
]
