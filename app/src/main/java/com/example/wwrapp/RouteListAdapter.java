package com.example.wwrapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wwrapp.database.Route;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RouteListAdapter extends RecyclerView.Adapter<RouteListAdapter.RouteViewHolder>{

    private static final String TAG = "RouteListAdapter";

    private List<Route> mRoutes;
    private LayoutInflater mInflater;

    public RouteListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.layout_listitem, parent, false);
        return new RouteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        if (mRoutes != null) {
            Route currentRoute = mRoutes.get(position);
            holder.routeName.setText(currentRoute.getRouteName());

            // Format the date
            LocalDateTime routeDate = currentRoute.getDate();
            if (routeDate == null) {
                Log.e(TAG, "Date is null");
                routeDate = LocalDateTime.now();
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            String formattedDate = routeDate.format(formatter);
            holder.routeDate.setText(formattedDate);

            double miles = currentRoute.getMiles();
            holder.routeMiles.setText(String.valueOf(miles));

            int steps = currentRoute.getSteps();
            holder.routeSteps.setText(String.valueOf(steps));
        } else {
            holder.routeName.setText("Route name not ready yet");
            holder.routeDate.setText("Route date not ready yet");
            holder.routeMiles.setText("Route miles not ready yet");
            holder.routeSteps.setText("Route steps not ready yet");
        }
    }

    @Override
    public int getItemCount() {
        if (mRoutes != null) {
            return mRoutes.size();
        } else {
            return 0;
        }
    }

    void setRoutes(List<Route> routes){
        mRoutes = routes;
        notifyDataSetChanged();
    }

    class RouteViewHolder extends RecyclerView.ViewHolder{

        private final TextView routeName;
        private final TextView routeDate;
        private final TextView routeMiles;
        private final TextView routeSteps;
        // RelativeLayout parentLayout;
        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            routeName = itemView.findViewById(R.id.route_name);
            routeDate = itemView.findViewById(R.id.route_date);
            routeMiles = itemView.findViewById(R.id.route_mile);
            routeSteps = itemView.findViewById(R.id.route_step);
            // parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
