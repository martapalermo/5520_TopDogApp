package edu.neu.madcourse.topdog;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * needs to be dynamic - and need to figure out how to add user profile images in list view
 */

public class Leaderboard extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        listView = findViewById(R.id.leaderboard_listview);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Cobalt");
        arrayList.add("Georgie");
        arrayList.add("Loki");
        arrayList.add("Nilou");

        ArrayAdapter arrayAdapter =
                new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(Leaderboard.this, "clicked item: " + id + " " + arrayList.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
