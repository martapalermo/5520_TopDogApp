package edu.neu.madcourse.topdog.DatabaseObjects;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
This is a utility class for fetching User information from the database off the main UI thread.
Use as follows:

 User user = new FetchDBInfoUtil().getUser("theUsername");

 where "theUsername" is a String containing the username as it appears exactly in the database
 */
public class FetchDBUserUtil {

    User returnUser = new User();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public User getUser(String username) {
        DatabaseReference mDatabaseAtUser = FirebaseDatabase.getInstance().getReference().child("USERS").child(username);
        String url = mDatabaseAtUser.toString() + ".json";

        //Make an async request to the Database
        Future<User> futureUser = executor.submit(() -> {
            RunnableFetch fetchRequest = new RunnableFetch(url);
            fetchRequest.run();
            return returnUser;
        });

        //Wait until the user has been set (in the future, because its on a separate thread)
        while(!futureUser.isDone()) {
            System.out.println("WAITING ON FETCH...");
            try {
                //Try to minimize the sleep time or else UX will suffer:
                Thread.sleep(150);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        return this.returnUser;
    }

    //Runnable class to perform database access off the main UI thread
    public class RunnableFetch implements Runnable {

        String url;

        public RunnableFetch(String url) {
            this.url = url;
        }

        @Override
        public void run(){
            try {
                //Connect to the database at the endpoint of the data you need (the user)
                URL endpoint = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) endpoint.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                // Read response
                InputStream inputStream = conn.getInputStream();
                JSONObject userAsJson = new JSONObject(convertStreamToString(inputStream));
                returnUser = User.deserialize(userAsJson);

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

