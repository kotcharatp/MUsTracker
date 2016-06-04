package com.bustracker.mustracker;

/**
 * Created by kotcharat on 1/31/16.
 */
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bustracker.mustracker.Class.plotRoute;
import com.bustracker.mustracker.Database.Comment;
import com.bustracker.mustracker.Database.CommentDataSource;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")

//LINK WITH MY HTTP HRL
public class FragmentTab1_Home extends Fragment{
    ArrayList<LatLng> mMarkerPoints;
    String defaultRoute;
    ArrayList<plotRoute> routeD = new ArrayList<plotRoute>();

    TextView timerText,arrive_km,outputText;
    View rootView;

    CommentDataSource dataSource;
    int allseconds;
    boolean check_time = false;

    //GOOGLE MAP
    static final LatLng STATION1 = new LatLng(13.782057, 100.417540);
    private GoogleMap map;
    //static final LatLng MAHIDOL = new LatLng(13.792686, 100.326425);
    //static final LatLng PHAYATHAI = new LatLng(13.764905, 100.526270);

    Marker now = null;
    double lat,lng;
    int busNo;
    String duration, distance, route_name;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar();
        ((AppCompatActivity) getActivity()).setTitle("MUST BUS");
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //DATABASE IN PHONE
        //GET THE STATION TIME FROM DATABASE
        //CALCULATE ESTIMATED TIME BUS WILL ARRIVE
        dataSource = new CommentDataSource(getActivity());
        dataSource.open();
        List<Comment> values = dataSource.getAllComments();
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        for (int i=0;i<values.size();i++){
            String temp = values.get(i).toString();
            String [] buffer,hourMinute,routeT;
            buffer = temp.split("\n");
            String time = buffer[0].substring(buffer[0].indexOf("(")+1,buffer[0].indexOf(")"));
            routeT = buffer[0].split("\\(");
            //route_name = routeT[0];

            if(NavigationSetting.checkLanguage.contains("en")) {
                route_name = routeT[0];
            } else {
                for (int j = 0; j<createEditRoute.routeEnglish1.size(); j++) {
                    if (createEditRoute.routeEnglish1.get(j).equals(routeT[0])) {
                        route_name = createEditRoute.routeThai1.get(j);
                    }
                }
            }

            hourMinute = time.split(":");
            if(today.hour <= Integer.parseInt(hourMinute[0])){
                check_time = true;
                int h = Integer.parseInt(hourMinute[0]) - today.hour -1;
                int m = 60 - today.minute;
                allseconds = (h*60*60 + (m + Integer.parseInt(hourMinute[1]))*60 + today.second)*1000;
            }
        }

        TextView route_c = (TextView) rootView.findViewById(R.id.route_info);
        route_c.setText(route_name);

        //SET TIMER
        timerText = (TextView) rootView.findViewById(R.id.travelText);
        if(check_time == false){
            timerText.setText("00:00:00");
        }else{
            final CounterClass timerClass = new CounterClass(allseconds,1000); //adjust countdown here
            timerClass.start();
            check_time = false;
            /* EXPLAIN TIMER PROCESS
            1 sec = 1000 millisec
            this countdowntimer must use millisec, so if we want to tick for 1 sec we must input 1000 (millisec)
            ex. we want 3 mins -> 180 sec (60sec*3)-> 180000 millisec
            CounterClass(TotalTime in millisec, tick by 1 sec or 1000 millisec)*/
        }

