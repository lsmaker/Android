package com.lasalle.lsmaker_remote.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.lasalle.lsmaker_remote.R;
import com.lasalle.lsmaker_remote.fragments.driving.AccelerometerDrivingFragment;
import com.lasalle.lsmaker_remote.fragments.driving.SliderDrivingFragment;
import com.lasalle.lsmaker_remote.fragments.driving.interfaces.DrivingFragment;
import com.lasalle.lsmaker_remote.fragments.driving.interfaces.DrivingFragmentObserver;
import com.lasalle.lsmaker_remote.services.DataSenderService;
import com.lasalle.lsmaker_remote.services.PreferencesService;

/**
 * Activity that manages the driving view.
 *
 * Contains a fragment to be able to choose between different driving views and is contained inside
 * a navigational drawer.
 *
 * @author Eduard de Torres
 * @version 0.1.2
 */
public class DrivingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private DrivingFragment drivingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_base);
        // Screen orientation's configuration.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Keeps screen from turning off.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.driving_activity_title);
        }
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        // Driving view fragment configuration.
        drivingFragment = new SliderDrivingFragment();
        //drivingFragment = new AccelerometerDrivingFragment();

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.driving_fragment_container, drivingFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DrivingFragmentObserver.setRunning(true);
        Intent mDataSenderServiceIntent = new Intent(this, DataSenderService.class);
        startService(mDataSenderServiceIntent);
        changeDrivingFragment(PreferencesService.getDrivingTheme(getApplicationContext()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        DrivingFragmentObserver.setRunning(false);
    }

    /**
     * Changes the current driving fragment for the one specified as a parameter
     *
     * @param theme DrivingTheme value to choose as driving view
     */
    public void changeDrivingFragment (PreferencesService.DrivingTheme theme) {

        if (theme.equals(PreferencesService.DrivingTheme.FULL_ACCELEROMETER)) {
            if (drivingFragment.getClass().equals(AccelerometerDrivingFragment.class)) {
                return;
            }
            drivingFragment = new AccelerometerDrivingFragment();
        }
        if (theme.equals(PreferencesService.DrivingTheme.SEMI_ACCELEROMETER)) {
            if (drivingFragment.getClass().equals(SliderDrivingFragment.class)) {
                return;
            }
            drivingFragment = new SliderDrivingFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.driving_fragment_container, drivingFragment).commit();
    }


    /* Navigation drawer methods */

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // The Driving activity is our main activity, so we want to disable it.
            //super.onBackPressed();
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
            // Driving view selected
            // Nothing to do as we already are on the driving activity.
        } else if (id == R.id.nav_configuration) {
            // Configuration or preferences view selected
            Intent i = new Intent(this, PreferencesActivity.class);
            startActivity(i);
            // After starting the activity, we will "kill" the current activity to prevent the
            // navigation stack from growing without stop.
            finish();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
