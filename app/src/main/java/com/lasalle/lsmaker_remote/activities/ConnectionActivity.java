package com.lasalle.lsmaker_remote.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lasalle.lsmaker_remote.R;
import com.lasalle.lsmaker_remote.adapters.DeviceListAdapter;
import com.lasalle.lsmaker_remote.services.BluetoothService;
import com.lasalle.lsmaker_remote.utils.Utils;

/**
 * A login screen that scans bluetooth devices and offers binding to them.
 *
 * @author Eduard de Torres
 * @version 1.0.3
 */
public class ConnectionActivity extends AppCompatActivity {

    private static final String TAG = ConnectionActivity.class.getName();

    /**
     * Keeps track of the login task to ensure we can cancel it if requested.
     */
    private BluetoothConnectionTask mAuthTask = null;

    // UI references.
    private View scanningProgress;
    private Button scanButton;

    private DeviceListAdapter deviceListAdapter;

    // Bluetooth
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_FINE_LOCATION = 2; // Needed on API >= 23
    private boolean askForEnableBLE = true;

    // ListView listener
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BluetoothDevice device = deviceListAdapter.getDevices().get(position);
            attemptLogin(device);
        }
    };

    // Broadcast receiver for the scanning progress
    private BroadcastReceiver scanningReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            final Intent mIntent = intent;
            //*********************//
            if (action.equals(BluetoothService.SCAN_STOPPED)) {
                showProgress(false);
            }

        }
    };

    // IntentFilter to configure the broadcast receiver
    private IntentFilter intentFilter = new IntentFilter(BluetoothService.SCAN_STOPPED);


    /**
     * Asks the system and / or the user for a given permission.
     *
     * @param perm permission name to request
     * @param requestCode internal code to identify the permission request result on an onActivityResult.
     */
    private void loadPermissions(String perm, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            //if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                ActivityCompat.requestPermissions(this, new String[]{perm},requestCode);
            //}
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        // Screen orientation's configuration.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        loadPermissions(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.connection_activity_title);
        }

        scanButton = (Button) findViewById(R.id.connection_scan_button);
        scanningProgress = findViewById(R.id.connection_scanning_progress);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // After requesting a change of orientation, the activity will be destroyed and recreated.
        // We only want to start Bluetooth connections during the second "creation".
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Log.d(TAG, "LANDSCAPE");

            boolean bluetoothCompatibility =
                    BluetoothService.checkDeviceCompatibility(getPackageManager(),
                            (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE));
            if (!bluetoothCompatibility) {
                showBluetoothNoCompatiblePopUp();
            }

            service_init();
            if (askForEnableBLE) {
                BluetoothService.enableBluetooth(this, REQUEST_ENABLE_BT);
                showProgress(true);

                ListView devicesListView = (ListView) findViewById(R.id.connection_devices_listview);
                if (devicesListView != null) {
                    deviceListAdapter = new DeviceListAdapter(this,
                            BluetoothService.getDeviceList(), BluetoothService.getDevRssiValues());
                    devicesListView.setAdapter(deviceListAdapter);
                    devicesListView.setOnItemClickListener(mDeviceClickListener);
                    BluetoothService.setDeviceAdapter(deviceListAdapter);
                }
            }
        }
        registerReceiver(scanningReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BluetoothService.pauseBluetooth();
        unregisterReceiver(scanningReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        BluetoothService.service_stop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            // When the request to enable Bluetooth returns
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, R.string.bluetooth_enabled_toast, Toast.LENGTH_SHORT).show();
                    // Starts scanning for devices.
                    BluetoothService.startScanningDevices();

                } else {
                    // User did not enable Bluetooth or an error occurred
                    askForEnableBLE = false;
                    Toast.makeText(this, R.string.bluetooth_not_enabled_toast, Toast.LENGTH_SHORT).show();
                    // Show an error pop up and finish the application.
                    showBluetoothPermissionNotGrantedPopUp(this);
                }
                break;
            case REQUEST_FINE_LOCATION:
                if (resultCode == Activity.RESULT_OK) {
                    // User did grant Bluetooth permissions
                } else {
                    // User did not grant Bluetooth permissions
                    showLocationPermissionNotGrantedPopUp();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Attempts to bind the android device to the bluetooth device.
     */
    private void attemptLogin(BluetoothDevice device) {
        if (mAuthTask != null) {
            return;
        }

        Utils.hideKeyboard(this);

        BluetoothService.enableBluetooth(this, REQUEST_ENABLE_BT);

        mAuthTask = new BluetoothConnectionTask(device, getApplicationContext());
        mAuthTask.execute((Void) null);
    }

    /**
     * Shows the progress UI and hides the scan button.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            
            scanningProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            scanningProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    scanningProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            scanningProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        scanButton.setVisibility(show ? View.GONE : View.VISIBLE);

    }


    private void goToDrivingActivity() {
        final Intent intent = new Intent(this, DrivingActivity.class);
        startActivity(intent);
    }

    private void service_init() {
        BluetoothService.service_init(this);
    }

    private static IntentFilter updateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothService.SCAN_STOPPED);
        return intentFilter;
    }

    public void startScanning(View view) {
        BluetoothService.startScanningDevices();
        showProgress(true);
    }


    /* ************************************************************************************
     * Pop up messages
     * ************************************************************************************/

    /**
     * Shows a pop up informing the user that there's been an error during connection.
     *
     * @param message error text message to show to the user
     */
    private void showConnectionErrorPopUp(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.connection_error_title));
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.pop_up_accept), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //pincodeView.requestFocus();
                //pincodeView.setText("");
            }
        });
        builder.show();
    }

    /**
     * Shows a pop up informing the user that the device isn't compatible with Bluetooth Low Energy
     * and the app will close.
     */
    private void showBluetoothNoCompatiblePopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.bluetooth_not_compatible_title));
        builder.setMessage(R.string.bluetooth_not_compatible_message);
        builder.setPositiveButton(getString(R.string.pop_up_accept), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    /**
     * Shows a pop up informing the user that Location permissions aren't granted and the app will close.
     */
    private void showLocationPermissionNotGrantedPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.location_not_granted_title));
        builder.setMessage(R.string.location_not_granted_message);
        builder.setPositiveButton(getString(R.string.pop_up_accept), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.show();
    }

    /**
     * Shows a pop up informing the user that Bluetooth isn't enabled.
     * @param connectionActivity
     */
    private void showBluetoothPermissionNotGrantedPopUp(final ConnectionActivity connectionActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.bluetooth_not_granted_title));
        builder.setMessage(R.string.bluetooth_not_granted_message);
        builder.setPositiveButton(getString(R.string.pop_up_accept), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                askForEnableBLE = true;
                BluetoothService.enableBluetooth(connectionActivity, REQUEST_ENABLE_BT);
            }
        });
        builder.setNegativeButton(getString(R.string.pop_up_close), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    public void showListHasResults(boolean hasDevices) {
        TextView noDevicesText = (TextView) findViewById(R.id.connection_devices_not_found_text);
        if (noDevicesText != null) {
            if (hasDevices) {
                noDevicesText.setVisibility(View.GONE);
            } else {
                noDevicesText.setVisibility(View.VISIBLE);
            }
        }
    }


    /**
     * Represents an asynchronous binding task used to connect the user with the bluetooth device.
     *
     */
    public class BluetoothConnectionTask extends AsyncTask<Void, Void, Boolean> {

        private final BluetoothDevice device;
        private final Context context;

        BluetoothConnectionTask(BluetoothDevice device, Context context) {
            this.device = device;
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return BluetoothService.connect(device, context);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                goToDrivingActivity();
            } else {
                showConnectionErrorPopUp(getString(R.string.connection_error_message));
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}

