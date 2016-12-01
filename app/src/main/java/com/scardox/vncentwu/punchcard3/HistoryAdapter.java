package com.scardox.vncentwu.punchcard3;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.Image;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

/**
 * Created by vncentwu on 11/9/2016.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{

    private List<HistoryEntry> mDataset;


    View lastView;


    public static class ViewHolder extends RecyclerView.ViewHolder{
        //public TextView mTextView;
        public TextView descripText;
        public TextView pointText;
        public TextView companyText;

        public ViewHolder(View v){
            super(v);
            //mTextView = v;
            descripText = (TextView) v.findViewById(R.id.history_descrip);
            pointText = (TextView) v.findViewById(R.id.history_amount);
            companyText = (TextView) v.findViewById(R.id.company_name);

        }
    }

    public HistoryAdapter(List<HistoryEntry> myDataset){

        mDataset = myDataset;
        Log.d("data set", myDataset.toString());
    }

    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = (LinearLayout) LayoutInflater.from(parent.getContext()).
                inflate(R.layout.history_entry_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        Log.d("position", "" + position);
        Log.d("size", "" + mDataset.size());
        HistoryEntry entry = mDataset.get(position);
        holder.descripText.setText(entry.description);
        if(entry.gainedPoints) {
            holder.pointText.setText("+" + entry.points);
            holder.pointText.setTextColor(Color.parseColor("#006300"));
        }
        else{
            holder.pointText.setText("-" +entry.points);
            holder.pointText.setTextColor(Color.parseColor("#7F0000"));
        }
        holder.companyText.setText(entry.companyName);

    }

    @Override
    public int getItemCount(){
        return mDataset.size();
    }

}
