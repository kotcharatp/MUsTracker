package com.bustracker.mustracker;

/**
 * Created by kotcharat on 1/31/16.
 */
import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;


public class FragmentTab3_Bus extends Fragment {

    private static GoogleMap mMap;
    private static Double latitude, longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bus, container, false);

        Spinner route = (Spinner)rootView.findViewById(R.id.spinner);

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

    // Server = AIzaSyBumsm3eWV31p1xqlt5yona3yeVaw9XjHc

    private static void setUpMap() {
        // For showing a move to my loction button
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setTrafficEnabled(true);

        // For dropping a marker at a point on the Map
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Mahidol University").snippet("Main Station"));
        // For zooming automatically to the Dropped PIN Location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,
                longitude), 13.0f));

        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.addPolyline(
                new PolylineOptions().add(new LatLng(13.788112, 100.327534)).add(new LatLng(latitude, longitude)).color(Color.BLUE));
        mMap.addPolyline(
                new PolylineOptions().add(new LatLng(13.788642, 100.356099)).add(new LatLng(13.788112, 100.327534)).color(Color.BLUE));
        mMap.addPolyline(
                new PolylineOptions().add(new LatLng(13.780407, 100.442659)).add(new LatLng(13.788642, 100.356099)).color(Color.BLUE));
    }

    @Override
    public void onResume()
    {
        super.onResume();
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
