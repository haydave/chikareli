package com.chikareli;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Admin on 06-Feb-16.
 */
public class GalleryFile {
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int MEDIA_TYPE_VIDEO = 2;
    private static String TAG = "GALLERY -> ";
    private static Context context;
    private static GPSTracker gps;
    private static HTTPClient client;

    public GalleryFile(Context context, GPSTracker gps, HTTPClient client) {
        this.gps = gps;
        this.context = context;
        this.client = client;
    }

    public static void workWithFile(Intent intent) {
        Uri selectedURI = intent.getData();
        String type = getFileType(selectedURI);
        if (type.indexOf("video") == 0) {
            saveFile(selectedURI, MEDIA_TYPE_VIDEO);
        } else if (type.indexOf("image") == 0) {
            saveFile(selectedURI, MEDIA_TYPE_IMAGE);
        }
    }

    private static String getOutputMediaFilePath(int type) {
        File mediaStorageDir = setUpDirs(type);
        Map location = getLocation();
        String timeStamp = new SimpleDateFormat("dd_MM_yyyy_HH.mm").format(new Date());
        String path = mediaStorageDir.getPath() + File.separator + timeStamp + "__"
                + location.get("lat") + "_" + location.get("long") + "__";
        String mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = path + ".jpg";
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = path + ".mp4";
        } else {
            return null;
        }
        return mediaFile;
    }

    private static String getFileType(Uri uri) {
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String extension = mime.getExtensionFromMimeType(cR.getType(uri));
        return mime.getMimeTypeFromExtension(extension);
    }

    public static String getRealPathFromUri(Uri uri) {
        String filePath = "";
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        int columnIndex = cursor.getColumnIndex("_data");
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    private static void saveFile(Uri src, int type) {
        String srcName = getRealPathFromUri(src);
        String destinationFilename = getOutputMediaFilePath(type);
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(srcName));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);
        } catch (IOException e) {
            Log.e(TAG, String.valueOf(e));
        } finally {
            try {
                if (bis != null) {bis.close();};
                if (bos != null) {bos.close();};
            } catch (IOException e) {
                Log.e(TAG, String.valueOf(e));
            }
        }
    }

    private static File setUpDirs(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "ChiKareli");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e(TAG, "failed to create directory");
                return null;
            }
        }
        if (type == MEDIA_TYPE_IMAGE) {
            File imgDir = new File(String.valueOf(mediaStorageDir), "image");
            if (!imgDir.exists()) {
                if (!imgDir.mkdirs()) {
                    Log.e(TAG, "failed to create directory");
                    return null;
                }
            }
            return imgDir;
        } else if (type == MEDIA_TYPE_VIDEO) {
            File videoDir = new File(String.valueOf(mediaStorageDir), "video");
            if (!videoDir.exists()) {
                if (!videoDir.mkdirs()) {
                    Log.e(TAG, "failed to create directory");
                    return null;
                }
            }
            return videoDir;
        } else {
            return null;
        }
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
}