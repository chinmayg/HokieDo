package org.vt.ece4564.hokietasks;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {
	ProgressDialog m_pd;
	String m_username = null;
	String m_password = null;
	String TAG = "TASKS";
	String websiteURL_ = null;
	SharedPreferences myPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Button loginBtn = (Button) findViewById(R.id.sign_in_button);
		
		loginBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				doLogin();
			}
		});
		
		Button registerBtn = (Button) findViewById(R.id.register);
		
		registerBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				doCreate();
			}
		});
		
		Button prefBtn = (Button) findViewById(R.id.prefButton);
		
		prefBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
			     Intent i = new Intent(LoginActivity.this, PrefActivity.class);
			     startActivity(i);
			}
		});

	}

	private class HandleAuth extends AsyncTask<String, Void, Long> {
		protected Long doInBackground(String... cred) {
			HttpResponse response = null;
			long returnStat = -1;
			String newURL = addLocationToUrl(websiteURL_+cred[2], cred[0], cred[1]);
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(newURL);
			Log.i(TAG, newURL);
			Log.i(TAG, "Before Network Task");

			// Execute HTTP Post Request
			try {
				response = httpclient.execute(httpget);
				Log.i(TAG, response.getStatusLine().toString());
			} catch (ClientProtocolException e) {
				Log.e(TAG, e.getMessage());
				e.printStackTrace();
				returnStat=-3;
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
				e.printStackTrace();
			}
			if(response.getStatusLine().getStatusCode() == 200)
				returnStat = 1;
			else if(response.getStatusLine().getStatusCode() == 201)
				returnStat = 2;
			else if(response.getStatusLine().getStatusCode() == 400)
				returnStat = -1;
			else if(response.getStatusLine().getStatusCode() == 401)
				returnStat = -2;
			return returnStat;

		}

		// Run on UI Thread
		protected void onPostExecute(Long result) {
			m_pd.dismiss();
			if (result == 1) {
				Log.i(TAG, "User Authenticated");
			    finish();
			}
			else if (result == 2) {
				Log.i(TAG, "User Created");
			}
			else if (result == -1){
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
			}
			else if (result == -2){
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
			}
			else if (result == -3){
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
		
		protected String addLocationToUrl(String url, String user, String pwd){
		    if(!url.endsWith("?"))
		        url += "?";
		    JSONObject obj=new JSONObject();
		    obj.put("user", user);
		    obj.put("pwd",pwd);
		    Log.i(TAG,obj.toString());
		    List<NameValuePair> params = new LinkedList<NameValuePair>();
		    params.add(new BasicNameValuePair("info", obj.toString()));

		    String paramString = URLEncodedUtils.format(params, "utf-8");

		    url += paramString;
		    return url;
		}
	}

	private void hideSoftKeyboard(Activity activity) {
		try {

			InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
		} catch (Exception e) {

		}
	}
	
	void requestAuthentication(String username, String password) {
		m_pd = ProgressDialog.show(LoginActivity.this, null,
				"Authenticating...");
		m_pd.setCancelable(true);
		new HandleAuth().execute(username, password,"login");
	}
	
	void createUser(String username, String password) {
		m_pd = ProgressDialog.show(LoginActivity.this, null,
				"Creating User...");
		m_pd.setCancelable(true);
		new HandleAuth().execute(username, password,"create");
	}
	
	

	private void doLogin() {
		EditText uText = (EditText) findViewById(R.id.email);
		EditText pText = (EditText) findViewById(R.id.password);
		m_username = uText.getText().toString();
		m_password = pText.getText().toString();
		
		myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putString("USER", m_username.toString());
        prefsEditor.commit();
        
	    myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
	    websiteURL_ = myPrefs.getString("SOCKET", "nothing");
        
		if ((m_username.length() == 0) || (m_password.length() == 0)) {
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
		} else {
			hideSoftKeyboard(this);
			requestAuthentication(m_username, m_password);
		}
	}
	
	private void doCreate() {
		EditText uText = (EditText) findViewById(R.id.email);
		EditText pText = (EditText) findViewById(R.id.password);
		m_username = uText.getText().toString();
		m_password = pText.getText().toString();
		
	    myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
	    websiteURL_ = myPrefs.getString("SOCKET", "nothing");
		
		if ((m_username.length() == 0) || (m_password.length() == 0)) {
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
		} else {
			hideSoftKeyboard(this);
			createUser(m_username,m_password);
		}

	}
}
