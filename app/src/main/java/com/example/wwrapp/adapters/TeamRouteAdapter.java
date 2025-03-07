package com.example.wwrapp.adapters;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
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
import com.example.wwrapp.utils.InitialsExtracter;
import com.example.wwrapp.utils.RouteDocumentNameUtils;
import com.example.wwrapp.utils.WWRConstants;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Adapter for the Route Recycler View
 * Code adapted from:
 * https://www.youtube.com/watch?v=lAGI6jGS4vs
 * https://www.youtube.com/watch?v=3WR4QAiVuCw
 */
public class TeamRouteAdapter extends FirestoreRecyclerAdapter<Route, TeamRouteAdapter.TeammateRouteViewHolder> {
    private static final String TAG = "TeammateRouteAdapter";

    private OnRouteSelectedListener mOnRouteSelectedListener;
    private LayoutInflater mInflater;

    private FirebaseFirestore mFirestore;
    private AbstractUser mUser;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.
     *
     * @param options
     */
    public TeamRouteAdapter(@NonNull FirestoreRecyclerOptions<Route> options, AbstractUser user) {
        super(options);
        Log.d(TAG, "in TeammateRouteAdapter constructor");
        mFirestore = FirebaseFirestore.getInstance();
        mUser = user;
    }

    @Override
    protected void onBindViewHolder(@NonNull TeammateRouteViewHolder holder, int position, @NonNull Route model) {
        Log.d(TAG, "in onBindViewHolder with route: " + model.getRouteName());

        // Set name and starting point
        Log.d(TAG, "Owner email is: " + model.getOwnerEmail());
        Log.d(TAG, "Owner name is: " + model.getOwnerName());


        // If a route belongs to the current user, hide it
        if (model.getOwnerEmail().equals(mUser.getEmail())) {
            Log.d(TAG, "Hiding route ");
            holder.itemView.setVisibility(View.GONE);
            // Collapse the view into nothing
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        } else {
            // Bind data to the view




            // Route name and starting point are the same for all users
            holder.routeName.setText(model.getRouteName());
            holder.routeStartingPoint.setText(model.getStartingPoint());

            // Important: Check if the current user has data that can be substituted for this Route
            String routeDocName = RouteDocumentNameUtils.getRouteDocumentName(model.getOwnerEmail(), model.getRouteName());
            mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                    .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                    .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_ROUTES_PATH)
                    .document(routeDocName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();

                                if (document.exists()) {
                                    Log.d(TAG, "Got route data: " + document.getData());

                                    // Check which data can be substituted

                                    // If the user has their own rating, display that instead

                                    mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                            .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_ROUTES_PATH)
                                            .document(routeDocName)
                                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_ROUTES_FAVORITERS_PATH)
                                            .document(mUser.getEmail())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@android.support.annotation.NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            // If the user has their own rating
                                                            Log.d(TAG, "User has own rating for " + model.getRouteName());
                                                            Map<String, Object> map = (Map<String, Object>) (document.getData());
                                                            boolean isFavorite = (Boolean) (map.get(mUser.getEmail()));
                                                            if (isFavorite) {
                                                                holder.favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_on));
                                                                holder.favoriteBtn.setChecked(true);
                                                            } else {
                                                                holder.favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_off));
                                                                holder.favoriteBtn.setChecked(false);
                                                            }
                                                        } else {
                                                            // User doesn't have their own rating
                                                            Log.d(TAG, "User does not have own rating for " + model.getRouteName());
                                                            boolean isOwnerFavorite = model.isFavorite();
                                                            if (isOwnerFavorite) {
                                                                holder.favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_on));
                                                                holder.favoriteBtn.setChecked(true);
                                                            } else {
                                                                holder.favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_off));
                                                            }
                                                        }
                                                    } else {
                                                        Log.e(TAG, "get failed with ", task.getException());
                                                    }
                                                }
                                            });


                                    // If the user has walked this route before, display their stats
                                    mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                            .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_ROUTES_PATH)
                                            .document(routeDocName)
                                            .collection(FirestoreConstants.FIRESTORE_COLLECTION_ROUTES_WALKERS_PATH)
                                            .document(mUser.getEmail())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            // If user has walked before
                                                            Log.d(TAG, "User has walked this route before " + model.getRouteName());
                                                            Walk walk = document.toObject(Walk.class);
                                                            holder.routeSteps.setText(String.valueOf(walk.getSteps()));
                                                            holder.routeMiles.setText(String.valueOf(walk.getMiles()));
                                                            holder.routeDate.setText(walk.getDate());
                                                            holder.checkMark.setVisibility(View.VISIBLE);

                                                        } else {
                                                            // If user has not walked before
                                                            Log.d(TAG, "Current user has NOT walked route before " + model.getRouteName());
                                                            // Substitute the owner's stats
                                                            holder.routeSteps.setText(String.valueOf(model.getSteps()));
                                                            holder.routeMiles.setText(String.valueOf(model.getMiles()));
                                                            holder.routeDate.setText(model.getDateOfLastWalk());
                                                            holder.checkMark.setVisibility(View.GONE);

                                                        }
                                                    } else {
                                                        Log.d(TAG, "get failed with ", task.getException());
                                                    }
                                                }
                                            });

                                } else {
                                    Log.d(TAG, "Couldn't find Route");
                                }
                            } else {
                                Log.w(TAG, "get failed with ", task.getException());
                            }
                        }
                    });

            // Set the user icons
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
            int iconColor = model.getOwnerColor();

            // Set the owner's name
            holder.teammateName.setText(iconName);

            // Set the owner's color
            Drawable roundDrawable = holder.teammateIcon.getResources().getDrawable(R.drawable.button_background);
            roundDrawable.setColorFilter(iconColor, PorterDuff.Mode.SRC_ATOP);
            holder.teammateIcon.setBackground(roundDrawable);
            holder.teammateIcon.setText(iconName);


            // Listen for favorite changes
            holder.favoriteBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        holder.favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_on));
                        // Update favorite rating for this user only

                        Map<String, Boolean> map = new HashMap<>();
                        map.put(mUser.getEmail(), true);
                        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                                .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_ROUTES_PATH)
                                .document(routeDocName)
                                .collection(FirestoreConstants.FIRESTORE_COLLECTION_ROUTES_FAVORITERS_PATH)
                                .document(mUser.getEmail())
                                .set(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Successfully updated user as route favoriter");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing favoriter", e);
                                    }
                                });

                    } else {
                        holder.favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_off));
                        // Update favorite rating for this user only
                        Map<String, Boolean> map = new HashMap<>();
                        map.put(mUser.getEmail(), false);

                        mFirestore.collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAMS_PATH)
                                .document(FirestoreConstants.FIRESTORE_DOCUMENT_TEAM_PATH)
                                .collection(FirestoreConstants.FIRESTORE_COLLECTION_TEAM_ROUTES_PATH)
                                .document(routeDocName)
                                .collection(FirestoreConstants.FIRESTORE_COLLECTION_ROUTES_FAVORITERS_PATH)
                                .document(mUser.getEmail())
                                .set(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Successfully updated user as un-favoriter");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing un-favoriter", e);
                                    }
                                });
                    }
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
        ImageView checkMark;
        Button teammateIcon;
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
            checkMark = itemView.findViewById(R.id.check_mark);
            teammateName = itemView.findViewById(R.id.teammate_name);
            teammateIcon = itemView.findViewById(R.id.teammate_icon);
//            Drawable roundDrawable = teammateIcon.getResources().getDrawable(R.drawable.button_background);
//            roundDrawable.setColorFilter(RandomColorGenerator.generateRandomNum(), PorterDuff.Mode.SRC_ATOP);
//            teammateIcon.setBackground(roundDrawable);
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
