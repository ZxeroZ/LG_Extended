package com.android.launcher3.util;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/* JADX INFO: loaded from: classes.dex */
public class ViewCache {
    protected final SparseArray<CacheEntry> mCache = new SparseArray<>();

    public void setCacheSize(int layoutId, int size) {
        this.mCache.put(layoutId, new CacheEntry(size));
    }

    public <T extends View> T getView(int i, Context context, ViewGroup viewGroup) {
        CacheEntry cacheEntry = this.mCache.get(i);
        if (cacheEntry == null) {
            cacheEntry = new CacheEntry(1);
            this.mCache.put(i, cacheEntry);
        }
        if (cacheEntry.mCurrentSize > 0) {
            cacheEntry.mCurrentSize--;
            T t = (T) cacheEntry.mViews[cacheEntry.mCurrentSize];
            cacheEntry.mViews[cacheEntry.mCurrentSize] = null;
            return t;
        }
        return (T) LayoutInflater.from(context).inflate(i, viewGroup, false);
    }

    public void recycleView(int layoutId, View view) {
        CacheEntry cacheEntry = this.mCache.get(layoutId);
        if (cacheEntry == null || cacheEntry.mCurrentSize >= cacheEntry.mMaxSize) {
            return;
        }
        cacheEntry.mViews[cacheEntry.mCurrentSize] = view;
        cacheEntry.mCurrentSize++;
    }

    private static class CacheEntry {
        int mCurrentSize = 0;
        final int mMaxSize;
        final View[] mViews;

        public CacheEntry(int maxSize) {
            this.mMaxSize = maxSize;
            this.mViews = new View[maxSize];
        }
    }
}
