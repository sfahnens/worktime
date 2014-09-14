package com.github.skyborla.worktime.ui;

import android.app.Activity;
import android.app.Fragment;
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
import com.github.skyborla.worktime.model.Record;
import com.github.skyborla.worktime.model.RecordDataSource;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.WeekFields;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.github.skyborla.worktime.ui.RecordsFragment.RecordsFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecordsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordsFragment extends Fragment {
    private static final String ARG_MONTH = "month";
    private String month;

    private ListView recordsList;
    private TextView summary;

    private RecordDataSource dataSource;

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

        dataSource = new RecordDataSource(getActivity());

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

        Set<LocalDate> workedDays = new HashSet<LocalDate>();
        Duration totalWorktime = Duration.ZERO;

        int lastWeek = -1;
        List<Object> listElements = new ArrayList<Object>();

        for (Record record : dataSource.getRecords(month)) {

            // build list
            int thisWeek = record.getDate().get(WeekFields.ISO.weekOfYear());

            if (thisWeek != lastWeek) {
                WeekHeader header = new WeekHeader();
                header.week = thisWeek;

                listElements.add(header);
                lastWeek = thisWeek;
            }

            listElements.add(record);

            // build summary
            workedDays.add(record.getDate());
            Duration worktime = Duration.between(record.getStartTime(), record.getEndTime());

            totalWorktime = totalWorktime.plus(worktime);
        }

        adapter = new RecordsAdapter(getActivity(), listElements);
        recordsList.setAdapter(adapter);


        long seconds = totalWorktime.getSeconds();
        if (seconds == 0) {
            summary.setText("Diesen Monat nicht gearbeitet.");

        } else {

            int h = (int) seconds / 3600;
            int m = (int) (seconds / 60) % 60;
            int d = workedDays.size();

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

        if (!(item instanceof Record)) {
            return;
        }
        Record record = (Record) item;

        String header = FormatUtil.DATE_FORMAT_SHORT.format(record.getDate());
        header += " (" + FormatUtil.formatTimes(record) + ")";

        menu.setHeaderTitle(header);
        menu.setHeaderIcon(R.drawable.ic_launcher);

        getActivity().getMenuInflater().inflate(R.menu.records_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adapterInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Record record = (Record) adapter.getItem(adapterInfo.position);

        switch (item.getItemId()) {
            case R.id.records_context_edit:
                mListener.requestEdit(record);
                break;
            case R.id.records_context_delete:
                mListener.requestDelete(record);
                break;
        }

        return true;
    }

    public interface RecordsFragmentInteractionListener {
        void requestEdit(Record record);

        void requestDelete(Record record);
    }

    public static class RecordHolder {
        public TextView dateText;
        public TextView durationText;
        public TextView timeText;
    }

    public static class WeekHeader {
        public int week;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            WeekHeader that = (WeekHeader) o;
            if (week != that.week) return false;
            return true;
        }

        @Override
        public int hashCode() {
            return week;
        }
    }

}
