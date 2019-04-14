package app.insti.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.insti.Constants;
import app.insti.R;
import app.insti.ShareURLMaker;
import app.insti.Utils;
import app.insti.adapter.TabAdapter;
import app.insti.api.EmptyCallback;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Body;
import app.insti.api.model.Event;
import app.insti.api.model.Role;
import app.insti.api.model.User;
import retrofit2.Call;
import retrofit2.Response;

import static android.view.View.VISIBLE;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends BackHandledFragment implements TransitionTargetFragment {
    private User user = null;

    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;
    private boolean zoomMode;
    private ImageView expandedImageView;
    private Rect startBounds;
    private float startScaleFinal;
    private ImageView userProfilePictureImageView;
    private boolean showingMin = false;

    public UserFragment() {
        // Required empty public constructor
    }

    public static UserFragment newInstance(String userID) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(Constants.USER_ID, userID);
        fragment.setArguments(args);
        return fragment;
    }

    public static UserFragment newInstance(User minUser) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(Constants.USER_JSON, Utils.gson.toJson(minUser));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void transitionEnd() {
        if (getActivity() == null || getView() == null) return;
        if (showingMin) {
            showingMin = false;
            loadUser(user.getUserID());
        }
    }

    @Override
    public boolean onBackPressed() {
        if (zoomMode) {
            zoomOut(expandedImageView, startBounds, startScaleFinal, userProfilePictureImageView);
            zoomMode = false;
            return true;
        }
        return false;
    }

    public void loadUser(String userID) {
        RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
        retrofitInterface.getUser(Utils.getSessionIDHeader(), userID).enqueue(new EmptyCallback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    if (getActivity() == null || getView() == null) return;
                    user = response.body();
                    populateViews();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");

        Bundle bundle = getArguments();

        String userID = bundle.getString(Constants.USER_ID);
        String userJson = bundle.getString(Constants.USER_JSON);
        if (user != null) {
            populateViews();
        } else if (userID != null) {
            loadUser(userID);
        } else if (userJson != null) {
            user = Utils.gson.fromJson(userJson, User.class);
            showingMin = true;
            populateViews();
        }
    }

    private void populateViews() {
        if (getActivity() == null || getView() == null) return;

        userProfilePictureImageView = getActivity().findViewById(R.id.user_profile_picture_profile);
        TextView userNameTextView = getActivity().findViewById(R.id.user_name_profile);
        TextView userRollNumberTextView = getActivity().findViewById(R.id.user_rollno_profile);
        final TextView userEmailIDTextView = getActivity().findViewById(R.id.user_email_profile);
        TextView userContactNumberTextView = getActivity().findViewById(R.id.user_contact_no_profile);
        FloatingActionButton userShareFab = getActivity().findViewById(R.id.share_user_button);

        Picasso.get()
                .load(user.getUserProfilePictureUrl())
                .placeholder(R.drawable.user_placeholder)
                .into(userProfilePictureImageView);

        userProfilePictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomImageFromThumb(userProfilePictureImageView);
            }
        });
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        if (!showingMin) {
            /* Show tabs */
            getActivity().findViewById(R.id.tab_layout).setVisibility(VISIBLE);

            /* Load lists */
            final List<Role> roleList = user.getUserRoles();
            final List<Body> bodyList = user.getUserFollowedBodies();
            final List<Event> eventList = user.getUserGoingEvents();
            List<Role> formerRoleList = user.getUserFormerRoles();
            List<Role> allRoles = new ArrayList<>(roleList);
            for (Role role : formerRoleList) {
                Role temp = new Role(role);
                temp.setRoleName("Former " + role.getRoleName() + " " + role.getRoleYear());
                allRoles.add(temp);
            }
            List<Event> eventInterestedList = user.getUserInterestedEvents();
            eventList.removeAll(eventInterestedList);
            eventList.addAll(eventInterestedList);
            RoleRecyclerViewFragment frag1 = RoleRecyclerViewFragment.newInstance(allRoles);
            BodyRecyclerViewFragment frag2 = BodyRecyclerViewFragment.newInstance(bodyList);
            EventRecyclerViewFragment frag3 = EventRecyclerViewFragment.newInstance(eventList);

            frag1.parentFragment = this;
            frag2.parentFragment = this;
            frag3.parentFragment = this;

            TabAdapter tabAdapter = new TabAdapter(getChildFragmentManager());
            tabAdapter.addFragment(frag1, "Associations");
            tabAdapter.addFragment(frag2, "Following");
            tabAdapter.addFragment(frag3, "Events");

            // Set up the ViewPager with the sections adapter.
            ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.viewPager);
            viewPager.setAdapter(tabAdapter);
            viewPager.setOffscreenPageLimit(2);

            TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tab_layout);
            tabLayout.setupWithViewPager(viewPager);

            userShareFab.show();
            getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }

        userNameTextView.setText(user.getUserName());
        userRollNumberTextView.setText(user.getUserRollNumber());
        if (user.getUserEmail() != null && !user.getUserEmail().equals("N/A")) {
            userEmailIDTextView.setText(user.getUserEmail());
        } else {
            if (user.getUserRollNumber() != null)
                userEmailIDTextView.setText(user.getUserRollNumber() + "@iitb.ac.in");
        }

        userEmailIDTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mail((String) userEmailIDTextView.getText());
            }
        });

        if (!"N/A".equals(user.getUserContactNumber())) {
            userContactNumberTextView.setText(user.getUserContactNumber());
            userContactNumberTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    call(user.getUserContactNumber());
                }
            });
        } else {
            userContactNumberTextView.setVisibility(View.GONE);
        }

        userShareFab.setOnClickListener(new View.OnClickListener() {
            String shareUrl = ShareURLMaker.getUserURL(user);

            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                i.putExtra(Intent.EXTRA_TEXT, shareUrl);
                startActivity(Intent.createChooser(i, "Share URL"));
            }
        });
    }

    private void call(String contactNumber) {
        String uri = "tel:" + contactNumber;
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(uri));
        startActivity(Intent.createChooser(intent, "Place a Call"));
    }

    private void mail(String email) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Let's have Coffee!");
        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    private void zoomOut(final ImageView expandedImageView, Rect startBounds, float startScaleFinal, final View thumbView) {
        expandedImageView.setBackgroundColor(0x00000000);
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Animate the four positioning/sizing properties in parallel,
        // back to their original values.
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator
                .ofFloat(expandedImageView, View.X, startBounds.left))
                .with(ObjectAnimator
                        .ofFloat(expandedImageView,
                                View.Y, startBounds.top))
                .with(ObjectAnimator
                        .ofFloat(expandedImageView,
                                View.SCALE_X, startScaleFinal))
                .with(ObjectAnimator
                        .ofFloat(expandedImageView,
                                View.SCALE_Y, startScaleFinal));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                thumbView.setAlpha(1f);
                expandedImageView.setVisibility(View.GONE);
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                thumbView.setAlpha(1f);
                expandedImageView.setVisibility(View.GONE);
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;
    }

    private void zoomImageFromThumb(final ImageView thumbView) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        expandedImageView = (ImageView) getActivity().findViewById(
                R.id.expanded_image_profile);
        expandedImageView.setImageDrawable(thumbView.getDrawable());

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        getActivity().findViewById(R.id.container_profile)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y,
                        startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
                expandedImageView.setBackgroundColor(Color.parseColor("#9E9E9E"));
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        startScaleFinal = startScale;
        zoomMode = true;
    }
}
