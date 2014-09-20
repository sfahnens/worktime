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
    private DB dbHelper;

    public static final DateTimeFormatter DB_MONTH_DATE_FORMAT = DateTimeFormatter.ofPattern("YYYYMM");

    public DataSource(Context context) {
        dbHelper = new DB(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public List<String> getMonths() {

        List<String> months = new ArrayList<String>();

        String table = DB.TABLE_WORK_RECORDS;
        String[] columns = new String[]{DB.COL_MONTH};
        String groupBy = DB.COL_MONTH;
        String orderBy = DB.COL_MONTH + " ASC";
        Cursor cursor = database.query(table, columns, null, null, groupBy, null, orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            months.add(cursor.getString(0));
            cursor.moveToNext();
        }

        cursor.close();
        return months;
    }

    public void persistWorkRecord(WorkRecord workRecord) {

        String table = DB.TABLE_WORK_RECORDS;
        ContentValues values = workRecordToContentValues(workRecord);
        database.insert(table, null, values);
    }

    public List<WorkRecord> getWorkRecords(String month) {
        List<WorkRecord> workRecords = new ArrayList<WorkRecord>();

        String table = DB.TABLE_WORK_RECORDS;
        String[] columns = DB.WORK_RECORD_COLUMNS;
        String where = DB.COL_MONTH + " = " + month;
        String orderBy = DB.COL_DATE + " ASC, " + DB.COL_START_TIME + " ASC, " + DB.COL_END_TIME + " ASC";
        Cursor cursor = database.query(table, columns, where, null, null, null, orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            workRecords.add(cursorToWorkRecord(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        return workRecords;
    }

    public void updateWorkRecord(WorkRecord workRecord) {
        String table = DB.TABLE_WORK_RECORDS;
        ContentValues values = workRecordToContentValues(workRecord);
        String whereClause = DB.COL_ID + " = " + workRecord.getId();
        database.update(table, values, whereClause, null);
    }

    public void deleteWorkRecord(WorkRecord workRecord) {
        String table = DB.TABLE_WORK_RECORDS;
        String whereClause = DB.COL_ID + " = " + workRecord.getId();
        database.delete(table, whereClause, null);
    }


    private WorkRecord cursorToWorkRecord(Cursor cursor) {
        WorkRecord workRecord = new WorkRecord();
        workRecord.setId(cursor.getLong(0));
        workRecord.setDate(LocalDate.parse(cursor.getString(1)));
        workRecord.setStartTime(LocalTime.parse(cursor.getString(2)));
        workRecord.setEndTime(LocalTime.parse(cursor.getString(3)));

        return workRecord;
    }

    private ContentValues workRecordToContentValues(WorkRecord workRecord) {
        ContentValues values = new ContentValues();

        values.put(DB.COL_DATE, workRecord.getDate().toString());
        values.put(DB.COL_MONTH, DB_MONTH_DATE_FORMAT.format(workRecord.getDate()));

        values.put(DB.COL_START_TIME,
                workRecord.getStartTime().truncatedTo(ChronoUnit.MINUTES).toString());

        values.put(DB.COL_END_TIME,
                workRecord.getEndTime().truncatedTo(ChronoUnit.MINUTES).toString());

        return values;
    }

    public long persistLeaveRecord(LeaveRecord leaveRecord) {
        String table = DB.TABLE_LEAVE_RECORDS;
        ContentValues values = leaveRecordToContentValues(leaveRecord);
        return database.insert(table, null, values);
    }

    public List<LeaveRecord> getLeaveRecords(String month) {
        List<LeaveRecord> workRecords = new ArrayList<LeaveRecord>();

        String table = DB.TABLE_LEAVE_RECORDS;
        String[] columns = DB.LEAVE_RECORD_COLUMNS;
        String where = null; //DB.COL_MONTH + " = " + month;
        String orderBy = DB.COL_DATE + " ASC";
        Cursor cursor = database.query(table, columns, where, null, null, null, orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            workRecords.add(cursorToLeaveRecord(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        return workRecords;
    }


    private LeaveRecord cursorToLeaveRecord(Cursor cursor) {
        LeaveRecord leaveRecord = new LeaveRecord();
        leaveRecord.setId(cursor.getLong(0));

        if (!cursor.isNull(1)) {
            leaveRecord.setBaseId(cursor.getLong(1));
        }

        leaveRecord.setDate(LocalDate.parse(cursor.getString(2)));

        leaveRecord.setReason(LeaveReason.valueOf(cursor.getString(3)));
        leaveRecord.setWorkdays(cursor.getInt(4) != 0); //poor man's boolean

        return leaveRecord;
    }

    private ContentValues leaveRecordToContentValues(LeaveRecord leaveRecord) {
        ContentValues values = new ContentValues();

        if (leaveRecord.getBaseId() != null) {
            values.put(DB.COL_BASE_ID, leaveRecord.getBaseId());
        }

        values.put(DB.COL_DATE, leaveRecord.getDate().toString());
        values.put(DB.COL_MONTH, DB_MONTH_DATE_FORMAT.format(leaveRecord.getDate()));

        values.put(DB.COL_REASON, leaveRecord.getReason().toString());
        values.put(DB.COL_WORKDAYS, leaveRecord.getWorkdays() ? 1 : 0);
        return values;
    }
}
