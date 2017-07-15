package in.ac.iitb.gymkhana.iitbapp.api.model;

public class NewsFeedRequest {
    public static final int FOLLOWED = 0;
    public static final int POPULAR = 1;

    private int type;
    private int from;
    private int to;

    public NewsFeedRequest(int type, int from, int to) {
        this.type = type;
        this.from = from;
        this.to = to;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
