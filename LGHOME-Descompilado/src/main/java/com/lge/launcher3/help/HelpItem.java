package com.lge.launcher3.help;

/* JADX INFO: compiled from: HelpItemInfo.java */
/* JADX INFO: loaded from: classes.dex */
class HelpItem {
    int mDescResId;
    int[] mDescSubResId;
    int mImageResId;
    int mTitleResId;

    public HelpItem(int titleResId, int imageResId, int descResId, int[] descSubResId) {
        this.mTitleResId = -1;
        this.mImageResId = -1;
        this.mDescResId = -1;
        this.mDescSubResId = null;
        this.mTitleResId = titleResId;
        this.mImageResId = imageResId;
        this.mDescResId = descResId;
        this.mDescSubResId = descSubResId;
    }
}
