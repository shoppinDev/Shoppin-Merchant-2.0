package com.shoppin.merchant.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shoppin.merchant.main.MainActivity;
import com.shoppin.merchant.R;
import com.shoppin.merchant.util.JSONParser;
import com.shoppin.merchant.util.ModuleClass;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin,btnFacebook;
    TextView tvSignUp,tvForget;
    TextInputLayout tilEmail,tilPassword;

    EditText etEmailId;
    EditText etPassword;

    SwitchCompat switchRemember;
    boolean isRememberMe;

    public static final int REQUEST_CODE_SIGN_UP = 1001;

    boolean isCheckRemind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isCheckRemind = sharedPreferences.getBoolean(ModuleClass.KEY_MERCHANT_PASS_REMIND,false);

        etEmailId = (EditText) findViewById(R.id.input_email);
        etPassword = (EditText) findViewById(R.id.input_password);

        tilEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        tilPassword = (TextInputLayout) findViewById(R.id.input_layout_password);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        switchRemember = (SwitchCompat) findViewById(R.id.switchRemember);

        if(isCheckRemind){

            etEmailId.setText(sharedPreferences.getString(ModuleClass.KEY_MERCHANT_EMAIL,""));
            etPassword.setText(sharedPreferences.getString(ModuleClass.KEY_MERCHANT_PASSWORD,""));
            switchRemember.setChecked(true);

        }else {
            switchRemember.setChecked(false);
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etEmailId.getText().toString().equals("")){
                    Toast.makeText(LoginActivity.this,"Please enter your email address",Toast.LENGTH_LONG).show();
                    return;
                }

                //Bug Fix 52 (By mistakenly this condition placed after password validation)
                if(!isEmailValid(etEmailId.getText().toString())){
                    Toast.makeText(LoginActivity.this,"Email address is incorrect",Toast.LENGTH_LONG).show();
                    return;
                }

                if(etPassword.getText().toString().equals("")){
                    Toast.makeText(LoginActivity.this,"Please enter your password",Toast.LENGTH_LONG).show();
                    return;
                }

                if(switchRemember.isChecked()){
                    isRememberMe = true;
                }else{
                    isRememberMe = false;
                }

                new LoginTask(etEmailId.getText().toString(),etPassword.getText().toString()).execute();

            }
        });

        btnFacebook = (Button) findViewById(R.id.btnFacebook);
        try {
            btnFacebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(LoginActivity.this,"Not implemented yet....",Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvSignUp = (TextView) findViewById(R.id.tvSignUp);
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivityForResult(intent,REQUEST_CODE_SIGN_UP);
            }
        });

        tvForget = (TextView) findViewById(R.id.tvForget);
        tvForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("Notification","Login On Activity called");
        if(requestCode == REQUEST_CODE_SIGN_UP){
            if(resultCode == Activity.RESULT_OK){
                if(data.getBooleanExtra("finish_login",false)){
                    Log.v("Notification","Login Finish called");
                    LoginActivity.this.finish();
                }
            }
        }
    }

    private boolean isEmailValid(String paramString) {
        return EmailValidator.isValidEmail(paramString);
    }

    class LoginTask extends AsyncTask<Void,Void,Void> {

        String email,password;
        boolean success;
        String responseError;
        ProgressDialog dialog;
        public LoginTask(String email,String password){
            this.email = email;
            this.password = password;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            if(success){

                savePreferences(ModuleClass.KEY_MERCHANT_EMAIL,etEmailId.getText().toString());
                savePreferences(ModuleClass.KEY_MERCHANT_PASSWORD,etPassword.getText().toString());
                savePreferences(ModuleClass.KEY_MERCHANT_PASS_REMIND,isRememberMe);

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }else{
                Toast.makeText(LoginActivity.this,responseError,Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<NameValuePair> inputArray = new ArrayList<>();
            inputArray.add(new BasicNameValuePair("webmethod", "login"));
            inputArray.add(new BasicNameValuePair("email", email));
            inputArray.add(new BasicNameValuePair("password", password));

            JSONObject responseJSON = new JSONParser().makeHttpRequest(ModuleClass.LIVE_API_PATH+"merchant.php", "GET", inputArray);
            Log.d("LOGIN ", responseJSON.toString());

            if(responseJSON != null && !responseJSON.toString().equals("")) {

                try {
                    JSONArray dataArray = responseJSON.getJSONArray("data");

                    for(int i = 0;i < dataArray.length();i++){
                        JSONObject object = dataArray.getJSONObject(i);

                        if(object.getString("responsecode").equals("1")){
                            success = true;
                            SharedPreferences.Editor editor = ModuleClass.appPreferences.edit();
                            editor.putBoolean(ModuleClass.KEY_IS_REMEMBER,isRememberMe);
                            editor.putString(ModuleClass.KEY_MERCHANT_ID,object.getString("userid"));
                            editor.putString(ModuleClass.KEY_MERCHANT_NAME,object.getString("username"));
                            editor.commit();
                            ModuleClass.MERCHANT_ID = object.getString("userid");
                            ModuleClass.MERCHANT_NAME = object.getString("username");
                        }else{
                            success = false;
                            responseError = object.getString("message");
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else{
                success = false;
                responseError = "There is some problem in server connection";
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(LoginActivity.this,R.style.MyThemeDialog);
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setIndeterminate(true);
            dialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
            dialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        this.finish();
    }

    private void savePreferences(String key, boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private void savePreferences(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

}
