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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.insti.Constants;
import app.insti.interfaces.ItemClickListener;
import app.insti.R;
import app.insti.ShareURLMaker;
import app.insti.activity.MainActivity;
import app.insti.adapter.BodyAdapter;
import app.insti.adapter.FeedAdapter;
import app.insti.adapter.UserAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Body;
import app.insti.api.model.Event;
import app.insti.api.model.Role;
import app.insti.api.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.noties.markwon.Markwon;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BodyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BodyFragment extends BackHandledFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    String TAG = "BodyFragment";
    // TODO: Rename and change types of parameters
    private Body min_body;
    private SwipeRefreshLayout bodySwipeRefreshLayout;


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
    private ImageView bodyPicture;
    private Body body;
    private boolean bodyDisplayed = false;

    public BodyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param arg_body Body for details
     * @return A new instance of fragment BodyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BodyFragment newInstance(Body arg_body) {
        BodyFragment fragment = new BodyFragment();
        Bundle args = new Bundle();
        args.putString(Constants.BODY_JSON, new Gson().toJson(arg_body));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onBackPressed() {
        if (zoomMode) {
            zoomOut(expandedImageView, startBounds, startScaleFinal, bodyPicture);
            zoomMode = false;
            return true;
        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            min_body = new Gson().fromJson(getArguments().getString(Constants.BODY_JSON), Body.class);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        /* Initialize */
        bodyDisplayed = false;
        body = min_body;
        displayBody();

        updateBody();

        bodySwipeRefreshLayout = getActivity().findViewById(R.id.body_swipe_refresh_layout);
        bodySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bodyDisplayed = false;
                updateBody();
            }
        });

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(min_body.getBodyName());
    }

    private void updateBody() {
        RetrofitInterface retrofitInterface = ((MainActivity) getActivity()).getRetrofitInterface();
        retrofitInterface.getBody(((MainActivity) getActivity()).getSessionIDHeader(), min_body.getBodyID()).enqueue(new Callback<Body>() {
            @Override
            public void onResponse(Call<Body> call, Response<Body> response) {
                if (response.isSuccessful()) {
                    Body bodyResponse = response.body();

                    if (!bodyDisplayed) {
                        body = bodyResponse;
                        displayBody();
                    }
                    if (bodySwipeRefreshLayout.isRefreshing())
                        bodySwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<Body> call, Throwable t) {
                bodySwipeRefreshLayout.setRefreshing(false);
                // Network Error
            }
        });
    }

    private void setVisibleIfHasElements(int[] viewIds, List list) {
        if (list != null && list.size() > 0) {
            for (int viewId : viewIds) {
                getActivity().findViewById(viewId).setVisibility(View.VISIBLE);
            }
        }
    }

    private void displayBody() {
        /* Skip if we're already destroyed */
        if (getActivity() == null || getView() == null) return;
        if (!body.equals(min_body)) bodyDisplayed = true;

        TextView bodyName = (TextView) getView().findViewById(R.id.body_name);
        TextView bodyDescription = (TextView) getView().findViewById(R.id.body_description);
        bodyPicture = (ImageView) getActivity().findViewById(R.id.body_picture);
        ImageButton webBodyButton = getActivity().findViewById(R.id.web_body_button);
        ImageButton shareBodyButton = getActivity().findViewById(R.id.share_body_button);
        final Button followButton = getActivity().findViewById(R.id.follow_button);

        /* Show relevant card titles */
        setVisibleIfHasElements(new int[]{R.id.body_events_title, R.id.event_card_recycler_view}, body.getBodyEvents());
        setVisibleIfHasElements(new int[]{R.id.body_orgs_title, R.id.org_card_recycler_view}, body.getBodyChildren());
        setVisibleIfHasElements(new int[]{R.id.body_parents_title, R.id.parentorg_card_recycler_view}, body.getBodyParents());
        setVisibleIfHasElements(new int[]{R.id.body_people_title, R.id.people_card_recycler_view}, body.getBodyRoles());

        /* Set body information */
        bodyName.setText(body.getBodyName());
        Picasso.get().load(body.getBodyImageURL()).into(bodyPicture);

        bodyPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomImageFromThumb(bodyPicture);
            }
        });
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        /* Return if it's a min body */
        if (body.getBodyDescription() == null) {
            return;
        }

        Markwon.setMarkdown(bodyDescription, body.getBodyDescription());

        /* Check if user is already following
         * Initialize follow button */
        followButton.setBackgroundColor(getResources().getColor(body.getBodyUserFollows() ? R.color.colorAccent : R.color.colorWhite));
        followButton.setText(EventFragment.getCountBadgeSpannable("FOLLOW", body.getBodyFollowersCount()));

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetrofitInterface retrofitInterface = ((MainActivity) getActivity()).getRetrofitInterface();
                retrofitInterface.updateBodyFollowing(((MainActivity) getActivity()).getSessionIDHeader(), body.getBodyID(), body.getBodyUserFollows() ? 0 : 1).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            body.setBodyUserFollows(!body.getBodyUserFollows());
                            body.setBodyFollowersCount(body.getBodyUserFollows()? body.getBodyFollowersCount()+1:body.getBodyFollowersCount()-1);
                            followButton.setBackgroundColor(getResources().getColor(body.getBodyUserFollows() ? R.color.colorAccent : R.color.colorWhite));
                            followButton.setText(EventFragment.getCountBadgeSpannable("FOLLOW", body.getBodyFollowersCount()));
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Network Error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        /* Initialize web button */
        if (body.getBodyWebsiteURL() != null && !body.getBodyWebsiteURL().isEmpty()) {
            webBodyButton.setVisibility(View.VISIBLE);
            webBodyButton.setOnClickListener(new View.OnClickListener() {
                String bodywebURL = body.getBodyWebsiteURL();

                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(bodywebURL));
                    startActivity(browserIntent);
                }
            });
        }

        /* Initialize share button */
        shareBodyButton.setOnClickListener(new View.OnClickListener() {
            String shareUrl = ShareURLMaker.getBodyURL(body);

            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                i.putExtra(Intent.EXTRA_TEXT, shareUrl);
                startActivity(Intent.createChooser(i, "Share URL"));
            }
        });

        /* Initialize events */
        final List<Event> eventList = body.getBodyEvents();
        RecyclerView eventRecyclerView = (RecyclerView) getActivity().findViewById(R.id.event_card_recycler_view);
        FeedAdapter eventAdapter =  new FeedAdapter(eventList, this);
        eventRecyclerView.setAdapter(eventAdapter);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        /* Get users from roles */
        final List<Role> roles = body.getBodyRoles();
        final List<User> users = new ArrayList<>();
        for (Role role : roles) {
            if (role.getRoleUsersDetail() != null) {
                for (User user : role.getRoleUsersDetail()) {
                    user.setCurrentRole(role.getRoleName());
                    users.add(user);
                }
            }
        }

        /* Initialize People */
        RecyclerView userRecyclerView = (RecyclerView) getActivity().findViewById(R.id.people_card_recycler_view);
        UserAdapter userAdapter = new UserAdapter(users, new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                User user = users.get(position);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.USER_ID, user.getUserID());
                UserFragment userFragment = new UserFragment();
                userFragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                ft.replace(R.id.framelayout_for_fragment, userFragment, userFragment.getTag());
                ft.addToBackStack(userFragment.getTag());
                ft.commit();
            }
        });
        userRecyclerView.setAdapter(userAdapter);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        /* Initialize Parent bodies */
        RecyclerView parentsRecyclerView = (RecyclerView) getActivity().findViewById(R.id.parentorg_card_recycler_view);
        BodyAdapter parentAdapter = new BodyAdapter(body.getBodyParents(), new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                openBody(body.getBodyParents().get(position));
            }
        });
        parentsRecyclerView.setAdapter(parentAdapter);
        parentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        /* Initialize child bodies */
        RecyclerView childrenRecyclerView = (RecyclerView) getActivity().findViewById(R.id.org_card_recycler_view);
        BodyAdapter childrenAdapter = new BodyAdapter(body.getBodyChildren(), new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                openBody(body.getBodyChildren().get(position));
            }
        });
        childrenRecyclerView.setAdapter(childrenAdapter);
        childrenRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        /* Show update button if role */
        if (((MainActivity) getActivity()).editBodyAccess(body)) {
            final FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.edit_fab);
            fab.show();
            NestedScrollView nsv = (NestedScrollView) getView().findViewById(R.id.body_scrollview);
            nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > oldScrollY) fab.hide();
                    else fab.show();
                }
            });

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddEventFragment addEventFragment = new AddEventFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("bodyId", body.getBodyID());
                    addEventFragment.setArguments(bundle);
                    ((MainActivity) getActivity()).updateFragment(addEventFragment);
                }
            });
        }
    }

    /**
     * Open body fragment for a body
     */
    private void openBody(Body body) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BODY_JSON, new Gson().toJson(body));
        BodyFragment bodyFragment = new BodyFragment();
        bodyFragment.setArguments(bundle);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
        ft.replace(R.id.framelayout_for_fragment, bodyFragment, bodyFragment.getTag());
        ft.addToBackStack(bodyFragment.getTag());
        ft.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_body, container, false);
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
                R.id.expanded_image_body);
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
        getActivity().findViewById(R.id.container_body)
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
        expandedImageView.setVisibility(View.VISIBLE);

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
