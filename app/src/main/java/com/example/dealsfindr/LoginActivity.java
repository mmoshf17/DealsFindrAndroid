package com.example.dealsfindr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.setTitle("Login");

    }

    public void onClickForgotPass(View view){

    }

    public void onClickProceedBtn(View view){

        new UserLogin().execute();

    }

    public  class UserLogin extends AsyncTask<String, Void, Void> {

        HttpConnection httpConnection = new HttpConnection();
        EditText userId = (EditText) findViewById(R.id.txtEmail);
        EditText userPassword = (EditText) findViewById(R.id.txtPassword);


        @Override
        protected Void doInBackground(String... params) {
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("username", userId.getText());
                postDataParams.put("password", userPassword.getText());

                //url = new URL("http://25.95.117.73:7549/api/Account/Login");
                //url = new URL("http://25.61.100.41:6429/api/Account/Login");
                url = new URL("http://192.168.1.196:45683/api/Account/Login");


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);

                OutputStream os = urlConnection.getOutputStream();

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(httpConnection.getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    String responseString = httpConnection.readStream(urlConnection.getInputStream());
                    JSONObject obj = new JSONObject(responseString);
                    String token = obj.get("access_token").toString();
                    String getUserRole = obj.get("roles").toString().trim();

                    String s1= getUserRole;


                    // "roles": "["Company"]",
                    //Saving Token
                    SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("token", token);
                    editor.apply();

                    //Saving logged in user for showing it later on in Profile Settings
                    String saveUser = userId.getText().toString();
                    editor.putString("savedUser", saveUser);
                    editor.apply();

                    if (getUserRole.contains("Supplier"))
                    {
                        //Go to Main after user is logged in
                        Intent goToMain = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(goToMain);
                        finish();
                    }
                    if(getUserRole.contains("Consumer"))
                    {
                        //Go to ..... after user is logged in
                        Intent goToMain = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(goToMain);
                        finish();
                    }




                } else {
                    //Showing message that you have entered your credential incorrect
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Login failed, please check your username/password",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }

    }
}
