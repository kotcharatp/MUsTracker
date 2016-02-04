package com.bustracker.mustracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class signin_page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_page);

    }

    public void gotoMainActivity(View v){
        startActivity(new Intent(signin_page.this, MainActivity.class));
    }

    public void gotosignup(View v){
        startActivity(new Intent(signin_page.this, signup_page.class));
    }

}
