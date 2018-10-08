package app.insti.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import app.insti.api.model.Venter;
import app.insti.fragment.DetailedComplaintFragment;
import app.insti.fragment.RelevantComplaintsFragment;

/**
 * Created by Shivam Sharma on 19-09-2018.
 */

public class ComplaintDetailsPagerAdapter extends FragmentPagerAdapter {

    Venter.Complaint detailedComplaint;
    Context context;
    String sessionid, complaintid, userid;
    int voteCount;

    public ComplaintDetailsPagerAdapter(FragmentManager fm, Venter.Complaint detailedComplaint, Context context, String sessionid, String complaintid, String userid, int voteCount) {
        super(fm);
        this.context = context;
        this.detailedComplaint = detailedComplaint;
        this.sessionid = sessionid;
        this.complaintid = complaintid;
        this.userid = userid;
        this.voteCount = voteCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return DetailedComplaintFragment.getInstance(sessionid, complaintid, userid, voteCount);
                return DetailedComplaintFragment.getInstance(sessionid, complaintid, userid);
            case 1:
                return RelevantComplaintsFragment.getInstance(sessionid, userid);
            default:
                return DetailedComplaintFragment.getInstance(sessionid, complaintid, userid, voteCount);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0){
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
