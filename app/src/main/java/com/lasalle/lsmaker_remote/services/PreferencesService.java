package com.lasalle.lsmaker_remote.services;

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

    // Singleton instance
    private static PreferencesService instance;
    private static synchronized PreferencesService getInstance() {
        if (instance == null) {
            instance = new PreferencesService();
        }
        return instance;
    }

    private static void saveInstance() {
        // TODO: Store information to database
    }

    public PreferencesService() {
        // TODO: Get data from database
        invertMode = false;
        drivingTheme = DrivingTheme.FULL_ACCELEROMETER;
    }

    public static boolean isInvertMode() {
        return getInstance().invertMode;
    }

    public static void setInvertMode(boolean invertMode) {
        if (invertMode != isInvertMode()) {
            getInstance().invertMode = invertMode;
            saveInstance();
        }
    }

    public static DrivingTheme getDrivingTheme() {
        return getInstance().drivingTheme;
    }

    public static void setDrivingTheme(DrivingTheme drivingTheme) {
        if (drivingTheme != getDrivingTheme()) {
            getInstance().drivingTheme = drivingTheme;
            saveInstance();

        }
    }
}
