package com.lge.launcher3.allapps;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsModeDialog {
    private final Activity mActivity;
    private final IModeDialog mCallback;

    public interface IModeDialog {
        void changeModeType(int modeType);

        int getSortMode();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cleanup() {
    }

    public Dialog createDialog() {
        return null;
    }

    public AllAppsModeDialog(Activity activity, IModeDialog callback) {
        this.mActivity = activity;
        this.mCallback = callback;
    }

    private final class NegativeClickListener implements DialogInterface.OnClickListener {
        private NegativeClickListener() {
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            AllAppsModeDialog.this.cleanup();
        }
    }

    private final class PositiveClickListener implements DialogInterface.OnClickListener {
        private final int[] mModeSequence;

        private PositiveClickListener(int[] mModeSequence) {
            this.mModeSequence = mModeSequence;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialog, int which) {
            AllAppsModeDialog.this.mCallback.changeModeType(this.mModeSequence[which]);
            AllAppsModeDialog.this.cleanup();
        }
    }
}
