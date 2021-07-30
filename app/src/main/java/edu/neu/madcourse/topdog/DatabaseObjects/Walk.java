package edu.neu.madcourse.topdog.DatabaseObjects;

import java.io.Serializable;

public class Walk implements Serializable {
    private static int walkCounter;

    public Walk() {
        walkCounter = 1;
    }

    public static int getWalkCounter() { return walkCounter;}

    public static void setWalkCounter() {
        walkCounter += 1;
    }
}
