package com.lasalle.lsmaker_remote.services;

/**
 * Created by Eduard on 31/05/2016.
 */
public interface DeviceConnection {

    public boolean connect (String device, String password);
    public boolean sendMessage (String message);
    public boolean disconnect ();
}
