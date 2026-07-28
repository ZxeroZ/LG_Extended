package com.google.android.material.dialog;

import android.R;
import android.app.Dialog;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.app.AlertDialog;

/* JADX INFO: loaded from: classes.dex */
public class InsetDialogOnTouchListener implements View.OnTouchListener {
    private final Dialog dialog;
    private final int leftInset;
    private final int topInset;

    public InsetDialogOnTouchListener(AlertDialog alertDialog, Rect rect) {
        this.dialog = alertDialog;
        this.leftInset = rect.left;
        this.topInset = rect.top;
    }

    public InsetDialogOnTouchListener(android.app.AlertDialog alertDialog, Rect rect) {
        this.dialog = alertDialog;
        this.leftInset = rect.left;
        this.topInset = rect.top;
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        View viewFindViewById = view.findViewById(R.id.content);
        int left = this.leftInset + viewFindViewById.getLeft();
        int width = viewFindViewById.getWidth() + left;
        if (new RectF(left, this.topInset + viewFindViewById.getTop(), width, viewFindViewById.getHeight() + r3).contains(motionEvent.getX(), motionEvent.getY())) {
            return false;
        }
        MotionEvent motionEventObtain = MotionEvent.obtain(motionEvent);
        motionEventObtain.setAction(4);
        view.performClick();
        if (Build.VERSION.SDK_INT >= 28) {
            return this.dialog.onTouchEvent(motionEventObtain);
        }
        this.dialog.onBackPressed();
        return true;
    }
}
