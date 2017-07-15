package in.ac.iitb.gymkhana.iitbapp.api.model;

import com.google.gson.annotations.SerializedName;

public class Event {
    @SerializedName("event_name")
    String eventName;
    @SerializedName("event_description")
    String eventDescription;
    @SerializedName("event_image")
    String eventImage;
    @SerializedName("event_creator_name")
    String eventCreatorName;
    @SerializedName("event_creator_id")
    String eventCreatorId;
    @SerializedName("event_going_status")
    int eventEnthu;

    public Event(String eventName, String eventDescription, String eventImage, String eventCreatorName, String eventCreatorId, int eventEnthu) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventImage = eventImage;
        this.eventCreatorName = eventCreatorName;
        this.eventCreatorId = eventCreatorId;
        this.eventEnthu = eventEnthu;
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

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }

    public String getEventCreatorName() {
        return eventCreatorName;
    }

    public void setEventCreatorName(String eventCreatorName) {
        this.eventCreatorName = eventCreatorName;
    }

    public String getEventCreatorId() {
        return eventCreatorId;
    }

    public void setEventCreatorId(String eventCreatorId) {
        this.eventCreatorId = eventCreatorId;
    }

    public int getEventEnthu() {
        return eventEnthu;
    }

    public void setEventEnthu(int eventEnthu) {
        this.eventEnthu = eventEnthu;
    }
}
