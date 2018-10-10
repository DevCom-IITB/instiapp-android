package app.insti.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import app.insti.api.model.Venter;
import app.insti.fragment.ComplaintDetailsFragment;

/**
 * Created by Shivam Sharma on 19-09-2018.
 */

public class ComplaintDetailsPagerAdapter extends FragmentPagerAdapter {

    private Venter.Complaint detailedComplaint;
    private Context context;
    private String sessionid, complaintid, userid, userProfileUrl;

    public ComplaintDetailsPagerAdapter(FragmentManager fm, Venter.Complaint detailedComplaint, Context context, String sessionid, String complaintid, String userid, String userProfileUrl) {
        super(fm);
        this.context = context;
        this.detailedComplaint = detailedComplaint;
        this.sessionid = sessionid;
        this.complaintid = complaintid;
        this.userid = userid;
        this.userProfileUrl = userProfileUrl;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ComplaintDetailsFragment.getInstance(sessionid, complaintid, userid, userProfileUrl);
           /* case 1:
                return RelevantComplaintsFragment.getInstance(sessionid, userid);*/
            default:
                return ComplaintDetailsFragment.getInstance(sessionid, complaintid, userid, userProfileUrl);
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
        return 2;
    }
}
