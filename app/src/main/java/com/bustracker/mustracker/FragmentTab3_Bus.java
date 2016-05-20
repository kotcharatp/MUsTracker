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
public class FragmentTab3_Bus extends Fragment {

    ArrayAdapter<String> adapter_route2;

    private static GoogleMap mMap;
    ArrayAdapter<routeSchedule> routeArrayAdapter;

    //Array list for each route
    /*public List<routeSchedule> satopaList;
    public List<routeSchedule> satosiList;
    public List<routeSchedule> phatosaList;
    public List<routeSchedule> sitosaList;*/
    public List<routeSchedule> busSchedule;
    public List<routeSchedule> busList;
    public List<plotRoute> dList;

    ArrayList<LatLng> mMarkerPoints;
    private GoogleApiClient client;
    TextView outputText;

    //URL for getting data from server
    private  static  String url = "http://bus.atilal.com/schedule.php?";
    private static String url2 = "http://bus.atilal.com/plot_routestation.php?";

    ArrayList<plotRoute> routeD = new ArrayList<plotRoute>();

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_bus, container, false);
        outputText = (TextView) rootView.findViewById(R.id.textView);

        //GET JSON DATA FROM SERVER
        new JSONParse().execute();

        final Spinner routeSpinner = (Spinner) rootView.findViewById(R.id.spinner_language);

        /*ArrayList<String> res;
        String lan = getResources().getConfiguration().locale.getLanguage();
        //Change language according to the language setting
        Log.d("Language at bus", getResources().getConfiguration().locale.getLanguage());
        if(lan.equals("en")){
            res = MainActivity.routeEnglish;
        } else {
            res = MainActivity.routeThai;
        }

        adapter_route2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, res);*/
        adapter_route2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, MainActivity.routeEnglish);
        adapter_route2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        routeSpinner.setAdapter(adapter_route2);

        final ListView mylist = (ListView) rootView.findViewById(R.id.listView);

        //Initialize route array list, allocate memory
        busSchedule = new ArrayList<routeSchedule>();
        busList = new ArrayList<routeSchedule>();
        dList = new ArrayList<plotRoute>();

        routeArrayAdapter = new RouteArrayAdapter(getActivity(), 0, busList);
        routeArrayAdapter.notifyDataSetChanged();
        mylist.setAdapter(routeArrayAdapter);

        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int temp = position;

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setPositiveButton("Add to notify route", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), createEditRoute.class);
                        intent.putExtra("time", busList.get(temp).getTimeNormal());
                        intent.putExtra("ƒ", busList.get(temp).getBusno());
                        intent.putExtra("driver", busList.get(temp).getDriver());
                        intent.putExtra("phoneNum", busList.get(temp).getTel());
                        intent.putExtra("route_name", busList.get(temp).getRoute());

                        startActivity(intent);
                    }
                });
                alertDialogBuilder.setNegativeButton("View schedule details", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(getActivity(), schedule_details.class);
                        intent.putExtra("time", busList.get(temp).getTimeNormal());
                        intent.putExtra("driver", busList.get(temp).getDriver());
                        intent.putExtra("bus_num", busList.get(temp).getBusno());
                        intent.putExtra("phoneNum", busList.get(temp).getTel());
                        intent.putExtra("route_name", busList.get(temp).getRoute());

                        startActivity(intent);
                    }
                });
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setMessage("Do you want to view the bus schedule details or add notification to this route?");
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                busList.clear();
                dList.clear();

                for (int i = 0; i < MainActivity.routeEnglish.size(); i++) {
                    if (routeSpinner.getSelectedItem().toString().equals(MainActivity.routeEnglish.get(i)) || routeSpinner.getSelectedItem().toString().equals(MainActivity.routeThai.get(i))) {
                        for (int j = 0; j < routeD.size(); j++) {
                            if (routeD.get(j).getRoute().equals(routeSpinner.getSelectedItem().toString())) {
                                dList.add(routeD.get(j));
                            }
                        }

                        for (int k = 0; k < busSchedule.size(); k++) {
                            if (busSchedule.get(k).getRoute().equals(routeSpinner.getSelectedItem().toString())) {
                                busList.add(busSchedule.get(k));
                            }
                        }
                        /*
                        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                for (int k = 0; k < busList.size(); k++) {
                                    startActivity(new Intent(getActivity(), schedule_details.class));
                                    Intent intent = new Intent(getActivity(), schedule_details.class);
                                    intent.putExtra("time", busSchedule.get(k).getTime());
                                    intent.putExtra("bus_num", busSchedule.get(k).getBusno());
                                    intent.putExtra("phoneNum", busSchedule.get(k).getTel());
                                    intent.putExtra("route_name", busSchedule.get(k).getRoute());
                                }
                            }
                        });*/

                        mMap.clear();
                        setUpMap();
                        plotRouteStation(dList);
                        routeArrayAdapter = new RouteArrayAdapter(getActivity(), 0, busList);
                        routeArrayAdapter.notifyDataSetChanged();

                    }
                    dList.clear();
                }
                mylist.setAdapter(routeArrayAdapter);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

