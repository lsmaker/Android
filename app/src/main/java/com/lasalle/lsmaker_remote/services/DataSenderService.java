package com.lasalle.lsmaker_remote.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.lasalle.lsmaker_remote.fragments.driving.interfaces.DrivingFragment;
import com.lasalle.lsmaker_remote.fragments.driving.interfaces.DrivingFragmentObserver;

/**
 * Service that retrieve information from the current DrivingFragment and sends it to the
 * DeviceConnection service to communicate with the robot.
 *
 * @author Eduard de Torres
 * @version 0.1.1
 */
public class DataSenderService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public DataSenderService() {
        super("DataSenderServiceThread");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DataSenderService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {

        while (DrivingFragmentObserver.isRunning()) {
            int acceleration = DrivingFragmentObserver.getAcceleration();
            int turning = DrivingFragmentObserver.getTurning();

            Log.d("DATASENDER", acceleration + " " + turning);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }

    }

}
