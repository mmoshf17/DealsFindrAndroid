package com.example.dealsfindr.Consumer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dealsfindr.AutoCompleteItems;
import com.example.dealsfindr.AutoCompleteItemsPromotions;
import com.example.dealsfindr.Item;
import com.example.dealsfindr.MainActivity;
import com.example.dealsfindr.PostDataString;
import com.example.dealsfindr.Promotion;
import com.example.dealsfindr.R;
import com.example.dealsfindr.ReadHttpTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ConsumerShoppingList extends AppCompatActivity {

    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<AutoCompleteItems> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_shoppinglist);

        this.setTitle("Shopping List");

    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String getUserId = sharedPref.getString("savedUserId", "");

        //Getting all the items that are stored in the db
        GetAllShoppingItems getAllShoppingItems = new GetAllShoppingItems();
        getAllShoppingItems.execute("http://192.168.1.196:45683/api/GetAllItems?userId=" + getUserId);


        //Getting all the shopping items that has been already added
        GetAllShoppingList getAllShoppingList = new GetAllShoppingList();
        getAllShoppingList.execute("http://192.168.1.196:45683/api/GetAllShoppingItems?userId=" + getUserId);


    }

    public void onClickAddItem(View view){

        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String getUserId = sharedPref.getString("savedUserId", "");


        new SaveShoppingItem().execute();
        autoCompleteTextView.setText("");

        GetAllShoppingList getAllShoppingList = new GetAllShoppingList();
        getAllShoppingList.execute("http://192.168.1.196:45683/api/GetAllShoppingItems?userId=" + getUserId);



    }

    public void onClickResetList(View view){

        new DeleteShoppingItems().execute();
    }


    public class SaveShoppingItem extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            URL url;
            HttpURLConnection urlConnection = null;

            try {
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("Item_name", autoCompleteTextView.getText());


                SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

                String token = sharedPref.getString("token", "");



                url = new URL("http://192.168.1.196:45683/api/AddItemShoppingList");

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


                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {

                    Intent intentLogin = new Intent(ConsumerShoppingList.this, MainActivity.class);
                    startActivity(intentLogin);
                    finish();

                    Toast.makeText(getApplicationContext(), "Error",
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

    public class DeleteShoppingItems extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            URL url;
            HttpURLConnection urlConnection = null;

            try {



                SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

                String token = sharedPref.getString("token", "");

                String getUserId = sharedPref.getString("savedUserId", "");



                url = new URL("http://192.168.1.196:45683/api/DeleteShoppingList?userId=" + getUserId);

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestProperty("Authorization", "Bearer " + token);

                urlConnection.setRequestMethod("DELETE");
                urlConnection.setDoInput(true);

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                PostDataString postDataString = new PostDataString();

                //writer.write(postDataString.getPostDataString());

                writer.flush();
                writer.close();
                os.close();


                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_ACCEPTED) {


                    Intent intent = new Intent(ConsumerShoppingList.this, ConsumerShoppingList.class);
                    startActivity(intent);
                    finish();

                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "The shopping list is empty now!",
                                    Toast.LENGTH_LONG).show();
                        }
                    });


                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {

                    Intent intentLogin = new Intent(ConsumerShoppingList.this, MainActivity.class);
                    startActivity(intentLogin);
                    finish();

                    Toast.makeText(getApplicationContext(), "Please login to continue!",
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

    private class GetAllShoppingItems extends ReadHttpTask {
        @Override
        protected void onPostExecute(CharSequence jsonString) {
            //int itemId = 0;
            //Gets the data from database and show all info into list by using loop
            final List<AutoCompleteItems> request = new ArrayList<>();

            try {

                JSONArray array = new JSONArray(jsonString.toString());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);

                    //token = obj.getString("UserId");
                    String itemName = obj.getString("Item_name");
                    //itemId = obj.getInt("Item_Id");




                    AutoCompleteItems autoCompleteItems = new AutoCompleteItems (itemName);

                    request.add(autoCompleteItems);

                }
//
                autoCompleteTextView =  findViewById(R.id.acSearchShoppingItem);

                //input = findViewById(R.id.autoCompletetxtName);
                adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_dropdown_item_1line, request);
                autoCompleteTextView.setAdapter(adapter);

                autoCompleteTextView.setThreshold(1);

            } catch (JSONException ex)
            {
                //messageTextView.setText(ex.getMessage());
                Log.e("InstallmentRequest", ex.getMessage());
            }


        }
    }

    private class GetAllShoppingList extends ReadHttpTask {
        @Override
        protected void onPostExecute(CharSequence jsonString) {



            //Gets the data from database and show all info into list by using loop
            final List<AutoCompleteItemsPromotions> list = new ArrayList<>();

            try {
                JSONArray array = new JSONArray(jsonString.toString());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);



                    String itemName = obj.getString("Item_name");




                    AutoCompleteItemsPromotions autocomplete = new AutoCompleteItemsPromotions(itemName);

                    list.add(autocomplete);


                }


                ListView listView = findViewById(R.id.shoppingItemsLV);
                ArrayAdapter<AutoCompleteItemsPromotions> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, list);
                listView.setAdapter(adapter);

            } catch (JSONException ex)
            {
                //messageTextView.setText(ex.getMessage());
                Log.e("AutoCompleteItem", ex.getMessage());
            }


        }
    }
}
