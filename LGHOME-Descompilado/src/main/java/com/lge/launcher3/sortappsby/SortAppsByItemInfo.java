package com.lge.launcher3.sortappsby;

import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.graphics.Rect;
import android.os.UserHandle;
import com.android.launcher3.Utilities;
import com.lge.launcher3.util.LauncherActivityInfoUtils;
import com.lge.launcher3.util.UserUtils;

/* JADX INFO: loaded from: classes.dex */
public class SortAppsByItemInfo {
    public static final String TAG = "SortAppsByItemInfo";
    private Rect mBound;
    int mCellX;
    int mCellY;
    int mId;
    Intent mIntent;
    int mItemType;
    String mLabel;
    LauncherActivityInfo mLauncherActivityInfo;
    int mProfileId;
    int mScreenId;
    int mScreenRank;
    int mSpanX;
    int mSpanY;
    String mTitle;
    UserHandle mUserHandle;
    int mNewScreenId = -1;
    int mNewScreenRank = -1;
    int mNewCellX = -1;
    int mNewCellY = -1;

    public SortAppsByItemInfo(Context context, int id, String title, Intent intent, int screenId, int screenRank, int cellX, int cellY, int spanX, int spanY, int itemType, int profileId) {
        this.mId = -1;
        this.mTitle = null;
        this.mIntent = null;
        this.mScreenId = -1;
        this.mScreenRank = -1;
        this.mCellX = -1;
        this.mCellY = -1;
        this.mSpanX = -1;
        this.mSpanY = -1;
        this.mItemType = -1;
        this.mProfileId = -1;
        this.mBound = null;
        this.mUserHandle = null;
        this.mLauncherActivityInfo = null;
        this.mLabel = null;
        this.mId = id;
        this.mTitle = title;
        this.mScreenId = screenId;
        this.mScreenRank = screenRank;
        this.mIntent = intent;
        this.mCellX = cellX;
        this.mCellY = cellY;
        this.mSpanX = spanX;
        this.mSpanY = spanY;
        this.mItemType = itemType;
        this.mProfileId = profileId;
        int i = this.mCellX;
        int i2 = this.mCellY;
        this.mBound = new Rect(i, i2, this.mSpanX + i, this.mSpanY + i2);
        UserHandle userHandle = UserUtils.getUserHandle(context, this.mProfileId);
        this.mUserHandle = userHandle;
        LauncherActivityInfo launcherActivityInfo = LauncherActivityInfoUtils.getLauncherActivityInfo(context, this.mIntent, userHandle);
        this.mLauncherActivityInfo = launcherActivityInfo;
        this.mLabel = Utilities.trim(getLabel(launcherActivityInfo));
    }

    private String getLabel(LauncherActivityInfo launcherActivityInfo) {
        CharSequence label;
        if (launcherActivityInfo == null || (label = launcherActivityInfo.getLabel()) == null) {
            return null;
        }
        return label.toString();
    }

    public boolean contains(int screenRank, int cellX, int cellY) {
        if (this.mScreenRank != screenRank) {
            return false;
        }
        return this.mBound.contains(cellX, cellY);
    }

    public String getName() {
        if (this.mItemType == 0) {
            String str = this.mLabel;
            return str == null ? this.mTitle : str;
        }
        String str2 = this.mTitle;
        return str2 == null ? this.mLabel : str2;
    }

    public UserHandle getUserHandle() {
        return this.mUserHandle;
    }

    public LauncherActivityInfo getLauncherActivityInfo() {
        return this.mLauncherActivityInfo;
    }

    public boolean isScreenIdChanged() {
        return this.mScreenId != this.mNewScreenId;
    }

    public boolean isCellXChanged() {
        return this.mCellX != this.mNewCellX;
    }

    public boolean isCellYChanged() {
        return this.mCellY != this.mNewCellY;
    }

    public void setNewCell(int newScreenId, int newScreenRank, int newCellX, int newCellY) {
        this.mNewScreenId = newScreenId;
        this.mNewScreenRank = newScreenRank;
        this.mNewCellX = newCellX;
        this.mNewCellY = newCellY;
    }

    public Intent getIntent() {
        return this.mIntent;
    }

    public int getItemType() {
        return this.mItemType;
    }

    public String getItemTypeToString() {
        int i = this.mItemType;
        if (i == 0) {
            return "APPLICATION";
        }
        if (i == 1) {
            return "SHORTCUT";
        }
        if (i == 2) {
            return "FOLDER";
        }
        if (i != 4) {
            return null;
        }
        return "WIDGET";
    }

    public String toString() {
        return SortAppsByItemInfo.class.getSimpleName() + "(mId=" + this.mId + ", mTitle=" + this.mTitle + ", mScreenId=" + this.mScreenId + ", mScreenRank=" + this.mScreenRank + ", mIntent=" + this.mIntent + ", mCellX=" + this.mCellX + ", mCellY=" + this.mCellY + ", mSpanX=" + this.mSpanX + ", mSpanY=" + this.mSpanY + ", mItemType=" + this.mItemType + ", mProfileId=" + this.mProfileId + ", mLabel=" + this.mLabel + ", mBound=" + this.mBound + ", mNewScreenId=" + this.mNewScreenId + ", mNewScreenRank=" + this.mNewScreenRank + ", mNewCellX=" + this.mNewCellX + ", mNewCellY=" + this.mNewCellY + ")";
    }
}
