package app.insti.api.model;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.insti.interfaces.CardInterface;

public class User implements CardInterface {
    @NonNull()
    @SerializedName("id")
    private String userID;

    @SerializedName("name")
    private String userName;

    @SerializedName("profile_pic")
    private String userProfilePictureUrl;

    @SerializedName("events_interested")
    private List<Event> userInterestedEvents;

    @SerializedName("events_going")
    private List<Event> userGoingEvents;

    @SerializedName("email")
    private String userEmail;

    @SerializedName("roll_no")
    private String userRollNumber;

    @SerializedName("contact_no")
    private String userContactNumber;

    @SerializedName("show_contact_no")
    private Boolean showContactNumber;

    @SerializedName("about")
    private String userAbout;

    @SerializedName("followed_bodies")
    private List<Body> userFollowedBodies;

    @SerializedName("followed_bodies_id")
    private List<String> userFollowedBodiesID;

    @SerializedName("roles")
    private List<Role> userRoles;

    @SerializedName("institute_roles")
    private List<Role> userInstituteRoles;

    @SerializedName("former_roles")
    private List<Role> userFormerRoles;

    @SerializedName("website_url")
    private String userWebsiteURL;

    @SerializedName("ldap_id")
    private String userLDAPId;

    @SerializedName("hostel")
    private String hostel;

    private String currentRole;

    public User(@NonNull String userID, String userName, String userProfilePictureUrl, List<Event> userInterestedEvents, List<Event> userGoingEvents, String userEmail, String userRollNumber, String userContactNumber, Boolean showContactNumber, String userAbout, List<Body> userFollowedBodies, List<String> userFollowedBodiesID, List<Role> userRoles, List<Role> userInstituteRoles, List<Role> userFormerRoles, String userWebsiteURL, String userLDAPId, String hostel, String currentRole) {
        this.userID = userID;
        this.userName = userName;
        this.userProfilePictureUrl = userProfilePictureUrl;
        this.userInterestedEvents = userInterestedEvents;
        this.userGoingEvents = userGoingEvents;
        this.userEmail = userEmail;
        this.userRollNumber = userRollNumber;
        this.userContactNumber = userContactNumber;
        this.showContactNumber = showContactNumber;
        this.userAbout = userAbout;
        this.userFollowedBodies = userFollowedBodies;
        this.userFollowedBodiesID = userFollowedBodiesID;
        this.userRoles = userRoles;
        this.userInstituteRoles = userInstituteRoles;
        this.userFormerRoles = userFormerRoles;
        this.userWebsiteURL = userWebsiteURL;
        this.userLDAPId = userLDAPId;
        this.hostel = hostel;
        this.currentRole = currentRole;
    }

    public static User fromString(String json) {
        return new Gson().fromJson(json, User.class);
    }

    @NonNull
    public String getUserID() {
        return userID;
    }

    public void setUserID(@NonNull String userID) {
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

    public List<Role> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<Role> userRoles) {
        this.userRoles = userRoles;
    }

    public List<Role> getUserInstituteRoles() {
        return userInstituteRoles;
    }

    public void setUserInstituteRoles(List<Role> userInstituteRoles) {
        this.userInstituteRoles = userInstituteRoles;
    }

    public List<Role> getUserFormerRoles() {
        return userFormerRoles;
    }

    public void setUserFormerRoles(List<Role> userFormerRoles) {
        this.userFormerRoles = userFormerRoles;
    }

    public String getUserWebsiteURL() {
        return userWebsiteURL;
    }

    public void setUserWebsiteURL(String userWebsiteURL) {
        this.userWebsiteURL = userWebsiteURL;
    }

    public String getUserLDAPId() {
        return userLDAPId;
    }

    public void setUserLDAPId(String userLDAPId) {
        this.userLDAPId = userLDAPId;
    }

    public String getHostel() {
        return hostel;
    }

    public void setHostel(String hostel) {
        this.hostel = hostel;
    }

    public Boolean getShowContactNumber() {
        return showContactNumber;
    }

    public void setShowContactNumber(Boolean showContactNumber) {
        this.showContactNumber = showContactNumber;
    }

    public String getCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(String currentRole) {
        this.currentRole = currentRole;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public long getId() {
        return getUserID().hashCode();
    }

    public String getTitle() {
        return getUserName();
    }

    public String getSubtitle() {
        if (getCurrentRole() == null || getCurrentRole().equals("")) {
            return getUserLDAPId();
        } else {
            return getCurrentRole();
        }
    }

    public String getAvatarUrl() {
        return getUserProfilePictureUrl();
    }
}
