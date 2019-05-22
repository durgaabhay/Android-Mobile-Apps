package com.example.inclass09;

import java.util.ArrayList;

/**
 * Created by Durga Abayakumar on 3/26/2018.
 */

public class Markers {

    String title;
    ArrayList<Points> points;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Points> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Points> points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "Markers{" +
                "title='" + title + '\'' +
                ", points=" + points +
                '}';
    }
}
