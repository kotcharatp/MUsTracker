package com.bustracker.mustracker;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by kotcharat on 5/13/16.
 */
public class plotRoute {
    private String route;
    private LatLng position;
    private String title;
    private String snip;



    public plotRoute(String route, LatLng position, String title, String snip){
        this.route = route;
        this.position = position;
        this.title = title;
        this.snip = snip;
    }
    public String getRoute() {return  this.route; }
    public LatLng getPosition() {return this.position; }
    public String getTitle() {return this.title; }
    public String getSnip() {return this.snip; }

}