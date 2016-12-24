package org.ghotkar.hokiedo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by cghotkar on 12/17/16.
 * This class is used to asynchronously upload data to the server
 */


public class UploadData extends AsyncTask<String, Void, Long> {
    String TAG = "UPLOAD";
    private String mWebsiteURL = "Not Set";
    private String mUser = "";
    private String mType = "";
    private String mJSONString = "";
    private ProgressDialog mPd = null;
    private Context mContext; // context reference

    public UploadData(Context context, String user, String type, String url, String jsonString, ProgressDialog pd){ //constructor
        this.mContext = context;
        this.mUser = user;
        this.mType = type;
        this.mWebsiteURL = url;
        this.mJSONString = jsonString;
        this.mPd = pd;
    }
    protected Long doInBackground(String... cred) {
        String charset = "UTF-8";
        String newURL = mWebsiteURL + "/" + mType;
        Log.i(TAG, newURL);
        long status = 0;

        Log.i(TAG, mJSONString);

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) new URL(newURL).openConnection();
            httpConnection.setRequestMethod("POST");
            String query = String.format("user=%s&list=%s",
                    mUser, mJSONString);
            httpConnection.setRequestProperty("Accept-Charset", charset);
            httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            try (OutputStream output = httpConnection.getOutputStream()) {
                output.write(query.getBytes(charset));
            }

            status = httpConnection.getResponseCode();

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return status;

    }

    // Run on UI Thread
    protected void onPostExecute(Long status_code) {
        mPd.dismiss();
        if (status_code == 200) {
            Log.i(TAG, "Data Updated");
        } else if (status_code == 400) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    mContext);
            // Add the buttons
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                        }
                    });
            builder.setMessage("User was not Found!");
            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (status_code == 404) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            // Add the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                }
            });
            builder.setMessage("Unable to connect to server.");
            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }
}