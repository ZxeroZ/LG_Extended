package com.lge.launcher3.homesettings;

/* JADX INFO: loaded from: classes.dex */
public class SearchIndexableRaw {
    String className;
    String intentAction;
    String intentClass;
    String intentPackage;
    String key;
    String keywords;
    String screenTitle;
    String summaryOff;
    String summaryOn;
    String title;
    boolean visible;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SearchIndexable(").append("title=").append(this.title).append(" screenTitle=").append(this.screenTitle).append(" key=").append(this.key).append(" visible=").append(this.visible).append(")");
        return sb.toString();
    }
}
