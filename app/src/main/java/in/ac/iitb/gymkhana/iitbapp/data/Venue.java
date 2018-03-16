package in.ac.iitb.gymkhana.iitbapp.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "venues")
class Venue {
    @ColumnInfo(name = "id")
    @SerializedName("id")
    String venueID;
    @ColumnInfo(name = "name")
    @SerializedName("name")
    String venueName;
    @ColumnInfo(name = "lat")
    @SerializedName("lat")
    double venueLatitude;
    @ColumnInfo(name = "lng")
    @SerializedName("lng")
    double venueLongitude;

    public Venue(String venueID, String venueName, double venueLatitude, double venueLongitude) {
        this.venueID = venueID;
        this.venueName = venueName;
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
}
