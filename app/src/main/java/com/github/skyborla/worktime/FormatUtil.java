package com.github.skyborla.worktime;

import com.github.skyborla.worktime.model.WorkRecord;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

/**
 * Created by Sebastian on 13.09.2014.
 */
public final class FormatUtil {

    public final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd. MM. YYYY");
    public final static DateTimeFormatter DATE_FORMAT_MEDIUM = DateTimeFormatter.ofPattern("eee dd. MM.");
    public final static DateTimeFormatter DATE_FORMAT_SHORT = DateTimeFormatter.ofPattern("dd. MM.");
    public final static DateTimeFormatter DATE_FORMAT_MONTH = DateTimeFormatter.ofPattern("MMMYY");

    public final static DateTimeFormatter DATE_FORMAT_DB_MONTH = DateTimeFormatter.ofPattern("YYYYMM");

    public final static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public static String formatTimes(WorkRecord workRecord) {

        String startTime = FormatUtil.TIME_FORMAT.format(workRecord.getStartTime());
        String endTime = FormatUtil.TIME_FORMAT.format(workRecord.getEndTime());

        return startTime + " - " + endTime;
    }

    public static LocalDate parseDate(String dateString) {
        if (dateString == null) {
            return null;
        }

        try {
            return LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static LocalTime parseTime(String timeString) {
        if (timeString == null) {
            return null;
        }
        try {
            return LocalTime.parse(timeString);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static LocalDate parseDBMonthFormat(String dbFormatted) {
        return LocalDate.of(Integer.valueOf(dbFormatted.substring(0, 4)),
                Integer.valueOf(dbFormatted.substring(4)), 1);
    }

}
