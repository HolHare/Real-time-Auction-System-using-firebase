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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class MyAds extends Fragment {
    private ListView myListView;
    private AdsAdapter myadapter;
    private List<Post> posts= new ArrayList<Post>();
    private FirebaseDatabase database;
    private DatabaseReference postsreference;
    private ChildEventListener childEventListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        if (container != null) {
//            container.removeAllViews();
//        }
        View view=inflater.inflate(R.layout.fragment_my_ads, container, false);
        database=FirebaseDatabase.getInstance();
        postsreference=database.getReference().child("Posts");
        System.out.println("checkff coming My Ads");
        myListView =(ListView) view.findViewById( R.id.myAds_listview);
        myadapter=new AdsAdapter(getActivity(),posts,"my ads");
        myListView.setAdapter(myadapter);
        posts.clear();
        childEventListener= new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post addpost= dataSnapshot.getValue(Post.class);
                if(addpost.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    System.out.println("checkff title"+addpost.getTitle());
                    if(!posts.contains(addpost)) {
                        posts.add(addpost);
                        myadapter.notifyDataSetChanged();
                    }
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
        postsreference.addChildEventListener(childEventListener);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("checkff onDestroy call");
        getActivity().finish();

    }
}
