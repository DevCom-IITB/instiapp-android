package app.insti.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "news")

public class Notification {
    @NonNull()
    @PrimaryKey()
    @ColumnInfo(name = "id")
    @SerializedName("id")
    private Integer notificationId;

    @ColumnInfo(name = "verb")
    @SerializedName("verb")
    private String notificationVerb;

    @ColumnInfo(name = "unread")
    @SerializedName("unread")
    private boolean notificationUnread;

    @ColumnInfo(name = "actor_type")
    @SerializedName("actor_type")
    private String notificationActorType;

    @ColumnInfo(name = "actor")
    @SerializedName("actor")
    private Object notificationActor;

    public Notification(@NonNull Integer notificationId, String notificationVerb, boolean notificationUnread, String notificationActorType, Object notificationActor) {
        this.notificationId = notificationId;
        this.notificationVerb = notificationVerb;
        this.notificationUnread = notificationUnread;
        this.notificationActorType = notificationActorType;
        this.notificationActor = notificationActor;
    }

    @NonNull
    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(@NonNull Integer notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationVerb() {
        return notificationVerb;
    }

    public void setNotificationVerb(String notificationVerb) {
        this.notificationVerb = notificationVerb;
    }

    public boolean isNotificationUnread() {
        return notificationUnread;
    }

    public void setNotificationUnread(boolean notificationUnread) {
        this.notificationUnread = notificationUnread;
    }

    public String getNotificationActorType() {
        return notificationActorType;
    }

    public void setNotificationActorType(String notificationActorType) {
        this.notificationActorType = notificationActorType;
    }

    public Object getNotificationActor() {
        return notificationActor;
    }

    public void setNotificationActor(Object notificationActor) {
        this.notificationActor = notificationActor;
    }
}
