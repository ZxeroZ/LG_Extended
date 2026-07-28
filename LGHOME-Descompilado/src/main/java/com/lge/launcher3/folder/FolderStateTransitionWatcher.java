package com.lge.launcher3.folder;

import com.android.launcher3.folder.Folder;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class FolderStateTransitionWatcher {
    private static FolderStateTransitionWatcher sInstance;
    private ArrayList<FolderStateChangeListener> mListeners = null;

    public enum FolderState {
        OPEN_START,
        OPEN_END,
        CLOSE_START,
        CLOSE_END
    }

    public interface FolderStateChangeListener {
        void onStateChanged(Folder folder, FolderState state);
    }

    public static FolderStateTransitionWatcher getInstance() {
        if (sInstance == null) {
            sInstance = new FolderStateTransitionWatcher();
        }
        return sInstance;
    }

    private FolderStateTransitionWatcher() {
    }

    public void setState(Folder folder, FolderState state) {
        notifyListeners(folder, state);
    }

    private void notifyListeners(Folder folder, FolderState state) {
        ArrayList<FolderStateChangeListener> arrayList = this.mListeners;
        if (arrayList == null) {
            return;
        }
        Iterator<FolderStateChangeListener> it = arrayList.iterator();
        while (it.hasNext()) {
            it.next().onStateChanged(folder, state);
        }
    }

    public boolean addListener(FolderStateChangeListener listener) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<>();
        }
        if (this.mListeners.contains(listener)) {
            return false;
        }
        this.mListeners.add(listener);
        return true;
    }

    public boolean removeListener(FolderStateChangeListener listener) {
        ArrayList<FolderStateChangeListener> arrayList = this.mListeners;
        if (arrayList == null || !arrayList.contains(listener)) {
            return false;
        }
        this.mListeners.remove(listener);
        if (this.mListeners.size() > 0) {
            return true;
        }
        this.mListeners = null;
        return true;
    }

    public void destroy() {
        ArrayList<FolderStateChangeListener> arrayList = this.mListeners;
        if (arrayList != null) {
            arrayList.clear();
            this.mListeners = null;
        }
        sInstance = null;
    }
}
