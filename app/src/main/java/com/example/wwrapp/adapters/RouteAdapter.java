package com.example.wwrapp.adapters;

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

import com.example.wwrapp.R;
import com.example.wwrapp.models.Route;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Adapter for the Route Recycler View
 * TODO:
 * TODO: IMPORTANT: Notes for Tony:
 * TODO: See part 3 for the general concept the FirestoreRecyclerAdapter: https://www.youtube.com/watch?v=lAGI6jGS4vs
 * TODO: See part 6 for click listeners: https://www.youtube.com/watch?v=3WR4QAiVuCw
 */
public class RouteAdapter extends FirestoreRecyclerAdapter<Route, RouteAdapter.RouteViewHolder> {
    private static final String TAG = "RouteAdapter";

    private OnRouteSelectedListener mOnRouteSelectedListener;
    private LayoutInflater mInflater;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public RouteAdapter(@NonNull FirestoreRecyclerOptions<Route> options) {
        super(options);
        Log.d(TAG, "in RouteAdapter constructor");
    }

    @Override
    protected void onBindViewHolder(@NonNull RouteViewHolder holder, int position, @NonNull Route model) {
        // Bind data to the view
        Log.d(TAG, "in onBindViewHolder");

        // Set name and starting point
        holder.routeName.setText(model.getRouteName());
        holder.routeStartingPoint.setText(model.getStartingPoint());

        holder.routeDate.setText(model.getDateOfLastWalk());

        // Set steps and miles
        holder.routeSteps.setText(String.valueOf(model.getSteps()));
        holder.routeMiles.setText(String.valueOf(model.getMiles()));

        // Set favorite
        boolean isFavorite = model.isFavorite();
        if (isFavorite) {
            holder.favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_on));
            holder.favoriteBtn.setChecked(true);
        } else {
            holder.favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_off));
        }
        holder.favoriteBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    holder.favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_on));
                } else
                    holder.favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_off));

            }
        });
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "in onCreateViewHolder");
        mInflater = LayoutInflater.from(parent.getContext());
        View itemView = mInflater.inflate(R.layout.layout_listitem, parent, false);
        return new RouteViewHolder(itemView);
    }

    public class RouteViewHolder extends RecyclerView.ViewHolder {

        TextView routeName;
        TextView routeStartingPoint;
        TextView routeDate;
        TextView routeMiles;
        TextView routeSteps;
        ToggleButton favoriteBtn;

        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "in RouteViewHolder constructor");
            routeName = itemView.findViewById(R.id.route_name);
            routeStartingPoint = itemView.findViewById(R.id.starting_point);
            routeDate = itemView.findViewById(R.id.route_date);
            routeMiles = itemView.findViewById(R.id.route_mile);
            routeSteps = itemView.findViewById(R.id.route_step);
            favoriteBtn = itemView.findViewById(R.id.favoriteBtn);

            // Register this view to respond to clicks
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    // Check the position beforehand
                    if (position != RecyclerView.NO_POSITION && mOnRouteSelectedListener != null) {
                        mOnRouteSelectedListener.onRouteSelected(getSnapshots().getSnapshot(position), position);

                    }
                }
            });
        }
    }

    public interface OnRouteSelectedListener {
        void onRouteSelected(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnRouteSelectedListener(OnRouteSelectedListener onRouteSelectedListener) {
        this.mOnRouteSelectedListener = onRouteSelectedListener;

    }
}
