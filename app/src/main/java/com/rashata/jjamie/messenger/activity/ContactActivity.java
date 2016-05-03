package com.rashata.jjamie.messenger.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.rashata.jjamie.messenger.R;
import com.rashata.jjamie.messenger.adapter.ContactAdapter;
import com.rashata.jjamie.messenger.manager.Message;
import com.rashata.jjamie.messenger.manager.MessageModel;
import com.rashata.jjamie.messenger.manager.UserModel;
import com.rashata.jjamie.messenger.util.HTTPHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactActivity extends Activity implements AppCompatCallback {
    private static final String TAG = "ContactActivity";
    private AppCompatDelegate delegate;
    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recycleViewContact;
    private ArrayList<String> contactList;
    private FloatingActionButton fab;
    private String sessionId;
    private String keyword = "";
    CharSequence[] usernameFriend;
    private ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create the delegate
        delegate = AppCompatDelegate.create(this, this);

        //call the onCreate() of the AppCompatDelegate
        delegate.onCreate(savedInstanceState);

        //use the delegate to inflate the layout
        delegate.setContentView(R.layout.activity_contact);

        //add the Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        delegate.setSupportActionBar(toolbar);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        delegate.setSupportActionBar(mToolbar);
        delegate.getSupportActionBar().setTitle("Contact");

        Intent intent = getIntent();
        sessionId = intent.getStringExtra("sessionId");
        contactList = UserModel.getInstance().getContact(sessionId);

    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetContact().execute();
        new GetMessage().execute();
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {

    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {

    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }


    public void bindWidget() {
        // set refresh when scroll recycle
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });


        recycleViewContact = (RecyclerView) findViewById(R.id.recyclViewContact);
        recycleViewContact.setHasFixedSize(true);

        // To manage the positioning of its items
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recycleViewContact.setLayoutManager(llm);

        // set refresh when scroll recycle
        contactAdapter = new ContactAdapter(contactList, this, sessionId);
        recycleViewContact.setAdapter(contactAdapter);
        fab = (FloatingActionButton) findViewById(R.id.fab);


    }

    public void refreshItems() {
        // Load items
        new GetContact().execute();
        // Load complete
        onItemsLoadComplete();
    }

    public void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...
        contactAdapter.notifyDataSetChanged();
        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }


    public void mClickAddContact(View v) {
        showDialogSearchFriend();
    }

    public void showDialogSearchFriend() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.add_user, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText edt_keyword = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Search",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                keyword = edt_keyword.getText().toString();
                                if (keyword.equals("")) {
                                    showToast("Please fill your friend's name");
                                } else {
                                    new SearchContact().execute();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private int selected = 0;
    private int buffKey = 0; // add buffer value

    public void showDialogListFriend() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);


        alertDialogBuilder.setSingleChoiceItems(
                usernameFriend,
                selected,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(
                            DialogInterface dialog,
                            int which) {
                        //set to buffKey instead of selected
                        //(when cancel not save to selected)
                        buffKey = which;
                    }
                })
                .setCancelable(false)
                .setPositiveButton("Add",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                //set buff to selected
                                selected = buffKey;
                                new AddContact().execute();
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                            }
                        }
                );

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private class GetContact extends AsyncTask<Void, Void, String> {
        HTTPHelper httpHelper = new HTTPHelper();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            HashMap<String, String> param = new HashMap<>();
            param.put("sessionid", sessionId);
            Log.d(TAG, "Sessionid: " + sessionId);
            return httpHelper.POST("https://mis.cp.eng.chula.ac.th/mobile/api/?q=getContact", param);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (result != null) {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("content");
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        String idFri = jsonArray.get(i).toString();
                        if (!idFri.equals("") && !contactList.contains(idFri)) {
                            UserModel.getInstance().addContact(sessionId, idFri);
                            contactList.add(jsonArray.get(i).toString());
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            bindWidget();

        }

    }

    private class AddContact extends AsyncTask<Void, Void, String> {
        HTTPHelper httpHelper = new HTTPHelper();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            HashMap<String, String> param = new HashMap<String, String>();
            param.put("sessionid", sessionId);
            param.put("username", usernameFriend[selected].toString());
            return httpHelper.POST("https://mis.cp.eng.chula.ac.th/mobile/api/?q=addContact", param);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new GetContact().execute();
            showToast("Added " + usernameFriend[selected].toString());
            contactAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class SearchContact extends AsyncTask<Void, Void, String> {
        HTTPHelper httpHelper = new HTTPHelper();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {
            HashMap<String, String> param = new HashMap<String, String>();
            param.put("sessionid", sessionId);
            param.put("keyword", keyword);
            return httpHelper.POST("https://mis.cp.eng.chula.ac.th/mobile/service.php?q=api/searchUser", param);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);

                    JSONArray jsonArray = jsonObject.getJSONArray("content");
                    if (jsonArray.length() == 0) {
                        showToast("Not found");
                        return;
                    }
                    usernameFriend = new CharSequence[jsonArray.length()];
                    for (int i = 0; i < usernameFriend.length; ++i) {
                        usernameFriend[i] = jsonArray.get(i).toString();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showDialogListFriend();
            } else {
                showToast("Please connect to the internet");
            }
        }
    }

    public Context getContext() {
        return this;
    }

    public void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ContactActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private class GetMessage extends AsyncTask<Void, Void, String> {
        HTTPHelper httpHelper = new HTTPHelper();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            HashMap<String, String> param = new HashMap<String, String>();
            param.put("sessionid", sessionId);
            String seq = MessageModel.getInstance().getLastSequence();
            param.put("seqno", seq);
            return httpHelper.POST("https://mis.cp.eng.chula.ac.th/mobile/api/?q=getMessage", param);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("content");
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        Message m = new Message();
                        m.seqno = object.getString("seqno");
                        m.fromuser = object.getString("from");
                        m.touser = object.getString("to");
                        m.message = object.getString("message");
                        String msgDate = object.getString("datetime");
                        String date[] = msgDate.split(" ");
                        String time[] = date[1].split(":");
                        m.date = date[0];
                        m.time = time[0] + ":" + time[1];
                        MessageModel.getInstance().addMessage(m);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

    }


}
