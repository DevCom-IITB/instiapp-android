package app.insti.api.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

import app.insti.interfaces.Clickable;

import static app.insti.Utils.openWebURL;

public class PlacementBlogPost implements Clickable {
    @NonNull()
    @SerializedName("id")
    private String postID;

    @SerializedName("link")
    private String link;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

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

    @Override
    public View.OnClickListener getOnClickListener(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebURL(context, link);
            }
        };
    }
}
