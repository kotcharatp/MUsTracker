package com.bustracker.mustracker;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bustracker.mustracker.Database.Comment;
import com.bustracker.mustracker.Database.CommentDataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//Nothing Selected Spinner Adapter
//RouteStation
//RouteStationSource
//MySQLitehelper
public class createEditRoute extends AppCompatActivity {

    private static String url2 = "http://bus.atilal.com/route_station.php?";
    TextView outputText;
    String time, days="";
    JSONObject info;
    String checkLanguage;

    public static ArrayList<String> routeEnglish1 = new ArrayList<String>();
    public static ArrayList<String> routeThai1 = new ArrayList<String>();
    public static ArrayList<String> stationEnglish1 = new ArrayList<String>();
    public static ArrayList<String> stationThai1 = new ArrayList<String>();
    public static ArrayList<String> stationTime1 = new ArrayList<String>();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_route);

        //Get intent value from FragmentTab_bus and schedule_details
        final Intent intent = getIntent();
        String fromActivity = intent.getStringExtra("sendFrom");
        String routeIntent = intent.getStringExtra("route_name");
        String timeIntent = intent.getStringExtra("time");
        final String stationIntent = intent.getStringExtra("station");


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
        }}
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
                            adapter_station = new ArrayAdapter<String>(createEditRoute.this, android.R.layout.simple_spinner_item,allRouteEng.get(i).getStationEng());                            adapter_station.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                            //sp_station.setAdapter(new NothingSelectedSpinnerAdapter(adapter_station,R.layout.contact_spinner_row_station,createEditRoute.this));
                            sp_station.setAdapter(adapter_station);
                        }
                    }
                }else{
                    for(int i=0;i<allRouteThai.size();i++){
                        if(parent.getSelectedItem().equals(allRouteThai.get(i).getRouteEng())){
                            adapter_station = new ArrayAdapter<String>(createEditRoute.this, android.R.layout.simple_spinner_item,allRouteThai.get(i).getStationEng());                            adapter_station.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                            sp_station.setAdapter(adapter_station);
                        }
                    }
                }
                //Set spinner based on the value from chosen schedule object from FragmentTab3_Bus
                if(intent.hasExtra("station")){
                    Log.d("sttion", adapter_station.getItem(0));
                    sp_station.setSelection(adapter_station.getPosition(stationIntent));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        sp_station.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(checkLanguage.contains("en")){
                    for(int i=0;i<allRouteEng.size();i++) {
                        if(allRouteEng.get(i).getRouteEng().equals(sp_route.getSelectedItem().toString())){
                            outputText.setText(allRouteEng.get(i).getStationTime().get(position));
                            String [] a = outputText.getText().toString().split(":");
                            notifyTime.setCurrentHour(Integer.valueOf(a[0]));
                            notifyTime.setCurrentMinute(Integer.valueOf(a[1]));
                        }
                    }
                }else{
                    for(int i=0;i<allRouteThai.size();i++) {
                        if(allRouteThai.get(i).getRouteEng().equals(sp_route.getSelectedItem().toString())){
                            outputText.setText(allRouteThai.get(i).getStationTime().get(position));
                            String [] a = outputText.getText().toString().split(":");
                            notifyTime.setCurrentHour(Integer.valueOf(a[0]));
                            notifyTime.setCurrentMinute(Integer.valueOf(a[1]));
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //START JSON
        new JSONParse().execute();
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
                    if(checkLanguage.contains("en")){
                        if(!routeEnglish1.contains(info.getString("route"))) routeEnglish1.add(info.getString("route"));
                    }else{
                        if(!routeThai1.contains(info.getString("route_thai"))) routeThai1.add(info.getString("route_thai"));
                    }
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
            outputText.setText("hello it's judy");
            adapter_route.notifyDataSetChanged();
        }
    }

    public void gotoAddDate(View v){
        Intent i = new Intent(createEditRoute.this,chooseDay.class);
        if(checkLanguage.contains("en"))i.putExtra("language","en");
        else i.putExtra("language","th");
        startActivity(i);
    }
    public void gotochooseLanguage(View v){
        startActivity(new Intent(createEditRoute.this, chooseLanguage.class));
    }

    public void gotoNavigationSettingFromRoute(View v){
        //startActivity(new Intent(createEditRoute.this, MainActivity.class));
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
        /*comment = dataSource.createComment(sp_route.getSelectedItem().toString() + "(" + outputText.getText().toString() + ")" + "\n"
                + sp_station.getSelectedItem().toString() + "\n" + days + "\n" + h + ":"+ m);

        public Comment createComment(String route, String station, String time, String notifyday, String notifytime) {*/
        //Log.d("Imhere", comment.toString());
        Log.d("Hello", sp_station.toString() );
        comment = dataSource.createComment(
                sp_route.getSelectedItem().toString(),
                sp_station.getSelectedItem().toString(),
                outputText.getText().toString(),
                days,
                h+":"+m);
        NotiArrayAdapter.add(comment);
        NotiArrayAdapter.notifyDataSetChanged();
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
            View view = inflater.inflate(R.layout.comment_list_layout, null);

            /*(sp_route.getSelectedItem().toString() + "(" + outputText.getText().toString() + ")" + "\n"
                    + sp_station.getSelectedItem().toString() + "\n"
                     + days + "\n"
                     + h + ":"+ m);*/

            //String data = d.toString();
            //String[] separated = data.split("\n");
            //Log.d("test", separated[0]);

            TextView routeText = (TextView) view.findViewById(R.id.routeText);
            routeText.setText(d.getRoute());

            TextView stationText = (TextView) view.findViewById(R.id.stationText);
            stationText.setText(d.getStation());

            TextView notifyText = (TextView) view.findViewById(R.id.notifydayText);
            notifyText.setText(d.getNotifyday());

            TextView notifyTimeText = (TextView) view.findViewById(R.id.notifyTimeText);
            notifyTimeText.setText(d.getNotifyTime());

            return view;
        }
    }
}