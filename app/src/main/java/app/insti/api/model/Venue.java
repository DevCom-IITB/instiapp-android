package app.insti.api.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Venue {
    @NonNull()
    @SerializedName("id")
    private String venueID;

    @SerializedName("name")
    private String venueName;

    @SerializedName("short_name")
    private String venueShortName;

    @SerializedName("description")
    private String venueDescripion;

    @SerializedName("parent")
    private String venueParentId;

    @SerializedName("parent_relation")
    private String venueParentRelation;

    @SerializedName("group_id")
    private Integer venueGroupId;

    @SerializedName("pixel_x")
    private Integer venuePixelX;

    @SerializedName("pixel_y")
    private Integer venuePixelY;

    @SerializedName("reusable")
    private Boolean venueReusable;

    @SerializedName("lat")
    private double venueLatitude;

    @SerializedName("lng")
    private double venueLongitude;

    public Venue(@NonNull String venueID) {
        this.venueID = venueID;
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
