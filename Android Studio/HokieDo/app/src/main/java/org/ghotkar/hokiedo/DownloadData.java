package org.ghotkar.hokiedo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by cghotkar on 12/17/16.
 * This class asynchronously downloads data and updates the UI
 */

public class DownloadData extends AsyncTask<String, Void, String> {
    private String TAG = "DOWNLOAD";
    private String mWebsiteURL = "Not Set";
    private String mUser = "";
    private String mType = "";
    private ArrayList<String> mRows = null;
    private ProgressDialog mPd = null;
    private Context mContext; // context reference

    public DownloadData(Context context, String user, String type, String url, ArrayList<String> rows, ProgressDialog pd){ //constructor
        this.mContext = context;
        this.mUser = user;
        this.mType = type;
        this.mWebsiteURL = url;
        this.mRows = rows;
        this.mPd = pd;
    }

    protected String doInBackground(String... cred) {
        String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
        String newURL = mWebsiteURL + "/" + mType + "/" + "user" + "/" + mUser;
        String responseBody = null;

        Log.i(TAG, newURL);

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) new URL(newURL).openConnection();
            httpConnection.setRequestMethod("GET");

            httpConnection.setRequestProperty("Accept-Charset", charset);
            int status = httpConnection.getResponseCode();

            if (status == HttpURLConnection.HTTP_OK) {
                InputStream response = httpConnection.getInputStream();

                try (Scanner scanner = new Scanner(response)) {
                    responseBody = scanner.useDelimiter("\\A").next();
                    Log.i(TAG, responseBody);
                }


            } else {
                Integer stat = status;
                responseBody = stat.toString();
            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseBody;

    }

    // Run on UI Thread
    protected void onPostExecute(String response) {
        mPd.dismiss();
        if (response.contains("{")) {
            Log.i(TAG, "Data Downloaded");
            try {
                JSONObject object = new JSONObject(response);
                JSONArray array = object.getJSONArray("list");

                for (int i = 0; i < array.length(); i++) {
                    Log.i(TAG, array.getString(i));
                    mRows.add(array.getString(i));
                }

                final Activity todolist = (Activity) mContext;
                todolist.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*
                        final TableLayout taskTable = (TableLayout) todolist.findViewById(R.id.taskTable);
                        taskTable.removeAllViews();
                        for (int i = 0; i < mRows.size(); i++) {
                            Log.i(TAG, mRows.get(i));
                            TableRow row = new TableRow(mContext);
                            CheckBox box = new CheckBox(mContext);
                            EditText rowText = new EditText(mContext);
                            rowText.setText(mRows.get(i));
                            row.addView(box);
                            row.addView(rowText);
                            taskTable.addView(row);
                        }
                        */
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (response.contains("400")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    mContext);
            // Add the buttons
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                        }
                    });
            builder.setMessage("No previous data found!");
            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (response.contains("404")) {
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
