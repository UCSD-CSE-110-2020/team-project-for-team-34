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

public class RouteListAdapter extends RecyclerView.Adapter<RouteListAdapter.RouteViewHolder> {

    private static final String TAG = "RouteListAdapter";

    private OnRouteListener mOnRouteListener;

    private List<Route> mRoutes;
    private LayoutInflater mInflater;

    public RouteListAdapter(OnRouteListener onRouteListener, Context context) {
        mOnRouteListener = onRouteListener;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.layout_listitem, parent, false);
        return new RouteViewHolder(itemView, mOnRouteListener);
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

            long steps = currentRoute.getSteps();
            holder.routeSteps.setText(String.valueOf(steps));

            String startingPoing = currentRoute.getStartingPoint();
            if (startingPoing != null)
                holder.routeStaringPoint.setText(startingPoing);

            Boolean isFavorite = currentRoute.isFavorite();
            if (isFavorite) {
                holder.favouriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_on));
                holder.favouriteBtn.setChecked(true);
            } else {
                holder.favouriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_off));
            }
            holder.favouriteBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        holder.favouriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_on));
                    }
                    else
                        holder.favouriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_off));
                }
            });
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

    void setRoutes(List<Route> routes) {
        mRoutes = routes;
        notifyDataSetChanged();
    }

    class RouteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnRouteListener onRouteListener;

        TextView routeName;
        TextView routeStaringPoint;
        TextView routeDate;
        TextView routeMiles;
        TextView routeSteps;
        ToggleButton favouriteBtn;

        //RelativeLayout parentLayout;
        public RouteViewHolder(@NonNull View itemView, OnRouteListener onRouteListener) {
            super(itemView);
            routeName = itemView.findViewById(R.id.route_name);
            routeStaringPoint = itemView.findViewById(R.id.starting_point);
            routeDate = itemView.findViewById(R.id.route_date);
            routeMiles = itemView.findViewById(R.id.route_mile);
            routeSteps = itemView.findViewById(R.id.route_step);
            favouriteBtn = itemView.findViewById(R.id.favoriteBtn);
            //parentLayout = itemView.findViewById(R.id.parent_layout);
            this.onRouteListener = onRouteListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRouteListener.onRouteClick(getAdapterPosition(),mRoutes);
        }
    }

    public interface OnRouteListener {
        void onRouteClick(int position,List<Route> routes);
    }
}
