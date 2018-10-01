package app.insti.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImageUploadResponse {
    @SerializedName("id")
    private String pictureID;
    @SerializedName("picture")
    private String pictureURL;

    public ImageUploadResponse(String pictureID, String pictureURL) {
        this.pictureID = pictureID;
        this.pictureURL = pictureURL;
    }

    public String getPictureID() {
        return pictureID;
    }

    public void setPictureID(String pictureID) {
        this.pictureID = pictureID;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }
}
