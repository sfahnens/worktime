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

import org.threeten.bp.LocalDate;

import java.sql.SQLException;
import java.util.List;

public class RecordsFragment extends Fragment {
    private static final String ARG_MONTH = "month";
    private String month;

    private ListView recordsList;
    private TextView summary;

    private DataSource dataSource;

    private RecordsFragmentInteractionListener mListener;
    private ArrayAdapter<ListViewItem> adapter;

    public static RecordsFragment newInstance(LocalDate month) {
        RecordsFragment fragment = new RecordsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MONTH, FormatUtil.DATE_FORMAT_DB_MONTH.format(month));
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
        List<LocalDate> holidays = dataSource.getHolidays(month);
        RecordsListProcessor processor = new RecordsListProcessor(workRecords, leaveRecords, holidays);
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
        System.out.println(month);
        AdapterView.AdapterContextMenuInfo adapterInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        adapter.getItem(adapterInfo.position).onCreateContextMenu(getActivity(), menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        // filter for correct target list !!
        // see http://stackoverflow.com/a/10162443
        if (!getUserVisibleHint()) {
            return false;
        }

        AdapterView.AdapterContextMenuInfo adapterInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        return adapter.getItem(adapterInfo.position).onContextItemSelected(item, mListener);
    }

    public class RecordsAdapter extends ArrayAdapter<ListViewItem> {

        public RecordsAdapter(Context context, List<ListViewItem> objects) {
            super(context, 0, objects);
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).getItemViewType();
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
            return getItem(position).getView(getActivity(), row, parent);
        }
    }

    public interface RecordsFragmentInteractionListener {
        void beginEditWorkRecord(WorkRecord workRecord);

        void beginDeleteWorkRecord(WorkRecord workRecord);

        void beginEditLeaveRecord(LeaveRecord leaveRecord);

        void beginDeleteLeaveRecord(LeaveRecord leaveRecord);
    }
}
