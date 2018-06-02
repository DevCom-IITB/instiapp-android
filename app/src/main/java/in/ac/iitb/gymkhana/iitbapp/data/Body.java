package in.ac.iitb.gymkhana.iitbapp.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "bodies")
public class Body {
    @PrimaryKey(autoGenerate = true)
    int db_id;

    @ColumnInfo(name = "id")
    @SerializedName("id")
    String bodyID;
    @ColumnInfo(name = "str_id")
    @SerializedName("str_id")
    String bodyStrID;
    @ColumnInfo(name = "name")
    @SerializedName("name")
    String bodyName;
    @ColumnInfo(name = "short_description")
    @SerializedName("short_description")
    String bodyShortDescription;
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
    @ColumnInfo(name = "website_url")
    @SerializedName("website_url")
    String bodyWebsiteURL;
    @ColumnInfo(name = "blog_url")
    @SerializedName("blog_url")
    String bodyBlogURL;

    public Body(String bodyID, String bodyStrID, String bodyName, String bodyShortDescription, String bodyDescription, String bodyImageURL, List<Body> bodyChildren, List<Body> bodyParents, List<Event> bodyEvents, int bodyFollowersCount, String bodyWebsiteURL, String bodyBlogURL) {
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

}