        //GET JSON DATA FROM SERVER
        new JSONParse().execute();
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(30000);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //GET JSON DATA FROM SERVER
                                new JSONParse().execute();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();
        return rootView;
    }

    //TIMER FUNCTION
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public class CounterClass extends CountDownTimer{
        public CounterClass(long millisInFuture, long countDownInterval){
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
           // System.out.println(hms);
            timerText.setText(hms);
        }

        @Override
        public void onFinish() {
            //timerText.setTextSize(60);
            timerText.setText("Arrived");
        }
    }

    //JSON CLASS
    private class JSONParse extends AsyncTask<String, Void, String> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            outputText = (TextView) rootView.findViewById(R.id.textTest);

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            outputText.setText(R.string.outputText);
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                //GET REAL TIME LOCATION from bus_location_get.php
                JSONObject objLocation = new JSONObject(MyHttpURL.getData("http://bus.atilal.com/bus_location_get.php"));
                JSONArray realLocation = objLocation.getJSONArray("realtime_location");
                for(int i=0;i<realLocation.length();i++) {
                    JSONObject info = (JSONObject) realLocation.get(i);
                    lat = info.getDouble("latitude");
                    lng = info.getDouble("longitude");
                    busNo = info.getInt("bus_num");
                }

                LatLng origin = STATION1;
                LatLng destination = new LatLng(lat, lng);
                //private String getDirectionsUrl(LatLng origin,LatLng dest){
                String urlDuration = getDirectionsUrl(origin, destination);

                //GET DURATION FROM GOOGLE DIRECTION API
                JSONObject objDuration = new JSONObject(MyHttpURL.getData("https://maps.googleapis.com/maps/api/directions/json?origin=13.792686,100.326425&destination=13.746655,100.535724&sensor=false"));
                //JSONObject objDuration = new JSONObject(MyHttpURL.getData(urlDuration);
                JSONArray routesDu = objDuration.getJSONArray("routes");
                JSONObject inRoutesDu = (JSONObject) routesDu.get(0);
                JSONArray legsDu = inRoutesDu.getJSONArray("legs");
                JSONObject inLegsDu = (JSONObject) legsDu.get(0);
                JSONObject durationObj = inLegsDu.getJSONObject("duration");
                //duration = durationObj.getString("text");

                if(NavigationSetting.checkLanguage.contains("en")) {
                    duration = durationObj.getString("text");
                } else {
                    String[] distanceTemp = durationObj.getString("text").split(" ");
                    duration = distanceTemp[0] + " นาที";
                }


                //GET DISTANCE FROM GOOGLE DIRECTION API
                JSONObject objDistance = new JSONObject(MyHttpURL.getData("https://maps.googleapis.com/maps/api/directions/json?origin=13.792686,100.326425&destination=13.746655,100.535724&sensor=false"));
                JSONArray routesDi = objDistance.getJSONArray("routes");
                JSONObject inRoutesDi = (JSONObject) routesDi.get(0);
                JSONArray legsDi = inRoutesDi.getJSONArray("legs");
                JSONObject inLegsDi = (JSONObject) legsDi.get(0);
                JSONObject distanceObj = inLegsDi.getJSONObject("distance");

                if(NavigationSetting.checkLanguage.contains("en")) {
                    distance = distanceObj.getString("text");
                } else {
                    String[] distanceTemp = distanceObj.getString("text").split(" ");
                    distance = distanceTemp[0] + " กม.";
                }


                //GET LOCATION from LOCATION.PHP
                //FOR PLOT THE LOCATION OF STATIONS
                //For getting route and station dropdown from phpmyadmin
                JSONObject objRoute = new JSONObject(MyHttpURL.getData("http://bus.atilal.com/plot_routestation.php?"));
                JSONArray routeDrop = objRoute.getJSONArray("station");

                for (int i = 0; i < routeDrop.length(); i++){
                    JSONObject info = (JSONObject) routeDrop.get(i);

                    String route = info.getString("route");
                    if(!NavigationSetting.routeEnglish.contains(route)) NavigationSetting.routeEnglish.add(route);

                    String routeThai = info.getString("route_thai");
                    if(!NavigationSetting.routeThai.contains(routeThai)) NavigationSetting.routeThai.add(routeThai);

                    String stationEng = info.getString("station");
                    if(!NavigationSetting.stationEnglish.contains(stationEng)) NavigationSetting.stationEnglish.add(stationEng);

                    String stationThai = info.getString("station_thai");
                    if(!NavigationSetting.stationThai.contains(stationThai)) NavigationSetting.stationThai.add(stationThai);

                    LatLng point = new LatLng(info.getDouble("latitude"), info.getDouble("longitude"));

                    routeD.add(new plotRoute(route, point, stationEng, stationThai));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();
            if (!pDialog.isShowing())
            {
                TextView arrive_km = (TextView) rootView.findViewById(R.id.arrive_info);
                arrive_km.setText(String.valueOf(distance));

                TextView arrive_min = (TextView) rootView.findViewById(R.id.time_info);
                arrive_min.setText(String.valueOf(duration));

                outputText.setText(String.valueOf(lat) +" " + String.valueOf(lng));
            }
            setupMap();
        }
    }

    //map
    private void setupMap(){
        map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_home)).getMap();
        map.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;
        map.setMyLocationEnabled(true);

        //Use for ploting station of the default route set
        defaultRoute = "Salaya to Phayathai";

        final List<plotRoute> dList = new ArrayList<plotRoute>();
        Log.d("dList size ", String.valueOf(routeD.size()));
        for(int j=0; j<routeD.size(); j++){
            if(routeD.get(j).getRoute().equals(defaultRoute)) {
                dList.add(routeD.get(j));
            }
        }
        mMarkerPoints = new ArrayList<LatLng>();

        for(int i=0; i<dList.size(); i++) {
            drawMarker(dList.get(i).getPosition(), dList.get(i).getTitle(), dList.get(i).getSnip());
        }

        //GET THE LAT, LNG FROM JSON BUS_LOCATION_GET
        //THE MOST RECENT ROW
        //map.addMarker(new MarkerOptions().position(new LatLng(lat,lng))).setTitle("Bus");
        //drawMarker(new LatLng(lat,lng),"Bus","wtf");

        if(now != null) now.remove();
        // Creating a LatLng object for the current location
        now = map.addMarker(new MarkerOptions().position(new LatLng(lat,lng)));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        // Move the camera instantly to mahidol with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(STATION1, 11));

    }

    private void drawMarker(LatLng point, String title, String snip) {
        mMarkerPoints.add(point);
        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();
        // Setting the position of the marker
        options.position(point);
        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */

        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_station_icon));
        // Add new marker to the Google Map Android API V2
        options.title(title).snippet(snip);
        map.addMarker(options);
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }
}
