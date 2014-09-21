package com.github.skyborla.worktime.ui.leave;

import android.view.View;
import android.widget.Toast;

import com.github.skyborla.worktime.R;
import com.github.skyborla.worktime.model.LeaveRecord;
import com.github.skyborla.worktime.model.MetaLeaveRecord;

import org.threeten.bp.LocalDate;

import java.util.List;
import java.util.Set;

/**
 * Created by Sebastian on 19.09.2014.
 */
public class EditLeaveRecordFragment extends LeaveRecordFormFragment {

    public static EditLeaveRecordFragment newInstance(MetaLeaveRecord metaLeaveRecord) {
        return new EditLeaveRecordFragment();
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

                MetaLeaveRecord metaLeaveRecord = new MetaLeaveRecord();
                metaLeaveRecord.setStartDate(startDate.getDate());
                metaLeaveRecord.setEndDate(endDate.getDate());
                metaLeaveRecord.setReason(reason);
                metaLeaveRecord.setWorkdays(workdays);

                Set<LocalDate> affectedMonths = mListener.getDataSource().persistLeaveRecord(metaLeaveRecord);

                if (affectedMonths.isEmpty()) {
                    Toast.makeText(getActivity(), R.string.validate_all_days_on_weekend, Toast.LENGTH_SHORT).show();
                    return;
                }

                mListener.modelChanged(affectedMonths);
                dismiss();

                // XXX
                List<LeaveRecord> leaveRecords = mListener.getDataSource().getLeaveRecords(null);
                for (LeaveRecord record : leaveRecords) {
                    System.out.println(record);
                }
            }
        };
    }
}