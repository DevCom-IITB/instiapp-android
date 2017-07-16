package in.ac.iitb.gymkhana.iitbapp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import in.ac.iitb.gymkhana.iitbapp.Constants;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.api.model.Event;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {


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
        Event event = new Gson().fromJson(eventJson, Event.class);
        inflateViews(event);
    }

    private void inflateViews(Event event) {
        ImageView eventPicture = (ImageView) getActivity().findViewById(R.id.event_picture_2);
        TextView eventTitle = (TextView) getActivity().findViewById(R.id.event_title_2);
        TextView eventDetails = (TextView) getActivity().findViewById(R.id.event_details_2);
        TextView eventDescription = (TextView) getActivity().findViewById(R.id.event_description_2);

        Picasso.with(getContext()).load(event.getEventImage()).into(eventPicture);
        eventTitle.setText(event.getEventName());
        eventDetails.setText(event.getEventDescription());
        eventDescription.setText(event.getEventDescription());
    }
}
