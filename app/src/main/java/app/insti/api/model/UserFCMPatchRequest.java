package app.insti.api.model;

import com.google.gson.annotations.SerializedName;

public class UserFCMPatchRequest {
    @SerializedName("fcm_id")
    private String userFCMId;

    public UserFCMPatchRequest(String userFCMId) {
        this.userFCMId = userFCMId;
    }
}
