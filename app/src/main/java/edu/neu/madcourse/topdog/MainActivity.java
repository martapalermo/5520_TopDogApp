package edu.neu.madcourse.topdog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import static android.content.ContentValues.TAG;

//Log in page
public class MainActivity extends AppCompatActivity {

    final static String USERKEY = "CURRENT_USER";

    private DatabaseReference mDatabase;
    private TextView usernameInput;
    public String username;
    protected String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // From XML
        usernameInput = findViewById(R.id.username_input);
        ImageButton logInButton = findViewById(R.id.signIn_btn);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }
                // get new FCM registration token
                token = task.getResult();
                //logs the token
                Log.d("FCMTEST", "Token: " + token);
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                username = usernameInput.getText().toString();
                checkUserExists(username);
                openHomepage(username);
            }
        });


    }

    public void checkUserExists(String username) {
        //accesses the reference list of users
        DatabaseReference userDataBaseRef = mDatabase.child("USERS");

        //adding listener which will run through the code when called
        userDataBaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            //this is the code that will be ran by the listener
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Snapshots are a localized memory storage. Instead of loading ALL data associated
                // with a certain child, it'll only give the immediate info at-hand i.e. the list
                // of users and not all the data associated with each user.
                // If user does not exist we will create a new user based on the username
                if (!snapshot.hasChild(username)) {
                    createUser(username);
                } else {
                    //update token of existing user
                    updateToken();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    public void createUser(String username) {
        User currentUser = new User(username, token);
        mDatabase.child("USERS").child(username).setValue(currentUser);
    }

    public void updateToken() {
        //if another device log-ins to the account this token will now be updated to that phone
        DatabaseReference tokenReference = mDatabase.child("USERS");
        tokenReference.child(MainActivity.this.usernameInput.getText().toString()).child("token").setValue(token);
    }

    public void openHomepage(String currentUserName) {
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra(USERKEY, currentUserName);
        startActivity(intent);
    }

}
