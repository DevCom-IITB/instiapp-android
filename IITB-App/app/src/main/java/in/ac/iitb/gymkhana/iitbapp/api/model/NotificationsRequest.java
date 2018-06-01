package in.ac.iitb.gymkhana.iitbapp.api.model;

public class NotificationsRequest {
    private int from;
    private int to;

    public NotificationsRequest(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }
}
