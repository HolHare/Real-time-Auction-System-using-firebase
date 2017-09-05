package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.osamakhalid.realtimeauctionsystem.AdsAdapter;
import com.example.osamakhalid.realtimeauctionsystem.Classes.Post;
import com.example.osamakhalid.realtimeauctionsystem.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class FutureAuctions extends Fragment {
    private ListView myListView;
    private AdsAdapter myAdapter;
    private List<Post> posts = new ArrayList<Post>();
    private DatabaseReference postRef;
    private ChildEventListener childEventListener;
    private SimpleDateFormat mdformat;
    Date currentDate, startDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        if (container != null) {
//            container.removeAllViews();
//        }
        View view = inflater.inflate(R.layout.fragment_future_auctions, container, false);
        myListView = (ListView) view.findViewById(R.id.future_auctions_listView);
        myAdapter = new AdsAdapter(getActivity(), posts, "future");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        mdformat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        myListView.setAdapter(myAdapter);
        posts.clear();
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post checkPost = dataSnapshot.getValue(Post.class);
                Calendar calendar = Calendar.getInstance();
                try {
                    currentDate = mdformat.parse(mdformat.format(calendar.getTime()));
                    startDate = mdformat.parse(checkPost.getStarttime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(currentDate.compareTo(startDate)==-1){
                    posts.add(checkPost);
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
        postRef.addChildEventListener(childEventListener);
        return view;
    }
}
