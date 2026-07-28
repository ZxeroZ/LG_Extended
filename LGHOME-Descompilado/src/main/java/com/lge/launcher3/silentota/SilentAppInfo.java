package com.lge.launcher3.silentota;

import android.content.ComponentName;
import com.android.launcher3.model.data.FolderInfo;

/* JADX INFO: loaded from: classes.dex */
public class SilentAppInfo {
    private String className;
    private ComponentName componentName;
    private FolderInfo folderInfo;
    private String folderTitle;
    private String packageName;
    private int screen;
    private int x;
    private int y;

    public SilentAppInfo(String className, String packageName, String folderTitle) {
        this.screen = -1;
        this.x = -1;
        this.y = -1;
        this.packageName = packageName;
        this.className = className;
        if (packageName != null && className != null) {
            this.componentName = new ComponentName(packageName, className);
        }
        this.folderTitle = folderTitle;
    }

    public SilentAppInfo(String className, String packageName, String screen, String x, String y) {
        this.screen = -1;
        this.x = -1;
        this.y = -1;
        this.packageName = packageName;
        this.className = className;
        this.screen = screen != null ? Integer.valueOf(screen).intValue() : -1;
        this.x = x != null ? Integer.valueOf(x).intValue() : -1;
        this.y = y != null ? Integer.valueOf(y).intValue() : -1;
        if (packageName != null && className != null) {
            this.componentName = new ComponentName(packageName, className);
        }
        this.folderTitle = null;
    }

    public ComponentName getComponentName() {
        return this.componentName;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getClassName() {
        return this.className;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getScreen() {
        return this.screen;
    }

    public void setFolderInfo(FolderInfo fi) {
        this.folderInfo = fi;
    }

    public FolderInfo getFolderInfo() {
        return this.folderInfo;
    }

    public String getFolderTitle() {
        return this.folderTitle;
    }

    public String toString() {
        return this.componentName.toString() + ", (" + this.screen + ", " + this.x + ", " + this.y + "), title = " + this.folderTitle + ", fi = " + this.folderInfo;
    }
}
