package org.vt.ece4564.hokietasks;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
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
import android.nfc.Tag;
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
                myPrefs = MainActivity.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
                username_ = myPrefs.getString("USER", "nothing");
				saveData();
            }

        });
		downloadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myPrefs = MainActivity.this.getSharedPreferences("myPrefs", MODE_PRIVATE);
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

	private class DownloadDataHandleAuth extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... cred) {
            String user = cred[0];
            String type = cred[1];
            String charset = "UTF-8";  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
            String newURL = websiteURL_ + "/" + type + "/" + "user" + "/" + user;
            String responseBody = null;

            Log.i(TAG, newURL);

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) new URL(newURL).openConnection();
                httpConnection.setRequestMethod("GET");

                httpConnection.setRequestProperty("Accept-Charset", charset);
                int status = httpConnection.getResponseCode();

                if(status == HttpURLConnection.HTTP_OK) {
                    InputStream response = httpConnection.getInputStream();

                    try (Scanner scanner = new Scanner(response)) {
                        responseBody = scanner.useDelimiter("\\A").next();
                        Log.i(TAG, responseBody);
                    }


                } else {
                    Integer stat = new Integer(status);
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
			pd_.dismiss();

			if (response.contains("{")) {
				Log.i(TAG, "Data Downloaded");
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("list");

                    for (int i = 0; i < array.length(); i++) {
                        Log.i(TAG, array.getString(i));
                        rows.add(array.getString(i));
                    }
                    taskTable.removeAllViews();

                    for(int i = 0; i < rows.size(); i++){
                        Log.i(TAG,rows.get(i).toString());
                        TableRow row = new TableRow(MainActivity.this);
                        CheckBox box = new CheckBox(MainActivity.this);
                        EditText rowText = new EditText(MainActivity.this);
                        rowText.setText(rows.get(i).toString());
                        row.addView(box);
                        row.addView(rowText);
                        taskTable.addView(row);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
			} else if (response.contains("400")) {
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
			else if (response.contains("404")){
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
	}

	private class UploadDataHandleAuth extends AsyncTask<String, Void, Long> {
        protected Long doInBackground(String... cred) {
            String charset = "UTF-8";
            String user = cred[0];
            String type = cred[1];
            String list = cred[2];
            String newURL = websiteURL_ + "/" + type;
            Log.i(TAG, newURL);
            long status = 0;

                Log.i(TAG, list);

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) new URL(newURL).openConnection();
                httpConnection.setRequestMethod("POST");
                String query = String.format("user=%s&list=%s",
                                             user, list);
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
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return status;

        }

		// Run on UI Thread
		protected void onPostExecute(Long status_code) {
			pd_.dismiss();

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
	}
    private JSONArray convertArrayListToString() {
        JSONArray list = new JSONArray();
        for (int i = 0; i < rows.size(); i++) {
            list.put(rows.get(i));
        }
        return list;
    }

	private void updateUI() {
		myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
		websiteURL_ = myPrefs.getString("SOCKET", "nothing");
		pd_ = ProgressDialog.show(MainActivity.this, null, "Downloading...");
		pd_.setCancelable(true);
		if(isWebserverSet()){
			new DownloadDataHandleAuth().execute(username_, "getData");
		}
	}

	private void saveData() {
		myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
		websiteURL_ = myPrefs.getString("SOCKET", "nothing");
		pd_ = ProgressDialog.show(MainActivity.this, null,
				"Saving to Server...");
		pd_.setCancelable(true);
        JSONArray array = convertArrayListToString();
		if(isWebserverSet()) {
			new UploadDataHandleAuth().execute(username_, "updateData", array.toString());
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
