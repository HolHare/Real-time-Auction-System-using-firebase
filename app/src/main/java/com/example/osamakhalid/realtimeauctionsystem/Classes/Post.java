package com.example.osamakhalid.realtimeauctionsystem.Classes;

import java.io.Serializable;

/**
 * Created by Osama Khalid on 8/12/2017.
 */

public class Post implements Serializable{
    private String photouri;
    private String starttime;
    private String title;
    private String description;
    private String initialbid;
    private String category;
    private String endtime;
    private String postKey;
    private String userId;
    public Post(){}
    public Post (String title,String description,String initialbid,String category,String starttime,String endtime,String photouri,String postKey,String userId){
        this.title=title;
        this.description=description;
        this.initialbid=initialbid;
        this.starttime=starttime;
        this.endtime=endtime;
        this.photouri=photouri;
        this.postKey=postKey;
        this.userId=userId;
        this.category=category;
    }

    public String getCategory() {
        return category;
    }

    public String getUserId() {
        return userId;
    }

    public String getPostKey() {
        return postKey;
    }

    public String getDescription() {
        return description;
    }

    public String getEndtime() {
        return endtime;
    }

    public String getInitialbid() {
        return initialbid;
    }

    public String getPhotouri() {
        return photouri;
    }

    public String getStarttime() {
        return starttime;
    }

    public String getTitle() {
        return title;
    }
}

