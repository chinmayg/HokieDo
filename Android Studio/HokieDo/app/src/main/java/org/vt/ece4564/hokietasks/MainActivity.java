package org.vt.ece4564.hokietasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
import android.os.AsyncTask;
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

public class MainActivity extends Activity {

	ArrayList<String> rows = new ArrayList<String>();
	TableLayout taskTable;
	EditText taskText;
	SharedPreferences myPrefs;
	String TAG = "TASKS";
	ArrayBlockingQueue<String> q;
	static final int MAX_QUEUE_SIZE = 10;
	ProgressDialog pd_;
	String username_ = null;
	String websiteURL_ = "NULL";
	String jsonString_ = null;
	JSONArray msg;
	AlertDialog randomTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		q = new ArrayBlockingQueue<String>(MAX_QUEUE_SIZE);
		Intent i = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(i);

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
				TableRow row = new TableRow(MainActivity.this);
				CheckBox box = new CheckBox(MainActivity.this);
				EditText rowText = new EditText(MainActivity.this);
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
                myPrefs = MainActivity.this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
                username_ = myPrefs.getString("USER", "nothing");
				saveData();
            }

        });
		downloadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myPrefs = MainActivity.this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
                username_ = myPrefs.getString("USER", "nothing");
				updateUI();
            }

        });
	}

    private boolean isWebserverSet(){
        boolean ret = true;
        if (websiteURL_.contains("NULL") || !websiteURL_.matches(".*\\d+.*")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

	private class DownloadDataHandleAuth extends AsyncTask<String, Void, HttpResponse> {
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
			String newURL;
			newURL = createURLwithoutList(websiteURL_ + cred[1], cred[0]);

			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(newURL);
			Log.i(TAG, newURL);

			// Execute HTTP Post Request
			try {
				response = httpclient.execute(httpget);
				if(response == null){
					response.setStatusCode(404);
					return response;
				}
			} catch (ClientProtocolException e) {
				Log.e(TAG, e.getMessage());
				e.printStackTrace();
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
				Log.i(TAG, "Data Downloaded");
				taskTable.removeAllViews();
				Log.i(TAG, rows.size() + "");
				try {
					jsonString_ = EntityUtils.toString(result.getEntity());
					Log.i(TAG, result.getStatusLine().toString());
					Log.i(TAG, jsonString_);

					JSONParser parser = new JSONParser();
					Object obj = parser.parse(jsonString_);
					JSONObject jsonObject = (JSONObject) obj;
					msg = (JSONArray) jsonObject.get("list");
					rows.clear();
					if (msg != null) {
						for (int i = 0; i < msg.size(); i++) {
							rows.add(msg.get(i).toString());
						}
						Log.i(TAG, msg.size() + "");
					}

					for (int i = 0; i < rows.size(); i++) {
						TableRow row = new TableRow(MainActivity.this);
						CheckBox box = new CheckBox(MainActivity.this);
						EditText rowText = new EditText(MainActivity.this);
						rowText.setText(rows.get(i).toString());
						row.addView(box);
						row.addView(rowText);
						taskTable.addView(row);
					}
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
					e.printStackTrace();
				} catch (ParseException e){
					Log.e(TAG, e.getMessage());
					e.printStackTrace();
				}
			} else if (status_code == 400) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
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
			}
			else if (status_code == 404){
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

		protected String createURLwithoutList(String url, String user) {
			if(!url.endsWith("?"))
				url += "?";

			url += "user="+user;
			return url;
		}
	}

	private class UploadDataHandleAuth extends AsyncTask<String, Void, HttpResponse> {
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
            String newURL;
			String list = convertArrayListToString();
			newURL = createURLwithList(websiteURL_ + cred[1], cred[0], list);

			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(newURL);
			Log.i(TAG, newURL);
			Log.i(TAG, "Before Network Task");

			// Execute HTTP Post Request
			try {
				response = httpclient.execute(httpget);
				if(response == null){
					return response;
				}


			} catch (ClientProtocolException e) {
				Log.e(TAG, e.getMessage());
				e.printStackTrace();
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
				Log.i(TAG, "Data Updated");
			} else if (status_code == 400) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
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
			}
			else if (status_code == 404){
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

		protected String createURLwithList(String url, String user, String list) {
			if(!url.endsWith("?"))
				url += "?";

			url += "user="+user+"&list=["+list + "]";
			return url;
		}

        protected String convertArrayListToString() {
			String list = "";
			if (rows.size() == 0) {
				return list;
			} else if (rows.size() == 1) {
				list += rows.get(0);
			} else {
				for (int i = 0; i < rows.size(); i++) {
					list += rows.get(i) + ",";
				}
				list += rows.get(rows.size());
			}
            return list;
        }
	}

	private void updateUI() {
		myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
		websiteURL_ = myPrefs.getString("SOCKET", "nothing");
		pd_ = ProgressDialog.show(MainActivity.this, null, "Downloading...");
		pd_.setCancelable(true);
		if(isWebserverSet()){
			new DownloadDataHandleAuth().execute(username_, "getData");
		}
	}

	private void saveData() {
		myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
		websiteURL_ = myPrefs.getString("SOCKET", "nothing");
		pd_ = ProgressDialog.show(MainActivity.this, null,
				"Saving to Server...");
		pd_.setCancelable(true);
		if(isWebserverSet()) {
			new UploadDataHandleAuth().execute(username_, "updateData");
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
			Intent i = new Intent(MainActivity.this, PrefActivity.class);
			startActivity(i);
			break;

		default:
			break;
		}

		return true;
	}

}
