package com.lasalle.lsmaker_remote.utils.comparators;

import android.bluetooth.BluetoothDevice;

import java.util.Comparator;

/**
 * Implements a comparator for BluetoothDevice objects.
 *
 * This class is useful to enable comparisons between different BluetoothDevice.
 *
 * @author Eduard de Torres
 * @version 1.0.0
 */
public class BluetoothDeviceComparator implements Comparator<BluetoothDevice> {

    @Override
    public int compare(BluetoothDevice lhs, BluetoothDevice rhs) {
        if (lhs == null) {
            if (rhs == null) {
                // Both devices are equal as the are null references.
                return 0;
            }
            // RHS is smaller as there is no LHS object.
            return 1;
        } else {
            if (rhs == null) {
                // LHS is smaller as there is no RHS object.
                return -1;
            }

            // Neither object is null, so we compare its name.
            if (lhs.getName() == null) {
                if (rhs.getName() == null) {
                    // Both devices are equal as their names are null references.
                    return 0;
                }
                // RHS is smaller as there is no name for LHS object.
                return 1;
            } else {
                if (rhs.getName() == null) {
                    // LHS is smaller as there is no name for RHS object.
                    return -1;
                }
                // Both objects names aren't null so we proceed to compare them.
                return lhs.getName().compareTo(rhs.getName());

            }
        }
    }

    @Override
    public boolean equals(Object object) {
        return false;
    }
}
