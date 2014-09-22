package com.github.skyborla.worktime.ui.list;

import com.github.skyborla.worktime.model.LeaveRecord;
import com.github.skyborla.worktime.model.MergingListProcessor;
import com.github.skyborla.worktime.model.WorkRecord;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * WARNING: NOT THREAD SAFE
 * Created by Sebastian on 20.09.2014.
 */
class RecordsListProcessor extends MergingListProcessor {

    private long totalWorkedSeconds = 0;
    private Set<LocalDate> workedDays = new HashSet<LocalDate>();

    private List<ListViewItem> elements = new ArrayList<ListViewItem>();

    public RecordsListProcessor(List<WorkRecord> workRecords, List<LeaveRecord> leaveRecords, List<LocalDate> holidays) {
        super(workRecords, leaveRecords, holidays);
    }

    @Override
    protected void process(WorkRecord workRecord) {
        // stats
        workedDays.add(workRecord.getDate());
        Duration worktime = Duration.between(workRecord.getStartTime(), workRecord.getEndTime());
        totalWorkedSeconds += worktime.getSeconds();

        elements.add(new WorkRecordItem(workRecord));
    }

    @Override
    protected void process(LeaveRecord leaveRecord) {
        elements.add(new LeaveRecordItem(leaveRecord));
    }

    @Override
    protected void newWeek(int week) {
        elements.add(new WeekHeaderItem(week));
    }

    public List<ListViewItem> getElements() {
        return elements;
    }

    public long getTotalWorkedSeconds() {
        return totalWorkedSeconds;
    }

    public int getWorkedHours() {
        return (int) totalWorkedSeconds / 3600;
    }

    public int getWorkedMinutes() {
        return (int) (totalWorkedSeconds / 60) % 60;
    }

    public int getWorkedDays() {
        return workedDays.size();
    }


}
