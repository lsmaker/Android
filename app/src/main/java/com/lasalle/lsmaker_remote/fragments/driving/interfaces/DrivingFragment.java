package com.lasalle.lsmaker_remote.fragments.driving.interfaces;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lasalle.lsmaker_remote.services.PreferencesService;

/**
 * Abstract base fragment to work with DrivingActivity.
 * Defines what a Driving fragment should be and implements basic common operations.
 *
 * @author Eduard de Torres
 * @version 2.0.0
 */
public abstract class DrivingFragment extends Fragment {

    protected View mainView;
    protected boolean isInverted;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isInverted = PreferencesService.isInvertMode(getContext());
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    protected void setViewLayout(int id) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(id, null);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.removeAllViews();
        rootView.addView(mainView);
    }


}
