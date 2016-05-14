package com.bustracker.mustracker;

/**
 * Created by kotcharat on 1/31/16.
 */
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.util.HashMap;
import java.util.List;

// Now is bus schedule
public class FragmentTab2_Seat extends Fragment {

    private static GoogleMap mMap;
    private static Double latitude, longitude;
    ArrayAdapter<routeSchedule> routeArrayAdapter;

    //Array list for each route
    public List<routeSchedule> dList;
    public List<routeSchedule> satopaList;
    public List<routeSchedule> satosiList;
    public List<routeSchedule> phatosaList;
    public List<routeSchedule> sitosaList;

    ArrayList<LatLng> mMarkerPoints;
    private GoogleApiClient client;
    View rootView;
    TextView outputText;

    private  static  String url = "http://bus.atilal.com/schedule.php?";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*
        View rootView = inflater.inflate(R.layout.fragment_bus, container, false);
        outputText = (TextView) rootView.findViewById(R.id.textView);

        final Spinner routeSpinner = (Spinner) rootView.findViewById(R.id.spinner_language);
        final ListView mylist = (ListView) rootView.findViewById(R.id.listView);

        //Initialize route array list, allocate memory
        dList = new ArrayList<routeSchedule>();
        satopaList = new ArrayList<routeSchedule>();
        satosiList = new ArrayList<routeSchedule>();
        phatosaList = new ArrayList<routeSchedule>();
        sitosaList = new ArrayList<routeSchedule>();

        routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (routeSpinner.getSelectedItemPosition() == 0) {
                    routeArrayAdapter = new RouteArrayAdapter(getActivity(), 0, satopaList);
                }

                if (routeSpinner.getSelectedItemPosition() == 1) {
                    routeArrayAdapter = new RouteArrayAdapter(getActivity(), 0, satopaList);

                }

                if (routeSpinner.getSelectedItemPosition() == 2) {
                    routeArrayAdapter = new RouteArrayAdapter(getActivity(), 0, satosiList);

                }
                if (routeSpinner.getSelectedItemPosition() == 3) {
                    routeArrayAdapter = new RouteArrayAdapter(getActivity(), 0, phatosaList);
                }
                if (routeSpinner.getSelectedItemPosition() == 4) {
                    routeArrayAdapter = new RouteArrayAdapter(getActivity(), 0, sitosaList);
                }
                mylist.setAdapter(routeArrayAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        latitude = 13.792686;
        longitude = 100.326425;

        setUpMapIfNeeded();

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());

        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this.getActivity(), requestCode);
            dialog.show();

        } else { // Google Play Services are available

            // Initializing
            mMarkerPoints = new ArrayList<LatLng>();

            //setUpMapIfNeeded();

            // Getting LocationManager object from System Service LOCATION_SERVICE
            //LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            //Criteria criteria = new Criteria();

            // Getting the name of the best provider
            //String provider = locationManager.getBestProvider(criteria, true);

            client = new GoogleApiClient.Builder(getActivity()).addApi(AppIndex.API).build();

            LatLng startPoint = new LatLng(13.792686, 100.326425);
            LatLng endPoint = new LatLng(13.782057, 100.417540);
            LatLng endPoint2 = new LatLng(13.764905, 100.526270);
            drawMarker(startPoint);
            drawMarker(endPoint);
            drawMarker(endPoint2);

            LatLng origin = mMarkerPoints.get(0);
            LatLng dest1 = mMarkerPoints.get(1);
            LatLng dest2 = mMarkerPoints.get(2);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest1);
            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);

            String url2 = getDirectionsUrl(dest1, dest2);
            DownloadTask downloadTask2 = new DownloadTask();
            downloadTask2.execute(url2);

        }

        //GET JSON DATA FROM SERVER
        new JSONParse().execute();
        */
        return rootView;

    }

    //JSON CLASS
    private class JSONParse extends AsyncTask<String, Void, String> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            StringBuilder sb = new StringBuilder();
            String content = MyHttpURL.getData(url);
            try {
                JSONObject obj = new JSONObject(content);
                JSONArray station = obj.getJSONArray("station");
                for (int i = 0; i < station.length(); i++) {
                    JSONObject info = (JSONObject) station.get(i);

                    String route = info.getString("route_name");
                    if (route.equals("Salaya-Phayathai")) {
                        satopaList.add(new routeSchedule(route, info.getString("driver_name"),
                                info.getString("time"), info.getString("phoneNum"),info.getInt("bus_num")));
                    } else if(route.equals("Salaya-Siriraj")) {
                        satosiList.add(new routeSchedule(route, info.getString("driver_name"),
                                info.getString("time"), info.getString("phoneNum"),info.getInt("bus_num")));
                    } else if(route.equals("Phayathai-Salaya")) {
                        phatosaList.add(new routeSchedule(route, info.getString("driver_name"),
                                info.getString("time"), info.getString("phoneNum"),info.getInt("bus_num")));
                    } else if(route.equals("Siriraj-Salaya")) {
                        sitosaList.add(new routeSchedule(route, info.getString("driver_name"),
                                info.getString("time"), info.getString("phoneNum"),info.getInt("bus_num")));
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
            pDialog.dismiss();
            outputText.setText(result);
        }
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

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            //Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /** A class to download data from Google Directions URL */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Directions in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.GREEN);
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    private void drawMarker(LatLng point) {
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
        } else if (mMarkerPoints.size() == 2) {
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_station_icon));
        } else {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        // Add new marker to the Google Map Android API V2
        mMap.addMarker(options);
    }

    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.location_map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null)
                setUpMap();
        }
    }

    private static void setUpMap() {
        // For showing a move to my loction button

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setTrafficEnabled(true);

        // For dropping a marker at a point on the Map
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                .title(MainActivity.getContext().getResources().getString(R.string.mahidol))
                .snippet(MainActivity.getContext().getResources().getString(R.string.start))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

        // For zooming automatically to the Dropped PIN Location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(13.791393, 100.349620), 13.0f));

        mMap.getUiSettings().setZoomControlsEnabled(true);

    }

    //custom adapter
    class RouteArrayAdapter extends ArrayAdapter<routeSchedule> {

        Context context;
        List<routeSchedule> objects;
        public RouteArrayAdapter(Context context, int resource, List<routeSchedule> objects) {
            super(context, resource, objects);
            this.context = context;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            routeSchedule d = objects.get(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_layout, null);

            TextView txt = (TextView) view.findViewById(R.id.language_name);
            txt.setText(d.toString());
            TextView timeText = (TextView) view.findViewById(R.id.timeText);
            timeText.setText(d.getTime());
            TextView travelText = (TextView) view.findViewById(R.id.travelText);
            travelText.setText(String.valueOf(d.getBusno()));

            return view;
        }
    }

    @Override
    public void onResume()
    {   super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (mMap != null)
            setUpMap();

        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.location_map)).getMap(); // getMap is deprecated
            // Check if we were successful in obtaining the map.
            if (mMap != null)
                setUpMap();
        }
    }

}
