package in.ac.iitb.gymkhana.iitbapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import in.ac.iitb.gymkhana.iitbapp.fragment.UserEventsFragment;
import in.ac.iitb.gymkhana.iitbapp.fragment.UserFollowersFragment;
import in.ac.iitb.gymkhana.iitbapp.fragment.UserFollowingFragment;

/**
 * Created by owais on 01/04/18.
 */

public class ProfileAdapter extends FragmentPagerAdapter {
    private static final String TAG = ProfileAdapter.class.getSimpleName();
    private static final int FRAGMENT_COUNT = 2;
    public ProfileAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new UserEventsFragment();
            case 1:
                return new UserFollowersFragment();
        }
        return null;
    }
    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Events";
            case 1:
                return "Followers";
        }
        return null;
    }
}
