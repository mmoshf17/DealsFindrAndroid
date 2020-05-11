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

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.setTitle("Register");

    }

    public void onClickRegisterbtn(View view){

        new Register().execute();


    }

    public class Register extends AsyncTask<String, Void, Void> {
        private EditText editText;

        HttpConnection httpConnection = new HttpConnection();
        EditText txtSupplierName = findViewById(R.id.txtSupplierName);
        EditText txtEmail = findViewById(R.id.txtEmail);
        EditText txtPhone = findViewById(R.id.txtPhone);
        EditText txtPassword = findViewById(R.id.txtPassword);
        EditText txtConfirmPassword = findViewById(R.id.txtConfirmPassword);
        EditText txtCVR = findViewById(R.id.txtCVR);

        @Override
        protected Void doInBackground(String... params) {
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("SupplierName", txtSupplierName.getText());
                postDataParams.put("Email", txtEmail.getText());
                postDataParams.put("PhoneNumber", txtPhone.getText());
                postDataParams.put("Password", txtPassword.getText());
                postDataParams.put("ConfirmPassword", txtConfirmPassword.getText());
                postDataParams.put("CVR", txtCVR.getText());



                //url = new URL("http://25.95.117.73:7549/api/Account/Login");
                url = new URL("http://192.168.1.196:45683/api/Account/RegisterSupplier");


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

                    Intent goToAuthentication = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(goToAuthentication);
                    finish();


                } else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST){
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Sign-up failed. Please try again",
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

        protected void onPostExecute(Void result){
            super.onPostExecute(result);

        }

    }

}
