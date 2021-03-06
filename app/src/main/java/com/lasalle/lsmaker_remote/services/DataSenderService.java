package com.lasalle.lsmaker_remote.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.lasalle.lsmaker_remote.adapters.DataSenderAdapter;
import com.lasalle.lsmaker_remote.fragments.driving.interfaces.DrivingFragmentObserver;

//import android.util.Log;

/**
 * Service that retrieve information from the current DrivingFragment and sends it to the
 * Service to communicate with the robot.
 *
 * @author Eduard de Torres
 * @version 1.0.1
 */
public class DataSenderService extends IntentService {

    private static final String TAG = DataSenderService.class.getName();
    private static final int WAIT_TIME = 100; //0.1 seconds

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
        DataSenderAdapter dataSenderAdapter = new DataSenderAdapter();
        boolean moving = false;

        while (DrivingFragmentObserver.isRunning()) {
            int speed = DrivingFragmentObserver.getSpeed();
            int turn = DrivingFragmentObserver.getTurn();
            int acceleration = 0;

            //Log.d("DATASENDER", speed + " " + turn);
            if (speed != 0 || turn != 0 || moving) {
                moving = true;
                //Log.d(TAG, speed + " " + turn);
                BluetoothService.sendMessage(
                        dataSenderAdapter.generateMovementFrame(speed, acceleration, turn));
                if (speed == 0 && turn == 0) {
                    moving = false;
                }
            }

            try {
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }

    }

}
