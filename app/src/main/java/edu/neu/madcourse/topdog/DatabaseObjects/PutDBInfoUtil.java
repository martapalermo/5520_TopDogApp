package edu.neu.madcourse.topdog.DatabaseObjects;

import com.google.firebase.database.DatabaseReference;

/**
 This is a utility class for putting info into the firebase database off the main thread.
 Use as follows:

 new PutDBInfoUtil().setValue(databaseRef, value);

 For example, if you want to update a user's walk info, the database reference would be:
 FirebaseDatabase.getInstance().getReference().child("USERS").child("theUsername");

 ...and the value would be the updated User object you want to place in the database.
 */
public class PutDBInfoUtil {

    public void setValue(DatabaseReference databaseRef, Object value) {
        RunnablePut putRequest = new RunnablePut(databaseRef, value);
        new Thread(putRequest).start();
    }

    public class RunnablePut implements Runnable {

        DatabaseReference dbRef;
        Object value;

        public RunnablePut(DatabaseReference dbRef, Object value) {
            this.dbRef = dbRef;
            this.value = value;
        }
        @Override
        public void run(){
           dbRef.setValue(value);
        }
    }
}
