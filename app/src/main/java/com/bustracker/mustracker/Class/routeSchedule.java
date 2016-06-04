package com.bustracker.mustracker.Class;

import android.content.Context;
import android.content.res.Resources;

import com.bustracker.mustracker.NavigationSetting;
import com.bustracker.mustracker.R;

import java.util.ArrayList;

/**
 * Created by kotcharat on 2/3/16.
 */
public class routeSchedule {
    //route_station database
    // NOTE: one route per object, multiple station, multiple stationTime, one time
    private String routeEng;
    private ArrayList<String> stationEng;
    //private String [] stationEng;

    private String routeThai;
    private ArrayList<String> stationThai;
    private ArrayList<String> stationTime;
    private String timeRob;
    private int busNo;

    public String getRouteEng(){return routeEng;}
    public String getRouteThai(){return routeThai;}

    //public String[] getStationEng(){ return stationEng;}
    public ArrayList<String> getStationEng(){return stationEng;}

    public ArrayList<String> getStationThai(){return stationThai;}
    public ArrayList<String> getStationTime(){return stationTime;}
    public String getTime(){return timeRob;}

    public void addStationEng(String se){
        if(this.stationEng.size() == 0){this.stationEng.add(se);}
        else{
            for(int i=0;i<=this.stationEng.size();i++){
                if(!this.stationEng.contains(se)){
                    this.stationEng.add(se);
                }
            }
        }
    }

    public void addStationTime(String se){
        if(this.stationTime.size() == 0){this.stationTime.add(se);}
        else{
            for(int i=0;i<=this.stationTime.size();i++){
                if(!this.stationTime.contains(se)){
                    this.stationTime.add(se);
                }
            }
        }
    }
    //public routeSchedule(String routeEng, String[] stationEng, ArrayList<String> stationTime, String time){
    public routeSchedule(String routeEng, ArrayList<String> stationEng, ArrayList<String> stationTime, String time){
        this.routeEng = routeEng;
        this.stationEng = stationEng;
        this.stationTime = stationTime;
        this.timeRob = time;
    }

    //---------------------------------------------------------------//

    //private long _id;
    private String route;
    private String station;
    private String driver;
    private String driverThai;
    private String time;
    private String tel;
    private int busno;

    private String busnoText;
    private String driverText;
    private String telText;


    public routeSchedule(String route, String driver, String driverThai, String time, String tel, int busno){
        this.route = route;
        this.driver = driver;
        this.driverThai = driverThai;
        this.tel = tel;
        this.time = time;
        this.busno =busno;
    }

    //public long getId() {return _id;}
    //public void setId(long id) {this._id = id; }
    public String getRoute() {return route; }
    public void setRoute(String route) { this.route = route; }
    public String getDriver() {
        String language = NavigationSetting.getContext().getResources().getConfiguration().locale.getLanguage();
        //For make driver name Thai in case the user choose thai language
        String driverLanguage = new String();

        if(language.contains("en")){
            driverLanguage = driver;
        } else{
            driverLanguage = driverThai;
        }

        return driverLanguage; }
    public void setDriver(String name) {this.driver = name; }
    public void setTime(String time) {this.time = time; }
    //public String getTime() {return time;}
    public String getTimeNormal() {return time;}
    public void setTel(String tel) {this.tel = tel; }
    public String getTel(){return tel;}
    public void setBusno(int busno) {this.busno = busno; }
    public int getBusno() {return this.busno; }

    @Override
    public String toString() {
        //return "Bus number: " + busno + "\n" +
        //        "Driver: " + driver + "\n" +
        //        "Tel: " + tel;

        // To get be able to call value from R.string.
        busnoText = NavigationSetting.getContext().getResources().getString(R.string.busno);
        driverText = NavigationSetting.getContext().getResources().getString(R.string.driver);
        telText = NavigationSetting.getContext().getResources().getString(R.string.tel);

        String driverLanguage = new String();

        String language = NavigationSetting.getContext().getResources().getConfiguration().locale.getLanguage();
        //For make driver name Thai in case the user choose thai language
        if(language.contains("en")){
            driverLanguage = driver;
        } else{
            driverLanguage = driverThai;
        }

        return busnoText +" "+ busno + "\n" +
                driverText +" "+ driverLanguage + "\n" +
                telText +" "+ tel;
    }
}