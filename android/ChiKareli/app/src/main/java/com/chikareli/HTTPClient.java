package com.chikareli;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Admin on 13-Feb-16.
 */
public class HTTPClient {

    private static final String BASE_URL = "http://192.168.0.100:8000/";
    private static final String TAG = "HTTP client -> ";
    private AsyncHttpResponseHandler rHandler = new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            //TODO: IF NECESSARY
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            //TODO: IF NECESSARY
        }
    };

    private static AsyncHttpClient client = new AsyncHttpClient();

    public void start(File file) {
        RequestParams params = new RequestParams();
        try {
            params.put("file", file);
            params.put("token", "F!HzW4W1;SAXOOBtG|90%Byy610x4XS7=MYBwf%l94[h;gV-F{j3uB|TAg35'46");
            post("mediaFile/", params, rHandler);
        } catch (FileNotFoundException e) {
            Log.e(TAG, String.valueOf(e));
        }
    }

    public void stop() {
        Log.d(TAG, "----HTTP--- Stopped");
    }

    private static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
