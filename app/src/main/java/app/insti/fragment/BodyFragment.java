package app.insti.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.insti.Constants;
import app.insti.R;
import app.insti.Utils;
import app.insti.activity.MainActivity;
import app.insti.adapter.GenericAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Body;
import app.insti.api.model.Role;
import app.insti.api.model.User;
import app.insti.interfaces.CardInterface;
import app.insti.utils.BodyHeadCard;
import app.insti.utils.TitleCard;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BodyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BodyFragment extends BackHandledFragment implements TransitionTargetFragment {
    public final String TAG = "BodyFragment";
    private Body min_body;
    private SwipeRefreshLayout bodySwipeRefreshLayout;

    private ImageView bodyPicture;
    private Body body;
    private boolean bodyDisplayed = false;
    private boolean transitionEnded = false;

    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private boolean zoomMode;
    private ImageView expandedImageView;
    private Rect startBounds;
    private float startScaleFinal;

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

            /* Show fab if the user has access */
            if (((MainActivity) getActivity()).editBodyAccess(body)) {
                final FloatingActionButton fab = getView().findViewById(R.id.edit_fab);
                fab.show();
            }

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
    public void transitionEnd() {
        if (getActivity() == null || getView() == null) return;
        bodyPicture = (ImageView) getView().findViewById(R.id.body_picture);
        Utils.loadImageWithPlaceholder(bodyPicture, body.getBodyImageURL());
        transitionEnded = true;
    }

    @Override
    public void onStart() {
        super.onStart();

        /* Initialize */
        bodyDisplayed = false;

        int index = Utils.bodyCache.indexOf(min_body);
        if (index < 0) {
            body = min_body;
            updateBody();
        } else {
            body = Utils.bodyCache.get(index);
        }

        displayBody();

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

        Bundle bundle = getArguments();
        if (bundle != null && bundle.getBoolean(Constants.NO_SHARED_ELEM, true)) {
            this.transitionEnd();
        }
    }

    private void updateBody() {
        RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
        retrofitInterface.getBody(Utils.getSessionIDHeader(), min_body.getBodyID()).enqueue(new Callback<Body>() {
            @Override
            public void onResponse(Call<Body> call, Response<Body> response) {
                if (response.isSuccessful()) {
                    Body bodyResponse = response.body();
                    Utils.bodyCache.updateCache(response.body());

                    if (!bodyDisplayed) {
                        body = bodyResponse;
                        displayBody();
                    }
                    bodySwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<Body> call, Throwable t) {
                bodySwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void displayBody() {
        /* Skip if we're already destroyed */
        if (getActivity() == null || getView() == null) return;
        if (body != min_body) bodyDisplayed = true;

        bodyPicture = (ImageView) getActivity().findViewById(R.id.body_picture);

        /* Load only low res image if transition is not completed */
        if (transitionEnded) {
            Utils.loadImageWithPlaceholder(bodyPicture, body.getBodyImageURL());
        } else {
            Picasso.get().load(Utils.resizeImageUrl(body.getBodyImageURL())).into(bodyPicture);
        }

        /* Skip for min body */
        if (body == min_body) {
            return;
        }

        bodyPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomImageFromThumb(bodyPicture);
                final FloatingActionButton fab = getView().findViewById(R.id.edit_fab);
                fab.hide();
            }
        });
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

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

        final List<CardInterface> cards = new ArrayList<>();
        cards.add(new BodyHeadCard(body));
        addWithTitleCard(cards,  body.getBodyEvents(), "Events");
        addWithTitleCard(cards,  users, "People");
        addWithTitleCard(cards,  body.getBodyChildren(), "Organizations");
        addWithTitleCard(cards,  body.getBodyParents(), "Part of");

        final RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.body_recycler_view);
        GenericAdapter genericAdapter =  new GenericAdapter(cards, this);
        recyclerView.setAdapter(genericAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        /* Show update button if role */
        if (((MainActivity) getActivity()).editBodyAccess(body)) {
            final FloatingActionButton fab = getView().findViewById(R.id.edit_fab);
            fab.show();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0) fab.hide();
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

    private <R extends CardInterface> void addWithTitleCard(List<CardInterface> cards, List<R> cardsToAdd, String title) {
        if (cardsToAdd.size() == 0) return;
        cards.add(new TitleCard(title));
        cards.addAll(cardsToAdd);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_body, container, false);
    }

    private void zoomImageFromThumb(final ImageView thumbView) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        expandedImageView = (ImageView) getView().findViewById(
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
}
