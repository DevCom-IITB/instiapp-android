package app.insti.api.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EventCreateRequest {
    @SerializedName("name")
    private String eventName;
    @SerializedName("description")
    private String eventDescription;
    @SerializedName("image_url")
    private String eventImageURL;
    @SerializedName("start_time")
    private String eventStartTime;
    @SerializedName("end_time")
    private String eventEndTime;
    @SerializedName("all_day")
    private boolean allDayEvent;
    @SerializedName("venue_names")
    private List<String> eventVenueNames;
    @SerializedName("bodies_id")
    private List<String> eventBodiesID;

    public EventCreateRequest(String eventName, String eventDescription, String eventImageURL, String eventStartTime, String eventEndTime, boolean allDayEvent, List<String> eventVenueNames, List<String> eventBodiesID) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventImageURL = eventImageURL;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.allDayEvent = allDayEvent;
        this.eventVenueNames = eventVenueNames;
        this.eventBodiesID = eventBodiesID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventImageURL() {
        return eventImageURL;
    }

    public void setEventImageURL(String eventImageURL) {
        this.eventImageURL = eventImageURL;
    }

    public String getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(String eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public String getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(String eventEndTime) {
        this.eventEndTime = eventEndTime;
    }

    public boolean isAllDayEvent() {
        return allDayEvent;
    }

    public void setAllDayEvent(boolean allDayEvent) {
        this.allDayEvent = allDayEvent;
    }

    public List<String> getEventVenueNames() {
        return eventVenueNames;
    }

    public void setEventVenueNames(List<String> eventVenueNames) {
        this.eventVenueNames = eventVenueNames;
    }

    public List<String> getEventBodiesID() {
        return eventBodiesID;
    }

    public void setEventBodiesID(List<String> eventBodiesID) {
        this.eventBodiesID = eventBodiesID;
    }
}
