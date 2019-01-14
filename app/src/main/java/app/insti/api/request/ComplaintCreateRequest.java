package app.insti.api.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Shivam Sharma on 18-09-2018.
 */

public class ComplaintCreateRequest {
    @SerializedName("description")
    private String complaintDescription;
    @SerializedName("suggestions")
    private String complaintSuggestions;
    @SerializedName("location_details")
    private String complaintLocationDetails;
    @SerializedName("location_description")
    private String complaintLocation;
    @SerializedName("latitude")
    private Float complaintLatitude;
    @SerializedName("longitude")
    private Float complaintLongitude;
    @SerializedName("tags")
    private List<String> tags;
    @SerializedName("images")
    private List<String> images;

    public ComplaintCreateRequest(String complaintDescription, String complaintSuggestions, String complaintLocationDetails, String complaintLocation, Float complaintLatitude, Float complaintLongitude, List<String> tags, List<String> images) {
        this.complaintDescription = complaintDescription;
        this.complaintSuggestions = complaintSuggestions;
        this.complaintLocationDetails = complaintLocationDetails;
        this.complaintLocation = complaintLocation;
        this.complaintLatitude = complaintLatitude;
        this.complaintLongitude = complaintLongitude;
        this.tags = tags;
        this.images = images;
    }

    public String getComplaintDescription() {
        return complaintDescription;
    }

    public void setComplaintDescription(String complaintDescription) {
        this.complaintDescription = complaintDescription;
    }

    public String getComplaintSuggestions() {
        return complaintSuggestions;
    }

    public void setComplaintSuggestions(String complaintSuggestions) {
        this.complaintSuggestions = complaintSuggestions;
    }

    public String getComplaintLocationDetails() {
        return complaintLocationDetails;
    }

    public void setComplaintLocationDetails(String complaintLocationDetails) {
        this.complaintLocationDetails = complaintLocationDetails;
    }

    public String getComplaintLocation() {
        return complaintLocation;
    }

    public void setComplaintLocation(String complaintLocation) {
        this.complaintLocation = complaintLocation;
    }

    public Float getComplaintLatitude() {
        return complaintLatitude;
    }

    public void setComplaintLatitude(Float complaintLatitude) {
        this.complaintLatitude = complaintLatitude;
    }

    public Float getComplaintLongitude() {
        return complaintLongitude;
    }

    public void setComplaintLongitude(Float complaintLongitude) {
        this.complaintLongitude = complaintLongitude;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}
