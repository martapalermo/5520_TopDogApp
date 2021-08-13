package edu.neu.madcourse.topdog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import edu.neu.madcourse.topdog.DatabaseObjects.User;
import edu.neu.madcourse.topdog.DatabaseObjects.Walk;

public class MyStats extends AppCompatActivity {

    String username;
    private ArrayList<Walk> walkHistory;
    private ArrayList<Walk> walkHistoryList = new ArrayList<>();
    RecyclerAdapter recyclerAdapter;
    private DatabaseReference mDatabase;

    private static final String KEY_OF_INSTANCE = "KEY_OF_INSTANCE";
    private static final String NUMBER_OF_ITEMS = "NUMBER_OF_ITEMS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_stats);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        username = getIntent().getStringExtra(MainActivity.USERKEY);
        Query query = mDatabase.child("USERS").child(username).child("walkList");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    GenericTypeIndicator<ArrayList<Walk>> t =
                            new GenericTypeIndicator<ArrayList<Walk>>() {};
                    walkHistory = dataSnapshot.getValue(t);
                    populateItemList();
                } else {
                    Toast.makeText(MyStats.this, "No walks yet!", Toast.LENGTH_SHORT).show();
                }
            }
            /**
             * This method will be triggered in the event that this listener either failed at the server, or
             * is removed as a result of the security and Firebase Database rules. For more information on
             * securing your data, see: <a
             * href="https://firebase.google.com/docs/database/security/quickstart" target="_blank"> Security
             * Quickstart</a>
             *
             * @param error A description of the error that occurred
             */
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        init(savedInstanceState);
    }

    private void populateItemList() {
        for (Walk walk : walkHistory) {
            Walk w = new Walk(walk.finalDistance, walk.walkDuration);
            walkHistoryList.add(w);
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    private void init(Bundle savedInstanceState) {
        initialItemData(savedInstanceState);
        createRecyclerView();
    }



    private void createRecyclerView() {

        RecyclerView recyclerView = findViewById(R.id.recycler_view_walks);
        System.out.println(recyclerView);
        RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(rLayoutManager);

        recyclerView.setHasFixedSize(true);
        recyclerAdapter = new RecyclerAdapter(walkHistory);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void initialItemData(Bundle savedInstanceState) {

        if (savedInstanceState != null && savedInstanceState.containsKey(NUMBER_OF_ITEMS)) {
            if (walkHistoryList == null || walkHistoryList.size() == 0) {
                int size = savedInstanceState.getInt(NUMBER_OF_ITEMS);
                for (int i = 0; i < size; i++) {
                    Long walkDistance = Long.parseLong(savedInstanceState.getString(KEY_OF_INSTANCE + i + "0"));
                    Double walkDuration = Double.parseDouble(savedInstanceState.getString(KEY_OF_INSTANCE + i + "1"));
                    Walk walkCard = new Walk(walkDistance, walkDuration);
                    walkHistoryList.add(walkCard);
                }
            }
        }
    }

    protected void onSaveInstanceState(@NonNull Bundle outState) {
        int size = walkHistoryList == null ? 0 : walkHistoryList.size();
        outState.putInt(NUMBER_OF_ITEMS, size);

        for (int i = 0; i < size; i++) {
            // outState.putString(KEY_OF_INSTANCE + i + "0", itemList.get(i).getLinkName());
            // outState.putString(KEY_OF_INSTANCE + i + "1", itemList.get(i).getLinkURL());
        }
        super.onSaveInstanceState(outState);
    }





    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra(MainActivity.USERKEY,username);
        startActivity(intent);
        finish();
    }
}
