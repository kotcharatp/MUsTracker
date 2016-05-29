package com.bustracker.mustracker.Database;

import java.util.List;

/**
 * Created by Cat on 22-May-16.
 */
public class Comment {
    private long id;
    private String comment;

    /*Class comment ควรมี
    1. Route
    2. Station
    3. Time รอบ
    4. Notify day
    5. Time notify*/

    private String route;
    private String station;
    private String time;
    private String notifyday;
    private String notifyTime;


    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    public String getRoute() {return route;}
    public void setRoute(String route) {this.route = route; }
    public String getStation() {return station; }
    public void setStation(String station) {this.station = station; }
    public String getTime() {return time; }
    public void setTime(String time) {this.time = time; }
    public String getNotifyday() {return notifyday; }
    public void setNotifyday(String notifyday) {this.notifyday = notifyday; }
    public String getNotifyTime() {return notifyTime; }
    public void setNotifyTime(String notifyTime) {this.notifyTime = notifyTime; }


    public String getComment() {/*(sp_route.getSelectedItem().toString() + "(" + outputText.getText().toString() + ")" + "\n"
                    + sp_station.getSelectedItem().toString() + "\n"
                     + days + "\n"
                     + h + ":"+ m);*/

        return route + "(" + time + ")" + "\n" +
                station + "\n" +
                notifyday + "\n" +
                notifyTime;
    }
    public void setComment(String comment) {this.comment = comment;}


    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return route + "(" + time + ")" + "\n" +
                station + "\n" +
                notifyday + "\n" +
                notifyTime;
    }
}

//must create an object [id and comment]
