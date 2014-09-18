package com.lycilph.lunchviewer;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.UUID;

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
    private onWeekMenuUpdatedListener listener;

    public WeekMenu() { }

    public final void setId(String i) { id = i; }

    public String getId() { return id; }

    public final void setYear(int y) { year = y; }

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

    public final void setItems(List<WeekMenuItem> i) { items = i; }

    public List<WeekMenuItem> getItems() { return items; }

    public void setUpdateListener(onWeekMenuUpdatedListener l) { listener = l; }

    public void Update(WeekMenu wm) {
        // Update stuff here
        if (listener != null) {
            listener.onUpdated();
        }
    }

    @Override
    public String toString() {
        return String.format("%d-%d [%s]", year, week, menuId);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof WeekMenu && ((WeekMenu) o).id == id;
    }

    public interface onWeekMenuUpdatedListener {
        public void onUpdated();
    }
}