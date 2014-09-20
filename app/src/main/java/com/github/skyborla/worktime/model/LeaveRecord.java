package com.github.skyborla.worktime.model;

import org.threeten.bp.LocalDate;

/**
 * Created by Sebastian on 20.09.2014.
 */
public class LeaveRecord {

    private Long id;
    private Long baseId;

    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveReason reason;
    private Boolean workdays;

    public LeaveRecord() {
    }

    public LeaveRecord(Long id, Long baseId, LocalDate startDate, LocalDate endDate, LeaveReason reason, Boolean workdays) {
        this.id = id;
        this.baseId = baseId;
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

    public Long getBaseId() {
        return baseId;
    }

    public void setBaseId(Long baseId) {
        this.baseId = baseId;
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

    public Boolean getWorkdays() {
        return workdays;
    }

    public void setWorkdays(Boolean workdays) {
        this.workdays = workdays;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LeaveRecord that = (LeaveRecord) o;

        if (baseId != null ? !baseId.equals(that.baseId) : that.baseId != null) return false;
        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (reason != that.reason) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null)
            return false;
        if (workdays != null ? !workdays.equals(that.workdays) : that.workdays != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (baseId != null ? baseId.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (workdays != null ? workdays.hashCode() : 0);
        return result;
    }
}
