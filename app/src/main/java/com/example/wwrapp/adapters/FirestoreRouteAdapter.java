/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.wwrapp.adapters;

import android.content.Context;
import android.content.res.Resources;
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
import com.example.wwrapp.database.Route;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * RecyclerView adapter for a list of Routes.
 */
public class FirestoreRouteAdapter extends FirestoreAdapter<FirestoreRouteAdapter.FirestoreRouteViewHolder> {

    public interface OnRouteSelectedListener {

        void onRouteSelected(DocumentSnapshot route);

    }

    private OnRouteSelectedListener mListener;
    private LayoutInflater mInflater;


    public FirestoreRouteAdapter(Query query, OnRouteSelectedListener listener, Context context) {
        super(query);
        mListener = listener;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public FirestoreRouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.layout_listitem, parent, false);
        return new FirestoreRouteViewHolder(itemView);
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        return new FirestoreRouteViewHolder(inflater.inflate(R.layout.layout_listitem, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FirestoreRouteViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    class FirestoreRouteViewHolder extends RecyclerView.ViewHolder {

        TextView routeName;
        TextView routeStartingPoint;
        TextView routeDate;
        TextView routeMiles;
        TextView routeSteps;
        ToggleButton favoriteBtn;


        public FirestoreRouteViewHolder(View itemView) {
            super(itemView);
            routeName = itemView.findViewById(R.id.route_name);
            routeStartingPoint = itemView.findViewById(R.id.starting_point);
            routeDate = itemView.findViewById(R.id.route_date);
            routeMiles = itemView.findViewById(R.id.route_mile);
            routeSteps = itemView.findViewById(R.id.route_step);
            favoriteBtn = itemView.findViewById(R.id.favoriteBtn);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnRouteSelectedListener listener) {

            Route route = snapshot.toObject(Route.class);
            Resources resources = itemView.getResources();

            // Render the route
            routeName.setText(route.getRouteName());
            routeStartingPoint.setText(route.getStartingPoint());

            LocalDateTime date = route.getDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            String formattedDate = date.format(formatter);
            routeDate.setText(formattedDate);

            routeMiles.setText(String.valueOf(route.getMiles()));
            routeSteps.setText(String.valueOf(route.getSteps()));

            boolean isFavorite = route.isFavorite();
            if (isFavorite) {
                favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_on));
                favoriteBtn.setChecked(true);
            } else {
                favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_off));
            }
            favoriteBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_on));
                    }
                    else
                        favoriteBtn.setBackgroundDrawable(ContextCompat.getDrawable(mInflater.getContext(), R.drawable.ic_star_off));
                }
            });


            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onRouteSelected(snapshot);
                    }
                }
            });
        } // end bind

    } // end FirestoreRouteViewHolder
}

