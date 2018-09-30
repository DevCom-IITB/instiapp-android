package app.insti.api.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class MessMenu {
    @NonNull()
    @SerializedName("id")
    private String mealID;

    @SerializedName("day")
    private int day;

    @SerializedName("breakfast")
    private String breakfast;

    @SerializedName("lunch")
    private String lunch;

    @SerializedName("snacks")
    private String snacks;

    @SerializedName("dinner")
    private String dinner;

    @SerializedName("hostel")
    private String hostelID;

    public MessMenu(String mealID, int day, String breakfast, String lunch, String snacks, String dinner, String hostelID) {
        this.mealID = mealID;
        this.day = day;
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.snacks = snacks;
        this.dinner = dinner;
        this.hostelID = hostelID;
    }

    public String getMealID() {
        return mealID;
    }

    public void setMealID(String mealID) {
        this.mealID = mealID;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(String breakfast) {
        this.breakfast = breakfast;
    }

    public String getLunch() {
        return lunch;
    }

    public void setLunch(String lunch) {
        this.lunch = lunch;
    }

    public String getSnacks() {
        return snacks;
    }

    public void setSnacks(String snacks) {
        this.snacks = snacks;
    }

    public String getDinner() {
        return dinner;
    }

    public void setDinner(String dinner) {
        this.dinner = dinner;
    }

    public String getHostelID() {
        return hostelID;
    }

    public void setHostelID(String hostelID) {
        this.hostelID = hostelID;
    }
}
