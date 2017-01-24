package com.lasalle.lsmaker_remote.services;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.BaseAdapter;

import com.lasalle.lsmaker_remote.utils.Utils;
import com.lasalle.lsmaker_remote.utils.comparators.BluetoothDeviceComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for communication with LsMaker's Bluetooth.
 *
 * This service manages the scanning/discovery of bluetooth devices and
 *
 * @author Eduard de Torres
 * @version 1.0.2
 */
public class BluetoothService {
    // Constants
    private static final String TAG = "BLUETOOTH_SERVICE";

    /**
     * Constant value for the broadcast receivers to identify that a scan process has finished.
     */
    public final static String SCAN_STOPPED = "com.lasalle.lsmaker_remote.ACTION_SCAN_STOPPED";

    private static final long SCAN_PERIOD = 5000; //scanning for 5 seconds

    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;

    // Own attributes
    private static BaseAdapter deviceAdapter;
    private static boolean serviceStarted = false;
    private static Activity binderActivity = null;

    // Bluetooth attributes
    private static List<BluetoothDevice> deviceList;
    private static Map<String, Integer> devRssiValues;
    private static BluetoothDevice mDevice = null;
    private static UartService uartService = null;
    private static Handler mHandler;
    private static BluetoothAdapter mBluetoothAdapter = null;
    private static int mState = UART_PROFILE_DISCONNECTED;

    // UART service connected/disconnected
    private static ServiceConnection mServiceConnection;

