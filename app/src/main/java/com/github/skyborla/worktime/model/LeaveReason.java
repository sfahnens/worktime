package com.github.skyborla.worktime.model;

import com.github.skyborla.worktime.R;

/**
 * Created by Sebastian on 19.09.2014.
 */
public enum LeaveReason {

    HOLIDAY(R.string.leave_reason_holiday),
    VACATION(R.string.leave_reason_vacation),
    LEAVE(R.string.leave_reason_leave),
    HEALTH(R.string.leave_reason_health),
    OTHER(R.string.leave_reason_other);

    public final int stringResource;

    LeaveReason(int stringResource) {
        this.stringResource = stringResource;
    }
}
