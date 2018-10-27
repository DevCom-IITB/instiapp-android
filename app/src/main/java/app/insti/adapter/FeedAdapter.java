package app.insti.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.insti.Utils;
import app.insti.api.model.Event;
import app.insti.api.model.Venue;

public class FeedAdapter extends CardAdapter<Event> {

    public FeedAdapter(List<Event> eventList, Fragment fragment) {
        super(eventList, fragment);
    }

    @Override
    public void onClick(Event event, FragmentActivity fragmentActivity) {
        Utils.openEventFragment(event, fragmentActivity);
    }

    @Override
    public String getTitle(Event event) {
        return event.getEventName();
    }

    @Override
    public String getSubtitle(Event event)
    {
        String subtitle = "";

        Date startTime = event.getEventStartTime();
        Date endTime = event.getEventEndTime();
        Date timeNow = Calendar.getInstance().getTime();
        boolean eventStarted = timeNow.compareTo(startTime) > 0;
        boolean eventEnded = timeNow.compareTo(endTime) > 0;

        if (eventEnded)
            subtitle += "Event ended | ";
        else if(eventStarted)
        {
            long difference = endTime.getTime() - timeNow.getTime();
            long minutes = difference / (60 * 1000 ) % 60;
            long hours = difference / (60 * 60 * 1000) % 24;
            long days = difference / (24 * 60 * 60 * 1000);
            String timeDiff = "";
            if (days > 0)
                timeDiff += Long.toString(days) + "D ";
            if (hours > 0)
                timeDiff += Long.toString(hours) + "H ";


            timeDiff += Long.toString(minutes) + "M";

            subtitle += "Ends in " + timeDiff + " | " ;
        }

        Timestamp timestamp = event.getEventStartTime();
        if (timestamp != null) {
            Date Date = new Date(timestamp.getTime());
            SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("dd MMM");
            SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("HH:mm");

            subtitle += simpleDateFormatDate.format(Date) + " | " + simpleDateFormatTime.format(Date);
        }
        StringBuilder eventVenueName = new StringBuilder();
        for (Venue venue : event.getEventVenues()) {
            eventVenueName.append(", ").append(venue.getVenueShortName());
        }
        if (!eventVenueName.toString().equals(""))
            subtitle += " | " + eventVenueName.toString().substring(2);

        return subtitle;
    }

    @Override
    public String getAvatarUrl(Event event) {
        return event.getEventImageURL();
    }

    @Override
    public String getBigImageUrl(Event event) {
        if (event.isEventBigImage()) {
            return event.getEventImageURL();
        }
        return null;
    }
}
