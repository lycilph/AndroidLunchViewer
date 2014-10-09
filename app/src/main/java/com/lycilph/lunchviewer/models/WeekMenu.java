package com.lycilph.lunchviewer.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class WeekMenu {
    @SerializedName("id")
    private String id;

    @SerializedName("year")
    private int year;

    @SerializedName("week")
    private int week;

    @SerializedName("menuId")
    private String menuId;

    private List<WeekMenuItem> items;

    public WeekMenu() {
        this(0, 0);
    }

    public WeekMenu(int y, int w) {
        setYear(y);
        setWeek(w);
        items = new ArrayList<WeekMenuItem>();
    }

    public String getId() { return id; }
    public final void setId(String i) { id = i; }

    public int getYear() { return year; }
    public final void setYear(int y) { year = y; }

    public int getWeek() { return week; }
    public final void setWeek(int w) { week = w; }

    public String getMenuId() { return menuId; }
    public final void setMenuId(String id) { menuId = id; }

    public List<WeekMenuItem> getItems() { return items; }
    public final void setItems(List<WeekMenuItem> i) { items = i; }

    public WeekMenuItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public String toString() {
        return String.format("%d-%d [%s]", year, week, menuId);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof WeekMenu && ((WeekMenu) o).id.equals(id);
    }
}