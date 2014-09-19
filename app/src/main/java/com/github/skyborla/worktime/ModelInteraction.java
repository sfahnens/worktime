package com.github.skyborla.worktime;

import com.github.skyborla.worktime.model.DataSource;

import org.threeten.bp.LocalDate;

/**
 * Created by Sebastian on 20.09.2014.
 */
public interface ModelInteraction {

    DataSource getDatasource();

    void modelChanged(LocalDate changed);

}
