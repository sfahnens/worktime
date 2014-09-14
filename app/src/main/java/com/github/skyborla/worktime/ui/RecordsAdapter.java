package com.github.skyborla.worktime.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.skyborla.worktime.FormatUtil;
import com.github.skyborla.worktime.R;
import com.github.skyborla.worktime.model.Record;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalTime;

import java.util.List;

/**
 * Created by Sebastian on 14.09.2014.
 */
public class RecordsAdapter extends ArrayAdapter<Object> {

    public RecordsAdapter(Context context, List<Object> objects) {
        super(context, 0, objects);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) { // TODO

        Object item = getItem(position);

        if (item instanceof RecordsFragment.WeekHeader) {
            return 0;
        }

        if (item instanceof Record) {
            return 1;
        }

        throw new IllegalArgumentException();
    }


    @Override
    public long getItemId(int position) {
        Object item = getItem(position);
        return item.hashCode();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        RecordsFragment.RecordHolder holder = null;

        Object i = getItem(position);

        if (i instanceof RecordsFragment.WeekHeader) {
            return getWeekHeaderRow(row, parent, (RecordsFragment.WeekHeader) i);
        }

        if (i instanceof Record) {
            return getRecordRow(row, parent, (Record) i);
        }

        throw new IllegalArgumentException();
    }

    private View getWeekHeaderRow(View row, ViewGroup parent, RecordsFragment.WeekHeader i) {
        RecordsFragment.RecordHolder holder;
        if (row == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            row = inflater.inflate(R.layout.record_list_header, parent, false);
        }

        TextView text = (TextView) row.findViewById(R.id.record_list_week);
        text.setText("KW " + Integer.toString(i.week));
        return row;
    }

    private View getRecordRow(View row, ViewGroup parent, Record record) {
        RecordsFragment.RecordHolder holder;
        if (row == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            row = inflater.inflate(R.layout.record_list_item, parent, false);

            holder = new RecordsFragment.RecordHolder();
            holder.dateText = (TextView) row.findViewById(R.id.record_list_date);
            holder.durationText = (TextView) row.findViewById(R.id.record_list_duration);
            holder.timeText = (TextView) row.findViewById(R.id.record_list_time);

            row.setTag(holder);
        } else {
            holder = (RecordsFragment.RecordHolder) row.getTag();
        }

        holder.dateText.setText(FormatUtil.DATE_FORMAT_MEDIUM.format(record.getDate()));

        Duration duration = Duration.between(record.getStartTime(), record.getEndTime());
        LocalTime hackedDuration = LocalTime.of(0, 0).plus(duration);
        holder.durationText.setText("(" + FormatUtil.TIME_FORMAT.format(hackedDuration) + ")");

        holder.timeText.setText(FormatUtil.formatTimes(record));

        return row;
    }
}
