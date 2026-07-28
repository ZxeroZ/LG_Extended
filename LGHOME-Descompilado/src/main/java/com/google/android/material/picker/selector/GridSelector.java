package com.google.android.material.picker.selector;

import android.os.Parcelable;
import android.view.View;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public interface GridSelector<S> extends Parcelable {
    void drawCell(View view, Calendar calendar);

    S getSelection();

    void select(Calendar calendar);
}
