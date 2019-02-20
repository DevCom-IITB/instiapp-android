package app.insti.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

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
