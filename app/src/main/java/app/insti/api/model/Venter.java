package app.insti.api.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Shivam Sharma on 04-09-2018.
 */

public class Venter {

    public static class Complaint{
        @SerializedName("id")
        private String complaintID;
        @SerializedName("created_by")
        private User complaintCreatedBy;
        @SerializedName("description")
        private String description;
        @SerializedName("suggestions")
        private String complaintSuggestions;
        @SerializedName("location_details")
        private String complaintLocationDetails;
        @SerializedName("report_date")
        private String complaintReportDate;
        @SerializedName("status")
        private String status;
        @SerializedName("latitude")
        private Float latitude;
        @SerializedName("longitude")
        private Float longitude;
        @SerializedName("location_description")
        private String locationDescription;
        @SerializedName("tags")
        private List<TagUri> tags;
        @SerializedName("users_up_voted")
        private List<User> usersUpVoted;
        @SerializedName("is_subscribed")
        private boolean complaintSubscribed;
        @SerializedName("upvoted")
        private boolean complaintUpvoted;
        @SerializedName("images")
        private List<String> images;
        @SerializedName("comments")
        private List<Comment> comment;

        @NonNull
        public String getComplaintID() {
            return complaintID;
        }

        public void setComplaintID(@NonNull String complaintID) {
            this.complaintID = complaintID;
        }

        public User getComplaintCreatedBy() {
            return complaintCreatedBy;
        }

        public void setComplaintCreatedBy(User complaintCreatedBy) {
            this.complaintCreatedBy = complaintCreatedBy;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getComplaintSuggestions() {
            return complaintSuggestions;
        }

        public void setComplaintSuggestions(String complaiintSuggestions) {
            this.complaintSuggestions = complaiintSuggestions;
        }

        public String getComplaintLocationDetails() {
            return complaintLocationDetails;
        }

        public void setComplaintLocationDetails(String complaintLocationDetails) {
            this.complaintLocationDetails = complaintLocationDetails;
        }


        public String getComplaintReportDate() {
            return complaintReportDate;
        }

        public void setComplaintReportDate(String complaintReportDate) {
            this.complaintReportDate = complaintReportDate;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Float getLatitude() {
            return latitude;
        }

        public void setLatitude(Float latitude) {
            this.latitude = latitude;
        }

        public Float getLongitude() {
            return longitude;
        }

        public void setLongitude(Float longitude) {
            this.longitude = longitude;
        }

        public String getLocationDescription() {
            return locationDescription;
        }

        public void setLocationDescription(String locationDescription) {
            this.locationDescription = locationDescription;
        }

        public List<TagUri> getTags() {
            return tags;
        }

        public void setTags(List<TagUri> tags) {
            this.tags = tags;
        }

        public List<User> getUsersUpVoted() {
            return usersUpVoted;
        }

        public void setUsersUpVoted(List<User> usersUpVoted) {
            this.usersUpVoted = usersUpVoted;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public List<Comment> getComment() {
            return comment;
        }

        public void setComment(List<Comment> comment) {
            this.comment = comment;
        }
        public boolean isComplaintUpvoted() {
            return complaintUpvoted;
        }

        public void setComplaintUpvoted(boolean complaintUpvoted) {
            this.complaintUpvoted = complaintUpvoted;
        }

        public boolean isComplaintSubscribed() {
            return complaintSubscribed;
        }

        public void setComplaintSubscribed(boolean complaintSubscribed) {
            this.complaintSubscribed = complaintSubscribed;
        }
    }

    public static class TagUri {
        @SerializedName("id")
        private String id;
        @SerializedName("tag_uri")
        private String tagUri;

        @NonNull
        public String getId() {
            return id;
        }

        public void setId(@NonNull String id) {
            this.id = id;
        }

        public String getTagUri() {
            return tagUri;
        }

        public void setTagUri(String tagUri) {
            this.tagUri = tagUri;
        }
    }

    public static class Comment {

        @SerializedName("id")
        private String id;
        @SerializedName("time")
        private String time;
        @SerializedName("text")
        private String text;
        @SerializedName("commented_by")
        private User commented_by;

        @NonNull
        public String getId() {
            return id;
        }

        public void setId(@NonNull String id) {
            this.id = id;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public User getUser() {
            return commented_by;
        }

        public void setUser(User commented_by) {
            this.commented_by = commented_by;
        }
    }
}
