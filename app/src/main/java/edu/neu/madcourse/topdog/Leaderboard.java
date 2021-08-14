package edu.neu.madcourse.topdog;

import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.Objects;

import edu.neu.madcourse.topdog.DatabaseObjects.FetchDBUserUtil;
import edu.neu.madcourse.topdog.DatabaseObjects.LeaderboardEntry;
import edu.neu.madcourse.topdog.DatabaseObjects.PutDBInfoUtil;
import edu.neu.madcourse.topdog.DatabaseObjects.User;

public class Leaderboard extends AppCompatActivity {

    private String username;
    private DatabaseReference mDatabase;

    ArrayList<LeaderboardEntry> leaderboardEntries = new ArrayList<>();
    ArrayList<String> currentLeaderNames = new ArrayList<>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");
        username = getIntent().getStringExtra(MainActivity.USERKEY);

        listView = findViewById(R.id.leaderboard_listview);
        ArrayAdapter arrayAdapter =
                new ArrayAdapter(this, android.R.layout.simple_list_item_1, currentLeaderNames);

        //Populates the listView
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                leaderboardEntries.clear();
                currentLeaderNames.clear();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    leaderboardEntries.add(new LeaderboardEntry(childSnapshot.getKey(),
                            Objects.requireNonNull(childSnapshot.child("dogName").getValue()).toString(),
                            childSnapshot.child("walkList").getChildrenCount()));
                }

                leaderboardEntries.sort((o1, o2) -> Long.compare(o2.getNumberWalks(), o1.getNumberWalks()));

                for (int i = 0; i < leaderboardEntries.size(); i++) {
                    currentLeaderNames.add(leaderboardEntries.get(i).getDogName());
                }
                listView.setAdapter(arrayAdapter);
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

        //Performs the pats!
        listView.setOnItemClickListener((parent, view, position, id) -> {
            User userToGetPat = new FetchDBUserUtil().getUser(leaderboardEntries.get(position).getUsername());

            if(userToGetPat.getUsername().equals(username)) {
                Toast.makeText(Leaderboard.this, "Oops, Can't pat yourself!", Toast.LENGTH_SHORT).show();
                return;
            }

            int displayPats = userToGetPat.getNumPats();
            displayPats++;

            new PutDBInfoUtil().setValue(mDatabase.child(userToGetPat.getUsername()).child("numPats"), displayPats);
            Toast.makeText(Leaderboard.this, "You sent " + userToGetPat.getDogName() + " a pat!", Toast.LENGTH_SHORT).show();
        });
    }

    //Methods for handling the go Home functionality in the menu bar
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_homepage, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.homepage) {
            Intent intent = new Intent(this, HomePage.class);
            intent.putExtra(MainActivity.USERKEY, username);
            startActivity(intent);
        }
        return true;
    }
    ////////////////////////////////////////////////////////////

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra(MainActivity.USERKEY, username);
        startActivity(intent);
    }

}
