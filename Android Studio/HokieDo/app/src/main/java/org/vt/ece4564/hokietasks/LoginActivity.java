package org.vt.ece4564.hokietasks;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
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
	String TAG = "TASKS";
	String websiteURL_ = "Not Set";
	SharedPreferences myPrefs;
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

	private class UserAuth extends AsyncTask<String, Void, Long> {
		protected Long doInBackground(String... cred) {
            String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
            String user = cred[0];
            String pass = cred[1];
            String type = cred[2];
			String newURL = websiteURL_+"/"+ type;
            int timeout = 7000;
            long status = 0;

            Log.i(TAG, websiteURL_);

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) new URL(newURL).openConnection();
                httpConnection.setRequestMethod("POST");
                httpConnection.setConnectTimeout(timeout);
                httpConnection.setReadTimeout(timeout);

                String query = String.format("user=%s&pass=%s",
                        URLEncoder.encode(user, charset),
                        URLEncoder.encode(pass, charset));
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
            pd_.dismiss();

            if(status == 200){
                Log.i(TAG, "User Authenticated");
                /* if user exists, puts user name in Shared Preferences
                  so all activites have access to data */
                myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = myPrefs.edit();
                prefsEditor.remove("USER");
                prefsEditor.putString("USER", username_.toString());
                prefsEditor.commit();
                finish();
            }else if (status == 201) {
                Log.i(TAG, "User Created");
            } else if (status == 401) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
				AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
				AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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

	private void hideSoftKeyboard(Activity activity) {
		try {

			InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
		} catch (Exception e) {

		}
	}

	void requestAuthentication(String username, String password) {
		pd_ = ProgressDialog.show(LoginActivity.this, null,
				"Authenticating...");
		pd_.setCancelable(true);
		new UserAuth().execute(username, password, "login");
	}

	void createUser(String username, String password) {
		pd_ = ProgressDialog.show(LoginActivity.this, null,
				"Creating User...");
		pd_.setCancelable(true);
		new UserAuth().execute(username, password, "create");
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
}
