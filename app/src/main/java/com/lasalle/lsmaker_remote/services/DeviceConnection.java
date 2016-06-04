package com.lasalle.lsmaker_remote.services;

/**
 * Interface to manage connection with LsMaker.
 *
 * @author Eduard de Torres
 * @version 1.0.0
 */
public interface DeviceConnection {

    boolean connect (String device, String password);
    boolean sendMessage (String message);
    boolean disconnect ();
}
