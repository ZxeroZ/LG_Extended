package com.lge.launcher3.allapps;

/* JADX INFO: loaded from: classes.dex */
public interface IAllAppsHostListener {
    void changeEditButtonState();

    void sendTalkBackDescription(String description);

    boolean setArrangeMode(boolean setMode, boolean useAnimation);

    void setMenuHostVisibility(int visibility);

    void setSearchComplete(boolean hasResults);

    void updateTabIndicator(int activePage);
}
