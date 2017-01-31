package org.ghotkar.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.ghotkar.helper.DividerItemDecoration;
import org.ghotkar.helper.DownloadData;
import org.ghotkar.adapters.TodoAdapter;
import org.ghotkar.helper.UploadData;
import org.ghotkar.testnavigationdrawer.R;
import org.json.JSONArray;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Fragment for listing out Todo items
 *
 */

public class TodoFragment extends Fragment {
    private View v;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<String> rows = new ArrayList<>();
    SharedPreferences myPrefs;
    String username_ = null;
    String websiteURL_ = "NULL";
    ProgressDialog pd_;
    String TAG = "TODO";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rows.add(0,"test");
        rows.add(1, "test1");
        rows.add(2,"test2");

        v = inflater.inflate(R.layout.todo_fragment, container, false);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add new item", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                rows.add("new");
                // specify an adapter (see also next example)
                mAdapter = new TodoAdapter(rows);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        // specify an adapter (see also next example)
        mAdapter = new TodoAdapter(rows);
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }


    private JSONArray convertArrayListToString() {
        JSONArray list = new JSONArray();
        for (int i = 0; i < rows.size(); i++) {
            list.put(rows.get(i));
        }
        return list;
    }

    private boolean isWebserverSet() {
        boolean ret = true;
        if (websiteURL_.contains("NULL") || !websiteURL_.matches(".*\\d+.*")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
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

    private void downloadData() {
        myPrefs = this.getActivity().getSharedPreferences("myPrefs", MODE_PRIVATE);
        websiteURL_ = myPrefs.getString("SOCKET", "nothing");
        pd_ = ProgressDialog.show(this.getActivity(), null, "Downloading...");
        pd_.setCancelable(true);
        if (isWebserverSet()) {
            DownloadData down = new DownloadData(this.getActivity(), username_, "getData", websiteURL_, rows, pd_);
            down.execute();
        }
    }

    private void uploadData() {
        myPrefs = this.getActivity().getSharedPreferences("myPrefs", MODE_PRIVATE);
        websiteURL_ = myPrefs.getString("SOCKET", "nothing");
        pd_ = ProgressDialog.show(this.getActivity(), null,
                "Saving to Server...");
        pd_.setCancelable(true);
        JSONArray array = convertArrayListToString();
        Log.i(TAG, array.toString());
        if (isWebserverSet()) {
            UploadData up = new UploadData(this.getActivity(), username_, "updateData", websiteURL_, array.toString(), pd_);
            up.execute();
        }
    }
}