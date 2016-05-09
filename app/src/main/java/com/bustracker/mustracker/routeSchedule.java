package com.bustracker.mustracker;

import android.content.Context;
import android.content.res.Resources;
import com.bustracker.mustracker.R;

/**
 * Created by kotcharat on 2/3/16.
 */
public class routeSchedule {
    //private long _id;
    private String route;
    private String driver;
    private String time;
    private String tel;
    private int busno;

    private String busnoText;
    private String driverText;
    private String telText;


    public routeSchedule(String route, String driver, String time, String tel, int busno){
        this.route = route;
        this.driver = driver;
        this.tel = tel;
        this.time = time;
        this.busno =busno;
    }


    //public long getId() {return _id;}
    //public void setId(long id) {this._id = id; }
    public String getRoute() {return route; }
    public void setRoute(String route) { this.route = route; }
    public String getDriver() {return driver; }
    public void setDriver(String name) {this.driver = name; }
    public void setTime(String time) {this.time = time; }
    public String getTime() {return time;}
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
        busnoText = MainActivity.getContext().getResources().getString(R.string.busno);
        driverText = MainActivity.getContext().getResources().getString(R.string.driver);
        telText = MainActivity.getContext().getResources().getString(R.string.tel);

        return busnoText +" "+ busno + "\n" +
                driverText +" "+ driver + "\n" +
               telText +" "+ tel;
    }

}
