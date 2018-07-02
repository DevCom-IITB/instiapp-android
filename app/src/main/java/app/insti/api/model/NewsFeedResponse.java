package app.insti.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.insti.data.Event;

public class NewsFeedResponse {
    @SerializedName("data")
    private List<Event> events;
    @SerializedName("count")
    private int count;

    public NewsFeedResponse(List<Event> events, int count) {
        this.events = events;
        this.count = count;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
