package in.ac.iitb.gymkhana.iitbapp.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "bodies")
class Body {
    @ColumnInfo(name = "id")
    @SerializedName("id")
    String bodyID;
    @ColumnInfo(name = "name")
    @SerializedName("name")
    String bodyName;
    @ColumnInfo(name = "description")
    @SerializedName("description")
    String bodyDescription;
    @ColumnInfo(name = "image_url")
    @SerializedName("image_url")
    String bodyImageURL;
    @ColumnInfo(name = "children")
    @SerializedName("children")
    List<Body> bodyChildren;
    @ColumnInfo(name = "parents")
    @SerializedName("parents")
    List<Body> bodyParents;
    @ColumnInfo(name = "events")
    @SerializedName("events")
    List<Event> bodyEvents;
    @ColumnInfo(name = "followers_count")
    @SerializedName("followers_count")
    int bodyFollowersCount;

    public Body(String bodyID, String bodyName, String bodyDescription, String bodyImageURL, List<Body> bodyChildren, List<Body> bodyParents, List<Event> bodyEvents, int bodyFollowersCount) {
        this.bodyID = bodyID;
        this.bodyName = bodyName;
        this.bodyDescription = bodyDescription;
        this.bodyImageURL = bodyImageURL;
        this.bodyChildren = bodyChildren;
        this.bodyParents = bodyParents;
        this.bodyEvents = bodyEvents;
        this.bodyFollowersCount = bodyFollowersCount;
    }

    public String getBodyID() {
        return bodyID;
    }

    public void setBodyID(String bodyID) {
        this.bodyID = bodyID;
    }

    public String getBodyName() {
        return bodyName;
    }

    public void setBodyName(String bodyName) {
        this.bodyName = bodyName;
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
}