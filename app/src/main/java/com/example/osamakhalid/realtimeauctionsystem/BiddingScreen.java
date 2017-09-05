package com.example.osamakhalid.realtimeauctionsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.media.Image;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.osamakhalid.realtimeauctionsystem.Classes.Bid;
import com.example.osamakhalid.realtimeauctionsystem.Classes.Post;
import com.example.osamakhalid.realtimeauctionsystem.Classes.Won;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BiddingScreen extends AppCompatActivity {
    private Post setPost;
    private TextView title, description, hours, mins, sec, initialBid;
    private ImageView postImage;
    private ListView myListView;
    private java.text.SimpleDateFormat mdformat;
    private Date startDate, endDate;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference reference = null;
    private long elapsedMinutes, elapsedHours, elapsedSeconds;
    private BiddingAdapter myadapter;
    private List<Bid> bids = new ArrayList<Bid>();
    private DatabaseReference bidReference;
    private ChildEventListener childEventListener;
    private Button bidButton;
    private EditText biddingValue;
    long diff = 0;
    private boolean onDestroyFlag = false;
    List<Bid> checkWinnerBids = new ArrayList<Bid>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bidding_screen);
        Intent i = getIntent();
        setPost = (Post) getIntent().getSerializableExtra("send post");
        title = (TextView) findViewById(R.id.title_bidding_screen);
        description = (TextView) findViewById(R.id.description_bidding_screen);
        hours = (TextView) findViewById(R.id.hours_bidding_screen);
        mins = (TextView) findViewById(R.id.mins_bidding_screen);
        sec = (TextView) findViewById(R.id.sec_bidding_screen);
        initialBid = (TextView) findViewById(R.id.initial_bid_bidding_screen);
        postImage = (ImageView) findViewById(R.id.image_bidding_screen);
        myListView = (ListView) findViewById(R.id.listview_bidding_screen);
        bidButton = (Button) findViewById(R.id.bid_button_bidding_screen);
        biddingValue = (EditText) findViewById(R.id.bidding_value_bidding_screen);
        myadapter = new BiddingAdapter(BiddingScreen.this, bids);
        bidReference = FirebaseDatabase.getInstance().getReference().child("Bids");
        myListView.setAdapter(myadapter);
        myListView.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
        mdformat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        title.setText(setPost.getTitle());
        description.setText(setPost.getDescription());
        initialBid.setText(setPost.getInitialbid());
        reference = storage.getReferenceFromUrl(setPost.getPhotouri());
        Glide.with(BiddingScreen.this).using(new FirebaseImageLoader()).load(reference).into(postImage);
        try {
            startDate = mdformat.parse(setPost.getStarttime() + ":00");
            endDate = mdformat.parse(setPost.getEndtime() + ":00");

        } catch (Exception c) {
            c.printStackTrace();
        }
        System.out.println("checkff end=" + setPost.getEndtime());
        System.out.println("checkff start=" + setPost.getStarttime());
        showTimer(startDate, endDate);
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Bid checkBid = dataSnapshot.getValue(Bid.class);
                if (checkBid.getPostkey().equals(setPost.getPostKey())) {
                    bids.add(checkBid);
                    myadapter.notifyDataSetChanged();
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
        bidReference.addChildEventListener(childEventListener);
        bidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Long.parseLong(initialBid.getText().toString()) >= Long.parseLong(biddingValue.getText().toString())) {
                    biddingValue.setText("");
                    Toast.makeText(BiddingScreen.this, "Entered bid is less than or equal to initial bid", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i = 0; i < bids.size(); i++) {
                    if (Long.parseLong(bids.get(i).getBid()) >= Long.parseLong(biddingValue.getText().toString())) {
                        biddingValue.setText("");
                        Toast.makeText(BiddingScreen.this, "Entered bid is less than or equal to current greatest bid", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Bid sendBid = new Bid(FirebaseAuth.getInstance().getCurrentUser().getEmail(), biddingValue.getText().toString(), setPost.getPostKey());
                bidReference.push().setValue(sendBid);
                biddingValue.setText("");
            }
        });
    }

    public void showTimer(Date start, Date end) {
        Calendar cal = Calendar.getInstance();
        Date currentdate = null;
        try {
            currentdate = mdformat.parse(mdformat.format(cal.getTime()));
        } catch (Exception c) {
            c.printStackTrace();
        }
        System.out.println("checkff end= " + end + " current= " + currentdate);
        diff = end.getTime() - currentdate.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        System.out.println("Checkff difference" + diff);
        long elapsedDays = diff / daysInMilli;
        diff = diff % daysInMilli;
        System.out.println("Checkff difference=" + diff + " days=" + elapsedDays);
        elapsedHours = diff / hoursInMilli;
        diff = diff % hoursInMilli;
        System.out.println("Checkff difference=" + diff + " hours=" + elapsedHours);
        elapsedMinutes = diff / minutesInMilli;
        diff = diff % minutesInMilli;
        System.out.println("Checkff difference=" + diff + " mins=" + elapsedMinutes);
        elapsedSeconds = diff / secondsInMilli;
        System.out.println("Checkff difference=" + diff + " sec=" + elapsedSeconds);

        hours.setText(String.valueOf(elapsedHours));
        mins.setText(String.valueOf(elapsedMinutes));
        final CountDownTimer mCountDown = new CountDownTimer(diff, 1000) {
            @Override
            public void onTick(long l) {
                sec.setText(String.valueOf(l / 1000));
            }

            @Override
            public void onFinish() {
                sec.setText("0");
                if (elapsedMinutes == 0) {
                    if (elapsedHours == 0) {
                        Toast.makeText(BiddingScreen.this, "Bidding time ends", Toast.LENGTH_SHORT).show();
                        Query query = bidReference;
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                checkWinnerBids.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Bid winnerBid = snapshot.getValue(Bid.class);
                                    if (winnerBid.getPostkey().equals(setPost.getPostKey())) {
                                        checkWinnerBids.add(winnerBid);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        if (checkWinnerBids.size() > 0) {
                            Bid max = checkWinnerBids.get(0);
                            for (int i = 0; i < checkWinnerBids.size(); i++) {
                                if (Long.parseLong(max.getBid()) < Long.parseLong(checkWinnerBids.get(i).getBid())) {
                                    max = checkWinnerBids.get(i);
                                }
                            }
                            if(onDestroyFlag==false) {
                                DatabaseReference wonAuctionsRef = FirebaseDatabase.getInstance().getReference().child("Won");
                                Won wonAuction = new Won(max.getUsername(), setPost.getPostKey());
                                wonAuctionsRef.push().setValue(wonAuction);
                                System.out.println("checkff setValue(Won) bidding screen");
                            }
                        }
                        Intent i = new Intent(BiddingScreen.this, LoginScreen.class);
                        i.putExtra("type", "Bidder");
                        startActivity(i);
                        finish();
                        return;
                    } else {
                        --elapsedHours;
                        elapsedMinutes = 59;
                    }
                } else
                    --elapsedMinutes;
                hours.setText(String.valueOf(elapsedHours));
                mins.setText(String.valueOf(elapsedMinutes));
                show2ndTimer();
            }
        }.start();
    }

    public void show2ndTimer() {
        final CountDownTimer mCountDown = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                sec.setText(String.valueOf(l / 1000));
            }

            @Override
            public void onFinish() {
                sec.setText("0");
                if (elapsedMinutes == 0) {
                    if (elapsedHours == 0) {
                        Toast.makeText(BiddingScreen.this, "Bidding time ends", Toast.LENGTH_SHORT).show();
                        Query query = bidReference;
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                List<Bid> checkWinnerBids = new ArrayList<Bid>();
                                int count = 0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Bid winnerBid = snapshot.getValue(Bid.class);
                                    if (winnerBid.getPostkey().equals(setPost.getPostKey())) {
                                        checkWinnerBids.add(winnerBid);
                                    }
                                }
                                if (checkWinnerBids.size() > 0) {
                                    Bid max = checkWinnerBids.get(0);
                                    for (int i = 0; i < checkWinnerBids.size(); i++) {
                                        if (Long.parseLong(max.getBid()) < Long.parseLong(checkWinnerBids.get(i).getBid())) {
                                            max = checkWinnerBids.get(i);
                                        }
                                    }
                                    if (onDestroyFlag == false) {
                                        DatabaseReference wonAuctionsRef = FirebaseDatabase.getInstance().getReference().child("Won");
                                        Won wonAuction = new Won(max.getUsername(), setPost.getPostKey());
                                        wonAuctionsRef.push().setValue(wonAuction);
                                        System.out.println("checkff setValue(Won) bidding screen");
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        Intent i = new Intent(BiddingScreen.this, LoginScreen.class);
                        i.putExtra("type", "Bidder");
                        startActivity(i);
                        finish();
                        this.cancel();
                        return;
                    } else {
                        --elapsedHours;
                        elapsedMinutes = 59;
                    }
                } else
                    --elapsedMinutes;
                hours.setText(String.valueOf(elapsedHours));
                mins.setText(String.valueOf(elapsedMinutes));
                this.start();
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onDestroyFlag = true;
        Intent i = new Intent(BiddingScreen.this, LoginScreen.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("type", "Bidder");
        startActivity(i);
        finish();
    }
}
