package com.github.skyborla.worktime.ui.list;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.skyborla.worktime.FormatUtil;
import com.github.skyborla.worktime.R;
import com.github.skyborla.worktime.model.LeaveRecord;

/**
 * Created by Sebastian on 20.09.2014.
 */
public class LeaveRecordRenderable implements ListViewRenderable {

    public static class LeaveRecordHolder {
        public TextView dateText;
        public TextView reasonText;
    }

    private LeaveRecord leaveRecord;

    public LeaveRecordRenderable(LeaveRecord leaveRecord) {
        this.leaveRecord = leaveRecord;
    }

    @Override
    public int getItemViewType() {
        return 2;
    }

    @Override
    public View getView(Activity activity, View row, ViewGroup parent) {

        LeaveRecordHolder holder;
        if (row == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(R.layout.record_list_leave_item, parent, false);

            holder = new LeaveRecordHolder();
            holder.dateText = (TextView) row.findViewById(R.id.record_list_date);
            holder.reasonText = (TextView) row.findViewById(R.id.record_list_reason);

            row.setTag(holder);
        } else {
            holder = (LeaveRecordHolder) row.getTag();
        }

        holder.dateText.setText(FormatUtil.DATE_FORMAT_MEDIUM.format(leaveRecord.getDate()));
        holder.reasonText.setText(leaveRecord.getReason().stringResource);

        return row;
    }
}
