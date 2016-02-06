package com.chikareli;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends Activity {
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private static final int GALLERY_ACTIVITY_REQUEST_CODE = 1;
    private static final String TAG = "Chi Kareli -> CAMERA";
    private Uri fileUri;
    private GalleryFile galleryClass;

    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File setUpDirs(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "ChiKareli");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }
        if (type == MEDIA_TYPE_IMAGE) {
            File imgDir = new File(String.valueOf(mediaStorageDir), "image");
            if (!imgDir.exists()) {
                if (!imgDir.mkdirs()) {
                    Log.d(TAG, "failed to create directory");
                    return null;
                }
            }
            return imgDir;
        } else if (type == MEDIA_TYPE_VIDEO) {
            File videoDir = new File(String.valueOf(mediaStorageDir), "video");
            if (!videoDir.exists()) {
                if (!videoDir.mkdirs()) {
                    Log.d(TAG, "failed to create directory");
                    return null;
                }
            }
            return videoDir;
        } else {
            return null;
        }
    }

    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = setUpDirs(type);
        String timeStamp = new SimpleDateFormat("dd_MM_yyyy_HH.mm").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setUpEventListeners();
        this.galleryClass = new GalleryFile(getApplicationContext());
    }

    private void setUpEventListeners() {
        ImageButton img = (ImageButton) findViewById(R.id.image_capture);
        setImgBtnListener(img, "img");
        ImageButton video = (ImageButton) findViewById(R.id.video_capture);
        setImgBtnListener(video, "video");
        ImageButton gallery = (ImageButton) findViewById(R.id.from_gallery);
        setImgBtnListener(gallery, "gallery");
    }

    private void setImgBtnListener(ImageButton btn, final String id) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id == "img") {
                    startCameraImage();
                } else if (id == "video") {
                    startCameraVideo();
                } else if (id == "gallery") {
                    startGallery();
                } else {
                    Log.e(TAG, "NO TYPE TO START!");
                }
            }
        });
    }

    private void startCameraImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void startCameraVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
    }

    private void startGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/* video/*");
        startActivityForResult(galleryIntent, GALLERY_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK){
            super.onActivityResult(requestCode, resultCode, intent);
            if(requestCode == GALLERY_ACTIVITY_REQUEST_CODE) {
                this.galleryClass.workWithFile(intent);
            }
        } else if (resultCode == RESULT_CANCELED){
            Log.e(TAG, "Back button pressed.");
        }
    }
}
