package org.tensorflow.demo.photoSearch.AccessorsAndSetters;

/**
 * Created by mgo983 on 8/10/17.
 */

public class Photos {
    private String id;
    private String user;
    private String date_taken;
    private String time_taken;
    private String name_caption;
    private String photo_url;

    public Photos(){}
    public Photos(String user, String date_taken, String time_taken, String name_caption, String photo_url){
        this.user = user;
        this.date_taken = date_taken;
        this.time_taken = time_taken;
        this.name_caption = name_caption;
        this.photo_url = photo_url;

    }

    public String getId(){
        return id;
    }

    public String getUser(){
        return user;
    }

    public String getDate_taken(){
        return date_taken;
    }

    public String getTime_taken(){
        return time_taken;
    }

    public String getName_caption(){
        return name_caption;
    }

    public String getPhoto_url(){
        return photo_url;
    }

    public void setId(String id){
        this.id = id;
    }

    public void setUser(String user){
        this.user = user;
    }

    public void setDate_taken(String date_taken){
        this.date_taken = date_taken;
    }

    public void setTime_taken(String time_taken){
        this.time_taken = time_taken;
    }

    public void setName_caption(String name_caption){
        this.name_caption = name_caption;
    }

    public void setPhoto_url(String photo_url){
        this.photo_url = photo_url;
    }
}


