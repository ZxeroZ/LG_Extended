package com.lge.launcher3.wing;

import android.content.Context;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.UserHandle;
import androidx.core.view.ViewCompat;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.graphics.ShadowGenerator;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.util.ComponentKey;
import com.lge.launcher3.R;
import com.lge.launcher3.liveicon.LiveIconManager;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.wing.carousel.util.CarouselGraphicUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class SwivelAppIconCache {
    private static final float EXTRA_INSET_PERCENTAGE = 0.4f;
    private static final String TAG = "SwivelAppIconCache";
    private static SwivelAppIconCache sInstance;
    final int mBgHeight;
    final int mBgWidth;
    Context mContext;
    final int mFgHeight;
    final int mFgWidth;
    final int mIconHeight;
    final int mIconWidth;
    final int CACHE_SIZE = 50;
    private final HashMap<UserHandle, Bitmap> mDefaultIcons = new HashMap<>();
    private final int mShadowLeftSideLength = -5;
    private final int mShadowRightSideLength = 5;
    private final int mShadowBottomLength = 5;
    private final int mShadowBlurRadius = 10;
    private final HashMap<ComponentKey, SwivelAppIconCacheEntry> mCache = new HashMap<>(50);

    public static class SwivelAppIconCacheEntry {
        public Drawable adaptiveFgDrawable;
        public Drawable bgDrawableWithLeftShadow;
        public Drawable bgDrawableWithRightShadow;
        public Drawable icon;
        public boolean isAdaptiveIcon = false;
        public boolean isMonochrome = false;
        public Drawable nonAdaptiveFgDrawable;
        public Bitmap reflection;
    }

    public static SwivelAppIconCache getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SwivelAppIconCache(context);
        }
        return sInstance;
    }

    public SwivelAppIconCache(Context context) {
        this.mContext = context;
        this.mFgWidth = context.getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_fg_width);
        this.mFgHeight = context.getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_fg_height);
        this.mBgWidth = context.getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_bg_width);
        this.mBgHeight = context.getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_bg_height);
        this.mIconWidth = context.getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_width);
        this.mIconHeight = context.getResources().getDimensionPixelSize(R.dimen.swivel_app_icon_height);
    }

    /* JADX DEBUG: Method merged with bridge method: lambda$fillCache$1$SwivelAppIconCache(Lcom/android/launcher3/ShortcutInfo;)V */
    /* JADX INFO: renamed from: get, reason: merged with bridge method [inline-methods] */
    public synchronized SwivelAppIconCacheEntry lambda$fillCache$1$SwivelAppIconCache(ShortcutInfo info) {
        ComponentKey componentKey = new ComponentKey(info.getTargetComponent(), info.user);
        if (info.itemType == 6) {
            return newCacheEntry(info);
        }
        SwivelAppIconCacheEntry swivelAppIconCacheEntryNewCacheEntry = info.isPromise() ? null : this.mCache.get(componentKey);
        if (swivelAppIconCacheEntryNewCacheEntry == null && (swivelAppIconCacheEntryNewCacheEntry = newCacheEntry(info)) != null && !LiveIconManager.getInstance(this.mContext).hasLiveIcon(info.getTargetComponent()) && !info.isPromise()) {
            this.mCache.put(componentKey, swivelAppIconCacheEntryNewCacheEntry);
        }
        return swivelAppIconCacheEntryNewCacheEntry;
    }

    public synchronized void clearCache() {
        this.mCache.clear();
    }

    public void fillCache(final ArrayList<ShortcutInfo> applist) {
        new Thread(new Runnable() { // from class: com.lge.launcher3.wing.-$$Lambda$SwivelAppIconCache$72JmttRpR11fOf4fOV8JNDnlyIY
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$fillCache$0$SwivelAppIconCache(applist);
            }
        }).start();
    }

    public /* synthetic */ void lambda$fillCache$0$SwivelAppIconCache(ArrayList arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            lambda$fillCache$1$SwivelAppIconCache((ShortcutInfo) it.next());
        }
    }

    public void fillCache(final ShortcutInfo info) {
        new Thread(new Runnable() { // from class: com.lge.launcher3.wing.-$$Lambda$SwivelAppIconCache$gUOM7PpdoqUU-ItiDGFCkNDRC_c
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$fillCache$1$SwivelAppIconCache(info);
            }
        }).start();
    }

    private SwivelAppIconCacheEntry newCacheEntry(ShortcutInfo info) {
        Drawable monochrome;
        SwivelAppIconCacheEntry swivelAppIconCacheEntry = new SwivelAppIconCacheEntry();
        LauncherAppState launcherAppState = LauncherAppState.getInstance(this.mContext);
        boolean z = true;
        if (info.isPromise()) {
            LGLog.i(TAG, "newCacheEntry with auto install package : " + ((Object) info.title));
            launcherAppState.getIconCache().getTitleAndIcon(info, info.getPromisedIntent(), info.user, info.shouldUseLowResIcon());
            LauncherModel.updateItemToDatabaseSwivel(this.mContext, info);
            swivelAppIconCacheEntry.icon = new BitmapDrawable(info.getIcon());
        } else if (info.itemType == 6) {
            if (info.getIcon() != null) {
                swivelAppIconCacheEntry.icon = new BitmapDrawable(info.getIcon());
            } else {
                ShortcutKey shortcutKeyFromItemInfo = ShortcutKey.fromItemInfo(info);
                List<ShortcutInfoCompat> listQueryForFullDetails = DeepShortcutManager.getInstance(this.mContext).queryForFullDetails(shortcutKeyFromItemInfo.componentName.getPackageName(), Arrays.asList(shortcutKeyFromItemInfo.getId()), shortcutKeyFromItemInfo.user);
                if (listQueryForFullDetails != null && !listQueryForFullDetails.isEmpty()) {
                    swivelAppIconCacheEntry.icon = new BitmapDrawable(new ShortcutInfo(listQueryForFullDetails.get(0), this.mContext).getIcon());
                }
            }
        } else {
            LauncherActivityInfo launcherActivityInfoResolveActivity = LauncherAppsCompat.getInstance(this.mContext).resolveActivity(info.getIntent(), info.user);
            if (launcherActivityInfoResolveActivity != null) {
                swivelAppIconCacheEntry.icon = launcherAppState.getIconCache().getSwivelIcon(launcherActivityInfoResolveActivity);
            }
        }
        if (swivelAppIconCacheEntry.icon == null) {
            return null;
        }
        if (HomeSettingsSharedPreferences.getThemedIconEnabled(this.mContext) && (swivelAppIconCacheEntry.icon instanceof AdaptiveIconDrawable) && (monochrome = ((AdaptiveIconDrawable) swivelAppIconCacheEntry.icon).getMonochrome()) != null) {
            swivelAppIconCacheEntry.icon = new ClippedMonoDrawable(monochrome);
            swivelAppIconCacheEntry.icon.setTint(this.mContext.getResources().getColor(R.color.themed_icon_color));
            swivelAppIconCacheEntry.isMonochrome = true;
        }
        if (swivelAppIconCacheEntry.icon instanceof AdaptiveIconDrawable) {
            swivelAppIconCacheEntry.isAdaptiveIcon = true;
            AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) swivelAppIconCacheEntry.icon;
            Drawable foreground = adaptiveIconDrawable.getForeground();
            Drawable background = adaptiveIconDrawable.getBackground();
            if (foreground == null) {
                foreground = new ColorDrawable(0);
            }
            Drawable drawable = foreground;
            if (background == null) {
                background = new ColorDrawable(0);
            }
            int i = this.mBgWidth;
            int i2 = this.mFgWidth;
            drawable.setBounds((i - i2) / 2, (this.mBgHeight - this.mFgHeight) / 2, (i + i2) / 2, ((r13 + r14) / 2) - 5);
            background.setBounds(0, 0, this.mBgWidth, this.mBgHeight);
            float scaleForBounds = ShadowGenerator.getScaleForBounds(new RectF(0.0f, 0.0f, 0.0f, 0.0f));
            Bitmap bitmapCreateIconBitmap = LauncherIcons.createIconBitmap(drawable, this.mContext, scaleForBounds, this.mFgWidth, this.mFgHeight, swivelAppIconCacheEntry.isMonochrome);
            Bitmap bitmapCreateIconBitmap2 = LauncherIcons.createIconBitmap(background, this.mContext, scaleForBounds, this.mBgWidth, this.mBgHeight, swivelAppIconCacheEntry.isMonochrome);
            swivelAppIconCacheEntry.adaptiveFgDrawable = new BitmapDrawable(this.mContext.getResources(), bitmapCreateIconBitmap);
            swivelAppIconCacheEntry.bgDrawableWithRightShadow = new BitmapDrawable(this.mContext.getResources(), CarouselGraphicUtils.addShadow(bitmapCreateIconBitmap2, this.mBgHeight, this.mBgWidth, ViewCompat.MEASURED_STATE_MASK, 10, 5.0f, 5.0f));
            swivelAppIconCacheEntry.bgDrawableWithLeftShadow = new BitmapDrawable(this.mContext.getResources(), CarouselGraphicUtils.addShadow(bitmapCreateIconBitmap2, this.mBgHeight, this.mBgWidth, ViewCompat.MEASURED_STATE_MASK, 10, -5.0f, 5.0f));
            swivelAppIconCacheEntry.icon.setBounds(0, 0, this.mBgWidth, this.mBgHeight);
            Drawable drawable2 = swivelAppIconCacheEntry.adaptiveFgDrawable;
            int i3 = this.mBgWidth;
            int i4 = this.mFgWidth;
            drawable2.setBounds((i3 - i4) / 2, (this.mBgHeight - this.mFgHeight) / 2, (i3 + i4) / 2, ((r13 + r14) / 2) - 5);
            swivelAppIconCacheEntry.bgDrawableWithRightShadow.setBounds(0, 0, this.mBgWidth, this.mBgHeight);
            swivelAppIconCacheEntry.bgDrawableWithLeftShadow.setBounds(0, 0, this.mBgWidth, this.mBgHeight);
            swivelAppIconCacheEntry.reflection = getReflection(bitmapCreateIconBitmap2, bitmapCreateIconBitmap, true);
            if (info.isPromise()) {
                swivelAppIconCacheEntry.bgDrawableWithLeftShadow.setAlpha(50);
                swivelAppIconCacheEntry.bgDrawableWithRightShadow.setAlpha(50);
                swivelAppIconCacheEntry.adaptiveFgDrawable.setAlpha(50);
            } else {
                swivelAppIconCacheEntry.bgDrawableWithLeftShadow.setAlpha(255);
                swivelAppIconCacheEntry.bgDrawableWithRightShadow.setAlpha(255);
                swivelAppIconCacheEntry.adaptiveFgDrawable.setAlpha(255);
            }
        } else if (swivelAppIconCacheEntry.icon != null && swivelAppIconCacheEntry.icon.getConstantState() != null && swivelAppIconCacheEntry.icon.getConstantState().newDrawable() != null) {
            swivelAppIconCacheEntry.nonAdaptiveFgDrawable = swivelAppIconCacheEntry.icon.getConstantState().newDrawable().mutate();
            if (!DDTUtils.isAdditionalThemeApplied(this.mContext) && !DDTUtils.isAdditionalIconThemeApplied(this.mContext)) {
                z = false;
            }
            ColorDrawable colorDrawable = new ColorDrawable(CarouselGraphicUtils.getAppIconBGColor(swivelAppIconCacheEntry.icon, z));
            float scaleForBounds2 = ShadowGenerator.getScaleForBounds(new RectF(0.0f, 0.0f, 0.0f, 0.0f));
            Bitmap bitmapCreateIconBitmap3 = LauncherIcons.createIconBitmap(swivelAppIconCacheEntry.nonAdaptiveFgDrawable, this.mContext, scaleForBounds2, this.mIconWidth, this.mIconHeight, swivelAppIconCacheEntry.isMonochrome);
            Bitmap bitmapCreateIconBitmap4 = LauncherIcons.createIconBitmap(colorDrawable, this.mContext, scaleForBounds2, this.mBgWidth, this.mBgHeight, swivelAppIconCacheEntry.isMonochrome);
            swivelAppIconCacheEntry.bgDrawableWithRightShadow = new BitmapDrawable(this.mContext.getResources(), CarouselGraphicUtils.addShadow(bitmapCreateIconBitmap4, this.mBgHeight, this.mBgWidth, ViewCompat.MEASURED_STATE_MASK, 10, 5.0f, 5.0f));
            swivelAppIconCacheEntry.bgDrawableWithLeftShadow = new BitmapDrawable(this.mContext.getResources(), CarouselGraphicUtils.addShadow(bitmapCreateIconBitmap4, this.mBgHeight, this.mBgWidth, ViewCompat.MEASURED_STATE_MASK, 10, -5.0f, 5.0f));
            Drawable drawable3 = swivelAppIconCacheEntry.nonAdaptiveFgDrawable;
            int i5 = this.mBgWidth;
            int i6 = this.mIconWidth;
            drawable3.setBounds((i5 - i6) / 2, (this.mBgHeight - this.mIconHeight) / 2, (i5 + i6) / 2, ((r12 + r13) / 2) - 5);
            swivelAppIconCacheEntry.reflection = getReflection(bitmapCreateIconBitmap4, bitmapCreateIconBitmap3, false);
            swivelAppIconCacheEntry.bgDrawableWithRightShadow.setBounds(0, 0, this.mBgWidth, this.mBgHeight);
            swivelAppIconCacheEntry.bgDrawableWithLeftShadow.setBounds(0, 0, this.mBgWidth, this.mBgHeight);
            swivelAppIconCacheEntry.icon.setBounds(0, 0, this.mBgWidth, this.mBgHeight);
            if (info.isPromise()) {
                swivelAppIconCacheEntry.bgDrawableWithRightShadow.setAlpha(50);
                swivelAppIconCacheEntry.bgDrawableWithLeftShadow.setAlpha(50);
                swivelAppIconCacheEntry.nonAdaptiveFgDrawable.setAlpha(50);
            } else {
                swivelAppIconCacheEntry.bgDrawableWithRightShadow.setAlpha(255);
                swivelAppIconCacheEntry.bgDrawableWithLeftShadow.setAlpha(255);
                swivelAppIconCacheEntry.nonAdaptiveFgDrawable.setAlpha(255);
            }
        }
        return swivelAppIconCacheEntry;
    }

    private static class ClippedMonoDrawable extends InsetDrawable {
        private final AdaptiveIconDrawable mCrop;

        public ClippedMonoDrawable(Drawable base) {
            super(base, -0.4f);
            this.mCrop = new AdaptiveIconDrawable(new ColorDrawable(ViewCompat.MEASURED_STATE_MASK), null);
        }

        @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            this.mCrop.setBounds(getBounds());
            int iSave = canvas.save();
            canvas.clipPath(this.mCrop.getIconMask());
            super.draw(canvas);
            canvas.restoreToCount(iSave);
        }
    }

    private Drawable setIcon(ShortcutInfo info) {
        PackageInfo packageInfo;
        PackageManager packageManager = this.mContext.getPackageManager();
        try {
            packageInfo = packageManager.getPackageInfo(info.getTargetComponent().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            packageInfo = null;
        }
        return packageInfo.applicationInfo.loadIcon(packageManager);
    }

    private Bitmap getReflection(Bitmap background, Bitmap foreground, boolean isAdaptive) {
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(this.mBgWidth - 5, this.mBgHeight - 5, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        canvas.drawBitmap(background, (Rect) null, new Rect(0, 0, background.getWidth() - 5, background.getHeight() - 5), paint);
        if (isAdaptive) {
            canvas.drawBitmap(foreground, (this.mBgWidth - this.mFgWidth) / 2, (this.mBgHeight - this.mFgHeight) / 2, paint);
        } else {
            canvas.drawBitmap(foreground, (this.mBgWidth - this.mIconWidth) / 2, (this.mBgHeight - this.mIconHeight) / 2, paint);
        }
        return CarouselGraphicUtils.createReflectionBitmap(bitmapCreateBitmap);
    }

    public synchronized void remove(String packageName, UserHandle user) {
        HashSet hashSet = new HashSet();
        for (ComponentKey componentKey : this.mCache.keySet()) {
            if (componentKey.componentName.getPackageName().equals(packageName) && componentKey.user.equals(user)) {
                hashSet.add(componentKey);
            }
        }
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            this.mCache.remove((ComponentKey) it.next());
        }
    }
}
