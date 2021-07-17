package app.insti.api.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import app.insti.interfaces.SearchDataInterface;

public class SearchDataPost implements SearchDataInterface {
    @NonNull()
    @SerializedName("id")
    private String questionID;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;   

    public SearchDataPost(String description, String title) {
        this.description = description;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }


    public String getId() {
        return questionID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}