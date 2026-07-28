package com.android.launcher3.compat;

/* JADX INFO: compiled from: AlphabeticIndexCompat.java */
/* JADX INFO: loaded from: classes.dex */
class BaseAlphabeticIndex {
    private static final String BUCKETS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-";
    private static final int UNKNOWN_BUCKET_INDEX = 36;

    public void setMaxLabelCount(int count) {
    }

    protected int getBucketIndex(String s) {
        if (s.isEmpty()) {
            return UNKNOWN_BUCKET_INDEX;
        }
        int iIndexOf = BUCKETS.indexOf(s.substring(0, 1).toUpperCase());
        return iIndexOf != -1 ? iIndexOf : UNKNOWN_BUCKET_INDEX;
    }

    protected String getBucketLabel(int index) {
        return BUCKETS.substring(index, index + 1);
    }
}
