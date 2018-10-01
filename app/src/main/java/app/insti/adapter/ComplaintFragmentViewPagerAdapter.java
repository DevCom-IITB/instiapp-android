package app.insti.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import app.insti.fragment.HomeFragment;
import app.insti.fragment.MeFragment;

/**
 * Created by Shivam Sharma on 15-08-2018.
 */

public class ComplaintFragmentViewPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = ComplaintFragmentViewPagerAdapter.class.getSimpleName();
    Context context;
    String userID, sessionID;

    public ComplaintFragmentViewPagerAdapter(FragmentManager fm, Context context, String userID, String sessionID) {
        super(fm);
        this.context = context;
        this.userID = userID;
        this.sessionID = sessionID;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return HomeFragment.getInstance(sessionID, userID);
            case 1:
                return MeFragment.getInstance(sessionID,userID);
            default:
                return HomeFragment.getInstance(sessionID, userID);
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Home";
        } else {
            return "Me";
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
