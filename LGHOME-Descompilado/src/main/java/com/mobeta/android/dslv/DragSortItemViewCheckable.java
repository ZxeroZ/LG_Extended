package com.mobeta.android.dslv;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.Checkable;

/* JADX INFO: loaded from: classes2.dex */
public class DragSortItemViewCheckable extends DragSortItemView implements Checkable {
    public DragSortItemViewCheckable(Context context) {
        super(context);
    }

    @Override // android.widget.Checkable
    public boolean isChecked() {
        KeyEvent.Callback childAt = getChildAt(0);
        if (childAt instanceof Checkable) {
            return ((Checkable) childAt).isChecked();
        }
        return false;
    }

    @Override // android.widget.Checkable
    public void setChecked(boolean z) {
        KeyEvent.Callback childAt = getChildAt(0);
        if (childAt instanceof Checkable) {
            ((Checkable) childAt).setChecked(z);
        }
    }

    @Override // android.widget.Checkable
    public void toggle() {
        KeyEvent.Callback childAt = getChildAt(0);
        if (childAt instanceof Checkable) {
            ((Checkable) childAt).toggle();
        }
    }
}
