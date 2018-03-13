package in.ac.iitb.gymkhana.iitbapp.data;

import android.arch.persistence.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Event {
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
    String eventStartTime;
    @ColumnInfo(name = "end_time")
    @SerializedName("end_time")
    String eventEndTime;
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
}
