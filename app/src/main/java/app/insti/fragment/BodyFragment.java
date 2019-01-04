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
public class BodyFragment extends BackHandledFragment implements TransitionTargetFragment {
    String TAG = "BodyFragment";
    private Body min_body;
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
    }

    @Override
    public void onStart() {
        super.onStart();

        /* Initialize */
        bodyDisplayed = false;
        body = min_body;
        displayBody();

        updateBody();

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
                }
            }

            @Override
            public void onFailure(Call<Body> call, Throwable t) {
                // Network Error
            }
        });
    }

    private void displayBody() {
        /* Skip if we're already destroyed */
        if (getActivity() == null || getView() == null) return;
        if (!body.equals(min_body)) bodyDisplayed = true;

        TextView bodyName = (TextView) getView().findViewById(R.id.body_name);
        TextView bodySubtitle = getView().findViewById(R.id.body_subtitle);
        TextView bodyDescription = (TextView) getView().findViewById(R.id.body_description);
        ImageButton webBodyButton = getActivity().findViewById(R.id.web_body_button);
        ImageButton shareBodyButton = getActivity().findViewById(R.id.share_body_button);
        final Button followButton = getActivity().findViewById(R.id.follow_button);

        /* Set body information */
        bodyName.setText(body.getBodyName());
        bodySubtitle.setText(body.getBodyShortDescription());

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_body, container, false);
    }
}
