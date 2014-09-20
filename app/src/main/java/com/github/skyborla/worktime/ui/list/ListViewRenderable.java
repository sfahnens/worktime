package com.github.skyborla.worktime.ui.list;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Sebastian on 20.09.2014.
 */
public interface ListViewRenderable {
    int getItemViewType();

    View getView(Activity activity, View row, ViewGroup parent);
}
