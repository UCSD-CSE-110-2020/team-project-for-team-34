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
import com.example.wwrapp.models.IUser;
import com.example.wwrapp.models.Route;
import com.example.wwrapp.utils.InitialsExtracter;
import com.example.wwrapp.utils.WWRConstants;
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
public class TeammateRouteAdapter extends FirestoreRecyclerAdapter<Route, TeammateRouteAdapter.TeammateRouteViewHolder> {
    private static final String TAG = "TeammateRouteAdapter";

    private OnRouteSelectedListener mOnRouteSelectedListener;
    private LayoutInflater mInflater;

    private IUser mUser;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.
     *
     * @param options
     */
    public TeammateRouteAdapter(@NonNull FirestoreRecyclerOptions<Route> options, IUser user) {
        super(options);
        mUser = user;
        Log.d(TAG, "in TeammateRouteAdapter constructor");
    }

    @Override
    protected void onBindViewHolder(@NonNull TeammateRouteViewHolder holder, int position, @NonNull Route model) {
        // Bind data to the view
        Log.d(TAG, "in onBindViewHolder");

        // Set name and starting point
        Log.d(TAG, "Owner email is: " + model.getOwnerEmail());
        Log.d(TAG, "Owner name is: " + model.getOwnerName());
        Log.d(TAG, "Route model name is " + model.getRouteName());
        Log.d(TAG, "Current user email is " + mUser.getEmail());
        if (model.getOwnerEmail().equals(mUser.getEmail())) {
            Log.d(TAG, "onBindViewHolder: ");
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        } else {
            holder.routeName.setText(model.getRouteName());
            holder.routeStartingPoint.setText(model.getStartingPoint());


            holder.routeDate.setText(model.getDateOfLastWalk());

            // Set steps and miles
            // TODO: Substitute your own data if available
            holder.routeSteps.setText(String.valueOf(model.getSteps()));
            holder.routeMiles.setText(String.valueOf(model.getMiles()));

            // TODO: Set the icon of the user with initials
            String ownerName = model.getOwnerName();
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

            // Set the owner's name
            holder.teammateName.setText(iconName);

            // TODO: Display check icon if you have walked this route before

            // Set favorite icon
            // TODO: Substitution of favorite: see your personal rating if you have one, else see owner's
            boolean isFavorite = model.isFavorite();
            if (isFavorite) {
                holder.favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_on));
                holder.favoriteBtn.setChecked(true);
            } else {
                holder.favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_off));
            }

            // Listen for favorite changes
            holder.favoriteBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        holder.favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_on));
                        // TODO: Update favorite status (refer to RoutesActivity)
                    } else
                        holder.favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_off));
                    // TODO: Update un-favorite status

                }
            });
        }
    }

    @NonNull
    @Override
    public TeammateRouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "in onCreateViewHolder");
        mInflater = LayoutInflater.from(parent.getContext());
        View itemView = mInflater.inflate(R.layout.layout_teammate_route, parent, false);
        return new TeammateRouteViewHolder(itemView);
    }

    public class TeammateRouteViewHolder extends RecyclerView.ViewHolder {

        TextView routeName;
        TextView routeStartingPoint;
        TextView routeDate;
        TextView routeMiles;
        TextView routeSteps;
        TextView teammateName;
        ToggleButton favoriteBtn;
        View itemView;

        public TeammateRouteViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "in RouteViewHolder constructor");
            routeName = itemView.findViewById(R.id.route_name);
            routeStartingPoint = itemView.findViewById(R.id.starting_point);
            routeDate = itemView.findViewById(R.id.route_date);
            routeMiles = itemView.findViewById(R.id.route_mile);
            routeSteps = itemView.findViewById(R.id.route_step);
            favoriteBtn = itemView.findViewById(R.id.favoriteBtn);
            teammateName = itemView.findViewById(R.id.teammate_name);
            this.itemView = itemView;

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
