from django.shortcuts import render_to_response
from website.models import MediaFile

def index(request):
    documents = MediaFile.objects.all()
    return render_to_response(
        'index.html', {'documents': documents}
    )