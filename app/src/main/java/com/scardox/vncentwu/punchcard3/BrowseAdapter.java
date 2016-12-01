package com.scardox.vncentwu.punchcard3;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Base64;
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

public class BrowseAdapter extends RecyclerView.Adapter<BrowseAdapter.ViewHolder> implements ContentManager.MakePurchaseListener{

    private List<CompanyEntry> mDataset;

    public int lastPosition;
    CompanyEntry.Promotion lastPromotion;

    View lastView;

    @Override
    public void MakePurchaseCallback(String code) {

    }




    public static class ViewHolder extends RecyclerView.ViewHolder{
        //public TextView mTextView;
        public TextView title;
        public TextView points;
        public ViewGroup linearLayout;
        public ImageView img;

        public ViewHolder(View v){
            super(v);
            //mTextView = v;
            title = (TextView) v.findViewById(R.id.infoText);
            points = (TextView) v.findViewById(R.id.pointsText);
            linearLayout =(ViewGroup) v.findViewById(R.id.linear_layout2);
            img = (ImageView) v.findViewById(R.id.image1);

        }
    }

    public BrowseAdapter(List<CompanyEntry> myDataset){

        mDataset = myDataset;
        Log.d("data set", myDataset.toString());
    }

    @Override
    public BrowseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = (LinearLayout) LayoutInflater.from(parent.getContext()).
                inflate(R.layout.card_entry_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        //Log.d("position", "" + position);
        //Log.d("size", "" + mDataset.size());
        holder.title.setText(mDataset.get(position).companyName);
        holder.points.setText("" + mDataset.get(position).points);

        Log.d("size of promotions", "" + mDataset.get(position).promotions.size());

        holder.linearLayout.removeAllViews();

        ImageView img = (ImageView) holder.img;

        if(!mDataset.get(position).pictureBytes.equals("None")){
            byte[] data = Base64.decode(mDataset.get(position).pictureBytes, Base64.DEFAULT);
            img.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
        }
        else if(mDataset.get(position).picID != -1){
            img.setImageResource(mDataset.get(position).picID);
        }


        for(int i = 0; i < mDataset.get(position).promotions.size(); i++)
        {
            CompanyEntry.Promotion promotion = mDataset.get(position).promotions.get(i);
            Log.d("promotion info", promotion.description);
            View promotionBar = LayoutInflater.from(holder.linearLayout.getContext()).inflate(R.layout.promotion_bar, holder.linearLayout, false);
            //lastView = promotionBar;
            promotionBar.setTag(R.string.lastPosition, position);
            promotionBar.setTag(R.string.lastPromotion, promotion);
            final BrowseAdapter browser = this;
            promotionBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = (int) v.getTag(R.string.lastPosition);
                    lastView = v;
                    CompanyEntry.Promotion prom = (CompanyEntry.Promotion) v.getTag(R.string.lastPromotion);

                    if(mDataset.get(pos).points >= prom.pointsNeeded){
                        lastPromotion = prom;
                        lastPosition = pos;

                        TextView title = (TextView) v.findViewById(R.id.promotion_description);
                        final String descrip = title.getText().toString();

                        new AlertDialog.Builder(v.getContext())
                                .setTitle("Deal from " + mDataset.get(lastPosition).companyName + "!")
                                .setMessage("Do you want " + title.getText().toString() + " for " + lastPromotion.pointsNeeded + " points?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        mDataset.get(lastPosition).points -= lastPromotion.pointsNeeded;
                                        String code = Long.toHexString(Double.doubleToLongBits(Math.random())).substring(0, 6);
                                        ContentManager.getInstance().makePurchase(code, mDataset.get(lastPosition), descrip, lastPromotion.pointsNeeded, browser);

                                        notifyDataSetChanged();

                                        Snackbar snackbar = Snackbar
                                                .make(lastView, "Show the cashier: " + code, Snackbar.LENGTH_INDEFINITE)
                                                .setAction("DONE", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        Snackbar snackbar1 = Snackbar.make(view, "Enjoy!", Snackbar.LENGTH_SHORT);
                                                        snackbar1.show();
                                                    }
                                                });

                                        snackbar.show();


                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(R.drawable.ic_card_3)
                                .show();
                    }

                }
            });

            TextView description = (TextView) promotionBar.findViewById(R.id.promotion_description);
            description.setText(promotion.description);
            ProgressBar prog = (ProgressBar) promotionBar.findViewById(R.id.promotion_progress);
            if(promotion.pointsNeeded == 0)
            {
                prog.setMax(1);
                prog.setProgress(1);
            }
            else{
                prog.setMax(promotion.pointsNeeded);
                prog.setProgress(mDataset.get(position).points);
            }

            TextView pointsDescrip = (TextView) promotionBar.findViewById(R.id.point_info);
            String pointsText = "";
            if(mDataset.get(position).points >= promotion.pointsNeeded){
                pointsText = "GET IT";
                pointsDescrip.setTextColor(Color.parseColor("#d10000"));
            }
            else
                pointsText = "" + mDataset.get(position).points + "/" + promotion.pointsNeeded;
            pointsDescrip.setText(pointsText);

            holder.linearLayout.addView(promotionBar);
        }

    }

    @Override
    public int getItemCount(){
        return mDataset.size();
    }

}
