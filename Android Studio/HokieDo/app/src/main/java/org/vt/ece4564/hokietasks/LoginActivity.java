package org.vt.ece4564.hokietasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class LoginActivity extends Activity {
    ProgressDialog pd_;
    String username_ = null;
    String password_ = null;
    String TAG = "LOGIN";
    String websiteURL_ = "Not Set";
    SharedPreferences myPrefs;
    Boolean isUrlSet = false;
    static final int SETTINGS_REQUEST = 1;  // The request code

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button loginBtn = (Button) findViewById(R.id.sign_in_button);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                doLogin("login");
            }
        });

        Button registerBtn = (Button) findViewById(R.id.register);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                doLogin("create");
            }
        });

        Button prefBtn = (Button) findViewById(R.id.prefButton);

        prefBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent i = new Intent(LoginActivity.this, PrefActivity.class);
                i.putExtra("calling-activity", ActivityId.LOGIN_ACTIVITY);
                startActivity(i);
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Login Page") // TODO: Define a title for the content shown.
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

    private void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    void requestAuthentication(String username, String password) {
        pd_ = ProgressDialog.show(LoginActivity.this, null,
                "Authenticating...");
        pd_.setCancelable(true);
        UserAuth auth = new UserAuth(LoginActivity.this,username, password, "login", websiteURL_);
        auth.execute();
    }

    void createUser(String username, String password) {
        pd_ = ProgressDialog.show(LoginActivity.this, null,
                "Creating User...");
        pd_.setCancelable(true);
        UserAuth auth = new UserAuth(LoginActivity.this, username, password, "create", websiteURL_);
        auth.execute();
    }

    private void doLogin(String type) {
        EditText uText = (EditText) findViewById(R.id.email);
        EditText pText = (EditText) findViewById(R.id.password);
        username_ = uText.getText().toString();
        password_ = pText.getText().toString();

        myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        websiteURL_ = myPrefs.getString("SOCKET", "nothing");
        Log.i(TAG, websiteURL_);

        if ((username_.length() == 0) || (password_.length() == 0)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            // Add the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                }
            });
            builder.setMessage("Please fill in the username and the password lines!");
            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (websiteURL_.contains("NULL") || !websiteURL_.matches(".*\\d+.*")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
        } else {
            hideSoftKeyboard(this);
            if (type.contains("login")) {
                requestAuthentication(username_, password_);
            } else {
                createUser(username_, password_);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == SETTINGS_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                if(data.getBooleanExtra("urlset",false)) {
                    isUrlSet = true;
                }
            }
        }
    }
}
