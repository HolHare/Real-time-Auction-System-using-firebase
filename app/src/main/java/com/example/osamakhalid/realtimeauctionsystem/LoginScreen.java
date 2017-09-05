package com.example.osamakhalid.realtimeauctionsystem;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.osamakhalid.realtimeauctionsystem.Classes.Bid;
import com.example.osamakhalid.realtimeauctionsystem.Classes.Post;
import com.example.osamakhalid.realtimeauctionsystem.Classes.Won;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import layout.FutureAuctions;
import layout.LiveAuctions;
import layout.MyAds;
import layout.PostAds;
import layout.WinAuctions;

public class LoginScreen extends AppCompatActivity implements
        ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private FirebaseAuth firebaseAuth;
    public String type;
    private ActionBar actionBar;
    private String[] tabs = {"My Ads", "Auctioned Ads", "Post Ads"};
    private DatabaseReference postRef, bidRef, wonRef;
    Calendar calendar;
    private SimpleDateFormat mdformat;
    Date currentDate = null;
    List<String> postsKey = new ArrayList<String>();
    private boolean onDestroyFlag = false;
    private int listenerCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //onTabSelectedListener(mViewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        firebaseAuth = FirebaseAuth.getInstance();
        mdformat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        calendar = Calendar.getInstance();
        onDestroyFlag = false;
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        bidRef = FirebaseDatabase.getInstance().getReference().child("Bids");
        wonRef = FirebaseDatabase.getInstance().getReference().child("Won");
        Intent i = getIntent();
        type = i.getStringExtra("type");
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (onDestroyFlag == false && listenerCounter == 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Post checkPost = snapshot.getValue(Post.class);
                        Date endDatePost = null;
                        try {
                            currentDate = mdformat.parse(mdformat.format(calendar.getTime()));
                            endDatePost = mdformat.parse(checkPost.getEndtime());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (currentDate.compareTo(endDatePost) == 1) {
                            postsKey.add(checkPost.getPostKey());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        wonRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (onDestroyFlag == false && listenerCounter == 0) {
                    if (postsKey.size() > 0) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Won checkWon = snapshot.getValue(Won.class);
                            for (int i = 0; i < postsKey.size(); i++) {
                                if (postsKey.get(i).equals(checkWon.getPostkey())) {
                                    postsKey.remove(i);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        bidRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (onDestroyFlag == false && listenerCounter == 0) {
                    List<Bid> bids = new ArrayList<Bid>();
                    if (postsKey.size() > 0) {
                        for (int i = 0; i < postsKey.size(); i++) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Bid checkBid = snapshot.getValue(Bid.class);
                                if (postsKey.get(i).equals(checkBid.getPostkey())) {
                                    bids.add(checkBid);
                                }
                            }
                            maxBid(bids);
                            bids.clear();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            firebaseAuth.signOut();
            Intent i = new Intent(LoginScreen.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            System.out.println("checkff position" + position);
            if (type.equals("Bidder")) {
                switch (position) {
                    case 0:
                        LiveAuctions liveAuctions = new LiveAuctions();
                        return liveAuctions;
                    case 1:
                        WinAuctions winAuctions = new WinAuctions();
                        return winAuctions;
                    case 2:
                        FutureAuctions futureAuctions = new FutureAuctions();
                        return futureAuctions;
                    default:
                        LiveAuctions liveAuctions1 = new LiveAuctions();
                        return liveAuctions1;
                }
            } else if (type.equals("Auctioneer")) {
                switch (position) {
                    case 0:
                        MyAds myads = new MyAds();
                        System.out.println("checkff my ads coming");
                        return myads;
                    case 1:
                        PostAds postAds = new PostAds();
                        System.out.println("checkff Post ads coming");
                        return postAds;
                    default:
                        MyAds myads1 = new MyAds();
                        System.out.println("checkff my ads coming");
                        return myads1;
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            Intent i = getIntent();
            type = i.getStringExtra("type");
            if (type.equals("Bidder")) {
                return 3;
            } else {
                return 2;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Intent i = getIntent();
            type = i.getStringExtra("type");
            if (type.equals("Bidder")) {
                switch (position) {
                    case 0:
                        return "Live Auctions";
                    case 1:
                        return "Won Auctions";
                    case 2:
                        return "Future Auctions";
                    default:
                        return null;
                }
            } else if (type.equals("Auctioneer")) {
                switch (position) {
                    case 0:
                        return "My Ads";
                    case 1:
                        return "Post Ad";
                    default:
                        return null;
                }
            }
            return null;
        }
    }

    public void maxBid(List<Bid> bids) {
        //WonFlag = false;
        ++listenerCounter;
        if (bids.size() > 0) {
            Bid max = bids.get(0);
            for (int i = 0; i < bids.size(); i++) {
                if (Long.parseLong(max.getBid()) < Long.parseLong(bids.get(i).getBid())) {
                    max = bids.get(i);
                }
            }
            DatabaseReference wonAuctionsRef = FirebaseDatabase.getInstance().getReference().child("Won");
            Won wonAuction = new Won(max.getUsername(), max.getPostkey());
            wonAuctionsRef.push().setValue(wonAuction);
            System.out.println("checkff setValue(Won) live screen");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyFlag = true;
    }
}
