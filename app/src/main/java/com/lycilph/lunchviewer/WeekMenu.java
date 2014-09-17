package com.lycilph.lunchviewer;

import com.google.gson.annotations.SerializedName;

public class WeekMenu {

    @SerializedName("id")
    private String id;

    @SerializedName("year")
    private int year;

    @SerializedName("week")
    private int week;

    @SerializedName("menuId")
    private String menuId;

    public WeekMenu() {
    }

    public WeekMenu(int y, int w) {
        this.setYear(y);
        this.setWeek(w);
    }

    @Override
    public String toString() {
        return getId();
    }

    public final void setId(String i) { id = i; }

    public String getId() { return id; }

    public final void setYear(int y) {
        year = y;
    }

    public int getYear() {
        return year;
    }

    public final void setWeek(int w) {
        week = w;
    }

    public int getWeek() {
        return week;
    }

    public final void setMenuId(String i) { menuId = i; }

    public String getMenuId() { return menuId; }

    @Override
    public boolean equals(Object o) {
        return o instanceof WeekMenu && ((WeekMenu) o).id == id;
    }
}