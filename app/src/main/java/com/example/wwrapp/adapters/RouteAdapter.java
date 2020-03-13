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
import com.example.wwrapp.models.AbstractUser;
import com.example.wwrapp.models.Route;
import com.example.wwrapp.models.Walk;
import com.example.wwrapp.utils.FirestoreConstants;
import com.example.wwrapp.utils.RouteDocumentNameUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

/**
 * Adapter for the Route Recycler View
 * TODO: See part 3 for the general concept the FirestoreRecyclerAdapter: https://www.youtube.com/watch?v=lAGI6jGS4vs
 * TODO: See part 6 for click listeners: https://www.youtube.com/watch?v=3WR4QAiVuCw
 */
public class RouteAdapter extends FirestoreRecyclerAdapter<Route, RouteAdapter.RouteViewHolder> {
    private static final String TAG = "RouteAdapter";

    private OnRouteSelectedListener mOnRouteSelectedListener;
    private LayoutInflater mInflater;
    private FirebaseFirestore mFirestore;
    private AbstractUser mUser;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     * @param user
     */
    public RouteAdapter(@NonNull FirestoreRecyclerOptions<Route> options, AbstractUser user) {
        super(options);
        Log.d(TAG, "in RouteAdapter constructor");
        mFirestore = FirebaseFirestore.getInstance();
        mUser = user;
        Log.d(TAG, "User email is: " + mUser.getEmail());
    }

    @Override
    protected void onBindViewHolder(@NonNull RouteViewHolder holder, int position, @NonNull Route model) {
        // Bind data to the view
        Log.d(TAG, "in onBindViewHolder");

        // TODO: Implement check-icon for previously walked routes
        Log.d(TAG, "Route is " + model.toString());
        Map<String, Walk> walkers = model.getWalkers();
        if (walkers.get(mUser.getEmail()) != null) {
            // If the current user has walked this route, display the check icon
        } else {
            // Gray out the check icon/don't display
        }

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
                Log.d(TAG, "onCheckedChanged: Clicked favorite star");
                // Update the route's favorite status on Firestore
                String routeDocName = RouteDocumentNameUtils.getRouteDocumentName(mUser.getEmail(), model.getRouteName());
                String nestedFieldName = RouteDocumentNameUtils.getNestedFieldName(Route.FIELD_FAVORITERS, mUser.getEmail());

                // If favorited
                if (isChecked) {
                    holder.favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_on));

                    // Update the user's personal route
                    mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                            .document(mUser.getEmail())
                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_ROUTES_PATH)
                            .document(routeDocName)
                            .update(Route.FIELD_FAVORITE, true,
                                    nestedFieldName, true)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Successfully favorited and  " +
                                            "added user to route favoriter in personal collection");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing favorite to personal collection", e);
                                }
                            });

                    // Update the route favorite on the team screen, if the user is on a team
                    if (!mUser.getTeamName().isEmpty()) {
                        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                                .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_ROUTES_PATH)
                                .document(routeDocName)
                                .update(Route.FIELD_FAVORITE, true,
                                        nestedFieldName, true)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Successfully favorited and  " +
                                                "added user to route favoriter in team collection");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing favorite to team collection", e);
                                    }
                                });
                    }


                } else {
                    // If unfavorited
                    holder.favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_off));

                    // Update the user's personal route
                    mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_USERS_PATH)
                            .document(mUser.getEmail())
                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_MY_ROUTES_PATH)
                            .document(routeDocName)
                            .update(Route.FIELD_FAVORITE, false,
                                    nestedFieldName, false)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Successfully unfavorited and  " +
                                            "removed user from route favoriter in personal collection");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing favorite to personal collection", e);
                                }
                            });

                    // Update the route favorite on the team screen, if the user is on a team
                    if (!mUser.getTeamName().isEmpty()) {
                        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                                .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_ROUTES_PATH)
                                .document(routeDocName)
                                .update(Route.FIELD_FAVORITE, false,
                                        nestedFieldName, false)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Successfully unfavorited and  " +
                                                "removed user from route favoriter in team collection");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing favorite to team collection", e);
                                    }
                                });
                    }
                }
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
