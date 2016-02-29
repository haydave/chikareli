from django.http import HttpResponse
from django.views.decorators.csrf import csrf_exempt
from website.models import Document

@csrf_exempt
def getMediaFile(request):
    if request.method == 'POST' and request.POST['token'] == "F!HzW4W1;SAXOOBtG|90%Byy610x4XS7=MYBwf%l94[h;gV-F{j3uB|TAg35'46":
		#file = request.FILES['file']    	
    	#m.lower().endswith(('.png', '.jpg', '.jpeg'))	
    	#print request.FILES['file'].name
        newdoc = Document(docfile=request.FILES['file'])
        newdoc.save()
        return HttpResponse("Success")
    else: 
        return HttpResonse("Fail")