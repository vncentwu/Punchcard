package com.scardox.vncentwu.punchcard3;

/**
 * Created by vncentwu on 11/10/2016.
 */

public class HistoryEntry {

    public String companyName;
    public int points;
    public boolean gainedPoints;
    public String description;

    public HistoryEntry(String companyName, int points, boolean gainedPoints, String description){
        this.companyName = companyName;
        this.points = points;
        this.gainedPoints = gainedPoints;
        this.description = description;
    }



}



