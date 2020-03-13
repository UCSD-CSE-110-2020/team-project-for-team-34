package com.example.wwrapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wwrapp.R;
import com.example.wwrapp.utils.WWRConstants;

public class SetUserActivity extends AppCompatActivity {

    private static final String TAG = "SetUserActivity";
    private Button mConfirmBtn;
    private Button mCancelBtn;
    private EditText mEmailEditText;
    private EditText mNameEditText;
    private boolean mRequestingSignIn;

    public static String INVALID_EMAIL_TOAST_TEXT = "Please enter a name and email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user);
        Log.d(TAG, "onCreate: ");

        // Check if sign-in is requested
        Intent intent = getIntent();
        mRequestingSignIn = intent.getBooleanExtra(WWRConstants.EXTRA_IS_SIGNING_IN_KEY, false);

        mConfirmBtn = (Button) findViewById(R.id.ok_button);
        mCancelBtn = (Button) findViewById(R.id.cancel_button);
        mEmailEditText = (EditText) findViewById(R.id.new_email);
        mNameEditText = (EditText) findViewById(R.id.new_name);

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Cancel button pressed");
                if (mRequestingSignIn) {
                    Log.w(TAG, "Closing app!");
                    // close the app
                    SetUserActivity.this.finishAffinity();
                } else {
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }
            }
        });

        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Ok button pressed");
                // Check that user entered both name and email
                if (mNameEditText.getText().toString().isEmpty() ||
                        mEmailEditText.getText().toString().isEmpty()) {
                    Log.d(TAG, "Name and/or email were not both entered");
                    Toast.makeText(SetUserActivity.this, INVALID_EMAIL_TOAST_TEXT, Toast.LENGTH_SHORT).show();
                } else {
                    returnToHomeActivity();
                }
            }
        });
    }

    private void returnToHomeActivity() {
        Log.d(TAG, "returnToHomeActivity: ");
        String newEmailStr = mEmailEditText.getText().toString();
        String newNameStr = mNameEditText.getText().toString();
        Intent intent = new Intent();

        intent.putExtra(WWRConstants.EXTRA_USER_EMAIL_KEY, newEmailStr);
        intent.putExtra(WWRConstants.EXTRA_USER_NAME_KEY, newNameStr);
        intent.putExtra(WWRConstants.EXTRA_IS_SIGNING_IN_KEY, mRequestingSignIn);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
