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
import com.lasalle.lsmaker_remote.utils.vertical_seekbar.VerticalSeekBar;

/**
 * Driving fragment consisting on a seekbar and two buttons.
 * Forward / backward speed controlled by buttons + seekbar.
 * Left / right turning controlled by accelerometer.
 *
 * @author Eduard de Torres
 * @version 0.1.1
 */
public class SliderDrivingFragment extends DrivingFragment implements SensorEventListener {

    private VerticalSeekBar vSeekBar;
    private Button forwardFab;
    private Button backwardFab;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private float x;
    private float y;
    private float z;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_slider_driving, container, false);

        configureView(view);

        return view;
    }

    @Override
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

    @Override
    public int getTurning() {
        Log.d("DRIVING", "X: " + x + " Y: " + y + " Z: " + z);
        return 0;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];
        }

        if (forwardFab.isPressed() || backwardFab.isPressed()) {
            observer.setTurning(getTurning());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    public void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    private void configureView(View view) {
        vSeekBar = (VerticalSeekBar) view.findViewById(R.id.SeekBar01);
        vSeekBar.setMax(100);
        vSeekBar.setProgress(30);
        vSeekBar.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(VerticalSeekBar seekBar, int progress, boolean fromUser) {

                if (forwardFab.isPressed() || backwardFab.isPressed()) {
                    observer.setAcceleration(getAcceleration());
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
                        observer.setAccelerationAndTurning(getAcceleration(), getTurning());
                        break;
                    case MotionEvent.ACTION_UP:
                        observer.setAccelerationAndTurning(0, 0);
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
                        observer.setAccelerationAndTurning(getAcceleration(), getTurning());
                        break;
                    case MotionEvent.ACTION_UP:
                        observer.setAccelerationAndTurning(0, 0);
                        break;
                }
                return false;
            }
        });

        // Accelerometer
        senSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }
}
