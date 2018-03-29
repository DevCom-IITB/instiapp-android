package in.ac.iitb.gymkhana.iitbapp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.ac.iitb.gymkhana.iitbapp.Constants;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.SessionManager;
import in.ac.iitb.gymkhana.iitbapp.api.RetrofitInterface;
import in.ac.iitb.gymkhana.iitbapp.api.ServiceGenerator;
import in.ac.iitb.gymkhana.iitbapp.data.Event;
import in.ac.iitb.gymkhana.iitbapp.data.Venue;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.noties.markwon.Markwon;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends BaseFragment implements View.OnClickListener {
    Event event;
    Button goingButton;
    Button interestedButton;
    Button notGoingButton;

    SessionManager sessionManager;

    String TAG = "EventFragment";


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

        Bundle bundle = getArguments();
        String eventJson = bundle.getString(Constants.EVENT_JSON);
        Log.d(TAG, "onStart: " + eventJson);
        event = new Gson().fromJson(eventJson, Event.class);
        inflateViews(event);
        sessionManager=new SessionManager(getContext());
    }

    private void inflateViews(Event event) {
        ImageView eventPicture = (ImageView) getActivity().findViewById(R.id.event_picture_2);
        TextView eventTitle = (TextView) getActivity().findViewById(R.id.event_page_title);
        TextView eventDate = (TextView) getActivity().findViewById(R.id.event_page_date);
        TextView eventTime = (TextView) getActivity().findViewById(R.id.event_page_time);
        TextView eventVenue = (TextView) getActivity().findViewById(R.id.event_page_venue);
        TextView eventDescription = (TextView) getActivity().findViewById(R.id.event_page_description);
        goingButton = getActivity().findViewById(R.id.going_button);
        interestedButton = getActivity().findViewById(R.id.interested_button);
        notGoingButton = getActivity().findViewById(R.id.not_going_button);

        Picasso.with(getContext()).load(event.getEventImageURL()).into(eventPicture);
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
            eventVenueName.append(", ").append(venue.getVenueName());
        }

        if (!eventVenueName.toString().equals(""))
            eventVenue.setText(eventVenueName.toString().substring(2));
        goingButton.setOnClickListener(this);
        interestedButton.setOnClickListener(this);
        notGoingButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        goingButton.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        interestedButton.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        notGoingButton.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        int status = 0;
        switch (view.getId()) {
            case R.id.going_button:
                status = Constants.STATUS_GOING;
                break;
            case R.id.interested_button:
                status = Constants.STATUS_INTERESTED;
                break;
            case R.id.not_going_button:
                status = Constants.STATUS_NOT_GOING;
                break;
        }
        final int finalStatus=status;
        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.updateUserEventStatus("sessionid=" + getArguments().getString(Constants.SESSION_ID), event.getEventID(), status).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    sessionManager.setUserEventStatus("sessionid=" + getArguments().getString(SESSION_ID), event.getEventID(), finalStatus);
                    sessionManager.setIfUpdated(true);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                sessionManager.setUserEventStatus("sessionid=" + getArguments().getString(SESSION_ID), event.getEventID(), finalStatus);
                sessionManager.setIfUpdated(false);
                Toast.makeText(getContext(), "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }
}
