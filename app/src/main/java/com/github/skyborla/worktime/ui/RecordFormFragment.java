package com.github.skyborla.worktime.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.github.skyborla.worktime.FormatUtil;
import com.github.skyborla.worktime.R;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeParseException;

/**
 * Created by Sebastian on 14.09.2014.
 */
public abstract class RecordFormFragment extends DialogFragment {

    protected static final String ARG_ID = "id";
    protected static final String ARG_DATE = "date";
    protected static final String ARG_START_TIME = "startTime";
    protected static final String ARG_END_TIME = "EndTime";

    protected RecordFormFragmentInteractionListener mListener;

    protected long id;

    protected LocalDate date;
    protected LocalTime startTime;
    protected LocalTime endTime;

    protected EditText datePreview;
    protected EditText startTimePreview;
    protected EditText endTimePreview;

    protected View view;
    protected AlertDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getLong(ARG_ID);

            try {
                date = LocalDate.parse(getArguments().getString(ARG_DATE));
            } catch (DateTimeParseException e) {
            }
            try {
                startTime = LocalTime.parse(getArguments().getString(ARG_START_TIME));
            } catch (DateTimeParseException e) {
            }
            try {
                endTime = LocalTime.parse(getArguments().getString(ARG_END_TIME));
            } catch (DateTimeParseException e) {
            }

        } else {
            date = LocalDate.now();
            startTime = LocalTime.now();
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (RecordFormFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.fragment_record_form, null);

        setupForm(view);
        onStateUpdated();

        dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getTitle())
                .setView(view)
                .setPositiveButton(R.string.dialog_form_submit, null)
                .setNegativeButton(R.string.dialog_generic_abort, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog) dialog)
                        .getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(getOnSubmitListener());

                ((AlertDialog) dialog)
                        .getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setOnClickListener(getOnAbortListener());

                if (!isDialogCancelable()) {
                    ((AlertDialog) dialog).setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            System.out.println("try cancel");
                        }
                    });

                    ((AlertDialog) dialog).setCanceledOnTouchOutside(false);
                    ((AlertDialog) dialog).setCancelable(false);
                }
            }
        });


        return dialog;
    }

    private void setupForm(View view) {
        setupDateForm(view);
        setupStartTimeForm(view);
        setupEndTimeForm(view);
    }

    private void setupDateForm(View view) {
        datePreview = (EditText) view.findViewById(R.id.form_date_preview);
        datePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });

        Button dateButton = (Button) view.findViewById(R.id.form_date_today);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = LocalDate.now();
                onStateUpdated();
            }
        });
    }

    private void pickDate() {
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
                                onStateUpdated();
                            }
                        },
                        date.getYear(),
                        date.getMonthValue() - 1,
                        date.getDayOfMonth()).show(getFragmentManager(), "calendardatepicker");
    }

    private void setupStartTimeForm(View view) {
        startTimePreview = (EditText) view.findViewById(R.id.form_start_time_preview);
        startTimePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickStartTime();
            }
        });

        Button startTimeButton = (Button) view.findViewById(R.id.form_start_time_now);
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime = LocalTime.now();
                onStateUpdated();
            }
        });
    }

    private void pickStartTime() {
        if (startTime == null) {
            startTime = LocalTime.now();
        }

        RadialTimePickerDialog
                .newInstance(new RadialTimePickerDialog.OnTimeSetListener() {
                                 @Override
                                 public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
                                     startTime = LocalTime.of(hour, minute);
                                     onStateUpdated();
                                 }
                             },
                        startTime.getHour(),
                        startTime.getMinute(),
                        true).show(getFragmentManager(), "startTimePicker");
    }

    private void setupEndTimeForm(View view) {
        endTimePreview = (EditText) view.findViewById(R.id.form_end_time_preview);
        endTimePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickEndTime();
            }
        });

        Button startTimeButton = (Button) view.findViewById(R.id.form_end_time_now);
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTime = LocalTime.now();
                onStateUpdated();
            }
        });
    }

    private void pickEndTime() {
        if (endTime == null) {
            endTime = LocalTime.now();
        }

        RadialTimePickerDialog
                .newInstance(new RadialTimePickerDialog.OnTimeSetListener() {
                                 @Override
                                 public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
                                     endTime = LocalTime.of(hour, minute);
                                     onStateUpdated();
                                 }
                             },
                        endTime.getHour(),
                        endTime.getMinute(),
                        true).show(getFragmentManager(), "startTimePicker");
    }


    protected void onStateUpdated() {
        if (date == null) {
            datePreview.setText("--. --. ----");
        } else {
            datePreview.setText(date.format(FormatUtil.DATE_FORMAT));
        }

        if (startTime == null) {
            startTimePreview.setText("--:--");
        } else {
            startTimePreview.setText(startTime.format(FormatUtil.TIME_FORMAT));
        }

        if (endTime == null) {
            endTimePreview.setText("--:--");
        } else {
            endTimePreview.setText(endTime.format(FormatUtil.TIME_FORMAT));
        }
    }

    protected boolean validate() {
        if (date == null || startTime == null || endTime == null) {
            Toast.makeText(getActivity(), R.string.record_missing_fields_message, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (endTime.isBefore(startTime)) {
            Toast.makeText(getActivity(), R.string.record_end_before_start, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    protected abstract int getTitle();

    protected abstract boolean isDialogCancelable();

    protected abstract View.OnClickListener getOnSubmitListener();

    protected abstract View.OnClickListener getOnAbortListener();

    public interface RecordFormFragmentInteractionListener {
        void createNewRecord(LocalDate date, LocalTime startTime, LocalTime endTime);

        void updateRecord(long id, LocalDate date, LocalTime startTime, LocalTime endTime);
    }
}
