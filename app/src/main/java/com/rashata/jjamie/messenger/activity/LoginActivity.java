package com.rashata.jjamie.messenger.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rashata.jjamie.messenger.R;
import com.rashata.jjamie.messenger.manager.UserModel;
import com.rashata.jjamie.messenger.util.HTTPHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class LoginActivity extends Activity {
    private Button btn_sign_in;
    private EditText edt_username;
    private EditText edt_password;
    private CheckBox chk_remember_usr;
    private FrameLayout layout_loading;
    public static final String TAG = "LoginActivity";
    private SharedPreferences sharedPreferences;
    private String sessionId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindWidget();
    }


    public void bindWidget() {
        btn_sign_in = (Button) findViewById(R.id.btn_sign_in);
        edt_username = (EditText) findViewById(R.id.edt_username);
        edt_password = (EditText) findViewById(R.id.edt_password);
        layout_loading = (FrameLayout) findViewById(R.id.layout_loading);
        edt_password.setText("d7uk2x5d");
        chk_remember_usr = (CheckBox) findViewById(R.id.chk_remember_usr);
        sharedPreferences = getSharedPreferences("MY_PREFERENCE", Context.MODE_PRIVATE);
        edt_username.setText(sharedPreferences.getString("USERNAME", ""));
        chk_remember_usr.setChecked(sharedPreferences.getBoolean("REMEMBER", false));

    }

    public void mClickSignIn(View v) {
        sessionId = UserModel.getInstance().loadSessionId(edt_username.getText().toString(), edt_password.getText().toString());
        if (sessionId == null) {
            new SignInTask().execute();
        } else {
            openContactActivity();
        }

    }


    public void openContactActivity() {
        Intent intent = new Intent(this, ContactActivity.class);
        intent.putExtra("sessionId", sessionId);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (chk_remember_usr.isChecked()) {
            editor.putString("USERNAME", edt_username.getText().toString());
        } else {
            editor.putString("USERNAME", "");
        }
        editor.putBoolean("REMEMBER", chk_remember_usr.isChecked());
        editor.commit();
    }


    private class SignInTask extends AsyncTask<Void, Void, String> {
        HTTPHelper httpHelper = new HTTPHelper();
        String username;
        String password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            username = edt_username.getText().toString();
            password = edt_password.getText().toString();
            layout_loading.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(Void... params) {
            HashMap<String, String> param = new HashMap<String, String>();
            param.put("username", username);
            param.put("password", password);
            return httpHelper.POST("https://mis.cp.eng.chula.ac.th/mobile/service.php?q=api/signIn", param);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                layout_loading.setVisibility(View.GONE);

                if (result == null) {
                    showToast("Please connect to the internet");
                    return;
                }
                JSONObject jsonObject = new JSONObject(result);
                String type = jsonObject.getString("type");
                sessionId = jsonObject.getString("content");
                if (!type.equals("error")) {
                    UserModel.getInstance().addSessionId(username, password, sessionId);
                    openContactActivity();
                } else {
                    showToast("username or password not found");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    public void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

//    public void login(View view) {
//        AsyncTask<Void, Void, String> loginTask = new AsyncTask<Void, Void, String>() {
//            @Override
//            protected String doInBackground(Void... params) {
//                return null;
//            }
//        };
//        loginTask.execute();
//    }


}
