package com.github.skyborla.worktime.ui.work;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.github.skyborla.worktime.FormatUtil;
import com.github.skyborla.worktime.ModelInteraction;
import com.github.skyborla.worktime.R;
import com.github.skyborla.worktime.model.WorkRecord;

import org.threeten.bp.LocalDate;

/**
 * Created by Sebastian on 21.09.2014.
 */
public class DeleteWorkRecordHelper implements DialogInterface.OnClickListener {

    private final WorkRecord workRecord;
    private final Activity activity;
    private final ModelInteraction modelInteraction;

    public DeleteWorkRecordHelper(WorkRecord workRecord, Activity activity, ModelInteraction modelInteraction) {
        this.workRecord = workRecord;
        this.activity = activity;
        this.modelInteraction = modelInteraction;
    }

    public void confirmAndDelete() {

        String message = "\n \u2022 " + FormatUtil.DATE_FORMAT_MEDIUM.format(workRecord.getDate());
        message += " (" + FormatUtil.formatTimes(workRecord) + ")\n";

        new AlertDialog.Builder(activity)
                .setTitle(R.string.dialog_confirm_delete_header)
                .setMessage(message)
                .setNegativeButton(R.string.dialog_generic_abort, null)
                .setPositiveButton(R.string.dialog_delete_confirm, this)
                .create().show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        LocalDate changed = modelInteraction.getDataSource().deleteWorkRecord(workRecord);
        modelInteraction.modelChanged(changed);

        Snackbar.make(activity.findViewById(R.id.pager), R.string.undo_delete, 10000)
                .setAction(R.string.action_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LocalDate changed = modelInteraction.getDataSource().persistWorkRecord(workRecord);
                        modelInteraction.modelChanged(changed);
                    }
                })
                .show();
    }
}
