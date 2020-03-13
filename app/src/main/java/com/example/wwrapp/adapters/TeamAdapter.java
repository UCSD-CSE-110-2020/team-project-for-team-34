package com.example.wwrapp.adapters;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wwrapp.R;
import com.example.wwrapp.models.WWRUser;
import com.example.wwrapp.utils.FirestoreConstants;
import com.example.wwrapp.utils.InitialsExtracter;
import com.example.wwrapp.utils.WWRConstants;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class TeamAdapter extends FirestoreRecyclerAdapter<WWRUser, TeamAdapter.TeamViewHolder> {

    private static final String TAG = "TeamAdapter";

    private LayoutInflater mInflater;

    private FirestoreRecyclerOptions<WWRUser> mOptions;

    private WWRUser mUser;

    public TeamAdapter(@NonNull FirestoreRecyclerOptions<WWRUser> options, WWRUser user) {
        super(options);
        mUser = user;
        mOptions = options;
        Log.d(TAG, "in TeamAdapter constructor");
    }


    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position, @NonNull WWRUser model) {
        Log.d(TAG, "onBindViewHolder: called");
        Log.d(TAG, "TeamMember email is " + model.getEmail());
        if (model.getEmail().equals(mUser.getEmail())) {
            Log.d(TAG, "Hiding user from view");
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        } else {
            Log.d(TAG, "Displaying user: " + model.getEmail());

            holder.teammateName.setText(model.getName());
            holder.teammateEmail.setText(model.getEmail());
            String status = model.getTeamStatus();
            // If the invitee has accepted AND the user is on a team, then we
            // know both are on a team
            if (FirestoreConstants.FIRESTORE_TEAM_INVITE_ACCEPTED.equals(status) &&
                    !mUser.getTeamName().isEmpty()) {
                holder.teammateName.setTextColor(Color.BLACK);
                holder.teammateEmail.setTextColor(Color.BLACK);
            } else {
                holder.teammateName.setTextColor(Color.GRAY);
                holder.teammateEmail.setTextColor(Color.GRAY);
            }

            // Set the user icons
            String ownerName = model.getName();
            String firstInitial = null;
            String secondInitial = null;
            if (InitialsExtracter.hasOnlyOneInitial(ownerName)) {
                firstInitial = InitialsExtracter.getFirstInitial(ownerName);
                secondInitial = WWRConstants.EMPTY_STR;
            } else {
                firstInitial = InitialsExtracter.getFirstInitial(ownerName);
                secondInitial = InitialsExtracter.getSecondInitial(ownerName);
            }
            String iconName = firstInitial + secondInitial;
            int iconColor = model.getColor();

            // Set the owner's name
            holder.teammateName.setText(iconName);

            // Set the owner's color
            Drawable roundDrawable = holder.teammateIcon.getResources().getDrawable(R.drawable.button_background);
            roundDrawable.setColorFilter(iconColor, PorterDuff.Mode.SRC_ATOP);
            holder.teammateIcon.setBackground(roundDrawable);
            holder.teammateIcon.setText(iconName);
        }
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "in onCreateViewHolder");
        mInflater = LayoutInflater.from(parent.getContext());
        View itemView = mInflater.inflate(R.layout.layout_team, parent, false);
        return new TeamViewHolder(itemView);
    }

    class TeamViewHolder extends RecyclerView.ViewHolder {
        TextView teammateName;
        TextView teammateEmail;
        Button teammateIcon;

        View itemView;

        //RelativeLayout parentLayout;
        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "TeamViewHolder: in TeamViewHolder constructor");
            teammateName = itemView.findViewById(R.id.team_member_name_team_activity);
            teammateEmail = itemView.findViewById(R.id.team_member_email_team_activity);

            teammateIcon = itemView.findViewById(R.id.teammate_icon_team_activity);
            this.itemView = itemView;
        }
    }
}
