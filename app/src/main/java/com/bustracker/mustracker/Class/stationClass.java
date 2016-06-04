package com.bustracker.mustracker.Class;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by kotcharat on 5/18/16.
 */
public class stationClass {
    private String route;
    private String station;
    private String day;
    private String time_leave;
    private String time_travel;



    public stationClass(String station, String day, String time_leave, String time_travel){
        this.station = station;
        this.day = day;
        this.time_leave = time_leave;
        this.time_travel = time_travel;
    }

    public String getRoute() {return  this.route; }
    public String getStation() {return this.station; }
    public String getDay() {return this.day; }
    public String getTime_leave() {return this.time_leave; }
    public String getTime_travel() {return this.time_travel; }



}
