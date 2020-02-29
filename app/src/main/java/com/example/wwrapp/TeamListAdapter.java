package com.example.wwrapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wwrapp.database.Route;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TeamListAdapter extends RecyclerView.Adapter<TeamListAdapter.RouteViewHolder> {

    private static final String TAG = "TeamListAdapter";

    private List<String> mName;
    private LayoutInflater mInflater;

    public TeamListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.layout_team, parent, false);
        return new RouteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        holder.teammateName.setText(mName.get(position));
    }

    @Override
    public int getItemCount() {
        if (mName != null) {
            return mName.size();
        } else {
            return 0;
        }
    }

    void setName(List<String> name) {
        mName = name;
        notifyDataSetChanged();
    }

    class RouteViewHolder extends RecyclerView.ViewHolder{

        TextView teammateName;


        //RelativeLayout parentLayout;
        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            teammateName = itemView.findViewById(R.id.teammate_name);

        }

    }
}
