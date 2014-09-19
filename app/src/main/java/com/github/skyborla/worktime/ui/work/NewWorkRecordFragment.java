package com.github.skyborla.worktime.ui.work;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.github.skyborla.worktime.R;
import com.github.skyborla.worktime.Worktime;

public class NewWorkRecordFragment extends WorkRecordFormFragment {

    public static NewWorkRecordFragment newInstance() {
        return new NewWorkRecordFragment();
    }

    public static NewWorkRecordFragment newInstance(String date, String startTime, String endTime) {
        NewWorkRecordFragment fragment = new NewWorkRecordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE, date);
        args.putString(ARG_START_TIME, startTime);
        args.putString(ARG_END_TIME, endTime);
        fragment.setArguments(args);
        return fragment;
    }

    public NewWorkRecordFragment() {
        // Required empty public constructor
    }

    @Override
    protected boolean isDialogCancelable() {
        return false;
    }

    @Override
    protected View.OnClickListener getOnSubmitListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validate()) {
                    return;
                }

                mListener.createNewRecord(date.getDate(), startTime.getTime(), endTime.getTime());
                dialog.dismiss();
            }
        };
    }

    @Override
    protected View.OnClickListener getOnAbortListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
                editor.putBoolean(Worktime.PENDING_RECORD, false);
                editor.commit();
                dialog.dismiss();
            }
        };
    }

    @Override
    public void onFormUpdated() {
        SharedPreferences.Editor editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
        editor.putBoolean(Worktime.PENDING_RECORD, true);

        editor.putString(Worktime.PENDING_DATE, (date == null) ? null : date.toString());
        editor.putString(Worktime.PENDING_START_TIME, (startTime == null) ? null : startTime.toString());
        editor.putString(Worktime.PENDING_END_TIME, (endTime == null) ? null : endTime.toString());

        editor.commit();
    }

    @Override
    protected int getTitle() {
        return R.string.action_new_work_record;
    }
}
