package com.github.skyborla.worktime.model;

import org.threeten.bp.LocalDate;

/**
 * Created by Sebastian on 20.09.2014.
 */
public class LeaveRecord {

    private Long id;
    private Long baseId;

    private LocalDate date;
    private LeaveReason reason;
    private Boolean workdays;

    public LeaveRecord() {
    }

    public LeaveRecord(Long id, Long baseId, LocalDate date, LeaveReason reason, Boolean workdays) {
        this.id = id;
        this.baseId = baseId;
        this.date = date;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (reason != that.reason) return false;
        if (workdays != null ? !workdays.equals(that.workdays) : that.workdays != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (baseId != null ? baseId.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        result = 31 * result + (workdays != null ? workdays.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LeaveRecord{" +
                "id=" + id +
                ", baseId=" + baseId +
                ", date=" + date +
                ", reason=" + reason +
                ", workdays=" + workdays +
                '}';
    }
}
