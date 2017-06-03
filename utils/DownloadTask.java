package com.ruto.nineanimeandroid.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Ruto on 2/26/2017.
 */

public class DownloadTask extends AsyncTask<String, Void, String> {
    private static final String TAG = DownloadTask.class.getSimpleName();

    @Override
    protected String doInBackground(String... params) {
        String response = null;
        try {
            URL url = new URL(params[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line).append("\n");
            }
            response = sb.toString();
            Log.d(TAG, response);
        } catch (Exception e) {
            Log.e(TAG, e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        Log.d(TAG, "Returning");
        return response;
    }
}
