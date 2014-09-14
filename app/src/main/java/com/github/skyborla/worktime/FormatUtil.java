package com.github.skyborla.worktime;

import com.github.skyborla.worktime.model.Record;

import org.threeten.bp.format.DateTimeFormatter;

/**
 * Created by Sebastian on 13.09.2014.
 */
public final class FormatUtil {

    public final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd. MM. YYYY");
    public final static DateTimeFormatter DATE_FORMAT_MEDIUM = DateTimeFormatter.ofPattern("eee dd. MM.");
    public final static DateTimeFormatter DATE_FORMAT_SHORT = DateTimeFormatter.ofPattern("dd. MM.");
    public final static DateTimeFormatter DATE_FORMAT_MONTH = DateTimeFormatter.ofPattern("MMMYY");

    public final static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public static String formatTimes(Record record) {

        String startTime = FormatUtil.TIME_FORMAT.format(record.getStartTime());
        String endTime = FormatUtil.TIME_FORMAT.format(record.getEndTime());

        return startTime + " - " + endTime;
    }

}
