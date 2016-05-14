package com.bustracker.mustracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class createEditRoute extends AppCompatActivity {

    private static String url2 = "http://bus.atilal.com/plot_routestation.php?";
    TextView outputText;
    ArrayAdapter <String> adapter_route;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_edit_route);

        Spinner sp1 = (Spinner) findViewById(R.id.spinner_route);
        adapter_route = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, MainActivity.routeEnglish);
        adapter_route.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sp1.setAdapter(adapter_route);

        new JSONParse().execute();
    }

    public void gotochooseLanguage(View v){
        startActivity(new Intent(createEditRoute.this, chooseLanguage.class));
    }

    public void gotoMainActivityFromRoute(View v){
        startActivity(new Intent(createEditRoute.this, MainActivity.class));
    }

    //JSON CLASS
    private class JSONParse extends AsyncTask<String, Void, String> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            outputText = (TextView) findViewById(R.id.outputText);

        }

        @Override
        protected String doInBackground(String... args) {
            StringBuilder sb = new StringBuilder();
            String contentRoute = MyHttpURL.getData(url2);

            try {

                //For getting route and station dropdown from phpmyadmin
                JSONObject objRoute = new JSONObject(contentRoute);
                JSONArray routeDrop = objRoute.getJSONArray("station");

                for (int i = 0; i < routeDrop.length(); i++){
                    JSONObject info = (JSONObject) routeDrop.get(i);

                    String route = info.getString("route");
                    if(!MainActivity.routeEnglish.contains(route)) MainActivity.routeEnglish.add(route);

                    String routeThai = info.getString("route_thai");
                    if(!MainActivity.routeThai.contains(routeThai)) MainActivity.routeThai.add(routeThai);

                    String stationEng = info.getString("station");
                    if(!MainActivity.stationEnglish.contains(stationEng)) MainActivity.stationEnglish.add(stationEng);

                    String stationThai = info.getString("station_thai");
                    if(!MainActivity.stationThai.contains(stationThai)) MainActivity.stationThai.add(stationThai);
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

            outputText.setText(result);
            adapter_route.notifyDataSetChanged();
        }
    }
}
