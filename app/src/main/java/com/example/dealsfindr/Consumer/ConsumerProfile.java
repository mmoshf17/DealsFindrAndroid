package com.example.dealsfindr.Consumer;

import android.os.Bundle;

import com.example.dealsfindr.R;

import androidx.appcompat.app.AppCompatActivity;

public class ConsumerProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_register);

        this.setTitle("Profile Settings");

    }
}
