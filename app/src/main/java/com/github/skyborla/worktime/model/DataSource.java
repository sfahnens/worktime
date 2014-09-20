package com.github.skyborla.worktime.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.github.skyborla.worktime.FormatUtil;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;

import java.sql.SQLException;
import java.text.Format;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Sebastian on 12.09.2014.
 */
public class DataSource {

    private SQLiteDatabase database;
    private DB dbHelper;

    public DataSource(Context context) {
        dbHelper = new DB(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public List<LocalDate> getMonths() {

        Set<LocalDate> months = new TreeSet<LocalDate>();

        String workTable = DB.TABLE_WORK_RECORDS;
        String[] columns = new String[]{DB.COL_MONTH};
        String groupBy = DB.COL_MONTH;
        String orderBy = DB.COL_MONTH + " ASC";
        Cursor workCursor = database.query(workTable, columns, null, null, groupBy, null, orderBy);

        workCursor.moveToFirst();
        while (!workCursor.isAfterLast()) {
            months.add(FormatUtil.parseDBMonthFormat(workCursor.getString(0)));
            workCursor.moveToNext();
        }
        workCursor.close();

        String leaveTable = DB.TABLE_LEAVE_RECORDS;
        Cursor leaveCursor = database.query(leaveTable, columns, null, null, groupBy, null, orderBy);

        leaveCursor.moveToFirst();
        while (!leaveCursor.isAfterLast()) {
            months.add(FormatUtil.parseDBMonthFormat(leaveCursor.getString(0)));
            leaveCursor.moveToNext();
        }
        leaveCursor.close();

        return new ArrayList<LocalDate>(months);
    }

    public LocalDate persistWorkRecord(WorkRecord workRecord) {

        String table = DB.TABLE_WORK_RECORDS;
        ContentValues values = workRecordToContentValues(workRecord);
        database.insert(table, null, values);

        return workRecord.getDate().withDayOfMonth(1);
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

    public Set<LocalDate> updateWorkRecord(WorkRecord workRecord) {
        Set<LocalDate> affectedMonths = new HashSet<LocalDate>();

        // add old month to affected month
        String table = DB.TABLE_WORK_RECORDS;
        String[] columns = DB.WORK_RECORD_COLUMNS;
        String where = DB.COL_ID + " = " + workRecord.getId();
        Cursor cursor = database.query(table, columns, where, null, null, null, null);

        cursor.moveToFirst();
        affectedMonths.add(FormatUtil.parseDBMonthFormat(cursor.getString(0)));
        cursor.close();

        // update record
        ContentValues values = workRecordToContentValues(workRecord);
        String whereClause = DB.COL_ID + " = " + workRecord.getId();
        database.update(table, values, whereClause, null);

        // add old month to affected month
        affectedMonths.add(workRecord.getDate().withDayOfMonth(1));
        return affectedMonths;
    }

    public LocalDate deleteWorkRecord(WorkRecord workRecord) {
        String table = DB.TABLE_WORK_RECORDS;
        String whereClause = DB.COL_ID + " = " + workRecord.getId();
        database.delete(table, whereClause, null);

        return workRecord.getDate().withDayOfMonth(1);
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
        values.put(DB.COL_MONTH, FormatUtil.DATE_FORMAT_DB_MONTH.format(workRecord.getDate()));

        values.put(DB.COL_START_TIME,
                workRecord.getStartTime().truncatedTo(ChronoUnit.MINUTES).toString());

        values.put(DB.COL_END_TIME,
                workRecord.getEndTime().truncatedTo(ChronoUnit.MINUTES).toString());

        return values;
    }

    public Set<LocalDate> persistLeaveRecord(MetaLeaveRecord metaLeaveRecord) {
        String table = DB.TABLE_LEAVE_RECORDS;

        LeaveRecord leaveRecord = new LeaveRecord();
        leaveRecord.setReason(metaLeaveRecord.getReason());
        leaveRecord.setWorkdays(metaLeaveRecord.isWorkdays());

        Set<LocalDate> affectedMonths = new LinkedHashSet<LocalDate>();

        LocalDate date = metaLeaveRecord.getStartDate();
        while (!date.isAfter(metaLeaveRecord.getEndDate())) {

            if (metaLeaveRecord.isWorkdays() &&
                    (date.getDayOfWeek() == DayOfWeek.SATURDAY ||
                            date.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                date = date.plusDays(1);
                continue;
            }

            leaveRecord.setDate(date);
            affectedMonths.add(date.withDayOfMonth(1));

            ContentValues values = leaveRecordToContentValues(leaveRecord);

            long id = database.insert(table, null, values);
            if (leaveRecord.getBaseId() == null) {
                leaveRecord.setBaseId(id);
            }

            date = date.plusDays(1);
        }

        return affectedMonths;
    }

    public MetaLeaveRecord getMetaLeaveRecord(LeaveRecord leaveRecord) {
        MetaLeaveRecord metaLeaveRecord = new MetaLeaveRecord();
        metaLeaveRecord.setReason(leaveRecord.getReason());
        metaLeaveRecord.setWorkdays(leaveRecord.getWorkdays());

        long id = getMetaIdOfLeaveRecord(leaveRecord);
        metaLeaveRecord.setId(id);

        // reconstruct date boundaries
        String table = DB.TABLE_LEAVE_RECORDS;
        String[] columns = new String[]{DB.COL_DATE};
        String where = DB.COL_ID + " = " + id + " or " + DB.COL_BASE_ID + " = " + id;
        String orderBy = DB.COL_DATE + " ASC";
        Cursor cursor = database.query(table, columns, where, null, null, null, orderBy);

        cursor.moveToFirst();
        metaLeaveRecord.setStartDate(LocalDate.parse(cursor.getString(0)));

        cursor.moveToLast();
        metaLeaveRecord.setEndDate(LocalDate.parse(cursor.getString(0)));

        cursor.close();

        return metaLeaveRecord;
    }

    private long getMetaIdOfLeaveRecord(LeaveRecord leaveRecord) {
        long id;
        if (leaveRecord.getBaseId() != null) {
            id = leaveRecord.getBaseId();
        } else {
            id = leaveRecord.getId();
        }
        return id;
    }

    public Set<LocalDate> deleteLeaveRecord(LeaveRecord leaveRecord) {
        long id = getMetaIdOfLeaveRecord(leaveRecord);
        return deleteLeaveRecord(id);
    }

    private Set<LocalDate> deleteLeaveRecord(long id) {
        Set<LocalDate> affectedMonths = new LinkedHashSet<LocalDate>();

        // record affected months
        String table = DB.TABLE_LEAVE_RECORDS;
        String[] columns = new String[]{DB.COL_MONTH};
        String where = DB.COL_ID + " = " + id + " or " + DB.COL_BASE_ID + " = " + id;
        String groupBy = DB.COL_MONTH;
        String orderBy = DB.COL_MONTH + " ASC";
        Cursor cursor = database.query(table, columns, where, null, groupBy, null, orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            affectedMonths.add(FormatUtil.parseDBMonthFormat(cursor.getString(0)));
            cursor.moveToNext();
        }
        cursor.close();

        // delete entries
        database.delete(table, where, null);

        return affectedMonths;
    }

    public Set<LocalDate> updateLeaveRecord(MetaLeaveRecord record) {

        Set<LocalDate> affectedMonths = deleteLeaveRecord(record.getId());
        affectedMonths.addAll(persistLeaveRecord(record));

        return affectedMonths;
    }

    public List<LeaveRecord> getLeaveRecords(String month) {
        List<LeaveRecord> workRecords = new ArrayList<LeaveRecord>();

        String table = DB.TABLE_LEAVE_RECORDS;
        String[] columns = DB.LEAVE_RECORD_COLUMNS;
        String where = DB.COL_MONTH + " = " + month;
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
        values.put(DB.COL_MONTH, FormatUtil.DATE_FORMAT_DB_MONTH.format(leaveRecord.getDate()));

        values.put(DB.COL_REASON, leaveRecord.getReason().toString());
        values.put(DB.COL_WORKDAYS, leaveRecord.getWorkdays() ? 1 : 0);
        return values;
    }
}
