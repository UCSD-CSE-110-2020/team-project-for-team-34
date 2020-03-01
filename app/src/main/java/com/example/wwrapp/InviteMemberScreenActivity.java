package com.example.wwrapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InviteMemberScreenActivity extends AppCompatActivity {

    private Button acceptInvite;
    private Button declineInvite;
    private TextView member;
    private String member_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_member);

        member = findViewById(R.id.team_member_name_text_view);
        // get the member's name from data base and set it to member_name
        member.setText(member_name);

        acceptInvite = findViewById(R.id.invite_accept_button);
        declineInvite = findViewById(R.id.invite_decline_button);
        acceptInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to team route and store user's info to team screen.
            }
        });

        declineInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to team route but user's info will not be stored.
            }
        });
    }
}
