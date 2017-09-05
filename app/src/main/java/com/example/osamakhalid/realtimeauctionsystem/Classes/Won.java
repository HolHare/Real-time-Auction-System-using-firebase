package com.example.osamakhalid.realtimeauctionsystem.Classes;

/**
 * Created by Osama Khalid on 8/21/2017.
 */

public class Won {
    String userid;
    String postkey;
    public Won(){}
    public Won(String userid,String postkey){
        this.userid=userid;
        this.postkey=postkey;
    }

    public String getPostkey() {
        return postkey;
    }

    public String getUserid() {
        return userid;
    }
}
