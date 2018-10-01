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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import app.insti.Constants;
import app.insti.Utils;
import app.insti.R;
import app.insti.ShareURLMaker;
import app.insti.activity.MainActivity;
import app.insti.adapter.BodyAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Body;
import app.insti.api.model.Event;
import app.insti.api.model.Venue;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.noties.markwon.Markwon;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends BackHandledFragment {
    Event event;
    Button goingButton;
    Button interestedButton;
    ImageButton navigateButton;
    ImageButton webEventButton;
    ImageButton shareEventButton;
    RecyclerView bodyRecyclerView;
    String TAG = "EventFragment";

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
    private ImageView eventPicture;


    public EventFragment() {
        // Required empty public constructor
    }

    /**
     * Get a spannable with a small count badge to set for an element text
     *
     * @param text  Text to show in the spannable
     * @param count integer count to show in the badge
     * @return spannable to be used as view.setText(spannable)
     */
    static Spannable getCountBadgeSpannable(String text, Integer count) {
        // Check for nulls
        if (count == null) return new SpannableString(text);

        // Make a spannable
        String countString = Integer.toString(count);
        Spannable spannable = new SpannableString(text + " " + countString);

        // Set font face and color of badge
        spannable.setSpan(new RelativeSizeSpan(0.75f), text.length(), text.length() + 1 + countString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.DKGRAY), text.length(), text.length() + 1 + countString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        return spannable;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public boolean onBackPressed() {
        if (zoomMode) {
            zoomOut(expandedImageView, startBounds, startScaleFinal, eventPicture);
            zoomMode = false;
            return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle bundle = getArguments();
        String eventJson = bundle.getString(Constants.EVENT_JSON);
        Log.d(TAG, "onStart: " + eventJson);
        event = new Gson().fromJson(eventJson, Event.class);
        inflateViews(event);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(event.getEventName());
    }

    private void inflateViews(final Event event) {
        eventPicture = (ImageView) getActivity().findViewById(R.id.event_picture_2);
        TextView eventTitle = (TextView) getActivity().findViewById(R.id.event_page_title);
        TextView eventDate = (TextView) getActivity().findViewById(R.id.event_page_date);
        TextView eventTime = (TextView) getActivity().findViewById(R.id.event_page_time);
        TextView eventVenue = (TextView) getActivity().findViewById(R.id.event_page_venue);
        TextView eventDescription = (TextView) getActivity().findViewById(R.id.event_page_description);
        goingButton = getActivity().findViewById(R.id.going_button);
        interestedButton = getActivity().findViewById(R.id.interested_button);
        navigateButton = getActivity().findViewById(R.id.navigate_button);
        webEventButton = getActivity().findViewById(R.id.web_event_button);
        shareEventButton = getActivity().findViewById(R.id.share_event_button);

        Utils.loadImageWithPlaceholder(eventPicture, event.getEventImageURL());

        eventTitle.setText(event.getEventName());
        Markwon.setMarkdown(eventDescription, event.getEventDescription());
        Timestamp timestamp = event.getEventStartTime();
        Date Date = new Date(timestamp.getTime());
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("dd MMM");
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("HH:mm");
        eventDate.setText(simpleDateFormatDate.format(Date));
        eventTime.setText(simpleDateFormatTime.format(Date));
        StringBuilder eventVenueName = new StringBuilder();

        for (Venue venue : event.getEventVenues()) {
            eventVenueName.append(", ").append(venue.getVenueShortName());
        }

        final List<Body> bodyList = event.getEventBodies();
        bodyRecyclerView = (RecyclerView) getActivity().findViewById(R.id.body_card_recycler_view);
        BodyAdapter bodyAdapter = new BodyAdapter(bodyList, this);
        bodyRecyclerView.setAdapter(bodyAdapter);
        bodyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        if (!eventVenueName.toString().equals(""))
            eventVenue.setText(eventVenueName.toString().substring(2));

        interestedButton.setOnClickListener(getUESOnClickListener(1));

        goingButton.setOnClickListener(getUESOnClickListener(2));

        setFollowButtonColors(event.getEventUserUes());

        if (!event.getEventVenues().isEmpty()) {
            if (event.getEventVenues().get(0).getVenueLatitude() == 0) {
                navigateButton.setVisibility(View.GONE);
            } else {
                navigateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Venue primaryVenue = event.getEventVenues().get(0);
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + primaryVenue.getVenueLatitude() + "," + primaryVenue.getVenueLongitude() + "(" + primaryVenue.getVenueName() + ")");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                });
            }
        } else {
            navigateButton.setVisibility(View.GONE);
        }
        shareEventButton.setOnClickListener(new View.OnClickListener() {
            String shareUrl = ShareURLMaker.getEventURL(event);

            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                i.putExtra(Intent.EXTRA_TEXT, shareUrl);
                startActivity(Intent.createChooser(i, "Share URL"));
            }
        });
        if (event.getEventWebsiteURL() != null && !event.getEventWebsiteURL().isEmpty()) {
            webEventButton.setVisibility(View.VISIBLE);
            webEventButton.setOnClickListener(new View.OnClickListener() {
                String eventwebURL = event.getEventWebsiteURL();

                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(eventwebURL));
                    startActivity(browserIntent);
                }
            });
        }

        eventPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomImageFromThumb(eventPicture);
            }
        });
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        final FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.edit_fab);

        if (((MainActivity) getActivity()).editEventAccess(event)) {
            fab.show();
            NestedScrollView nsv = (NestedScrollView) getView().findViewById(R.id.event_scrollview);
            nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > oldScrollY) fab.hide();
                    else fab.show();
                }
            });
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEventFragment addEventFragment = new AddEventFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id", event.getEventID());
                addEventFragment.setArguments(bundle);
                ((MainActivity) getActivity()).updateFragment(addEventFragment);
            }
        });
    }

    void setFollowButtonColors(int status) {
        interestedButton.setBackgroundColor(getResources().getColor(status == Constants.STATUS_INTERESTED ? R.color.colorAccent : R.color.colorWhite));
        goingButton.setBackgroundColor(getResources().getColor(status == Constants.STATUS_GOING ? R.color.colorAccent : R.color.colorWhite));

        // Show badges
        interestedButton.setText(getCountBadgeSpannable("INTERESTED", event.getEventInterestedCount()));
        goingButton.setText(getCountBadgeSpannable("GOING", event.getEventGoingCount()));
    }

    View.OnClickListener getUESOnClickListener(final int status) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int endStatus = event.getEventUserUes() == status ? 0 : status;
                RetrofitInterface retrofitInterface = ((MainActivity) getActivity()).getRetrofitInterface();
                retrofitInterface.updateUserEventStatus(((MainActivity) getActivity()).getSessionIDHeader(), event.getEventID(), endStatus).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            /* TODO: Find a better way to  change counts */
                            if (endStatus == 0) {
                                if (event.getEventUserUes() == 1) {
                                    event.setEventInterestedCount(event.getEventInterestedCount() - 1);
                                }
                                if (event.getEventUserUes() == 2) {
                                    event.setEventGoingCount(event.getEventGoingCount() - 1);
                                }
                            } else if (endStatus == 1) {
                                if (event.getEventUserUes() != 1) {
                                    event.setEventInterestedCount(event.getEventInterestedCount() + 1);
                                }
                                if (event.getEventUserUes() == 2) {
                                    event.setEventGoingCount(event.getEventGoingCount() - 1);
                                }
                            } else if (endStatus == 2) {
                                if (event.getEventUserUes() != 2) {
                                    event.setEventGoingCount(event.getEventGoingCount() + 1);
                                }
                                if (event.getEventUserUes() == 1) {
                                    event.setEventInterestedCount(event.getEventInterestedCount() - 1);
                                }
                            }

                            event.setEventUserUes(endStatus);
                            setFollowButtonColors(endStatus);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Network Error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        };
    }

    private void zoomImageFromThumb(final ImageView thumbView) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        expandedImageView = (ImageView) getActivity().findViewById(
                R.id.expanded_image_event);
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
        getActivity().findViewById(R.id.container_event)
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
