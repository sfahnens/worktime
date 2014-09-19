package com.github.skyborla.worktime.ui.work;

import android.os.Bundle;
import android.view.View;

import com.github.skyborla.worktime.R;

public class EditWorkRecordFragment extends WorkRecordFormFragment {

    public static EditWorkRecordFragment newInstance(long id, String date, String startTime, String endTime) {
        EditWorkRecordFragment fragment = new EditWorkRecordFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, id);
        args.putString(ARG_DATE, date);
        args.putString(ARG_START_TIME, startTime);
        args.putString(ARG_END_TIME, endTime);
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
                if (!validate()) {
                    return;
                }

                mListener.updateRecord(id, date.getDate(), startTime.getTime(), endTime.getTime());
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
