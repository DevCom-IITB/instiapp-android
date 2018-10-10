package app.insti.adapter;

import android.content.Context;
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

    Context context;
    private String userID, sessionID, userProfileUrl;

    public ComplaintFragmentViewPagerAdapter(FragmentManager fm, Context context, String userID, String sessionID, String userProfileUrl) {
        super(fm);
        this.context = context;
        this.userID = userID;
        this.sessionID = sessionID;
        this.userProfileUrl = userProfileUrl;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ComplaintsHomeFragment.getInstance(sessionID, userID, userProfileUrl);
            case 1:
                return ComplaintsMeFragment.getInstance(sessionID,userID, userProfileUrl);
            default:
                return ComplaintsHomeFragment.getInstance(sessionID, userID, userProfileUrl);
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
