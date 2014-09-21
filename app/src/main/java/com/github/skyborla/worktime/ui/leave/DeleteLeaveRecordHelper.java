package com.github.skyborla.worktime.ui.leave;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Parcelable;

import com.cocosw.undobar.UndoBarController;
import com.github.skyborla.worktime.FormatUtil;
import com.github.skyborla.worktime.ModelInteraction;
import com.github.skyborla.worktime.R;
import com.github.skyborla.worktime.model.LeaveRecord;
import com.github.skyborla.worktime.model.MetaLeaveRecord;

import org.threeten.bp.LocalDate;

import java.util.Set;

/**
 * Created by Sebastian on 21.09.2014.
 */
public class DeleteLeaveRecordHelper implements DialogInterface.OnClickListener, UndoBarController.UndoListener {

    private final LeaveRecord leaveRecord;
    private final MetaLeaveRecord metaLeaveRecord;

    private final Activity activity;
    private final ModelInteraction modelInteraction;

    public DeleteLeaveRecordHelper(LeaveRecord leaveRecord, Activity activity, ModelInteraction modelInteraction) {
        this.leaveRecord = leaveRecord;
        this.activity = activity;
        this.modelInteraction = modelInteraction;

        metaLeaveRecord = modelInteraction.getDataSource().getMetaLeaveRecord(leaveRecord);
    }

    public void confirmAndDelete() {
        String message = "\n \u2022 von: " + FormatUtil.DATE_FORMAT_MEDIUM.format(metaLeaveRecord.getStartDate());
        message += "\n \u2022 bis: " + FormatUtil.DATE_FORMAT_MEDIUM.format(metaLeaveRecord.getEndDate());
        message += "\n \u2022 Grund: " + activity.getString(metaLeaveRecord.getReason().stringResource);
        message += "\n";

        new AlertDialog.Builder(activity)
                .setTitle(R.string.dialog_confirm_delete_header)
                .setMessage(message)
                .setNegativeButton(R.string.dialog_generic_abort, null)
                .setPositiveButton(R.string.dialog_delete_confirm, this)
                .create().show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Set<LocalDate> dates = modelInteraction.getDataSource().deleteLeaveRecord(leaveRecord);
        modelInteraction.modelChanged(dates);

        new UndoBarController.UndoBar(activity)
                .message(R.string.undo_delete)
                .listener(this)
                .duration(10000)
                .show(true);
    }

    @Override
    public void onUndo(Parcelable parcelable) {
        Set<LocalDate> dates = modelInteraction.getDataSource().persistLeaveRecord(metaLeaveRecord);
        modelInteraction.modelChanged(dates);
    }
}
