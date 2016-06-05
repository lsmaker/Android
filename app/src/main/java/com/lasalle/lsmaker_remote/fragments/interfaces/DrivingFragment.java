package com.lasalle.lsmaker_remote.fragments.interfaces;

import android.support.v4.app.Fragment;

/**
 * Abstract base fragment to work with DrivingActivity.
 *
 * @author Eduard de Torres
 * @version 0.1.1
 */
public abstract class DrivingFragment extends Fragment {

    /**
     * Returns the forward/backward speed represented by the fragment.
     * <p>
     *     All speed is bound between [-100, 100] values.
     *
     *     >0 => Forward
     *      0 => Not moving forward/backward
     *     <0 => Backward
     * </p>
     *
     * @return speed value of vertical movement
     */
    public abstract int getAcceleration();

    /**
     * Returns the turning right/left speed represented by the fragment.
     * <p>
     *     All speed is bound between [-100, 100] values.
     *
     *     >0 => Turning right
     *      0 => Not turning
     *     <0 => Turning left
     * </p>
     *
     * @return speed value of turning movement
     */
    public abstract int getTurning();

}
