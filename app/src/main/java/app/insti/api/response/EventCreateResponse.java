package app.insti.api.response;

/**
 * Created by mrunz on 15/7/17.
 */

public class EventCreateResponse {

    private String result;


    private String eventId;

    public EventCreateResponse(String result, String eventId) {
        this.result = result;
        this.eventId = eventId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }


    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
