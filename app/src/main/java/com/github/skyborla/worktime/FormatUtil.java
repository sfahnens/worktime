package com.github.skyborla.worktime;

import android.content.Context;

import com.github.skyborla.worktime.model.WorkRecord;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

/**
 * Created by Sebastian on 13.09.2014.
 */
public final class FormatUtil {

    public final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd. MM. yyyy");
    public final static DateTimeFormatter DATE_FORMAT_DAY = DateTimeFormatter.ofPattern("eee");
    public final static DateTimeFormatter DATE_FORMAT_MEDIUM = DateTimeFormatter.ofPattern("eee dd. MM.");
    public final static DateTimeFormatter DATE_FORMAT_SHORT = DateTimeFormatter.ofPattern("dd. MM.");
    public final static DateTimeFormatter DATE_FORMAT_MONTH = DateTimeFormatter.ofPattern("MMMyy");

    public final static DateTimeFormatter DATE_FORMAT_DB_MONTH = DateTimeFormatter.ofPattern("yyyyMM");

    public final static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public final static DateTimeFormatter DATE_FORMAT_MONTH_FULL = DateTimeFormatter.ofPattern("MMMM");

    public final static DateTimeFormatter DATE_FORMAT_FILE = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");

    public final static DateTimeFormatter DATE_TIME_FORMATTER_FULL = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

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

    public static String formatNaturalLanguageDuration(Context context, int seconds) {
        int h = (int) seconds / 3600;
        int m = (int) (seconds / 60) % 60;

        String minutes = (m == 0) ? "" : context.getResources().getQuantityString(R.plurals.total_worktime_minutes, m, m) + " ";
        String hours = (h == 0) ? "" : context.getResources().getQuantityString(R.plurals.total_worktime_hours, h, h) + " ";

        return hours + minutes;
    }

    public static String formatDuration(WorkRecord workRecord) {
        return formatDuration(workRecord.getStartTime(), workRecord.getEndTime());
    }

    public static String formatDuration(LocalTime start, LocalTime end) {
        Duration duration = Duration.between(start, end);
        return formatDuration((int) duration.getSeconds());
    }

    public static String formatDuration(int seconds) {
        int h = (int) seconds / 3600;
        int m = (int) (seconds / 60) % 60;

        return String.format("%02d:%02d", h, m);
    }
}
