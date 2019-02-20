package app.insti.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import app.insti.fragment.ComplaintDetailsFragment;

/**
 * Created by Shivam Sharma on 19-09-2018.
 */

public class ComplaintDetailsPagerAdapter extends FragmentPagerAdapter {

    private String complaintid, userid, userProfileUrl;

    public ComplaintDetailsPagerAdapter(FragmentManager fm, String complaintid, String userid, String userProfileUrl) {
        super(fm);
        this.complaintid = complaintid;
        this.userid = userid;
        this.userProfileUrl = userProfileUrl;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ComplaintDetailsFragment.getInstance(complaintid, userid, userProfileUrl);
           /*
            For version 2:
            case 1:
               return RelevantComplaintsFragment.getInstance(sessionid, userid);
           */
            default:
                return ComplaintDetailsFragment.getInstance(complaintid, userid, userProfileUrl);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Complaint Details";
        } else {
            return "Relevant Complaints";
        }

    }

    @Override
    public int getCount() {
        return 1;
    }
}
