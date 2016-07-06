package com.lasalle.lsmaker_remote.utils;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lasalle.lsmaker_remote.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DatabaseHandler extends  SQLiteOpenHelper {

    private static final String DB_NAME = "Ls_Remote";
    private static final int DB_VERSION = 1;
    private static SQLiteDatabase.CursorFactory cursorFactory;
    private static DatabaseHandler instance;
    private Context context;

    protected DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;

        // Call to force database creation
        getWritableDatabase();
    }

    public static DatabaseHandler getInstance(Context context) {
        if(instance == null) {
            instance = new DatabaseHandler(context, DB_NAME, cursorFactory, DB_VERSION);
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        executeSQLScript(db, R.raw.db_creation);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        executeSQLScript(db, R.raw.db_removal);
        executeSQLScript(db, R.raw.db_creation);
    }

    private void executeSQLScript(SQLiteDatabase database, int scriptFile) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        InputStream inputStream = null;

        try{
            inputStream = context.getResources().openRawResource(scriptFile);
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();

            String[] createScript = outputStream.toString().split(";");
            for (int i = 0; i < createScript.length; i++) {
                String sqlStatement = createScript[i].trim();
                if (sqlStatement.length() > 0) {
                    database.execSQL(sqlStatement + ";");
                }
            }
        } catch (IOException e) {
            // TODO Handle Script Failed to Load
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO Handle Script Failed to Execute
        }
    }
}
