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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import edu.neu.madcourse.topdog.DatabaseObjects.FetchDBInfoUtil;
import edu.neu.madcourse.topdog.DatabaseObjects.LeaderboardEntry;
import edu.neu.madcourse.topdog.DatabaseObjects.PutDBInfoUtil;
import edu.neu.madcourse.topdog.DatabaseObjects.User;
import edu.neu.madcourse.topdog.DatabaseObjects.Walk;

public class Leaderboard extends AppCompatActivity {

    private DatabaseReference mDatabase;
    ArrayList<LeaderboardEntry> leaderboardEntries = new ArrayList<>();
    ArrayList<String> currentLeaders = new ArrayList<>();
    ListView listView;
    ArrayList<String> tempList = new ArrayList<>();
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        listView = findViewById(R.id.leaderboard_listview);
        tempList = currentLeaders;
        ArrayAdapter arrayAdapter =
                new ArrayAdapter(this, android.R.layout.simple_list_item_1, currentLeaders);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");
        username = getIntent().getStringExtra(MainActivity.USERKEY);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                leaderboardEntries.clear();
                currentLeaders.clear();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    leaderboardEntries.add(new LeaderboardEntry(childSnapshot.getKey(),
                            Objects.requireNonNull(childSnapshot.child("dogName").getValue()).toString(),
                            childSnapshot.child("walkList").getChildrenCount()));
                }
                System.out.println(leaderboardEntries);
                leaderboardEntries.sort((o1, o2) -> Long.compare(o2.getNumberWalks(), o1.getNumberWalks()));

                for (int i = 0; i < leaderboardEntries.size(); i++) {
                    currentLeaders.add(leaderboardEntries.get(i).getDogName());
                }
                listView.setAdapter(arrayAdapter);
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                JSONObject userToGetPat = new FetchDBInfoUtil().getResults(mDatabase.child(leaderboardEntries.get(position).getUsername()));

                if(userToGetPat.toString().equals(username)) {
                    Toast.makeText(Leaderboard.this, "Can't pat yourself!", Toast.LENGTH_SHORT).show();
                }

                int displayPats = 0;

                try {
                    displayPats = Integer.parseInt(userToGetPat.getString("numPats"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                displayPats++;
                new PutDBInfoUtil().setValue(mDatabase.child(leaderboardEntries.get(position).getUsername()).child("numPats"), displayPats);
                Toast.makeText(Leaderboard.this, "You sent " + currentLeaders.get(position) + " a pat!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra(MainActivity.USERKEY, username);
        startActivity(intent);
    }

}
