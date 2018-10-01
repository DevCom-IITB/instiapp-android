package app.insti.fragment;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Objects;

import app.insti.R;
import app.insti.activity.MainActivity;
import app.insti.adapter.ComplaintDetailsPagerAdapter;
import app.insti.adapter.ImageViewPagerAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.User;
import app.insti.api.model.Venter;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplaintDetailsFragment extends Fragment {

    private static final String TAG = ComplaintDetailsFragment.class.getSimpleName();
    Button buttonVoteUp;
    TabLayout slidingTabLayout;
    ViewPager viewPager;
    View mview;
    private String complaintId, sessionID, userId;
    private ComplaintDetailsPagerAdapter complaintDetailsPagerAdapter;
    private int voteCount;
    CircleIndicator circleIndicator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complaint_details, container, false);

        LinearLayout imageViewHolder = view.findViewById(R.id.image_holder_view);
        CollapsingToolbarLayout.LayoutParams layoutParams = new CollapsingToolbarLayout.LayoutParams
                (CollapsingToolbarLayout.LayoutParams.MATCH_PARENT,
                        getResources().getDisplayMetrics().heightPixels / 2);
        imageViewHolder.setLayoutParams(layoutParams);

        buttonVoteUp = view.findViewById(R.id.buttonVoteUp);
        slidingTabLayout = view.findViewById(R.id.sliding_tab_layout);
        circleIndicator = view.findViewById(R.id.indicator);
        this.mview = view;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonVoteUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upVote();
            }
        });

        Bundle bundle = getArguments();
        complaintId = bundle.getString("id");
        sessionID = bundle.getString("sessionId");
        userId = bundle.getString("userId");

        if (bundle != null) {
            Log.i(TAG, "bundle != null");
            callServerToGetDetailedComplaint();
        }
    }

    private void upVote() {
        if (voteCount == 0) {
            RetrofitInterface retrofitInterface = ((MainActivity) getActivity()).getRetrofitInterface();
            retrofitInterface.upVote("sessionid=" + sessionID, complaintId).enqueue(new Callback<Venter.Complaint>() {
                @Override
                public void onResponse(Call<Venter.Complaint> call, Response<Venter.Complaint> response) {
                    if (response.isSuccessful()) {
                        Venter.Complaint complaint = response.body();
                        initTabViews(complaint);
                        Toast.makeText(getActivity().getApplicationContext(), "You have Up Voted this complaint", Toast.LENGTH_SHORT).show();
                        voteCount++;
                    }
                }

                @Override
                public void onFailure(Call<Venter.Complaint> call, Throwable t) {
                    Log.i(TAG, "failure in up vote: " + t.toString());
                }
            });
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "You have already UpVoted this complaint", Toast.LENGTH_SHORT).show();
        }
    }

    private void callServerToGetDetailedComplaint() {

        RetrofitInterface retrofitInterface = ((MainActivity) getActivity()).getRetrofitInterface();
        retrofitInterface.getComplaint("sessionid=" + sessionID, complaintId).enqueue(new Callback<Venter.Complaint>() {
            @Override
            public void onResponse(Call<Venter.Complaint> call, Response<Venter.Complaint> response) {
                if (response.body() != null) {
                    Venter.Complaint complaint = response.body();
                    for (User currentUser : complaint.getUsersUpVoted()) {
                        if (currentUser.getUserID().equals(userId)) {
                            voteCount = 1;
                        }
                    }
                    initViewPagerForImages(complaint);
                    initTabViews(complaint);
                }
            }

            @Override
            public void onFailure(Call<Venter.Complaint> call, Throwable t) {
                if (t != null) {
                    Log.i(TAG, "error and t = " + t.toString());
                }
            }
        });
    }

    private void initViewPagerForImages(Venter.Complaint detailedComplaint) {

        viewPager = mview.findViewById(R.id.complaint_image_view_pager);
        if (viewPager != null) {
            try {
                ImageViewPagerAdapter imageFragmentPagerAdapter = new ImageViewPagerAdapter(getChildFragmentManager(), detailedComplaint);

                viewPager.setAdapter(imageFragmentPagerAdapter);
                circleIndicator.setViewPager(viewPager);
                imageFragmentPagerAdapter.registerDataSetObserver(circleIndicator.getDataSetObserver());
                viewPager.getAdapter().notifyDataSetChanged();
                synchronized (viewPager) {
                    viewPager.notifyAll();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initTabViews(final Venter.Complaint detailedComplaint) {
        try {
            buttonVoteUp.setText("UpVote");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (detailedComplaint != null) {
                viewPager = mview.findViewById(R.id.tab_viewpager_details);
                if (viewPager != null) {
                    Log.i(TAG, "viewPager != null");
                    complaintDetailsPagerAdapter = new ComplaintDetailsPagerAdapter(getChildFragmentManager(), detailedComplaint, getContext(), sessionID, complaintId, userId);

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

                                final TypedArray styledAttributes = Objects.requireNonNull(ComplaintDetailsFragment.this.getActivity()).getTheme().obtainStyledAttributes(
                                        new int[]{android.R.attr.actionBarSize});
                                int mActionBarSize = (int) styledAttributes.getDimension(0, 0);
                                styledAttributes.recycle();

                                AppBarLayout.LayoutParams layoutParams = new AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT,
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

                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    });

                    DetailedComplaintFragment detailedComplaintFragment = (DetailedComplaintFragment) getChildFragmentManager().findFragmentByTag(
                            "android:switcher:" + R.id.tab_viewpager_details + ":0"
                    );

                    Log.i(TAG, "detailedComplaintFragment: " + detailedComplaint);

                    /*For Relevant Complaint Fragment
                    * RelevantComplaintFragment relevantComplaintFragment = (RelevantComplaintFragment) getSupportFragmentManager().findFragmentByTag(
                            "android:switcher:" + R.id.tab_viewpager + ":1");
                    */

                    if (detailedComplaintFragment != null) {
                        Log.i(TAG, "detailedComplinatFragment != null");
                        detailedComplaintFragment.setDetailedComplaint(detailedComplaint);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}