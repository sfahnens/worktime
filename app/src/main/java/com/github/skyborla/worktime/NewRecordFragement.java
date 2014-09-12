package com.github.skyborla.worktime;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialPickerLayout;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

import roboguice.fragment.RoboDialogFragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewRecordFragement.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewRecordFragement#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewRecordFragement extends RoboDialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private LocalDate date = LocalDate.now();
    private LocalTime startTime = LocalTime.now();
    private LocalTime endTime = null;

    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd. MM. YYYY");
    private DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

    private EditText datePreview;
    private EditText startTimePreview;
    private EditText endTimePreview;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewRecordFragement.
     */
    // TODO: Rename and change types and number of parameters
    public static NewRecordFragement newInstance(String param1, String param2) {
        NewRecordFragement fragment = new NewRecordFragement();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public NewRecordFragement() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.action_new_entry)
                .setView(view)
                .setPositiveButton(R.string.dialog_new_finish, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.createNewRecord();
                        System.out.println("OK");
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
                                        updatePreview();
                                    }
                                },
                                date.getYear(),
                                date.getMonthValue(),
                                date.getDayOfMonth()).show(getFragmentManager(), "calendardatepicker");
            }
        });

        Button dateButton = (Button) view.findViewById(R.id.form_date_today);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = LocalDate.now();
                updatePreview();
            }


        });
    }

    private void setupStartTimeForm(View view) {
        startTimePreview = (EditText) view.findViewById(R.id.form_start_time_preview);
        startTimePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startTime == null) {
                    startTime = LocalTime.now();
                }

                RadialTimePickerDialog
                        .newInstance(new RadialTimePickerDialog.OnTimeSetListener() {
                                         @Override
                                         public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
                                             startTime = LocalTime.of(hour, minute);
                                             updatePreview();
                                         }
                                     },
                                startTime.getHour(),
                                startTime.getMinute(),
                                true).show(getFragmentManager(), "startTimePicker");
            }
        });

        Button startTimeButton = (Button) view.findViewById(R.id.form_start_time_now);
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime = LocalTime.now();
                updatePreview();
            }
        });
    }

    private void setupEndTimeForm(View view) {
        endTimePreview = (EditText) view.findViewById(R.id.form_end_time_preview);
        endTimePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (endTime == null) {
                    endTime = LocalTime.now();
                }

                RadialTimePickerDialog
                        .newInstance(new RadialTimePickerDialog.OnTimeSetListener() {
                                         @Override
                                         public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
                                             endTime = LocalTime.of(hour, minute);
                                             updatePreview();
                                         }
                                     },
                                endTime.getHour(),
                                endTime.getMinute(),
                                true).show(getFragmentManager(), "startTimePicker");
            }
        });

        Button startTimeButton = (Button) view.findViewById(R.id.form_end_time_now);
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTime = LocalTime.now();
                updatePreview();
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);

        public void createNewRecord();

    }

    private void updatePreview() {
        if (date == null) {
            datePreview.setText("--. --. ----");
        } else {
            datePreview.setText(date.format(dateFormat));
        }

        if (startTime == null) {
            startTimePreview.setText("--:--");
        } else {
            startTimePreview.setText(startTime.format(timeFormat));
        }

        if (endTime == null) {
            endTimePreview.setText("--:--");
        } else {
            endTimePreview.setText(endTime.format(timeFormat));
        }
    }

}
