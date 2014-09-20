package com.github.skyborla.worktime.ui.leave;

import android.view.View;
import android.widget.Toast;

import com.github.skyborla.worktime.R;
import com.github.skyborla.worktime.model.LeaveRecord;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

import java.util.LinkedHashSet;
import java.util.List;
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

                LocalDate start = startDate.getDate();
                LocalDate end = endDate.getDate();

                LeaveRecord leaveRecord = new LeaveRecord();
                leaveRecord.setReason(reason);
                leaveRecord.setWorkdays(workdays);

                Set<LocalDate> affectedMonths = new LinkedHashSet<LocalDate>();

                LocalDate date = start;
                while (!date.isAfter(end)) {

                    if (workdays &&
                            (date.getDayOfWeek() == DayOfWeek.SATURDAY ||
                                    date.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                        date = date.plusDays(1);
                        continue;
                    }

                    leaveRecord.setDate(date);
                    affectedMonths.add(date.withDayOfMonth(1));

                    long id = mListener.getDatasource().persistLeaveRecord(leaveRecord);
                    if (leaveRecord.getBaseId() == null) {
                        leaveRecord.setBaseId(id);
                    }

                    date = date.plusDays(1);
                }

                if(affectedMonths.isEmpty()) {
                    Toast.makeText(getActivity(), R.string.all_days_on_weekend, Toast.LENGTH_SHORT).show();
                }


                mListener.modelChanged(start);
                dismiss();

                List<LeaveRecord> leaveRecords = mListener.getDatasource().getLeaveRecords(null);
                for (LeaveRecord record : leaveRecords) {
                    System.out.println(record);
                }
            }
        };
    }
}
