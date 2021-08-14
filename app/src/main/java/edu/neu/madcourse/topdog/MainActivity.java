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

import edu.neu.madcourse.topdog.DatabaseObjects.PutDBInfoUtil;

import static android.content.ContentValues.TAG;

//Log in page
public class MainActivity extends AppCompatActivity {

    /* Used to transfer the current user's username from one activity to another
    Search for " intent.putExtra(USERKEY, currentUserName); " for use example */
    final static String USERKEY = "CURRENT_USER";
    final static String TOKENKEY = "CURRENT_TOKEN";

    private DatabaseReference mDatabase;
    private String username;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");

        TextView usernameInput = findViewById(R.id.username_input);
        ImageButton logInButton = findViewById(R.id.signIn_btn);
        Button signUpButton = findViewById(R.id.signUp_btn_on_main_activity);

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

        logInButton.setOnClickListener(v -> {
            username = usernameInput.getText().toString();
            if (username.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter a username",
                        Toast.LENGTH_SHORT).show();
            } else {
                checkUserExists_AndLogIn();// launches home page if user does exist
            }
        });

        signUpButton.setOnClickListener(v -> {
            username = usernameInput.getText().toString();
            openSignUpPage();
        });
    }

    public void checkUserExists_AndLogIn() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChild(username)) {
                    Toast.makeText(MainActivity.this, "Oops! Incorrect username. " +
                                    "User does not exist.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    //if another device log-ins to the account this token will now be updated to that phone
                    new PutDBInfoUtil().setValue(mDatabase.child(username).child("token"), token);
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

    public void openSignUpPage() {
        Intent intent = new Intent(MainActivity.this, SignUp.class);
        intent.putExtra(USERKEY, username);
        intent.putExtra(TOKENKEY, token);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        //This is intentionally left empty so that when the user is at the homepage and clicks
        //the back button, they do not go anywhere, giving the impression that the homepage is
        //the top of the navigation tree.
    }
}
