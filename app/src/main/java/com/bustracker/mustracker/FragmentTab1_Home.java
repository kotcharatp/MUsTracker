package com.bustracker.mustracker;

/**
 * Created by kotcharat on 1/31/16.
 */
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

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

//LINK WITH MYHTTPHRL
public class FragmentTab1_Home extends Fragment {
    private  static  String url = "http://bus.atilal.com/location_an.php?";
    private static String url2 = "http://bus.atilal.com/plot_routestation.php?";

    ArrayList<LatLng> mMarkerPoints;
    String defaultRoute;
    ArrayList<plotRoute> routeD = new ArrayList<plotRoute>();

    ArrayAdapter<String> adapter_route;
    ArrayAdapter<String> adapter_station;
    String name, version;
    Button btnClick;
    TextView timerText, outputText;
    View rootView;

    //GOOGLE MAP
    //static final LatLng MAHIDOL = new LatLng(13.792686, 100.326425);
    static final LatLng STATION1 = new LatLng(13.782057, 100.417540);
    //static final LatLng PHAYATHAI = new LatLng(13.764905, 100.526270);
    private GoogleMap map;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar();
        ((AppCompatActivity) getActivity()).setTitle("MUST BUS");
        //for crate home button


        //TIMER
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        btnClick = (Button) rootView.findViewById(R.id.clickButton);
        timerText = (TextView) rootView.findViewById(R.id.timer);
        timerText.setText("00:03:00");

        final CounterClass timerClass = new CounterClass(180000,1000); //adjust countdown here
        /* EXPLAIN TIMER PROCESS
        1 sec = 1000 millisec
        this countdowntimer must use millisec, so if we want to tick for 1 sec we must input 1000 (millisec)
        ex. we want 3 mins -> 180 sec (60sec*3)-> 180000 millisec
        CounterClass(TotalTime in millisec, tick by 1 sec or 1000 millisec)
        */
        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerClass.start();
            }
        });

        //SPINNER
        Spinner sp1 = (Spinner) rootView.findViewById(R.id.spinner_route);
        adapter_route = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, MainActivity.routeEnglish);
        adapter_route.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sp1.setAdapter(adapter_route);

        Spinner sp2 = (Spinner) rootView.findViewById(R.id.spinner_station);
        adapter_station = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, MainActivity.stationEnglish);
        adapter_station.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sp2.setAdapter(adapter_station);

        //GET JSON DATA FROM SERVER
        new JSONParse().execute();

        //GOOGLE MAP
        setupMap();
        return rootView;
    }

    private void setupMap(){
        map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_home)).getMap();
        map.getUiSettings().setZoomControlsEnabled(true);

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
        if (mMarkerPoints.size() == 1) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_station_icon));
        }

        // Add new marker to the Google Map Android API V2
        options.title(title).snippet(snip);

        map.addMarker(options);
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
            System.out.println(hms);
            timerText.setText(hms);
        }

        @Override
         public void onFinish() {
            timerText.setText("Completed.");
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
            StringBuilder sb = new StringBuilder();
            String content = MyHttpURL.getData(url);
            String contentRoute = MyHttpURL.getData(url2);

            try {
                JSONObject obj = new JSONObject(content);
                JSONArray location = obj.getJSONArray("Location");
                for (int i = 0; i < location.length(); i++) {
                    JSONObject info = (JSONObject) location.get(i);
                    sb.append("Latitude = " + info.getString("latitude") + "\n");
                    sb.append("Longitude = " + info.getString("longitude") + "\n");
                    sb.append("Bus Number = " + info.getString("bus_num") + "\n");
                    sb.append("\n");
                }

                //For getting route and station dropdown from phpmyadmin
                JSONObject objRoute = new JSONObject(contentRoute);
                JSONArray routeDrop = objRoute.getJSONArray("station");

                for (int i = 0; i < routeDrop.length(); i++){
                    JSONObject info = (JSONObject) routeDrop.get(i);

                    String route = info.getString("route");
                    if(!MainActivity.routeEnglish.contains(route)) MainActivity.routeEnglish.add(route);

                    String routeThai = info.getString("route_thai");
                    if(!MainActivity.routeThai.contains(routeThai)) MainActivity.routeThai.add(routeThai);

                    String stationEng = info.getString("station");
                    if(!MainActivity.stationEnglish.contains(stationEng)) MainActivity.stationEnglish.add(stationEng);

                    String stationThai = info.getString("station_thai");
                    if(!MainActivity.stationThai.contains(stationThai)) MainActivity.stationThai.add(stationThai);

                    LatLng point = new LatLng(info.getDouble("latitude"), info.getDouble("longitude"));

                    routeD.add(new plotRoute(route, point, stationEng, stationThai));
                }
                Log.d("dList size at JSON", String.valueOf(routeD.size()));

                /*JSONObject objPlot = new JSONObject(contentRoute);
                JSONArray plot = objPlot.getJSONArray("station");

                for (int i = 0; i < routeDrop.length(); i++){
                    JSONObject info = (JSONObject) plot.get(i);

                    String route = info.getString("route");
                    LatLng point = new LatLng(info.getDouble("latitude"), info.getDouble("longitude"));
                    String title = info.getString("station");
                    String snip = info.getString("station_thai");

                    MainActivity.plotData.add(new plotRoute(route, point, title, snip));
                }*/

                return sb.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();
            outputText.setText(result);
            adapter_route.notifyDataSetChanged();
            adapter_station.notifyDataSetChanged();
            setupMap();
        }
    }
}
