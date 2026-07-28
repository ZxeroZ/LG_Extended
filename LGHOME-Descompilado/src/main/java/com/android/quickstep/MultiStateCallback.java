package com.android.quickstep;

import android.os.Looper;
import android.util.SparseArray;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.Executors;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringJoiner;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class MultiStateCallback {
    public static final boolean DEBUG_STATES = false;
    private static final String TAG = "MultiStateCallback";
    private final SparseArray<LinkedList<Runnable>> mCallbacks = new SparseArray<>();
    private final SparseArray<ArrayList<Consumer<Boolean>>> mStateChangeListeners = new SparseArray<>();
    private int mState = 0;
    private final String[] mStateNames = null;

    public MultiStateCallback(String[] stateNames) {
    }

    public void setStateOnUiThread(final int stateFlag) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            lambda$setStateOnUiThread$0$MultiStateCallback(stateFlag);
        } else {
            Utilities.postAsyncCallback(Executors.MAIN_EXECUTOR.getHandler(), new Runnable() { // from class: com.android.quickstep.-$$Lambda$MultiStateCallback$M3R_dakLrO_fjD4VIKEgPKCvazI
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setStateOnUiThread$0$MultiStateCallback(stateFlag);
                }
            });
        }
    }

    /* JADX DEBUG: Method merged with bridge method: lambda$setStateOnUiThread$0$MultiStateCallback(I)V */
    /* JADX INFO: renamed from: setState, reason: merged with bridge method [inline-methods] */
    public void lambda$setStateOnUiThread$0$MultiStateCallback(int stateFlag) {
        int i = this.mState;
        this.mState = stateFlag | i;
        int size = this.mCallbacks.size();
        for (int i2 = 0; i2 < size; i2++) {
            int iKeyAt = this.mCallbacks.keyAt(i2);
            if ((this.mState & iKeyAt) == iKeyAt) {
                LinkedList<Runnable> linkedListValueAt = this.mCallbacks.valueAt(i2);
                while (!linkedListValueAt.isEmpty()) {
                    linkedListValueAt.pollFirst().run();
                }
            }
        }
        notifyStateChangeListeners(i);
    }

    public void clearState(int stateFlag) {
        int i = this.mState;
        this.mState = (~stateFlag) & i;
        notifyStateChangeListeners(i);
    }

    private void notifyStateChangeListeners(int oldState) {
        int size = this.mStateChangeListeners.size();
        for (int i = 0; i < size; i++) {
            int iKeyAt = this.mStateChangeListeners.keyAt(i);
            boolean z = (iKeyAt & oldState) == iKeyAt;
            boolean z2 = (this.mState & iKeyAt) == iKeyAt;
            if (z != z2) {
                Iterator<Consumer<Boolean>> it = this.mStateChangeListeners.valueAt(i).iterator();
                while (it.hasNext()) {
                    it.next().accept(Boolean.valueOf(z2));
                }
            }
        }
    }

    public void runOnceAtState(int stateMask, Runnable callback) {
        LinkedList<Runnable> linkedList;
        if ((this.mState & stateMask) == stateMask) {
            callback.run();
            return;
        }
        if (this.mCallbacks.indexOfKey(stateMask) >= 0) {
            linkedList = this.mCallbacks.get(stateMask);
        } else {
            LinkedList<Runnable> linkedList2 = new LinkedList<>();
            this.mCallbacks.put(stateMask, linkedList2);
            linkedList = linkedList2;
        }
        linkedList.add(callback);
    }

    public void addChangeListener(int stateMask, Consumer<Boolean> listener) {
        ArrayList<Consumer<Boolean>> arrayList;
        if (this.mStateChangeListeners.indexOfKey(stateMask) >= 0) {
            arrayList = this.mStateChangeListeners.get(stateMask);
        } else {
            ArrayList<Consumer<Boolean>> arrayList2 = new ArrayList<>();
            this.mStateChangeListeners.put(stateMask, arrayList2);
            arrayList = arrayList2;
        }
        arrayList.add(listener);
    }

    public int getState() {
        return this.mState;
    }

    public boolean hasStates(int stateMask) {
        return (this.mState & stateMask) == stateMask;
    }

    private String convertToFlagNames(int flags) {
        StringJoiner stringJoiner = new StringJoiner(", ", "[", " (" + flags + ")]");
        int i = 0;
        while (true) {
            String[] strArr = this.mStateNames;
            if (i >= strArr.length) {
                return stringJoiner.toString();
            }
            if (((1 << i) & flags) != 0) {
                stringJoiner.add(strArr[i]);
            }
            i++;
        }
    }
}
