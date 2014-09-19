package com.github.skyborla.worktime.ui.control;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.github.skyborla.worktime.FormatUtil;

import org.threeten.bp.LocalTime;

/**
 * Created by Sebastian on 19.09.2014.
 */
public class TimeControl {

    private final FragmentActivity activity;

    private LocalTime time;
    private EditText preview;

    public TimeControl(FragmentActivity activity) {
        this.activity = activity;
    }

    public void setup(View view, int previewControl, int setNowControl, final FormUpdateListener listener) {
        preview = (EditText) view.findViewById(previewControl);
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime(listener);
            }
        });

        Button startTimeButton = (Button) view.findViewById(setNowControl);
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time = LocalTime.now();

                updatePreview();
                listener.onFormUpdated();
            }
        });

        updatePreview();
    }

    private void pickTime(final FormUpdateListener listener) {
        if (time == null) {
            time = LocalTime.now();
        }

        RadialTimePickerDialog
                .newInstance(new RadialTimePickerDialog.OnTimeSetListener() {
                                 @Override
                                 public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
                                     time = LocalTime.of(hour, minute);

                                     updatePreview();
                                     listener.onFormUpdated();
                                 }
                             },
                        time.getHour(),
                        time.getMinute(),
                        true).show(activity.getSupportFragmentManager(), "timePicker");
    }

    private void updatePreview() {
        if (time == null) {
            preview.setText("--:--");
        } else {
            preview.setText(time.format(FormatUtil.TIME_FORMAT));
        }
    }

    public void setTime(String timeString) {
        LocalTime time = FormatUtil.parseTime(timeString);
        if (time == null) {
            setTime(LocalTime.now());
        } else {
            setTime(time);
        }
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public LocalTime getTime() {
        return time;
    }
}
