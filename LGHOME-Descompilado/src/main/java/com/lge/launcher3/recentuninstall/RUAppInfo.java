package com.lge.launcher3.recentuninstall;

import android.graphics.drawable.Drawable;

/* JADX INFO: loaded from: classes.dex */
public class RUAppInfo {
    private Drawable icon;
    private boolean isSelected = false;
    private String packageName;
    private CharSequence title;

    public RUAppInfo(Drawable icon, CharSequence title, String packageName) {
        this.icon = icon;
        this.title = title;
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public CharSequence getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
