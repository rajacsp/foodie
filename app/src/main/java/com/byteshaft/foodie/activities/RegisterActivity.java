package com.byteshaft.foodie.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.foodie.R;
import com.byteshaft.foodie.utils.AppGlobals;
import com.byteshaft.foodie.utils.Helpers;
import com.byteshaft.foodie.utils.MultiPartUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class RegisterActivity extends Activity implements View.OnClickListener {

    private static RegisterActivity instance;
    private EditText userNameEdittext;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private Button loginButton;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userNameEdittext = (EditText) findViewById(R.id.username);
        emailEditText = (EditText) findViewById(R.id.email);
        passwordEditText = (EditText) findViewById(R.id.password);
        registerButton = (Button) findViewById(R.id.register);
        loginButton = (Button) findViewById(R.id.login);
        registerButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.getInstance().closeApplication();
    }

    public static RegisterActivity getInstance() {
        return instance;
    }

    public void closeApplication() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        startActivity(startMain);
        RegisterActivity.this.finish();
    }


    protected void onResume() {
        super.onResume();
        if (AppGlobals.sRegisterStatus) {
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                RegisterActivity.this.finish();
                break;
            case R.id.register:
                if (userNameEdittext.getText().toString().trim().isEmpty() ||
                        passwordEditText.getText().toString().trim().isEmpty()
                        || emailEditText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Helpers.isValidEmail(emailEditText.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "please enter a valid email",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!emailEditText.getText().toString().trim().isEmpty() &&
                        !userNameEdittext.getText().toString().trim().isEmpty() &&
                        !passwordEditText.getText().toString().trim().isEmpty()) {
                    String[] data = {userNameEdittext.getText().toString(),
                            passwordEditText.getText().toString(), emailEditText.getText().toString()};
                    new RegisterTask().execute(data);
                }

                break;
        }

    }

    class RegisterTask extends AsyncTask<String, String, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(RegisterActivity.this);
            mProgressDialog.setMessage("Registering...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            if (Helpers.isNetworkAvailable() && Helpers.isInternetWorking()) {
                try {
                    MultiPartUtility multiPartUtility =
                            new MultiPartUtility(new URL(AppGlobals.REGISTER_URL), "POST");
                    multiPartUtility.addFormField("username", params[0]);
                    multiPartUtility.addFormField("password", params[1]);
                    multiPartUtility.addFormField("email", params[2]);
                    String string = multiPartUtility.finish();
                    JSONObject jsonObject = new JSONObject(string);
                    if (jsonObject.getInt("result") == 0) {

                    }
                    return jsonObject.getInt("result");
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            return 404;
        }

        @Override
        protected void onPostExecute(Integer s) {
            super.onPostExecute(s);
            mProgressDialog.dismiss();
            if (s == 404) {
                Helpers.alertDialog(RegisterActivity.this, AppGlobals.NO_INTERNET_TITLE,
                        AppGlobals.NO_INTERNET_MESSAGE, null);
                userNameEdittext.setText("");
                emailEditText.setText("");
                passwordEditText.setText("");
            } else if (s == 0) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Helpers.alertDialog(RegisterActivity.this, AppGlobals.SUCCESS_TITLE,
                        AppGlobals.SUCCESS_MESSAGE, intent);
                AppGlobals.sRegisterStatus = true;
            } else if (s == 2) {
                Helpers.alertDialog(RegisterActivity.this, AppGlobals.USER_EXIST,
                        AppGlobals.USER_EXIST_MESSAGE, null);
                userNameEdittext.setText("");
            }
        }
    }
}

