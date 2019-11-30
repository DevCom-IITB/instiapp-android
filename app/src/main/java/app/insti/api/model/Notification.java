package app.insti.api.model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import app.insti.Utils;
import app.insti.interfaces.CardInterface;

public class Notification implements CardInterface {

    private final String TYPE_EVENT = "event";
    private final String TYPE_NEWSENTRY = "newsentry";
    private final String TYPE_BLOG = "blogentry";

    @NonNull()
    @SerializedName("id")
    private Integer notificationId;

    @SerializedName("verb")
    private String notificationVerb;

    @SerializedName("unread")
    private boolean notificationUnread;

    @SerializedName("actor_type")
    private String notificationActorType;

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

    public long getId() {
        return getNotificationId().hashCode();
    }

    public String getTitle() {
        if (isEvent()) {
            return getEvent().getEventName();
        } else if (isNews()) {
            return getNews().getTitle();
        } else if (isBlogPost()) {
            return getBlogPost().getTitle();
        }
        return "Notification";
    }

    public String getSubtitle() {
        return getNotificationVerb();
    }

    public String getAvatarUrl() {
        if (isEvent()) {
            return getEvent().getEventImageURL();
        } else if (isNews()) {
            return getNews().getBody().getBodyImageURL();
        }
        return null;
    }

    public boolean isEvent() {
        return getNotificationActorType().contains(TYPE_EVENT);
    }

    public boolean isNews() {
        return getNotificationActorType().contains(TYPE_NEWSENTRY);
    }

    public boolean isBlogPost() {
        return getNotificationActorType().contains(TYPE_BLOG);
    }

    public Event getEvent() {
        Gson gson = Utils.gson;
        return gson.fromJson(gson.toJson(getNotificationActor()), Event.class);
    }

    public NewsArticle getNews() {
        Gson gson = Utils.gson;
        return gson.fromJson(gson.toJson(getNotificationActor()), NewsArticle.class);
    }

    public PlacementBlogPost getBlogPost() {
        Gson gson = Utils.gson;
        return gson.fromJson(gson.toJson(getNotificationActor()), PlacementBlogPost.class);
    }

    public int getBadge() { return 0; }
}
