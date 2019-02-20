package app.insti.api.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HostelMessMenu {
    @NonNull()
    @SerializedName("id")
    private String menuID;

    @SerializedName("name")
    private String name;

    @SerializedName("short_name")
    private String shortName;

    @SerializedName("long_name")
    private String longName;

    @SerializedName("mess")
    private List<MessMenu> messMenus;

    public HostelMessMenu(String menuID, String name, String shortName, String longName, List<MessMenu> messMenus) {
        this.menuID = menuID;
        this.name = name;
        this.shortName = shortName;
        this.longName = longName;
        this.messMenus = messMenus;
    }

    public String getMenuID() {
        return menuID;
    }

    public void setMenuID(String menuID) {
        this.menuID = menuID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public List<MessMenu> getMessMenus() {
        return messMenus;
    }

    public void setMessMenus(List<MessMenu> messMenus) {
        this.messMenus = messMenus;
    }
}
