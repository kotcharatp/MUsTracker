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

import java.util.concurrent.TimeUnit;
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")

public class FragmentTab1_Home extends Fragment {
    private  static  String url = "http://bus.atilal.com/location_an.php?";
    String name, version;
    Button btnClick;
    TextView timerText, outputText;
    View rootView;

    //GOOGLE MAP

    static final LatLng MAHIDOL = new LatLng(13.792686, 100.326425);
    static final LatLng STATION1 = new LatLng(13.782057, 100.417540);
    static final LatLng PHAYATHAI = new LatLng(13.764905, 100.526270);
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
        ArrayAdapter<String> adapter_route = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.routes_list));
        adapter_route.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sp1.setAdapter(adapter_route);

        Spinner sp2 = (Spinner) rootView.findViewById(R.id.spinner_station);
        ArrayAdapter<String> adapter_station = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.station_MU));
        adapter_station.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sp2.setAdapter(adapter_station);

        //GOOGLE MAP
        map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_home)).getMap();
        map.getUiSettings().setZoomControlsEnabled(true);

        Marker mahidol = map.addMarker(new MarkerOptions().position(MAHIDOL)
                .title(getString(R.string.mahidol))
                .snippet(getString(R.string.start)));
        Marker station1 = map.addMarker(new MarkerOptions()
                .position(STATION1)
                .title(getString(R.string.PinkloaS))
                .snippet(getString(R.string.pinkloades))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_station_icon)));
        Marker phayathai = map.addMarker(new MarkerOptions().position(PHAYATHAI)
                .title(getString(R.string.phayathai))
                .snippet(getString(R.string.destination)));

        // Move the camera instantly to mahidol with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(STATION1, 15));


        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        //GET JSON DATA FROM SERVER
        new JSONParse().execute();
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

                /*
                JSONObject result = obj.getJSONObject("Owner Info");
                sb.append("Name : " + result.getString("Name") + "\n");
                sb.append("DOB : " + result.getString("DOB") + "\n");
                sb.append("Gender : " + result.getString("Gender") + "\n");
                sb.append("Website : " + obj.getString("Website") + "\n");
                JSONArray people = obj.getJSONArray("Content");
                for (int i = 0; i < people.length(); i++) {
                    JSONObject p = (JSONObject) people.get(i);
                    sb.append(i + 1 + " content : " + p.getString("content")
                            + "\n");
                    sb.append("webpage : " + p.getString("webpage") + "\n");
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
            //outputText.setText(result);
        }
    }
}
