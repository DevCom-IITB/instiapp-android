package in.ac.iitb.gymkhana.iitbapp.api.model;

import android.media.Image;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

/**
 * Created by mrunz on 15/7/17.
 */

public class EventCreateRequest {
    @SerializedName("event_name")
    private String eventName;
    @SerializedName("event_description")
    private String eventDescription;
    @SerializedName("event_timing_from")
    private Timestamp eventTimingFrom;
    @SerializedName("event_timing_to")
    private Timestamp eventTimingTo;
    @SerializedName("event_venue_id")
    private int eventVenueID;
    @SerializedName("event_maplocation_id")
    private int eventMapLocationId;
    @SerializedName("event_venue")
    private String eventVenue;
    @SerializedName("public_status")
    private int publicStatus;


    public EventCreateRequest(String eventName, String eventDescription,String eventVenue,Timestamp eventTimingFrom,Timestamp eventTimingTo,int publicStatus,@Nullable int eventVenueID,@Nullable int eventMapLocationId) {
        this.eventName=eventName;
        this.eventDescription=eventDescription;
        this.eventTimingFrom=eventTimingFrom;
        this.eventTimingTo=eventTimingTo;
        this.eventVenueID=eventVenueID;
        this.eventMapLocationId=eventMapLocationId;
        this.eventVenue=eventVenue;
        this.publicStatus=publicStatus;

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

    public Timestamp getEventTimingFrom() {
        return eventTimingFrom;
    }

    public void setEventTimingFrom(Timestamp eventTimingFrom) {
        this.eventTimingFrom = eventTimingFrom;
    }

    public Timestamp getEventTimingTo() {
        return eventTimingTo;
    }

    public void setEventTimingTo(Timestamp eventTimingTo) {
        this.eventTimingTo = eventTimingTo;
    }

    public int getEventVenueID() {
        return eventVenueID;
    }

    public void setEventVenueID(@Nullable  int eventVenueID) {
        this.eventVenueID = eventVenueID;
    }

    public int getEventMapLocationId() {
        return eventMapLocationId;
    }

    public void setEventMapLocationId(@Nullable  int eventMapLocationId) {
        this.eventMapLocationId = eventMapLocationId;
    }

    public String getEventVenue() {
        return eventVenue;
    }

    public void setEventVenue(String eventVenue) {
        this.eventVenue = eventVenue;
    }

    public int getPublicStatus() {
        return publicStatus;
    }

    public void setPublicStatus(int publicStatus) {
        this.publicStatus = publicStatus;
    }


}
