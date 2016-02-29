from django.shortcuts import render_to_response
from website.models import Document

def index(request):
    documents = Document.objects.all()
    return render_to_response(
        'index.html', {'documents': documents}
    )