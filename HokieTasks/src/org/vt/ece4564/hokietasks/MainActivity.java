package org.vt.ece4564.hokietasks;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

public class MainActivity extends Activity {

	ArrayList<OnClickListener> listOfListeners = new ArrayList<OnClickListener>();
	TableLayout taskTable;
	EditText taskText;
	SharedPreferences myPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Intent i = new Intent(MainActivity.this, PrefActivity.class);
		// Intent i = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(i);

		myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
		String prefName = myPrefs.getString("USER", "nothing");

		this.setTitle("Hello " + prefName);

		taskText = (EditText) findViewById(R.id.taskText);
		Button submitButton = (Button) findViewById(R.id.submitTaskButton);
		taskTable = (TableLayout) findViewById(R.id.taskTable);

		submitButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				// Add task to ui
				TableRow row = new TableRow(MainActivity.this);
				CheckBox box = new CheckBox(MainActivity.this);
				EditText rowText = new EditText(MainActivity.this);
				rowText.setText(taskText.getText().toString());
				row.addView(box);
				row.addView(rowText);
				taskTable.addView(row);
			}
		});
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
