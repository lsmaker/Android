package com.lasalle.lsmaker_remote.services;

import android.content.Context;

import com.lasalle.lsmaker_remote.repositories.PreferencesRepo;
import com.lasalle.lsmaker_remote.repositories.impl.PreferencesDB;

/**
 * Service that manages the preferences configuration and information.
 *
 * @author Eduard de Torres
 * @version 1.0.0
 */
public class PreferencesService {

    public enum DrivingTheme {
        FULL_ACCELEROMETER,
        SEMI_ACCELEROMETER
    }

    private boolean invertMode;
    private DrivingTheme drivingTheme;
    private PreferencesRepo preferencesRepo;

    // Singleton instance
    private static PreferencesService instance;
    private static synchronized PreferencesService getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesService(context);
        }
        return instance;
    }

    private static void saveInstance(Context context) {
        // Stores information to database
        PreferencesRepo repo = getInstance(context).preferencesRepo;
        repo.storePreferences(isInvertMode(context), getDrivingTheme(context));
    }


    public PreferencesService(Context context) {
        invertMode = false;
        drivingTheme = DrivingTheme.FULL_ACCELEROMETER;

        // Get data from database
        preferencesRepo = new PreferencesDB(context);
        drivingTheme = preferencesRepo.recoverDrivingTheme();
        invertMode = preferencesRepo.recoverInvertMode();
    }

    public static boolean isInvertMode(Context context) {
        return getInstance(context).invertMode;
    }

    public static void setInvertMode(boolean invertMode, Context context) {
        if (invertMode != isInvertMode(context)) {
            getInstance(context).invertMode = invertMode;
            saveInstance(context);
        }
    }

    public static DrivingTheme getDrivingTheme(Context context) {
        return getInstance(context).drivingTheme;
    }

    public static void setDrivingTheme(DrivingTheme drivingTheme, Context context) {
        if (drivingTheme != getDrivingTheme(context)) {
            getInstance(context).drivingTheme = drivingTheme;
            saveInstance(context);
        }
    }
}
