package com.lasalle.lsmaker_remote.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lasalle.lsmaker_remote.R;
import com.lasalle.lsmaker_remote.services.BluetoothService;
import com.lasalle.lsmaker_remote.services.PreferencesService;

/**
 * Activity that manages the settings view.
 *
 * @author Eduard de Torres
 * @version 1.0.0
 */
public class PreferencesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_base);
        // Screen orientation's configuration.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Drawer configuration.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.preferences_activity_title);
        }
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        // Loads the activity content's view to the drawer content view
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.app_bar_content);
        View child = getLayoutInflater().inflate(R.layout.content_preferences, null);
        if (mainLayout != null) {
            mainLayout.addView(child);
        }

        TextView deviceNameView = (TextView) findViewById(R.id.preferences_device_name);
        if (deviceNameView != null) {
            deviceNameView.setText(BluetoothService.getBluetoothDeviceName());
        }

        TextView deviceAddressView = (TextView) findViewById(R.id.preferences_device_address);
        if (deviceAddressView != null) {
            deviceAddressView.setText(BluetoothService.getBluetoothDeviceAddress());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        CheckBox invertModeCheckbox = (CheckBox) findViewById(R.id.preferences_invert_checkbox);
        if (invertModeCheckbox != null) {
            invertModeCheckbox.setChecked(PreferencesService.isInvertMode(getApplicationContext()));
        }

        TextView themeText = (TextView) findViewById(R.id.preferences_theme_text);
        if (themeText != null) {
            themeText.setText(PreferencesService.getDrivingTheme(getApplicationContext()).name());
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent i = new Intent(this, DrivingActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.driving, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_driving) {
            Intent i = new Intent(this, DrivingActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_configuration) {
            // Nothing to do.
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onDisconnectButtonClick (View view) {
        Log.d(this.getClass().getName(), "Disconnected!");
        BluetoothService.disconnect();
        final Intent intent = new Intent(this, ConnectionActivity.class);
        startActivity(intent);
        finish();
    }

    public void onInvertControlsButtonClick (View view) {
        final CheckBox invertModeCheckbox = (CheckBox) findViewById(R.id.preferences_invert_checkbox);
        if (invertModeCheckbox != null) {
            final boolean invertMode = invertModeCheckbox.isChecked();
            PreferencesService.setInvertMode(invertMode, getApplicationContext());
        }
    }

    public void onChangeThemeButtonClick (View view) {
        showThemePicker();
    }

    private void showThemePicker() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        //builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle(getString(R.string.preferences_theme_picker_title));

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.select_dialog_singlechoice);

        for (PreferencesService.DrivingTheme theme: PreferencesService.DrivingTheme.values()) {
            arrayAdapter.add(theme.name());
        }

        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        PreferencesService.setDrivingTheme(PreferencesService.DrivingTheme.valueOf(strName), getApplicationContext());

                        TextView themeText = (TextView) findViewById(R.id.preferences_theme_text);
                        if (themeText != null)
                            themeText.setText(PreferencesService.getDrivingTheme(getApplicationContext()).name());
                    }
                });
        builderSingle.show();
    }
}