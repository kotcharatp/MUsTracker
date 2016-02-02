package com.bustracker.mustracker;

/**
 * Created by kotcharat on 1/31/16.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

public class FragmentTab3_Bus extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment3_bus, container, false);

        Spinner route = (Spinner)rootView.findViewById(R.id.spinner);



        return rootView;
    }

}
