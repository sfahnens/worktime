package com.github.skyborla.worktime.ui.list;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.skyborla.worktime.FormatUtil;
import com.github.skyborla.worktime.R;
import com.github.skyborla.worktime.Worktime;
import com.github.skyborla.worktime.model.LeaveReason;
import com.github.skyborla.worktime.model.LeaveRecord;

/**
 * Created by Sebastian on 20.09.2014.
 */
public class LeaveRecordItem implements ListViewItem {

    public static class LeaveRecordHolder {
        public TextView dayText;
        public TextView dateText;
        public TextView reasonText;
    }

    private LeaveRecord leaveRecord;

    public LeaveRecordItem(LeaveRecord leaveRecord) {
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
            holder.dayText = (TextView) row.findViewById(R.id.record_list_day);
            holder.dateText = (TextView) row.findViewById(R.id.record_list_date);
            holder.reasonText = (TextView) row.findViewById(R.id.record_list_reason);

            holder.dayText.setWidth(Worktime.DATE_COLUMN_WIDTH);

            row.setTag(holder);
        } else {
            holder = (LeaveRecordHolder) row.getTag();
        }

        holder.dayText.setText(FormatUtil.DATE_FORMAT_DAY.format(leaveRecord.getDate()));
        holder.dateText.setText(FormatUtil.DATE_FORMAT_SHORT.format(leaveRecord.getDate()));
        holder.reasonText.setText(leaveRecord.getReason().stringResource);

        int color;
        if (leaveRecord.getReason() == LeaveReason.HOLIDAY) {
            color = activity.getResources().getColor(android.R.color.holo_purple);
        } else {
            color = activity.getResources().getColor(android.R.color.holo_green_dark);
        }
        holder.reasonText.setTextColor(color);

        return row;
    }

    @Override
    public void onCreateContextMenu(Activity activity, ContextMenu menu) {
        menu.setHeaderTitle(R.string.context_leave);
        menu.setHeaderIcon(R.drawable.ic_launcher);

        activity.getMenuInflater().inflate(R.menu.records_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item, RecordsFragment.RecordsFragmentInteractionListener mListener) {
        switch (item.getItemId()) {
            case R.id.records_context_edit:
                mListener.beginEditLeaveRecord(leaveRecord);
                break;
            case R.id.records_context_delete:
                mListener.beginDeleteLeaveRecord(leaveRecord);
                break;
        }

        return true;
    }
}
