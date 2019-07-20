package app.insti.api.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import app.insti.interfaces.CardInterface;

public class OfferedAchievement implements CardInterface {
    @NonNull()
    @SerializedName("id")
    private String achievementID;

    @SerializedName("title")
    private String achievementTitle;

    @SerializedName("description")
    private String achievementDescription;

    @SerializedName("body")
    private String achievementBodyId;

    @SerializedName("event")
    private String achievementEventId;

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
        OfferedAchievement achievement = (OfferedAchievement) o;
        return Objects.equals(achievementID, achievement.achievementID);
    }

    public long getId() {
        return getAchievementID().hashCode();
    }

    public String getTitle() {
        return getAchievementTitle();
    }

    public String getSubtitle() {
        return getAchievementDescription();
    }

    public String getAvatarUrl() {
        return null;
    }
}
