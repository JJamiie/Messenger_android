package com.rashata.jjamie.messenger.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rashata.jjamie.messenger.R;
import com.rashata.jjamie.messenger.adapter.MessageAdapter;
import com.rashata.jjamie.messenger.manager.Message;
import com.rashata.jjamie.messenger.manager.MessageModel;
import com.rashata.jjamie.messenger.util.HTTPHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;

public class MessageActivity extends Activity implements AppCompatCallback {
    private AppCompatDelegate delegate;
    private Toolbar mToolbar;
    private RecyclerView recyclerViewMessage;
    private ArrayList<Message> messages;
    private ArrayList<Message> content;

    private String usernameFriend;
    private String sessionId;
    private EditText edt_message;
    private Button btn_sent;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //create the delegate
        delegate = AppCompatDelegate.create(this, this);

        //call the onCreate() of the AppCompatDelegate
        delegate.onCreate(savedInstanceState);

        //use the delegate to inflate the layout
        delegate.setContentView(R.layout.activity_message);

        Intent intent = getIntent();
        usernameFriend = intent.getStringExtra("usernameFriend");
        sessionId = intent.getStringExtra("sessionId");

        //add the Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        delegate.setSupportActionBar(toolbar);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        delegate.setSupportActionBar(mToolbar);
        delegate.getSupportActionBar().setTitle(usernameFriend);
        delegate.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        content = new ArrayList<>();
        loadMessage();
        bindWidget();

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void bindWidget() {
        edt_message = (EditText) findViewById(R.id.edt_message);
        btn_sent = (Button) findViewById(R.id.btn_sent);
        recyclerViewMessage = (RecyclerView) findViewById(R.id.recycleViewMessage);
        recyclerViewMessage.setHasFixedSize(true);

        // To manage the positioning of its items
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerViewMessage.setLayoutManager(llm);
        messageAdapter = new MessageAdapter(content, usernameFriend);
        recyclerViewMessage.setAdapter(messageAdapter);

    }

    public void mClickSentMessage(View v) {
        new PostMessage().execute();
        edt_message.setText("");
    }

    private void loadMessage() {
        messages = MessageModel.getInstance().loadMessage(usernameFriend);
        content.clear();
        String lastDate = "";
        for (int i = 0; i < messages.size(); i++) {
            Message m = messages.get(i);
            if ((m.fromuser.equals(usernameFriend) || m.touser.equals(usernameFriend)) && !lastDate.equals(m.date)) {
                Message d = new Message();
                lastDate = m.date;
                d.date = m.date;
                d.type = Message.TYPE_DATE;
                content.add(d);
            }
            if (m.fromuser.equals(usernameFriend)) {
                m.type = Message.TYPE_FRIEND;
                content.add(m);
            } else if (m.touser.equals(usernameFriend)) {
                m.type = Message.TYPE_ME;
                content.add(m);
            }
        }

    }


    private class PostMessage extends AsyncTask<Void, Void, String> {
        HTTPHelper httpHelper = new HTTPHelper();
        String message;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            message = edt_message.getText().toString();
        }

        @Override
        protected String doInBackground(Void... params) {
            HashMap<String, String> param = new HashMap<String, String>();
            param.put("sessionid", sessionId);
            param.put("targetname", usernameFriend);
            param.put("message", message);
            return httpHelper.POST("https://mis.cp.eng.chula.ac.th/mobile/api/?q=postMessage", param);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new GetMessage().execute();
        }

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
                loadMessage();
                messageAdapter.notifyDataSetChanged();
                recyclerViewMessage.smoothScrollToPosition(content.size() - 1);
            }else{
                showToast("Please connect to the internet");
            }


        }

    }

    public void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MessageActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
