package com.bustracker.mustracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class schedule_details extends ActionBarActivity {

    public String route;
    public String time;
    TextView travelText;
    public List<stationClass> stationList;
    ArrayAdapter<stationClass> stationClassArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_details);

        stationList = new ArrayList<stationClass>();

        travelText = (TextView) findViewById(R.id.travelText);

        Intent intent = getIntent();

        time = intent.getStringExtra("time");
        String driver = intent.getStringExtra("driver");
        route = intent.getStringExtra("route_name");
        final String phone = intent.getStringExtra("phoneNum");

        ImageButton back = (ImageButton) findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView travelText = (TextView) findViewById(R.id.travelText);
        travelText.setText("30 minutes");

        TextView detail = (TextView) findViewById(R.id.detail);
        detail.setText(getString(R.string.driver) + "  " + driver + "\n" +
                getString(R.string.tel) + "  " + phone);

        ImageButton call = (ImageButton) findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                try {
                    startActivity(in);
                } catch (Exception e) {
                    Log.d("Call error", e.toString());
                }
            }
        });

        //GET JSON DATA FROM SERVER
        new JSONParse().execute();

        final ListView mylist = (ListView) findViewById(R.id.myList);
        stationClassArrayAdapter = new StationArrayAdapter(this, 0, stationList);
        stationClassArrayAdapter.notifyDataSetChanged();
        mylist.setAdapter(stationClassArrayAdapter);

        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(schedule_details.this);
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setMessage("Are you sure you want to add this station in your notification list?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(schedule_details.this, createEditRoute.class);

                        startActivity(intent);
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.setCancelable(true);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }


    //JSON CLASS
    class JSONParse extends AsyncTask<String, Void, String> {
        //private ProgressDialog p;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        /*p = new ProgressDialog(this);
        p.setMessage(getString(R.string.loading));
        p.setIndeterminate(false);
        p.setCancelable(true);
        p.show();*/
        }

        @Override
        protected String doInBackground(String... args) {
            StringBuilder sb = new StringBuilder();
            String url = "http://bus.atilal.com/travel_time_an.php?";
            String content = MyHttpURL.getData(url);

            try {
                //For getting route and station lat long from phpmyadmin
                JSONObject obj = new JSONObject(content);
                JSONArray stationDetail = obj.getJSONArray("travel_time");

                for (int i = 0; i < stationDetail.length(); i++) {
                    JSONObject info = (JSONObject) stationDetail.get(i);

                    String routeGet = info.getString("route");
                    String station = info.getString("station");
                    String day = info.getString("day");
                    String timeLeave = info.getString("time_leave");
                    String timeTravel = info.getString("time_travel");

                    Log.d("time from schedule", time);
                    if(route.equals(routeGet) && time.equals(timeLeave)){
                        Log.d("hello", "1");
                        stationList.add(new stationClass(station, day, timeLeave, timeTravel));
                    }
                }

                return sb.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            stationClassArrayAdapter.notifyDataSetChanged();

        }
    }

    //custom adapter
    class StationArrayAdapter extends ArrayAdapter<stationClass> {

        Context context;
        List<stationClass> objects;

        public StationArrayAdapter(Context context, int resource, List<stationClass> objects) {
            super(context, resource, objects);
            this.context = context;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            stationClass d = objects.get(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.station_list_layout, null);

            TextView timeText = (TextView) view.findViewById(R.id.stationText);
            timeText.setText(d.getStation());

            TextView arriveText = (TextView) view.findViewById(R.id.arriveText);
            arriveText.setText(d.getTime_leave());

            return view;
        }
    }
}
