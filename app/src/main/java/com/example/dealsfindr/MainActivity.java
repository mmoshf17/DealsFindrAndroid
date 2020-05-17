package com.example.dealsfindr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.dealsfindr.Consumer.ConsumerMainPage;
import com.example.dealsfindr.Consumer.ConsumerRegisterActivity;
import com.example.dealsfindr.Consumer.ConsumerSearchItems;
import com.example.dealsfindr.Consumer.ConsumerShoppingList;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Welcome");

    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");
        //String showLogUser = sharedPref.getString("savedUser", "");

        if (Objects.equals(token, "")) {

            Button logoutBtn = (Button) findViewById(R.id.LogoutBtn);
            logoutBtn.setVisibility(View.GONE);

        } else if (!Objects.equals(token, "")) {

            Button logoutBtn = (Button) findViewById(R.id.LogoutBtn);
            logoutBtn.setVisibility(View.VISIBLE);
        }

    }

    public void onClickRegisterbtn(View view){

        Intent goToRegister = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(goToRegister);


    }

    public void onClickLoginBtn(View view){

        Intent goToLogin = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(goToLogin);
    }

    public void itemBtn(View view){
        Intent goToItem = new Intent(getApplicationContext(), ItemActivity.class);
        startActivity(goToItem);
    }

    public void onClickOfferBtn(View view){
        Intent goToOffer = new Intent(getApplicationContext(), Offer.class);
        startActivity(goToOffer);
    }

    public void onClickPromotionBtn(View view){
        Intent goToPromotion = new Intent(getApplicationContext(), Promotion.class);
        startActivity(goToPromotion);
    }

    public void onClickLogoutBtn(View view){

        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.clear();
        editor.apply();

        Intent goToLogin = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(goToLogin);
        finish();
    }

    public void onClickRegisterConsumerbtn(View view){

        Intent goToRegisterConsumer = new Intent(getApplicationContext(), ConsumerRegisterActivity.class);
        startActivity(goToRegisterConsumer);
    }

    public void onClickConsumerShoppinglistbtn(View view){

        Intent goToConsumerShoppinglist = new Intent(getApplicationContext(), ConsumerShoppingList.class);
        startActivity(goToConsumerShoppinglist);
    }

    public void onClickConsumerMainPage(View view){

        Intent goToConsumerMainPage = new Intent(getApplicationContext(), ConsumerMainPage.class);
        startActivity(goToConsumerMainPage);
    }


    public void onClickConsumerSearchItem(View view){

        Intent goToConsumerSearchItem = new Intent(getApplicationContext(), ConsumerSearchItems.class);
        startActivity(goToConsumerSearchItem);
    }
}

