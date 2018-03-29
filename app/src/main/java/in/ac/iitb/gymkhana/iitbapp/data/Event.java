package in.ac.iitb.gymkhana.iitbapp.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.List;

@Entity(tableName = "events")

public class Event {

    @PrimaryKey(autoGenerate = true)
    int db_id;
    @ColumnInfo(name = "id")
    @SerializedName("id")
    String eventID;
    @ColumnInfo(name = "name")
    @SerializedName("name")
    String eventName;
    @ColumnInfo(name = "description")
    @SerializedName("description")
    String eventDescription;
    @ColumnInfo(name = "image_url")
    @SerializedName("image_url")
    String eventImageURL;
    @ColumnInfo(name = "start_time")
    @SerializedName("start_time")
    Timestamp eventStartTime;
    @ColumnInfo(name = "end_time")
    @SerializedName("end_time")
    Timestamp eventEndTime;
    @ColumnInfo(name = "all_day")
    @SerializedName("all_day")
    boolean allDayEvent;
    @ColumnInfo(name = "venues")
    @SerializedName("venues")
    List<Venue> eventVenues;
    @ColumnInfo(name = "bodies")
    @SerializedName("bodies")
    List<Body> eventBodies;
    @ColumnInfo(name = "interested_count")
    @SerializedName("interested_count")
    int eventInterestedCount;
    @ColumnInfo(name = "going_count")
    @SerializedName("going_count")
    int eventGoingCount;
    @ColumnInfo(name = "interested")
    @SerializedName("interested")
    List<User> eventInterested;
    @ColumnInfo(name = "going")
    @SerializedName("going")
    List<User> eventGoing;

    public Event(String eventID, String eventName, String eventDescription, String eventImageURL, Timestamp eventStartTime, Timestamp eventEndTime, boolean allDayEvent, List<Venue> eventVenues, List<Body> eventBodies, int eventInterestedCount, int eventGoingCount, List<User> eventInterested, List<User> eventGoing) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventImageURL = eventImageURL;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.allDayEvent = allDayEvent;
        this.eventVenues = eventVenues;
        this.eventBodies = eventBodies;
        this.eventInterestedCount = eventInterestedCount;
        this.eventGoingCount = eventGoingCount;
        this.eventInterested = eventInterested;
        this.eventGoing = eventGoing;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
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

    public Timestamp getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(Timestamp eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public Timestamp getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(Timestamp eventEndTime) {
        this.eventEndTime = eventEndTime;
    }

    public boolean isAllDayEvent() {
        return allDayEvent;
    }

    public void setAllDayEvent(boolean allDayEvent) {
        this.allDayEvent = allDayEvent;
    }

    public List<Venue> getEventVenues() {
        return eventVenues;
    }

    public void setEventVenues(List<Venue> eventVenues) {
        this.eventVenues = eventVenues;
    }

    public List<Body> getEventBodies() {
        return eventBodies;
    }

    public void setEventBodies(List<Body> eventBodies) {
        this.eventBodies = eventBodies;
    }

    public int getEventInterestedCount() {
        return eventInterestedCount;
    }

    public void setEventInterestedCount(int eventInterestedCount) {
        this.eventInterestedCount = eventInterestedCount;
    }

    public int getEventGoingCount() {
        return eventGoingCount;
    }

    public void setEventGoingCount(int eventGoingCount) {
        this.eventGoingCount = eventGoingCount;
    }

    public List<User> getEventInterested() {
        return eventInterested;
    }

    public void setEventInterested(List<User> eventInterested) {
        this.eventInterested = eventInterested;
    }

    public List<User> getEventGoing() {
        return eventGoing;
    }

    public void setEventGoing(List<User> eventGoing) {
        this.eventGoing = eventGoing;
    }
}
