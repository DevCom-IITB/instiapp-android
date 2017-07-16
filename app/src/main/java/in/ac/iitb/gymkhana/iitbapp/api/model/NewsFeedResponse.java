package in.ac.iitb.gymkhana.iitbapp.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsFeedResponse {
    @SerializedName("posts")
    private List<Event> events;

    public NewsFeedResponse(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
