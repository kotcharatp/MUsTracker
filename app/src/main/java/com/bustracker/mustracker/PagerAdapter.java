package com.bustracker.mustracker;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by kotcharat on 2/2/16.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                FragmentTab1_Home tab1 = new FragmentTab1_Home();
                return tab1;
            case 1:
                FragmentTab2_Seat tab2 = new FragmentTab2_Seat();
                return tab2;
            case 2:
                FragmentTab3_Bus tab3 = new FragmentTab3_Bus();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}