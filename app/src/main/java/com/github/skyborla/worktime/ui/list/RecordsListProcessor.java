package com.github.skyborla.worktime.ui.list;

import com.github.skyborla.worktime.model.LeaveRecord;
import com.github.skyborla.worktime.model.MergingListProcessor;
import com.github.skyborla.worktime.model.Summary;
import com.github.skyborla.worktime.model.WorkRecord;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

/**
 * WARNING: NOT THREAD SAFE
 * Created by Sebastian on 20.09.2014.
 */
class RecordsListProcessor extends MergingListProcessor {

    private Summary summary = new Summary();
    private List<ListViewItem> elements = new ArrayList<ListViewItem>();

    public RecordsListProcessor(List<WorkRecord> workRecords, List<LeaveRecord> leaveRecords, List<LocalDate> holidays) {
        super(workRecords, leaveRecords, holidays);
    }

    @Override
    protected void process(WorkRecord workRecord) {
        summary.add(workRecord);
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

    public Summary getSummary() {
        return summary;
    }




}
