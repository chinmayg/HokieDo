package org.vt.ece4564.hokietasks;

import java.util.ArrayList;
import org.json.JSONArray;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class TodoListActivity extends Activity {

    ArrayList<String> rows = new ArrayList<>();
    static final int SETTINGS_REQUEST = 1;  // The request code
    TableLayout taskTable;
    EditText taskText;
    SharedPreferences myPrefs;
    String TAG = "TODO";
    ProgressDialog pd_;
    String username_ = null;
    String websiteURL_ = "NULL";
    Boolean isUrlSet = false;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskText = (EditText) findViewById(R.id.taskText);
        Button submitButton = (Button) findViewById(R.id.submitTaskButton);
        Button saveButton = (Button) findViewById(R.id.saveToServerButton);
        Button downloadButton = (Button) findViewById(R.id.Button02);

        taskTable = (TableLayout) findViewById(R.id.taskTable);

        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Plays Default Notification Sound to Confirm add to List
                Uri notification = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(
                        getApplicationContext(), notification);
                r.play();

                // Vibrates Device to Confirm add to list
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                v.vibrate(500);

                // Add task to ui
                TableRow row = new TableRow(TodoListActivity.this);
                CheckBox box = new CheckBox(TodoListActivity.this);
                EditText rowText = new EditText(TodoListActivity.this);
                rowText.setText(taskText.getText().toString());
                rows.add(taskText.getText().toString());
                row.addView(box);
                row.addView(rowText);
                taskTable.addView(row);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myPrefs = TodoListActivity.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                username_ = myPrefs.getString("USER", "nothing");
                uploadData();
            }

        });
        downloadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myPrefs = TodoListActivity.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                username_ = myPrefs.getString("USER", "nothing");
                downloadData();
            }

        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private boolean isWebserverSet() {
        boolean ret = true;
        if (websiteURL_.contains("NULL") || !websiteURL_.matches(".*\\d+.*")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(TodoListActivity.this);
            // Add the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                }
            });
            builder.setMessage("Server and Port for webserver is not set!");
            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
            ret = false;
        }

        return ret;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private JSONArray convertArrayListToString() {
        JSONArray list = new JSONArray();
        for (int i = 0; i < rows.size(); i++) {
            list.put(rows.get(i));
        }
        return list;
    }

    private void downloadData() {
        myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        websiteURL_ = myPrefs.getString("SOCKET", "nothing");
        pd_ = ProgressDialog.show(TodoListActivity.this, null, "Downloading...");
        pd_.setCancelable(true);
        if (isWebserverSet()) {
            DownloadData down = new DownloadData(TodoListActivity.this, username_, "getData", websiteURL_, rows, pd_);
            down.execute();
        }
    }

    private void uploadData() {
        myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        websiteURL_ = myPrefs.getString("SOCKET", "nothing");
        pd_ = ProgressDialog.show(TodoListActivity.this, null,
                "Saving to Server...");
        pd_.setCancelable(true);
        JSONArray array = convertArrayListToString();
        Log.i(TAG, array.toString());
        if (isWebserverSet()) {
            UploadData up = new UploadData(TodoListActivity.this, username_, "updateData", websiteURL_, array.toString(), pd_);
            up.execute();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(TodoListActivity.this, PrefActivity.class);
                startActivityForResult(i,SETTINGS_REQUEST);
                break;

            default:
                break;
        }

        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == SETTINGS_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                if(data.getBooleanExtra("logout", false)) {
                    Intent i = new Intent(TodoListActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                } else if(data.getBooleanExtra("urlset",false)) {
                    isUrlSet = true;
                }
            }
        }
    }

}
