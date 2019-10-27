package app.insti.api.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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

    public List<MessMenu> getSortedMessMenus() {
        List<MessMenu> messMenus = getMessMenus();

        /* Sort by day starting today
         * This could have been done in a much simpler way with Java 8 :(
         * Don't try to fix this */
        final List<MessMenu> sortedMenus = new ArrayList<>();
        final Calendar calendar = Calendar.getInstance(Locale.UK);
        int today = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if (today == -1) {
            today = 6;
        }

        /* Sort by day */
        for (int i = 0; i < 7; i++) {
            final int day = (today + i) % 7 + 1;
            for (MessMenu menu : messMenus) {
                if (menu.getDay() == day) {
                    sortedMenus.add(menu);
                }
            }
        }
        return sortedMenus;
    }
}
