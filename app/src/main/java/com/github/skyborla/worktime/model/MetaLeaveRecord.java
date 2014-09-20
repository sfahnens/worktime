package com.github.skyborla.worktime.model;

import org.threeten.bp.LocalDate;

/**
 * Created by Sebastian on 20.09.2014.
 */
public class MetaLeaveRecord {

    private Long id;

    private LocalDate startDate;
    private LocalDate endDate;

    private LeaveReason reason;
    private boolean workdays;

    public MetaLeaveRecord() {
    }

    public MetaLeaveRecord(Long id, LocalDate startDate, LocalDate endDate, LeaveReason reason, boolean workdays) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.workdays = workdays;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LeaveReason getReason() {
        return reason;
    }

    public void setReason(LeaveReason reason) {
        this.reason = reason;
    }

    public boolean isWorkdays() {
        return workdays;
    }

    public void setWorkdays(boolean workdays) {
        this.workdays = workdays;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetaLeaveRecord that = (MetaLeaveRecord) o;

        if (workdays != that.workdays) return false;
        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (reason != that.reason) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (workdays ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MetaLeaveRecord{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", reason=" + reason +
                ", workdays=" + workdays +
                '}';
    }
}
