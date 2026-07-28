package com.android.launcher3.allapps;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsSearchEditView extends EditText {
    private OnBackKeyListener mBackKeyListener;

    public interface OnBackKeyListener {
        void onBackKey();
    }

    public AllAppsSearchEditView(Context context) {
        this(context, null);
    }

    public AllAppsSearchEditView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AllAppsSearchEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnBackKeyListener(OnBackKeyListener listener) {
        this.mBackKeyListener = listener;
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == 4 && event.getAction() == 1) {
            OnBackKeyListener onBackKeyListener = this.mBackKeyListener;
            if (onBackKeyListener == null) {
                return false;
            }
            onBackKeyListener.onBackKey();
            return false;
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
