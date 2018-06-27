package in.ac.iitb.gymkhana.iitbapp.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import in.ac.iitb.gymkhana.iitbapp.Constants;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.adapter.ProfileAdapter;
import in.ac.iitb.gymkhana.iitbapp.api.RetrofitInterface;
import in.ac.iitb.gymkhana.iitbapp.api.ServiceGenerator;
import in.ac.iitb.gymkhana.iitbapp.data.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends BaseFragment {
    User user;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        tabLayout = (TabLayout)view.findViewById(R.id.profile_tabs);
        viewPager = (ViewPager)view.findViewById(R.id.profile_view_pager);
        viewPager.setAdapter(new ProfileAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        return view;    }

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

        Picasso.with(getContext()).load(user.getUserProfilePictureUrl()).into(userProfilePictureImageView);
        userNameTextView.setText(user.getUserName());
        userRollNumberTextView.setText(user.getUserRollNumber());
        userEmailIDTextView.setText(user.getUserEmail());
        userContactNumberTextView.setText(user.getUserContactNumber());
    }
}
