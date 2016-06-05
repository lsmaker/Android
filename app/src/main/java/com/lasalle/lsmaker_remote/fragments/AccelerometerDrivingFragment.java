package com.lasalle.lsmaker_remote.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lasalle.lsmaker_remote.R;
import com.lasalle.lsmaker_remote.fragments.interfaces.DrivingFragment;

/**
 * Driving fragment consisting on a button.
 * Forward / backward speed controlled by accelerometer.
 * Left / right turning controlled by accelerometer.
 *
 * @author Eduard de Torres
 * @version 0.1.1
 */
public class AccelerometerDrivingFragment extends DrivingFragment implements SensorEventListener {

    private FloatingActionButton runFab;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private float x;
    private float y;
    private float z;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_accelerometer_driving, container, false);

        runFab = (FloatingActionButton) view.findViewById(R.id.run_button);
        runFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ToDo: Implement data storing on button pressed (pressed vs clicked?)
                Log.d("FAB", "Run CLICK");
            }
        });

        // Accelerometer
        senSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);

        return view;
    }

    @Override
    public int getAcceleration() {
        if (runFab.isPressed()) {
            Log.d("DRIVING", "X: " + x + " Y: " + y + " Z: " + z);
            return 0;
        }

        return 0;
    }

    @Override
    public int getTurning() {
        if (runFab.isPressed()) {
            Log.d("DRIVING", "X: " + x + " Y: " + y + " Z: " + z);
            return 0;
        }
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
}
