package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.osamakhalid.realtimeauctionsystem.AdsAdapter;
import com.example.osamakhalid.realtimeauctionsystem.Classes.Bid;
import com.example.osamakhalid.realtimeauctionsystem.Classes.Post;
import com.example.osamakhalid.realtimeauctionsystem.Classes.Won;
import com.example.osamakhalid.realtimeauctionsystem.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LiveAuctions extends Fragment {
    private ListView mylistview;
    private AdsAdapter myadapter;
    private List<Post> posts = new ArrayList<Post>();
    private Spinner category;
    private String mCategory;
    private DatabaseReference postreference;
    private DatabaseReference bidRef;
    private ChildEventListener childEventListener;
    private SimpleDateFormat mdformat;
    private boolean isCountDownRunning = false;
    Date currentDate = null;
    private boolean onDestroyFlag = false, returnType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        if (container != null) {
//            container.removeAllViews();
//        }
        View view = inflater.inflate(R.layout.fragment_live_auctions, container, false);
        onDestroyFlag = false;
        mdformat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        myadapter = new AdsAdapter(getActivity(), posts, "live");
        category = (Spinner) view.findViewById(R.id.categoryOfItem_liveAuctions);
        mylistview = (ListView) view.findViewById(R.id.live_auctions_listview);
        postreference = FirebaseDatabase.getInstance().getReference().child("Posts");
        bidRef = FirebaseDatabase.getInstance().getReference().child("Bids");
        ArrayAdapter<CharSequence> adapterCategoryOfItem = ArrayAdapter.createFromResource(getActivity(), R.array.category_search, android.R.layout.simple_spinner_item);
        adapterCategoryOfItem.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapterCategoryOfItem);
        mylistview.setAdapter(myadapter);
        posts.clear();
        mCategory = category.getSelectedItem().toString();
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mCategory = category.getItemAtPosition(i).toString();
                System.out.println("checkff category=" + mCategory);
                Query query = postreference;
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        posts.clear();
                        myadapter.notifyDataSetChanged();
                        System.out.println("checkff post.clear runs");
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Post checkPost = snapshot.getValue(Post.class);
                            if (mCategory.equals("All")) {
                                Calendar c = Calendar.getInstance();
                                try {
                                    currentDate = mdformat.parse(mdformat.format(c.getTime()));
                                } catch (Exception c1) {
                                    c1.printStackTrace();
                                }
                                Date postStartDate = null, postEndDate = null;
                                try {
                                    System.out.println("checkaa start=" + checkPost.getStarttime() + " end=" + checkPost.getEndtime());
                                    postStartDate = mdformat.parse(checkPost.getStarttime());
                                    postEndDate = mdformat.parse(checkPost.getEndtime());
                                } catch (Exception c1) {
                                    c1.printStackTrace();
                                }
                                System.out.println("checkff all current=" + currentDate + " start=" + postStartDate + " end=" + postEndDate);
                                if (currentDate.compareTo(postStartDate) >= 0 && currentDate.compareTo(postEndDate) == -1) {
                                    posts.add(checkPost);
                                    myadapter.notifyDataSetChanged();
                                }
                            } else if (checkPost.getCategory().equals(mCategory)) {
                                Calendar c = Calendar.getInstance();
                                try {
                                    currentDate = mdformat.parse(mdformat.format(c.getTime()));
                                } catch (Exception c1) {
                                    c1.printStackTrace();
                                }
                                Date postStartDate = null, postEndDate = null;
                                try {
                                    postStartDate = mdformat.parse(checkPost.getStarttime());
                                    postEndDate = mdformat.parse(checkPost.getEndtime());
                                } catch (Exception c1) {
                                    c1.printStackTrace();
                                }
                                System.out.println("checkff category current=" + currentDate + " start=" + postStartDate + " end=" + postEndDate);
                                if (currentDate.compareTo(postStartDate) >= 0 && currentDate.compareTo(postEndDate) == -1) {
                                    posts.add(checkPost);
                                    myadapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        /*
        childEventListener= new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post checkPost= dataSnapshot.getValue(Post.class);
                Calendar c = Calendar.getInstance();
                try {
                     currentDate = mdformat.parse(mdformat.format(c.getTime()));
                } catch (Exception c1) {
                    c1.printStackTrace();
                }
                Date postStartDate=null,postEndDate=null;
                try {
                    postStartDate = mdformat.parse(checkPost.getStarttime());
                    postEndDate = mdformat.parse(checkPost.getEndtime());
                }
                catch (Exception c1){
                    c1.printStackTrace();
                }
                System.out.println("checkff onChild current="+currentDate+" start="+postStartDate+" end="+postEndDate);
                if(currentDate.compareTo(postStartDate)>=0 && currentDate.compareTo(postEndDate)==-1){
                    posts.add(checkPost);
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
        postreference.addChildEventListener(childEventListener);  */
        final CountDownTimer mCountDown = new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                checkTimeOfPosts();
                this.start();
            }
        }.start();
        return view;
    }

    public void checkTimeOfPosts() {
        Calendar c = Calendar.getInstance();
        try {
            currentDate = mdformat.parse(mdformat.format(c.getTime()));
        } catch (Exception c1) {
            c1.printStackTrace();
        }
        Date postEndDate = null;

        for (int i = 0; i < posts.size(); i++) {
            try {
                postEndDate = mdformat.parse(posts.get(i).getEndtime());
            } catch (Exception c1) {
                c1.printStackTrace();
            }
            if (currentDate.compareTo(postEndDate) >= 0) {
                System.out.println("checkff setValue compare to coming");
                checkWinner(posts.get(i).getPostKey());
                posts.remove(i);
                myadapter.notifyDataSetChanged();
            }
        }
        Query query = postreference;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post checkPost = snapshot.getValue(Post.class);
                    Calendar c = Calendar.getInstance();
                    Date startDate = null, endDate = null;
                    try {
                        currentDate = mdformat.parse(mdformat.format(c.getTime()));
                        startDate = mdformat.parse(checkPost.getStarttime());
                        endDate = mdformat.parse(checkPost.getEndtime());
                    } catch (Exception c1) {
                        c1.printStackTrace();
                    }
                    if (currentDate.compareTo(startDate) >= 0 && currentDate.compareTo(endDate) == -1) {
                        posts.add(checkPost);
                        myadapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void checkWinner(final String postkey) {
        Query query = bidRef;
        final List<Bid> allBids = new ArrayList<Bid>();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildren() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Bid checkbid = snapshot.getValue(Bid.class);
                        if (checkbid.getPostkey().equals(postkey)) {
                            allBids.add(checkbid);
                        }
                    }
                    maxBid(allBids);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void maxBid(List<Bid> bids) {
        //WonFlag = false;
        if (bids.size() > 0) {
            Bid max = bids.get(0);
            for (int i = 0; i < bids.size(); i++) {
                if (Long.parseLong(max.getBid()) < Long.parseLong(bids.get(i).getBid())) {
                    max = bids.get(i);
                }
            }
            if (onDestroyFlag == false) {
                DatabaseReference wonAuctionsRef = FirebaseDatabase.getInstance().getReference().child("Won");
                    Won wonAuction = new Won(max.getUsername(), max.getPostkey());
                    wonAuctionsRef.push().setValue(wonAuction);
                    System.out.println("checkff setValue(Won) live screen");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onDestroyFlag = true;
    }
}
