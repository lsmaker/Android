package com.lasalle.lsmaker_remote.services.impl;

import com.lasalle.lsmaker_remote.services.DeviceConnection;

/**
 * Created by Eduard on 31/05/2016.
 */
public class BluetoothConnection implements DeviceConnection {
    private String deviceName;
    private String pincode;

    /*
     * Singelton implementation
     */
    private static BluetoothConnection ourInstance = new BluetoothConnection();

    private BluetoothConnection() {
    }

    public static BluetoothConnection getInstance() {
        return ourInstance;
    }

    /*
     * DeviceConnection implementation.
     */

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "eduard:1234", "demo:12345"
    };

    @Override
    public boolean connect(String device, String password) {
        // TODO: attempt authentication against a network service.
        try {
            // Simulate network access.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            return false;
        }

        for (String credential : DUMMY_CREDENTIALS) {
            String[] pieces = credential.split(":");
            if (pieces[0].equals(deviceName)) {
                // Account exists, return true if the password matches.
                return pieces[1].equals(pincode);
            }
        }

        ourInstance.deviceName = device;
        ourInstance.pincode = password;

        return true;
    }

    @Override
    public boolean sendMessage(String message) {
        return false;
    }

    @Override
    public boolean disconnect() {
        return false;
    }

    /*
     * Getters and setters
     */
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
}
