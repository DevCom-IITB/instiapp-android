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
import app.insti.R;
import app.insti.ShareURLMaker;
import app.insti.Utils;
import app.insti.activity.MainActivity;
import app.insti.adapter.BodyAdapter;
import app.insti.adapter.FeedAdapter;
import app.insti.adapter.GenericAdapter;
import app.insti.adapter.UserAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Body;
import app.insti.api.model.Event;
import app.insti.api.model.Role;
import app.insti.api.model.User;
import app.insti.interfaces.CardInterface;
import app.insti.utils.BodyHeadCard;
import app.insti.utils.TitleCard;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.noties.markwon.Markwon;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BodyFragmentNew#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BodyFragmentNew extends BackHandledFragment implements TransitionTargetFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    String TAG = "BodyFragmentNew";
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
    private boolean transitionEnded = false;

    public BodyFragmentNew() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param arg_body Body for details
     * @return A new instance of fragment BodyFragmentNew.
     */
    // TODO: Rename and change types and number of parameters
    public static BodyFragmentNew newInstance(Body arg_body) {
        BodyFragmentNew fragment = new BodyFragmentNew();
        Bundle args = new Bundle();
        args.putString(Constants.BODY_JSON, new Gson().toJson(arg_body));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onBackPressed() {
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
        body = min_body;
        displayBody();

        updateBody();

        /*bodySwipeRefreshLayout = getActivity().findViewById(R.id.body_swipe_refresh_layout);
        bodySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bodyDisplayed = false;
                updateBody();
            }
        });*/

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

                    if (!bodyDisplayed) {
                        body = bodyResponse;
                        displayBody();
                    }
                    /*if (bodySwipeRefreshLayout.isRefreshing())
                        bodySwipeRefreshLayout.setRefreshing(false);*/
                }
            }

            @Override
            public void onFailure(Call<Body> call, Throwable t) {
                /*bodySwipeRefreshLayout.setRefreshing(false);*/
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

        //TextView bodyName = (TextView) getView().findViewById(R.id.body_name);
        //TextView bodySubtitle = getView().findViewById(R.id.body_subtitle);
        //TextView bodyDescription = (TextView) getView().findViewById(R.id.body_description);
        bodyPicture = (ImageView) getActivity().findViewById(R.id.body_picture);
        //ImageButton webBodyButton = getActivity().findViewById(R.id.web_body_button);
        //ImageButton shareBodyButton = getActivity().findViewById(R.id.share_body_button);
        //final Button followButton = getActivity().findViewById(R.id.follow_button);

        /* Show relevant card titles */
        //setVisibleIfHasElements(new int[]{R.id.body_events_title, R.id.event_card_recycler_view}, body.getBodyEvents());
        //setVisibleIfHasElements(new int[]{R.id.body_orgs_title, R.id.org_card_recycler_view}, body.getBodyChildren());
        //setVisibleIfHasElements(new int[]{R.id.body_parents_title, R.id.parentorg_card_recycler_view}, body.getBodyParents());
        //setVisibleIfHasElements(new int[]{R.id.body_people_title, R.id.people_card_recycler_view}, body.getBodyRoles());

        /* Set body information */
        //bodyName.setText(body.getBodyName());
        //bodySubtitle.setText(body.getBodyShortDescription());

        /* Load only low res image if transition is not completed */
        if (transitionEnded) {
            Utils.loadImageWithPlaceholder(bodyPicture, body.getBodyImageURL());
        } else {
            Picasso.get().load(Utils.resizeImageUrl(body.getBodyImageURL())).into(bodyPicture);
        }

        /* Return if it's a min body */
        if (body.getBodyDescription() == null) {
            return;
        }

        //Markwon.setMarkdown(bodyDescription, body.getBodyDescription());

        /* Check if user is already following
         * Initialize follow button */
        //followButton.setBackgroundColor(getResources().getColor(body.getBodyUserFollows() ? R.color.colorAccent : R.color.colorWhite));
        //followButton.setText(EventFragment.getCountBadgeSpannable("FOLLOW", body.getBodyFollowersCount()));

        /**followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
                retrofitInterface.updateBodyFollowing(Utils.getSessionIDHeader(), body.getBodyID(), body.getBodyUserFollows() ? 0 : 1).enqueue(new Callback<Void>() {
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
        });*/

        /* Initialize web button
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
        }*/

        /* Initialize share button
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
        });*/

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

        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.body_recycler_view);
        GenericAdapter genericAdapter =  new GenericAdapter(cards, this);
        recyclerView.setAdapter(genericAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        /* Show update button if role
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
        }*/
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
        return inflater.inflate(R.layout.fragment_body_fragment_new, container, false);
    }
}
