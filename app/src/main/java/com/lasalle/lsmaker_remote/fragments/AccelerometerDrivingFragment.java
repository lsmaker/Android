package com.lasalle.lsmaker_remote.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lasalle.lsmaker_remote.R;

/**
 * Driving fragment consisting on a button.
 * Forward / backward speed controlled by accelerometer.
 * Left / right turning controlled by accelerometer.
 *
 * @author Eduard de Torres
 * @version 0.1.1
 */
public class AccelerometerDrivingFragment extends DrivingFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_accelerometer_driving, container, false);

        final FloatingActionButton runFab = (FloatingActionButton) view.findViewById(R.id.run_button);
        runFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo
                Log.d("FAB", "Run CLICK");
            }
        });

        return view;
    }

}
