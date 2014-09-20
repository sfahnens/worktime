package com.github.skyborla.worktime.ui.list;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.skyborla.worktime.FormatUtil;
import com.github.skyborla.worktime.R;
import com.github.skyborla.worktime.model.DataSource;
import com.github.skyborla.worktime.model.LeaveRecord;
import com.github.skyborla.worktime.model.WorkRecord;

import java.sql.SQLException;
import java.util.List;

public class RecordsFragment extends Fragment {
    private static final String ARG_MONTH = "month";
    private String month;

    private ListView recordsList;
    private TextView summary;

    private DataSource dataSource;

    private RecordsFragmentInteractionListener mListener;
    private ArrayAdapter<Object> adapter;

    public static RecordsFragment newInstance(String month) {
        RecordsFragment fragment = new RecordsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MONTH, month);
        fragment.setArguments(args);
        return fragment;
    }

    public RecordsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            month = getArguments().getString(ARG_MONTH);
        }

        dataSource = new DataSource(getActivity());

        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    public String getMonth() {
        return month;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_records, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recordsList = (ListView) view.findViewById(R.id.records_list);
        summary = (TextView) view.findViewById(R.id.records_summary);

        onRecordsUpdated();
        registerForContextMenu(recordsList);
    }

    public void onRecordsUpdated() {

        List<WorkRecord> workRecords = dataSource.getWorkRecords(month);
        List<LeaveRecord> leaveRecords = dataSource.getLeaveRecords(month);
        RecordsListProcessor processor = new RecordsListProcessor(workRecords, leaveRecords);

        processor.process();

        adapter = new RecordsAdapter(getActivity(), processor.getElements());
        recordsList.setAdapter(adapter);

        if (processor.getTotalWorkedSeconds() == 0) {
            summary.setText("Diesen Monat nicht gearbeitet.");

        } else {

            int h = processor.getWorkedHours();
            int m = processor.getWorkedMinutes();
            int d = processor.getWorkedDays();

            String minutes = (m == 0) ? "" : getResources().getQuantityString(R.plurals.total_worktime_minutes, m, m) + " ";
            String hours = (h == 0) ? "" : getResources().getQuantityString(R.plurals.total_worktime_hours, h, h) + " ";

            String days = getResources().getQuantityString(R.plurals.total_worktime_days, d, d);

            summary.setText("Gesamt: " + hours + minutes + "an " + days + ".");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (RecordsFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement RecordsFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo adapterInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Object item = adapter.getItem(adapterInfo.position);

        if (!(item instanceof WorkRecord)) {
            return;
        }
        WorkRecord workRecord = (WorkRecord) item;

        String header = FormatUtil.DATE_FORMAT_SHORT.format(workRecord.getDate());
        header += " (" + FormatUtil.formatTimes(workRecord) + ")";

        menu.setHeaderTitle(header);
        menu.setHeaderIcon(R.drawable.ic_launcher);

        getActivity().getMenuInflater().inflate(R.menu.records_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adapterInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        WorkRecord workRecord;
        try {
            workRecord = (WorkRecord) adapter.getItem(adapterInfo.position);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }

        switch (item.getItemId()) {
            case R.id.records_context_edit:
                mListener.requestEdit(workRecord);
                break;
            case R.id.records_context_delete:
                mListener.requestDelete(workRecord);
                break;
        }

        return true;
    }

    public class RecordsAdapter extends ArrayAdapter<Object> {

        public RecordsAdapter(Context context, List<Object> objects) {
            super(context, 0, objects);
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            return ((ListViewRenderable) getItem(position)).getItemViewType();
        }

        @Override
        public long getItemId(int position) {
            Object item = getItem(position);
            return item.hashCode();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View row, ViewGroup parent) {
            return ((ListViewRenderable) getItem(position)).getView((Activity) getContext(), row, parent);
        }
    }

    public interface RecordsFragmentInteractionListener {
        void requestEdit(WorkRecord workRecord);

        void requestDelete(WorkRecord workRecord);
    }
}
