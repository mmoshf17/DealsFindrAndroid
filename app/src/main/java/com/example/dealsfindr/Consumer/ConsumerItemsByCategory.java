package com.example.dealsfindr.Consumer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dealsfindr.MainActivity;
import com.example.dealsfindr.PostDataString;
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

public class ConsumerItemsByCategory extends AppCompatActivity {

    private Categories categories;
    private int getCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_items_by_category);

        this.setTitle("Category items");

        Intent intent = getIntent();
        categories = (Categories) intent.getSerializableExtra("Categories");

        getCategoryId = categories.getCategoryId();

    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String getUserId = sharedPref.getString("savedUserId", "");


        //Getting all the items that are stored in the db
        GetItemsByCategory getItemsByCategory = new GetItemsByCategory();
        getItemsByCategory.execute("http://192.168.1.196:45683/api/GetItemsByCategory?userId=" + getUserId + "&categoryId=" + getCategoryId);
    }

    private class GetItemsByCategory extends ReadHttpTask {
        @Override
        protected void onPostExecute(CharSequence jsonString) {



            //Gets the data from database and show all info into list by using loop
            final List<CategoryItemsDetails> list = new ArrayList<>();

            try {
                JSONArray array = new JSONArray(jsonString.toString());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);

                    String itemName = obj.getString("Item_name");
                    String supplierName = obj.getString("Supplier_name");
                    int price = obj.getInt("Price");

                    CategoryItemsDetails categoryItemsDetails = new CategoryItemsDetails(itemName, price, supplierName);

                    list.add(categoryItemsDetails);


                }


                ListView listView = findViewById(R.id.categoryLV);
                ArrayAdapter<CategoryItemsDetails> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, list);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ConsumerItemsByCategory.this);
                    builder1.setMessage("Are you sure that you wish to add the item to the shopping list?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //dialog.cancel();

                                    CategoryItemsDetails categoriesDetails = (CategoryItemsDetails) parent.getItemAtPosition(position);


                                    new SaveShoppingItem(categoriesDetails.getItemName(), categoriesDetails.getPrice(), categoriesDetails.getSupplierName()).execute();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();



                });





            } catch (JSONException ex)
            {
                //messageTextView.setText(ex.getMessage());
                Log.e("CategoryItemsDetails", ex.getMessage());
            }


        }
    }

    public class SaveShoppingItem extends AsyncTask<String, Void, Void> {

        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String getUserId = sharedPref.getString("savedUserId", "");

        private String ItemName;
        private int Price;
        private String SupplierName;

        public SaveShoppingItem(String itemName, int price, String supplierName){

            this.ItemName = itemName;
            this.Price = price;
            this.SupplierName = supplierName;

        }
        @Override
        protected Void doInBackground(String... params) {




            URL url;
            HttpURLConnection urlConnection = null;

            try {
                JSONObject postDataParams = new JSONObject();

                postDataParams.put("Consumer_Id", getUserId);
                postDataParams.put("Item_name", ItemName);
                postDataParams.put("Price", Price);
                postDataParams.put("Supplier_name", SupplierName);


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


                    Intent intent = new Intent(ConsumerItemsByCategory.this, MainActivity.class);
                    startActivity(intent);
                    finish();


                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {

                    Intent intentLogin = new Intent(ConsumerItemsByCategory.this, MainActivity.class);
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
