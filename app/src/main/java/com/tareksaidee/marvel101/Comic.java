package com.tareksaidee.marvel101;

import android.graphics.Bitmap;

/**
 * Created by tarek on 12/29/2016.
 */

public class Comic {

    private boolean mClicked = false;
    private String mTitle;
    private int mDigitlaID;
    private String mSynop;
    private Bitmap mCover;
    private String mPubDate;
    private String mCreators;
    private String mDetailsURL;
    private String mCollections;
    private String mPartOfEvent;

    public Comic(String title, int ID, String synop, Bitmap cover, String pubDate, String creators,
                 String detailsURL, String collections, String partOfEvent) {
        mTitle = title;
        mDigitlaID = ID;
        mSynop = synop;
        mCover = cover;
        mPubDate = pubDate;
        mCreators = creators;
        mDetailsURL = detailsURL;
        mCollections = collections;
        mPartOfEvent = partOfEvent;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getID() {
        return mDigitlaID;
    }

    public String getSynop() {
        return mSynop;
    }

    public Bitmap getCover() {
        return mCover;
    }

    public String getPubDate() {
        return mPubDate;
    }

    public String getCreators(){return mCreators;}

    public String getDetailsURL(){return mDetailsURL;}

    public String getCollections(){return mCollections;}

    public String getPartOfEvent(){return mPartOfEvent;}

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
