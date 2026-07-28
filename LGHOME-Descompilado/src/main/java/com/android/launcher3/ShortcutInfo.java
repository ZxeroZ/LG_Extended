package com.android.launcher3;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.util.ContentWriter;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.liveicon.LiveIconManager;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.IntentUtils;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.PackageUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class ShortcutInfo extends ItemInfoWithIcon {
    public static final int DEFAULT = 0;
    public static final int FLAG_AUTOINSTALL_ICON = 2;
    public static final int FLAG_INSTALL_SESSION_ACTIVE = 4;

    @Deprecated
    public static final int FLAG_RESTORED_APP_TYPE = 240;
    public static final int FLAG_RESTORED_ICON = 1;
    public static final int FLAG_RESTORE_STARTED = 8;
    public static final int FLAG_SUPPORTS_WEB_UI = 16;
    public static final int FLAG_UPDATECENTER_ICON = 256;
    public static final int OPTIONS_NO_ICON_FRAMES = 1;
    private Bitmap cushionIcon;
    public boolean customIcon;
    public CharSequence disabledMessage;
    public long firstInstallTime;
    public int flags;
    private String iconId;
    public Intent.ShortcutIconResource iconResource;
    public Intent intent;
    private int mInstallProgress;
    public int options;
    Intent promisedIntent;
    public int status;
    private Bitmap userCustomizedIcon;
    public boolean usingFallbackIcon;

    public ShortcutInfo() {
        this.flags = 0;
        this.options = 0;
        this.cushionIcon = null;
        this.itemType = 1;
    }

    public Intent getPromisedIntent() {
        Intent intent = this.promisedIntent;
        return intent != null ? intent : this.intent;
    }

    ShortcutInfo(Intent intent, CharSequence title, CharSequence contentDescription, Bitmap icon, UserHandle user) {
        this();
        this.intent = intent;
        this.title = Utilities.trim(title);
        this.contentDescription = contentDescription;
        this.iconBitmap = icon;
        this.user = user;
    }

    public ShortcutInfo(Context context, ShortcutInfo info) {
        super(info);
        this.flags = 0;
        this.options = 0;
        this.cushionIcon = null;
        this.title = Utilities.trim(info.title);
        this.intent = new Intent(info.intent);
        if (info.iconResource != null) {
            Intent.ShortcutIconResource shortcutIconResource = new Intent.ShortcutIconResource();
            this.iconResource = shortcutIconResource;
            shortcutIconResource.packageName = info.iconResource.packageName;
            this.iconResource.resourceName = info.iconResource.resourceName;
        }
        this.customIcon = info.customIcon;
        this.flags = info.flags;
        this.firstInstallTime = info.firstInstallTime;
        this.user = info.user;
        this.status = info.status;
    }

    public ShortcutInfo(ShortcutInfo info) {
        super(info);
        this.flags = 0;
        this.options = 0;
        this.cushionIcon = null;
        this.title = info.title;
        this.intent = new Intent(info.intent);
        this.iconResource = info.iconResource;
        this.flags = info.flags;
        this.status = info.status;
        this.mInstallProgress = info.mInstallProgress;
        this.usingFallbackIcon = info.usingFallbackIcon;
    }

    public ShortcutInfo(AppInfo info) {
        super(info);
        this.flags = 0;
        this.options = 0;
        this.cushionIcon = null;
        this.title = Utilities.trim(info.title);
        this.intent = new Intent(info.intent);
        this.customIcon = false;
        this.flags = info.flags;
        this.firstInstallTime = info.firstInstallTime;
    }

    public ShortcutInfo(ShortcutInfoCompat shortcutInfo, Context context) {
        this.flags = 0;
        this.options = 0;
        this.cushionIcon = null;
        this.user = shortcutInfo.getUserHandle();
        this.itemType = 6;
        this.flags = 0;
        updateFromDeepShortcutInfo(shortcutInfo, context);
    }

    public void setIcon(Bitmap b) {
        this.iconBitmap = b;
        resetCushionIcon();
    }

    public Bitmap getIcon() {
        return this.iconBitmap;
    }

    public Bitmap getIcon(IconCache iconCache) {
        if (this.iconBitmap == null) {
            updateIcon(iconCache);
        }
        if (LGHomeFeature.Config.FEATURE_USE_SHORTCUT_CUSHION.getValue() && this.itemType == 1 && !this.usingFallbackIcon) {
            return getCushionIcon(iconCache.getContext());
        }
        return this.iconBitmap;
    }

    public void updateIcon(IconCache iconCache) {
        if (this.itemType == 0) {
            Intent intent = this.promisedIntent;
            if (intent == null) {
                intent = this.intent;
            }
            iconCache.getTitleAndIcon(this, intent, this.user, shouldUseLowResIcon());
        }
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public void onAddToDatabase(ContentWriter writer) {
        super.onAddToDatabase(writer);
        writer.put("title", this.title).put(LauncherSettings.BaseLauncherColumns.INTENT, getIntent()).put(LauncherSettings.Favorites.RESTORED, Integer.valueOf(this.status));
        if (!this.usingLowResIcon) {
            writer.putIcon(this.iconBitmap, this.user);
        }
        Intent.ShortcutIconResource shortcutIconResource = this.iconResource;
        if (shortcutIconResource != null) {
            writer.put(LauncherSettings.BaseLauncherColumns.ICON_PACKAGE, shortcutIconResource.packageName).put(LauncherSettings.BaseLauncherColumns.ICON_RESOURCE, this.iconResource.resourceName);
        }
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public void onAddToDatabase(Context context, ContentValues values) {
        super.onAddToDatabase(context, values);
        String uri = null;
        values.put("title", this.title != null ? this.title.toString() : null);
        Intent intent = this.promisedIntent;
        if (intent != null) {
            uri = intent.toUri(0);
        } else {
            Intent intent2 = this.intent;
            if (intent2 != null) {
                uri = intent2.toUri(0);
            }
        }
        values.put(LauncherSettings.BaseLauncherColumns.INTENT, uri);
        values.put(LauncherSettings.Favorites.RESTORED, Integer.valueOf(this.status));
        if (this.customIcon) {
            values.put(LauncherSettings.BaseLauncherColumns.ICON_TYPE, (Integer) 1);
            writeBitmap(values, this.iconBitmap);
        } else {
            if (!this.usingFallbackIcon) {
                writeBitmap(values, this.iconBitmap);
            }
            if (this.iconResource != null) {
                values.put(LauncherSettings.BaseLauncherColumns.ICON_TYPE, (Integer) 0);
                values.put(LauncherSettings.BaseLauncherColumns.ICON_PACKAGE, this.iconResource.packageName);
                values.put(LauncherSettings.BaseLauncherColumns.ICON_RESOURCE, this.iconResource.resourceName);
            }
        }
        values.put("options", Integer.valueOf(this.options));
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public String toString() {
        CharSequence charSequence = this.title;
        return "ShortcutInfo(title=" + ((Object) charSequence) + "intent=" + this.intent + "id=" + this.id + " type=" + this.itemType + " container=" + this.container + " screen=" + this.screenId + " cellX=" + this.cellX + " cellY=" + this.cellY + " spanX=" + this.spanX + " spanY=" + this.spanY + " dropPos=" + Arrays.toString(this.dropPos) + " user=" + this.user + " swivelPosition=" + this.swivelPosition + ")";
    }

    public static void dumpShortcutInfoList(String tag, String label, ArrayList<ShortcutInfo> list) {
        Log.d(tag, label + " size=" + list.size());
        for (ShortcutInfo shortcutInfo : list) {
            CharSequence charSequence = shortcutInfo.title;
            Log.d(tag, "   title=\"" + ((Object) charSequence) + " icon=" + shortcutInfo.iconBitmap + " customIcon=" + shortcutInfo.customIcon);
        }
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public ComponentName getTargetComponent() {
        Intent intent = this.promisedIntent;
        if (intent == null) {
            intent = this.intent;
        }
        return intent.getComponent();
    }

    public Set<String> getCategories() {
        return this.intent.getCategories();
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public Intent getIntent() {
        return this.intent;
    }

    public boolean hasStatusFlag(int flag) {
        return (flag & this.status) != 0;
    }

    public final boolean isPromise() {
        return hasStatusFlag(259);
    }

    public boolean hasPromiseIconUi() {
        return isPromise() && !hasStatusFlag(16);
    }

    public int getInstallProgress() {
        return this.mInstallProgress;
    }

    public void setInstallProgress(int progress) {
        this.mInstallProgress = progress;
        this.status |= 4;
    }

    public boolean shouldUseLowResIcon() {
        return this.usingLowResIcon && this.container >= 0 && this.rank >= 9;
    }

    public static ShortcutInfo fromActivityInfo(LauncherActivityInfo info, Context context) {
        ShortcutInfo shortcutInfo = new ShortcutInfo();
        shortcutInfo.user = info.getUser();
        shortcutInfo.title = Utilities.trim(info.getLabel());
        shortcutInfo.contentDescription = UserManagerCompat.getInstance(context).getBadgedLabelForUser(info.getLabel(), info.getUser());
        shortcutInfo.customIcon = false;
        shortcutInfo.intent = AppInfo.makeLaunchIntent(context, info, info.getUser());
        shortcutInfo.itemType = 0;
        shortcutInfo.flags = AppInfo.initFlags(info);
        shortcutInfo.firstInstallTime = info.getFirstInstallTime();
        return shortcutInfo;
    }

    public String getIconId() {
        return this.iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    public Bitmap getUserCustomizedIcon() {
        return this.userCustomizedIcon;
    }

    public void setUserCustomizedIcon(Bitmap userCustomizedIcon) {
        this.userCustomizedIcon = userCustomizedIcon;
    }

    public boolean hasPhotoIcon() {
        String str = this.iconId;
        if (str != null) {
            return str.contains("/");
        }
        return false;
    }

    public boolean hasLargeIcon() {
        return this.spanX > 1 || this.spanY > 1;
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public void onResizeItemInDatabase(ContentValues values) {
        values.put(LauncherSettings.BaseLauncherColumns.ICON_ID, getIconId());
        if (getUserCustomizedIcon() != null) {
            values.put(LauncherSettings.BaseLauncherColumns.USER_CUSTOMIZED_ICON, Utilities.flattenBitmap(getUserCustomizedIcon()));
        }
    }

    public void copyFrom(ShortcutInfo info) {
        this.id = info.id;
        this.cellX = info.cellX;
        this.cellY = info.cellY;
        this.spanX = info.spanX;
        this.spanY = info.spanY;
        this.minSpanX = info.minSpanX;
        this.minSpanY = info.minSpanY;
        this.rank = info.rank;
        this.screenId = info.screenId;
        this.itemType = info.itemType;
        this.container = info.container;
        this.user = info.user;
        this.contentDescription = info.contentDescription;
        this.title = Utilities.trim(info.title);
        this.intent = new Intent(info.intent);
        if (info.iconResource != null) {
            Intent.ShortcutIconResource shortcutIconResource = new Intent.ShortcutIconResource();
            this.iconResource = shortcutIconResource;
            shortcutIconResource.packageName = info.iconResource.packageName;
            this.iconResource.resourceName = info.iconResource.resourceName;
        }
        this.iconBitmap = info.iconBitmap;
        this.usingLowResIcon = info.usingLowResIcon;
        this.customIcon = info.customIcon;
        this.flags = info.flags;
        this.firstInstallTime = info.firstInstallTime;
        this.user = info.user;
        this.status = info.status;
        this.cushionIcon = info.cushionIcon;
    }

    public Bitmap getCushionIcon(Context context) {
        String targetPackage;
        Drawable drawableConvertToCushionIconNoIconFrames;
        Bitmap bitmap = this.cushionIcon;
        if (bitmap != null) {
            return bitmap;
        }
        int identifier = 0;
        Intent.ShortcutIconResource shortcutIconResource = this.iconResource;
        if (shortcutIconResource != null) {
            targetPackage = shortcutIconResource.packageName;
            try {
                identifier = context.getPackageManager().getResourcesForApplication(targetPackage).getIdentifier(this.iconResource.resourceName, LauncherConst.RESOURCE_IMAGE_TYPE, this.iconResource.packageName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            targetPackage = IntentUtils.getTargetPackage(this.intent);
        }
        if ((this.options & 1) == 0) {
            drawableConvertToCushionIconNoIconFrames = DDTUtils.convertToCushionIcon(context, this.iconBitmap, targetPackage, identifier);
        } else {
            drawableConvertToCushionIconNoIconFrames = DDTUtils.convertToCushionIconNoIconFrames(context, this.iconBitmap, targetPackage);
        }
        return Utilities.createIconBitmap(drawableConvertToCushionIconNoIconFrames, context);
    }

    public void resetCushionIcon() {
        this.cushionIcon = null;
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public void onAddFromClipData(Bundle bundle) {
        super.onAddFromClipData(bundle);
        this.intent = (Intent) bundle.getParcelable(LauncherSettings.BaseLauncherColumns.INTENT);
        this.iconBitmap = (Bitmap) bundle.getParcelable("icon");
        String string = bundle.getString(LauncherSettings.BaseLauncherColumns.ICON_PACKAGE);
        if (string != null) {
            Intent.ShortcutIconResource shortcutIconResource = new Intent.ShortcutIconResource();
            this.iconResource = shortcutIconResource;
            shortcutIconResource.packageName = string;
            this.iconResource.resourceName = bundle.getString(LauncherSettings.BaseLauncherColumns.ICON_RESOURCE);
        }
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public void onAddToClipData(Bundle bundle) {
        super.onAddToClipData(bundle);
        bundle.putParcelable(LauncherSettings.BaseLauncherColumns.INTENT, this.intent);
        bundle.putParcelable("icon", this.iconBitmap);
        Intent.ShortcutIconResource shortcutIconResource = this.iconResource;
        if (shortcutIconResource != null) {
            bundle.putString(LauncherSettings.BaseLauncherColumns.ICON_PACKAGE, shortcutIconResource.packageName);
            bundle.putString(LauncherSettings.BaseLauncherColumns.ICON_RESOURCE, this.iconResource.resourceName);
        }
    }

    public void updateFromDeepShortcutInfo(ShortcutInfoCompat shortcutInfo, Context context) {
        Bitmap bitmapCreateScaledBitmapWithoutShadow;
        this.intent = shortcutInfo.makeIntent(context);
        this.title = shortcutInfo.getShortLabel();
        CharSequence longLabel = shortcutInfo.getLongLabel();
        if (TextUtils.isEmpty(longLabel)) {
            longLabel = shortcutInfo.getShortLabel();
        }
        this.contentDescription = UserManagerCompat.getInstance(context).getBadgedLabelForUser(longLabel, this.user);
        if (shortcutInfo.isEnabled()) {
            this.runtimeStatusFlags &= -17;
        } else {
            this.runtimeStatusFlags |= 16;
        }
        this.disabledMessage = shortcutInfo.getDisabledMessage();
        LauncherAppState launcherAppState = LauncherAppState.getInstance(context);
        Drawable shortcutIconDrawable = launcherAppState.getShortcutManager().getShortcutIconDrawable(shortcutInfo, launcherAppState.getInvariantDeviceProfile().fillResIconDpi);
        IconCache iconCache = launcherAppState.getIconCache();
        if (shortcutIconDrawable == null) {
            bitmapCreateScaledBitmapWithoutShadow = iconCache.getDefaultIcon(Process.myUserHandle());
        } else {
            bitmapCreateScaledBitmapWithoutShadow = Utilities.createScaledBitmapWithoutShadow(shortcutIconDrawable, context);
        }
        setIcon(getBadgedIcon(bitmapCreateScaledBitmapWithoutShadow, shortcutInfo, iconCache, context));
    }

    protected Bitmap getBadgedIcon(Bitmap unbadgedBitmap, ShortcutInfoCompat shortcutInfo, IconCache cache, Context context) {
        Drawable defaultIconOfLiveIcon;
        Bitmap bitmapAddShadowToIcon = Utilities.addShadowToIcon(unbadgedBitmap, context);
        AppInfo appInfo = new AppInfo();
        appInfo.user = this.user;
        appInfo.componentName = shortcutInfo.getActivity();
        appInfo.intent = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN).addCategory("android.intent.category.LAUNCHER").setComponent(appInfo.componentName);
        try {
            cache.getTitleAndIcon(appInfo, false);
            if (LiveIconManager.getInstance(context).hasLiveIcon(appInfo.componentName) && (defaultIconOfLiveIcon = LiveIconManager.getInstance(context).getDefaultIconOfLiveIcon(context, appInfo.componentName)) != null) {
                appInfo.iconBitmap = Utilities.createIconBitmap(defaultIconOfLiveIcon, context);
            }
            return Utilities.badgeWithBitmap(bitmapAddShadowToIcon, appInfo.iconBitmap, context);
        } catch (NullPointerException unused) {
            return Utilities.badgeIconForUser(bitmapAddShadowToIcon, this.user, context);
        }
    }

    public String getDeepShortcutId() {
        if (this.itemType == 6) {
            return getIntent().getStringExtra("shortcut_id");
        }
        return null;
    }
}
