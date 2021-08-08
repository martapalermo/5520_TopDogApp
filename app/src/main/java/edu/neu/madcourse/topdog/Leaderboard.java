package edu.neu.madcourse.topdog;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import edu.neu.madcourse.topdog.DatabaseObjects.LeaderboardEntry;

/**
 * needs to be dynamic - and need to figure out how to add user profile images in list view
 */

public class Leaderboard extends AppCompatActivity {

    private DatabaseReference mDatabase;
    ArrayList<LeaderboardEntry> leaderboardEntries = new ArrayList<>();
    ArrayList<String> currentLeaders = new ArrayList<>();
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        listView = findViewById(R.id.leaderboard_listview);
        ArrayAdapter arrayAdapter =
                new ArrayAdapter(this, android.R.layout.simple_list_item_1, currentLeaders);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");
        String username = getIntent().getStringExtra("CURRENT_USER");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @org.jetbrains.annotations.NotNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    leaderboardEntries.add(new LeaderboardEntry(childSnapshot.getKey(),childSnapshot.child("walks").getChildrenCount()));
                }
                leaderboardEntries.sort((o1, o2) -> Long.compare(o2.getNumberWalks(), o1.getNumberWalks()));

                for (int i = 0; i < leaderboardEntries.size(); i++) {
                    currentLeaders.add(leaderboardEntries.get(i).getUsername());
                }
                System.out.println(currentLeaders);
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Leaderboard.this, "clicked item: " + id + " " + currentLeaders.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
