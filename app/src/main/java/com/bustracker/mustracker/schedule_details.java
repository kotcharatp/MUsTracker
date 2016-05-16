package com.bustracker.mustracker;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class schedule_details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_details);

        Intent intent = getIntent();

        /* Note what sent in intent
        intent.putExtra("time", busSchedule.get(temp).getTime());
        intent.putExtra("bus_num", busSchedule.get(temp).getBusno());
        intent.putExtra("driver", busSchedule.get(temp).getDriver());
        intent.putExtra("phoneNum", busSchedule.get(temp).getTel());
        intent.putExtra("route_name", busSchedule.get(temp).getRoute());*/

        String time = intent.getStringExtra("time");
        String driver = intent.getStringExtra("driver");
        final String phone = intent.getStringExtra("phoneNum");
        ArrayList<plotRoute> list = (ArrayList<plotRoute>)getIntent().getExtras().getSerializable("stationList");


        ImageButton back = (ImageButton) findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView travelText = (TextView)findViewById(R.id.travelText);
        travelText.setText("30 minutes");

        TextView detail = (TextView)findViewById(R.id.detail);
        detail.setText(getString(R.string.driver) + "  "+ driver + "\n" +
                getString(R.string.tel)+"  " + phone);

        ImageButton call = (ImageButton)findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phone));
                try{
                    startActivity(in);
                } catch (Exception e){
                    Log.d("Call error", e.toString());
                }
            }
        });

        final ListView mylist = (ListView) findViewById(R.id.listView);
        ArrayList<String> stations = new ArrayList<String>();
        for(int i =1; i<list.size(); i++){
            stations.add(list.get(i).getTitle());
        }
        //ArrayAdapter adapter = new ArrayAdapter(this, 0, getResources().getStringArray(R.array.station_MU));
        //mylist.setAdapter(adapter);

    }
}
