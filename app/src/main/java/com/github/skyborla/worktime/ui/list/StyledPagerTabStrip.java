package com.github.skyborla.worktime.ui.list;

import android.content.Context;
import android.support.v4.view.PagerTabStrip;
import android.util.AttributeSet;

/**
 * Created by Sebastian on 13.09.2014.
 */
public class StyledPagerTabStrip extends PagerTabStrip {

    public StyledPagerTabStrip(Context context) {
        super(context);
        style();
    }

    public StyledPagerTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        style();
    }

    public void style() {
        setDrawFullUnderline(true);
        setTabIndicatorColorResource(android.R.color.holo_blue_light);
    }
}
