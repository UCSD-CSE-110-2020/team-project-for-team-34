package com.example.wwrapp.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wwrapp.R;
import com.example.wwrapp.models.IUser;
import com.example.wwrapp.models.TeamMember;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class TeamAdapter extends FirestoreRecyclerAdapter<TeamMember,TeamAdapter.TeamViewHolder> {

    private static final String TAG = "TeamAdapter";

    private LayoutInflater mInflater;

    private FirestoreRecyclerOptions<TeamMember> mOptions;

    private IUser mUser;

    public TeamAdapter(@NonNull FirestoreRecyclerOptions<TeamMember> options,IUser User) {
        super(options);
        mUser = User;
        mOptions = options;
        Log.d(TAG, "in TeamAdapter constructor");
    }



    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position, @NonNull TeamMember model) {
        Log.d(TAG, "onBindViewHolder: called");
        if(model.getEmail().equals(mUser.getEmail()))
        {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
        }
        else {
            holder.teammateName.setText(model.getName());
            String status = model.getStatus();
            if (status.equals("accepted")) {
                holder.teammateName.setTextColor(Color.BLACK);
            } else {
                holder.teammateName.setTextColor(Color.GRAY);
            }
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

    class TeamViewHolder extends RecyclerView.ViewHolder{
        TextView teammateName;
        View itemView;

        //RelativeLayout parentLayout;
        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "TeamViewHolder: in TeamViewHolder constructor");
            teammateName = itemView.findViewById(R.id.teammate_name);
            this.itemView = itemView;
        }
    }
}
