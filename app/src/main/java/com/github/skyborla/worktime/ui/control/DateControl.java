package com.github.skyborla.worktime.ui.control;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.github.skyborla.worktime.FormatUtil;
import com.github.skyborla.worktime.R;

import org.threeten.bp.LocalDate;

/**
 * Created by Sebastian on 19.09.2014.
 */
public class DateControl {

    private final FragmentActivity activity;

    private LocalDate date;
    private EditText preview;

    public DateControl(FragmentActivity activity) {
        this.activity = activity;
    }

    public void setup(View view, int previewControl, int setNowControl, final FormUpdateListener listener) {
        preview = (EditText) view.findViewById(previewControl);
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate(listener);
            }
        });

        Button dateButton = (Button) view.findViewById(setNowControl);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = LocalDate.now();

                updatePreview();
                listener.onFormUpdated();
            }
        });

        updatePreview();
    }

    private void pickDate(final FormUpdateListener listener) {
        if (date == null) {
            date = LocalDate.now();
        }

        CalendarDatePickerDialog
                .newInstance(
                        new CalendarDatePickerDialog
                                .OnDateSetListener() {
                            @Override
                            public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int month, int day) {
                                date = LocalDate.of(year, month + 1, day);
                                updatePreview();
                                listener.onFormUpdated();
                            }
                        },
                        date.getYear(),
                        date.getMonthValue() - 1,
                        date.getDayOfMonth()).show(activity.getSupportFragmentManager(), "calendardatepicker");
    }

    private void updatePreview() {
        if (date == null) {
            preview.setText("--. --. ----");
        } else {
            preview.setText(date.format(FormatUtil.DATE_FORMAT));
        }
    }


    public void setDate(String dateString) {
        LocalDate date = FormatUtil.parseDate(dateString);
        if (date == null) {
            setDate(LocalDate.now());
        } else {
            setDate(date);
        }
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }
}
