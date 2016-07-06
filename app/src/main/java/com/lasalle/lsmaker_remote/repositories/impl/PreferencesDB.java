package com.lasalle.lsmaker_remote.repositories.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lasalle.lsmaker_remote.repositories.PreferencesRepo;
import com.lasalle.lsmaker_remote.services.PreferencesService;
import com.lasalle.lsmaker_remote.utils.DatabaseHandler;

/**
 * Implementation of the PreferencesRepo contract.
 * Stores all information on the device's database.
 */
public class PreferencesDB implements PreferencesRepo {

    private static final  String TABLE_NAME = "preferences";

    private static final  String DB_INVERT_MODE     = "invert_mode";
    private static final  String DB_DRIVING_THEME   = "driving_theme";

    private static final String[] TABLE_COLUMNS = new String[]{DB_INVERT_MODE, DB_DRIVING_THEME};

    private Context context;

    public PreferencesDB(Context context) {
        this.context = context;
    }


    @Override
    public boolean storePreferences(boolean invertMode, PreferencesService.DrivingTheme theme) {
        SQLiteDatabase database = DatabaseHandler.getInstance(context).getWritableDatabase();
        ContentValues preferencesValue = createContentValuesFromPreferences(invertMode, theme);

        long insertedId = database.update(TABLE_NAME, preferencesValue, null, null);
        return (insertedId != -1);
    }

    private ContentValues createContentValuesFromPreferences(boolean invertMode, PreferencesService.DrivingTheme drivingTheme) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(DB_INVERT_MODE, (invertMode)?1:0);
        contentValues.put(DB_DRIVING_THEME, drivingTheme.ordinal());

        return contentValues;
    }

    @Override
    public boolean recoverInvertMode() {
        SQLiteDatabase database = DatabaseHandler.getInstance(context).getWritableDatabase();
        Cursor cursor =  database.query(
                TABLE_NAME,
                TABLE_COLUMNS,
                null,
                null,
                null,
                null,
                null,
                "1");

        boolean invertMode =  getInvertModeFromCursor(cursor);

        cursor.close();

        return invertMode;
    }

    private boolean getInvertModeFromCursor(Cursor cursor) {
        if(cursor.getCount() != 0 && cursor.moveToFirst()) {
            return 1 == cursor.getInt(cursor.getColumnIndex(DB_INVERT_MODE));
        }
        return false;
    }


    @Override
    public PreferencesService.DrivingTheme recoverDrivingTheme() {
        SQLiteDatabase database = DatabaseHandler.getInstance(context).getWritableDatabase();
        Cursor cursor =  database.query(
                TABLE_NAME,
                TABLE_COLUMNS,
                null,
                null,
                null,
                null,
                null,
                "1");

        PreferencesService.DrivingTheme theme =  getDrivingThemeFromCursor(cursor);

        cursor.close();

        return theme;
    }

    private PreferencesService.DrivingTheme getDrivingThemeFromCursor(Cursor cursor) {
        // Will return first theme by default.
        PreferencesService.DrivingTheme drivingTheme = PreferencesService.DrivingTheme.values()[0];
        if(cursor.getCount() != 0 && cursor.moveToFirst()) {
            int theme = cursor.getInt(cursor.getColumnIndex(DB_DRIVING_THEME));
            drivingTheme = PreferencesService.DrivingTheme.values()[theme];
        }
        return drivingTheme;
    }

}
