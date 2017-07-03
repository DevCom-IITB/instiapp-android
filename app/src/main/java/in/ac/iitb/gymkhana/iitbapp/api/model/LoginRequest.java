package in.ac.iitb.gymkhana.iitbapp.api.model;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("AUTHORIZATION_CODE")
    private String authCode;
    @SerializedName("reg_id")
    private String regId;

    public LoginRequest(String authCode, String regId) {
        this.authCode = authCode;
        this.regId = regId;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }
}
