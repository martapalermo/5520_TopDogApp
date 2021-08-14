package edu.neu.madcourse.topdog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.topdog.DatabaseObjects.Walk;


public class MyStatsRecyclerAdapter extends RecyclerView.Adapter<MyStatsRecyclerAdapter.RecyclerHolder> {

    private final ArrayList<Walk> walkHistoryList;

    public MyStatsRecyclerAdapter(ArrayList<Walk> walkHistoryList) {
        this.walkHistoryList = walkHistoryList;
    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_walk_card, parent, false);
        return new RecyclerHolder(view, parent);
    }

    @Override
    public void onBindViewHolder(RecyclerHolder holder, int position) {
        Walk currentItem = walkHistoryList.get(position);

        int minutes = (int) Math.floor(currentItem.getWalkDuration());
        int seconds = (int) Math.floor((currentItem.getWalkDuration() - minutes) * 60);

        String displayTime = minutes + "m " + seconds + "s";
        holder.walkDuration.setText(displayTime);

        String displayDistance = currentItem.getFinalDistance() + " meters";
        holder.walkDistance.setText(displayDistance);

        String displayWalkCount = "Walk " + (position+1);
        holder.walkCount.setText(displayWalkCount);

        holder.walkDate.setText(currentItem.getLogDate());
    }

    @Override
    public int getItemCount() {
        return walkHistoryList.size();
    }

    public class RecyclerHolder extends RecyclerView.ViewHolder {

        public TextView walkDistance;
        public TextView walkDuration;
        public TextView walkCount;
        public TextView walkDate;
        public ViewGroup parent;

        public RecyclerHolder(View itemView, ViewGroup parent) {
            super(itemView);
            this.parent = parent;
            this.walkDate = itemView.findViewById(R.id.walk_logDate);
            this.walkDistance = itemView.findViewById(R.id.walk_distance);
            this.walkDuration = itemView.findViewById(R.id.walk_duration);
            this.walkCount = itemView.findViewById(R.id.walk_count_id);
        }
    }

}