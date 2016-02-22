# -*- coding: utf-8 -*-
from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponseRedirect
from django.core.urlresolvers import reverse

from website.models import Document
from website.forms import DocumentForm


def list(request):
    # Handle file upload
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if request.FILES['docfile'].name.lower().endswith(('.png', '.jpg', '.jpeg')):
            print "lava"
        if form.is_valid():
            newdoc = Document(docfile=request.FILES['docfile'])
            newdoc.save()
            # Redirect to the document list after POST 'Hin method e 1.10-um jnjvelu e website.views.index
            return HttpResponseRedirect(reverse('website.views.index'))
    else:
        form = DocumentForm()  # A empty, unbound form

    # Load documents for the list page
    documents = Document.objects.all()

    # Render list page with the documents and the form
    return render_to_response(
        'list.html',
        {'documents': documents, 'form': form},
        #  'Hin method e 1.10-um jnjvelu e website.views.inde
        context_instance=RequestContext(request)
    )

def index(request):
    # Load documents for the list page
    documents = Document.objects.all()
    return render_to_response(
        'index.html', {'documents': documents}
    )