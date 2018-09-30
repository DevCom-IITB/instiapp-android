package app.insti.api.request;

import com.google.gson.annotations.SerializedName;

public class UserFCMPatchRequest {
    @SerializedName("fcm_id")
    private String userFCMId;

    @SerializedName("android_version")
    private int userAndroidVersion;

    public UserFCMPatchRequest(String userFCMId, int userAndroidVersion) {
        this.userFCMId = userFCMId;
        this.userAndroidVersion = userAndroidVersion;
    }
}
