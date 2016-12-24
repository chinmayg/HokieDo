package org.ghotkar.hokiedo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.ghotkar.testnavigationdrawer.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by cghotkar on 12/24/16.
 */

public class PrefFragment extends Fragment {
    String ip_, port_, websiteURL_;
    String TAG = "PREFS";
    SharedPreferences myPrefs;
    ProgressDialog pd_;
    private View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.pref_fragment,container,false);
        Button saveButton = (Button) v.findViewById(R.id.setButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                changePort();
            }
        });

        return v;
    }


    private void changePort() {
        EditText ipText = (EditText) v.findViewById(R.id.ipText);
        EditText pText = (EditText) v.findViewById(R.id.portText);
        ip_ = ipText.getText().toString();
        port_ = pText.getText().toString();

        myPrefs = this.getActivity().getSharedPreferences("myPrefs", MODE_PRIVATE);
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
    }

    private void setTextBox() {
        EditText ipText = (EditText) v.findViewById(R.id.ipText);
        EditText pText = (EditText) v.findViewById(R.id.portText);

        myPrefs = this.getActivity().getSharedPreferences("myPrefs", MODE_PRIVATE);

        ip_ = myPrefs.getString("IP", "Not Set");
        port_ = myPrefs.getString("PORT", "Not Set");
        Log.i(TAG, ip_);
        Log.i(TAG, port_);
        ipText.setText(ip_);
        pText.setText(port_);


    }
}