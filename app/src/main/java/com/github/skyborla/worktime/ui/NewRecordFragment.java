package com.github.skyborla.worktime.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.github.skyborla.worktime.R;
import com.github.skyborla.worktime.Worktime;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

import roboguice.fragment.RoboDialogFragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewRecordFragment.NewFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewRecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewRecordFragment extends RoboDialogFragment {

    private static final String ARG_DATE = "date";
    private static final String ARG_START_TIME = "startTime";
    private static final String ARG_END_TIME = "EndTime";

    private NewFragmentInteractionListener mListener;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd. MM. YYYY");
    private DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

    private EditText datePreview;
    private EditText startTimePreview;
    private EditText endTimePreview;

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
        View view = inflater.inflate(R.layout.fragment_record_form, null);

        setupForm(view);
        stateUpdated();

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.action_new_entry)
                .setView(view)
                .setPositiveButton(R.string.dialog_new_finish, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.createNewRecord(date, startTime, endTime);
                        System.out.println("OK");
                    }
                })
                .setNegativeButton(R.string.dialog_new_abort, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
                        editor.putBoolean(Worktime.PENDING_RECORD, false);
                        editor.commit();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        SharedPreferences.Editor editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
                        editor.putBoolean(Worktime.PENDING_RECORD, false);
                        editor.commit();
                    }
                }).create();
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
                                date = LocalDate.of(year, month, day);
                                stateUpdated();
                            }
                        },
                        date.getYear(),
                        date.getMonthValue(),
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
            datePreview.setText(date.format(dateFormat));
        }

        if (startTime == null) {
            startTimePreview.setText("--:--");
        } else {
            editor.putString(Worktime.PENDING_START_TIME, startTime.toString());
            startTimePreview.setText(startTime.format(timeFormat));
        }

        if (endTime == null) {
            endTimePreview.setText("--:--");
        } else {
            editor.putString(Worktime.PENDING_END_TIME, endTime.toString());
            endTimePreview.setText(endTime.format(timeFormat));
        }

        editor.commit();
    }
}
