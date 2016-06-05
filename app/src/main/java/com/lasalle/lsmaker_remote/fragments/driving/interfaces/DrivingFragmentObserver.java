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

    public int getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(int acceleration) {
        this.acceleration = acceleration;
    }

    public int getTurning() {
        return turning;
    }

    public void setTurning(int turning) {
        this.turning = turning;
    }

    public void setAccelerationAndTurning(int acceleration, int turning) {
        this.acceleration = acceleration;
        this.turning = turning;
    }

    /**
     * Returns if the Driving fragment is running (Activity onResume)
     *
     * @return true if the driving activity is running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Sets if the DrivingActivity is running and visible.
     *
     * @param running value to be set
     */
    public void setRunning(boolean running) {
        this.running = running;
    }
}
