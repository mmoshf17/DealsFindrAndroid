package com.example.dealsfindr.Consumer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dealsfindr.HttpConnection;
import com.example.dealsfindr.MainActivity;
import com.example.dealsfindr.R;
import com.example.dealsfindr.RegisterActivity;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;

public class ConsumerRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_register);

        this.setTitle("Register Now!");

    }


    public void onClickConsumerRegisterbtn(View view){

        new RegisterConsumer().execute();
    }

    public class RegisterConsumer extends AsyncTask<String, Void, Void> {

        private EditText editText;

        HttpConnection httpConnection = new HttpConnection();
        EditText firstName = findViewById(R.id.txtConsumerFirstName);
        EditText lastName = findViewById(R.id.txtConsumerLastName);
        EditText email = findViewById(R.id.txtConsumerEmail);
        EditText password = findViewById(R.id.txtConsumerPassword);
        EditText confirmPassword = findViewById(R.id.txtConsumerConfirmPassword);
        EditText phone = findViewById(R.id.txtConsumerPhone);
        EditText street = findViewById(R.id.txtConsumerStreetName);
        EditText house = findViewById(R.id.txtConsumerHouse);
        EditText post = findViewById(R.id.txtConsumerPost);
        EditText city = findViewById(R.id.txtConsumerCity);

        @Override
        protected Void doInBackground(String... params) {
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("FirstName", firstName.getText());
                postDataParams.put("LastName", lastName.getText());
                postDataParams.put("Email", email.getText());
                postDataParams.put("Password", password.getText());
                postDataParams.put("ConfirmPassword", confirmPassword.getText());
                postDataParams.put("PhoneNumber", phone.getText());
                postDataParams.put("StreetName", street.getText());
                postDataParams.put("HouseNumber", house.getText());
                postDataParams.put("PostCode", post.getText());
                postDataParams.put("CityName", city.getText());



                //url = new URL("http://25.95.117.73:7549/api/Account/Login");
                url = new URL("http://192.168.1.196:45683/api/Account/RegisterConsumer");


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

                    Intent goToAuthentication = new Intent(ConsumerRegisterActivity.this, MainActivity.class);
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
