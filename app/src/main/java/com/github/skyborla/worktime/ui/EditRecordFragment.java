package com.github.skyborla.worktime.ui;

import android.os.Bundle;
import android.view.View;

import com.github.skyborla.worktime.R;

public class EditRecordFragment extends RecordFormFragment {

    public static EditRecordFragment newInstance(long id, String date, String startTime, String endTime) {
        EditRecordFragment fragment = new EditRecordFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, id);
        args.putString(ARG_DATE, date);
        args.putString(ARG_START_TIME, startTime);
        args.putString(ARG_END_TIME, endTime);
        fragment.setArguments(args);
        return fragment;
    }

    public EditRecordFragment() {
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

                if (!validate()) {
                    return;
                }

                mListener.updateRecord(id, date, startTime, endTime);

                dialog.dismiss();
            }
        };
    }

    @Override
    protected View.OnClickListener getOnAbortListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        };
    }

    @Override
    protected int getTitle() {
        return R.string.action_edit_record;
    }
}
