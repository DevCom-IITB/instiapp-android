package app.insti.api.model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import app.insti.R;
import app.insti.interfaces.CardInterface;

public class Event implements CardInterface {
    @NonNull()
    @SerializedName("id")
    private String eventID;

    @SerializedName("str_id")
    private String eventStrID;

    @SerializedName("name")
    private String eventName;

    @SerializedName("description")
    private String eventDescription;

    @SerializedName("image_url")
    private String eventImageURL;

    @SerializedName("start_time")
    private Timestamp eventStartTime;

    @SerializedName("end_time")
    private Timestamp eventEndTime;

    @SerializedName("all_day")
    private boolean allDayEvent;

    @SerializedName("venues")
    private List<Venue> eventVenues;

    @SerializedName("bodies")
    private List<Body> eventBodies;

    @SerializedName("interested_count")
    private int eventInterestedCount;

    @SerializedName("going_count")
    private int eventGoingCount;

    @SerializedName("interested")
    private List<User> eventInterested;

    @SerializedName("going")
    private List<User> eventGoing;

    @SerializedName("website_url")
    private String eventWebsiteURL;

    @SerializedName("user_ues")
    private int eventUserUes;

    @SerializedName("offered_achievements")
    private List<OfferedAchievement> eventOfferedAchievements;

    private boolean eventBigImage = false;

    public Event(@NonNull String eventID) {
        this.eventID = eventID;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventStrID() {
        return eventStrID;
    }

    public void setEventStrID(String eventStrID) {
        this.eventStrID = eventStrID;
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
        // Fallback to body image if event has no image
        if (eventImageURL == null && getEventBodies() != null && getEventBodies().size() > 0) {
            return getEventBodies().get(0).getBodyImageURL();
        }
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

    public String getEventWebsiteURL() {
        return eventWebsiteURL;
    }

    public void setEventWebsiteURL(String eventWebsiteURL) {
        this.eventWebsiteURL = eventWebsiteURL;
    }

    public int getEventUserUes() {
        return eventUserUes;
    }

    public void setEventUserUes(int eventUserUes) {
        this.eventUserUes = eventUserUes;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public boolean isEventBigImage() {
        return eventBigImage;
    }

    public void setEventBigImage(boolean eventBigImage) {
        this.eventBigImage = eventBigImage;
    }

    public List<OfferedAchievement> getEventOfferedAchievements() {
        return eventOfferedAchievements;
    }

    public void setEventOfferedAchievements(List<OfferedAchievement> eventOfferedAchievements) {
        this.eventOfferedAchievements = eventOfferedAchievements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(eventID, event.eventID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventID);
    }

    public long getId() {
        return hashCode();
    }

    public String getTitle() {
        return getEventName();
    }

    public String getSubtitle()
    {
        String subtitle = "";

        Date startTime = getEventStartTime();
        Date endTime = getEventEndTime();
        Date timeNow = Calendar.getInstance().getTime();
        boolean eventStarted = timeNow.compareTo(startTime) > 0;
        boolean eventEnded = timeNow.compareTo(endTime) > 0;

        if (eventEnded)
            subtitle += "Ended | ";
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

        Timestamp timestamp = getEventStartTime();
        if (timestamp != null) {
            Date Date = new Date(timestamp.getTime());
            SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("dd MMM");
            SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("HH:mm");

            subtitle += simpleDateFormatDate.format(Date) + " | " + simpleDateFormatTime.format(Date);
        }

        String eventVenueName = getEventVenueString();
        if (!eventVenueName.equals(""))
            subtitle += " | " + eventVenueName;

        return subtitle;
    }

    public String getEventVenueString() {
        StringBuilder eventVenueName = new StringBuilder();
        for (Venue venue : getEventVenues()) {
            eventVenueName.append(", ").append(venue.getVenueShortName());
        }
        return eventVenueName.toString().equals("") ? "" : eventVenueName.toString().substring(2);
    }

    public String getAvatarUrl() {
        return getEventImageURL();
    }

    public int getBadge() {
        return getEventOfferedAchievements().size() > 0 ? R.drawable.badge_medal : 0;
    }
}
