package edu.neu.madcourse.topdog.DatabaseObjects;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import edu.neu.madcourse.topdog.MyStats;

/**
This is a utility class for fetching info from the database OFF THE MAIN THREAD
Use as follows:

 JsonObject desiredInformation = new FetchDBInfoUtil().getResults(databaseRef);

 For example, if you want all information associated with a particular user, the
 databaseRef in this case will be a reference to that user's branch in the database, like this:
 FirebaseDatabase.getInstance().getReference().child("USERS").child("theUsername");
 */
public class FetchDBInfoUtil {

    String url;
    JSONObject results = new JSONObject();
    User resultsTest = new User();


    public JSONObject getResults(DatabaseReference databaseRef) {
        Query query = databaseRef;

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    GenericTypeIndicator<User> t =
                            new GenericTypeIndicator<User>() {
                            };
                    System.out.println("\n\n+++++++++t.toString: " + t.toString());
                    System.out.println("\n\n+++++++++snapshot.toString(): " + snapshot.toString());
                    System.out.println("\n\n+++++++++snapshot.getValue(): " + snapshot.getValue());

                    resultsTest = snapshot.getValue(t);

                    System.out.println("\n\n+++++++++resultsTest: " + resultsTest.getDogName());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });




        url = databaseRef.toString() + ".json";
        RunnableFetch fetchRequest = new RunnableFetch();

        new Thread(fetchRequest).start();
        try {
            //to avoid a data race
            // TODO: come up with a better solution than this
            Thread.sleep(2000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        return this.results;
    }

    public class RunnableFetch implements Runnable {
        @Override
        public void run(){
            try {
                URL endpoint = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) endpoint.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                // Read response.
                InputStream inputStream = conn.getInputStream();
                results = new JSONObject(convertStreamToString(inputStream));

            } catch (Exception e) {
                System.out.println("JSON ERROR while fetching info from DB: " + e.toString());
            }
        }

        private String convertStreamToString(InputStream is) {
            Scanner s = new Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next().replace(",", ",\n") : "";
        }
    }
}
