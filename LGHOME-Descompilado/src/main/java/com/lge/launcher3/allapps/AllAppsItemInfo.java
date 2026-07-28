package com.lge.launcher3.allapps;

import android.view.View;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.model.data.AppInfo;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsItemInfo extends AppInfo {
    public boolean bScaled;
    public boolean isSdcard;
    public boolean isSearched;
    public View itemView;
    public AllAppsFolderInfo mFolderInfo;
    public String mLowerTitle;
    public float mScaleX;
    public int reserved2;
    public String reserved3;
    public String reserved4;
    public String searchBody;
    public String searchPostfix;
    public String searchPrefix;

    public AllAppsItemInfo() {
        this.bScaled = false;
        this.isSdcard = false;
        this.reserved2 = 0;
        this.isSearched = false;
        this.mScaleX = 0.0f;
        this.mLowerTitle = null;
    }

    public AllAppsItemInfo(final AppInfo applicationInfo) {
        super(applicationInfo);
        this.bScaled = false;
        this.isSdcard = false;
        this.reserved2 = 0;
        this.isSearched = false;
        this.mScaleX = 0.0f;
        this.mLowerTitle = null;
        this.itemType = 0;
        this.mFolderInfo = null;
    }

    public AllAppsItemInfo(final String folderName) {
        this.bScaled = false;
        this.isSdcard = false;
        this.reserved2 = 0;
        this.isSearched = false;
        this.mScaleX = 0.0f;
        this.mLowerTitle = null;
        this.title = folderName;
        this.itemType = 2;
        this.mFolderInfo = null;
    }

    public AllAppsItemInfo(ShortcutInfo item) {
        this.bScaled = false;
        this.isSdcard = false;
        this.reserved2 = 0;
        this.isSearched = false;
        this.mScaleX = 0.0f;
        this.mLowerTitle = null;
        this.user = item.user;
        this.componentName = item.intent.getComponent();
        this.title = item.title;
        this.itemType = 0;
        this.mFolderInfo = null;
        this.cellX = item.cellX;
        this.cellY = item.cellY;
    }

    public AllAppsItemInfo(AllAppsFolderInfo folderInfo) {
        this.bScaled = false;
        this.isSdcard = false;
        this.reserved2 = 0;
        this.isSearched = false;
        this.mScaleX = 0.0f;
        this.mLowerTitle = null;
        this.title = folderInfo.title;
        this.mFolderInfo = folderInfo;
        this.itemType = 2;
    }

    public String getComponentShortString() {
        if (this.componentName != null) {
            return this.componentName.flattenToShortString();
        }
        return null;
    }

    @Override // com.android.launcher3.model.data.AppInfo
    public ShortcutInfo makeShortcut() {
        this.id = -1L;
        return super.makeShortcut();
    }

    public AllAppsFolderInfo getFolderInfo() {
        return this.mFolderInfo;
    }
}
