package in.ac.iitb.gymkhana.iitbapp.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    int db_id;

    @ColumnInfo(name = "id")
    @SerializedName("id")
    String userID;
    @ColumnInfo(name = "name")
    @SerializedName("name")
    String userName;
    @ColumnInfo(name = "profile_pic")
    @SerializedName("profile_pic")
    String userProfilePictureUrl;
    @ColumnInfo(name = "events_interested")
    @SerializedName("events_interested")
    List<Event> userInterestedEvents;
    @ColumnInfo(name = "events_going")
    @SerializedName("events_going")
    List<Event> userGoingEvents;
    @ColumnInfo(name = "email")
    @SerializedName("email")
    String userEmail;
    @ColumnInfo(name = "year")
    @SerializedName("year")
    int userYear;
    @ColumnInfo(name = "roll_no")
    @SerializedName("roll_no")
    String userRollNumber;
    @ColumnInfo(name = "contact_no")
    @SerializedName("contact_no")
    String userContactNumber;
    @ColumnInfo(name = "about")
    @SerializedName("about")
    String userAbout;
    @ColumnInfo(name = "followed_bodies")
    @SerializedName("followed_bodies")
    List<Body> userFollowedBodies;
    @ColumnInfo(name = "followed_bodies_id")
    @SerializedName("followed_bodies_id")
    List<String> userFollowedBodiesID;

    public User(String userID, String userName, String userProfilePictureUrl, List<Event> userInterestedEvents, List<Event> userGoingEvents, String userEmail, int userYear, String userRollNumber, String userContactNumber, String userAbout, List<Body> userFollowedBodies, List<String> userFollowedBodiesID) {
        this.userID = userID;
        this.userName = userName;
        this.userProfilePictureUrl = userProfilePictureUrl;
        this.userInterestedEvents = userInterestedEvents;
        this.userGoingEvents = userGoingEvents;
        this.userEmail = userEmail;
        this.userYear = userYear;
        this.userRollNumber = userRollNumber;
        this.userContactNumber = userContactNumber;
        this.userAbout = userAbout;
        this.userFollowedBodies = userFollowedBodies;
        this.userFollowedBodiesID = userFollowedBodiesID;
    }

    public static User fromString(String json) {
        return new Gson().fromJson(json, User.class);
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfilePictureUrl() {
        return userProfilePictureUrl;
    }

    public void setUserProfilePictureUrl(String userProfilePictureUrl) {
        this.userProfilePictureUrl = userProfilePictureUrl;
    }

    public List<Event> getUserInterestedEvents() {
        return userInterestedEvents;
    }

    public void setUserInterestedEvents(List<Event> userInterestedEvents) {
        this.userInterestedEvents = userInterestedEvents;
    }

    public List<Event> getUserGoingEvents() {
        return userGoingEvents;
    }

    public void setUserGoingEvents(List<Event> userGoingEvents) {
        this.userGoingEvents = userGoingEvents;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getUserYear() {
        return userYear;
    }

    public void setUserYear(int userYear) {
        this.userYear = userYear;
    }

    public String getUserRollNumber() {
        return userRollNumber;
    }

    public void setUserRollNumber(String userRollNumber) {
        this.userRollNumber = userRollNumber;
    }

    public String getUserContactNumber() {
        return userContactNumber;
    }

    public void setUserContactNumber(String userContactNumber) {
        this.userContactNumber = userContactNumber;
    }

    public String getUserAbout() {
        return userAbout;
    }

    public void setUserAbout(String userAbout) {
        this.userAbout = userAbout;
    }

    public List<Body> getUserFollowedBodies() {
        return userFollowedBodies;
    }

    public void setUserFollowedBodies(List<Body> userFollowedBodies) {
        this.userFollowedBodies = userFollowedBodies;
    }

    public List<String> getUserFollowedBodiesID() {
        return userFollowedBodiesID;
    }

    public void setUserFollowedBodiesID(List<String> userFollowedBodiesID) {
        this.userFollowedBodiesID = userFollowedBodiesID;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
