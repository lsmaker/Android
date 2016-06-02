package com.lasalle.lsmaker_remote.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import com.lasalle.lsmaker_remote.services.impl.BluetoothConnection;
import com.lasalle.lsmaker_remote.utils.Utils;

/**
 * A login screen that offers login via email/password.
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
        
        // Set up the login form.
        deviceNameView = (AutoCompleteTextView) findViewById(R.id.device_name);

        pincodeView = (EditText) findViewById(R.id.pincode);
        pincodeView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.login_button);
        if (mEmailSignInButton != null) {
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
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
        String password = pincodeView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
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
            mAuthTask = new UserLoginTask(deviceName, password);
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
            //mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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
                // finish();
            } else {
                pincodeView.setError(getResources().getString(R.string.error_incorrect_credentials));
                pincodeView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

