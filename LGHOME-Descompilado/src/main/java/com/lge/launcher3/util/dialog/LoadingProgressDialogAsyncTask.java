package com.lge.launcher3.util.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class LoadingProgressDialogAsyncTask implements DialogInterface.OnDismissListener {
    private static final String TAG = "LoadingProgressDialogAsyncTask";
    private Context mContext;
    private DialogInterface.OnDismissListener mOnDismissListener;
    private ProgressDialog mProgressDialog;
    AsyncTask<Integer, Void, Void> mProgressDialogAsyncTask;

    public LoadingProgressDialogAsyncTask(Context context) {
        this(context, null);
    }

    public LoadingProgressDialogAsyncTask(Context context, DialogInterface.OnDismissListener listener) {
        this.mOnDismissListener = null;
        this.mProgressDialogAsyncTask = new AsyncTask<Integer, Void, Void>() { // from class: com.lge.launcher3.util.dialog.LoadingProgressDialogAsyncTask.1
            @Override // android.os.AsyncTask
            protected void onPreExecute() {
                LoadingProgressDialogAsyncTask.this.mProgressDialog = new ProgressDialog(LoadingProgressDialogAsyncTask.this.mContext);
                LoadingProgressDialogAsyncTask.this.mProgressDialog.setProgressStyle(0);
                LoadingProgressDialogAsyncTask.this.mProgressDialog.setMessage(LoadingProgressDialogAsyncTask.this.mContext.getResources().getString(R.string.STR_LGHome_LOADING_NORMAL));
                LoadingProgressDialogAsyncTask.this.mProgressDialog.setCancelable(false);
                LoadingProgressDialogAsyncTask.this.mProgressDialog.setOnDismissListener(LoadingProgressDialogAsyncTask.this);
                LoadingProgressDialogAsyncTask.this.mProgressDialog.show();
            }

            /* JADX DEBUG: Method merged with bridge method: doInBackground([Ljava/lang/Object;)Ljava/lang/Object; */
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Void doInBackground(Integer... params) {
                int i = 0;
                int iIntValue = params[0].intValue();
                int i2 = iIntValue / 100;
                int i3 = iIntValue - (i2 * 100);
                while (i <= i2) {
                    int i4 = i >= i2 ? i3 : 100;
                    if (i4 > 0) {
                        try {
                            Thread.sleep(i4);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    i++;
                }
                return null;
            }

            /* JADX DEBUG: Method merged with bridge method: onPostExecute(Ljava/lang/Object;)V */
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Void result) {
                if (((LoadingProgressDialogAsyncTask.this.mContext instanceof Activity) && ((Activity) LoadingProgressDialogAsyncTask.this.mContext).isDestroyed()) || LoadingProgressDialogAsyncTask.this.mProgressDialog == null) {
                    return;
                }
                LoadingProgressDialogAsyncTask.this.mProgressDialog.dismiss();
            }
        };
        this.mContext = context;
        this.mOnDismissListener = listener;
    }

    public void show(int duration) {
        LGLog.i(TAG, String.format("Show the LoadingProgressDialogAsyncTask dialog(%d)", Integer.valueOf(duration)));
        this.mProgressDialogAsyncTask.execute(Integer.valueOf(duration));
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        LGLog.i(TAG, "Dismiss the LoadingProgressDialogAsyncTask dialog");
        DialogInterface.OnDismissListener onDismissListener = this.mOnDismissListener;
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }
}
