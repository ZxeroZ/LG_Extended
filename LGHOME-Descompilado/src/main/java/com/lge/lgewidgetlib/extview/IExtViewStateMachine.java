package com.lge.lgewidgetlib.extview;

/* JADX INFO: loaded from: classes2.dex */
public interface IExtViewStateMachine {
    ExtViewState getExpandedState();

    ExtViewState getExpandingState();

    ExtViewState getNormalState();

    ExtViewState getRestoreState();

    void setState(ExtViewState newState);
}
