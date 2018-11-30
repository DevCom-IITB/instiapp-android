package app.insti.fragment;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import app.insti.Constants;
import app.insti.R;
import app.insti.Utils;
import app.insti.adapter.ComplaintFragmentViewPagerAdapter;

public class ComplaintsFragment extends BaseFragment {

    private String userID, userProfileUrl;
    private TabLayout slidingTabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_complaints, container, false);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Complaints/Suggestions");
        Utils.setSelectedMenuItem(getActivity(), R.id.nav_complaint);

        Bundle bundle = getArguments();
        userID = bundle.getString(Constants.USER_ID);
        userProfileUrl = bundle.getString(Constants.CURRENT_USER_PROFILE_PICTURE);
        CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);
        ViewPager viewPager = view.findViewById(R.id.tab_viewpager);

        slidingTabLayout = view.findViewById(R.id.sliding_tab_layout);

        Button buttonVentIssues = view.findViewById(R.id.buttonVentIssues);

        buttonVentIssues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileComplaintFragment fileComplaintFragment = new FileComplaintFragment();
                fileComplaintFragment.setArguments(getArguments());
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.framelayout_for_fragment, fileComplaintFragment, fileComplaintFragment.getTag());
                fragmentTransaction.addToBackStack("Complaint Fragment").commit();
            }
        });

        slidingTabLayout = view.findViewById(R.id.sliding_tab_layout);

        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        return view;
    }

    private void setupViewPager(final ViewPager viewPager) {

        viewPager.setAdapter(new ComplaintFragmentViewPagerAdapter(getChildFragmentManager(), userID, getArguments().getString(Constants.SESSION_ID), userProfileUrl));
        slidingTabLayout.setupWithViewPager(viewPager);
        slidingTabLayout.post(new Runnable() {
            @Override
            public void run() {
                int tabLayoutWidth = slidingTabLayout.getWidth();

                DisplayMetrics metrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int deviceWidth = metrics.widthPixels;

                if (tabLayoutWidth <= (deviceWidth + 1)) {
                    final TypedArray styledAttributes = getActivity().getTheme().obtainStyledAttributes(
                            new int[]{android.R.attr.actionBarSize}
                    );

                    int mActionBarSize = (int) styledAttributes.getDimension(0, 0);
                    styledAttributes.recycle();

                    AppBarLayout.LayoutParams layoutParams = new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            mActionBarSize);

                    slidingTabLayout.setLayoutParams(layoutParams);
                    slidingTabLayout.setTabMode(TabLayout.MODE_FIXED);
                    slidingTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                } else {
                    slidingTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                }
            }
        });

        slidingTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //On Tab Unselected
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //On Tab Reselected
            }
        });

        viewPager.setOffscreenPageLimit(3);
    }
}
