package edu.neu.madcourse.topdog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

    /* Used to transfer the current user's username from one activity to another
    Search for " intent.putExtra(USERKEY, currentUserName); " for example */
    final static String USERKEY = "CURRENT_USER";
    final static String TOKEN = "CURRENT_TOKEN";

    private DatabaseReference mDatabase;
    private String username;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");

        TextView usernameInput = findViewById(R.id.username_input);
        ImageButton logInButton = findViewById(R.id.signIn_btn);
        Button signUpButton = findViewById(R.id.signUp_btn);


        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }
                // get new FCM registration token
                token = task.getResult();
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameInput.getText().toString();
                checkUserExists_LogIn();//Also launches home page if user does exist
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameInput.getText().toString();
                checkUserExists_SignUp();//Also leads to home page after user signs up
            }
        });
    }


    public void checkUserExists_LogIn() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChild(username)) {
                    Toast.makeText(MainActivity.this, "Oops! Incorrect username or password.",
                            Toast.LENGTH_LONG).show();
                } else {
                    //if another device log-ins to the account this token will now be updated to that phone
                    mDatabase.child(username).child("token").setValue(token);//update token of existing user
                    openHomepage();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    public void openHomepage() {
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra(USERKEY, username);
        startActivity(intent);
    }

    public void checkUserExists_SignUp() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChild(username)) {
                    openSignUpPage();
                } else {
                    Toast.makeText(MainActivity.this, "Oops! This username is already taken.",
                            Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    public void openSignUpPage() {
        Intent intent = new Intent(MainActivity.this, SignUp.class);
        intent.putExtra(USERKEY, username);
        intent.putExtra(TOKEN, token);
        startActivity(intent);
    }

}
