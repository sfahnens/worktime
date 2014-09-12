package com.github.skyborla.worktime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sebastian on 12.09.2014.
 */
public class RecordDataSource {

    private SQLiteDatabase database;
    private WorktimeSQLiteHelper dbHelper;

    public RecordDataSource(Context context) {
        dbHelper = new WorktimeSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Record persistRecord(Record record) {

        ContentValues values = new ContentValues();

        if (record.getDate() != null) {
            values.put(WorktimeSQLiteHelper.COL_DATE, record.getDate().toString());
        }

        if (record.getStartTime() != null) {
            values.put(WorktimeSQLiteHelper.COL_START_TIME, record.getStartTime().toString());
        }

        if (record.getEndTime() != null) {
            values.put(WorktimeSQLiteHelper.COL_END_TIME, record.getEndTime().toString());
        }

        long id = database.insert(WorktimeSQLiteHelper.TABLE_WORKTIME_RECORDS, null, values);

        Cursor cursor = database.query(WorktimeSQLiteHelper.TABLE_WORKTIME_RECORDS,
                WorktimeSQLiteHelper.COLUMNS,
                WorktimeSQLiteHelper.COL_ID + " = " + id,
                null, null, null, null);

        cursor.moveToFirst();
        Record newRecord = cursorToRecord(cursor);
        cursor.close();

        return newRecord;
    }

    public List<Record> getAllRecords() {
        List<Record> records = new ArrayList<Record>();

        Cursor cursor = database.query(WorktimeSQLiteHelper.TABLE_WORKTIME_RECORDS,
                WorktimeSQLiteHelper.COLUMNS,
                null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            records.add(cursorToRecord(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        return records;
    }


    private Record cursorToRecord(Cursor cursor) {

        Record record = new Record();
        record.setId(cursor.getLong(0));
        record.setDate(LocalDate.parse(cursor.getString(1)));
        record.setStartTime(LocalTime.parse(cursor.getString(2)));
        record.setEndTime(LocalTime.parse(cursor.getString(3)));

        return record;
    }

}
