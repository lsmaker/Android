package com.lasalle.lsmaker_remote.fragments.driving;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lasalle.lsmaker_remote.R;
import com.lasalle.lsmaker_remote.fragments.driving.interfaces.DrivingFragment;
import com.lasalle.lsmaker_remote.fragments.driving.interfaces.DrivingFragmentObserver;
import com.lasalle.lsmaker_remote.services.PreferencesService;
import com.lasalle.lsmaker_remote.utils.vertical_seekbar.VerticalSeekBar;

/**
 * Driving fragment consisting on a seekbar and two buttons.
 * Forward / backward speed controlled by buttons + seekbar.
 * Left / right turning controlled by accelerometer.
 *
 * @author Eduard de Torres
 * @version 0.1.3
 */
public class SliderDrivingFragment extends DrivingFragment implements SensorEventListener {

    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;

    private VerticalSeekBar vSeekBar;
    private Button forwardFab;
    private Button backwardFab;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    // Accelerometer data.
    private double[] data;
    private double initialYAngle;
    private double currentYAngle;

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

        return mainView;
    }

    public void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    public void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);

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

                if (forwardFab.isPressed() || backwardFab.isPressed()) {
                    DrivingFragmentObserver.setSpeed(getAcceleration());
                }
            }

            @Override
            public void onStartTrackingTouch(VerticalSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(VerticalSeekBar seekBar) {

            }
        });


        forwardFab = (Button) view.findViewById(R.id.forward_movement_button);
        forwardFab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialYAngle = getYAngle();
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
                        initialYAngle = getYAngle();
                        break;
                    case MotionEvent.ACTION_UP:
                        DrivingFragmentObserver.setSpeedAndTurn(0, 0);
                        break;
                }
                return false;
            }
        });

        // Accelerometer
        data = new double[3];
        senSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }


    public int getAcceleration() {
        if (forwardFab.isPressed()) {
            Log.d("DRIVING", "Speed = " + vSeekBar.getProgress());
            return vSeekBar.getProgress();
        }
        if (backwardFab.isPressed()) {
            Log.d("DRIVING", "Speed = " + vSeekBar.getProgress());
            return -vSeekBar.getProgress();
        }
        return 0;
    }

    public int getTurning() {
        if (forwardFab.isPressed() || backwardFab.isPressed()) {
            // TODO: Implement truly
            currentYAngle = getYAngle();
            Log.d("DRIVING", "X= " + data[X] + " Y= " + data[Y] + " Z= " + data[Z]);
            return (int) (initialYAngle - currentYAngle);
        }
        return 0;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            data[X] = event.values[X];
            data[Y] = event.values[Y];
            data[Z] = event.values[Z];

            DrivingFragmentObserver.setSpeed(getAcceleration());
            DrivingFragmentObserver.setTurn(getTurning());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public double getYAngle() {
        double yAngle;

        double g = Math.sqrt(Math.pow(data[X], 2) + Math.pow(data[Y], 2) + Math.pow(data[Z], 2));
        yAngle = Math.cos(data[X] / g);

        return yAngle;
    }

}
