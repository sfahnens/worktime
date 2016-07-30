package com.github.skyborla.worktime;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.skyborla.worktime.export.RecordsExporter;
import com.github.skyborla.worktime.model.DataSource;
import com.github.skyborla.worktime.model.LeaveRecord;
import com.github.skyborla.worktime.model.WorkRecord;
import com.github.skyborla.worktime.ui.AboutDialog;
import com.github.skyborla.worktime.ui.leave.DeleteLeaveRecordHelper;
import com.github.skyborla.worktime.ui.leave.EditLeaveRecordFragment;
import com.github.skyborla.worktime.ui.leave.NewLeaveRecordFragment;
import com.github.skyborla.worktime.ui.list.RecordsFragment;
import com.github.skyborla.worktime.ui.work.DeleteWorkRecordHelper;
import com.github.skyborla.worktime.ui.work.EditWorkRecordFragment;
import com.github.skyborla.worktime.ui.work.NewWorkRecordFragment;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Worktime extends AppCompatActivity implements RecordsFragment.RecordsFragmentInteractionListener, ModelInteraction {

    public static int DATE_COLUMN_WIDTH;

    public static final String PENDING_RECORD = "PENDING_RECORD";
    public static final String PENDING_DATE = "PENDING_DATE";
    public static final String PENDING_START_TIME = "PENDING_START_TIME";
    public static final String PENDING_END_TIME = "PENDING_END_TIME";

    private DataSource dataSource;

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    private List<LocalDate> months;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_worktime);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));


        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);

        if (pref.getBoolean(PENDING_RECORD, false)) {
            NewWorkRecordFragment.newInstance(
                    pref.getString(PENDING_DATE, ""),
                    pref.getString(PENDING_START_TIME, ""),
                    pref.getString(PENDING_END_TIME, "")).show(getSupportFragmentManager(), "newWorkRecord");
        }

        dataSource = new DataSource(this);
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

        LocalDate now = LocalDate.now().withDayOfMonth(1);
        if (months.contains(now)) {
            mViewPager.setCurrentItem(months.indexOf(now));
        } else if (months.size() > 0) {
            mViewPager.setCurrentItem(months.size() - 1);
        }

        initializeDayColumnListWidth();
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
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_work_record:
                NewWorkRecordFragment.newInstance().show(getSupportFragmentManager(), "newWorkRecord");
                return true;

            case R.id.action_new_leave_record:
                NewLeaveRecordFragment.newInstance().show(getSupportFragmentManager(), "newLeaveRecord");
                return true;

            case R.id.action_send_email:
                RecordsExporter exporter = new RecordsExporter(this, dataSource);
                exporter.execute();
                exporter.promise().done(new DoneCallback<Uri>() {
                    @Override
                    public void onDone(Uri uri) {
                        System.out.println("DONE");

                        String text = "Arbeitszeit Export " + LocalDateTime.now().format(FormatUtil.DATE_TIME_FORMATTER_FULL);

                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.setType("application/octet-stream");
                        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, text);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                        sendIntent.putExtra(Intent.EXTRA_TITLE, text);
                        startActivity(Intent.createChooser(sendIntent, "Email senden"));
                    }
                }).fail(new FailCallback<Throwable>() {
                    @Override
                    public void onFail(Throwable result) {
                        Toast.makeText(Worktime.this, R.string.export_failed, Toast.LENGTH_SHORT).show();
                        result.printStackTrace();
                    }
                });
                return true;

            case R.id.action_about_dialog:
                new AboutDialog().show(getSupportFragmentManager(), "about");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void beginEditWorkRecord(WorkRecord workRecord) {
        EditWorkRecordFragment
                .newInstance(workRecord)
                .show(getSupportFragmentManager(), "editWorkRecord");
    }

    @Override
    public void beginDeleteWorkRecord(final WorkRecord workRecord) {
        DeleteWorkRecordHelper helper = new DeleteWorkRecordHelper(workRecord, this, this);
        helper.confirmAndDelete();
    }

    @Override
    public void beginEditLeaveRecord(LeaveRecord leaveRecord) {
        EditLeaveRecordFragment
                .newInstance(dataSource.getMetaLeaveRecord(leaveRecord))
                .show(getSupportFragmentManager(), "editLeaveRecord");
    }

    @Override
    public void beginDeleteLeaveRecord(final LeaveRecord leaveRecord) {
        DeleteLeaveRecordHelper helper = new DeleteLeaveRecordHelper(leaveRecord, this, this);
        helper.confirmAndDelete();
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void modelChanged(Set<LocalDate> changed) {

        LocalDate currentMonth = null;
        try {
            currentMonth = months.get(mViewPager.getCurrentItem());
        } catch (Throwable t) {
            System.out.println("MODEL CHANGED : cannot determine current month.");
        }

        months = dataSource.getMonths();
        mSectionsPagerAdapter.notifyDataSetChanged();

        // propagate update to pages
        for (LocalDate date : changed) {
            String tag = "android:switcher:" + R.id.pager + ":" + date.hashCode();
            RecordsFragment fragment = (RecordsFragment) getFragmentManager().findFragmentByTag(tag);

            if (fragment != null) {
                fragment.onRecordsUpdated();
            } else {
                System.out.println("FRAGMENT null " + date);
            }
        }

        // determine if we need to change the page
        Set<LocalDate> displayCandidates = new HashSet<LocalDate>();
        displayCandidates.addAll(changed);
        displayCandidates.retainAll(months);

        LocalDate firstDisplayCandidate = null;
        if (!displayCandidates.isEmpty()) {
            firstDisplayCandidate = displayCandidates.iterator().next();
        }

        // current month changed -> do nothing
        if (displayCandidates.contains(currentMonth)) {
            System.out.println("MODEL CHANGED : Current Month modified, do nothing.");
            return;
        }

        // try to go to a visible changed page
        else if (firstDisplayCandidate != null) {
            int target = months.indexOf(firstDisplayCandidate);

            System.out.println("MODEL CHANGED : Go to first display candidate (index) " + target);
            mViewPager.setCurrentItem(target);
        }

        // ensure valid page (unrelated to change)
        else {
            LocalDate thisMonth = LocalDate.now().withDayOfMonth(1);

            if (currentMonth != null && months.contains(currentMonth)) {
                System.out.println("MODEL CHANGED : WAT?.");
                return;
            } else if (months.contains(thisMonth)) {
                System.out.println("MODEL CHANGED : Go to this month.");
                mViewPager.setCurrentItem(months.indexOf(thisMonth));
            } else {
                System.out.println("MODEL CHANGED : Go to view zero.");
                mViewPager.setCurrentItem(0);
            }
        }
    }

    @Override
    public void modelChanged(LocalDate date) {
        Set<LocalDate> affectedMonth = new HashSet<LocalDate>();
        affectedMonth.add(date);
        modelChanged(affectedMonth);
    }

    private void initializeDayColumnListWidth() {
        int max = 0;
        for (String s : DateFormatSymbols.getInstance().getShortWeekdays()) {
            TextView v = new TextView(this);
            v.setTextSize(18);
            v.setText(s);
            v.measure(0, 0);

            max = Math.max(max, v.getMeasuredWidth());
        }

        DATE_COLUMN_WIDTH = max + 8;
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
            return RecordsFragment.newInstance(months.get(position));
        }

        @Override
        public int getCount() {
            return months.size();
        }

        @Override
        public long getItemId(int position) {
            return months.get(position).hashCode();
        }

        @Override
        public int getItemPosition(Object object) {
            String monthString = ((RecordsFragment) object).getMonth();
            int position = months.indexOf(FormatUtil.parseDBMonthFormat(monthString));
            System.out.println(monthString + "@" + position);
            return position;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            LocalDate date = months.get(position);
            return FormatUtil.DATE_FORMAT_MONTH.format(date).toUpperCase();
        }
    }
}
