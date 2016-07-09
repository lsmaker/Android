package com.lasalle.lsmaker_remote.services;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
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
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "BLUETOOTH_SERVICE";
    private static final long SCAN_PERIOD = 10000; //scanning for 10 seconds

    // Own atributes
    private static String deviceName;
    private static String pincode;

    // Bluetooth atributesç
    private static List<BluetoothDevice> deviceList;
    private static Map<String, Integer> devRssiValues;
    private static Handler mHandler;
    private static BluetoothDevice mDevice = null;
    private static UartService mService = null;
    private static BluetoothAdapter mBluetoothAdapter;

    // UART service connected/disconnected
    private static ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((UartService.LocalBinder) rawBinder).getService();
            Log.d(TAG, "onServiceConnected mService= " + mService);
            if (!mService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }

        }

        public void onServiceDisconnected(ComponentName classname) {
            ////     mService.disconnect(mDevice);
            mService = null;
        }
    };

    private static BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            Log.d(TAG, "Device found: "+ device.getName());
            addDevice(device,rssi);
        }
    };


    /*
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    /*private static final String[] DUMMY_CREDENTIALS = new String[]{
            "eduard:1234",
            "demo:12345"
    };*/

    public static boolean connect(String deviceId, String pincode) {
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
        mService = new UartService();
        Log.d(TAG, "onServiceConnected mService= " + mService);
        if (!mService.initialize()) {
            Log.e(TAG, "Unable to initialize Bluetooth");
        }

        // Lookup for a matching device from the scanned device's list
        for (BluetoothDevice device: deviceList) {
            // Found a match in the list
            Log.d(TAG, "Device name: "+ device.getName());
            Log.d(TAG, "Device address: "+ device.getAddress());
            if (device.getName().equals(deviceId)) {
                String deviceAddress = device.getAddress();
                mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);

                Log.d(TAG, "... device.address==" + mDevice + "mserviceValue" + mService);
                mService.connect(deviceAddress);

                BluetoothService.deviceName = deviceId;
                BluetoothService.pincode = pincode;

                return true;
            }
        }

        return false;
    }

    public boolean sendMessage(String message) {
        byte[] value;
        try {
            //send data to service
            value = message.getBytes("UTF-8");
            mService.writeRXCharacteristic(value);
        } catch (UnsupportedEncodingException e) {
            return false;
        }
        return true;
    }

    public static boolean disconnect() {
        if (mDevice!=null)
        {
            mService.disconnect();
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

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);

                }
            }, SCAN_PERIOD);

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


    /*
     * Getters and setters
     */
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        BluetoothService.deviceName = deviceName;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        BluetoothService.pincode = pincode;
    }

    public String getBluetoothDeviceName() {
        if (mDevice == null) {
            return null;
        }
        return mDevice.getName();
    }

    public String getBluetoothDeviceAddress() {
        if (mDevice == null) {
            return null;
        }
        return mDevice.getAddress();
    }

}
