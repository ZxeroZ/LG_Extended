package com.lge.launcher3.pageindicator;

import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public class MarkerOnClickListener implements View.OnClickListener {
    private int index;
    public int[] mClickIndexArray;
    private PageIndicatorListener mListenr;

    public MarkerOnClickListener(int num, PageIndicatorListener listener, int[] clickIndexArray) {
        this.index = num;
        this.mListenr = listener;
        this.mClickIndexArray = clickIndexArray;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        PageIndicatorListener pageIndicatorListener = this.mListenr;
        if (pageIndicatorListener != null) {
            int i = this.index;
            int[] iArr = this.mClickIndexArray;
            if (i < iArr.length) {
                pageIndicatorListener.onChangePage(iArr[i]);
            }
        }
    }
}
