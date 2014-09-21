package com.github.skyborla.worktime.ui.list;

import com.github.skyborla.worktime.model.LeaveReason;
import com.github.skyborla.worktime.model.LeaveRecord;
import com.github.skyborla.worktime.model.WorkRecord;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.WeekFields;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * WARNING: NOT THREAD SAFE
 * Created by Sebastian on 20.09.2014.
 */
class RecordsListProcessor {

    private final LinkedList<WorkRecord> workRecords = new LinkedList<WorkRecord>();
    private final LinkedList<LeaveRecord> leaveRecords = new LinkedList<LeaveRecord>();
    private final List<LocalDate> holidays;

    private long totalWorkedSeconds = 0;
    private Set<LocalDate> workedDays = new HashSet<LocalDate>();

    long lastWeek = -1;

    private List<ListViewItem> elements = new ArrayList<ListViewItem>();

    public RecordsListProcessor(List<WorkRecord> workRecords, List<LeaveRecord> leaveRecords, List<LocalDate> holidays) {
        this.workRecords.addAll(workRecords);
        this.leaveRecords.addAll(leaveRecords);

        this.holidays = holidays;
    }

    public void process() {

        while (workRecords.size() > 0 || leaveRecords.size() > 0) {

            if (workRecords.size() > 0 && leaveRecords.size() > 0) {
                WorkRecord workRecord = workRecords.peekFirst();
                LeaveRecord leaveRecord = leaveRecords.peekFirst();

                if (!workRecord.getDate().isAfter(leaveRecord.getDate())) {
                    append(workRecord);
                    workRecords.removeFirst();
                } else {
                    append(leaveRecord);
                    leaveRecords.removeFirst();
                }
            } else if (workRecords.size() > 0) {
                for (WorkRecord workRecord : workRecords) {
                    append(workRecord);
                }
                workRecords.clear();
            } else if (leaveRecords.size() > 0) {
                for (LeaveRecord leaveRecord : leaveRecords) {
                    append(leaveRecord);
                }
                leaveRecords.clear();
            }
        }
    }

    private void append(WorkRecord workRecord) {
        checkAppendHeader(workRecord.getDate());

        // stats
        workedDays.add(workRecord.getDate());
        Duration worktime = Duration.between(workRecord.getStartTime(), workRecord.getEndTime());
        totalWorkedSeconds += worktime.getSeconds();

        elements.add(new WorkRecordItem(workRecord));
    }

    private void append(LeaveRecord leaveRecord) {
        checkAppendHeader(leaveRecord.getDate());

        //  holidays override other leave records
        if (leaveRecord.getReason() != LeaveReason.HOLIDAY && holidays.contains(leaveRecord.getDate())) {
            return;
        }

        elements.add(new LeaveRecordItem(leaveRecord));
    }

    private void checkAppendHeader(LocalDate date) {
        int thisWeek = date.get(WeekFields.ISO.weekOfYear());

        if (thisWeek != lastWeek) {
            elements.add(new WeekHeaderItem(thisWeek));
            lastWeek = thisWeek;
        }
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
