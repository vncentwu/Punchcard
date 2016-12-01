package com.scardox.vncentwu.punchcard3;

/**
 * Created by vncentwu on 11/30/2016.
 */



import android.util.Log;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.data;
import static android.R.attr.name;

public class ContentManager {

    public interface GetPointListener{
        void GetPointCallback(List<PointPair> points);
    }

    public interface GetCompanyEntryListener{
        void GetCompanyEntryCallback(List<CompanyEntry> entries);
    }

    public interface EnterCodeListener{
        void EnterCodeCallBack(String company, int points);
    }

    public interface GetHistoryListener{
        void GetHistoryCallback(List<HistoryEntry> entries);
    }

    public interface MakePurchaseListener{
        void MakePurchaseCallback(String code);
    }

    public static ContentManager instance;
    protected DatabaseReference userDB;
    protected DatabaseReference contentDB;
    protected DatabaseReference codeDB;
    protected DatabaseReference redeemDB;

    private static final String DATAREF = "Data";

    public String username;

    public void connect(String username){
        this.username = username;
        contentDB = FirebaseDatabase.getInstance().getReference(DATAREF);
        userDB = FirebaseDatabase.getInstance().getReference(username.replaceAll("\\.", "@"));
        codeDB = FirebaseDatabase.getInstance().getReference("Codes");
        redeemDB = FirebaseDatabase.getInstance().getReference("RedeemCodes");
    }


    public void getCompanyEntries(final GetCompanyEntryListener listener){

        if(contentDB == null){
            ContentManager.getInstance().connect(username);
        }
        Query query = contentDB;
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<CompanyEntry> l = new ArrayList<CompanyEntry>();
                        for (DataSnapshot data: dataSnapshot.getChildren()) {
                            //Getting the data from snapshot

                            String companyName = data.getKey();
                            String bytes = (String) data.child("Picture").getValue();
                            if(bytes==null)
                                bytes="None";


                            CompanyEntry companyEntry = new CompanyEntry(companyName, 0);
                            companyEntry.pictureBytes = bytes;
                            Map<String, Object> promotions = (HashMap<String,Object>) data.child("Promotions").getValue();
                            for (Map.Entry<String, Object> entry : promotions.entrySet()) {
                                Map<String, Object> promotionData = (HashMap<String,Object>) entry.getValue();
                                String description = (String) promotionData.get("Description");
                                int points = ((Long) promotionData.get("Points")).intValue();

                                Log.d("data", description + " " + points);
                                companyEntry.addPromotion(description, points);
                            }

                            l.add(companyEntry);
                        }
                        listener.GetCompanyEntryCallback(l);
                    }
                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Log.d("erg", "Date query cancelled");
                    }
                });
    }

    public class PointPair{

        public int points;
        public String companyName;

        public PointPair(String name, int points){
            this.points = points;
            companyName = name;
        }
    }

    public void makePurchase(final String code, final CompanyEntry entry, final String descrip, final int pointsNeeded, final MakePurchaseListener listener){
        final String name = entry.companyName;
        if(userDB == null || codeDB == null || redeemDB == null)
            connect(username);

        Query query = userDB.child("Points");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    if(data.getKey().equals(name)){
                        Log.d("wefwef", "we're getting there");
                        int amount = data.getValue(Integer.class);
                        userDB.child("Points").child(name).setValue(amount - pointsNeeded);
                        Map<String, Object> historyData = new HashMap<String, Object>();
                        historyData.put("Company", entry.companyName);
                        historyData.put("Date", "12/1/16");
                        historyData.put("Description", descrip);
                        historyData.put("Points", -pointsNeeded);
                        userDB.child("History").push().setValue(historyData);


                        Map<String, Object> codeData = new HashMap<String, Object>();
                        codeData.put("Company", entry.companyName);
                        codeData.put("Code", code.toLowerCase());
                        codeData.put("Description", descrip);
                        codeData.put("Claimed", 0);
                        redeemDB.push().setValue(codeData);
                        listener.MakePurchaseCallback(code);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void enterCode(String code, final EnterCodeListener listener){
        Query query = codeDB.orderByChild("Code").equalTo(code);
        Log.d("wef", "Entering code" + code);
        if(codeDB == null || userDB == null){
            ContentManager.getInstance().connect(username);
        }
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("At least we're trying", "...arent we");
                        int amount = 0;
                        String company = null;
                        String key = null;
                        for (DataSnapshot data: dataSnapshot.getChildren()) {
                            Log.d("FOUND SOME", data.child("Company").getValue(String.class));
                            //Getting the data from snapshot
                            int claimed = ((Long) data.child("Claimed").getValue()).intValue();
                            Log.d("Claimed?????????????", "" + claimed);
                            if(claimed == 0){
                                key = data.getKey();
                                amount = data.child("Points").getValue(Integer.class);
                                company = data.child("Company").getValue(String.class);
                                break;
                            }
                            else
                                listener.EnterCodeCallBack("Looks like that code has already been claimed!", -1);

                        }
                        if(key != null)
                        {
                            final String fCompany = company;
                            final int fPoints = amount;
                            codeDB.child(key).child("Claimed").setValue(1);
                            userDB.child("Points").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Integer points = fPoints;
                                    if(dataSnapshot.hasChild(fCompany)){
                                        points += dataSnapshot.child(fCompany).getValue(Integer.class);
                                    }
                                    userDB.child("Points").child(fCompany).setValue(points);
                                    listener.EnterCodeCallBack(fCompany, fPoints);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            Map<String, Object> historyData = new HashMap<String, Object>();
                            historyData.put("Company", fCompany);
                            historyData.put("Date", "12/1/16");
                            historyData.put("Description", "No description");
                            historyData.put("Points", fPoints);
                            userDB.child("History").push().setValue(historyData);
                        }
                        else{
                            listener.EnterCodeCallBack("Sorry, we couldn't find that code!", -1);
                        }




                    }
                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Log.d("erg", "Date query cancelled");
                        listener.EnterCodeCallBack("Connection error. Please try again.", -1);
                    }
                });
    }

    public void getHistory(final GetHistoryListener listener){
        Query query = userDB.child("History");

        if(userDB == null){
            ContentManager.getInstance().connect(username);
        }
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<HistoryEntry> l = new ArrayList<HistoryEntry>();
                        for (DataSnapshot data: dataSnapshot.getChildren()) {
                            //Getting the data from snapshot
                            Integer points = data.child("Points").getValue(Integer.class);
                            String company = data.child("Company").getValue(String.class);
                            String date =data.child("Date").getValue(String.class);
                            String description = data.child("Description").getValue(String.class);
                            l.add(new HistoryEntry(company, Math.abs(points), points > 0, description));
                        }
                        listener.GetHistoryCallback(l);
                    }
                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Log.d("erg", "Date query cancelled");
                    }
                });
    }


    public void getPoints(final GetPointListener listener){
        Query query = userDB.child("Points");

        if(userDB == null){
            ContentManager.getInstance().connect(username);
        }
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<PointPair> l = new ArrayList<PointPair>();
                        for (DataSnapshot data: dataSnapshot.getChildren()) {
                            //Getting the data from snapshot
                            Integer points = data.getValue(Integer.class);
                            Log.d("points", points.toString());
                            l.add(new PointPair(data.getKey(), points));
                        }
                        listener.GetPointCallback(l);
                    }
                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Log.d("erg", "Date query cancelled");
                    }
                });
    }


    public static ContentManager getInstance(){
        if(instance == null){
            instance = new ContentManager();
        }
        return instance;
    }



}
