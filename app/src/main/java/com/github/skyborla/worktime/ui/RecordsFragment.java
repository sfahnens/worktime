package com.github.skyborla.worktime.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.skyborla.worktime.DateUtil;
import com.github.skyborla.worktime.R;
import com.github.skyborla.worktime.model.Record;
import com.github.skyborla.worktime.model.RecordDataSource;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalTime;
import org.threeten.bp.temporal.WeekFields;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


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

        System.out.println("TAG: " + getTag());
        System.out.println("ID: " + getId());


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
//
//    @Override
//    public void onPause() {
//        dataSource.close();
//        super.onPause();
//    }


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

        adapter = new RecordsAdapter(getActivity(), loadModel());

        recordsList = (ListView) view.findViewById(R.id.records_list);
        recordsList.setAdapter(adapter);
    }

    private List<Object> loadModel() {

        int lastWeek = -1;

        List<Object> elements = new ArrayList<Object>();

        for (Record record : dataSource.getRecords(month)) {
            int thisWeek = record.getDate().get(WeekFields.ISO.weekOfYear());

            if (thisWeek != lastWeek) {
                WeekHeader header = new WeekHeader();
                header.week = thisWeek;

                elements.add(header);
                lastWeek = thisWeek;
            }

            elements.add(record);
        }

        return elements;
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

    public void onRecordsUpdated() {
        adapter.clear();
        adapter.addAll(loadModel());
        adapter.notifyDataSetChanged();

        System.out.println("list updated");
    }

    public interface RecordsFragmentInteractionListener {
    }

    public class RecordsAdapter extends ArrayAdapter<Object> {

        public RecordsAdapter(Context context, List<Object> objects) {
            super(context, 0, objects);

        }

        @Override
        public View getView(int position, View row, ViewGroup parent) {
            RecordHolder holder = null;

            Object i = getItem(position);

            if (i instanceof WeekHeader) {
                return getWeekHeaderRow(row, parent, (WeekHeader) i);
            }

            if (i instanceof Record) {
                return getRecordRow(row, parent, (Record) i);
            }

            throw new IllegalArgumentException();
        }

        private View getWeekHeaderRow(View row, ViewGroup parent, WeekHeader i) {
            RecordHolder holder;
            if (row == null) {
                LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
                row = inflater.inflate(R.layout.record_list_header, parent, false);
            }

            TextView text = (TextView) row.findViewById(R.id.record_list_week);
            text.setText("KW " + Integer.toString(i.week));
            return row;
        }

        private View getRecordRow(View row, ViewGroup parent, Record record) {
            RecordHolder holder;
            if (row == null) {
                LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
                row = inflater.inflate(R.layout.record_list_item, parent, false);

                holder = new RecordHolder();
                holder.dateText = (TextView) row.findViewById(R.id.record_list_date);
                holder.durationText = (TextView) row.findViewById(R.id.record_list_duration);
                holder.timeText = (TextView) row.findViewById(R.id.record_list_time);

                row.setTag(holder);
            } else {
                holder = (RecordHolder) row.getTag();
            }

            holder.dateText.setText(DateUtil.DATE_FORMAT_SHORT.format(record.getDate()));

            Duration duration = Duration.between(record.getStartTime(), record.getEndTime());
            LocalTime hackedDuration = LocalTime.of(0, 0).plus(duration);
            holder.durationText.setText("(" + DateUtil.TIME_FORMAT.format(hackedDuration) + ")");

            String startTime = DateUtil.TIME_FORMAT.format(record.getStartTime());
            String endTime = DateUtil.TIME_FORMAT.format(record.getEndTime());
            holder.timeText.setText(startTime + " - " + endTime);

            return row;
        }
    }

    public static class RecordHolder {
        public TextView dateText;
        public TextView durationText;
        public TextView timeText;
    }

    public static class WeekHeader {
        public int week;
    }

}
