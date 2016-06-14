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

/**
 * Driving fragment consisting on a button.
 * Forward / backward speed controlled by accelerometer.
 * Left / right turning controlled by accelerometer.
 *
 * @author Eduard de Torres
 * @version 0.1.3
 */
public class AccelerometerDrivingFragment extends DrivingFragment implements SensorEventListener {

    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;

    private Button runFab;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    // Accelerometer data.
    private double[] data;
    private double initialXAngle;
    private double currentXAngle;
    private double initialYAngle;
    private double currentYAngle;
    private double YAngle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_accelerometer_driving, container, false);

        runFab = (Button) view.findViewById(R.id.run_button);
        runFab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("FAB", event.toString());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("FAB", "PRESSeD");
                        initialXAngle = getXAngle();
                        initialYAngle = getYAngle();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("FAB", "RELEASED");
                        break;
                }
                return false;
            }
        });

        // Accelerometer
        data = new double[3];
        senSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);

        return view;
    }

    @Override
    public int getAcceleration() {
        if (runFab.isPressed()) {
            // TODO: Implement
            //currentXAngle = getXAngle();
            //Log.d("DRIVING", "X_ANGLE: " + currentXAngle);
            Log.d("DRIVING", "X= " + data[X] + " Y= " + data[Y] + " Z= " + data[Z]);
            return 0;
        }

        return 0;
    }

    @Override
    public int getTurning() {
        if (runFab.isPressed()) {
        // TODO: Implement
            return 0;
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

            DrivingFragmentObserver.setAcceleration(getAcceleration());
            DrivingFragmentObserver.setTurning(getTurning());
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

    private double getXAngle() {
        double xAngle;

        double g = Math.sqrt(Math.pow(data[X], 2) + Math.pow(data[Y], 2) + Math.pow(data[Z], 2));
        xAngle = Math.cos(data[Y] / g);

        //xAngle = (xAngle - 0.5) / 0.5 * 100 * (data[Z] / Math.abs(data[Z])) ;

        return xAngle;
    }

    public double getYAngle() {


        return YAngle;
    }
}
