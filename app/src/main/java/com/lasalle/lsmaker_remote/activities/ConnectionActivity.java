package com.lasalle.lsmaker_remote.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lasalle.lsmaker_remote.R;
import com.lasalle.lsmaker_remote.services.BluetoothConnection;
import com.lasalle.lsmaker_remote.utils.Utils;

/**
 * A login screen that offers login via device-name/password (or pincode).
 *
 * @author Eduard de Torres
 * @version 0.1.1
 */
public class ConnectionActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView deviceNameView;
    private EditText pincodeView;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        // Screen orientation's configuration.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        // Set up the login form.
        deviceNameView = (AutoCompleteTextView) findViewById(R.id.device_name);
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
        }

        mProgressView = findViewById(R.id.login_progress_view);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        Utils.hideKeyboard(this);

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
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(deviceName, pincode);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String deviceName) {
        //TODO: Replace this with your own logic
        return deviceName.length() >= 4;
    }

    private boolean isPasswordValid(String pincode) {
        //TODO: Replace this with your own logic
        return pincode.length() >= 4;
    }

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

    private void showConnectionErrorPopUp(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.connection_error_title));
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.pop_up_accept), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                pincodeView.requestFocus();
                pincodeView.setText("");
            }
        });
        builder.show();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String deviceName;
        private final String pincode;

        UserLoginTask(String email, String password) {
            deviceName = email;
            pincode = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return BluetoothConnection.getInstance().connect(deviceName, pincode);
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

