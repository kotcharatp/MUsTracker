package com.bustracker.mustracker;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class chooseDay extends Activity {
    Button button;
    ListView listView;
    ArrayAdapter<String> adapter;
    String[] days;
    String lan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_day);

        TextView setday = (TextView)findViewById(R.id.setDay);
        setday.setText(R.string.setDay);

        listView = (ListView) findViewById(R.id.list);
        button = (Button) findViewById(R.id.testbutton);

        Intent i = getIntent();
        if(i.getExtras().getString("language").equals("en")) {days = getResources().getStringArray(R.array.eng_days_array); lan = "en";}
        else {days = getResources().getStringArray(R.array.thai_days_array); lan = "th";}

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, days);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);
    }

    public void gotoCreateEditRoute(View v) {
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        ArrayList<String> selectedItems = new ArrayList<String>();
        for (int i = 0; i < checked.size(); i++) {
            // Item position in adapter
            int position = checked.keyAt(i);
            // Add sport if it is checked i.e.) == TRUE!
            if (checked.valueAt(i))
                selectedItems.add(adapter.getItem(position));
        }

        String[] outputStrArr = new String[selectedItems.size()];

        for (int i = 0; i < selectedItems.size(); i++) {
            outputStrArr[i] = selectedItems.get(i);
        }

        Intent intent = new Intent(getApplicationContext(), createEditRoute.class);

        // Create a bundle object
        Bundle b = new Bundle();
        b.putStringArray("selectedItems", outputStrArr);
        intent.putExtra("language",lan);
        // Add the bundle to the intent.
        intent.putExtras(b);

        // start the ResultActivity
        startActivity(intent);
    }
}
