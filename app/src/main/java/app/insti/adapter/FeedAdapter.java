package app.insti.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Calendar;


import app.insti.Constants;
import app.insti.activity.MainActivity;
import app.insti.fragment.EventFragment;
import app.insti.interfaces.ItemClickListener;
import app.insti.R;
import app.insti.api.model.Event;
import app.insti.api.model.Venue;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private List<Event> events;
    private Context context;
    private Fragment mFragment;

    public FeedAdapter(List<Event> events, Fragment fragment) {
        this.events = events;
        mFragment = fragment;
    }

    public void getSubtitle(ViewHolder viewHolder, Event currentEvent)
    {
        String subtitle = "";

        Date startTime = currentEvent.getEventStartTime();
        Date endTime = currentEvent.getEventEndTime();
        Date timeNow = Calendar.getInstance().getTime();
        boolean eventStarted = timeNow.compareTo(startTime) > 0;
        boolean eventEnded = timeNow.compareTo(endTime) > 0;

        if (eventEnded)
            subtitle += "Event ended | ";
        else if(eventStarted)
        {
            int difference = (int) (endTime.getTime() - timeNow.getTime());
            int minutes = difference / (60 * 1000 ) % 60;
            int hours = difference / (60 * 60 * 1000) % 24;
            int days = (int) difference / (24 * 60 * 60 * 1000);
            String timeDiff = "";
            if (days > 0)
                timeDiff += Integer.toString(days) + "D ";
            if (hours > 0)
                timeDiff += Integer.toString(hours) + "H ";


            timeDiff += Integer.toString(minutes) + "M";

            subtitle += "Ends in " + timeDiff + " | " ;
        }

        Timestamp timestamp = currentEvent.getEventStartTime();
        if (timestamp != null) {
            Date Date = new Date(timestamp.getTime());
            SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("dd MMM");
            SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("HH:mm");

            subtitle += simpleDateFormatDate.format(Date) + " | " + simpleDateFormatTime.format(Date);
        }
        StringBuilder eventVenueName = new StringBuilder();
        for (Venue venue : currentEvent.getEventVenues()) {
            eventVenueName.append(", ").append(venue.getVenueShortName());
        }
        if (!eventVenueName.toString().equals(""))
            subtitle += " | " + eventVenueName.toString().substring(2);

        viewHolder.eventSubtitle.setText(subtitle);
        return ;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 1;
        else return 2;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.feed_card, viewGroup, false);

        final ViewHolder postViewHolder = new ViewHolder(postView);
        postView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String eventJson = new Gson().toJson(events.get(postViewHolder.getAdapterPosition()));
                Bundle bundle = new Bundle();
                bundle.putString(Constants.EVENT_JSON, eventJson);
                EventFragment eventFragment = new EventFragment();
                eventFragment.setArguments(bundle);
                FragmentManager manager = mFragment.getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
                transaction.replace(R.id.framelayout_for_fragment, eventFragment, eventFragment.getTag());
                transaction.addToBackStack(eventFragment.getTag()).commit();
            }
        });

        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Event currentEvent = events.get(i);
        viewHolder.eventTitle.setText(currentEvent.getEventName());

        getSubtitle(viewHolder, currentEvent);

        if (currentEvent.isEventBigImage()) {
            viewHolder.eventBigPicture.setVisibility(View.VISIBLE);
            viewHolder.eventPicture.setVisibility(View.GONE);
            Picasso.get().load(currentEvent.getEventImageURL()).into(viewHolder.eventBigPicture);
        } else {
            Picasso.get().load(
                    Constants.resizeImageUrl(currentEvent.getEventImageURL(), 200)
            ).into(viewHolder.eventPicture);
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView eventPicture;
        private TextView eventTitle;
        private TextView eventSubtitle;
        private ImageView eventBigPicture;

        public ViewHolder(View itemView) {
            super(itemView);

            eventPicture = (ImageView) itemView.findViewById(R.id.object_picture);
            eventTitle = (TextView) itemView.findViewById(R.id.object_title);
            eventSubtitle = (TextView) itemView.findViewById(R.id.object_subtitle);
            eventBigPicture = (ImageView) itemView.findViewById(R.id.big_object_picture);
        }
    }
}
