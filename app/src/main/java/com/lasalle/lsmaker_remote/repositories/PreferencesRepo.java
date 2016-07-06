package com.lasalle.lsmaker_remote.repositories;

import com.lasalle.lsmaker_remote.services.PreferencesService;

/**
 * Preferences contract to define what's needed to create a Preference repository.
 *
 * @author Eduard de Torres
 * @version 1.0.0
 */
public interface PreferencesRepo {

    /**
     * Stores the invert mode to the repository.
     * The invert mode manages the horizontal flipping of the driving fragments' layouts.
     *
     * @param invertMode current status on invert mode.
     *                   True for "normal" mode. False for inverted mode.
     */
    boolean storePreferences(boolean invertMode, PreferencesService.DrivingTheme theme);

    /**
     * Recovers the stored invert mode at the repository.
     * The invert mode manages the horizontal flipping of the driving fragments' layouts.
     *
     * @return the stored status on invert mode.
     *         True for "normal" mode. False for inverted mode.
     */
    boolean recoverInvertMode();

    /**
     * Recovers the stored driving theme at the repository.
     * The driving theme manages the driving view that will be showed to the user.
     *
     * @return the stored driving theme.
     */
    PreferencesService.DrivingTheme recoverDrivingTheme();
}
