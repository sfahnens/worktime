package com.github.skyborla.worktime.model;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Sebastian on 22.09.2014.
 */
public class Summary {

    private int totalWorkedSeconds = 0;
    private Set<LocalDate> workedDays = new HashSet<LocalDate>();

    private Map<LeaveReason, Integer> leaveCounter = new EnumMap<LeaveReason, Integer>(LeaveReason.class);

    private int added = 0;

    public void add(WorkRecord workRecord) {
        workedDays.add(workRecord.getDate());
        Duration worktime = Duration.between(workRecord.getStartTime(), workRecord.getEndTime());
        totalWorkedSeconds += worktime.getSeconds();

        added++;
    }

    public void add(LeaveRecord leaveRecord) {
        Integer count = leaveCounter.get(leaveRecord.getReason());
        if (count == null) {
            count = 0;
        }
        leaveCounter.put(leaveRecord.getReason(), count + 1);

        added++;
    }

    public int getTotalWorkedSeconds() {
        return totalWorkedSeconds;
    }

    public int getWorkedDays() {
        return workedDays.size();
    }

    public int getLeaveCounter(LeaveReason reason) {
        return leaveCounter.get(reason) == null ? 0 : leaveCounter.get(reason);
    }

    public int getAddedCount() {
        return added;
    }
}
