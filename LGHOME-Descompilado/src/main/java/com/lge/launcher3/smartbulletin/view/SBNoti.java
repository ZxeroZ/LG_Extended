package com.lge.launcher3.smartbulletin.view;

/* JADX INFO: loaded from: classes.dex */
public class SBNoti {
    String mComponentName;
    int mID;
    String mNotiType;
    String mResUri;
    long mTime;

    public SBNoti() {
    }

    public SBNoti(long time, String notiType, String resUri, String componentName) {
        this.mTime = time;
        this.mNotiType = notiType;
        this.mResUri = resUri;
        this.mComponentName = componentName;
    }
}
