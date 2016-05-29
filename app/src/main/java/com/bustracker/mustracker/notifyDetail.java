package com.bustracker.mustracker;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
            View view = inflater.inflate(R.layout.comment_list_layout, null);

            TextView routeText = (TextView) view.findViewById(R.id.routeText);
            routeText.setText(d.getRoute());

            TextView stationText = (TextView) view.findViewById(R.id.stationText);
            stationText.setText(d.getStation());

            TextView roundText = (TextView) view.findViewById(R.id.roundText);
            roundText.setText(d.getTime());

            TextView notifyText = (TextView) view.findViewById(R.id.notifydayText);
            notifyText.setText(d.getNotifyday());

            TextView timeText = (TextView) view.findViewById(R.id.notifyTimeText);
            timeText.setText(d.getNotifyTime());

            return view;
        }
    }
}
