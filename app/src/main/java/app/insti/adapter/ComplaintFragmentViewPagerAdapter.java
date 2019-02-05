package app.insti.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import app.insti.fragment.ComplaintsHomeFragment;
import app.insti.fragment.ComplaintsMeFragment;

/**
 * Created by Shivam Sharma on 15-08-2018.
 */

public class ComplaintFragmentViewPagerAdapter extends FragmentStatePagerAdapter {

    private String userID, sessionID, userProfileUrl;

    public ComplaintFragmentViewPagerAdapter(FragmentManager fm,String userID, String sessionID, String userProfileUrl) {
        super(fm);
        this.userID = userID;
        this.sessionID = sessionID;
        this.userProfileUrl = userProfileUrl;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ComplaintsHomeFragment.getInstance(userID, userProfileUrl);
            case 1:
                return ComplaintsMeFragment.getInstance(userID, userProfileUrl);
            default:
                return ComplaintsHomeFragment.getInstance(userID, userProfileUrl);
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
