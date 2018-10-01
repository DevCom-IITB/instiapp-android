package app.insti.api.response;

import com.google.gson.annotations.SerializedName;

import app.insti.api.model.User;

public class LoginResponse {
    @SerializedName("sessionid")
    private String sessionID;
    @SerializedName("user")
    private int userID;
    @SerializedName("profile_id")
    private String profileID;
    @SerializedName("profile")
    private User user;

    public LoginResponse(String sessionID, int userID, String profileID, User user) {
        this.sessionID = sessionID;
        this.userID = userID;
        this.profileID = profileID;
        this.user = user;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getProfileID() {
        return profileID;
    }

    public void setProfileID(String profileID) {
        this.profileID = profileID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
