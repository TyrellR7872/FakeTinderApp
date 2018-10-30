package my.own.FakeTinderApp;

import android.support.annotation.NonNull;

import java.util.ArrayList;

public class Subject {
    private int leftSwipes, rightSwipes, subjectId;
    private ArrayList<String> swipeHistory;
    private ArrayList<Integer> timeHistory;


    Subject(int id){
        leftSwipes = 0;
        rightSwipes = 0;
        subjectId = id;
        swipeHistory = new ArrayList<>();
        timeHistory = new ArrayList<>();

    }

    void swipeLeft(){
        leftSwipes++;
        swipeHistory.add("L");
    }

    void swipeRight(){
        rightSwipes++;
        swipeHistory.add("R");
    }

    void clear(){
        leftSwipes = 0;
        rightSwipes = 0;
        swipeHistory.clear();
        timeHistory.clear();
    }

    void addTime(int seconds){
        timeHistory.add(seconds);
    }

    @NonNull
    public String toString(){
        return ""+subjectId+", "+leftSwipes+", "+rightSwipes+", "+swipeHistory.toString().replaceAll("[^a-zA-Z, ]", "")+", "+
                timeHistory.toString().replaceAll("[^a-zA-Z0-9, ]", "");
    }

}
