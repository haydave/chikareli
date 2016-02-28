package com.chikareli;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CameraActivity extends Activity {
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private static final int GALLERY_ACTIVITY_REQUEST_CODE = 1;
    private static final String TAG = "CAMERA -> ";
    private static GPSTracker gps;
    private Uri fileUri;
    private File file;
    private GalleryFile galleryClass;
    private HTTPClient client = new HTTPClient();

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
        Map location = getLocation();
        String name = mediaStorageDir.getPath() + File.separator + timeStamp + "__"
                + location.get("lat") + "_" + location.get("long") + "__";
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(name + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(name + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    private static Map getLocation() {
        Map obj = new HashMap();
        Location location = gps.getLocation();
        if (location != null) {
            obj.put("long", location.getLongitude());
            obj.put("lat", location.getLatitude());
        }
        return obj;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        gps = new GPSTracker(getApplicationContext());
        setUpBtnEventListeners();
        client.stop();
        this.galleryClass = new GalleryFile(getApplicationContext(), gps, client);
    }

    private void setUpBtnEventListeners() {
        Button img = (Button) findViewById(R.id.material_camera);
        setBtnListener(img, "img");
        Button video = (Button) findViewById(R.id.material_video);
        setBtnListener(video, "video");
        Button gallery = (Button) findViewById(R.id.material_gallery);
        setBtnListener(gallery, "gallery");
    }

    private void setBtnListener(Button btn, final String id) {
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
        if (resultCode == RESULT_OK) {
            Map location = getLocation();
            if (location.isEmpty()) {
                gps.showSettingsAlert(this);
            }
            super.onActivityResult(requestCode, resultCode, intent);
            if (requestCode == GALLERY_ACTIVITY_REQUEST_CODE) {
                this.galleryClass.workWithFile(intent);
            }
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                file = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            } else if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
                file = getOutputMediaFile(MEDIA_TYPE_VIDEO);
            }
            client.start(file);
            client.stop();
        } else if (resultCode == RESULT_CANCELED) {
            Log.e(TAG, "Back button pressed.");
        }
    }
}
