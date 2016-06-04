package com.bustracker.mustracker;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.bustracker.mustracker.Database.Comment;
import com.bustracker.mustracker.Database.CommentDataSource;

import java.util.List;

public class notifyDetail extends AppCompatActivity {

    //DATABASE IN PHONE
    CommentDataSource dataSource;
    ListView list;
    ArrayAdapter<Comment> NotiArrayAdapter;
    Comment comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_detail);

        //DATABASE IN PHONE
        dataSource = new CommentDataSource(this);
        dataSource.open();
        List<Comment> values = dataSource.getAllComments();
        NotiArrayAdapter = new NotiArrayAdapter(this, android.R.layout.simple_list_item_1,values);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(NotiArrayAdapter);

        //Back button
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //custom adapter
    class NotiArrayAdapter extends ArrayAdapter<Comment> {

        Context context;
        List<Comment> objects;
        public NotiArrayAdapter(Context context, int resource, List<Comment> objects) {
            super(context, resource, objects);
            this.context = context;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Comment d = objects.get(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.lsitlayout_comment, null);

            TextView routeText = (TextView) view.findViewById(R.id.routeText);
            if(createEditRoute.checkLanguage.contains("en")){
                routeText.setText(d.getRoute());
            } else {
                for(int i=0; i< createEditRoute.routeEnglish1.size(); i++){
                    if(d.getRoute().equals(createEditRoute.routeEnglish1.get(i))){
                        routeText.setText(createEditRoute.routeThai1.get(i));
                    }
                }
            }

            TextView stationText = (TextView) view.findViewById(R.id.stationText);
            if(createEditRoute.checkLanguage.contains("en")){
                stationText.setText(d.getStation());
            } else {
                for(int i=0; i<createEditRoute.stationEngTranslate.size(); i++){
                    if(d.getStation().equals(createEditRoute.stationEngTranslate.get(i))){
                        stationText.setText(createEditRoute.stationThaiTranslate.get(i));
                    }
                }
            }

            TextView roundText = (TextView) view.findViewById(R.id.roundText);
            roundText.setText(d.getTime());

            TextView notifyText = (TextView) view.findViewById(R.id.notifydayText);
            if(createEditRoute.checkLanguage.contains("en")){
                notifyText.setText(d.getNotifyday());
            } else {
                StringBuilder sb = new StringBuilder();
                String data = d.getNotifyday();
                String[] items = data.split("\n");
                for (String item : items) {
                    for (int j = 0; j < createEditRoute.dayEng.size(); j++) {
                        if (createEditRoute.dayEng.get(j).equals(item)) {
                            sb.append(createEditRoute.dayThai.get(j) + "\n");
                        }
                    }
                }
                notifyText.setText(sb);
            }

            TextView notifyTimeText = (TextView) view.findViewById(R.id.notidyTimeText);
            notifyTimeText.setText(d.getNotifyTime());

            return view;
        }
    }
}
