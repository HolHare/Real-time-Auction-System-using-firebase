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
import com.example.osamakhalid.realtimeauctionsystem.Classes.Won;
import com.example.osamakhalid.realtimeauctionsystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class WinAuctions extends Fragment {
    private ListView myListView;
    private AdsAdapter myAdapter;
    private List<Post> posts = new ArrayList<Post>();
    private DatabaseReference wonAuctionsRef, postRef;
    private ChildEventListener childEventListener;
    String postKey;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        if (container != null) {
//            container.removeAllViews();
//        }
        View view = inflater.inflate(R.layout.fragment_win_auctions, container, false);
        myListView = (ListView) view.findViewById(R.id.won_auctions_listview);
        myAdapter = new AdsAdapter(getActivity(), posts, "won");
        wonAuctionsRef = FirebaseDatabase.getInstance().getReference().child("Won");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        myListView.setAdapter(myAdapter);
        posts.clear();
        Query query = wonAuctionsRef;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Won checkWon = snapshot.getValue(Won.class);
                    if (checkWon.getUserid().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        postKey = checkWon.getPostkey();
                        checkWinPosts(postKey);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("checkff onDestroy call");
        getActivity().finish();
    }

    public void checkWinPosts(final String postkey) {
        Query query = postRef;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post checkPost = snapshot.getValue(Post.class);
                    System.out.println("checkff titles of post=" + checkPost.getTitle());
                    System.out.println("checkff checkpostkey=" + checkPost.getPostKey() + " postkey" + postkey);
                    if (checkPost.getPostKey().equals(postkey)) {
                        for (int i = 0; i < posts.size(); i++) {
                            if (posts.get(i).getPostKey().equals(checkPost.getPostKey())) {
                                return;
                            }
                        }
                        if (!posts.contains(checkPost)) {
                            posts.add(checkPost);
                            myAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
