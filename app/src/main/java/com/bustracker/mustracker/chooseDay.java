package com.bustracker.mustracker;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_day);

        TextView setday = (TextView)findViewById(R.id.setDay);
        setday.setText(R.string.setDay);

        listView = (ListView) findViewById(R.id.list);
        button = (Button) findViewById(R.id.testbutton);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, getResources().getStringArray(R.array.eng_days_array));
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
            String dayLanguage = selectedItems.get(i);
            if(createEditRoute.checkLanguage.equals("en")){
                outputStrArr[i] = dayLanguage;
            } else {
                for(int j=0; j<createEditRoute.dayEng.size(); j++){
                    if(createEditRoute.dayThai.get(j).equals(dayLanguage)){
                        outputStrArr[i] = createEditRoute.dayEng.get(j);
                    }
                }
            }
            //outputStrArr[i] = selectedItems.get(i);
        }

        Intent intent = new Intent(getApplicationContext(), createEditRoute.class);

        //Get selected item
        int temp1 = 0;
        int temp2 = 0;
        Intent i = getIntent();
        temp1 = i.getIntExtra("chosenRoute", 0);
        temp2 = i.getIntExtra("chosenStation", 0);

        // Create a bundle object
        Bundle b = new Bundle();
        b.putStringArray("selectedItems", outputStrArr);
        // Add the bundle to the intent.
        intent.putExtras(b);

        intent.putExtra("chosenRoute", temp1);
        intent.putExtra("chosenStation", temp2);

        // start the ResultActivity
        startActivity(intent);
    }
}
