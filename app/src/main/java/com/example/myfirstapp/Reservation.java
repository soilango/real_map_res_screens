package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Reservation extends AppCompatActivity {

    boolean outdoorSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        outdoorSelected = true;
    }

    public void toggle(View view) {
        ImageView iv = (ImageView) view;
        if (outdoorSelected) {
            outdoorSelected = false;
            iv.setImageResource(R.drawable.indoor_selected);
        }
        else {
            outdoorSelected = true;
            iv.setImageResource(R.drawable.outdoor_selected);
        }
    }

}