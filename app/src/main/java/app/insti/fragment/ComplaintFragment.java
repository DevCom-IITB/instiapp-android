package app.insti.fragment;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import app.insti.R;
import app.insti.Utils;
import app.insti.adapter.ComplaintDetailsPagerAdapter;
import app.insti.api.EmptyCallback;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Venter;
import retrofit2.Call;
import retrofit2.Response;

public class ComplaintFragment extends Fragment {

    private static final String TAG = ComplaintFragment.class.getSimpleName();
    private TabLayout slidingTabLayout;
    private ViewPager viewPager;
    private View mview;
    private String complaintId, userId, userProfileUrl;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complaint, container, false);

        slidingTabLayout = view.findViewById(R.id.sliding_tab_layout);
        this.mview = view;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            complaintId = bundle.getString("id");
            userId = bundle.getString("userId");
            userProfileUrl = bundle.getString("userProfileUrl");
            callServerToGetDetailedComplaint();
        }
    }

    private void callServerToGetDetailedComplaint() {

        RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
        retrofitInterface.getComplaint(Utils.getSessionIDHeader(), complaintId).enqueue(new EmptyCallback<Venter.Complaint>() {
            @Override
            public void onResponse(Call<Venter.Complaint> call, Response<Venter.Complaint> response) {
                if (getActivity() == null || getView() == null) return;
                if (response.body() != null) {
                    Venter.Complaint complaint = response.body();
                    initTabViews(complaint);
                    //Make progress circle gone After loading
                    getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                }
            }
        });
    }

    private void initTabViews(final Venter.Complaint detailedComplaint) {
        try {
            if (detailedComplaint != null) {
                viewPager = mview.findViewById(R.id.tab_viewpager_details);
                if (viewPager != null) {
                    ComplaintDetailsPagerAdapter complaintDetailsPagerAdapter = new ComplaintDetailsPagerAdapter(getChildFragmentManager(), complaintId, userId, userProfileUrl);

                    viewPager.setAdapter(complaintDetailsPagerAdapter);
                    slidingTabLayout.setupWithViewPager(viewPager);

                    slidingTabLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            int tablLayoutWidth = slidingTabLayout.getWidth();

                            DisplayMetrics metrics = new DisplayMetrics();
                            Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            int deviceWidth = metrics.widthPixels;

                            if (tablLayoutWidth <= deviceWidth) {

                                final TypedArray styledAttributes = Objects.requireNonNull(ComplaintFragment.this.getActivity()).getTheme().obtainStyledAttributes(
                                        new int[]{android.R.attr.actionBarSize});
                                styledAttributes.recycle();
                                //Replace second parameter to mActionBarSize = (int) styledAttributes.getDimension(0, 0) after adding "Relevant Complaints"
                                AppBarLayout.LayoutParams layoutParams = new AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT,
                                        0);
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
                            //on Tab Unselected
                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {
                            //on Tab Reselected
                        }
                    });

                    ComplaintDetailsFragment complaintDetailsFragment = (ComplaintDetailsFragment) getChildFragmentManager().findFragmentByTag(
                            "android:switcher:" + R.id.tab_viewpager_details + ":0"
                    );

                    if (complaintDetailsFragment != null)
                        complaintDetailsFragment.setDetailedComplaint(detailedComplaint);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}