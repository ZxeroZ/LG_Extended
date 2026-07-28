package com.lge.launcher3.screeneffect;

import android.content.Context;
import com.android.launcher3.Workspace;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class WorkspaceStateTransitionWatcher {
    public static final String TAG = "WorkspaceStateTransitionWatcher";
    private static WorkspaceStateTransitionWatcher sInstance;
    private Workspace.State mFromState = Workspace.State.NORMAL;
    private Workspace.State mToState = Workspace.State.NORMAL;
    private boolean mIsStateTrnsitioning = false;
    private ArrayList<StateTransitionListener> mListeners = null;

    public interface StateTransitionListener {
        void onStateTransitionEnd(Workspace.State fromState, Workspace.State toState);

        void onStateTransitionStart(Workspace.State fromState, Workspace.State toState);
    }

    public static WorkspaceStateTransitionWatcher getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new WorkspaceStateTransitionWatcher(context.getApplicationContext());
        }
        return sInstance;
    }

    private WorkspaceStateTransitionWatcher(Context context) {
    }

    public void startStateTransition(Workspace.State fromState, Workspace.State toState) {
        this.mFromState = fromState;
        this.mToState = toState;
        this.mIsStateTrnsitioning = true;
        notifyStartListeners();
    }

    public void endStateTransition() {
        this.mIsStateTrnsitioning = false;
        notifyEndListeners();
    }

    public Workspace.State getFromState() {
        return this.mFromState;
    }

    public Workspace.State getToState() {
        return this.mToState;
    }

    public boolean isStateTransitioning() {
        return this.mIsStateTrnsitioning;
    }

    public boolean addListener(StateTransitionListener listener) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<>();
        }
        if (this.mListeners.contains(listener)) {
            return false;
        }
        this.mListeners.add(listener);
        return true;
    }

    public boolean removeListener(StateTransitionListener listener) {
        ArrayList<StateTransitionListener> arrayList = this.mListeners;
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

    private void notifyStartListeners() {
        ArrayList<StateTransitionListener> arrayList = this.mListeners;
        if (arrayList == null) {
            return;
        }
        Iterator<StateTransitionListener> it = arrayList.iterator();
        while (it.hasNext()) {
            it.next().onStateTransitionStart(this.mFromState, this.mToState);
        }
    }

    private void notifyEndListeners() {
        ArrayList<StateTransitionListener> arrayList = this.mListeners;
        if (arrayList == null) {
            return;
        }
        Iterator<StateTransitionListener> it = arrayList.iterator();
        while (it.hasNext()) {
            it.next().onStateTransitionEnd(this.mFromState, this.mToState);
        }
    }

    public void destroy() {
        ArrayList<StateTransitionListener> arrayList = this.mListeners;
        if (arrayList != null) {
            arrayList.clear();
            this.mListeners = null;
        }
        this.mToState = Workspace.State.NORMAL;
        this.mFromState = Workspace.State.NORMAL;
    }
}
