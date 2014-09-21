package com.github.skyborla.worktime.ui.work;

import android.os.Bundle;
import android.view.View;

import com.github.skyborla.worktime.R;
import com.github.skyborla.worktime.model.WorkRecord;

public class EditWorkRecordFragment extends WorkRecordFormFragment {

    public static EditWorkRecordFragment newInstance(WorkRecord workRecord) {
        EditWorkRecordFragment fragment = new EditWorkRecordFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, workRecord.getId());
        args.putString(ARG_DATE, workRecord.getDate().toString());
        args.putString(ARG_START_TIME, workRecord.getStartTime().toString());
        args.putString(ARG_END_TIME, workRecord.getEndTime().toString());
        fragment.setArguments(args);
        return fragment;
    }

    public EditWorkRecordFragment() {
        // Required empty public constructor
    }

    @Override
    protected boolean isDialogCancelable() {
        return true;
    }

    @Override
    protected View.OnClickListener getOnSubmitListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isValid()) {
                    return;
                }

                WorkRecord workRecord = new WorkRecord();
                workRecord.setId(id);
                workRecord.setDate(date.getDate());
                workRecord.setStartTime(startTime.getTime());
                workRecord.setEndTime(endTime.getTime());

                mListener.getDataSource().updateWorkRecord(workRecord);
                mListener.modelChanged(date.getDate());

                dialog.dismiss();
            }
        };
    }

    @Override
    protected View.OnClickListener getOnAbortListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        };
    }

    @Override
    protected int getTitle() {
        return R.string.action_edit_record;
    }
}
