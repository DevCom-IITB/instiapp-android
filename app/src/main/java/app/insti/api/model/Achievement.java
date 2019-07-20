package app.insti.api.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import app.insti.interfaces.CardInterface;

public class Achievement implements CardInterface {
    @NonNull()
    @SerializedName("id")
    private String achievementID;

    @SerializedName("title")
    private String achievementTitle;

    @SerializedName("description")
    private String achievementDescription;

    @SerializedName("body_detail")
    private Body achievementBody;

    @SerializedName("event_detail")
    private Event achievementEvent;

    @SerializedName("dismissed")
    private boolean achievementDismissed;

    @SerializedName("verified")
    private boolean achievementVerified;

    @SerializedName("user")
    private User achivementUser;

    @SerializedName("body")
    private String achievementBodyId;

    @SerializedName("event")
    private String achievementEventId;

    public Achievement(@NonNull String achievementID, String achievementTitle, String achievementDescription, boolean achievementDismissed, boolean achievementVerified, String achievementBodyId, String achievementEventId) {
        this.achievementID = achievementID;
        this.achievementTitle = achievementTitle;
        this.achievementDescription = achievementDescription;
        this.achievementDismissed = achievementDismissed;
        this.achievementVerified = achievementVerified;
        this.achievementBodyId = achievementBodyId;
        this.achievementEventId = achievementEventId;
    }

    @NonNull
    public String getAchievementID() {
        return achievementID;
    }

    public void setAchievementID(@NonNull String achievementID) {
        this.achievementID = achievementID;
    }

    public String getAchievementTitle() {
        return achievementTitle;
    }

    public void setAchievementTitle(String achievementTitle) {
        this.achievementTitle = achievementTitle;
    }

    public String getAchievementDescription() {
        return achievementDescription;
    }

    public void setAchievementDescription(String achievementDescription) {
        this.achievementDescription = achievementDescription;
    }

    public Body getAchievementBody() {
        return achievementBody;
    }

    public void setAchievementBody(Body achievementBody) {
        this.achievementBody = achievementBody;
    }

    public Event getAchievementEvent() {
        return achievementEvent;
    }

    public void setAchievementEvent(Event achievementEvent) {
        this.achievementEvent = achievementEvent;
    }

    public boolean isAchievementDismissed() {
        return achievementDismissed;
    }

    public void setAchievementDismissed(boolean achievementDismissed) {
        this.achievementDismissed = achievementDismissed;
    }

    public boolean isAchievementVerified() {
        return achievementVerified;
    }

    public void setAchievementVerified(boolean achievementVerified) {
        this.achievementVerified = achievementVerified;
    }

    public User getAchivementUser() {
        return achivementUser;
    }

    public void setAchivementUser(User achivementUser) {
        this.achivementUser = achivementUser;
    }

    public String getAchievementBodyId() {
        return achievementBodyId;
    }

    public void setAchievementBodyId(String achievementBodyId) {
        this.achievementBodyId = achievementBodyId;
    }

    public String getAchievementEventId() {
        return achievementEventId;
    }

    public void setAchievementEventId(String achievementEventId) {
        this.achievementEventId = achievementEventId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Achievement achievement = (Achievement) o;
        return Objects.equals(achievementID, achievement.achievementID);
    }

    public long getId() {
        return getAchievementID().hashCode();
    }

    public String getTitle() {
        return getAchievementTitle();
    }

    public String getSubtitle() {
        if (getAchievementEvent() != null) {
            return getAchievementEvent().getEventName();
        }
        if (getAchievementBody() != null) {
            return getAchievementBody().getBodyName();
        }
        return getAchievementDescription();
    }

    public String getAvatarUrl() {
        String url = null;
        if (getAchievementEvent() != null) {
            url = getAchievementEvent().getEventImageURL();
        }
        if (url == null || url.equals("") && getAchievementBody() != null) {
            url = getAchievementBody().getBodyImageURL();
        }
        return url;
    }
}
