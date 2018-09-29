package app.insti.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ExploreResponse {
    @SerializedName("bodies")
    private List<Body> bodies;
    @SerializedName("events")
    private List<Event> events;
    @SerializedName("users")
    private List<User> users;

    public ExploreResponse(List<Body> bodies, List<Event> events, List<User> users) {
        this.bodies = bodies;
        this.events = events;
        this.users = users;
    }

    public List<Body> getBodies() {
        return bodies;
    }

    public void setBodies(List<Body> bodies) {
        this.bodies = bodies;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
