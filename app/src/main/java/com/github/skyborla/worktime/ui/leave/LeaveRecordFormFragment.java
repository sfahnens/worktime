package com.github.skyborla.worktime.ui.leave;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.github.skyborla.worktime.R;
import com.github.skyborla.worktime.ui.control.DateControl;
import com.github.skyborla.worktime.ui.control.FormUpdateListener;

import org.threeten.bp.LocalDate;

public class LeaveRecordFormFragment extends DialogFragment implements FormUpdateListener {

    private static final String ARG_BASE_ID = "base_id";
    private static final String ARG_START_DATE = "start_date";
    private static final String ARG_END_DATE = "end_date";
    private static final String ARG_REASON = "reason";
    private static final String ARG_WORKDAYS = "workdays";

    private LeaveRecordFragmentInteractionListener mListener;

    protected long baseId;
    protected DateControl startDate;
    protected DateControl endDate;
    protected int reason;
    protected boolean workdays;

    private View view;
    private AlertDialog dialog;

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
            reason = R.id.leave_reason_vacation;
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

    }

    @Override
    public void onFormUpdated() {
    }


    public interface LeaveRecordFragmentInteractionListener {
    }

}
