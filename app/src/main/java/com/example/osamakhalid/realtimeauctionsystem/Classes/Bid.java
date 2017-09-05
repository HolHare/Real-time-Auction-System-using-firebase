package com.example.osamakhalid.realtimeauctionsystem.Classes;

/**
 * Created by Osama Khalid on 8/20/2017.
 */

public class Bid {
    String username;
    String bid;
    String postkey;
    public Bid(){}
    public Bid(String username,String bid,String postkey){
        this.username=username;
        this.bid=bid;
        this.postkey=postkey;
    }

    public String getBid() {
        return bid;
    }

    public String getUsername() {
        return username;
    }

    public String getPostkey() {
        return postkey;
    }
}
