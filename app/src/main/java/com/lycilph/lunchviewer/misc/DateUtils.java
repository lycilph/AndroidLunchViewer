package com.lycilph.lunchviewer.misc;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class DateUtils {
    public static LocalDate getNextValidDate() {
        LocalTime lunchEnd = DateTime.now().withTime(13, 15, 0, 0).toLocalTime();
        LocalTime now = DateTime.now().toLocalTime();

        if (now.isBefore(lunchEnd))
            return DateTime.now().toLocalDate();

        LocalDate nextDay = DateTime.now().plusDays(1).toLocalDate();
        int dayOfWeek = nextDay.dayOfWeek().get();

        if (dayOfWeek < 6)
            return nextDay;
        else {
            LocalDate mondayOfNextWeek = DateTime.now().plusWeeks(1).withDayOfWeek(DateTimeConstants.MONDAY).toLocalDate();
            return mondayOfNextWeek;
        }
    }

    public static int getYear() {
        return DateTime.now().year().get();
    }

    public static int getWeekNumber(int weekOffset) {
        return DateTime.now().plusWeeks(weekOffset).weekOfWeekyear().get();
    }
}
