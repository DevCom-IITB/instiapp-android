package app.insti.api.model;

import java.util.List;

public class NotificationsResponse {
    private String result;
    private List<AppNotification> notifications;

    public NotificationsResponse(String result, List<AppNotification> notifications) {
        this.result = result;
        this.notifications = notifications;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<AppNotification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<AppNotification> notifications) {
        this.notifications = notifications;
    }
}
