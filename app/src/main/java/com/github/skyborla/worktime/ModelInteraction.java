package com.github.skyborla.worktime;

import com.github.skyborla.worktime.model.DataSource;

import org.threeten.bp.LocalDate;

import java.util.Set;

/**
 * Created by Sebastian on 20.09.2014.
 */
public interface ModelInteraction {

    DataSource getDatasource();

    void modelChanged(Set<LocalDate> changed);

    void modelChanged(LocalDate changed);

}
