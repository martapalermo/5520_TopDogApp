package edu.neu.madcourse.topdog.DatabaseObjects;

import org.json.JSONObject;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class FetchInfoFromDatabse implements Runnable{
    String url;
    JSONObject results = new JSONObject();

    public FetchInfoFromDatabse(String url) {
        this.url = url;
    }

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
            System.out.println("JSON ERROR: " + e.toString());
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

    public JSONObject getResults() {
        return this.results;
    }
}
