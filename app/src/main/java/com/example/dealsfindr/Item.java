package com.example.dealsfindr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

public class Item extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        this.setTitle("Item");

    }


    public void onClickSaveItemBtn(View view){

        new SaveItem().execute();
    }


    public class SaveItem extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            EditText itemName = findViewById(R.id.txtName);
            EditText price = findViewById(R.id.txtPrice);
            Spinner category = findViewById(R.id.spinnerCat);
            EditText description = findViewById(R.id.txtDescription);


            URL url;
            HttpURLConnection urlConnection = null;

            try {
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("Item_name", itemName.getText());
                postDataParams.put("Price", price.getText());
                postDataParams.put("CategoryId", category.getSelectedItemPosition());
                postDataParams.put("Description", description.getText());



                SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

                String token = sharedPref.getString("token", "");



                url = new URL("http://192.168.1.196:45683/api/AddItem");

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestProperty("Authorization", "Bearer " + token);

                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                PostDataString postDataString = new PostDataString();

                writer.write(postDataString.getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();


                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_ACCEPTED) {


                    Intent intent = new Intent(Item.this, MainActivity.class);
                    startActivity(intent);
                    finish();


                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {

                    Intent intentLogin = new Intent(Item.this, MainActivity.class);
                    startActivity(intentLogin);
                    finish();

                    Toast.makeText(getApplicationContext(), "Please login/signup, to sell an item.",
                            Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return null;
        }
    }
}
