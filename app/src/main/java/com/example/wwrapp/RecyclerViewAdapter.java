package com.example.wwrapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.content.ContextWrapper;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private OnRouteListener mOnRouteListener;

    private ArrayList<String> mRouteName = new ArrayList<>();
    private ArrayList<String> mRouteDate = new ArrayList<>();
    private ArrayList<String> mRouteMile = new ArrayList<>();
    private ArrayList<String> mRouteStep = new ArrayList<>();
    private ArrayList<Boolean> mFavourite = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(OnRouteListener onRouteListener,ArrayList<String> RouteName, ArrayList<String> RouteDate, ArrayList<String> RouteMile, ArrayList<String> RouteStep, ArrayList<Boolean> Favourite ,Context Context) {
        this.mOnRouteListener = onRouteListener;
        this.mRouteName = RouteName;
        this.mRouteDate = RouteDate;
        this.mRouteMile = RouteMile;
        this.mRouteStep = RouteStep;
        this.mFavourite = Favourite;
        this.mContext = Context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem,parent,false);
        ViewHolder holder = new ViewHolder(view, mOnRouteListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder: called");

        holder.routeName.setText(mRouteName.get(position));
        holder.routeDate.setText(mRouteDate.get(position));
        holder.routeMile.setText(mRouteMile.get(position));
        holder.routeStep.setText(mRouteStep.get(position));
        holder.favouriteBtn.setChecked(mFavourite.get(position));
        if(mFavourite.get(position))
            holder.favouriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_star_on));
        else
            holder.favouriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_star_off));

        holder.favouriteBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    holder.favouriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_star_on));
                else
                    holder.favouriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_star_off));
            }
        });
        
    }

    @Override
    public int getItemCount() {
        return mRouteName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OnRouteListener onRouteListener;

        TextView routeName;
        TextView routeDate;
        TextView routeMile;
        TextView routeStep;
        ToggleButton favouriteBtn;
        RelativeLayout parentLayout;
        public ViewHolder(@NonNull View itemView, OnRouteListener onRouteListener) {
            super(itemView);
            routeName = itemView.findViewById(R.id.route_name);
            routeDate = itemView.findViewById(R.id.route_date);
            routeMile = itemView.findViewById(R.id.route_mile);
            routeStep = itemView.findViewById(R.id.route_step);
            favouriteBtn = itemView.findViewById(R.id.favoriteBtn);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            this.onRouteListener = onRouteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRouteListener.onRouteClick(getAdapterPosition());
        }
    }

    public interface OnRouteListener{
        void onRouteClick(int position);
    }
}
