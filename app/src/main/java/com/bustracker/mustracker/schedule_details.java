package com.bustracker.mustracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bustracker.mustracker.Class.stationClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class schedule_details extends Activity {

    public String route;
    public String time;
    TextView travelText, appText;
    public List<stationClass> stationList;
    public List<stationClass> stationNotday;
    ArrayAdapter<stationClass> stationClassArrayAdapter;
    String travelMin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_details);

        //GET JSON DATA FROM SERVER
        new JSONParse().execute();

        stationList = new ArrayList<stationClass>();
        stationNotday = new ArrayList<stationClass>();

        travelText = (TextView) findViewById(R.id.travelText);
        appText = (TextView) findViewById(R.id.appText);

        Intent intent = getIntent();

        time = intent.getStringExtra("time");
        String driver = intent.getStringExtra("driver");
        route = intent.getStringExtra("route_name");
        int busno = intent.getIntExtra("bus_num", 0);
        final String phone = intent.getStringExtra("phoneNum");


        ImageView busImg = (ImageView) findViewById(R.id.busImg);
        String uri = "@drawable/" + "no_" + String.valueOf(busno);
        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
        Drawable res = getResources().getDrawable(imageResource);
        busImg.setImageDrawable(res);


        RelativeLayout rlayout = (RelativeLayout) findViewById(R.id.mainlayout);
        rlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });

        TextView travelText = (TextView) findViewById(R.id.travelText);
        //travelText.setText("30 mins");

        TextView detail = (TextView) findViewById(R.id.detail);
        String sourceString = "<b>" + getString(R.string.driver) + "</b> " + driver +
                "<br><b>" + getString(R.string.tel) + "</b> " + phone;
        detail.setText(Html.fromHtml(sourceString));


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

        final ListView mylist = (ListView) findViewById(R.id.myList);

        String listRes = "stationList";
        if(stationList.size()==0){
            stationClassArrayAdapter = new StationArrayAdapter(this, 0, stationNotday);
            listRes = "stationNotday";
        } else stationClassArrayAdapter = new StationArrayAdapter(this, 0, stationList);

        stationClassArrayAdapter.notifyDataSetChanged();
        mylist.setAdapter(stationClassArrayAdapter);

        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(schedule_details.this);
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setMessage(getString(R.string.sure));
                alertDialogBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(schedule_details.this, createEditRoute.class);

                        intent.putExtra("time", stationClassArrayAdapter.getItem(position).getTime_leave());
                        intent.putExtra("route_name", stationClassArrayAdapter.getItem(position).getRoute());
                        intent.putExtra("station", stationClassArrayAdapter.getItem(position).getStation());

                        startActivity(intent);
                    }
                });
                alertDialogBuilder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
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
        ProgressDialog p;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            p = new ProgressDialog(schedule_details.this);
            p.setMessage(getString(R.string.loading));
            p.setIndeterminate(false);
            p.setCancelable(true);
            p.show();
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

                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                    Date d = new Date();
                    String dayOfTheWeek = sdf.format(d);

                    if(route.equals(routeGet) && time.equals(timeLeave)){
                        if(!station.isEmpty()) {
                            if(day.equals(dayOfTheWeek)) {
                                stationList.add(new stationClass(station, day, timeLeave, timeTravel));
                            }else if(day.equals("Monday")) {
                                stationNotday.add(new stationClass(station, day, timeLeave, timeTravel));
                            }
                        }
                        else if(station.isEmpty() && day.equals(dayOfTheWeek)) travelMin = timeTravel + " " + getResources().getString(R.string.minute);
                        else if(station.isEmpty() && day.equals("Monday")) travelMin = timeTravel + " " + getResources().getString(R.string.minute);
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
            travelText.setText(travelMin);
            p.dismiss();

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
            View view = inflater.inflate(R.layout.listlayout_schedule_details, null);

            TextView stationText = (TextView) view.findViewById(R.id.routeText);
            if(createEditRoute.checkLanguage.contains("en")){
                stationText.setText(d.getStation());
            } else {
                for(int i=0; i<createEditRoute.stationEngTranslate.size(); i++){
                    if(d.getStation().equals(createEditRoute.stationEngTranslate.get(i))){
                        stationText.setText(createEditRoute.stationThaiTranslate.get(i));
                    }
                }
            }

            TextView arriveText = (TextView) view.findViewById(R.id.textView58);
            arriveText.setText(d.getTime_travel());

            return view;
        }
    }
}
