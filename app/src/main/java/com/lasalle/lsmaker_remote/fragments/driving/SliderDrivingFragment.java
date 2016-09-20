package com.lasalle.lsmaker_remote.fragments.driving;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lasalle.lsmaker_remote.R;
import com.lasalle.lsmaker_remote.fragments.driving.interfaces.DrivingFragment;
import com.lasalle.lsmaker_remote.fragments.driving.interfaces.DrivingFragmentObserver;
import com.lasalle.lsmaker_remote.services.PreferencesService;
import com.lasalle.lsmaker_remote.services.TiltService;
import com.lasalle.lsmaker_remote.utils.vertical_seekbar.VerticalSeekBar;

/**
 * Driving fragment consisting on a seekbar and two buttons.
 * Forward / backward speed controlled by buttons + seekbar.
 * Left / right turning controlled by device tilt.
 *
 * @author Eduard de Torres
 * @version 1.0.2
 */
public class SliderDrivingFragment extends DrivingFragment {

    private VerticalSeekBar vSeekBar;
    private Button forwardFab;
    private Button backwardFab;
    private TextView sliderProgressText;

    // Accelerometer data.;
    private float currentTurnAngle;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        if (isInverted) {
            mainView = inflater.inflate(R.layout.fragment_slider_driving_inverted, container, false);
        } else {
            mainView = inflater.inflate(R.layout.fragment_slider_driving, container, false);
        }

        configureView(mainView);

        // Accelerometer
        TiltService.initializeService(getActivity());

        return mainView;
    }

    public void onPause() {
        super.onPause();
        TiltService.stopService();
        getActivity().unregisterReceiver(tiltReceiver);
    }


    public void onResume() {
        super.onResume();
        TiltService.initializeService(getActivity());
        getActivity().registerReceiver(tiltReceiver, intentFilter);

        if (isInverted != PreferencesService.isInvertMode(getContext())) {
            isInverted = PreferencesService.isInvertMode(getContext());
            if (isInverted) {
                setViewLayout(R.layout.fragment_accelerometer_driving_inverted);
            } else {
                setViewLayout(R.layout.fragment_accelerometer_driving_inverted);
            }
        }

    }


    private void configureView(View view) {
        vSeekBar = (VerticalSeekBar) view.findViewById(R.id.SeekBar01);
        vSeekBar.setMax(100);
        vSeekBar.setProgress(30);
        vSeekBar.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(VerticalSeekBar seekBar, int progress, boolean fromUser) {
                String text = vSeekBar.getProgress() + " %";
                sliderProgressText.setText(text);
                //if (forwardFab.isPressed() || backwardFab.isPressed()) {
                    DrivingFragmentObserver.setSpeed(getSpeed());
                //}
            }

            @Override
            public void onStartTrackingTouch(VerticalSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(VerticalSeekBar seekBar) {

            }
        });

        sliderProgressText = (TextView) view.findViewById(R.id.driving_slider_progress_text);
        String text = vSeekBar.getProgress() + " %";
        sliderProgressText.setText(text);


        forwardFab = (Button) view.findViewById(R.id.forward_movement_button);
        forwardFab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        DrivingFragmentObserver.setSpeedAndTurn(0, 0);
                        break;
                }
                return false;
            }
        });

        backwardFab = (Button) view.findViewById(R.id.backward_movement_button);
        backwardFab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        DrivingFragmentObserver.setSpeedAndTurn(0, 0);
                        break;
                }
                return false;
            }
        });

    }

    /**
     * Method that returns the current speed set by the vertical seekbar.
     *
     * @return the current speed set by the vertical seekbar
     */
    private int getSpeed() {
        if (forwardFab.isPressed()) {
            //Log.d("DRIVING", "Speed = " + vSeekBar.getProgress());
            return vSeekBar.getProgress();
        }
        if (backwardFab.isPressed()) {
            //Log.d("DRIVING", "Speed = " + vSeekBar.getProgress());
            return -vSeekBar.getProgress();
        }
        return 0;
    }

    /**
     * Method that returns the current turn set by the smartphone's orientation.
     *
     * The current turn can be calculated using the initial turn (considered 0º or horizontal
     * position) and the last turn angle gotten from the Tilt service. Maximum turn will be
     * achieved on a tilt >= 45º.
     *
     * @return the current turn set by the smartphone's orientation
     */
    private int getTurn() {
        if (forwardFab.isPressed() || backwardFab.isPressed()) {
            float angle = currentTurnAngle;
            if (angle > 45) angle = 45;
            if (angle < -45) angle = -45;

            return Math.round(angle * 100 / 45);
        }
        return 0;
    }

    /** Broadcast receiver to listen to the TiltService's changes. */
    private BroadcastReceiver tiltReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            //*********************//
            if (action.equals(TiltService.TILT_DATA_UPDATED)) {
                currentTurnAngle = TiltService.getPitch();

                DrivingFragmentObserver.setSpeedAndTurn(getSpeed(), getTurn());
                //Log.d(TAG, currentSpeedAngle + " : " + currentTurnAngle);
            }

        }
    };

    /** IntentFilter to configure the broadcast receiver */
    private IntentFilter intentFilter = new IntentFilter(TiltService.TILT_DATA_UPDATED);


}
