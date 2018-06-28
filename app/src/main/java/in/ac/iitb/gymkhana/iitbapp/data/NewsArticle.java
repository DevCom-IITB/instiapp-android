package in.ac.iitb.gymkhana.iitbapp.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

@Entity(tableName = "news")

public class NewsArticle {

    @PrimaryKey(autoGenerate = true)
    int db_id;

    @ColumnInfo(name = "id")
    @SerializedName("id")
    private String articleID;

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

    @ColumnInfo(name = "body")
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
