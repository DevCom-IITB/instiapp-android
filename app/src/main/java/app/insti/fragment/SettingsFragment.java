package app.insti.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import app.insti.Constants;
import app.insti.R;
import app.insti.SessionManager;
import app.insti.activity.LoginActivity;
import app.insti.activity.MainActivity;
import app.insti.api.RetrofitInterface;
import app.insti.api.ServiceGenerator;
import app.insti.data.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    User user;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");

        Bundle bundle = getArguments();

        String userID = bundle.getString(Constants.USER_ID);

        populateViews();

        RetrofitInterface retrofitInterface = ((MainActivity) getActivity()).getRetrofitInterface();
        retrofitInterface.getUser("sessionid=" + getArguments().getString(Constants.SESSION_ID), userID).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    user = response.body();
                    populateUserCard();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    private void populateUserCard() {
        if (getActivity() == null || getView() == null) {
            return;
        }
        ImageView userProfilePictureImageView = getActivity().findViewById(R.id.user_card_avatar);
        TextView userNameTextView = getActivity().findViewById(R.id.user_card_name);

        Picasso.get()
                .load(user.getUserProfilePictureUrl())
                .resize(800, 0)
                .placeholder(R.drawable.user_placeholder)
                .into(userProfilePictureImageView);
        userNameTextView.setText(user.getUserName());

        getView().findViewById(R.id.role_card_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserFragment userFragment = UserFragment.newInstance(user.getUserID());
                ((MainActivity)getActivity()).updateFragment(userFragment);
            }
        });
    }

    private void populateViews() {
        // Check if we exist
        if (getActivity() == null || getView() == null) return;

        Button updateProfileButton = getView().findViewById(R.id.settings_update_profile);
        Button feedbackButton = getView().findViewById(R.id.settings_feedback);
        Button aboutButton = getView().findViewById(R.id.settings_about);
        Button logoutButton = getView().findViewById(R.id.settings_logout);

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebURL("https://gymkhana.iitb.ac.in/sso/user");
            }
        });

        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebURL("https://insti.app/feedback");
            }
        });

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutFragment aboutFragment = new AboutFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                transaction.replace(R.id.framelayout_for_fragment, aboutFragment, aboutFragment.getTag());
                transaction.addToBackStack(aboutFragment.getTag()).commit();
            }
        });

        // Logged in user vs Guest
        final SessionManager sessionManager = new SessionManager(getContext());
        if (sessionManager.isLoggedIn()) {
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RetrofitInterface retrofitInterface = ((MainActivity) getActivity()).getRetrofitInterface();
                    retrofitInterface.logout("sessionid=" + getArguments().getString(Constants.SESSION_ID)).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                sessionManager.logout();
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                            //Server Error
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            //Network Error
                        }
                    });
                }
            });
        } else {
            logoutButton.setVisibility(View.GONE);
            getView().findViewById(R.id.role_card_layout).setVisibility(View.GONE);
        }
    }

    private void openWebURL(String URL) {
        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
        startActivity(browse);
    }
}
