package com.byteshaft.foodie.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.byteshaft.foodie.R;
import com.byteshaft.foodie.utils.AppGlobals;
import com.byteshaft.foodie.utils.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class LoginActivity extends Activity implements View.OnClickListener {

    private Button mLogin;
    private EditText mEmail;
    private EditText mPassword;
    private String getEmail;
    private String getPassword;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLogin = (Button) findViewById(R.id.login_button);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mLogin.setOnClickListener(this);
        mEmail = (EditText) findViewById(R.id.email_login);
        mPassword = (EditText) findViewById(R.id.password_login);
        getEmail = mEmail.getText().toString();
        getPassword = mPassword.getText().toString();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                if (mEmail.getText().toString().trim().isEmpty() &&
                        mPassword.getText().toString().trim().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "All fields must be filled",
                            Toast.LENGTH_SHORT).show();

                }
                if (!mEmail.getText().toString().trim().isEmpty() &&
                        !mPassword.getText().toString().trim().isEmpty()) {
                    String[] data = {mEmail.getText().toString(), mPassword.getText().toString()};
                    new LoginTask().execute(data);
                }
                break;
        }
    }

    class LoginTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            mLogin.setClickable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String data = "";
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                try {
                  data =   Helpers.connectionRequest
                          (String.format(
                                  AppGlobals.LOGIN_URL +"username="+"%s"+"&password="+"%s",
                                  params[0], params[1]), "POST");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject jsonObject = new JSONObject(data);
                    System.out.println(jsonObject + "okay");
                    Helpers.saveDataToSharedPreferences(AppGlobals.KEY_USERNAME,
                            jsonObject.getString("username"));
                    Helpers.saveDataToSharedPreferences(AppGlobals.KEY_USER_ID,
                            String.valueOf(jsonObject.getInt("userid")));
                    Helpers.saveDataToSharedPreferences(AppGlobals.KEY_PASSWORD, params[1]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                System.out.println(jsonObject);
                if (jsonObject.get("result").equals("0")) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    mProgressBar.setVisibility(View.GONE);
                    Helpers.userLogin(true);

                } else if (!jsonObject.get("result").equals("0")) {
                    Toast.makeText(getApplicationContext(), "invalid credentials", Toast.LENGTH_SHORT)
                            .show();
                    mEmail.setText("");
                    mPassword.setText("");
                    mProgressBar.setVisibility(View.GONE);
                    mLogin.setClickable(true);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (AppGlobals.sRegisterStatus) {
        MainActivity.getInstance().closeApplication();
        }
    }
}
