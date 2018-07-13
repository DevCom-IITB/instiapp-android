package app.insti.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import app.insti.Constants;
import app.insti.ItemClickListener;
import app.insti.MainActivity;
import app.insti.R;
import app.insti.ShareURLMaker;
import app.insti.adapter.BodyAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.ServiceGenerator;
import app.insti.data.AppDatabase;
import app.insti.data.Body;
import app.insti.data.Event;
import app.insti.data.Venue;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.noties.markwon.Markwon;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends BaseFragment {
    Event event;
    Button goingButton;
    Button interestedButton;
    ImageButton navigateButton;
    ImageButton webEventButton;
    ImageButton shareEventButton;
    RecyclerView bodyRecyclerView;
    String TAG = "EventFragment";
    private AppDatabase appDatabase;

    public EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        /* Initialize */
        appDatabase = AppDatabase.getAppDatabase(getContext());

        Bundle bundle = getArguments();
        String eventJson = bundle.getString(Constants.EVENT_JSON);
        Log.d(TAG, "onStart: " + eventJson);
        event = new Gson().fromJson(eventJson, Event.class);
        inflateViews(event);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(event.getEventName());
    }

    private void inflateViews(final Event event) {
        ImageView eventPicture = (ImageView) getActivity().findViewById(R.id.event_picture_2);
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

        // Fallback to image of first body if event has no image
        if (event.getEventImageURL() == null) {
            event.setEventImageURL(event.getEventBodies().get(0).getBodyImageURL());
        }

        Picasso.get().load(event.getEventImageURL()).into(eventPicture);
        eventTitle.setText(event.getEventName());
        Markwon.setMarkdown(eventDescription, event.getEventDescription());
        Timestamp timestamp = event.getEventStartTime();
        Date Date = new Date(timestamp.getTime());
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("dd MMM");
        SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("HH:mm a");
        eventDate.setText(simpleDateFormatDate.format(Date));
        eventTime.setText(simpleDateFormatTime.format(Date));
        StringBuilder eventVenueName = new StringBuilder();

        for (Venue venue : event.getEventVenues()) {
            eventVenueName.append(", ").append(venue.getVenueShortName());
        }

        final List<Body> bodyList = event.getEventBodies();
        bodyRecyclerView = (RecyclerView) getActivity().findViewById(R.id.body_card_recycler_view);
        BodyAdapter bodyAdapter = new BodyAdapter(bodyList, new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Body body = bodyList.get(position);
                BodyFragment bodyFragment = BodyFragment.newInstance(body);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                ft.replace(R.id.framelayout_for_fragment, bodyFragment, bodyFragment.getTag());
                ft.addToBackStack(bodyFragment.getTag());
                ft.commit();
            }
        });
        bodyRecyclerView.setAdapter(bodyAdapter);
        bodyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        if (!eventVenueName.toString().equals(""))
            eventVenue.setText(eventVenueName.toString().substring(2));

        interestedButton.setOnClickListener(getUESOnClickListener(1));

        goingButton.setOnClickListener(getUESOnClickListener(2));

        interestedButton.setBackgroundColor(getResources().getColor(event.getEventUserUes() == Constants.STATUS_INTERESTED ? R.color.colorAccent : R.color.colorWhite));
        goingButton.setBackgroundColor(getResources().getColor(event.getEventUserUes() == Constants.STATUS_GOING ? R.color.colorAccent : R.color.colorWhite));

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
    }

    View.OnClickListener getUESOnClickListener(final int status) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int endStatus = event.getEventUserUes() == status ? 0 : status;
                RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
                retrofitInterface.updateUserEventStatus(((MainActivity) getActivity()).getSessionIDHeader(), event.getEventID(), endStatus).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            event.setEventUserUes(endStatus);
                            new updateDbEvent().execute(event);
                            interestedButton.setBackgroundColor(getResources().getColor(endStatus == Constants.STATUS_INTERESTED ? R.color.colorAccent : R.color.colorWhite));
                            goingButton.setBackgroundColor(getResources().getColor(endStatus == Constants.STATUS_GOING ? R.color.colorAccent : R.color.colorWhite));
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

    private class updateDbEvent extends AsyncTask<Event, Void, Integer> {
        @Override
        protected Integer doInBackground(Event... event) {
            appDatabase.dbDao().updateEvent(event[0]);
            return 1;
        }
    }

}
