package com.lasalle.lsmaker_remote.fragments.driving.interfaces;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Abstract base fragment to work with DrivingActivity.
 *
 * @author Eduard de Torres
 * @version 1.0.0
 */
public abstract class DrivingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

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
