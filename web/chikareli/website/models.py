from __future__ import unicode_literals
from django.db import models
from PIL import Image
from cStringIO import StringIO
from django.core.files.uploadedfile import SimpleUploadedFile
import os

class MediaFile(models.Model):
    THUMBNAIL_SIZE = (200,200)
    PIL_TYPE = 'jpeg'
    FILE_EXTENSION = 'jpg'

    file = models.FileField(upload_to='documents/%Y/%m/%d',max_length=500,blank=True,null=True)
    thumbnail = models.ImageField(upload_to='documents/%Y/%m/%d',max_length=500,blank=True,null=True)
    
    def create_thumbnail(self):
        # original code for this method came from
        # http://snipt.net/danfreak/generate-thumbnails-in-django-with-pil/

        # If there is no image associated with this.
        # do not create thumbnail
        if not self.file:
            return
        # Why we get "application/octet-stream" MIME type
        # DJANGO_TYPE = self.file.file.content_type
        print os.path.basename(self.file.name)
        filename = os.path.basename(self.file.name)
        if filename.lower().endswith(('.jpg', '.jpeg')):
            self.create_for_img()
        elif filename.lower().endswith(('.mp4')):
            self.create_for_video()

    def create_for_img(self):
        # Open original photo which we want to thumbnail using PIL's Image
        image = Image.open(StringIO(self.file.read()))

        # Convert to RGB if necessary
        # Thanks to Limodou on DjangoSnippets.org
        # http://www.djangosnippets.org/snippets/20/
        if image.mode not in ('L', 'RGB'):
            image = image.convert('RGB')

        # We use our PIL Image object to create the thumbnail, which already
        # has a thumbnail() convenience method that contrains proportions.
        # Additionally, we use Image.ANTIALIAS to make the image look better.
        # Without antialiasing the image pattern artifacts may result.
        image.thumbnail(self.THUMBNAIL_SIZE, Image.ANTIALIAS)

        # Save the thumbnail
        temp_handle = StringIO()
        image.save(temp_handle, self.PIL_TYPE)
        temp_handle.seek(0)

        # Save image to a SimpleUploadedFile which can be saved into
        # ImageField
        suf = SimpleUploadedFile(os.path.split(self.file.name)[-1],
            temp_handle.read(), content_type='image/jpeg')
        # Save SimpleUploadedFile into image field
        self.thumbnail.save('%s_thumbnail.%s'%(os.path.splitext(suf.name)[0],self.FILE_EXTENSION), suf, save=False)

    def create_for_video(self):
        pass

    def save(self):
        # create a thumbnail
        self.create_thumbnail()
        super(MediaFile, self).save()