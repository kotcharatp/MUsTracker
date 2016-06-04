package com.bustracker.mustracker;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bustracker.mustracker.Class.routeSchedule;
import com.bustracker.mustracker.Database.Comment;
import com.bustracker.mustracker.Database.CommentDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

//Nothing Selected Spinner Adapter
//RouteStation
//RouteStationSource
//MySQLitehelper
public class createEditRoute extends AppCompatActivity {

    private static String url2 = "http://bus.atilal.com/route_station.php?";
    TextView outputText;
    String time, days="";
    JSONObject info;
    public static String checkLanguage;

    public static ArrayList<String> routeEnglish1 = new ArrayList<String>();
    public static ArrayList<String> routeThai1 = new ArrayList<String>();
    public static ArrayList<String> stationEnglish1 = new ArrayList<String>();
    public static ArrayList<String> stationThai1 = new ArrayList<String>();
    public static ArrayList<String> stationTime1 = new ArrayList<String>();

    //for translation
    public static ArrayList<String> stationEngTranslate = new ArrayList<String>();
    public static ArrayList<String> stationThaiTranslate = new ArrayList<String>();

    //all data from route_station database
    ArrayList<routeSchedule> allRouteEng = new ArrayList<routeSchedule>();
    ArrayList<routeSchedule> allRouteThai = new ArrayList<routeSchedule>();

    //spinner
    Spinner sp_route, sp_station,sp_stationTime;
    ArrayAdapter <String> adapter_route, adapter_station;

    //DATABASE IN PHONE
    CommentDataSource dataSource;
    ListView list;
    ArrayAdapter<Comment> adapter;
    ArrayAdapter<Comment> NotiArrayAdapter;

    Comment comment;

    //timepicker
    TimePicker notifyTime;

    //day
    String[] resultDay;

