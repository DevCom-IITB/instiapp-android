package app.insti.api.request;

import com.google.gson.annotations.SerializedName;

public class UserShowContactPatchRequest {
    @SerializedName("show_contact_no")
    private Boolean showContactNumber;

    public UserShowContactPatchRequest(Boolean showContactNumber) {
        this.showContactNumber = showContactNumber;
    }
}
