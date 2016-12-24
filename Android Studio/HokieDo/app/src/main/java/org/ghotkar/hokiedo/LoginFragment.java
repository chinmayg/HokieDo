package org.ghotkar.hokiedo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

/**
 * Created by cghotkar on 12/24/16.
 */

public class LoginFragment extends Fragment {
    ProgressDialog pd_;
    String username_ = null;
    String password_ = null;
    String TAG = "LOGIN";
    String websiteURL_ = "Not Set";
    SharedPreferences myPrefs;
    private View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.login_fragment,container,false);
        Button loginBtn = (Button) v.findViewById(R.id.sign_in_button);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                doLogin("login");
            }
        });

        Button registerBtn = (Button) v.findViewById(R.id.register);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                doLogin("create");
            }
        });
        return v;
    }

    void requestAuthentication(String username, String password) {
        pd_ = ProgressDialog.show(getActivity(), null,
                "Authenticating...");
        pd_.setCancelable(true);
        UserAuth auth = new UserAuth(getActivity(),username, password, "login", websiteURL_);
        auth.execute();
    }

    void createUser(String username, String password) {
        pd_ = ProgressDialog.show(getActivity(), null,
                "Creating User...");
        pd_.setCancelable(true);
        UserAuth auth = new UserAuth(getActivity(), username, password, "create", websiteURL_);
        auth.execute();
    }

    private void doLogin(String type) {
        EditText uText = (EditText) getActivity().findViewById(R.id.email);
        EditText pText = (EditText) getActivity().findViewById(R.id.password);
        username_ = uText.getText().toString();
        password_ = pText.getText().toString();

        myPrefs = getActivity().getSharedPreferences("myPrefs", getActivity().MODE_PRIVATE);
        websiteURL_ = myPrefs.getString("SOCKET", "nothing");
        Log.i(TAG, websiteURL_);

        if ((username_.length() == 0) || (password_.length() == 0)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                requestAuthentication(username_, password_);
            } else {
                createUser(username_, password_);
            }
        }
    }
}