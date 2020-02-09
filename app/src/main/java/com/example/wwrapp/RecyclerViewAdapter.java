package com.example.wwrapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mRouteName = new ArrayList<>();
    private ArrayList<String> mRouteDate = new ArrayList<>();
    private ArrayList<String> mRouteMile = new ArrayList<>();
    private ArrayList<String> mRouteStep = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> RouteName, ArrayList<String> RouteDate, ArrayList<String> RouteMile, ArrayList<String> RouteStep, Context Context) {
        this.mRouteName = RouteName;
        this.mRouteDate = RouteDate;
        this.mRouteMile = RouteMile;
        this.mRouteStep = RouteStep;
        this.mContext = Context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder: called");

        holder.routeName.setText(mRouteName.get(position));
        holder.routeDate.setText(mRouteDate.get(position));
        holder.routeMile.setText(mRouteMile.get(position));
        holder.routeStep.setText(mRouteStep.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Launch the detail page
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRouteName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView routeName;
        TextView routeDate;
        TextView routeMile;
        TextView routeStep;
        RelativeLayout parentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            routeName = itemView.findViewById(R.id.route_name);
            routeDate = itemView.findViewById(R.id.route_date);
            routeMile = itemView.findViewById(R.id.route_mile);
            routeStep = itemView.findViewById(R.id.route_step);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
