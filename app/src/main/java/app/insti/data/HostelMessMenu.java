package app.insti.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "hostelMessMenus")
public class HostelMessMenu {
    @NonNull()
    @PrimaryKey()
    @ColumnInfo(name = "id")
    @SerializedName("id")
    private String menuID;

    @ColumnInfo(name = "name")
    @SerializedName("name")
    private String name;

    @ColumnInfo(name = "short_name")
    @SerializedName("short_name")
    private String shortName;

    @ColumnInfo(name = "long_name")
    @SerializedName("long_name")
    private String longName;

    @ColumnInfo(name = "mess")
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