/*
        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int temp = position;

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setPositiveButton("Add to notify route", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), createEditRoute.class);
                        intent.putExtra("time", busList.get(temp).getTime());
                        intent.putExtra("bus_num", busList.get(temp).getBusno());
                        intent.putExtra("driver", busList.get(temp).getDriver());
                        intent.putExtra("phoneNum", busList.get(temp).getTel());
                        intent.putExtra("route_name", busList.get(temp).getRoute());
                        startActivity(intent);
                    }
                });
                alertDialogBuilder.setNegativeButton("View schedule details", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(getActivity(), schedule_details.class);
                        intent.putExtra("time", busList.get(temp).getTime());
                        intent.putExtra("driver", busList.get(temp).getDriver());
                        intent.putExtra("bus_num", busList.get(temp).getBusno());
                        intent.putExtra("phoneNum", busList.get(temp).getTel());

                        intent.putExtra("route_name", busList.get(temp).getRoute());
                        startActivity(intent);
                    }
                });
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setMessage("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. " +
                        "Aenean commodo ligula eget dolor. Aenean massa. ");
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });*/

        return rootView;
    }

    private void plotRouteStation(List<plotRoute> data){
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());

        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this.getActivity(), requestCode);
            dialog.show();

        } else { // Google Play Services are available
            // Initializing
            mMarkerPoints = new ArrayList<LatLng>();

            client = new GoogleApiClient.Builder(getActivity()).addApi(AppIndex.API).build();

            for(int i=0; i<data.size(); i++){

                drawMarker(data.get(i).getPosition(), data.get(i).getTitle(), data.get(i).getSnip());

                if(i!=data.size()-1) {
                    LatLng startPoint = data.get(i).getPosition();
                    LatLng endPoint = data.get(i + 1).getPosition();
                    String url = getDirectionsUrl(startPoint, endPoint);
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url);
                }

            }

        }
    }

    //JSON CLASS
    private class JSONParse extends AsyncTask<String, Void, String> {
        private ProgressDialog p;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            p = new ProgressDialog(getActivity());
            p.setMessage(getString(R.string.loading));
            p.setIndeterminate(false);
            p.setCancelable(true);
            p.show();
        }

        @Override
        protected String doInBackground(String... args) {
            StringBuilder sb = new StringBuilder();
            String content = MyHttpURL.getData(url);
            String contentRoute = MyHttpURL.getData(url2);

            try {
                JSONObject obj = new JSONObject(content);
                JSONArray station = obj.getJSONArray("station");

                for (int i = 0; i < station.length(); i++) {
                    JSONObject info = (JSONObject) station.get(i);

                    String routeS = info.getString("route_name");
                    String driver = info.getString("driver_name");
                    String driver_thai = info.getString("driver_Thai");
                    String phone = info.getString("phoneNum");
                    String time = info.getString("time");
                    int busnum = info.getInt("bus_num");

                    busSchedule.add(new routeSchedule(routeS, driver, driver_thai, time, phone, busnum));

                    /*

                    if (routeS.equals("Salaya-Phayathai")) {
                        satopaList.add(new routeSchedule(routeS, info.getString("driver_name"),
                                info.getString("time"), info.getString("phoneNum"),info.getInt("bus_num")));
                    } else if(routeS.equals("Salaya-Siriraj")) {
                        satosiList.add(new routeSchedule(routeS, info.getString("driver_name"),
                                info.getString("time"), info.getString("phoneNum"),info.getInt("bus_num")));
                    } else if(routeS.equals("Phayathai-Salaya")) {
                        phatosaList.add(new routeSchedule(routeS, info.getString("driver_name"),
                                info.getString("time"), info.getString("phoneNum"),info.getInt("bus_num")));
                    } else if(routeS.equals("Siriraj-Salaya")) {
                        sitosaList.add(new routeSchedule(routeS, info.getString("driver_name"),
                                info.getString("time"), info.getString("phoneNum"),info.getInt("bus_num")));
                    }*/
                }

                //For getting route and station lat long from phpmyadmin
                JSONObject objRoute = new JSONObject(contentRoute);
                JSONArray routeDrop = objRoute.getJSONArray("station");

                for (int i = 0; i < routeDrop.length(); i++){
                    JSONObject info = (JSONObject) routeDrop.get(i);

                    String route = info.getString("route");
                    LatLng point = new LatLng(info.getDouble("latitude"), info.getDouble("longitude"));
                    String title = info.getString("station");
                    String snip = info.getString("station_thai");

                    MainActivity.plotData.add(new plotRoute(route, point, title, snip));
                    routeD.add(new plotRoute(route, point, title, snip));
                }

                return sb.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            p.dismiss();
            outputText.setText(result);
            adapter_route2.notifyDataSetChanged();
            routeArrayAdapter.notifyDataSetChanged();

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

    private void setUpMap() {
        // For showing a move to my loction button

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setTrafficEnabled(true);

        // For dropping a marker at a point on the Map
        /*
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                .title(MainActivity.getContext().getResources().getString(R.string.mahidol))
                .snippet(MainActivity.getContext().getResources().getString(R.string.start))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        */

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

            TextView txt = (TextView) view.findViewById(R.id.arriveText);
            txt.setText(d.toString());
            TextView timeText = (TextView) view.findViewById(R.id.stationText);
            timeText.setText(d.getTimeNormal());
            TextView travelText = (TextView) view.findViewById(R.id.travelText);
            travelText.setText(String.valueOf(d.getBusno()));

            return view;
        }
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
