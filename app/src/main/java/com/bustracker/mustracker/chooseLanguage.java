package com.bustracker.mustracker;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Locale;

//LINK WITH ITEMDATA & LANGUAGE ADAPTER
public class chooseLanguage extends AppCompatActivity {

    //can use every activity for check language
    String language;
    public ArrayList<itemData> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        list = new ArrayList<itemData>();
        list.add(new itemData("English",R.drawable.us_flag));
        list.add(new itemData("Thai",R.drawable.thai_flag));
        Spinner sp = (Spinner)findViewById(R.id.spinner_language);
        final languageAdapter adapter=new languageAdapter(this,R.layout.language_layout,R.id.arriveText,list);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemData a = (itemData) adapter.getItem(position);
                //Toast.makeText(chooseLanguage.this, a.getText(), Toast.LENGTH_SHORT).show();

                //SET LANGUAGE
                Configuration config = new Configuration();
                if(a.getText().equals("Thai")){
                    language = "th";
                    config.locale = new Locale("th");
                    getResources().updateConfiguration(config, null);
                } else {
                    language = "en";
                    config.locale = new Locale("en");
                    getResources().updateConfiguration(config, null);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //itemData class for info & languageAdapter for creating Adapter
    }

    public void gotocreateEditRoute(View v){
        Intent i = new Intent(chooseLanguage.this, createEditRoute.class);
        i.putExtra("language",language);
        startActivity(i);
    }
}
