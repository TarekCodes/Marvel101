package com.tareksaidee.marvel101;

/**
 * Created by tarek on 12/28/2016.
 */

public class Character {

    private String mName;
    private int mID;
    private String mDescrp;
    private String mImageURL;
    private String mImageExt;

    public Character(String name, int ID, String descrp, String imageURL, String imageExt){
        mName = name;
        mID = ID;
        mDescrp = descrp;
        mImageURL = imageURL;
        mImageExt = imageExt;
    }

    public String getCharName(){
        return mName;
    }

    public int getID(){
        return mID;
    }

    public String getDecrp(){
        return mDescrp;
    }

    public String getImageURL(){
        return mImageURL;
    }

    public String getImageExt(){
        return mImageExt;
    }

}
