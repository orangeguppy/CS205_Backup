package com.example.cs205project;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class StartGame extends MainActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new SpaceShooter(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}