package com.github.skyborla.worktime.model;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import java.io.Serializable;

/**
 * Created by Sebastian on 12.09.2014.
 */
public class WorkRecord implements Serializable {



    private Long id;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public WorkRecord() {
    }

    public WorkRecord(LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return id.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkRecord workRecord = (WorkRecord) o;

        if (date != null ? !date.equals(workRecord.date) : workRecord.date != null) return false;
        if (endTime != null ? !endTime.equals(workRecord.endTime) : workRecord.endTime != null)
            return false;
        if (id != null ? !id.equals(workRecord.id) : workRecord.id != null) return false;
        if (startTime != null ? !startTime.equals(workRecord.startTime) : workRecord.startTime != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        return result;
    }
}
