package com.lasalle.lsmaker_remote.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lasalle.lsmaker_remote.R;
import com.lasalle.lsmaker_remote.adapters.DeviceListAdapter;
import com.lasalle.lsmaker_remote.services.BluetoothService;
import com.lasalle.lsmaker_remote.utils.Utils;

/**
 * A login screen that offers login via device-name/password (or pincode).
 *
 * @author Eduard de Torres
 * @version 0.1.1
 */
public class ConnectionActivity extends AppCompatActivity {

    private static final String TAG = ConnectionActivity.class.getName();

    /**
     * Keeps track of the login task to ensure we can cancel it if requested.
     */
    private BluetoothConnectionTask mAuthTask = null;

    // UI references.
    //private AutoCompleteTextView deviceNameView;
    //private EditText pincodeView;
    private View mProgressView;

    private DeviceListAdapter deviceListAdapter;

    // Bluetooth
    private static final int REQUEST_ENABLE_BT = 1;

    // ListView listener
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BluetoothDevice device = deviceListAdapter.getDevices().get(position);
            attemptLogin(device);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        // Screen orientation's configuration.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.connection_activity_title);
        }
        
        // Set up the login form.
        /*deviceNameView = (AutoCompleteTextView) findViewById(R.id.device_name);
        deviceNameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    pincodeView.requestFocus();
                }
                return false;
            }
        });

        pincodeView = (EditText) findViewById(R.id.pincode);
        pincodeView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_ACTION_DONE) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mLogInButton = (Button) findViewById(R.id.login_button);
        if (mLogInButton != null) {
            mLogInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });
        }*/

        mProgressView = findViewById(R.id.login_progress_view);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // After requesting a change of orientation, the activity will be destroyed and recreated.
        // We only want to start Bluetooth connections during the second "creation".
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(TAG, "LANDSCAPE");


            boolean bluetoothCompatibility =
                    BluetoothService.checkDeviceCompatibility(getPackageManager(),
                            (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE));
            if (!bluetoothCompatibility) {
                showBluetoothNoCompatiblePopUp();
            }


            service_init();
            BluetoothService.enableBluetooth(this);

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

    @Override
    protected void onPause() {
        super.onPause();
        BluetoothService.pauseBluetooth();
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
                    Toast.makeText(this, "Bluetooth has turned on ", Toast.LENGTH_SHORT).show();
                    // Starts scanning for devices.
                    BluetoothService.startScanningDevices();

                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(this, "Problem in BT Turning ON ", Toast.LENGTH_SHORT).show();
                    // Show an error pop up and finish the application.
                    showBluetoothNotEnabledPopUp();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin(BluetoothDevice device) {
        if (mAuthTask != null) {
            return;
        }

        Utils.hideKeyboard(this);

        BluetoothService.enableBluetooth(this);

        /*
        // Reset errors.
        deviceNameView.setError(null);
        pincodeView.setError(null);

        // Store values at the time of the login attempt.
        String deviceName = deviceNameView.getText().toString();
        String pincode = pincodeView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(pincode) && !isPasswordValid(pincode)) {
            pincodeView.setError(getResources().getString(R.string.error_invalid_pincode));
            focusView = pincodeView;
            cancel = true;
        }

        // Check for a valid deviceName address.
        if (TextUtils.isEmpty(deviceName)) {
            deviceNameView.setError(getResources().getString(R.string.error_field_required));
            focusView = deviceNameView;
            cancel = true;
        } else if (!isEmailValid(deviceName)) {
            deviceNameView.setError(getResources().getString(R.string.error_invalid_device_name));
            focusView = deviceNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {*/
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new BluetoothConnectionTask(device, getApplicationContext());
            mAuthTask.execute((Void) null);
        //}
    }


    /*private boolean isEmailValid(String deviceName) {
        //TODO: Replace this with your own logic
        //return deviceName.length() >= 4;
        return true;
    }

    private boolean isPasswordValid(String pincode) {
        //TODO: Replace this with your own logic
        //return pincode.length() >= 4;
        return true;
    }*/

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }


    private void goToDrivingActivity() {
        final Intent intent = new Intent(this, DrivingActivity.class);
        startActivity(intent);
    }

    private void service_init() {
        BluetoothService.service_init(this);
    }


    /*
     * Pop up messages
     */

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
     * Shows a pop up informing the user that Bluetooth isn't enabled and the app will close.
     */
    private void showBluetoothNotEnabledPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.bluetooth_not_enabled_title));
        builder.setMessage(R.string.bluetooth_not_enabled_message);
        builder.setPositiveButton(getString(R.string.pop_up_accept), new DialogInterface.OnClickListener() {
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
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    /*public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String deviceName;
        private final String pincode;
        private final Context context;

        UserLoginTask(String email, String password, Context context) {
            deviceName = email;
            pincode = password;
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return BluetoothService.connect(deviceName, pincode, context);
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
    }*/

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
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

