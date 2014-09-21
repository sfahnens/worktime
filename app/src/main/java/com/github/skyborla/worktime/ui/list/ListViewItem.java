package com.github.skyborla.worktime.ui.list;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Sebastian on 20.09.2014.
 */
public interface ListViewItem {

    int getItemViewType();

    View getView(Activity activity, View row, ViewGroup parent);

    void onCreateContextMenu(Activity activity, ContextMenu menu);

    boolean onContextItemSelected(MenuItem item, RecordsFragment.RecordsFragmentInteractionListener mListener);
}
