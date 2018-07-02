package app.insti.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

@Entity(tableName = "placementBlogPosts")

public class PlacementBlogPost {
    @NonNull()
    @PrimaryKey()
    @ColumnInfo(name = "id")
    @SerializedName("id")
    private String postID;

    @ColumnInfo(name = "link")
    @SerializedName("link")
    private String link;

    @ColumnInfo(name = "title")
    @SerializedName("title")
    private String title;

    @ColumnInfo(name = "content")
    @SerializedName("content")
    private String content;

    @ColumnInfo(name = "published")
    @SerializedName("published")
    private Timestamp published;

    public PlacementBlogPost(String postID, String link, String title, String content, Timestamp published) {
        this.postID = postID;
        this.link = link;
        this.title = title;
        this.content = content;
        this.published = published;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getPublished() {
        return published;
    }

    public void setPublished(Timestamp published) {
        this.published = published;
    }
}
