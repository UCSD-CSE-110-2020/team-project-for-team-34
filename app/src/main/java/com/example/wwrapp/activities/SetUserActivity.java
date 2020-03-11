package com.example.wwrapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.wwrapp.R;
import com.example.wwrapp.utils.WWRConstants;

public class SetUserActivity extends AppCompatActivity {

    private static final String TAG = "SetUserActivity";
    private Button okButton;
    private Button cancelButton;
    private EditText newEmail;
    private EditText newName;

    public static String INVALID_EMAIL_TOAST_TEXT = "Please enter a valid email";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user);

        okButton = (Button)findViewById(R.id.ok_button);
        cancelButton = (Button)findViewById(R.id.cancel_button);
        newEmail = (EditText)findViewById(R.id.new_email);
        newName = (EditText)findViewById(R.id.new_name);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Cancel button pressed");
//                Intent intent = new Intent(SetUserActivity.this, HomeScreenActivity.class);

                finish();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Ok button pressed");
                returnToHomeActivity();
            }
        });
    }

    private void returnToHomeActivity() {
        String newEmailStr = newEmail.getText().toString();
        String newNameStr = newName.getText().toString();
        Intent intent = new Intent(SetUserActivity.this, HomeScreenActivity.class);

        intent.putExtra(WWRConstants.EXTRA_CALLER_ID_KEY, WWRConstants.EXTRA_SET_USER_ACTIVITY_CALLER_ID);
        intent.putExtra(WWRConstants.EXTRA_USER_EMAIL_KEY, newEmailStr);
        intent.putExtra(WWRConstants.EXTRA_USER_NAME_KEY, newNameStr);
        setResult(RESULT_OK, intent);
        finish();
    }
}
