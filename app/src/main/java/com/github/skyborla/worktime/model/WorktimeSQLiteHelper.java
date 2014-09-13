package com.github.skyborla.worktime.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sebastian on 12.09.2014.
 */
public class WorktimeSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_WORKTIME_RECORDS = "worktime_records";
    public static final String DATABASE_NAME = "worktime.db";

    public static final String COL_ID = "id";

    public static final String COL_DATE = "date";
    public static final String COL_MONTH = "month";

    public static final String COL_START_TIME = "start_time";
    public static final String COL_END_TIME = "end_time";

    public static final String[] RECORD_COLUMNS = new String[]{COL_ID, COL_DATE, COL_START_TIME, COL_END_TIME};

    private static final String CREATE = "create table " + TABLE_WORKTIME_RECORDS +
            " (" + COL_ID + " integer primary key autoincrement, " +
            COL_DATE + " text not null, " +
            COL_MONTH + " text not null, " +
            COL_START_TIME + " text, " +
            COL_END_TIME + " text);";

    private static final String DROP = "drop table if exists " + TABLE_WORKTIME_RECORDS;

    public WorktimeSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP);
        onCreate(db);
    }
}
