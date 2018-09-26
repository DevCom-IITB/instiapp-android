package app.insti.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Body {
    @SerializedName("id")
    String bodyID;
    @SerializedName("str_id")
    String bodyStrID;
    @SerializedName("name")
    String bodyName;
    @SerializedName("short_description")
    String bodyShortDescription;
    @SerializedName("description")
    String bodyDescription;
    @SerializedName("image_url")
    String bodyImageURL;
    @SerializedName("children")
    List<Body> bodyChildren;
    @SerializedName("parents")
    List<Body> bodyParents;
    @SerializedName("events")
    List<Event> bodyEvents;
    @SerializedName("followers_count")
    int bodyFollowersCount;
    @SerializedName("website_url")
    String bodyWebsiteURL;
    @SerializedName("blog_url")
    String bodyBlogURL;
    @SerializedName("user_follows")
    boolean bodyUserFollows;
    @SerializedName("roles")
    List<Role> bodyRoles;

    public Body(@NonNull String bodyID) {
        this.bodyID = bodyID;
    }

    public Body(String bodyID, String bodyStrID, String bodyName, String bodyShortDescription, String bodyDescription, String bodyImageURL, List<Body> bodyChildren, List<Body> bodyParents, List<Event> bodyEvents, int bodyFollowersCount, String bodyWebsiteURL, String bodyBlogURL, boolean bodyUserFollows, List<Role> bodyRoles) {
        this.bodyID = bodyID;
        this.bodyStrID = bodyStrID;
        this.bodyName = bodyName;
        this.bodyShortDescription = bodyShortDescription;
        this.bodyDescription = bodyDescription;
        this.bodyImageURL = bodyImageURL;
        this.bodyChildren = bodyChildren;
        this.bodyParents = bodyParents;
        this.bodyEvents = bodyEvents;
        this.bodyFollowersCount = bodyFollowersCount;
        this.bodyWebsiteURL = bodyWebsiteURL;
        this.bodyBlogURL = bodyBlogURL;
        this.bodyUserFollows = bodyUserFollows;
        this.bodyRoles = bodyRoles;
    }

    public String getBodyID() {
        return bodyID;
    }

    public void setBodyID(String bodyID) {
        this.bodyID = bodyID;
    }

    public String getBodyStrID() {
        return bodyStrID;
    }

    public void setBodyStrID(String bodyStrID) {
        this.bodyStrID = bodyStrID;
    }

    public String getBodyName() {
        return bodyName;
    }

    public void setBodyName(String bodyName) {
        this.bodyName = bodyName;
    }

    public String getBodyShortDescription() {
        return bodyShortDescription;
    }

    public void setBodyShortDescription(String bodyShortDescription) {
        this.bodyShortDescription = bodyShortDescription;
    }

    public String getBodyDescription() {
        return bodyDescription;
    }

    public void setBodyDescription(String bodyDescription) {
        this.bodyDescription = bodyDescription;
    }

    public String getBodyImageURL() {
        return bodyImageURL;
    }

    public void setBodyImageURL(String bodyImageURL) {
        this.bodyImageURL = bodyImageURL;
    }

    public List<Body> getBodyChildren() {
        return bodyChildren;
    }

    public void setBodyChildren(List<Body> bodyChildren) {
        this.bodyChildren = bodyChildren;
    }

    public List<Body> getBodyParents() {
        return bodyParents;
    }

    public void setBodyParents(List<Body> bodyParents) {
        this.bodyParents = bodyParents;
    }

    public List<Event> getBodyEvents() {
        return bodyEvents;
    }

    public void setBodyEvents(List<Event> bodyEvents) {
        this.bodyEvents = bodyEvents;
    }

    public int getBodyFollowersCount() {
        return bodyFollowersCount;
    }

    public void setBodyFollowersCount(int bodyFollowersCount) {
        this.bodyFollowersCount = bodyFollowersCount;
    }

    public String getBodyWebsiteURL() {
        return bodyWebsiteURL;
    }

    public void setBodyWebsiteURL(String bodyWebsiteURL) {
        this.bodyWebsiteURL = bodyWebsiteURL;
    }

    public String getBodyBlogURL() {
        return bodyBlogURL;
    }

    public void setBodyBlogURL(String bodyBlogURL) {
        this.bodyBlogURL = bodyBlogURL;
    }

    public boolean getBodyUserFollows() {
        return bodyUserFollows;
    }

    public void setBodyUserFollows(boolean bodyUserFollows) {
        this.bodyUserFollows = bodyUserFollows;
    }

    public List<Role> getBodyRoles() {
        return bodyRoles;
    }

    public void setBodyRoles(List<Role> bodyRoles) {
        this.bodyRoles = bodyRoles;
    }
}