package com.lasalle.lsmaker_remote.fragments.driving.interfaces;


import android.util.Log;

/**
 * Observer for DrivingFragment class.
 * Provides updated information on speed and turn values to be checked by DataSenderService.
 *
 * @author Eduard de Torres
 * @version 1.1.1
 */
public class DrivingFragmentObserver {
    private int speed;
    private int turn;
    private boolean running;

    // Singleton synchronized implementation.
    private static DrivingFragmentObserver instance = null;
    private static synchronized DrivingFragmentObserver getInstance() {
        if (instance == null) {
            instance = new DrivingFragmentObserver();
        }
        return instance;
    }

    public static int getSpeed() {
        return getInstance().speed;
    }

    public static void setSpeed(int speed) {
        getInstance().speed = speed;
    }

    public static int getTurn() {
        return getInstance().turn;
    }

    public static void setTurn(int turn) {
        getInstance().turn = turn;
    }

    public static void setSpeedAndTurn(int speed, int turn) {
        setSpeed(speed);
        setTurn(turn);
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
