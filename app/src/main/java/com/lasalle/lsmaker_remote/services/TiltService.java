package com.lasalle.lsmaker_remote.services;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Singleton service to manage device's tilt status.
 *
 * @author Eduard de Torres
 * @version 1.0.0
 */
public class TiltService implements SensorEventListener {

    // Class constants
    private static final float RAD_TO_DEG = 57.2957795f;
    public static final String TILT_DATA_UPDATED = "com.lasalle.lsmaker_remote.TILT_DATA_UPDATED";
    private static final String TAG = "TILT_SERVICE";

    // Singleton's attributes
    private static TiltService instance = null;

    // Context's attributes
    private Context context;

    // Sensor's attributes
    private SensorManager m_sensorManager;

    private float []m_lastMagFields;
    private float []m_lastAccels;

    private float[] m_rotationMatrix = new float[16];
    private float[] m_orientation = new float[4];

    private float m_lastPitch = 0.f;
    private float m_lastYaw = 0.f;
    private float m_lastRoll = 0.f;

    protected TiltService() {
    }

    private static TiltService getInstance() {
        if (instance == null) {
            instance = new TiltService();
        }
        return instance;
    }

    /**
     * Initializes the service.
     *
     * @param context current activity's context
     */
    public static void initializeService(Context context) {
        getInstance().context = context;
        getInstance().initializeService();
    }

    private void initializeService() {
        m_sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        registerListeners();
    }

    private void registerListeners() {
        m_sensorManager.registerListener(this,
                m_sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                                                 SensorManager.SENSOR_DELAY_GAME);
        m_sensorManager.registerListener(this,
                m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                                                 SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     * Stops the service.
     */
    public static void stopService() {
        getInstance().unregisterListeners();
        getInstance().context = null;
    }

    private void unregisterListeners() {
        m_sensorManager.unregisterListener(this);
    }


    /**
     * Returns the rotation angle around y axis.
     * @return angle of rotation around y axis.
     */
    public static float getRoll() {
        return getInstance().m_lastRoll;
    }

    /**
     * Returns the rotation angle around x axis.
     * @return angle of rotation around x axis.
     */
    public static float getPitch() {
        return getInstance().m_lastPitch;
    }

    /**
     * Returns the rotation angle around z axis.
     * @return angle of rotation around z axis.
     */
    public static float getYaw() {
        return getInstance().m_lastYaw;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            manageAccelerometerEvent(event);
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            manageMagneticFieldEvent(event);
        }
    }


    private void manageAccelerometerEvent(SensorEvent event) {
        if (m_lastAccels == null) {
            m_lastAccels = new float[3];
        }

        System.arraycopy(event.values, 0, m_lastAccels, 0, 3);
    }

    private void manageMagneticFieldEvent(SensorEvent event) {
        if (m_lastMagFields == null) {
            m_lastMagFields = new float[3];
        }

        System.arraycopy(event.values, 0, m_lastMagFields, 0, 3);

        if (m_lastAccels != null) {
            computeOrientation();
        }
    }

    Filter [] m_filters = { new Filter(), new Filter(), new Filter() };

    private class Filter {
        static final int AVERAGE_BUFFER = 10;
        float []m_arr = new float[AVERAGE_BUFFER];
        int m_idx = 0;

        public float append(float val) {
            m_arr[m_idx] = val;
            m_idx++;
            if (m_idx == AVERAGE_BUFFER)
                m_idx = 0;
            return avg();
        }

        public float avg() {
            float sum = 0;
            for (float x: m_arr)
                sum += x;
            return sum / AVERAGE_BUFFER;
        }

    }

    private void computeOrientation() {
        if (SensorManager.getRotationMatrix(m_rotationMatrix, null, m_lastAccels, m_lastMagFields)) {
            SensorManager.getOrientation(m_rotationMatrix, m_orientation);

            /* 1 radian = 57.2957795 degrees */
            /* [0] : yaw, rotation around z axis
             * [1] : pitch, rotation around x axis
             * [2] : roll, rotation around y axis */
            float yaw = m_orientation[0] * RAD_TO_DEG;
            float pitch = m_orientation[1] * RAD_TO_DEG;
            float roll = m_orientation[2] * RAD_TO_DEG;

            m_lastYaw = m_filters[0].append(yaw);
            m_lastPitch = m_filters[1].append(pitch);
            m_lastRoll = m_filters[2].append(roll);

            //Log.d(TAG, String.format("Roll: %1$.2f Pitch: %2$.2f", m_lastRoll, m_lastPitch));

            Intent intent = new Intent(TILT_DATA_UPDATED);
            context.sendBroadcast(intent);
        }
    }
}
