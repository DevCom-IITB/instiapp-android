package in.ac.iitb.gymkhana.iitbapp.api.model;

import com.google.gson.annotations.SerializedName;

public class AppNotification {
    @SerializedName("notification_type")
    private int notificationType;
    @SerializedName("notification_id")
    private String notificationId;
    @SerializedName("notification_related_id")
    private String notificationRelatedId;
    @SerializedName("notification_name")
    private String notificationName;
    @SerializedName("notification_description")
    private String notificationDescription;
    @SerializedName("notification_image")
    private String notificationImage;

    public AppNotification(int notificationType, String notificationId, String notificationRelatedId, String notificationName, String notificationDescription, String notificationImage) {
        this.notificationType = notificationType;
        this.notificationId = notificationId;
        this.notificationRelatedId = notificationRelatedId;
        this.notificationName = notificationName;
        this.notificationDescription = notificationDescription;
        this.notificationImage = notificationImage;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationRelatedId() {
        return notificationRelatedId;
    }

    public void setNotificationRelatedId(String notificationRelatedId) {
        this.notificationRelatedId = notificationRelatedId;
    }

    public String getNotificationName() {
        return notificationName;
    }

    public void setNotificationName(String notificationName) {
        this.notificationName = notificationName;
    }

    public String getNotificationDescription() {
        return notificationDescription;
    }

    public void setNotificationDescription(String notificationDescription) {
        this.notificationDescription = notificationDescription;
    }

    public String getNotificationImage() {
        return notificationImage;
    }

    public void setNotificationImage(String notificationImage) {
        this.notificationImage = notificationImage;
    }
}
