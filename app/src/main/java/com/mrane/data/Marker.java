package com.mrane.data;

import android.graphics.Color;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Arrays;

public class Marker {
    public static final int COLOR_BLUE = Color.rgb(75, 186, 238);
    public static final int COLOR_YELLOW = Color.rgb(255, 186, 0);
    public static final int COLOR_GREEN = Color.rgb(162, 208, 104);
    public static final int COLOR_GRAY = Color.rgb(156, 156, 156);
    public static final int DEPARTMENTS = 1;
    public static final int HOSTELS = 2;
    public static final int RESIDENCES = 3;
    public static final int HALLS_N_AUDITORIUMS = 4;
    public static final int FOOD_STALLS = 5;
    public static final int BANKS_N_ATMS = 6;
    public static final int SCHOOLS = 7;
    public static final int SPORTS = 8;
    public static final int OTHERS = 9;
    public static final int GATES = 10;
    public static final int PRINT = 11;
    public static final int LABS = 12;
    private static final String DEPARTMENTS_NAME = "Departments";
    private static final String HOSTELS_NAME = "Hostels";
    private static final String RESIDENCES_NAME = "Residences";
    private static final String HALLS_N_AUDITORIUMS_NAME = "Halls and Auditoriums";
    private static final String FOOD_STALLS_NAME = "Food Stalls";
    private static final String BANKS_N_ATMS_NAME = "Banks and Atms";
    private static final String SCHOOLS_NAME = "Schools";
    private static final String SPORTS_NAME = "Sports";
    private static final String OTHERS_NAME = "Others";
    private static final String GATES_NAME = "Gates";
    private static final String PRINT_NAME = "Printer facility";
    private static final String LABS_NAME = "Labs";
    private int id;
    private String name;
    private String shortName;
    private PointF point;
    private int groupIndex;
    private boolean showDefault;
    private String description;
    private String tag;
    private String imageUri;
    private int parentId;
    private String parentRel;
    private int[] childIds;
    private long lat;
    private long lng;

    public Marker(String name, String shortName, float x, float y,
                  int groupIndex, String description) {
        this.setPoint(new PointF(x, y));
        this.groupIndex = groupIndex;
        this.setName(name);
        this.setShortName(shortName);
        this.setShowDefault(false);
        this.setDescription(description);
        this.setImageUri("");
    }

    public Marker(int id, String name, String shortName, float pixelX, float pixelY,
                  int groupIndex, String description, int parentId, String parentRel,
                  int[] childIds, long lat, long lng) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.point = new PointF(pixelX, pixelY);
        this.groupIndex = groupIndex;
        this.description = description;
        this.setParentId(parentId);
        this.setParentRel(parentRel);
        this.setChildIds(childIds);
        this.setLat(lat);
        this.setLng(lng);
    }

    public static int getColor(int group) {
        if (group < -8) {
            return group;
        }

        Integer[] yellowGroup = new Integer[]{HOSTELS};
        Integer[] blueGroup = new Integer[]{DEPARTMENTS, LABS,
                HALLS_N_AUDITORIUMS};
        Integer[] greenGroup = new Integer[]{RESIDENCES};
        Integer[] purpleGroup = new Integer[]{FOOD_STALLS, BANKS_N_ATMS,
                SCHOOLS, SPORTS, OTHERS, GATES, PRINT};

        ArrayList<Integer> yellowList = new ArrayList<Integer>(
                Arrays.asList(yellowGroup));
        ArrayList<Integer> blueList = new ArrayList<Integer>(
                Arrays.asList(blueGroup));
        ArrayList<Integer> greenList = new ArrayList<Integer>(
                Arrays.asList(greenGroup));
        ArrayList<Integer> purpleList = new ArrayList<Integer>(
                Arrays.asList(purpleGroup));

        if (yellowList.contains(group)) {
            return COLOR_YELLOW;
        } else if (blueList.contains(group)) {
            return COLOR_BLUE;
        } else if (greenList.contains(group)) {
            return COLOR_GREEN;
        } else if (purpleList.contains(group)) {
            return COLOR_GRAY;
        }

        return 0;
    }

    public static String[] getGroupNames() {
        String[] groupNames = {DEPARTMENTS_NAME, LABS_NAME,
                HALLS_N_AUDITORIUMS_NAME, HOSTELS_NAME, RESIDENCES_NAME,
                FOOD_STALLS_NAME, BANKS_N_ATMS_NAME, SCHOOLS_NAME, SPORTS_NAME,
                PRINT_NAME, GATES_NAME, OTHERS_NAME};
        return groupNames;
    }

    public static int getGroupId(String groupName) {
        int result = 0;
        if (groupName.equals(DEPARTMENTS_NAME))
            result = DEPARTMENTS;
        if (groupName.equals(HOSTELS_NAME))
            result = HOSTELS;
        if (groupName.equals(RESIDENCES_NAME))
            result = RESIDENCES;
        if (groupName.equals(HALLS_N_AUDITORIUMS_NAME))
            result = HALLS_N_AUDITORIUMS;
        if (groupName.equals(FOOD_STALLS_NAME))
            result = FOOD_STALLS;
        if (groupName.equals(BANKS_N_ATMS_NAME))
            result = BANKS_N_ATMS;
        if (groupName.equals(SCHOOLS_NAME))
            result = SCHOOLS;
        if (groupName.equals(SPORTS_NAME))
            result = SPORTS;
        if (groupName.equals(OTHERS_NAME))
            result = OTHERS;
        if (groupName.equals(GATES_NAME))
            result = GATES;
        if (groupName.equals(PRINT_NAME))
            result = PRINT;
        if (groupName.equals(LABS_NAME))
            result = LABS;

        return result;
    }

    public int getColor() {
        int group = this.groupIndex;
        return getColor(group);
    }

    public String getGroupName() {
        switch (groupIndex) {
            case DEPARTMENTS:
                return DEPARTMENTS_NAME;
            case HOSTELS:
                return HOSTELS_NAME;
            case RESIDENCES:
                return RESIDENCES_NAME;
            case HALLS_N_AUDITORIUMS:
                return HALLS_N_AUDITORIUMS_NAME;
            case FOOD_STALLS:
                return FOOD_STALLS_NAME;
            case BANKS_N_ATMS:
                return BANKS_N_ATMS_NAME;
            case SCHOOLS:
                return SCHOOLS_NAME;
            case SPORTS:
                return SPORTS_NAME;
            case OTHERS:
                return OTHERS_NAME;
            case GATES:
                return GATES_NAME;
            case PRINT:
                return PRINT_NAME;
            case LABS:
                return LABS_NAME;
        }
        return "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public PointF getPoint() {
        return point;
    }

    public void setPoint(PointF point) {
        this.point = point;
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(int groupIndex) {
        this.groupIndex = groupIndex;
    }

    public boolean isShowDefault() {
        return showDefault;
    }

    public void setShowDefault(boolean showDefault) {
        this.showDefault = showDefault;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public int[] getChildIds() {
        return childIds;
    }

    public void setChildIds(int[] childIds) {
        this.childIds = childIds;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getParentRel() {
        return parentRel;
    }

    public void setParentRel(String parentRel) {
        this.parentRel = parentRel;
    }

    public long getLat() {
        return lat;
    }

    public void setLat(long lat) {
        this.lat = lat;
    }

    public long getLng() {
        return lng;
    }

    public void setLng(long lng) {
        this.lng = lng;
    }
}
