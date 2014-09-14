package com.github.skyborla.worktime;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.cocosw.undobar.UndoBarController;
import com.github.skyborla.worktime.model.Record;
import com.github.skyborla.worktime.model.RecordDataSource;
import com.github.skyborla.worktime.ui.EditRecordFragment;
import com.github.skyborla.worktime.ui.NewRecordFragment;
import com.github.skyborla.worktime.ui.RecordFormFragment;
import com.github.skyborla.worktime.ui.RecordsFragment;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


public class Worktime extends FragmentActivity implements RecordFormFragment.RecordFormFragmentInteractionListener, RecordsFragment.RecordsFragmentInteractionListener {

    public static final String PENDING_RECORD = "PENDING_RECORD";
    public static final String PENDING_DATE = "PENDING_DATE";
    public static final String PENDING_START_TIME = "PENDING_START_TIME";
    public static final String PENDING_END_TIME = "PENDING_END_TIME";

    private RecordDataSource dataSource;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private List<String> months;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worktime);

        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);

        if (pref.getBoolean(PENDING_RECORD, false)) {
            NewRecordFragment.newInstance(
                    pref.getString(PENDING_DATE, ""),
                    pref.getString(PENDING_START_TIME, ""),
                    pref.getString(PENDING_END_TIME, "")).show(getSupportFragmentManager(), "newRecord");
        }


        dataSource = new RecordDataSource(this);
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        months = dataSource.getMonths();


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        String now = RecordDataSource.DB_MONTH_DATE_FORMAT.format(LocalDate.now());
        if (months.contains(now)) {
            mViewPager.setCurrentItem(months.indexOf(now));
        } else if (months.size() > 0) {
            mViewPager.setCurrentItem(months.size() - 1);
        }
    }

    @Override
    protected void onResume() {
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        dataSource.close();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        switch (item.getItemId()) {
            case R.id.action_new_record:
                NewRecordFragment.newInstance().show(getSupportFragmentManager(), "newRecord");
                return true;
            case R.id.action_send_email:


                try {
                    File temp = File.createTempFile("asd", "bsd", getCacheDir());

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("application/octet-stream");
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(temp));
                    startActivity(Intent.createChooser(intent, "Email senden"));

                } catch (IOException e) {
                    e.printStackTrace();
                }


                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void createNewRecord(LocalDate date, LocalTime startTime, LocalTime endTime) {

        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        editor.putBoolean(PENDING_RECORD, false);
        editor.commit();

        Record record = new Record();
        record.setDate(date);
        record.setStartTime(startTime);
        record.setEndTime(endTime);

        dataSource.persist(record);
        updateView(date, true);
    }

    @Override
    public void updateRecord(long id, LocalDate date, LocalTime startTime, LocalTime endTime) {
        Record record = new Record();
        record.setId(id);
        record.setDate(date);
        record.setStartTime(startTime);
        record.setEndTime(endTime);

        dataSource.update(record);
        updateView(date, true);
    }

    private void updateView(LocalDate date, boolean go) {
        months = dataSource.getMonths();
        mSectionsPagerAdapter.notifyDataSetChanged();

        String dbFormatted = RecordDataSource.DB_MONTH_DATE_FORMAT.format(date);

        if (go) {
            mViewPager.setCurrentItem(months.indexOf(dbFormatted));
        }

        String tag = "android:switcher:" + R.id.pager + ":" + dbFormatted;
        RecordsFragment fragment = (RecordsFragment) getFragmentManager().findFragmentByTag(tag);

        if (fragment != null) {
            fragment.onRecordsUpdated();
        }
    }

    @Override
    public void requestEdit(Record record) {
        EditRecordFragment
                .newInstance(record.getId(),
                        record.getDate().toString(),
                        record.getStartTime().toString(),
                        record.getEndTime().toString())
                .show(getSupportFragmentManager(), "editRecord");
    }

    @Override
    public void requestDelete(final Record record) {

        String message = "\n \u2022 " + FormatUtil.DATE_FORMAT_MEDIUM.format(record.getDate());
        message += " (" + FormatUtil.formatTimes(record) + ")\n";

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_confirm_delete_header)
                .setMessage(message)
                .setNegativeButton(R.string.dialog_generic_abort, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton(R.string.dialog_delete_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataSource.delete(record);
                        updateView(record.getDate(), false);

                        new UndoBarController.UndoBar(Worktime.this)
                                .message(R.string.undo_delete)
                                .listener(new UndoBarController.UndoListener() {
                                    @Override
                                    public void onUndo(Parcelable parcelable) {
                                        createNewRecord(record.getDate(), record.getStartTime(), record.getEndTime());
                                    }
                                })
                                .duration(10000)
                                .show(true);
                    }
                })
                .create().show();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            System.out.println("new " + position + " - " + months.get(position));
            return RecordsFragment.newInstance(months.get(position));
        }

        @Override
        public int getCount() {
            return months.size();
        }

        @Override
        public long getItemId(int position) {
            String month = months.get(position);
            return Long.valueOf(month);
        }

        @Override
        public int getItemPosition(Object object) {
            return months.indexOf(((RecordsFragment) object).getMonth());
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String dbFormatted = months.get(position);

            if (dbFormatted.length() == 6) {

                LocalDate date = LocalDate.of(Integer.valueOf(dbFormatted.substring(0, 4)),
                        Integer.valueOf(dbFormatted.substring(4)), 1);

                return FormatUtil.DATE_FORMAT_MONTH.format(date).toUpperCase();
            }

            return "-";
        }
    }

}
