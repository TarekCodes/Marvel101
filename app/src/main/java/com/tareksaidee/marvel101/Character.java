package com.tareksaidee.marvel101;

import android.graphics.Bitmap;

/**
 * Created by tarek on 12/28/2016.
 */

public class Character {

    private boolean mClicked = false;
    private String mName;
    private int mID;
    private String mDescrp;
    private Bitmap mImage;
    private int mAvailableComics;
    private String mWikiURL;

    public Character(String name, int ID, String descrp, Bitmap image, int availableComics, String wikiURL){
        mName = name;
        mID = ID;
        mDescrp = descrp;
        mImage = image;
        mAvailableComics = availableComics;
        mWikiURL = wikiURL;
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

    public int getAvailableComics(){
        return mAvailableComics;
    }

    public String getWikiURL(){
        return mWikiURL;
    }

    public void gotClicked(){
        mClicked = true;
    }

    public boolean wasClicked(){
        return mClicked;
    }

    public void unClicked(){
        mClicked = false;
    }
}
