package com.lasalle.lsmaker_remote.fragments.driving.interfaces;

import java.io.Serializable;

/**
 * Observer for DrivingFragment class.
 * Provides updated information on acceleration and turning values to be checked by DataSenderService.
 *
 * @author Eduard de Torres
 * @version 1.0.0
 */
public class DrivingFragmentObserver implements Serializable {
    private int acceleration;
    private int turning;
    private boolean running;

    private static DrivingFragmentObserver instance = null;
    private static synchronized DrivingFragmentObserver getInstance() {
        if (instance == null) {
            instance = new DrivingFragmentObserver();
        }
        return instance;
    }

    public static int getAcceleration() {
        return getInstance().acceleration;

    }

    public static void setAcceleration(int acceleration) {
        getInstance().acceleration = acceleration;
    }

    public static int getTurning() {
        return getInstance().turning;
    }

    public static void setTurning(int turning) {
        getInstance().turning = turning;
    }

    public static void setAccelerationAndTurning(int acceleration, int turning) {
        setAcceleration(acceleration);
        setTurning(turning);
    }

    /**
     * Returns if the Driving fragment is running (Activity onResume)
     *
     * @return true if the driving activity is running
     */
    public static boolean isRunning() {
        return getInstance().running;
    }

    /**
     * Sets if the DrivingActivity is running and visible.
     *
     * @param running value to be set
     */
    public static void setRunning(boolean running) {
        getInstance().running = running;
    }
}
