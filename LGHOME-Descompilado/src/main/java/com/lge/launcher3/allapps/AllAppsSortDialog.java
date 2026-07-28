package com.lge.launcher3.allapps;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.lge.launcher3.R;
import com.lge.launcher3.allapps.AllAppsSort;
import com.lge.launcher3.util.Utilities;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsSortDialog {
    private static AlertDialog sCurrentDialog;

    public interface IAllAppsSortDialog {
        void changeSortType(AllAppsSort.SortType sortType);
    }

    public static void showSelectSortTypeDialog(final Context context, final IAllAppsSortDialog callBack) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.allapps_sortappsby_title);
        builder.setSingleChoiceItems(getArrayAdapter(context, 33751058), -1, new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.allapps.AllAppsSortDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                AllAppsSortDialog.showApplyDialog(context, AllAppsSort.SortType.values()[which], callBack);
            }
        });
        builder.setNegativeButton(R.string.cancel_action, (DialogInterface.OnClickListener) null);
        AlertDialog alertDialogCreate = builder.create();
        sCurrentDialog = alertDialogCreate;
        alertDialogCreate.show();
    }

    private static ArrayAdapter<String> getArrayAdapter(Context context, int layoutResourceId) {
        ArrayList arrayList = new ArrayList();
        for (AllAppsSort.SortType sortType : AllAppsSort.SortType.values()) {
            arrayList.add(context.getString(sortType.getDialogTitle()));
        }
        return new ArrayAdapter<>(context, layoutResourceId, android.R.id.text1, arrayList);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void showApplyDialog(final Context context, final AllAppsSort.SortType sortType, final IAllAppsSortDialog callBack) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle((CharSequence) null);
        builder.setMessage(sortType.getDialogDesc());
        builder.setPositiveButton(R.string.sort_action, new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.allapps.AllAppsSortDialog.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                if (!AllAppsSortManager.rearrange(context, sortType, callBack) || Utilities.isLGUI10_0()) {
                    return;
                }
                Toast.makeText(context, sortType.getToastDesc(), 0).show();
            }
        });
        builder.setNegativeButton(R.string.cancel_action, (DialogInterface.OnClickListener) null);
        AlertDialog alertDialogCreate = builder.create();
        sCurrentDialog = alertDialogCreate;
        alertDialogCreate.show();
    }

    public static void closeDialog() {
        AlertDialog alertDialog = sCurrentDialog;
        if (alertDialog == null || !alertDialog.isShowing()) {
            return;
        }
        sCurrentDialog.dismiss();
        sCurrentDialog = null;
    }
}
