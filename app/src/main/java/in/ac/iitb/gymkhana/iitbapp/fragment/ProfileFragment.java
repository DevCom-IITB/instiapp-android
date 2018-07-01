package in.ac.iitb.gymkhana.iitbapp.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import in.ac.iitb.gymkhana.iitbapp.Constants;
import in.ac.iitb.gymkhana.iitbapp.ItemClickListener;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.adapter.RoleAdapter;
import in.ac.iitb.gymkhana.iitbapp.adapter.TabAdapter;
import in.ac.iitb.gymkhana.iitbapp.api.RetrofitInterface;
import in.ac.iitb.gymkhana.iitbapp.api.ServiceGenerator;
import in.ac.iitb.gymkhana.iitbapp.data.Body;
import in.ac.iitb.gymkhana.iitbapp.data.Event;
import in.ac.iitb.gymkhana.iitbapp.data.Role;
import in.ac.iitb.gymkhana.iitbapp.data.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends BaseFragment {
    User user;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        String userID = bundle.getString(Constants.USER_ID);

        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.getUser("sessionid=" + getArguments().getString(Constants.SESSION_ID), userID).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();
                    populateViews();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    private void populateViews() {
        ImageView userProfilePictureImageView = getActivity().findViewById(R.id.user_profile_picture_profile);
        TextView userNameTextView = getActivity().findViewById(R.id.user_name_profile);
        TextView userRollNumberTextView = getActivity().findViewById(R.id.user_rollno_profile);
        TextView userEmailIDTextView = getActivity().findViewById(R.id.user_email_profile);
        TextView userContactNumberTextView = getActivity().findViewById(R.id.user_contact_no_profile);

        /** Show tabs */
        getActivity().findViewById(R.id.tab_layout).setVisibility(View.VISIBLE);

        final List<Role> roleList = user.getUserRoles();
        RecyclerView userRoleRecyclerView = getActivity().findViewById(R.id.role_recycler_view);
        RoleAdapter roleAdapter = new RoleAdapter(roleList, new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Role role = roleList.get(position);
                Body roleBody = role.getRoleBodyDetails();
                BodyFragment bodyFragment = BodyFragment.newInstance(roleBody);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.framelayout_for_fragment, bodyFragment, bodyFragment.getTag());
                ft.addToBackStack(bodyFragment.getTag());
                ft.commit();

            }
        });
        userRoleRecyclerView.setAdapter(roleAdapter);
        userRoleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Picasso.with(getContext()).load(user.getUserProfilePictureUrl()).into(userProfilePictureImageView);

        final List<Body> bodyList = user.getUserFollowedBodies();
        final List<Event> eventList = user.getUserGoingEvents();
        final List<Event> eventInterestedList = user.getUserInterestedEvents();
        eventList.removeAll(eventInterestedList);
        eventList.addAll(eventInterestedList);
        BodyRecyclerViewFragment frag1 = BodyRecyclerViewFragment.newInstance(bodyList);
        EventRecyclerViewFragment frag2 = EventRecyclerViewFragment.newInstance(eventList);
        TabAdapter tabAdapter = new TabAdapter(getChildFragmentManager());
        tabAdapter.addFragment(frag1, "Following");
        tabAdapter.addFragment(frag2, "Events");
        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewPager);
        viewPager.setAdapter(tabAdapter);
        viewPager.setOffscreenPageLimit(2);


        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        userNameTextView.setText(user.getUserName());
        userRollNumberTextView.setText(user.getUserRollNumber());
        userEmailIDTextView.setText(user.getUserEmail());
        userContactNumberTextView.setText(user.getUserContactNumber());

        getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }


}
