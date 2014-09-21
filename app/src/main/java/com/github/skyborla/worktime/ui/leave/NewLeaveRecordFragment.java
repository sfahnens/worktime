package com.github.skyborla.worktime.ui.leave;

import android.view.View;
import android.widget.Toast;

import com.github.skyborla.worktime.R;
import com.github.skyborla.worktime.model.MetaLeaveRecord;

import org.threeten.bp.LocalDate;

import java.util.Set;

/**
 * Created by Sebastian on 19.09.2014.
 */
public class NewLeaveRecordFragment extends LeaveRecordFormFragment {

    public static NewLeaveRecordFragment newInstance() {
        return new NewLeaveRecordFragment();
    }

    public NewLeaveRecordFragment() {
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
                Set<LocalDate> affectedMonths = mListener.getDataSource().persistLeaveRecord(metaLeaveRecord);

                if (affectedMonths.isEmpty()) {
                    Toast.makeText(getActivity(), R.string.validate_all_days_on_weekend, Toast.LENGTH_SHORT).show();
                    return;
                }

                mListener.modelChanged(affectedMonths);
                dismiss();
            }
        };
    }
}
