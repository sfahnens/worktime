package com.github.skyborla.worktime.ui.leave;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.skyborla.worktime.R;
import com.github.skyborla.worktime.model.LeaveReason;
import com.github.skyborla.worktime.ui.control.DateControl;
import com.github.skyborla.worktime.ui.control.FormUpdateListener;

import org.threeten.bp.LocalDate;

import java.util.Arrays;

public class LeaveRecordFormFragment extends DialogFragment implements FormUpdateListener {

    private static final String ARG_BASE_ID = "base_id";
    private static final String ARG_START_DATE = "start_date";
    private static final String ARG_END_DATE = "end_date";
    private static final String ARG_REASON = "reason";
    private static final String ARG_WORKDAYS = "workdays";
    public static final LeaveReason DEFAULT_LEAVE_REASON = LeaveReason.VACATION;

    private LeaveRecordFragmentInteractionListener mListener;

    protected long baseId;
    protected DateControl startDate;
    protected DateControl endDate;
    protected LeaveReason reason;
    protected boolean workdays;

    private View view;
    private AlertDialog dialog;

    private Spinner reasonSpinner;
    private Switch workdaysSwitch;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LeaveRecordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LeaveRecordFormFragment newInstance(String param1, String param2) {
        LeaveRecordFormFragment fragment = new LeaveRecordFormFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BASE_ID, param1);
        args.putString(ARG_START_DATE, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public LeaveRecordFormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startDate = new DateControl(getActivity());
        endDate = new DateControl(getActivity());

        if (getArguments() != null) {
            startDate.setDate(getArguments().getString(ARG_START_DATE));
            endDate.setDate(getArguments().getString(ARG_END_DATE));

        } else {
            startDate.setDate(LocalDate.now());
            endDate.setDate(LocalDate.now());
            reason = DEFAULT_LEAVE_REASON;
            workdays = true;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (LeaveRecordFragmentInteractionListener) activity;
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
        view = inflater.inflate(R.layout.fragment_leave_record_form, null);

        setupForm();
        onFormUpdated();

        dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getTitle())
                .setView(view)
                .setPositiveButton(R.string.dialog_generic_submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.dialog_generic_abort, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();

        return dialog;
    }

    public int getTitle() {
        return R.string.action_new_leave_record;
    }

    private void setupForm() {
        startDate.setup(view, R.id.form_start_date_preview, R.id.form_start_date_today, this);
        endDate.setup(view, R.id.form_end_date_preview, R.id.form_end_date_today, this);

        setupReasonPicker();
        setupWorkdaysSwitch();
    }

    private void setupReasonPicker() {
        reasonSpinner = (Spinner) view.findViewById(R.id.form_reason_spinner);

        reasonSpinner.setAdapter(new ArrayAdapter<LeaveReason>(getActivity(), 0, LeaveReason.values()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    convertView = inflater.inflate(R.layout.leave_reason_item, parent, false);
                }

                LeaveReason reason = (LeaveReason) getItem(position);
                ((TextView) convertView).setText(reason.stringResource);

                return convertView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    convertView = inflater.inflate(R.layout.leave_reason_dropdown_item, parent, false);
                }

                LeaveReason reason = (LeaveReason) getItem(position);
                TextView textView = (TextView) convertView;
                textView.setText(reason.stringResource);

                if (position == reasonSpinner.getSelectedItemPosition()) {
                    textView.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                } else {
                    textView.setTextColor(getResources().getColor(android.R.color.primary_text_light));
                }
                return convertView;
            }
        });

        reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reason = LeaveReason.values()[position];
                onFormUpdated();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("nothing");
            }
        });

        if (reason == null) {
            reason = DEFAULT_LEAVE_REASON;
        }

        int pos = Arrays.asList(LeaveReason.values()).indexOf(reason);
        reasonSpinner.setSelection(pos);
        reasonSpinner.setPromptId(R.string.leave_reason);
    }


    private void setupWorkdaysSwitch() {
        workdaysSwitch = (Switch) view.findViewById(R.id.form_workday_switch);

        workdaysSwitch.setChecked(workdays);
        workdaysSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                workdays = isChecked;
                onFormUpdated();
            }
        });
    }

    protected boolean validate() {

        if (endDate.getDate().isBefore(startDate.getDate())) {
            Toast.makeText(getActivity(), R.string.validate_end_before_start, Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }


    @Override
    public void onFormUpdated() {
    }


    public interface LeaveRecordFragmentInteractionListener {
    }

}
