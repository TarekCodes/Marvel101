package com.tareksaidee.marvel101;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by tarek on 12/29/2016.
 */

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    SimpleFragmentPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
