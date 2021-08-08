package edu.neu.madcourse.topdog.DatabaseObjects;

import com.google.firebase.database.DatabaseReference;

import org.json.JSONObject;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

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

    public JSONObject getResults(DatabaseReference databaseRef) {
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
