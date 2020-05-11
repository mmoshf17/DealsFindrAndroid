package com.example.dealsfindr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class Offer extends AppCompatActivity {

    ArrayAdapter<AutoCompleteItems> adapter;
    AutoCompleteTextView autoCompleteView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        this.setTitle("Offer");



        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autocompleteName);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                String showLogUser = sharedPref.getString("savedUser", "");
                String token = sharedPref.getString("token", "");

                GetItemPrice getItemPrice = new GetItemPrice();
                getItemPrice.execute("http://192.168.1.196:45683/api/GetNormalPrice?email=" + showLogUser + "&itemName=" +
                        autoCompleteTextView.getText() );
            }
        });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Offer.this.adapter.getFilter().filter(s);


            }
            @Override
            public void afterTextChanged(Editable s) {



            }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String showLogUser = sharedPref.getString("savedUser", "");
        String token = sharedPref.getString("token", "");

        /*if (Objects.equals(token, "")) {
            //// MenuItem logoutItem = menu.findItem(R.id.action_logout);
            NavigationView navigationView = findViewById(R.id.navigation_view);
            Menu menu = navigationView.getMenu();
            MenuItem menuItem = menu.findItem(R.id.action_logout);
            menuItem.setVisible(false);
        }*/

        GetAllItemNames getAllItemNames = new GetAllItemNames();
        getAllItemNames.execute("http://192.168.1.196:45683/api/GetAllItemNames?email="+ showLogUser);


    }


    public void onClickSaveOffer(View view){

        new MakeOffer().execute();
    }

    public class MakeOffer extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            DatePicker endingDate = findViewById(R.id.clndEndingDate);
            EditText itemName = findViewById(R.id.autocompleteName);
            EditText newPrice = findViewById(R.id.txtPrice);

            int day = endingDate.getDayOfMonth();
            int month = endingDate.getMonth();
            int year =  endingDate.getYear();


            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy");
            String calendar = dateformat.format(c.getTime());


            URL url;
            HttpURLConnection urlConnection = null;

            try {
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("Item_name", itemName.getText());
                postDataParams.put("End_date", calendar);
                postDataParams.put("New_price", newPrice.getText());


                SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

                String token = sharedPref.getString("token", "");



                url = new URL("http://192.168.1.196:45683/api/AddOffer");

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


                    Intent intent = new Intent(Offer.this, MainActivity.class);
                    startActivity(intent);
                    finish();


                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {

                    Intent intentLogin = new Intent(Offer.this, MainActivity.class);
                    startActivity(intentLogin);
                    finish();

                    Toast.makeText(getApplicationContext(), "Please login/signup, to make an offer.",
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

    private class GetAllItemNames extends ReadHttpTask {
        @Override
        protected void onPostExecute(CharSequence jsonString) {

            //Gets the data from database and show all info into list by using loop
            final List<AutoCompleteItems> request = new ArrayList<>();

            try {

                JSONArray array = new JSONArray(jsonString.toString());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);

                    //token = obj.getString("UserId");
                    String itemName = obj.getString("Item_name");

                    AutoCompleteItems autoCompleteItems = new AutoCompleteItems (itemName);

                    request.add(autoCompleteItems);

                }

                 autoCompleteView = findViewById(R.id.autocompleteName);
                 adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_dropdown_item_1line, request);
                autoCompleteView.setAdapter(adapter);
                autoCompleteView.setThreshold(1);

            } catch (JSONException ex)
            {
                //messageTextView.setText(ex.getMessage());
                Log.e("InstallmentRequest", ex.getMessage());
            }


        }
    }


    private class GetItemPrice extends ReadHttpTask {
        @Override
        protected void onPostExecute(CharSequence jsonString) {

            try {
                JSONObject jsono = new JSONObject(jsonString.toString());


                TextView txtNormalPrice = findViewById(R.id.txtNormalPrice);
                //adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, request);
                String value = jsono.get("Price").toString();
                txtNormalPrice.setText(value);



            }

            catch (JSONException ex)
            {
                //messageTextView.setText(ex.getMessage());
                Log.e("getNormalPrice", ex.getMessage());
            }


        }
    }
}