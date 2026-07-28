package com.lge.launcher3.sortappsby;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.lge.launcher3.R;
import com.lge.launcher3.sortappsby.SortAppsByConst;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;
import com.lge.launcher3.util.dialog.LoadingProgressDialogAsyncTask;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class SortAppsByDialog {
    private static final String TAG = "SortAppsByDialog";
    private static String sCancleAction = null;
    private static int sCheckedItem = -1;
    private static Context sLauncherContext;
    private static String sSortAction;
    private static String sSortAppsByTitle;

    private static void setLauncherContext(Context context) {
        try {
            sLauncherContext = context.createPackageContext("com.lge.launcher3", 0);
        } catch (PackageManager.NameNotFoundException e) {
            LGLog.i(TAG, "SortAppsByDialog.setLauncherContext :" + e);
            e.printStackTrace();
        }
    }

    private static void getLauncherResource() {
        sSortAppsByTitle = sLauncherContext.getResources().getString(R.string.sortappsby_title);
        sCancleAction = sLauncherContext.getResources().getString(R.string.cancel_action);
        sSortAction = sLauncherContext.getResources().getString(R.string.sort_action);
    }

    public static void showSelectSortTypeDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        setLauncherContext(context);
        getLauncherResource();
        builder.setTitle(sSortAppsByTitle);
        builder.setAdapter(getArrayAdapter(context, 33751058), new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.sortappsby.SortAppsByDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                SortAppsByDialog.showApplyDialog(context, SortAppsByConst.SortType.values()[which], null);
            }
        });
        builder.setNegativeButton(sCancleAction, (DialogInterface.OnClickListener) null);
        builder.create().show();
    }

    public static void showSelectSortTypeDialog(final Context context, int checkedItem, final DialogInterface.OnClickListener sortButtonListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        setLauncherContext(context);
        getLauncherResource();
        builder.setTitle(sSortAppsByTitle);
        builder.setSingleChoiceItems(getArrayAdapter(context, 33751059), checkedItem, new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.sortappsby.SortAppsByDialog.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                SortAppsByDialog.sCheckedItem = which;
                dialog.dismiss();
                SortAppsByDialog.showApplyDialog(context, SortAppsByConst.SortType.values()[which], sortButtonListener);
            }
        });
        builder.setNegativeButton(sCancleAction, (DialogInterface.OnClickListener) null);
        builder.create().show();
    }

    private static ArrayAdapter<String> getArrayAdapter(Context context, int layoutResourceId) {
        ArrayList arrayList = new ArrayList();
        for (SortAppsByConst.SortType sortType : SortAppsByConst.SortType.values()) {
            arrayList.add(sLauncherContext.getString(sortType.getDialogTitle()));
        }
        return new ArrayAdapter<>(context, layoutResourceId, android.R.id.text1, arrayList);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void showApplyDialog(final Context context, final SortAppsByConst.SortType sortType, final DialogInterface.OnClickListener sortButtonListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle((CharSequence) null);
        builder.setMessage(sortType.getDialogDesc());
        builder.setPositiveButton(sSortAction, new DialogInterface.OnClickListener() { // from class: com.lge.launcher3.sortappsby.SortAppsByDialog.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                if (SortAppsByManager.rearrange(context, sortType, true)) {
                    new LoadingProgressDialogAsyncTask(context, new DialogInterface.OnDismissListener() { // from class: com.lge.launcher3.sortappsby.SortAppsByDialog.3.1
                        @Override // android.content.DialogInterface.OnDismissListener
                        public void onDismiss(DialogInterface dialog2) {
                            if (Utilities.isLGUI10_0()) {
                                return;
                            }
                            Toast.makeText(context, sortType.getToastDesc(), 0).show();
                        }
                    }).show(1000);
                    DialogInterface.OnClickListener onClickListener = sortButtonListener;
                    if (onClickListener != null) {
                        onClickListener.onClick(dialog, SortAppsByDialog.sCheckedItem);
                    }
                }
            }
        });
        builder.setNegativeButton(sCancleAction, (DialogInterface.OnClickListener) null);
        builder.create().show();
    }
}
