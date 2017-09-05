package com.example.osamakhalid.realtimeauctionsystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.osamakhalid.realtimeauctionsystem.Classes.Bid;
import com.example.osamakhalid.realtimeauctionsystem.Classes.Post;
import com.example.osamakhalid.realtimeauctionsystem.Classes.Won;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AuctioneerBiddingScreen extends AppCompatActivity {
    private ListView myListView;
    private TextView title,description,initialBid,winner;
    private BiddingAdapter myAdapter;
    private List<Bid> bids= new ArrayList<Bid>();
    private DatabaseReference wonRef,bidRef;
    private Post setPost;
    private String winnerUsername="No Winner";
    private ChildEventListener childEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auctioneer_bidding_screen);
        setPost = (Post) getIntent().getSerializableExtra("send post");
        myListView=(ListView) findViewById(R.id.listview_auc_bidding_screen);
        title=(TextView) findViewById(R.id.title__auc_bidding_screen);
        description=(TextView) findViewById(R.id.description_auc_bidding_screen);
        initialBid=(TextView) findViewById(R.id.initial_bid_auc_bidding_screen);
        winner=(TextView) findViewById(R.id.winner);
        wonRef= FirebaseDatabase.getInstance().getReference().child("Won");
        bidRef=FirebaseDatabase.getInstance().getReference().child("Bids");
        myAdapter= new BiddingAdapter(AuctioneerBiddingScreen.this,bids);
        myListView.setAdapter(myAdapter);
        wonRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Won checkWon=snapshot.getValue(Won.class);
                    if(checkWon.getPostkey().equals(setPost.getPostKey())){
                        winnerUsername=checkWon.getUserid();
                    }
                }
                winner.setText(winnerUsername);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Bid checkBid= dataSnapshot.getValue(Bid.class);
                if(checkBid.getPostkey().equals(setPost.getPostKey())){
                    bids.add(checkBid);
                    myAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        bidRef.addChildEventListener(childEventListener);
    }
}
