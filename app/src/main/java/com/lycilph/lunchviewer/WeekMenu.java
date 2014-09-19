package com.lycilph.lunchviewer;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.modelmapper.ModelMapper;

import java.util.ArrayList;
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
    private transient onWeekMenuUpdatedListener listener;

    public WeekMenu() {
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

    public void setUpdateListener(onWeekMenuUpdatedListener l) { listener = l; }

    public void update(WeekMenu wm) {
        ModelMapper mm = new ModelMapper();
        mm.map(wm, this);

        if (listener != null) {
            listener.onUpdated();
        }
    }

    public void clear() {
        update(new WeekMenu());
    }

    @Override
    public String toString() {
        return String.format("%d-%d [%s]", year, week, menuId);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof WeekMenu && ((WeekMenu) o).id.equals(id);
    }

    public interface onWeekMenuUpdatedListener {
        public void onUpdated();
    }
}