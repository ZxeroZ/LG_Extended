package com.android.launcher3.widget;

import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

/* JADX INFO: loaded from: classes.dex */
public class WidgetsRowViewHolder extends RecyclerView.ViewHolder {
    ViewGroup mContent;

    public WidgetsRowViewHolder(ViewGroup v) {
        super(v);
        this.mContent = v;
    }

    ViewGroup getContent() {
        return this.mContent;
    }
}
