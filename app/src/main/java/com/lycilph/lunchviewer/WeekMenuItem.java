package com.lycilph.lunchviewer;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;

public class WeekMenuItem {
    @SerializedName("id")
    private String id;

    @SerializedName("text")
    private String text;

    @SerializedName("link")
    private String link;

    @SerializedName("date")
    private String date;

    public WeekMenuItem() { }

    @Override
    public String toString() {
        LocalDate ld = getDate();
        return String.format("%s (%s) %s of week %d", text, link, ld.dayOfWeek().getAsText(), ld.weekOfWeekyear().get());
    }

    public String getText() {
        return text;
    }

    public LocalDate getDate() {
        return new DateTime(date).toLocalDate();
    }

    public String getLink() {
        return link;
    }
}
