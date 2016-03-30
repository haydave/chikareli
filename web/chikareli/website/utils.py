from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
from website.models import MediaFile
import Image

@csrf_exempt
def get_media_file(request):
    if request.method == 'POST' and request.POST['token'] == "F!HzW4W1;SAXOOBtG|90%Byy610x4XS7=MYBwf%l94[h;gV-F{j3uB|TAg35'46":
        newdoc = MediaFile(file=request.FILES['file'])
        newdoc.save()
        return HttpResponse("Success")
    else: 
        return HttpResonse("Fail")
