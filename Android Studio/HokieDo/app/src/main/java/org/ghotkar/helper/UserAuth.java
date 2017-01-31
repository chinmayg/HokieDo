package org.ghotkar.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by cghotkar on 12/17/16.
 * This class is used to asynchronously authenticate or create a user
 */

public class UserAuth extends AsyncTask<String, Void, Long> {
    private String TAG = "USERAUTH";
    private String mWebsiteURL = "Not Set";
    private String mUser = "";
    private String mPass = "";
    private String mType = "";
    private Context mContext; // context reference

    public UserAuth(Context context, String user, String pass, String type, String url){ //constructor
        this.mContext = context;
        this.mUser = user;
        this.mPass = pass;
        this.mType = type;
        mWebsiteURL = url;
    }

    protected Long doInBackground(String... cred) {
        String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
        String newURL = mWebsiteURL + "/" + mType;
        int timeout = 7000;
        long status = 0;

        Log.i(TAG, newURL);

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) new URL(newURL).openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setConnectTimeout(timeout);
            httpConnection.setReadTimeout(timeout);

            String query = String.format("user=%s&pass=%s",
                    URLEncoder.encode(mUser, charset),
                    URLEncoder.encode(mPass, charset));
            httpConnection.setRequestProperty("Accept-Charset", charset);
            httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            try (OutputStream output = httpConnection.getOutputStream()) {
                output.write(query.getBytes(charset));
            }

            httpConnection.connect();

            status = httpConnection.getResponseCode();
            Log.i(TAG, "Status " + status);

            httpConnection.disconnect();

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return status;

    }

    // Run on UI Thread
    protected void onPostExecute(Long status) {
        if (status == 200) {
            Log.i(TAG, "User Authenticated");
                /* if user exists, puts user name in Shared Preferences
                  so all activites have access to data */
            SharedPreferences myPrefs = mContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = myPrefs.edit();
            prefsEditor.remove("USER");
            prefsEditor.putString("USER", mUser);
            prefsEditor.apply();

            Intent i = new Intent();
            i.putExtra("username", mUser);
            Activity loginActivity = (Activity) mContext;
            loginActivity.setResult(Activity.RESULT_OK,i);
            loginActivity.finish();

        } else if (status == 201) {
            Log.i(TAG, "User Created");
        } else if (status == 401) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            // Add the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                }
            });
            builder.setMessage("The username or password is not correct! If you are having issues, contact server admin");
            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();

        } else if (status == 400) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            // Add the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                }
            });
            builder.setMessage("This UserName already exists!");
            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (status == 404) {
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