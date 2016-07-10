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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for communication with LsMaker's Bluetooth.
 *
 * @author Eduard de Torres
 * @version 0.1.1
 */
public class BluetoothService {
    // Constants
    private static final String TAG = "BLUETOOTH_SERVICE";

    private static final long SCAN_PERIOD = 10000; //scanning for 10 seconds
    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private static final int UART_PROFILE_READY = 10;
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int STATE_OFF = 10;


    // Own atributes
    private static String deviceName;
    private static String pincode;

    // Bluetooth atributesç
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
            Log.d(TAG, "Device found: "+ device.getName());
            addDevice(device,rssi);
        }
    };

    private static final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            final Intent mIntent = intent;
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

    public static void initialize(final Context context) {
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


    /*
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    /*private static final String[] DUMMY_CREDENTIALS = new String[]{
            "eduard:1234",
            "demo:12345"
    };*/

    public static boolean connect(String deviceId, String pincode, Context context) {
        // Dummy connection
        /*try {
            // Simulate network access.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            return false;
        }

        for (String credential : DUMMY_CREDENTIALS) {
            String[] pieces = credential.split(":");
            if (pieces[0].equals(device)) {
                // Account exists, return true if the password matches.
                return pieces[1].equals(pincode);
            }
        }*/

        Log.d(TAG, "CONNECT");
        uartService = new UartService();
        Log.d(TAG, "onServiceConnected uartService= " + uartService);
        if (!uartService.initialize(context)) {
            Log.e(TAG, "Unable to initialize Bluetooth");
        }

        // Lookup for a matching device from the scanned device's list
        for (BluetoothDevice device: deviceList) {
            // Found a match in the list
            Log.d(TAG, "Device name: "+ device.getName());
            Log.d(TAG, "Device address: "+ device.getAddress());
            if (device.getName()!= null && device.getName().equals(deviceId)) {
                String deviceAddress = device.getAddress();
                mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);

                Log.d(TAG, "... device.address==" + mDevice + "mserviceValue" + uartService);
                if (uartService.connect(deviceAddress)) {
                    BluetoothService.deviceName = deviceId;
                    BluetoothService.pincode = pincode;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    return true;
                } else {
                    return false;
                }
            }
        }

        return false;
    }

    public static boolean sendMessage(String message) {
        byte[] value;
        try {
            //send data to service
            value = message.getBytes("UTF-8");
            uartService.writeRXCharacteristic(value);
        } catch (UnsupportedEncodingException e) {
            return false;
        }
        return true;
    }

    public static boolean disconnect() {
        if (mDevice!=null)
        {
            uartService.disconnect();
        }
        return true;
    }


    public static boolean checkDeviceCompatibility(PackageManager manager, BluetoothManager bluetoothManager) {
        mHandler = new Handler();

        if (!manager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        mBluetoothAdapter = bluetoothManager.getAdapter();
        return true;
    }

    public static void enableBluetooth(Activity activityCaller) {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activityCaller.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            // Starts scanning for devices.
            startScanningDevices();
        }
    }

    public static void pauseBluetooth() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            scanLeDevice(false);
        }
    }


    public static void startScanningDevices() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            if (deviceList == null) deviceList = new ArrayList<>();
            if (devRssiValues == null) devRssiValues = new HashMap<>();
            scanLeDevice(true);
        }
    }

    private static void scanLeDevice(final boolean enable) {
        Log.d(TAG, "Scan Le Device: "+ enable);
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            /*mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);

                }
            }, SCAN_PERIOD);*/

            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

    }

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
        }
    }

    public static void service_init(Activity callerActivity) {
        initialize(callerActivity);
        Intent bindIntent = new Intent(callerActivity, UartService.class);
        callerActivity.bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(callerActivity).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    public static void service_stop(Activity callerActivity) {
        try {
            LocalBroadcastManager.getInstance(callerActivity).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
        callerActivity.unbindService(mServiceConnection);
        if (uartService != null) {
            uartService.stopSelf();
            uartService = null;
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

    public static String getBluetoothDeviceName() {
        if (mDevice == null) {
            return null;
        }
        return mDevice.getName();
    }

    public static String getBluetoothDeviceAddress() {
        if (mDevice == null) {
            return null;
        }
        return mDevice.getAddress();
    }

}
