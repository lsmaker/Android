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

import com.lasalle.lsmaker_remote.R;
import com.lasalle.lsmaker_remote.fragments.driving.interfaces.DrivingFragment;
import com.lasalle.lsmaker_remote.fragments.driving.interfaces.DrivingFragmentObserver;
import com.lasalle.lsmaker_remote.services.PreferencesService;
import com.lasalle.lsmaker_remote.services.TiltService;

//import android.util.Log;

/**
 * Driving fragment consisting on a button.
 * Forward / backward speed controlled by device tilt.
 * Left / right turning controlled by device tilt.
 *
 * @author Eduard de Torres
 * @version 1.0.0
 */
public class AccelerometerDrivingFragment extends DrivingFragment {

    //private static final String TAG = "ACCELEROMETER_DRIVING_FRAGMENT";
    private Button runFab;

    // Accelerometer data.
    private float initialSpeedAngle;
    private float currentSpeedAngle;
    private float currentTurnAngle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        if (isInverted) {
            mainView = inflater.inflate(R.layout.fragment_accelerometer_driving_inverted, container, false);
        } else {
            mainView = inflater.inflate(R.layout.fragment_accelerometer_driving, container, false);
        }

        runFab = (Button) mainView.findViewById(R.id.run_button);
        runFab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Log.d("FAB", event.toString());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //Log.d("FAB", "PRESSeD");
                        initialSpeedAngle = TiltService.getRoll();
                        break;
                    case MotionEvent.ACTION_UP:
                        //Log.d("FAB", "RELEASED");
                        break;
                }
                return false;
            }
        });

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

    /**
     * Method that returns the current speed set by the smartphone's orientation.
     *
     * The current speed can be calculated using the initial speed set when the user first pressed
     * the run button and the last speed angle gotten from the Tilt service. Maximum speed will be
     * achieved on a tilt >= 45º.
     *
     * @return the current speed set by the smartphone's orientation
     */
    private int getSpeed() {
        if (runFab.isPressed()) {
            float angle = currentSpeedAngle - initialSpeedAngle;
            if (angle > 45) angle = 45;
            if (angle < -45) angle = -45;

            return Math.round(angle * 100 / 45);
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
        if (runFab.isPressed()) {
            float angle = currentTurnAngle;
            if (angle > 45) angle = 45;
            if (angle < -45) angle = -45;

            return Math.round(angle * 100 / 45);

        }
        return 0;
    }

    /** Broadcast receiver to listen to the TiltService's changes.*/
    private BroadcastReceiver tiltReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            //*********************//
            if (action.equals(TiltService.TILT_DATA_UPDATED)) {
                currentSpeedAngle = TiltService.getRoll();
                currentTurnAngle = TiltService.getPitch();

                DrivingFragmentObserver.setSpeedAndTurn(getSpeed(), getTurn());
                //Log.d(TAG, currentSpeedAngle + " : " + currentTurnAngle);
            }

        }
    };

    /** IntentFilter to configure the broadcast receiver */
    private IntentFilter intentFilter = new IntentFilter(TiltService.TILT_DATA_UPDATED);


}
