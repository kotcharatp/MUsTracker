package com.bustracker.mustracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class signin_page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_page);

        final EditText userText = (EditText)findViewById(R.id.userText);
        final EditText passText = (EditText)findViewById(R.id.passText);

    }

    public void gotoMainActivity(View v){
        startActivity(new Intent(signin_page.this, MainActivity.class));
    }

    public void gotosignup(View v){
        startActivity(new Intent(signin_page.this, signup_page.class));
    }

}
