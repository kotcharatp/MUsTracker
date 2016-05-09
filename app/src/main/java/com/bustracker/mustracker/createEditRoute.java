package com.bustracker.mustracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class createEditRoute extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_route);
    }

    public void gotochooseLanguage(View v){
        startActivity(new Intent(createEditRoute.this, chooseLanguage.class));
    }

    public void gotoMainActivityFromRoute(View v){
        startActivity(new Intent(createEditRoute.this, MainActivity.class));
    }
}
