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

    /**
     * List of all possible driving fragments by name
     */
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



    protected PreferencesService(Context context) {
        invertMode = false;
        drivingTheme = DrivingTheme.FULL_ACCELEROMETER;

        // Get data from database
        preferencesRepo = new PreferencesDB(context);
        drivingTheme = preferencesRepo.recoverDrivingTheme();
        invertMode = preferencesRepo.recoverInvertMode();
    }

    /**
     * Method that returns the current status of "invert mode" preference.
     *
     * @param context a context to be used to enable communication with {@link PreferencesRepo}
     * @return true if invert mode is enabled. False otherwise.
     */
    public static boolean isInvertMode(Context context) {
        return getInstance(context).invertMode;
    }

    /**
     * Method that stores the current preference for invert mode.
     *
     * @param invertMode status to be stored. True to enable invert mode, false otherwise.
     * @param context a context to be used to enable communication with {@link PreferencesRepo}
     */
    public static void setInvertMode(boolean invertMode, Context context) {
        if (invertMode != isInvertMode(context)) {
            getInstance(context).invertMode = invertMode;
            saveInstance(context);
        }
    }

    /**
     * Method that returns the current status of "driving theme" preference.
     *
     * @param context a context to be used to enable communication with {@link PreferencesRepo}
     * @return the current driving theme status
     */
    public static DrivingTheme getDrivingTheme(Context context) {
        return getInstance(context).drivingTheme;
    }

    /**
     * Method that stores the current preference for driving theme.
     *
     * @param drivingTheme driving theme to be stored.
     * @param context a context to be used to enable communication with {@link PreferencesRepo}
     */
    public static void setDrivingTheme(DrivingTheme drivingTheme, Context context) {
        if (drivingTheme != getDrivingTheme(context)) {
            getInstance(context).drivingTheme = drivingTheme;
            saveInstance(context);
        }
    }
}
