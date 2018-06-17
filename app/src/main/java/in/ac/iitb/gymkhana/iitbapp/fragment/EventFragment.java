package in.ac.iitb.gymkhana.iitbapp.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import in.ac.iitb.gymkhana.iitbapp.Constants;
import in.ac.iitb.gymkhana.iitbapp.ItemClickListener;
import in.ac.iitb.gymkhana.iitbapp.MainActivity;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.ShareURLMaker;
import in.ac.iitb.gymkhana.iitbapp.adapter.BodyCardAdapter;
import in.ac.iitb.gymkhana.iitbapp.api.RetrofitInterface;
import in.ac.iitb.gymkhana.iitbapp.api.ServiceGenerator;
import in.ac.iitb.gymkhana.iitbapp.data.Event;
import in.ac.iitb.gymkhana.iitbapp.data.Body;
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
    ImageButton shareEventButton;
    ImageButton webEventButton;
    RecyclerView bodyRecyclerView;
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
        notGoingButton = getActivity().findViewById(R.id.not_going_button);
        shareEventButton = getActivity().findViewById(R.id.share_event_button);
        webEventButton = getActivity().findViewById(R.id.web_event_button);

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

       /* if(((LinearLayout) getActivity().findViewById(R.id.body_container)).getChildCount() == 0) {
            for (Body body : event.getEventBodies()) {
                Fragment bodyCardFragment = BodyCardFragment.newInstance(body);
                getChildFragmentManager().beginTransaction()
                        .add(R.id.body_container, bodyCardFragment, getTag())
                        .disallowAddToBackStack()
                        .commit();
            }
        }*/
       final List<Body> bodyList = event.getEventBodies();
       bodyRecyclerView= (RecyclerView) getActivity().findViewById(R.id.body_card_RecyclerView);
       BodyCardAdapter bodyCardAdapter = new BodyCardAdapter(bodyList, new ItemClickListener() {
           @Override
           public void onItemClick(View v, int position) {
               Body body = bodyList.get(position);
               BodyFragment bodyFragment = BodyFragment.newInstance(body);
               bodyFragment.setArguments(getArguments());
               FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
               ft.replace(R.id.framelayout_for_fragment, bodyFragment, bodyFragment.getTag());
               ft.addToBackStack(bodyFragment.getTag());
               ft.commit();
           }
       });
       bodyRecyclerView.setAdapter(bodyCardAdapter);
       bodyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        if (!eventVenueName.toString().equals(""))
            eventVenue.setText(eventVenueName.toString().substring(2));
        goingButton.setOnClickListener(this);
        interestedButton.setOnClickListener(this);
        notGoingButton.setOnClickListener(this);
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
       if (event.getEventWebsiteURL() != null)
      {
        webEventButton.setVisibility(View.VISIBLE);
      }
        webEventButton.setOnClickListener(new View.OnClickListener() {
           String eventwebURL = event.getEventWebsiteURL();
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(eventwebURL));
                startActivity(browserIntent);
            }
        });
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
        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.updateUserEventStatus("sessionid=" + getArguments().getString(Constants.SESSION_ID), event.getEventID(), status).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    //TODO: Set flag for details updated so as to not try again when connected
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                //TODO: Store the status offline and update when connected
                Toast.makeText(getContext(), "Network Error", Toast.LENGTH_LONG).show();
            }
        });
    }
}