    private static final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            //Log.d(TAG, "Device found: "+ device.getName());
            addDevice(device,rssi);
        }
    };

    private static final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            //final Intent mIntent = intent;
            //*********************//
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                mState = UART_PROFILE_CONNECTED;
            }

            //*********************//
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                mState = UART_PROFILE_DISCONNECTED;
                uartService.close();
            }


            //*********************//
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                uartService.enableTXNotification();
            }
            //*********************//
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
                // Do what you need to do with the data received.
            }
            //*********************//
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)){
                uartService.disconnect();
            }
        }
    };

    /**
     * Method that initializes the service.
     *
     * This method mus be called first before using any other functionality, otherwise correct
     * execution can not be assured.
     *
     * @param context context that first calls the service. This context must not be destroyed
     *                while the service is working.
     */
    private static void initialize(final Context context) {
        mServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder rawBinder) {
                uartService = ((UartService.LocalBinder) rawBinder).getService();
                Log.d(TAG, "onServiceConnected uartService= " + uartService);
                if (!uartService.initialize(context)) {
                    Log.e(TAG, "Unable to initialize Bluetooth");
                }
            }

            public void onServiceDisconnected(ComponentName classname) {
                ////     uartService.disconnect(mDevice);
                uartService = null;
            }
        };
    }


    /**
     * Method that binds the smartphone to a bluetooth device.
     *
     * @param device the bluetooth device selected from the scanned list
     * @param context the activity's context to use to initialize the binding.
     * @return true if the binding was successfully achieved. False otherwise.
     */
    public static Boolean connect(BluetoothDevice device, Context context) {
        Log.d(TAG, "CONNECT");
        uartService = new UartService();
        Log.d(TAG, "onServiceConnected uartService= " + uartService);
        if (!uartService.initialize(context)) {
            Log.e(TAG, "Unable to initialize Bluetooth");
        }

        String deviceAddress = device.getAddress();
        mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);

        Log.d(TAG, "... device.address==" + mDevice + "mserviceValue" + uartService);
        if (uartService.connect(deviceAddress)) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method that sends a message to the currently binded bluetooth device.
     *
     * @param message the message to be send. Be sure to follow the API message format.
     * @return true if the message was send successfully. False otherwise.
     */
    public static boolean sendMessage(byte[] message) {
        //send data to service
        Log.d(TAG, Utils.bytesToHex(message));
        uartService.writeRXCharacteristic(message);

        return true;
    }

    /**
     * Method that disconnects the smartphone from the currently binded bluetooth device.
     *
     * @return true if the unbinding was successful. False otherwise.
     */
    public static boolean disconnect() {
        if (mDevice!=null)
        {
            uartService.disconnect();
        }
        return true;
    }


    /**
     * Method that checks if the current smartphone is compatible with the Bluetooth Low Energy (BLE)
     * characteristics.
     *
     * @param manager the activity's PackageManager
     * @param bluetoothManager the activity's BluetoothManager
     * @return true if the smartphone is compatible. False otherwise.
     */
    public static boolean checkDeviceCompatibility(PackageManager manager, BluetoothManager bluetoothManager) {
        mHandler = new Handler();

        if (!manager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        mBluetoothAdapter = bluetoothManager.getAdapter();
        return true;
    }

    /**
     * Method that checks if the bluetooth is enabled.
     *
     * If bluetooth isn't enabled, the method will ask the user permission to enable it.
     * If bluetooth is enabled or permission to enable it is given, then the service starts scanning
     * for devices.
     *
     * @param activityCaller activity that calls the method to let it call Activity's functionalities.
     */
    public static void enableBluetooth(Activity activityCaller, int resultCode) {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activityCaller.startActivityForResult(enableBtIntent, resultCode);
        } else {
            // Starts scanning for devices.
            startScanningDevices();
        }
    }

    /**
     * Method that stops a current scanning process.
     */
    public static void pauseBluetooth() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            scanLeDevice(false);
        }
    }

    /**
     * Method that starts scanning for bluetooth devices inside effective radius.
     */
    public static void startScanningDevices() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            if (deviceList == null) deviceList = new ArrayList<>();
            if (devRssiValues == null) devRssiValues = new HashMap<>();
            scanLeDevice(true);
        }
    }

    /**
     * Method that enables or disables device's scanning.
     *
     * A scanning process wil have a duration of {@value #SCAN_PERIOD} milliseconds.
     *
     * @param enable true to start a scanning process, false to stop it.
     */
    private static void scanLeDevice(final boolean enable) {
        //Log.d(TAG, "Scan Le Device: "+ enable);
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    Intent intent = new Intent(SCAN_STOPPED);
                    binderActivity.sendBroadcast(intent);
                }
            }, SCAN_PERIOD);

            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            Intent intent = new Intent(SCAN_STOPPED);
            binderActivity.sendBroadcast(intent);
        }

    }

    /**
     * Method that adds a device to the current device list.
     *
     * @param device discovered device
     * @param rssi rssi value of discovered device
     */
    private static void addDevice(BluetoothDevice device, int rssi) {
        boolean deviceFound = false;

        for (BluetoothDevice listDev : deviceList) {
            if (listDev.getAddress().equals(device.getAddress())) {
                deviceFound = true;
                break;
            }
        }

        devRssiValues.put(device.getAddress(), rssi);
        if (!deviceFound) {
            deviceList.add(device);
            Collections.sort(deviceList, new BluetoothDeviceComparator());
            devRssiValues.put(device.getAddress(), rssi);
            if (deviceAdapter != null) {
                deviceAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Method that initializes the service.
     *
     * This method mus be called first before using any other functionality, otherwise correct
     * execution can not be assured.
     *
     * @param binderActivity activity that first calls the service. This activity must not be
     *                       destroye while the service is working.
     */
    public static void service_init(Activity binderActivity) {
        BluetoothService.binderActivity = binderActivity;
        initialize(binderActivity);
        Intent bindIntent = new Intent(binderActivity, UartService.class);
        binderActivity.bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(binderActivity).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
        serviceStarted = true;
    }

    /**
     * Method that stops the service.
     */
    public static void service_stop() {
        if (serviceStarted) {
            try {
                LocalBroadcastManager.getInstance(binderActivity).unregisterReceiver(UARTStatusChangeReceiver);
            } catch (Exception ignore) {
                Log.e(TAG, ignore.toString());
            }

            binderActivity.unbindService(mServiceConnection);
            if (uartService != null) {
                uartService.stopSelf();
                uartService = null;
            }
            serviceStarted = false;
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }



    /*
     * Getters and setters
     */

    /**
     * Method that returns the name of the current binded bluetooth device.
     *
     * @return the name of the current binded device. May be null if the device doesn't have a name.
     */
    public static String getBluetoothDeviceName() {
        if (mDevice == null) {
            return null;
        }
        return mDevice.getName();
    }

    /**
     * Method that returns the physical address of the current binded bluetooth device.
     *
     * @return the physical address of the current binded device. May not be unique, as the
     * manufacturer sets its value of factory (2^48 combinations)
     */
    public static String getBluetoothDeviceAddress() {
        if (mDevice == null) {
            return null;
        }
        return mDevice.getAddress();
    }

    /**
     * Method that returns a list of all current discovered bluetooth devices.
     * @return a list of all current discovered devices
     */
    public static List<BluetoothDevice> getDeviceList() {
        return deviceList;
    }

    /**
     * Method that returns a {@link Map} containing a list of all the current discovered devices'
     * rssi values. The {@link Map} contains a list of pairs Address / RSSI value.
     *
     * @return a list of rssi values from all the discovered devices
     */
    public static Map<String, Integer> getDevRssiValues() {
        return BluetoothService.devRssiValues;
    }

    /**
     * Method that configures the adapter that will manage the device's list to let the service
     * notify any change on its members.
     *
     * @param deviceAdapter an adapter to manage the device's list
     */
    public static void setDeviceAdapter(BaseAdapter deviceAdapter) {
        BluetoothService.deviceAdapter = deviceAdapter;
    }

}
