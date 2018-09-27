package app.insti.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Shivam Sharma on 04-09-2018.
 */

public class Venter {

    @Entity(tableName = "complaints")
    public static class Complaint {
        @NonNull
        @PrimaryKey()
        @ColumnInfo(name = "id")
        @SerializedName("id")
        String complaintID;
        @ColumnInfo(name = "created_by")
        @SerializedName("created_by")
        User complaintCreatedBy;
        @ColumnInfo(name = "description")
        @SerializedName("description")
        String description;
        @ColumnInfo(name = "report_date")
        @SerializedName("report_date")
        Timestamp complaintReportDate;
        @ColumnInfo(name = "status")
        @SerializedName("status")
        String status;
        @ColumnInfo(name = "latitude")
        @SerializedName("latitude")
        Float latitude;
        @ColumnInfo(name = "longitude")
        @SerializedName("longitude")
        Float longitude;
        @ColumnInfo(name = "location_description")
        @SerializedName("location_description")
        String locationDescription;
        @ColumnInfo(name = "tags")
        @SerializedName("tags")
        List<TagUri> tags;
        @ColumnInfo(name = "users_up_voted")
        @SerializedName("users_up_voted")
        List<User> usersUpVoted;
        @ColumnInfo(name = "images")
        @SerializedName("images")
        List<String> images;
        @SerializedName("comments")
        List<Comment> comment;

        public Complaint(@NonNull String complaintID, User complaintCreatedBy, String description, Timestamp complaintReportDate, String status, Float latitude, Float longitude, String locationDescription, List<TagUri> tags, List<User> usersUpVoted, List<String> images, List<Comment> comment) {
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

        public Timestamp getComplaintReportDate() {
            return complaintReportDate;
        }

        public void setComplaintReportDate(Timestamp complaintReportDate) {
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
    }

    @Entity(tableName = "tag_uris")
    public static class TagUri {
        @NonNull
        @PrimaryKey()
        @ColumnInfo(name = "id")
        @SerializedName("id")
        String id;
        @ColumnInfo(name = "tag_uri")
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

    @Entity(tableName = "comment")
    public static class Comment {

        @NonNull
        @PrimaryKey()
        @ColumnInfo(name = "id")
        @SerializedName("id")
        String id;
        @ColumnInfo(name = "time")
        @SerializedName("time")
        Timestamp time;
        @ColumnInfo(name = "text")
        @SerializedName("text")
        String text;
        @ColumnInfo(name = "commented_by")
        @SerializedName("commented_by")
        User commented_by;

        @NonNull
        public String getId() {
            return id;
        }

        public void setId(@NonNull String id) {
            this.id = id;
        }

        public Timestamp getTime() {
            return time;
        }

        public void setTime(Timestamp time) {
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
