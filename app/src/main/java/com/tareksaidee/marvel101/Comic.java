package com.tareksaidee.marvel101;

import android.graphics.Bitmap;

/**
 * Created by tarek on 12/29/2016.
 */

public class Comic {
    private String mTitle;
    private int mDigitlaID;
    private String mDescrp;
    private Bitmap mCover;
    private String mPubDate;
    private String mCreators;

    public Comic(String title, int ID, String descrp, Bitmap cover, String pubDate, String creators) {
        mTitle = title;
        mDigitlaID = ID;
        mDescrp = descrp;
        mCover = cover;
        mPubDate = pubDate;
        mCreators = creators;
    }

    public String getCharName() {
        return mTitle;
    }

    public int getID() {
        return mDigitlaID;
    }

    public String getDecrp() {
        return mDescrp;
    }

    public Bitmap getImage() {
        return mCover;
    }

    public String getPubDate() {
        return mPubDate;
    }

    public String getCreators(){return mCreators;}
}
