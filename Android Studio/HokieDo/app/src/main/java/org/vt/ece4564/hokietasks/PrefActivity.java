package org.vt.ece4564.hokietasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PrefActivity extends Activity {

    String ip_, port_, websiteURL_;
    String TAG = "TASKS";
    SharedPreferences myPrefs;
    ProgressDialog pd_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref);
        this.setTitle("Set Socket to Server");

        setTextBox();

        Button saveButton = (Button) findViewById(R.id.setButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                changePort();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });

    }

    private void changePort() {
        EditText ipText = (EditText) findViewById(R.id.ipText);
        EditText pText = (EditText) findViewById(R.id.portText);
        ip_ = ipText.getText().toString();
        port_ = pText.getText().toString();

        myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.clear();
        if (ip_.length() == 0 || port_.length() == 0) {
            ip_ = "NULL";
            port_ = "NULL";
        }
        prefsEditor.putString("SOCKET", "http://" + ip_ + ":" + port_);
        prefsEditor.putString("IP", ip_);
        prefsEditor.putString("PORT", port_);

        prefsEditor.commit();

        websiteURL_ = "http://" + ip_ + ":" + port_;
        Intent i = new Intent(PrefActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    private void setTextBox() {
        EditText ipText = (EditText) findViewById(R.id.ipText);
        EditText pText = (EditText) findViewById(R.id.portText);

        myPrefs = this.getSharedPreferences("myPrefs", MODE_PRIVATE);

        ip_ = myPrefs.getString("IP", "Not Set");
        port_ = myPrefs.getString("PORT", "Not Set");
        Log.i(TAG, ip_);
        Log.i(TAG, port_);
        ipText.setText(ip_);
        pText.setText(port_);


    }
}
