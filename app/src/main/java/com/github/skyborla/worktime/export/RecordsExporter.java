package com.github.skyborla.worktime.export;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.github.skyborla.worktime.FormatUtil;
import com.github.skyborla.worktime.model.DataSource;
import com.github.skyborla.worktime.model.LeaveRecord;
import com.github.skyborla.worktime.model.WorkRecord;

import org.jdeferred.android.DeferredAsyncTask;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import jxl.Workbook;
import jxl.write.WritableWorkbook;

/**
 * Created by Sebastian on 21.09.2014.
 */
public class RecordsExporter extends DeferredAsyncTask<Void, Void, Uri> {

    private final static String FILENAME_IDENTIFIER = "arbeitszeit-export";

    private Context context;
    private DataSource dataSource;

    public RecordsExporter(Context context, DataSource dataSource) {
        this.context = context;
        this.dataSource = dataSource;
    }

    @Override
    protected Uri doInBackgroundSafe(Void... voids) throws Exception {
        System.out.println("XXXXX");
        long start = System.currentTimeMillis();

        cleanupOldFiles();

        String timeFormat = LocalDateTime.now().format(FormatUtil.DATE_FORMAT_FILE);
        File file = new File(context.getCacheDir(), timeFormat + "_" + FILENAME_IDENTIFIER + ".xls");
        List<LocalDate> months = dataSource.getMonths();

        WritableWorkbook workbook = Workbook.createWorkbook(file);

        // TODO split in years
        MonthExporter exporter = new MonthExporter(context, workbook, "export");
        for (LocalDate month : months) {
            String formattedMonth = FormatUtil.DATE_FORMAT_DB_MONTH.format(month);

            List<WorkRecord> workRecords = dataSource.getWorkRecords(formattedMonth);
            List<LeaveRecord> leaveRecords = dataSource.getLeaveRecords(formattedMonth);
            List<LocalDate> holidays = dataSource.getHolidays(formattedMonth);
            exporter.writeMonth(month, workRecords, leaveRecords, holidays);

        }
        exporter.finalizeSheet();

        workbook.write();
        workbook.close();


        Uri uri = FileProvider.getUriForFile(context, "com.github.skyborla.worktime.records", file);

        System.out.println("EXPORT " + (System.currentTimeMillis() - start));
        return uri;
    }

    private void cleanupOldFiles() {
        try {
            String[] fileNames = context.getCacheDir().list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.contains(FILENAME_IDENTIFIER);
                }
            });

            if (fileNames == null) {
                return;
            }

            long oneHourAgo = System.currentTimeMillis() - 60 * 60 * 1000;
            for (String filename : fileNames) {
                try {
                    File f = new File(context.getCacheDir(), filename);
                    long lastModified = f.lastModified();
                    if (lastModified < oneHourAgo) {
                        f.delete();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
