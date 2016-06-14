package com.lasalle.lsmaker_remote.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lasalle.lsmaker_remote.R;
import com.lasalle.lsmaker_remote.services.BluetoothConnection;

/**
 * Activity that manages the settings view.
 *
 * @author Eduard de Torres
 * @version 1.0.0
 */
public class SettingsActivity extends AppCompatActivity
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
        View child = getLayoutInflater().inflate(R.layout.content_settings, null);
        mainLayout.addView(child);

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        } else if (id == R.id.nav_configuration) {
            // Nothing to do.
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onDisconnectButtonClick (View view) {
        // TODO: Implement disconnection from Device.
        Log.d(this.getClass().getName(), "Disconnected!");
        BluetoothConnection.getInstance().disconnect();
    }

    public void onInvertControlsButtonClick (View view) {
        // TODO: Implement invert controls.
        Log.d(this.getClass().getName(), "Controls inversion");
    }

    public void onChangeThemeButtonClick (View view) {
        // TODO: Implement change driving theme.
        Log.d(this.getClass().getName(), "Theme selection pop up");
    }
}