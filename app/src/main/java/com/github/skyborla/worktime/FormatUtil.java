package com.github.skyborla.worktime;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.TextView;

import com.github.skyborla.worktime.model.WorkRecord;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

import java.text.DateFormatSymbols;

/**
 * Created by Sebastian on 13.09.2014.
 */
public final class FormatUtil {

    public final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd. MM. YYYY");
    public final static DateTimeFormatter DATE_FORMAT_DAY = DateTimeFormatter.ofPattern("eee");
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

//    public final static int MAXIMUM_SHORT_DAY_NAME_WIDTH_AT_18_SP;
//
//    static {
//        float max = 0;
//
//        Paint p = new Paint();
//        p.setTextSize(18);
//        p.setTypeface(Typeface.SANS_SERIF);
//
//        for (String s : DateFormatSymbols.getInstance().getShortWeekdays()) {
//            System.out.println(s + " " + p.measureText(s));
//            max = Math.max(max, p.measureText(s));
//        }
//
//        Canvas c = new Canvas();
//        c.drawText("Mo.", 0, 0, p);
//
//        System.out.println("--");
//        System.out.println(c.getWidth());
//        System.out.println("--");
//
//        System.out.println("M" + p.measureText("M"));
//        System.out.println("o" + p.measureText("o"));
//
//        TextPaint tp = new TextPaint();
//        tp.setTextSize(18);
//
//        StaticLayout l = new StaticLayout("Mo.", tp, 1024, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
//
//        Rect r = new Rect();
//        l.getLineBounds(0, r);
//        System.out.println(r.width());
//        System.out.println("XX " + l.getLineWidth(0));
//
//        Rect bounds = new Rect();
//        p.getTextBounds("Mo.", 0, "Mo.".length(), bounds);
//        int width = bounds.left + bounds.width();
//        System.out.println("WIDTH " + width);
//
//
//        MAXIMUM_SHORT_DAY_NAME_WIDTH_AT_18_SP = (int) max * 2;
//        System.out.println(max);
//    }
}
