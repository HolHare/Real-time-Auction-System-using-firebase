package com.example.osamakhalid.realtimeauctionsystem.Classes;

/**
 * Created by Osama Khalid on 8/8/2017.
 */

public class User {
    String name;
    String phonenum;
    String userid;
    String type;
    public User(){}
    public User(String name,String phonenum,String userid,String type){
        this.name=name;
        this.phonenum=phonenum;
        this.userid=userid;
        this.type=type;
    }

    public String getUserid() {
        return userid;
    }

    public String getName() {
        return name;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public String getType() {
        return type;
    }
}
