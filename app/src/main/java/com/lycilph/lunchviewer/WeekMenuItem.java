package com.lycilph.lunchviewer;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

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
        DateTime dt = getDate();
        return String.format("%s (%s) %s of week %d", text, link, dt.dayOfWeek().getAsText(), dt.weekOfWeekyear().get());
    }

    public String getText() {
        return text;
    }

    public DateTime getDate() {
        return new DateTime(date).withZone(DateTimeZone.forID("UTC"));
    }

    public String getLink() {
        return link;
    }
}
