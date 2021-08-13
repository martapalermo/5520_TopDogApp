package edu.neu.madcourse.topdog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.neu.madcourse.topdog.DatabaseObjects.Walk;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerHolder> {
    private final ArrayList<Walk> walkHistoryList;

    public RecyclerAdapter(ArrayList<Walk> walkHistoryList) {
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

//        DecimalFormat numberFormat = new DecimalFormat("#.00");
//        String finalWalkDurationStr = numberFormat.format(finalWalkDurationMin);

        holder.walkDuration.setText(String.valueOf(minutes) + "m " + String.valueOf(seconds) + "s");
//        holder.walkDuration.setText(String.valueOf(currentItem.getWalkDuration()) + " minutes");
        holder.walkDistance.setText(String.valueOf(currentItem.getFinalDistance()) + " meters");
        holder.walkCount.setText("Walk " + (position+1));

        System.out.println("WALKDATE: " + currentItem.getLogDate());
        holder.walkDate.setText(currentItem.getLogDate());
    }

    @Override
    public int getItemCount() {
        return walkHistoryList.size();
    }
}