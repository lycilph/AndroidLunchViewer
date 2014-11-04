package com.lycilph.lunchviewer.misc;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import static org.joda.time.DateTimeConstants.MONDAY;

public class DateUtils {
    public static LocalDate getNextValidDate() {
        LocalTime lunchEnd = DateTime.now().withTime(13, 15, 0, 0).toLocalTime();
        LocalTime now = DateTime.now().toLocalTime();
        int dayOfWeek = DateTime.now().toLocalDate().dayOfWeek().get();
        boolean isAfterLunch = now.isAfter(lunchEnd);

        if (dayOfWeek > 5 || (dayOfWeek == 5 && isAfterLunch))
            return DateTime.now().plusWeeks(1).withDayOfWeek(MONDAY).toLocalDate();

        if (isAfterLunch)
            return DateTime.now().plusDays(1).toLocalDate();
        else
            return DateTime.now().toLocalDate();
    }

    public static int getYear() {
        return DateTime.now().year().get();
    }

    public static int getWeekNumber(int weekOffset) {
        return DateTime.now().plusWeeks(weekOffset).weekOfWeekyear().get();
    }
}
