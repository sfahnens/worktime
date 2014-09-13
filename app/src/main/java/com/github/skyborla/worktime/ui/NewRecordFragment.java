package com.github.skyborla.worktime.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import com.github.skyborla.worktime.DateUtil;
import com.github.skyborla.worktime.R;
import com.github.skyborla.worktime.Worktime;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeParseException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewRecordFragment.NewFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewRecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewRecordFragment extends DialogFragment {

    private static final String ARG_DATE = "date";
    private static final String ARG_START_TIME = "startTime";
    private static final String ARG_END_TIME = "EndTime";

    private NewFragmentInteractionListener mListener;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    private EditText datePreview;
    private EditText startTimePreview;
    private EditText endTimePreview;
    private View view;
    private AlertDialog dialog;

    public static NewRecordFragment newInstance() {
        return new NewRecordFragment();
    }

    public static NewRecordFragment newInstance(String date, String startTime, String endTime) {
        NewRecordFragment fragment = new NewRecordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE, date);
        args.putString(ARG_START_TIME, startTime);
        args.putString(ARG_END_TIME, endTime);
        fragment.setArguments(args);
        return fragment;
    }

    public NewRecordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
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
            mListener = (NewFragmentInteractionListener) activity;
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
        stateUpdated();

        dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.action_new_record)
                .setView(view)
                .setPositiveButton(R.string.dialog_new_finish, null)
                .setNegativeButton(R.string.dialog_new_abort, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
                        editor.putBoolean(Worktime.PENDING_RECORD, false);
                        editor.commit();
                    }
                })
                .setCancelable(false)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog) dialog)
                        .getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                validateAndCreate();
                            }
                        });
            }
        });


        return dialog;
    }

    private void validateAndCreate() {

        if (date == null || startTime == null || endTime == null) {
            Toast.makeText(getActivity(), R.string.record_missing_fields_message, Toast.LENGTH_SHORT).show();
            return;
        }

        if(endTime.isBefore(startTime)) {
            Toast.makeText(getActivity(), R.string.record_end_before_start, Toast.LENGTH_SHORT).show();
            return;
        }

        mListener.createNewRecord(date, startTime, endTime);
        System.out.println("OK");

        dialog.dismiss();
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
                stateUpdated();
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
                                stateUpdated();
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
                stateUpdated();
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
                                     stateUpdated();
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
                stateUpdated();
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
                                     stateUpdated();
                                 }
                             },
                        endTime.getHour(),
                        endTime.getMinute(),
                        true).show(getFragmentManager(), "startTimePicker");
    }

    public interface NewFragmentInteractionListener {
        void createNewRecord(LocalDate date, LocalTime startTime, LocalTime endTime);
    }

    private void stateUpdated() {
        SharedPreferences.Editor editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
        editor.putBoolean(Worktime.PENDING_RECORD, true);

        if (date == null) {
            datePreview.setText("--. --. ----");
        } else {
            editor.putString(Worktime.PENDING_DATE, date.toString());
            datePreview.setText(date.format(DateUtil.DATE_FORMAT));
        }

        if (startTime == null) {
            startTimePreview.setText("--:--");
        } else {
            editor.putString(Worktime.PENDING_START_TIME, startTime.toString());
            startTimePreview.setText(startTime.format(DateUtil.TIME_FORMAT));
        }

        if (endTime == null) {
            endTimePreview.setText("--:--");
        } else {
            editor.putString(Worktime.PENDING_END_TIME, endTime.toString());
            endTimePreview.setText(endTime.format(DateUtil.TIME_FORMAT));
        }

        editor.commit();
    }
}
