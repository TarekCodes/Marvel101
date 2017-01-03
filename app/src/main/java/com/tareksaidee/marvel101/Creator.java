package com.tareksaidee.marvel101;

import android.graphics.Bitmap;

/**
 * Created by tarek on 12/30/2016.
 */

public class Creator {

    private String mName;
    private int mDigitlaID;
    private Bitmap mImage;
    private String mEvents;
    private String mAllComicsURL;

    public Creator(String name, int ID, Bitmap image, String events, String allComicsURL) {
        mName = name;
        mDigitlaID = ID;
        mImage = image;
        mEvents = events;
        mAllComicsURL = allComicsURL;
    }

    public String getName() {
        return mName;
    }

    public int getID() {
        return mDigitlaID;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public String getEvents() {
        return mEvents;
    }

    public String getAllComicsURL() {
        return mAllComicsURL;
    }

}
