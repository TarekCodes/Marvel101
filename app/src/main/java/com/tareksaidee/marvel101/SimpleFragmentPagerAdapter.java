package com.tareksaidee.marvel101;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by tarek on 12/29/2016.
 */

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private String tabTitles[] = new String[] { "Characters", "Colors", "Family", "Phrases" };

    SimpleFragmentPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public Fragment getItem(int position) {
        return new CharSearchFragment();
    }

    @Override
    public int getCount() {
        return 1;
    }
}
