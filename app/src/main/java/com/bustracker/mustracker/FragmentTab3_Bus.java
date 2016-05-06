package com.bustracker.mustracker;

/**
 * Created by kotcharat on 1/31/16.
 */
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;


public class FragmentTab3_Bus extends Fragment {

    private static GoogleMap mMap;
    private static Double latitude, longitude;
    ArrayAdapter<routeSchedule> routeArrayAdapter;
    public List<routeSchedule> dList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bus, container, false);

        final Spinner routeSpinner = (Spinner)rootView.findViewById(R.id.spinner);
        final ListView mylist = (ListView) rootView.findViewById(R.id.listView);
        dList = new ArrayList<routeSchedule>();
        routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (routeSpinner.getSelectedItemPosition() == 0) {
                    dList.clear();
                    dList.add(new routeSchedule("Salaya-Phayathai", "Prakorb", "5:30", "081-111-1111", 1));
                    dList.add(new routeSchedule("Salaya-Phayathai", "Somchai", "7:00", "082-122-1212", 2));
                    dList.add(new routeSchedule("Salaya-Phayathai", "Somsri", "7:30", "091-422-1212", 3));
                    dList.add(new routeSchedule("Salaya-Phayathai", "Somruk", "8:00", "082-122-1212", 2));
                    dList.add(new routeSchedule("Salaya-Phayathai", "Somjai", "9:00", "082-122-1212", 2));

                } if (routeSpinner.getSelectedItemPosition() == 1) {
                    dList.clear();
                    dList.add(new routeSchedule("Salaya-Siriraj", "Somsuk", "6:00", "099-155-1441", 1));
                } if (routeSpinner.getSelectedItemPosition() == 2) {
                    dList.clear();

                }
                if (routeSpinner.getSelectedItemPosition() == 3) {
                    dList.clear();

                }
                routeArrayAdapter = new RouteArrayAdapter(getActivity(), 0, dList);
                mylist.setAdapter(routeArrayAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        latitude = 13.792686;
        longitude = 100.326425;

        setUpMapIfNeeded();

        return rootView;
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
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Mahidol University").snippet("Main Station"));
        // For zooming automatically to the Dropped PIN Location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(13.791393, 100.349620), 13.0f));

        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.addPolyline(
                new PolylineOptions().add(new LatLng(13.788112, 100.327534)).add(new LatLng(latitude, longitude)).color(Color.BLUE));
        mMap.addPolyline(
                new PolylineOptions().add(new LatLng(13.788642, 100.356099)).add(new LatLng(13.788112, 100.327534)).color(Color.BLUE));
        mMap.addPolyline(
                new PolylineOptions().add(new LatLng(13.780407, 100.442659)).add(new LatLng(13.788642, 100.356099)).color(Color.BLUE));
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

            TextView txt = (TextView) view.findViewById(R.id.titleText);
            txt.setText(d.toString());
            TextView timeText = (TextView) view.findViewById(R.id.timeText);
            timeText.setText(d.getTime());

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
