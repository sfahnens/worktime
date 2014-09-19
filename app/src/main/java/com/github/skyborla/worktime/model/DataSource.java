package com.github.skyborla.worktime.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sebastian on 12.09.2014.
 */
public class DataSource {

    private SQLiteDatabase database;
    private WorktimeSQLiteHelper dbHelper;

    public static final DateTimeFormatter DB_MONTH_DATE_FORMAT = DateTimeFormatter.ofPattern("YYYYMM");

    public DataSource(Context context) {
        dbHelper = new WorktimeSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public WorkRecord persist(WorkRecord workRecord) {

        ContentValues values = recordToContentValues(workRecord);

        long id = database.insert(WorktimeSQLiteHelper.TABLE_WORKTIME_RECORDS, null, values);

        Cursor cursor = database.query(WorktimeSQLiteHelper.TABLE_WORKTIME_RECORDS,
                WorktimeSQLiteHelper.RECORD_COLUMNS,
                WorktimeSQLiteHelper.COL_ID + " = " + id,
                null, null, null, null);

        cursor.moveToFirst();
        WorkRecord newWorkRecord = cursorToRecord(cursor);
        cursor.close();

        return newWorkRecord;
    }

    private ContentValues recordToContentValues(WorkRecord workRecord) {
        ContentValues values = new ContentValues();

        if (workRecord.getDate() != null) {
            values.put(WorktimeSQLiteHelper.COL_DATE, workRecord.getDate().toString());
            values.put(WorktimeSQLiteHelper.COL_MONTH, DB_MONTH_DATE_FORMAT.format(workRecord.getDate()));
        }

        if (workRecord.getStartTime() != null) {
            values.put(WorktimeSQLiteHelper.COL_START_TIME,
                    workRecord.getStartTime().truncatedTo(ChronoUnit.MINUTES).toString());
        }

        if (workRecord.getEndTime() != null) {
            values.put(WorktimeSQLiteHelper.COL_END_TIME,
                    workRecord.getEndTime().truncatedTo(ChronoUnit.MINUTES).toString());
        }
        return values;
    }

    public List<String> getMonths() {

        List<String> months = new ArrayList<String>();
        Cursor cursor = database.query(WorktimeSQLiteHelper.TABLE_WORKTIME_RECORDS,
                new String[]{WorktimeSQLiteHelper.COL_MONTH},
                null, null, WorktimeSQLiteHelper.COL_MONTH, null,
                WorktimeSQLiteHelper.COL_MONTH + " ASC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            months.add(cursor.getString(0));
            cursor.moveToNext();
        }

        cursor.close();
        return months;
    }

    public List<WorkRecord> getRecords(String month) {
        List<WorkRecord> workRecords = new ArrayList<WorkRecord>();

        Cursor cursor = database.query(WorktimeSQLiteHelper.TABLE_WORKTIME_RECORDS,
                WorktimeSQLiteHelper.RECORD_COLUMNS,
                WorktimeSQLiteHelper.COL_MONTH + " = " + month, null, null, null,
                WorktimeSQLiteHelper.COL_DATE + " ASC, " +
                        WorktimeSQLiteHelper.COL_START_TIME + " ASC, " +
                        WorktimeSQLiteHelper.COL_END_TIME + " ASC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            workRecords.add(cursorToRecord(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        return workRecords;
    }

    private WorkRecord cursorToRecord(Cursor cursor) {

        WorkRecord workRecord = new WorkRecord();
        workRecord.setId(cursor.getLong(0));
        workRecord.setDate(LocalDate.parse(cursor.getString(1)));
        workRecord.setStartTime(LocalTime.parse(cursor.getString(2)));
        workRecord.setEndTime(LocalTime.parse(cursor.getString(3)));

        return workRecord;
    }

    public void delete(WorkRecord workRecord) {
        database.delete(WorktimeSQLiteHelper.TABLE_WORKTIME_RECORDS,
                WorktimeSQLiteHelper.COL_ID + " = " + workRecord.getId(), null);
    }

    public void update(WorkRecord workRecord) {

        database.update(WorktimeSQLiteHelper.TABLE_WORKTIME_RECORDS,
                recordToContentValues(workRecord),
                WorktimeSQLiteHelper.COL_ID + " = " + workRecord.getId(), null);

    }
}
