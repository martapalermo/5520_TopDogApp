package edu.neu.madcourse.topdog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.neu.madcourse.topdog.DatabaseObjects.User;

public class SignUp extends AppCompatActivity {

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersReference = mDatabase.child("USERS");

        username = getIntent().getStringExtra(MainActivity.USERKEY);
        String token = getIntent().getStringExtra(MainActivity.TOKEN);

        User currentUser = new User(username, token);
        mDatabase.child(username).setValue(currentUser);

        openHomepage();
    }


    public void openHomepage() {
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra(MainActivity.USERKEY, username);
        startActivity(intent);
    }
}