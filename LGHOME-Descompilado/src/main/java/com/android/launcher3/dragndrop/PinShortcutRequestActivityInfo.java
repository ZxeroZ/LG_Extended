package com.android.launcher3.dragndrop;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.PinItemRequestCompat;
import com.android.launcher3.compat.ShortcutConfigActivityInfo;
import com.android.launcher3.icons.IconCache;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class PinShortcutRequestActivityInfo extends ShortcutConfigActivityInfo {
    private static final String DUMMY_COMPONENT_CLASS = "pinned-shortcut";
    private final Context mContext;
    private final ShortcutInfo mInfo;
    private final PinItemRequestCompat mRequest;

    @Override // com.android.launcher3.compat.ShortcutConfigActivityInfo
    public int getItemType() {
        return 6;
    }

    @Override // com.android.launcher3.compat.ShortcutConfigActivityInfo
    public boolean isPersistable() {
        return false;
    }

    @Override // com.android.launcher3.compat.ShortcutConfigActivityInfo
    public boolean startConfigActivity(Activity activity, int requestCode) {
        return false;
    }

    public PinShortcutRequestActivityInfo(PinItemRequestCompat request, Context context) {
        super(new ComponentName(request.getShortcutInfo().getPackage(), DUMMY_COMPONENT_CLASS), request.getShortcutInfo().getUserHandle());
        this.mRequest = request;
        this.mInfo = request.getShortcutInfo();
        this.mContext = context;
    }

    @Override // com.android.launcher3.compat.ShortcutConfigActivityInfo
    public CharSequence getLabel() {
        return this.mInfo.getShortLabel();
    }

    @Override // com.android.launcher3.compat.ShortcutConfigActivityInfo
    public Drawable getFullResIcon(IconCache cache) {
        return ((LauncherApps) this.mContext.getSystemService(LauncherApps.class)).getShortcutIconDrawable(this.mInfo, LauncherAppState.getIDP(this.mContext).fillResIconDpi);
    }

    @Override // com.android.launcher3.compat.ShortcutConfigActivityInfo
    public com.android.launcher3.ShortcutInfo createShortcutInfo() {
        return LauncherAppsCompat.createShortcutInfoFromPinItemRequest(this.mContext, this.mRequest, this.mContext.getResources().getInteger(R.integer.config_dropAnimMaxDuration) + 300 + (this.mContext.getResources().getInteger(R.integer.config_overlayTransitionTime) / 2));
    }
}
