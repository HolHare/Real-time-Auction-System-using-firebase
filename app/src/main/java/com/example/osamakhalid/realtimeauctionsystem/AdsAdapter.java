package com.example.osamakhalid.realtimeauctionsystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.osamakhalid.realtimeauctionsystem.Classes.Post;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Created by Osama Khalid on 8/15/2017.
 */

public class AdsAdapter extends ArrayAdapter<Post> {
    private TextView title;
    private TextView description;
    private ImageView image;
    private List<Post> posts;
    private Context mContext;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference reference = null;
    private String mType;
    private RelativeLayout myLinearLayout;

    public AdsAdapter(@NonNull Context context, List<Post> posts, String type) {
        super(context, R.layout.ad_items, posts);
        this.posts = posts;
        mContext = context;
        mType = type;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View customView = convertView;
        if (customView == null) {
            customView = LayoutInflater.from(getContext()).inflate(R.layout.ad_items, parent, false);
        }
        Post post = getItem(position);
        title = (TextView) customView.findViewById(R.id.title_of_ad);
        description = (TextView) customView.findViewById(R.id.description_of_ad);
        image = (ImageView) customView.findViewById(R.id.thumbnail);
        myLinearLayout = (RelativeLayout) customView.findViewById(R.id.relative_layout_item);
        myLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("checkff coming onClick, type="+mType);
                if (mType.equals("live")) {
                    Post sendPost = getItem(position);
                    Intent i = new Intent(mContext, BiddingScreen.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("send post", sendPost);
                    mContext.startActivity(i);
                    ((Activity)mContext).finish();
                    return;
                }
                else if(mType.equals("my ads")){
                    Post sendPost = getItem(position);
                    Intent i = new Intent(mContext, AuctioneerBiddingScreen.class);
                    i.putExtra("send post", sendPost);
                    mContext.startActivity(i);
                    ((Activity)mContext).finish();
                    return;
                }
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mType.equals("live")) {
                    Post sendPost = getItem(position);
                    Intent i = new Intent(mContext, BiddingScreen.class);
                    i.putExtra("send post", sendPost);
                    mContext.startActivity(i);
                    ((Activity)mContext).finish();
                    return;
                }
                else if(mType.equals("my ads")){
                    Post sendPost = getItem(position);
                    Intent i = new Intent(mContext, AuctioneerBiddingScreen.class);
                    i.putExtra("send post", sendPost);
                    mContext.startActivity(i);
                    ((Activity)mContext).finish();
                    return;
                }
            }
        });
        title.setText(post.getTitle());
        reference = storage.getReferenceFromUrl(post.getPhotouri());
        description.setText(post.getDescription());
        Glide.with(mContext).using(new FirebaseImageLoader()).load(reference).into(image);
        return customView;
    }
}
