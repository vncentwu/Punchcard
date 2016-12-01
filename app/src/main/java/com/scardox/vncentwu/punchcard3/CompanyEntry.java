package com.scardox.vncentwu.punchcard3;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vncentwu on 11/9/2016.
 */

public class CompanyEntry {

    public String companyName;
    public int points;
    public List<Promotion> promotions;
    public int picID = R.drawable.tap_house;
    public String pictureBytes;


    public class Promotion{
        public String description;
        public int pointsNeeded;

        public Promotion(String description, int pointsNeeded){
            this.description = description;
            this.pointsNeeded = pointsNeeded;
        }

    }

    public CompanyEntry(String companyName, int points){
        this.companyName = companyName;
        this.points = points;
        //Log.d("Company created", companyName + " with po ints of: " + this.points;)
        promotions = new ArrayList<Promotion>();
    }

    public void addPromotion(String description, int pointsNeeded){

        Promotion prom = new Promotion(description, pointsNeeded);
        //Log.d("Size after adding", "" +promotions.size());
        promotions.add(prom);
        //Log.d("Size after adding", "" +promotions.size());
    }

    public void setPicture(int picID){
        this.picID = picID;
    }



}
