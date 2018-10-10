package app.insti.api.model;

import android.support.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import java.util.List;

import app.insti.api.model.User;
import app.insti.interfaces.Browsable;

/**
 * Created by Shivam Sharma on 04-09-2018.
 */

public class Venter {

    public static class Complaint{
        @NonNull
        @SerializedName("id")
        private String complaintID;
        @SerializedName("created_by")
        User complaintCreatedBy;
        @SerializedName("description")
        private String description;
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
        @SerializedName("images")
        private List<String> images;
        @SerializedName("comments")
        private List<Comment> comment;
        private int voteCount;

        public Complaint(@NonNull String complaintID, User complaintCreatedBy, String description, String complaintReportDate, String status, Float latitude, Float longitude, String locationDescription, List<TagUri> tags, List<User> usersUpVoted, List<String> images, List<Comment> comment) {
            this.complaintID = complaintID;
            this.complaintCreatedBy = complaintCreatedBy;
            this.description = description;
            this.complaintReportDate = complaintReportDate;
            this.status = status;
            this.latitude = latitude;
            this.longitude = longitude;
            this.locationDescription = locationDescription;
            this.tags = tags;
            this.usersUpVoted = usersUpVoted;
            this.images = images;
            this.comment = comment;
        }

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

        public int getVoteCount() {
            return voteCount;
        }

        public void setVoteCount(int voteCount) {
            this.voteCount = voteCount;
        }
    }

    public static class TagUri {
        @NonNull
        @SerializedName("id")
        String id;
        @SerializedName("tag_uri")
        String tagUri;

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

        @NonNull
        @SerializedName("id")
        String id;
        @SerializedName("time")
        String time;
        @SerializedName("text")
        String text;
        @SerializedName("commented_by")
        User commented_by;

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
