package com.android.launcher3.icons;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Process;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.graphics.IconShape;
import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.util.Themes;
import com.lge.launcher3.util.PackageUtils;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes.dex */
public class LauncherIcons extends BaseIconFactory implements AutoCloseable {
    private static final String EXTRA_BADGEPKG = "badge_package";
    private static LauncherIcons sPool;
    private static int sPoolId;
    private static final Object sPoolSync = new Object();
    private final int mPoolId;
    private LauncherIcons next;

    public static LauncherIcons obtain(Context context) {
        return obtain(context, IconShape.getShape().enableShapeDetection());
    }

    public static LauncherIcons obtain(Context context, boolean shapeDetection) {
        synchronized (sPoolSync) {
            LauncherIcons launcherIcons = sPool;
            if (launcherIcons != null) {
                sPool = launcherIcons.next;
                launcherIcons.next = null;
                return launcherIcons;
            }
            int i = sPoolId;
            InvariantDeviceProfile idp = LauncherAppState.getIDP(context);
            return new LauncherIcons(context, idp.fillResIconDpi, idp.iconBitmapSize, i, shapeDetection);
        }
    }

    public static void clearPool() {
        synchronized (sPoolSync) {
            sPool = null;
            sPoolId++;
        }
    }

    private LauncherIcons(Context context, int fillResIconDpi, int iconBitmapSize, int poolId, boolean shapeDetection) {
        super(context, fillResIconDpi, iconBitmapSize, shapeDetection);
        this.mPoolId = poolId;
    }

    public void recycle() {
        synchronized (sPoolSync) {
            if (sPoolId != this.mPoolId) {
                return;
            }
            clear();
            this.next = sPool;
            sPool = this;
        }
    }

    @Override // com.android.launcher3.icons.BaseIconFactory, java.lang.AutoCloseable
    public void close() {
        recycle();
    }

    public BitmapInfo createShortcutIcon(ShortcutInfo shortcutInfo) {
        return createShortcutIcon(shortcutInfo, true);
    }

    public BitmapInfo createShortcutIcon(ShortcutInfo shortcutInfo, boolean badged) {
        return createShortcutIcon(shortcutInfo, badged, null);
    }

    public BitmapInfo createShortcutIcon(ShortcutInfo shortcutInfo, boolean badged, Supplier<ItemInfoWithIcon> fallbackIconProvider) {
        final Bitmap defaultIcon;
        ItemInfoWithIcon itemInfoWithIcon;
        Drawable shortcutIconDrawable = DeepShortcutManager.getInstance(this.mContext).getShortcutIconDrawable(new ShortcutInfoCompat(shortcutInfo), this.mFillResIconDpi);
        IconCache iconCache = LauncherAppState.getInstance(this.mContext).getIconCache();
        if (shortcutIconDrawable != null) {
            defaultIcon = createScaledBitmapWithoutShadow(shortcutIconDrawable, 0);
        } else {
            if (fallbackIconProvider != null && (itemInfoWithIcon = fallbackIconProvider.get()) != null && itemInfoWithIcon.iconBitmap != null) {
                return new BitmapInfo(itemInfoWithIcon.iconBitmap, itemInfoWithIcon.iconColor);
            }
            defaultIcon = iconCache.getDefaultIcon(Process.myUserHandle());
        }
        if (!badged) {
            return new BitmapInfo(defaultIcon, Themes.getColorAccent(this.mContext));
        }
        final ItemInfoWithIcon shortcutInfoBadge = getShortcutInfoBadge(shortcutInfo, iconCache);
        return new BitmapInfo(BitmapRenderer.createHardwareBitmap(this.mIconBitmapSize, this.mIconBitmapSize, new BitmapRenderer() { // from class: com.android.launcher3.icons.-$$Lambda$LauncherIcons$PdsaZ2rmvbmJR2H6qBXNOzUy7FY
            @Override // com.android.launcher3.icons.BitmapRenderer
            public final void draw(Canvas canvas) {
                this.f$0.lambda$createShortcutIcon$0$LauncherIcons(defaultIcon, shortcutInfoBadge, canvas);
            }
        }), shortcutInfoBadge.iconColor);
    }

    public /* synthetic */ void lambda$createShortcutIcon$0$LauncherIcons(Bitmap bitmap, ItemInfoWithIcon itemInfoWithIcon, Canvas canvas) {
        getShadowGenerator().recreateIcon(bitmap, canvas);
        badgeWithDrawable(canvas, new FastBitmapDrawable(itemInfoWithIcon));
    }

    public ItemInfoWithIcon getShortcutInfoBadge(ShortcutInfo shortcutInfo, IconCache cache) {
        ComponentName activity = shortcutInfo.getActivity();
        String badgePackage = getBadgePackage(shortcutInfo);
        boolean z = !badgePackage.equals(shortcutInfo.getPackage());
        if (activity != null && !z) {
            AppInfo appInfo = new AppInfo();
            appInfo.user = shortcutInfo.getUserHandle();
            appInfo.componentName = activity;
            appInfo.intent = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN).addCategory("android.intent.category.LAUNCHER").setComponent(activity);
            cache.getTitleAndIcon(appInfo, false);
            return appInfo;
        }
        PackageItemInfo packageItemInfo = new PackageItemInfo(badgePackage);
        cache.getTitleAndIconForApp(packageItemInfo, false);
        return packageItemInfo;
    }

    private String getBadgePackage(ShortcutInfo si) {
        if (this.mContext.getString(com.lge.launcher3.R.string.shortcutinfo_badgepkg_whitelist).equals(si.getPackage()) && si.getExtras() != null && si.getExtras().containsKey(EXTRA_BADGEPKG)) {
            return si.getExtras().getString(EXTRA_BADGEPKG);
        }
        return si.getPackage();
    }
}
