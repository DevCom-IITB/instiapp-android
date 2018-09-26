package app.insti.data;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class NewsArticle {
    @NonNull()
    @SerializedName("id")
    private String articleID;

    @SerializedName("link")
    private String link;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("published")
    private Timestamp published;

    @SerializedName("body")
    private Body body;

    public NewsArticle(String articleID, String link, String title, String content, Timestamp published, Body body) {
        this.articleID = articleID;
        this.link = link;
        this.title = title;
        this.content = content;
        this.published = published;
        this.body = body;
    }

    public String getArticleID() {
        return articleID;
    }

    public void setArticleID(String articleID) {
        this.articleID = articleID;
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

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
