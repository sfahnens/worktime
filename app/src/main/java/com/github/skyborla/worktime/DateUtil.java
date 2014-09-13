package com.github.skyborla.worktime;

import org.threeten.bp.format.DateTimeFormatter;

/**
 * Created by Sebastian on 13.09.2014.
 */
public final class DateUtil {

    public final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd. MM. YYYY");
    public final static DateTimeFormatter DATE_FORMAT_SHORT = DateTimeFormatter.ofPattern("eee dd. MM.");
    public final static DateTimeFormatter DATE_FORMAT_MONTH = DateTimeFormatter.ofPattern("MMMYY");

    public final static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

}
