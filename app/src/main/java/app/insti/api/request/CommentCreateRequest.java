package app.insti.api.request;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shivam Sharma on 21-09-2018.
 */

public class CommentCreateRequest {
    @SerializedName("text")
    private String text;

    public CommentCreateRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
