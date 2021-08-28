package app.insti.api.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import app.insti.interfaces.SearchDataInterface;

public class SearchDataPost implements SearchDataInterface {
    @NonNull()
    @SerializedName("id")
    private String questionID;

    @SerializedName("question")
    private String question;

    @SerializedName("answer")
    private String description;   

    public SearchDataPost(String description, String title) {
        this.description = description;
        this.question = title;
    }

    public String getTitle() {
        Log.d("myTag", question);
        return question;
    }


    public String getId() {
        return questionID;
    }

    public void setTitle(String title) {
        this.question = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}