package com.lasalle.lsmaker_remote.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import com.lasalle.lsmaker_remote.R;
import com.lasalle.lsmaker_remote.activities.DrivingActivity;
import com.lasalle.lsmaker_remote.utils.vertical_seekbar.VerticalSeekBar;

/**
 * Driving fragment consisting on a seekbar and two buttons.
 * Forward / backward speed controlled by buttons + seekbar.
 * Left / right turning controlled by accelerometer.
 *
 * @author Eduard de Torres
 * @version 0.1.1
 */
public class SliderDrivingFragment extends DrivingFragment {

    private VerticalSeekBar vSeekBar;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_slider_driving, container, false);

        vSeekBar = (VerticalSeekBar)view.findViewById(R.id.SeekBar01);
        vSeekBar.setMax(100);
        vSeekBar.setProgress(30);
        /*vSeekBar.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(VerticalSeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(VerticalSeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(VerticalSeekBar seekBar, int progress,
                                          boolean fromUser) {

            }
        });*/

        final FloatingActionButton forwardFab = (FloatingActionButton) view.findViewById(R.id.forward_movement_button);
        forwardFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo
                Log.d("FAB", "Forward CLICK");
            }
        });

        final FloatingActionButton backwardFab = (FloatingActionButton) view.findViewById(R.id.backward_movement_button);
        backwardFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo
                Log.d("FAB", "Forward CLICK");
            }
        });

        return view;
    }

}
