package com.tareksaidee.marvel101;

import android.graphics.Bitmap;

/**
 * Created by tarek on 12/28/2016.
 */

public class Character {

    private String mName;
    private int mID;
    private String mDescrp;
    private Bitmap mImage;

    public Character(String name, int ID, String descrp, Bitmap image){
        mName = name;
        mID = ID;
        mDescrp = descrp;
        mImage = image;
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

    public Bitmap getImage(){
        return mImage;
    }


}
