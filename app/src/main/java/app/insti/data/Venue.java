package app.insti.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Venue {
    @NonNull()
    @SerializedName("id")
    String venueID;

    @SerializedName("name")
    String venueName;

    @SerializedName("short_name")
    String venueShortName;

    @SerializedName("description")
    String venueDescripion;

    @SerializedName("parent")
    String venueParentId;

    @SerializedName("parent_relation")
    String venueParentRelation;

    @SerializedName("group_id")
    Integer venueGroupId;

    @SerializedName("pixel_x")
    Integer venuePixelX;

    @SerializedName("pixel_y")
    Integer venuePixelY;

    @SerializedName("reusable")
    Boolean venueReusable;

    @SerializedName("lat")
    double venueLatitude;

    @SerializedName("lng")
    double venueLongitude;

    public Venue(String venueID, String venueName, String venueShortName, String venueDescripion, String venueParentId, String venueParentRelation, Integer venueGroupId, Integer venuePixelX, Integer venuePixelY, Boolean venueReusable, double venueLatitude, double venueLongitude) {
        this.venueID = venueID;
        this.venueName = venueName;
        this.venueShortName = venueShortName;
        this.venueDescripion = venueDescripion;
        this.venueParentId = venueParentId;
        this.venueParentRelation = venueParentRelation;
        this.venueGroupId = venueGroupId;
        this.venuePixelX = venuePixelX;
        this.venuePixelY = venuePixelY;
        this.venueReusable = venueReusable;
        this.venueLatitude = venueLatitude;
        this.venueLongitude = venueLongitude;
    }

    public String getVenueID() {
        return venueID;
    }

    public void setVenueID(String venueID) {
        this.venueID = venueID;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public double getVenueLatitude() {
        return venueLatitude;
    }

    public void setVenueLatitude(double venueLatitude) {
        this.venueLatitude = venueLatitude;
    }

    public double getVenueLongitude() {
        return venueLongitude;
    }

    public void setVenueLongitude(double venueLongitude) {
        this.venueLongitude = venueLongitude;
    }

    public String getVenueShortName() {
        return venueShortName;
    }

    public void setVenueShortName(String venueShortName) {
        this.venueShortName = venueShortName;
    }

    public String getVenueDescripion() {
        return venueDescripion;
    }

    public void setVenueDescripion(String venueDescripion) {
        this.venueDescripion = venueDescripion;
    }

    public String getVenueParentId() {
        return venueParentId;
    }

    public void setVenueParentId(String venueParentId) {
        this.venueParentId = venueParentId;
    }

    public String getVenueParentRelation() {
        return venueParentRelation;
    }

    public void setVenueParentRelation(String venueParentRelation) {
        this.venueParentRelation = venueParentRelation;
    }

    public Integer getVenueGroupId() {
        return venueGroupId;
    }

    public void setVenueGroupId(Integer venueGroupId) {
        this.venueGroupId = venueGroupId;
    }

    public Integer getVenuePixelX() {
        return venuePixelX;
    }

    public void setVenuePixelX(Integer venuePixelX) {
        this.venuePixelX = venuePixelX;
    }

    public Integer getVenuePixelY() {
        return venuePixelY;
    }

    public void setVenuePixelY(Integer venuePixelY) {
        this.venuePixelY = venuePixelY;
    }

    public Boolean getVenueReusable() {
        return venueReusable;
    }

    public void setVenueReusable(Boolean venueReusable) {
        this.venueReusable = venueReusable;
    }
}
