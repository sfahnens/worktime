package com.github.skyborla.worktime.export;

import android.content.Context;

import com.github.skyborla.worktime.FormatUtil;
import com.github.skyborla.worktime.model.LeaveRecord;
import com.github.skyborla.worktime.model.MergingListProcessor;
import com.github.skyborla.worktime.model.WorkRecord;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

import jxl.biff.EmptyCell;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class MonthExporter {

    private final static int COL_MONTH = 0;
    private final static int COL_WEEK = 1;
    private final static int COL_DATE = 2;
    private final static int COL_DAY = 3;
    private final static int COL_START_TIME = 4;
    private final static int COL_END_TIME = 5;
    private final static int COL_DURATION = 6;
    private final static int COL_LEAVE = 7;

    private Context context;
    private WritableSheet sheet;

    private List<Integer> monthStartRows = new ArrayList<Integer>();
    private List<Integer> weekStartRows = new ArrayList<Integer>();

    private int rowCursor = 0;

    private WritableCellFormat overLineFormatGrey80;
    private WritableCellFormat overLineFormatGrey50;

    public MonthExporter(Context context, WritableWorkbook workbook, String name) throws WriteException {
        this.context = context;
        sheet = workbook.createSheet(name, 0);

        overLineFormatGrey80 = new WritableCellFormat();
        overLineFormatGrey80.setBorder(Border.TOP, BorderLineStyle.HAIR, Colour.GREY_80_PERCENT);

        overLineFormatGrey50 = new WritableCellFormat();
        overLineFormatGrey50.setBorder(Border.TOP, BorderLineStyle.HAIR, Colour.GREY_50_PERCENT);

        writeHeaders();
    }

    private void writeHeaders() throws WriteException {

        appendLabel(COL_MONTH, "Monat");
        appendLabel(COL_WEEK, "Woche");
        appendLabel(COL_DATE, "Datum");
        appendLabel(COL_DAY, "Wochentag");
        appendLabel(COL_START_TIME, "Startzeit");
        appendLabel(COL_END_TIME, "Endzeit");
        appendLabel(COL_DURATION, "Dauer");
        appendLabel(COL_LEAVE, "Urlaubsgrund");

        rowCursor++;
    }

    public void writeMonth(LocalDate month, List<WorkRecord> workRecords, List<LeaveRecord> leaveRecords,
                           List<LocalDate> holidays) throws WriteException {

        appendLabel(COL_MONTH, FormatUtil.DATE_FORMAT_MONTH_FULL.format(month));
        monthStartRows.add(rowCursor);

        new MergingListProcessor(workRecords, leaveRecords, holidays) {
            @Override
            protected void process(WorkRecord workRecord) {
                try {
                    appendLabel(COL_DATE, FormatUtil.DATE_FORMAT_SHORT.format(workRecord.getDate()));
                    appendLabel(COL_DAY, FormatUtil.DATE_FORMAT_DAY.format(workRecord.getDate()));

                    appendLabel(COL_START_TIME, FormatUtil.TIME_FORMAT.format(workRecord.getStartTime()));
                    appendLabel(COL_END_TIME, FormatUtil.TIME_FORMAT.format(workRecord.getEndTime()));

                    appendLabel(COL_DURATION, "00:00");
                    rowCursor++;
                } catch (WriteException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            protected void process(LeaveRecord leaveRecord) {
                try {
                    appendLabel(COL_DATE, FormatUtil.DATE_FORMAT_SHORT.format(leaveRecord.getDate()));
                    appendLabel(COL_DAY, FormatUtil.DATE_FORMAT_DAY.format(leaveRecord.getDate()));

                    appendLabel(COL_LEAVE, context.getString(leaveRecord.getReason().stringResource));

                    rowCursor++;
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void newWeek(int week) {
                try {
                    appendLabel(COL_WEEK, "KW " + week);
                    weekStartRows.add(rowCursor);
                } catch (WriteException e) {
                    throw new RuntimeException(e);
                }
            }
        }.process();
    }

    private void appendLabel(int column, String string) throws WriteException {

        sheet.addCell(new Label(column, rowCursor, string));
    }

    public void finalizeSheet() throws WriteException {

        // Freeze first row
        sheet.getSettings().setVerticalFreeze(1);

        // adjust column widths
        sheet.setColumnView(0, 16);
        sheet.setColumnView(3, 12);
        sheet.setColumnView(7, 16);

        // adjust row heights
        for (int i = 0; i < rowCursor; i++) {
            sheet.setRowView(i, 15 * 20);
        }

        // markers for weekstart
        for (Integer weekStartRow : weekStartRows) {
            for (int i = 1; i < 8; i++) {
                formatCell(i, weekStartRow, overLineFormatGrey50);
            }
        }

        // markers for month start
        for (Integer monthStartRow : monthStartRows) {
            for (int i = 0; i < 16; i++) {
                formatCell(i, monthStartRow, overLineFormatGrey80);
            }
        }
    }

    private void formatCell(int col, int row, WritableCellFormat format) throws WriteException {
        WritableCell wc = sheet.getWritableCell(col, row);

        if (wc instanceof EmptyCell) {
            wc = new Label(col, row, "");
            sheet.addCell(wc);
        }

        wc.setCellFormat(format);
    }
}