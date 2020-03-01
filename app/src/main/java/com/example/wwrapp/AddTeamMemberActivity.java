package com.example.wwrapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AddTeamMemberActivity extends AppCompatActivity {

    private Button confirm;
    private Button cancel;
    private EditText new_member_name;
    private EditText new_member_email;
    private String member_name;
    private String member_email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        new_member_name = (EditText) findViewById(R.id.member_name_edit_text);
        new_member_email = (EditText) findViewById(R.id.member_email_edit_text);
        member_name = new_member_name.getText().toString();
        member_email = new_member_email.getText().toString();
        confirm = (Button) findViewById(R.id.add_member_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save member_name and member_email to database and go to team screen.
            }
        });

        cancel = (Button) findViewById(R.id.add_member_cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // going back to previous screen
                finish();
            }
        });


    }
}
