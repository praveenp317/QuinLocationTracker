package com.quinlocationtracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.quinlocationtracker.data.model.LocationTaskModel;

import java.util.List;
import java.util.Locale;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private Context mCtx;
    private List<LocationTaskModel> taskList;

    public LocationAdapter(Context mCtx, List<LocationTaskModel> taskList) {
        this.mCtx = mCtx;
        this.taskList = taskList;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.item_location_data, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LocationViewHolder holder, int position) {
        LocationTaskModel t = taskList.get(position);
        holder.mLastUpdateTimeTextView.setText(String.format(Locale.ENGLISH, "%s: %s",
                "Time: " ,  t.getTime()));
        holder.mLatitudeTextView.setText(t.getLat());
        holder.mLongitudeTextView.setText(t.getLng());
        holder.mSpeedTextView.setText(String.format(Locale.ENGLISH, "%s: %s",
                "Speed: " ,  t.getAvgSpeed()));

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }


    class LocationViewHolder extends RecyclerView.ViewHolder {

        private TextView mLastUpdateTimeTextView;
        private TextView mLatitudeTextView;
        private TextView mLongitudeTextView;
        private TextView mSpeedTextView;


        public LocationViewHolder(View itemView) {
            super(itemView);

            mLastUpdateTimeTextView = itemView.findViewById(R.id.last_update_time_text);
            mLatitudeTextView = itemView.findViewById(R.id.latitude_text);
            mLongitudeTextView = itemView.findViewById(R.id.longitude_text);
            mSpeedTextView = itemView.findViewById(R.id.speed_text);

        }

    }

}
