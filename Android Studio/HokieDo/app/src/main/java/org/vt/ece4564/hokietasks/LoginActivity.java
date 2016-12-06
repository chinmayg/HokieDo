package org.vt.ece4564.hokietasks;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;

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

	private class CreateHandleAuth extends AsyncTask<String, Void, HttpResponse> {
		protected HttpResponse doInBackground(String... cred) {
			StatusLine error = new StatusLine() {
				@Override
				public ProtocolVersion getProtocolVersion() {
					return null;
				}

				@Override
				public int getStatusCode() {
					return 404;
				}

				@Override
				public String getReasonPhrase() {
					return null;
				}
			};
			HttpResponse response = new BasicHttpResponse(error);
			String newURL = addLocationToUrl(websiteURL_ + cred[2], cred[0], cred[1]);
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(newURL);
			Log.i(TAG, newURL);
			Log.i(TAG, "Before Network Task");

			// Execute HTTP Post Request
			try {
				response = httpclient.execute(httpget);
				if (response == null) {
					response.setStatusCode(404);
					return response;
				}
				Log.i(TAG, response.getStatusLine().toString());
			} catch (ClientProtocolException e) {
				Log.e(TAG, e.getMessage());
				e.printStackTrace();
				response.setStatusCode(404);
				return response;
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
				e.printStackTrace();
			}

			return response;

		}

		// Run on UI Thread
		protected void onPostExecute(HttpResponse result) {
			pd_.dismiss();

			StatusLine code = result.getStatusLine();
			int status_code = code.getStatusCode();

			if (status_code == 201) {
				Log.i(TAG, "User Created");
			} else if (status_code == 400) {
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
			} else if (status_code == 404) {
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

		protected String addLocationToUrl(String url, String user, String pwd) {
			if (!url.endsWith("?"))
				url += "?";
			url += "user=" + user + "&pass=" + pwd + "";
			return url;
		}
	}

	private class LoginHandleAuth extends AsyncTask<String, Void, HttpResponse> {
		protected HttpResponse doInBackground(String... cred) {
			StatusLine error = new StatusLine() {
				@Override
				public ProtocolVersion getProtocolVersion() {
					return null;
				}

				@Override
				public int getStatusCode() {
					return 404;
				}

				@Override
				public String getReasonPhrase() {
					return null;
				}
			};
			HttpResponse response = new BasicHttpResponse(error);
			String newURL = addLocationToUrl(websiteURL_ + cred[2], cred[0], cred[1]);
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(newURL);
			Log.i(TAG, newURL);
			Log.i(TAG, "Before Network Task");

			// Execute HTTP Post Request
			try {
				response = httpclient.execute(httpget);
				if (response == null) {
					response.setStatusCode(404);
					return response;
				}
				Log.i(TAG, response.getStatusLine().toString());
			} catch (ClientProtocolException e) {
				Log.e(TAG, e.getMessage());
				e.printStackTrace();
				response.setStatusCode(404);
				return response;
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
				e.printStackTrace();
			}

			return response;

		}

		// Run on UI Thread
		protected void onPostExecute(HttpResponse result) {
			pd_.dismiss();

			StatusLine code = result.getStatusLine();
			int status_code = code.getStatusCode();

			if (status_code == 200) {
				Log.i(TAG, "User Authenticated");
				finish();
			} else if (status_code == 401) {
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
			} else if (status_code == 404) {
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

		protected String addLocationToUrl(String url, String user, String pwd) {
			if (!url.endsWith("?"))
				url += "?";
			url += "user=" + user + "&pass=" + pwd + "";
			return url;
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
		new LoginHandleAuth().execute(username, password, "login");
	}

	void createUser(String username, String password) {
		pd_ = ProgressDialog.show(LoginActivity.this, null,
				"Creating User...");
		pd_.setCancelable(true);
		new CreateHandleAuth().execute(username, password, "create");
	}

	private void doLogin(String type) {
		EditText uText = (EditText) findViewById(R.id.email);
		EditText pText = (EditText) findViewById(R.id.password);
		username_ = uText.getText().toString();
		password_ = pText.getText().toString();

		myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
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
			if (type.contains("login")) {
				myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
				SharedPreferences.Editor prefsEditor = myPrefs.edit();
				prefsEditor.remove("USER");
				prefsEditor.putString("USER", username_.toString());
				prefsEditor.commit();

				hideSoftKeyboard(this);
				requestAuthentication(username_, password_);
			} else {
				hideSoftKeyboard(this);
				createUser(username_, password_);
			}
		}
	}
}
