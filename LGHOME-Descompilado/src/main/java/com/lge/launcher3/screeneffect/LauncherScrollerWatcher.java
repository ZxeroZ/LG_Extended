package com.lge.launcher3.screeneffect;

import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class LauncherScrollerWatcher {
    public static final String TAG = "LauncherScrollerWatcher";
    private static LauncherScrollerWatcher sInstance;
    private ArrayList<ScrollerListener> mListeners = null;

    public enum ScrollerFinishType {
        TIME_EXPIRATION,
        FORCE_FINISHED,
        ABORT_ANIMATION
    }

    public interface ScrollerListener {
        void onScrollerFinish(int currX, int currY, ScrollerFinishType finishType);

        void onScrollerStart(int startX, int startY);
    }

    public static LauncherScrollerWatcher getInstance() {
        if (sInstance == null) {
            sInstance = new LauncherScrollerWatcher();
        }
        return sInstance;
    }

    private LauncherScrollerWatcher() {
    }

    public boolean addListener(ScrollerListener listener) {
        if (listener == null) {
            return false;
        }
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<>();
        }
        if (this.mListeners.contains(listener)) {
            return false;
        }
        this.mListeners.add(listener);
        return true;
    }

    public boolean removeListener(ScrollerListener listener) {
        ArrayList<ScrollerListener> arrayList;
        if (listener == null || (arrayList = this.mListeners) == null || !arrayList.contains(listener)) {
            return false;
        }
        this.mListeners.remove(listener);
        if (this.mListeners.size() > 0) {
            return true;
        }
        this.mListeners = null;
        return true;
    }

    public void removeAllListeners() {
        this.mListeners.clear();
    }

    public void notifyStartListeners(int startX, int startY) {
        ArrayList<ScrollerListener> arrayList = this.mListeners;
        if (arrayList == null) {
            return;
        }
        Iterator<ScrollerListener> it = arrayList.iterator();
        while (it.hasNext()) {
            it.next().onScrollerStart(startX, startY);
        }
    }

    public void notifyFinishListeners(int currX, int currY, ScrollerFinishType finishType) {
        ArrayList<ScrollerListener> arrayList = this.mListeners;
        if (arrayList == null) {
            return;
        }
        Iterator<ScrollerListener> it = arrayList.iterator();
        while (it.hasNext()) {
            it.next().onScrollerFinish(currX, currY, finishType);
        }
    }

    public void destroy() {
        ArrayList<ScrollerListener> arrayList = this.mListeners;
        if (arrayList != null) {
            arrayList.clear();
            this.mListeners = null;
        }
    }
}
