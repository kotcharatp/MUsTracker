package com.bustracker.mustracker;

/**
 * Created by kotcharat on 1/31/16.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

// Now is bus schedule
public class FragmentTab2_Seat extends Fragment {
    private static String url2 = "http://bus.atilal.com/route_station.php?";
    String time;
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

    //timepicker
    TimePicker notifyTime;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_seat, container, false);
        final TextView stationTime = (TextView) rootView.findViewById(R.id.stationTime);

        //TIME PICKER
        notifyTime = (TimePicker)rootView.findViewById(R.id.timePicker);
        notifyTime.setIs24HourView(true);

        //CHECK DAY
        TextView dayText = (TextView) rootView.findViewById(R.id.day);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.SATURDAY:
                dayText.setText("MONDAY"); break;
            case Calendar.SUNDAY:
                dayText.setText("MONDAY"); break;
            case Calendar.MONDAY:
                dayText.setText("MONDAY"); break;
            case Calendar.TUESDAY:
                dayText.setText("TUESDAY"); break;
            case Calendar.WEDNESDAY:
                dayText.setText("WEDNESDAY"); break;
            case Calendar.THURSDAY:
                dayText.setText("THURSDAY"); break;
            case Calendar.FRIDAY:
                dayText.setText("FRIDAY"); break;
        }

        //SPINNER 1
        sp_route = (Spinner) rootView.findViewById(R.id.spinner_route);

        if(NavigationSetting.checkLanguage.contains("en")){
            adapter_route = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,routeEnglish1);
        }else{
            adapter_route = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,routeThai1);
        }
        adapter_route.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        //sp_route.setAdapter(new NothingSelectedSpinnerAdapter(adapter_route, R.layout.contact_spinner_row_nothing_selected, this));
        sp_route.setAdapter(adapter_route);

        //SPINNER 2
        sp_station = (Spinner) rootView.findViewById(R.id.spinner_station);

        //SPINNER 3
        sp_stationTime = (Spinner) rootView.findViewById(R.id.spinner_day);

        sp_route.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(NavigationSetting.checkLanguage.contains("en")){
                    for(int i=0;i<allRouteEng.size();i++){
                        if(parent.getSelectedItem().equals(allRouteEng.get(i).getRouteEng())){
                            adapter_station = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item,allRouteEng.get(i).getStationEng());
                            adapter_station.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                            sp_station.setAdapter(adapter_station);
                        }
                    }
                }else{
                    for(int i=0;i<allRouteThai.size();i++){
                        if(parent.getSelectedItem().equals(allRouteThai.get(i).getRouteEng())){
                            adapter_station = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item,allRouteThai.get(i).getStationEng());
                            adapter_station.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                            sp_station.setAdapter(adapter_station);
                        }
                    }
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        sp_station.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(NavigationSetting.checkLanguage.contains("en")){
                    for(int i=0;i<allRouteEng.size();i++) {
                        if(allRouteEng.get(i).getRouteEng().equals(sp_route.getSelectedItem().toString())){
                            stationTime.setText(allRouteEng.get(i).getStationTime().get(position));
                            String [] a = stationTime.getText().toString().split("\\.");
                            /*notifyTime.setCurrentHour(Integer.valueOf(a[0]));
                            notifyTime.setCurrentMinute(Integer.valueOf(a[1]));*/
                        }
                    }
                }else{
                    for(int i=0;i<allRouteThai.size();i++) {
                        if(allRouteThai.get(i).getRouteEng().equals(sp_route.getSelectedItem().toString())){
                            stationTime.setText(allRouteThai.get(i).getStationTime().get(position));
                            String [] a = stationTime.getText().toString().split("\\.");
                            /*notifyTime.setCurrentHour(Integer.valueOf(a[0]));
                            notifyTime.setCurrentMinute(Integer.valueOf(a[1]));*/
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //START JSON
        new JSONParse().execute();
        return rootView;
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
                    if(NavigationSetting.checkLanguage.contains("en")){
                        if(!routeEnglish1.contains(info.getString("route"))) routeEnglish1.add(info.getString("route"));
                    }else{
                        if(!routeThai1.contains(info.getString("route_thai"))) routeThai1.add(info.getString("route_thai"));
                    }
                }

                ArrayList<String> value = new ArrayList<String>();
                if(NavigationSetting.checkLanguage.contains("en")){
                    value = routeEnglish1;
                }else{
                    value = routeThai1;
                }
                for(int j=0;j<value.size();j++) {
                    //STATIONS
                    for (int k = 0; k < routeDrop.length(); k++) {
                        info = (JSONObject) routeDrop.get(k);
                        if(NavigationSetting.checkLanguage.contains("en")){
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
                    if(NavigationSetting.checkLanguage.contains("en")){
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

                    if(NavigationSetting.checkLanguage.contains("en")){
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
            adapter_route.notifyDataSetChanged();
        }
    }




}
