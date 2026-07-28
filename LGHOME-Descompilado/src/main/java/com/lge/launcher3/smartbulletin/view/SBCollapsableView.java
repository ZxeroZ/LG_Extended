package com.lge.launcher3.smartbulletin.view;

import android.animation.Animator;

/* JADX INFO: loaded from: classes.dex */
public interface SBCollapsableView {
    void collapseProvider();

    void expandProvider(boolean isAnimation);

    Animator getAnimatorscrollToComponent(String providerName);
}
