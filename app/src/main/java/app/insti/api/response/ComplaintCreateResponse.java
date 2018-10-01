package app.insti.api.response;

/**
 * Created by Shivam Sharma on 18-09-2018.
 */

public class ComplaintCreateResponse {

    private String result;
    private String complaintId;

    public ComplaintCreateResponse(String result, String complaintId) {
        this.result = result;
        this.complaintId = complaintId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }
}
