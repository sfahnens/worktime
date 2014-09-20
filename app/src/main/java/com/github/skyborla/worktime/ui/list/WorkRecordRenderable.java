package com.github.skyborla.worktime.ui.list;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.skyborla.worktime.FormatUtil;
import com.github.skyborla.worktime.R;
import com.github.skyborla.worktime.model.WorkRecord;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalTime;

/**
 * Created by Sebastian on 20.09.2014.
 */
public class WorkRecordRenderable implements ListViewRenderable {

    public static class WorkRecordHolder {
        public TextView dateText;
        public TextView durationText;
        public TextView timeText;
    }

    private WorkRecord workRecord;

    public WorkRecordRenderable(WorkRecord workRecord) {
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
            row = inflater.inflate(R.layout.record_list_item, parent, false);

            holder = new WorkRecordHolder();
            holder.dateText = (TextView) row.findViewById(R.id.record_list_date);
            holder.durationText = (TextView) row.findViewById(R.id.record_list_duration);
            holder.timeText = (TextView) row.findViewById(R.id.record_list_time);

            row.setTag(holder);
        } else {
            holder = (WorkRecordHolder) row.getTag();
        }

        holder.dateText.setText(FormatUtil.DATE_FORMAT_MEDIUM.format(workRecord.getDate()));

        Duration duration = Duration.between(workRecord.getStartTime(), workRecord.getEndTime());
        LocalTime hackedDuration = LocalTime.of(0, 0).plus(duration);
        holder.durationText.setText("(" + FormatUtil.TIME_FORMAT.format(hackedDuration) + ")");

        holder.timeText.setText(FormatUtil.formatTimes(workRecord));

        return row;
    }
}
