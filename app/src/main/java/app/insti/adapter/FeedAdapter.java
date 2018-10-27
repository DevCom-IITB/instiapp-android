package app.insti.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.List;

import app.insti.Utils;
import app.insti.api.model.Event;

public class FeedAdapter extends CardAdapter<Event> {

    public FeedAdapter(List<Event> eventList, Fragment fragment) {
        super(eventList, fragment);
    }

    @Override
    public void onClick(Event event, FragmentActivity fragmentActivity) {
        Utils.openEventFragment(event, fragmentActivity);
    }

    @Override
    public String getBigImageUrl(Event event) {
        if (event.isEventBigImage()) {
            return event.getEventImageURL();
        }
        return null;
    }
}
