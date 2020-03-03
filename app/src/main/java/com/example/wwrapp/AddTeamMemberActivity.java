package com.example.wwrapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AddTeamMemberActivity extends AppCompatActivity {

    private Button mConfirmBtn;
    private Button mCancelBtn;
    private EditText mNewMemberName;
    private EditText mNewMemberEmail;
    private String mMemberName;
    private String mMemberEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);


        mNewMemberName = (EditText) findViewById(R.id.member_name_edit_text);
        mNewMemberEmail = (EditText) findViewById(R.id.member_email_edit_text);
        mMemberName = mNewMemberName.getText().toString();
        mMemberEmail = mNewMemberEmail.getText().toString();
        mConfirmBtn = (Button) findViewById(R.id.add_member_button);
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save member_name and member_email to database and go to team screen.
            }
        });

        mCancelBtn = (Button) findViewById(R.id.add_member_cancel_button);
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // going back to previous screen
                finish();
            }
        });


    }
}
