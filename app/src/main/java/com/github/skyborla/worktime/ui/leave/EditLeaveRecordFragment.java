package com.github.skyborla.worktime.ui.leave;

import android.os.Bundle;
import android.view.View;

import com.github.skyborla.worktime.model.MetaLeaveRecord;

import org.threeten.bp.LocalDate;

import java.util.Set;

/**
 * Created by Sebastian on 19.09.2014.
 */
public class EditLeaveRecordFragment extends LeaveRecordFormFragment {

    public static EditLeaveRecordFragment newInstance(MetaLeaveRecord metaLeaveRecord) {
        EditLeaveRecordFragment fragment = new EditLeaveRecordFragment();
        Bundle args = new Bundle();

        args.putLong(ARG_ID, metaLeaveRecord.getId());
        args.putString(ARG_START_DATE, metaLeaveRecord.getStartDate().toString());
        args.putString(ARG_END_DATE, metaLeaveRecord.getEndDate().toString());
        args.putString(ARG_REASON, metaLeaveRecord.getReason().toString());
        args.putBoolean(ARG_WORKDAYS, metaLeaveRecord.isWorkdays());

        fragment.setArguments(args);
        return fragment;
    }

    public EditLeaveRecordFragment() {
        // Required empty public constructor
    }

    @Override
    protected View.OnClickListener getOnSubmitListener() {

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isValid()) {
                    return;
                }

                MetaLeaveRecord metaLeaveRecord = getMetaLeaveRecord();

                Set<LocalDate> affectedMonths = mListener.getDataSource().updateLeaveRecord(metaLeaveRecord);
                mListener.modelChanged(affectedMonths);

                dismiss();
            }
        };
    }
}
