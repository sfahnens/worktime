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
import com.github.skyborla.worktime.model.WorkRecord;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalTime;

/**
 * Created by Sebastian on 20.09.2014.
 */
public class WorkRecordItem implements ListViewItem {

    public static class WorkRecordHolder {
        public TextView dayText;
        public TextView dateText;
        public TextView durationText;
        public TextView timeText;
    }

    private WorkRecord workRecord;

    public WorkRecordItem(WorkRecord workRecord) {
        this.workRecord = workRecord;
    }

    @Override
    public int getItemViewType() {
        return 1;
    }

    @Override
    public View getView(Activity activity, View row, ViewGroup parent) {

        WorkRecordHolder holder;
        if (row == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(R.layout.record_list_work_item, parent, false);

            holder = new WorkRecordHolder();
            holder.dayText = (TextView) row.findViewById(R.id.record_list_day);
            holder.dateText = (TextView) row.findViewById(R.id.record_list_date);
            holder.durationText = (TextView) row.findViewById(R.id.record_list_reason);
            holder.timeText = (TextView) row.findViewById(R.id.record_list_time);

            holder.dayText.setWidth(Worktime.DATE_COLUMN_WIDTH);
            
            row.setTag(holder);
        } else {
            holder = (WorkRecordHolder) row.getTag();
        }

        holder.dayText.setText(FormatUtil.DATE_FORMAT_DAY.format(workRecord.getDate()));
        holder.dateText.setText(FormatUtil.DATE_FORMAT_SHORT.format(workRecord.getDate()));

        Duration duration = Duration.between(workRecord.getStartTime(), workRecord.getEndTime());
        LocalTime hackedDuration = LocalTime.of(0, 0).plus(duration);
        holder.durationText.setText("(" + FormatUtil.TIME_FORMAT.format(hackedDuration) + ")");

        holder.timeText.setText(FormatUtil.formatTimes(workRecord));

        return row;
    }

    @Override
    public void onCreateContextMenu(Activity activity, ContextMenu menu) {

        String header = FormatUtil.DATE_FORMAT_SHORT.format(workRecord.getDate());
        header += " (" + FormatUtil.formatTimes(workRecord) + ")";

        menu.setHeaderTitle(header);
        menu.setHeaderIcon(R.drawable.ic_launcher);

        activity.getMenuInflater().inflate(R.menu.records_context, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item, RecordsFragment.RecordsFragmentInteractionListener mListener) {
        switch (item.getItemId()) {
            case R.id.records_context_edit:
                mListener.beginEditWorkRecord(workRecord);
                break;
            case R.id.records_context_delete:
                mListener.beginDeleteWorkRecord(workRecord);
                break;
        }

        return true;
    }
}
