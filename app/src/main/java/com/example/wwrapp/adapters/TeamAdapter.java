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
import com.example.wwrapp.model.MockUser;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

public class TeamAdapter extends FirestoreRecyclerAdapter<MockUser,TeamAdapter.TeamViewHolder> {

    private static final String TAG = "TeamListAdapter";

    private List<String> mName;
    private LayoutInflater mInflater;

    //TODO:implement actual user
    public TeamAdapter(@NonNull FirestoreRecyclerOptions<MockUser> options) {
        super(options);
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "in onCreateViewHolder");
        mInflater = LayoutInflater.from(parent.getContext());
        View itemView = mInflater.inflate(R.layout.layout_listitem, parent, false);
        return new TeamViewHolder(itemView);
    }

    //TODO:implement actual user
    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position, @NonNull MockUser model) {
        Log.d(TAG, "onBindViewHolder: called");
        holder.teammateName.setText(model.getName());

        //TODO: Change Invite Status in IUSER to Boolean
        /*boolean isTeammate = model.getInviteStatus();
        if (isTeammate) {
            holder.teammateName.setTextColor(Color.BLACK);
        } else {
            holder.teammateName.setTextColor(Color.GRAY);
        }*/
    }

    @Override
    public int getItemCount() {
        if (mName != null) {
            return mName.size();
        } else {
            return 0;
        }
    }


    class TeamViewHolder extends RecyclerView.ViewHolder{
        TextView teammateName;

        //RelativeLayout parentLayout;
        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            teammateName = itemView.findViewById(R.id.teammate_name);

        }

    }
}
