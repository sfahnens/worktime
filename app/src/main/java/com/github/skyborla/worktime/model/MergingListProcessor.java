package com.github.skyborla.worktime.model;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.WeekFields;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Sebastian on 22.09.2014.
 */
public abstract class MergingListProcessor {

    private final LinkedList<WorkRecord> workRecords = new LinkedList<WorkRecord>();
    private final LinkedList<LeaveRecord> leaveRecords = new LinkedList<LeaveRecord>();
    private final List<LocalDate> holidays;

    int lastWeek = -1;

    public MergingListProcessor(List<WorkRecord> workRecords, List<LeaveRecord> leaveRecords, List<LocalDate> holidays) {
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
                    checkProcess(workRecord);
                    workRecords.removeFirst();
                } else {
                    checkProcess(leaveRecord);
                    leaveRecords.removeFirst();
                }
            } else if (workRecords.size() > 0) {
                for (WorkRecord workRecord : workRecords) {
                    checkProcess(workRecord);
                }
                workRecords.clear();
            } else if (leaveRecords.size() > 0) {
                for (LeaveRecord leaveRecord : leaveRecords) {
                    checkProcess(leaveRecord);
                }
                leaveRecords.clear();
            }
        }
    }

    private void checkProcess(WorkRecord workRecord) {
        checkNewWeek(workRecord.getDate());
        process(workRecord);
    }


    protected abstract void process(WorkRecord workRecord);

    private void checkProcess(LeaveRecord leaveRecord) {
        //  holidays override other leave records
        if (leaveRecord.getReason() != LeaveReason.HOLIDAY && holidays.contains(leaveRecord.getDate())) {
            return;
        }

        checkNewWeek(leaveRecord.getDate());
        process(leaveRecord);
    }

    protected abstract void process(LeaveRecord leaveRecord);

    private void checkNewWeek(LocalDate date) {
        int thisWeek = date.get(WeekFields.ISO.weekOfYear());

        if (thisWeek != lastWeek) {
            newWeek(thisWeek);
            lastWeek = thisWeek;
        }
    }

    protected abstract void newWeek(int week);

}
