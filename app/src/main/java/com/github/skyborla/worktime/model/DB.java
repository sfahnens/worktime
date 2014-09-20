package com.github.skyborla.worktime.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sebastian on 12.09.2014.
 */
public class DB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "worktime.db";

    public static final String TABLE_WORK_RECORDS = "work_records";
    public static final String TABLE_LEAVE_RECORDS = "leave_records";

    public static final String COL_ID = "id";
    public static final String COL_MONTH = "month";
    public static final String COL_DATE = "date";

    // work record
    public static final String COL_START_TIME = "start_time";
    public static final String COL_END_TIME = "end_time";

    // leave record
    public static final String COL_BASE_ID = "base_id";
    public static final String COL_REASON = "reason";
    public static final String COL_WORKDAYS = "workdays";

    public static final String[] WORK_RECORD_COLUMNS =
            new String[]{COL_ID, COL_DATE, COL_START_TIME, COL_END_TIME};
    public static final String[] LEAVE_RECORD_COLUMNS =
            new String[]{COL_ID, COL_BASE_ID, COL_DATE, COL_REASON, COL_WORKDAYS};

    private static final String CREATE_TABLE_WORK_RECORDS = "create table " + TABLE_WORK_RECORDS +
            " (" + COL_ID + " integer primary key autoincrement, " + // PK
            COL_MONTH + " text not null, " +        // fast lookup in gui
            COL_DATE + " text not null, " +
            COL_START_TIME + " text not null, " +
            COL_END_TIME + " text not null);";

    private static final String CREATE_TABLE_LEAVE_RECORDS = "create table " + TABLE_LEAVE_RECORDS +
            " (" + COL_ID + " integer primary key autoincrement, " + // PK
            COL_BASE_ID + " integer, " +            // metadata to group multiday entries
            COL_MONTH + " text not null, " +        // fast lookup in gui
            COL_DATE + " text not null, " +
            COL_REASON + " text not null, " +
            COL_WORKDAYS + " integer not null);";

    public DB(Context context) {
        super(context, DATABASE_NAME, null, 7);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_WORK_RECORDS);
        db.execSQL(CREATE_TABLE_LEAVE_RECORDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists worktime_records");
        db.execSQL("drop table if exists work_records");
        db.execSQL("drop table if exists leave_records");
        onCreate(db);
    }
}
