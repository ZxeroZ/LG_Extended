package com.android.launcher3;

import android.view.View;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class CheckLongPressHelper {
    boolean mHasPerformedLongPress;
    View.OnLongClickListener mListener;
    private int mLongPressTimeout;
    private CheckForLongPress mPendingCheckForLongPress;
    View mView;

    class CheckForLongPress implements Runnable {
        CheckForLongPress() {
        }

        @Override // java.lang.Runnable
        public void run() {
            boolean zPerformLongClick;
            if (CheckLongPressHelper.this.mView.getParent() == null || !CheckLongPressHelper.this.mView.hasWindowFocus() || CheckLongPressHelper.this.mHasPerformedLongPress) {
                return;
            }
            if (CheckLongPressHelper.this.mListener != null) {
                zPerformLongClick = CheckLongPressHelper.this.mListener.onLongClick(CheckLongPressHelper.this.mView);
            } else {
                zPerformLongClick = CheckLongPressHelper.this.mView.performLongClick();
            }
            if (zPerformLongClick) {
                CheckLongPressHelper.this.mView.setPressed(false);
                CheckLongPressHelper.this.mHasPerformedLongPress = true;
            }
        }
    }

    public CheckLongPressHelper(View v) {
        this.mLongPressTimeout = 300;
        this.mView = v;
        this.mLongPressTimeout = v.getContext().getResources().getInteger(R.integer.config_appIcon_longpress_delay);
    }

    public CheckLongPressHelper(View v, View.OnLongClickListener listener) {
        this.mLongPressTimeout = 300;
        this.mView = v;
        this.mListener = listener;
        this.mLongPressTimeout = v.getContext().getResources().getInteger(R.integer.config_appIcon_longpress_delay);
    }

    public void setLongPressTimeout(int longPressTimeout) {
        this.mLongPressTimeout = longPressTimeout;
    }

    public void postCheckForLongPress() {
        this.mHasPerformedLongPress = false;
        if (this.mPendingCheckForLongPress == null) {
            this.mPendingCheckForLongPress = new CheckForLongPress();
        }
        this.mView.postDelayed(this.mPendingCheckForLongPress, this.mLongPressTimeout);
    }

    public void cancelLongPress() {
        this.mHasPerformedLongPress = false;
        CheckForLongPress checkForLongPress = this.mPendingCheckForLongPress;
        if (checkForLongPress != null) {
            this.mView.removeCallbacks(checkForLongPress);
            this.mPendingCheckForLongPress = null;
        }
    }

    public boolean hasPerformedLongPress() {
        return this.mHasPerformedLongPress;
    }
}
