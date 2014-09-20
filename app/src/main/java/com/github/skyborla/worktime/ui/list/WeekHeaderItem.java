package com.github.skyborla.worktime.ui.list;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.skyborla.worktime.R;

/**
 * Created by Sebastian on 20.09.2014.
 */
public class WeekHeaderItem implements ListViewItem {

    private int week;

    public WeekHeaderItem(int week) {
        this.week = week;
    }

    @Override
    public int getItemViewType() {
        return 0;
    }

    @Override
    public View getView(Activity activity, View row, ViewGroup parent) {
        if (row == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(R.layout.record_list_header_item, parent, false);
        }

        TextView text = (TextView) row.findViewById(R.id.record_list_week);
        text.setText("KW " + Integer.toString(week));
        return row;
    }

    @Override
    public void onCreateContextMenu(Activity activity, ContextMenu menu) {
    }

    @Override
    public boolean onContextItemSelected(MenuItem item, RecordsFragment.RecordsFragmentInteractionListener mListener) {
        return false;
    }
}