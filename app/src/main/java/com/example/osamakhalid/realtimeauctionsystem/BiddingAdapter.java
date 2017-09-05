package com.example.osamakhalid.realtimeauctionsystem;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.osamakhalid.realtimeauctionsystem.Classes.Bid;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Osama Khalid on 8/20/2017.
 */

public class BiddingAdapter extends ArrayAdapter<Bid> {
    List<Bid> bids= new ArrayList<Bid>();
    private TextView username,bidValue;
    public BiddingAdapter(@NonNull Context context, List<Bid> bids) {
        super(context, R.layout.bidding_items,bids);
        this.bids=bids;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View customView=convertView;
        if(customView==null){
            customView= LayoutInflater.from(getContext()).inflate(R.layout.bidding_items,parent,false);
        }
        Bid bid=getItem(position);
        username=(TextView) customView.findViewById(R.id.bidder_username);
        bidValue=(TextView) customView.findViewById(R.id.bid_value);
        bidValue.setText(bid.getBid());
        username.setText(bid.getUsername());
        return customView;
    }
}
