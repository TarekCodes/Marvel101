package com.tareksaidee.marvel101;

import android.graphics.Bitmap;

/**
 * Created by tarek on 12/31/2016.
 */

public class Event {
    private String mTitle;
    private int mID;
    private String mDescrp;
    private Bitmap mImage;
    private String mStartDate;
    private String mEndDate;
    private String mNextEvent;
    private String mPrevEvent;

    public Event(String title, int ID, String descrp, Bitmap image, String startDate, String endDate, String nextEvent, String prevEvent) {
        mTitle = title;
        mID = ID;
        mDescrp = descrp;
        mImage = image;
        mStartDate = startDate;
        mEndDate = endDate;
        mNextEvent = nextEvent;
        mPrevEvent = prevEvent;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getID() {
        return mID;
    }

    public String getDescrp() {
        return mDescrp;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public String getStartDate() {
        return mStartDate;
    }

    public String getEndDate() {
        return mEndDate;
    }

    public String getNextEvent() {
        return mNextEvent;
    }

    public String getPrevEvent() {
        return mPrevEvent;
    }
}
