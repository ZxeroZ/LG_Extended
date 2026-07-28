package com.lge.launcher3.memory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class MemoryFullAlertDialogHandler extends Handler {
    private static final int SHOW_DIALOG = 0;
    public static final String TAG = "MemoryFullAlertDialogHandler";
    private static MemoryFullAlertDialogHandler sInstance;
    private AlertDialog mAlertDialog = null;
    private Context mContext;

    public static MemoryFullAlertDialogHandler getInstance() {
        if (sInstance == null) {
            sInstance = new MemoryFullAlertDialogHandler();
        }
        return sInstance;
    }

    public void show(Context context, int delay) {
        if (context == null) {
            LGLog.i(TAG, "Context is null");
        } else if (!(context instanceof Activity)) {
            LGLog.i(TAG, "Context isn't Activity");
        } else {
            this.mContext = context;
            sendEmptyMessageDelayed(0, delay);
        }
    }

    @Override // android.os.Handler
    public void handleMessage(final Message msg) {
        if (msg.what != 0) {
            return;
        }
        AlertDialog alertDialog = this.mAlertDialog;
        if (alertDialog == null || !alertDialog.isShowing()) {
            if (this.mAlertDialog == null) {
                try {
                    AlertDialog alertDialogCreateMemoryFullAlertDialog = createMemoryFullAlertDialog(this.mContext);
                    this.mAlertDialog = alertDialogCreateMemoryFullAlertDialog;
                    alertDialogCreateMemoryFullAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() { // from class: com.lge.launcher3.memory.MemoryFullAlertDialogHandler.1
                        @Override // android.content.DialogInterface.OnShowListener
                        public void onShow(DialogInterface dialog) {
                            LGLog.i(MemoryFullAlertDialogHandler.TAG, "onShow()");
                        }
                    });
                    this.mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.lge.launcher3.memory.MemoryFullAlertDialogHandler.2
                        @Override // android.content.DialogInterface.OnDismissListener
                        public void onDismiss(DialogInterface dialog) {
                            LGLog.i(MemoryFullAlertDialogHandler.TAG, "onDismiss()");
                        }
                    });
                } catch (Resources.NotFoundException e) {
                    LGLog.i(TAG, String.format("NotFoundException(%s)", e.toString()));
                    return;
                }
            }
            this.mAlertDialog.show();
        }
    }

    private AlertDialog createMemoryFullAlertDialog(Context context) throws Resources.NotFoundException {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.sp_internal_storage_full_ics_NORMAL));
        builder.setMessage(context.getString(R.string.sp_less_internal_storage_NORMAL, context.getString(R.string.sp_lowercase_internal_storage_NORMAL)));
        builder.setPositiveButton(context.getString(R.string.rename_action), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    public void dismiss() {
        AlertDialog alertDialog = this.mAlertDialog;
        if (alertDialog == null || !alertDialog.isShowing()) {
            return;
        }
        this.mAlertDialog.dismiss();
        this.mAlertDialog = null;
    }
}
