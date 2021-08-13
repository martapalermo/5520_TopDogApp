package edu.neu.madcourse.topdog;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

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

