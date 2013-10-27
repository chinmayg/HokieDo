package org.vt.ece4564.hokietasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

public class MainActivity extends Activity implements SensorEventListener {

	ArrayList<OnClickListener> listOfListeners = new ArrayList<OnClickListener>();
	ArrayList<String> rows = new ArrayList<String>();
	TableLayout taskTable;
	EditText taskText;
	SharedPreferences myPrefs;
	private SensorManager sensorMan;
	private Sensor accelerometer;
	String TAG = "TASKS";
	private float[] Gravity_;
	private float Accel_;
	private float Accel_Current_;
	private float Accel_Last_;
	ArrayBlockingQueue<String> q;
	static final int MAX_QUEUE_SIZE = 10;
	ProgressDialog pd_;
	String username_ = null;
	String password_ = null;
	String websiteURL_ = null;
	String jsonString_ = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		q = new ArrayBlockingQueue<String>(MAX_QUEUE_SIZE);
		Intent i = new Intent(MainActivity.this, PrefActivity.class);
		// Intent i = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(i);

		myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
		username_ = myPrefs.getString("USER", "nothing");

		this.setTitle("Hello " + username_);

		sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Accel_ = 0.00f;
		Accel_Current_ = SensorManager.GRAVITY_EARTH;
		Accel_Last_ = SensorManager.GRAVITY_EARTH;

		taskText = (EditText) findViewById(R.id.taskText);
		Button submitButton = (Button) findViewById(R.id.submitTaskButton);
		Button saveButton = (Button) findViewById(R.id.saveToServerButton);
		taskTable = (TableLayout) findViewById(R.id.taskTable);

		submitButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				// Plays Default Notification Sound to Confirm add to List
				Uri notification = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				Ringtone r = RingtoneManager.getRingtone(
						getApplicationContext(), notification);
				r.play();
				
				//Vibrates Device to Confirm add to list
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
		saveButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				saveData();
			}
			
		});
	}
	
	private class HandleAuth extends AsyncTask<String, Void, Long> {
		protected Long doInBackground(String... cred) {
			HttpResponse response = null;
			long returnStat = -1;
			String newURL = addLocationToUrl(websiteURL_+cred[1], cred[0],cred[1]);
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(newURL);
			Log.i(TAG, newURL);
			Log.i(TAG, "Before Network Task");

			// Execute HTTP Post Request
			try {
				response = httpclient.execute(httpget);
				jsonString_ = EntityUtils.toString(response.getEntity());
				Log.i(TAG, response.getStatusLine().toString());
				Log.i(TAG, jsonString_);
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
			pd_.dismiss();
			if (result == 1) {
				Log.i(TAG, "Data Downloaded");
			}
			else if (result == 2) {
				Log.i(TAG, "Data Updated");
			}
		}
		
		protected String addLocationToUrl(String url, String user, String type){
		    if(!url.endsWith("?"))
		        url += "?";
		    JSONObject obj=new JSONObject();
		    obj.put("user", user);
		    if(type.equals("updateData"))
		    	obj.put("task",rows);	
		    Log.i(TAG,obj.toString());
		    List<NameValuePair> params = new LinkedList<NameValuePair>();
		    params.add(new BasicNameValuePair("info", obj.toString()));

		    String paramString = URLEncodedUtils.format(params, "utf-8");

		    url += paramString;
		    return url;
		}
	}
	
	
	private void updateUI(){
	    myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
	    websiteURL_ = myPrefs.getString("SOCKET", "nothing");
		pd_ = ProgressDialog.show(MainActivity.this, null,
				"Downloading...");
		pd_.setCancelable(true);
		new HandleAuth().execute(username_,"getData");
	}
	
	private void saveData(){
	    myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
	    websiteURL_ = myPrefs.getString("SOCKET", "nothing");
		pd_ = ProgressDialog.show(MainActivity.this, null,
				"Saving to Server...");
		pd_.setCancelable(true);
		new HandleAuth().execute(username_,"updateData");
	}

	@Override
	public void onResume() {
		super.onResume();
		sensorMan.registerListener(this, accelerometer,
				SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	protected void onPause() {
		super.onPause();
		sensorMan.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			Gravity_ = event.values.clone();
			// Shake detection
			float x = Gravity_[0];
			float y = Gravity_[1];
			float z = Gravity_[2];
			Accel_Last_ = Accel_Current_;
			Accel_Current_ = FloatMath.sqrt(x * x + y * y + z * z);
			float delta = Accel_Current_ - Accel_Last_;
			Accel_ = Accel_ * 0.9f + delta;
			// Make this higher or lower according to how much
			// motion you want to detect
			if (Accel_ > 3) {
				Log.i(MainActivity.this.TAG, "It worked!");
			}
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// required method
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