    //Translate chosen day
    public static List<String> dayEng, dayThai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_route);

        //Initialize list for translating days
        dayEng = asList("Every Monday", "Every Tuesday", "Every Wednesday", "Every Thursday", "Every Friday");
        dayThai = asList("ทุกวันจันทร์", "ทุกวันอังคาร", "ทุกวันพุธ", "ทุกวันพฤหัสบดี", "ทุกวันศุกร์");

        //Get intent value from FragmentTab_bus and schedule_details
        final Intent intent = getIntent();
        String fromActivity = intent.getStringExtra("sendFrom");
        String routeIntent = intent.getStringExtra("route_name");
        String timeIntent = intent.getStringExtra("time");
        final String stationIntent = intent.getStringExtra("station");

        //Get intent value from chooseDay
        final Intent in = getIntent();
        final int chosenRoute = in.getIntExtra("chosenRoute",0);
        final int chosenStation = in.getIntExtra("chosenStation", 0);

        outputText = (TextView) findViewById(R.id.outputText);
        TextView header = (TextView)findViewById(R.id.createEdit);
        header.setText(R.string.SetNotification);
        TextView timeRob = (TextView)findViewById(R.id.timeRob);
        timeRob.setText(R.string.TheBus);
        final TextView notiTime = (TextView)findViewById(R.id.time);
        notiTime.setText(R.string.SetNotiTime);
        TextView routeStation = (TextView)findViewById(R.id.routeStation);
        routeStation.setText(R.string.yourRouteStation);

        //CHOOSE DAY
        ImageButton ibutton = (ImageButton) findViewById(R.id.imageButton);

        //TIMEPICKER
        notifyTime = (TimePicker)findViewById(R.id.timePicker);
        notifyTime.setIs24HourView(true);

        //START JSON
        new JSONParse().execute();

        //DATABASE IN PHONE
        dataSource = new CommentDataSource(this);
        dataSource.open();
        List<Comment> values = dataSource.getAllComments();
        NotiArrayAdapter = new NotiArrayAdapter(this, android.R.layout.simple_list_item_1,values);
        list = (ListView) findViewById(R.id.listRoute);
        list.setAdapter(NotiArrayAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int position1 = position;

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(createEditRoute.this);
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setMessage("Are you sure you want to delete this notification?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Comment comment = NotiArrayAdapter.getItem(position1);
                        dataSource.deleteComment(comment); //delete on the database
                        NotiArrayAdapter.remove(comment); //delete from the list view
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

        //GET INTENT FROM CHOOSELANGUAGE ACTIVITY
        Intent i = getIntent();
        checkLanguage = getResources().getConfiguration().locale.getLanguage();

        //GET INTENT FROM CHOOSEDAY
        Bundle extras = i.getExtras();
        if(extras != null){
            if(extras.containsKey("selectedItems")) {
                //Do stuff because extra has been added
                resultDay = extras.getStringArray("selectedItems");
                for(int k=0;k<resultDay.length;k++){
                    days = days + resultDay[k];
                    if(k != resultDay.length-1) days = days + "\n";
                }
            }
        }

        checkLanguage = getResources().getConfiguration().locale.getLanguage();

        //SPINNER 1
        sp_route = (Spinner) findViewById(R.id.spinner_route);

        if(checkLanguage.contains("en")){
            adapter_route = new ArrayAdapter(this,android.R.layout.simple_spinner_item,routeEnglish1);
            ibutton.setImageResource(R.drawable.button_eng);
        }else{
            adapter_route = new ArrayAdapter(this,android.R.layout.simple_spinner_item,routeThai1);
            ibutton.setImageResource(R.drawable.button_thai);
        }
        adapter_route.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        //sp_route.setAdapter(new NothingSelectedSpinnerAdapter(adapter_route, R.layout.contact_spinner_row_nothing_selected, this));
        sp_route.setAdapter(adapter_route);

        //Set spinner based on the value from chosen schedule object from FragmentTab3_Bus
        if(intent.hasExtra("route_name")) {
            sp_route.setSelection(adapter_route.getPosition(routeIntent));
        }

        //Set spinner based on the value from chosen schedule object from chooseDay
        if(intent.hasExtra("chosenRoute")){
            sp_route.setSelection(chosenRoute);
        }

        //SPINNER 2
        sp_station = (Spinner) findViewById(R.id.spinner_station);

        //SPINNER 3
        final Spinner sp_stationTime = (Spinner) findViewById(R.id.spinner_day);

        sp_route.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(checkLanguage.contains("en")){
                    for(int i=0;i<allRouteEng.size();i++){
                        if(parent.getSelectedItem().equals(allRouteEng.get(i).getRouteEng())){
                            adapter_station = new ArrayAdapter<String>(createEditRoute.this, android.R.layout.simple_spinner_item,allRouteEng.get(i).getStationEng());
                            adapter_station.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                            //sp_station.setAdapter(new NothingSelectedSpinnerAdapter(adapter_station,R.layout.contact_spinner_row_station,createEditRoute.this));
                            sp_station.setAdapter(adapter_station);
                        }
                    }
                }else{
                    for(int i=0;i<allRouteThai.size();i++){
                        if(parent.getSelectedItem().equals(allRouteThai.get(i).getRouteEng())){
                            adapter_station = new ArrayAdapter<String>(createEditRoute.this, android.R.layout.simple_spinner_item,allRouteThai.get(i).getStationEng());
                            adapter_station.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                            sp_station.setAdapter(adapter_station);
                        }
                    }
                }
                //Set spinner based on the value from chosen schedule object from FragmentTab3_Bus or chooseDay
                if(intent.hasExtra("station")){
                    Log.d("sp_station", stationIntent);
                    sp_station.setSelection(adapter_station.getPosition(stationIntent));
                } else if(intent.hasExtra("chosenStation")){
                    Log.d("sp_station_fromChosen", String.valueOf(chosenStation));
                    sp_station.setSelection(chosenStation);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        sp_station.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (checkLanguage.contains("en")) {
                    for (int i = 0; i < allRouteEng.size(); i++) {
                        if (allRouteEng.get(i).getRouteEng().equals(sp_route.getSelectedItem().toString())) {
                            outputText.setText(allRouteEng.get(i).getStationTime().get(position));
                            String[] a = outputText.getText().toString().split(":");
                            notifyTime.setCurrentHour(Integer.valueOf(a[0]));
                            notifyTime.setCurrentMinute(Integer.valueOf(a[1]));
                        }
                    }
                } else {
                    for (int i = 0; i < allRouteThai.size(); i++) {
                        if (allRouteThai.get(i).getRouteEng().equals(sp_route.getSelectedItem().toString())) {
                            outputText.setText(allRouteThai.get(i).getStationTime().get(position));
                            String[] a = outputText.getText().toString().split(":");
                            notifyTime.setCurrentHour(Integer.valueOf(a[0]));
                            notifyTime.setCurrentMinute(Integer.valueOf(a[1]));
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }

    @Override
    public void onResume() {
        //START JSON
        super.onResume();
    }

    //JSON CLASS
    private class JSONParse extends AsyncTask<String, Void, String> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            StringBuilder sb = new StringBuilder();
            String contentRoute = MyHttpURL.getData(url2);
            try {
                //For getting route and station dropdown from phpmyadmin
                JSONObject objRoute = new JSONObject(contentRoute);
                JSONArray routeDrop = objRoute.getJSONArray("route_station");
                for (int i=0;i<routeDrop.length();i++){
                    info = (JSONObject) routeDrop.get(i);
                    //if(checkLanguage.contains("en")){
                        if(!routeEnglish1.contains(info.getString("route"))) routeEnglish1.add(info.getString("route"));
                    //}else{
                        if(!routeThai1.contains(info.getString("route_thai"))) routeThai1.add(info.getString("route_thai"));
                    //}
                    if(!stationEngTranslate.contains(info.getString("station"))) stationEngTranslate.add(info.getString("station"));
                    if(!stationThaiTranslate.contains(info.getString("station_thai"))) stationThaiTranslate.add(info.getString("station_thai"));
                }

                ArrayList<String> value = new ArrayList<String>();
                if(checkLanguage.contains("en")){
                    value = routeEnglish1;
                }else{
                    value = routeThai1;
                }
                for(int j=0;j<value.size();j++) {
                    //STATIONS
                    for (int k = 0; k < routeDrop.length(); k++) {
                        info = (JSONObject) routeDrop.get(k);
                        if(checkLanguage.contains("en")){
                            //eng
                            if (routeEnglish1.get(j).equals(info.getString("route"))) {
                                time = info.getString("time");
                                if (!stationEnglish1.contains(info.getString("station"))) {
                                    stationEnglish1.add(info.getString("station"));
                                    stationTime1.add(info.getString("stationTime"));
                                }
                            }
                        }
                        else{
                            //thai
                            if (routeThai1.get(j).equals(info.getString("route_thai"))) {
                                time = info.getString("time");
                                if (!stationThai1.contains(info.getString("station_thai"))) {
                                    stationThai1.add(info.getString("station_thai"));
                                    stationTime1.add(info.getString("stationTime"));
                                }
                            }
                        }
                    }

                    //STATIONS TEMP
                    ArrayList<String> a = new ArrayList<String>();
                    if(checkLanguage.contains("en")){
                        for(int c=0;c<stationEnglish1.size();c++) {
                            a.add(stationEnglish1.get(c));
                        }
                    }else{
                        for(int c=0;c<stationThai1.size();c++) {
                            a.add(stationThai1.get(c));
                        }
                    }


                    //STATIONS TIME
                    ArrayList<String> b = new ArrayList<String>();
                    for(int c=0;c<stationTime1.size();c++) {
                        b.add(stationTime1.get(c));
                    }

                    if(checkLanguage.contains("en")){
                        allRouteEng.add(new routeSchedule(routeEnglish1.get(j), a, b, time));
                    }else{
                        allRouteThai.add(new routeSchedule(routeThai1.get(j), a, b, time));
                    }

                    stationEnglish1.clear();
                    stationThai1.clear();
                    stationTime1.clear();
                }

                return sb.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("enterrr", String.valueOf(routeEnglish1.size()));
            outputText.setText("hello it's judy");
            adapter_route.notifyDataSetChanged();
            NotiArrayAdapter.notifyDataSetChanged();
        }
    }

    public void gotoAddDate(View v){
        Intent i = new Intent(createEditRoute.this,chooseDay.class);
        if(checkLanguage.contains("en")) i.putExtra("language","en");
        else i.putExtra("language","th");
        Log.d("sp_route", String.valueOf(sp_route.getSelectedItemPosition()));
        i.putExtra("chosenRoute", sp_route.getSelectedItemPosition());
        i.putExtra("chosenStation", sp_station.getSelectedItemPosition());
        startActivity(i);
    }
    public void gotochooseLanguage(View v){
        startActivity(new Intent(createEditRoute.this, chooseLanguage.class));
    }

    public void gotoNavigationSettingFromRoute(View v){
        startActivity(new Intent(createEditRoute.this, NavigationSetting.class));
        Intent i = new Intent(createEditRoute.this, NavigationSetting.class);
        if(checkLanguage.contains("en"))i.putExtra("language","en");
        else i.putExtra("language","th");
        startActivity(i);
    }

    public void addRouteStation(View v){
        notifyTime = ((TimePicker) findViewById(R.id.timePicker));
        notifyTime.clearFocus();
        int hour = notifyTime.getCurrentHour();
        int minute = notifyTime.getCurrentMinute();
        String h,m;
        if(hour <10)h = "0"+String.valueOf(hour);
        else h =String.valueOf(hour);
        if(minute<10) m = "0"+String.valueOf(minute);
        else m = String.valueOf(minute);

        if(days == "") days = "Every Monday";
        comment = null;

        String routeLanguage = new String();
        if(checkLanguage.contains("en")){
            routeLanguage = sp_route.getSelectedItem().toString();
        } else {
            for(int i=0; i<routeThai1.size(); i++){
                if(sp_route.getSelectedItem().toString().equals(routeThai1.get(i))){
                    Log.d("enterrr", String.valueOf(routeEnglish1.size()));
                    routeLanguage = routeEnglish1.get(i);
                }
            }
        }

        String stationLanguage = new String();
        if(checkLanguage.contains("en")){
            stationLanguage = sp_station.getSelectedItem().toString();
        } else {
            for(int i=0; i<stationThaiTranslate.size(); i++){
                if(sp_station.getSelectedItem().toString().equals(stationThaiTranslate.get(i))){
                    stationLanguage = stationEngTranslate.get(i);
                }
            }
        }

        String stationTime = outputText.getText().toString();

        comment = dataSource.createComment(
                routeLanguage,
                stationLanguage,
                stationTime,
                days,
                h+":"+m);
        NotiArrayAdapter.add(comment);
        NotiArrayAdapter.notifyDataSetChanged();
        Toast.makeText(this, getString(R.string.success), Toast.LENGTH_LONG).show();
    }

    public void deleteRouteStation(View v){
        if(NotiArrayAdapter.getCount() > 0){
            List<Comment> temp = dataSource.getAllComments();
            comment = (Comment)NotiArrayAdapter.getItem(temp.size()-1);
            dataSource.deleteComment(comment); //delete on the database
            NotiArrayAdapter.remove(comment); //delete from the list view
        }
    }

    //custom adapter
    class NotiArrayAdapter extends ArrayAdapter<Comment> {

        Context context;
        List<Comment> objects;
        public NotiArrayAdapter(Context context, int resource, List<Comment> objects) {
            super(context, resource, objects);
            this.context = context;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Comment d = objects.get(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.lsitlayout_comment, null);

            TextView routeText = (TextView) view.findViewById(R.id.routeText);
            if(checkLanguage.contains("en")){
                routeText.setText(d.getRoute());
            } else {
                for(int i=0; i< routeEnglish1.size(); i++){
                    if(d.getRoute().equals(routeEnglish1.get(i))){
                        routeText.setText(routeThai1.get(i));
                    }
                }
            }

            TextView stationText = (TextView) view.findViewById(R.id.stationText);
            Log.d("languageee eng_size", String.valueOf(stationEngTranslate.size()));
            Log.d("languageee thai_size", String.valueOf(stationThaiTranslate.size()));

            if(checkLanguage.contains("en")){
                stationText.setText(d.getStation());
            } else {
                for(int i=0; i<stationEngTranslate.size(); i++){
                    if(d.getStation().equals(stationEngTranslate.get(i))){
                        stationText.setText(stationThaiTranslate.get(i));
                    }
                }
            }


            TextView roundText = (TextView) view.findViewById(R.id.roundText);
            roundText.setText(d.getTime());

            TextView notifyText = (TextView) view.findViewById(R.id.notifydayText);
            if(checkLanguage.contains("en")){
                notifyText.setText(d.getNotifyday());
            } else {
                StringBuilder sb = new StringBuilder();
                String data = d.getNotifyday();
                String[] items = data.split("\n");
                for (String item : items) {
                    for (int j = 0; j < dayEng.size(); j++) {
                        if (dayEng.get(j).equals(item)) {
                            sb.append(dayThai.get(j) + "\n");
                        }
                    }
                }
                notifyText.setText(sb);
            }

            TextView notifyTimeText = (TextView) view.findViewById(R.id.notidyTimeText);
            notifyTimeText.setText(d.getNotifyTime());

            return view;
        }
    }
}