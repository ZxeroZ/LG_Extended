package com.android.launcher3.compat;

import android.app.Activity;
import android.app.ActivityOptions;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.widget.Toast;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.PackageUserKey;
import com.lge.launcher3.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
class AppWidgetManagerCompatVL extends AppWidgetManagerCompat {
    private final PackageManager mPm;
    private final UserManager mUserManager;

    AppWidgetManagerCompatVL(Context context) {
        super(context);
        this.mPm = context.getPackageManager();
        this.mUserManager = (UserManager) context.getSystemService("user");
    }

    @Override // com.android.launcher3.compat.AppWidgetManagerCompat
    public List<AppWidgetProviderInfo> getAllProviders() {
        ArrayList arrayList = new ArrayList();
        try {
            Iterator<UserHandle> it = this.mUserManager.getUserProfiles().iterator();
            while (it.hasNext()) {
                arrayList.addAll(this.mAppWidgetManager.getInstalledProvidersForProfile(it.next()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    @Override // com.android.launcher3.compat.AppWidgetManagerCompat
    public List<AppWidgetProviderInfo> getAllProviders(PackageUserKey packageUser) {
        if (packageUser == null) {
            ArrayList arrayList = new ArrayList();
            Iterator<UserHandle> it = this.mUserManager.getUserProfiles().iterator();
            while (it.hasNext()) {
                arrayList.addAll(this.mAppWidgetManager.getInstalledProvidersForProfile(it.next()));
            }
            return arrayList;
        }
        ArrayList arrayList2 = new ArrayList(this.mAppWidgetManager.getInstalledProvidersForProfile(packageUser.mUser));
        Iterator it2 = arrayList2.iterator();
        while (it2.hasNext()) {
            if (!((AppWidgetProviderInfo) it2.next()).provider.getPackageName().equals(packageUser.mPackageName)) {
                it2.remove();
            }
        }
        return arrayList2;
    }

    @Override // com.android.launcher3.compat.AppWidgetManagerCompat
    public String loadLabel(LauncherAppWidgetProviderInfo info) {
        return info.getLabel(this.mPm);
    }

    @Override // com.android.launcher3.compat.AppWidgetManagerCompat
    public boolean bindAppWidgetIdIfAllowed(int appWidgetId, AppWidgetProviderInfo info, Bundle options) {
        return this.mAppWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, info.getProfile(), info.provider, options);
    }

    @Override // com.android.launcher3.compat.AppWidgetManagerCompat
    public UserHandle getUser(LauncherAppWidgetProviderInfo info) {
        if (info.isCustomWidget) {
            return Process.myUserHandle();
        }
        return info.getProfile();
    }

    @Override // com.android.launcher3.compat.AppWidgetManagerCompat
    public void startConfigActivity(AppWidgetProviderInfo info, int widgetId, Activity activity, AppWidgetHost host, int requestCode) {
        try {
            host.startAppWidgetConfigureActivityForResult(activity, widgetId, 0, requestCode, ActivityOptions.makeCustomAnimation(activity.getApplicationContext(), R.anim.fade_in_widget, R.anim.fade_out_widget).toBundle());
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(activity, R.string.activity_not_found, 0).show();
        } catch (SecurityException unused2) {
            Toast.makeText(activity, R.string.activity_not_found, 0).show();
        }
    }

    @Override // com.android.launcher3.compat.AppWidgetManagerCompat
    public Drawable loadPreview(AppWidgetProviderInfo info) {
        return info.loadPreviewImage(this.mContext, 0);
    }

    @Override // com.android.launcher3.compat.AppWidgetManagerCompat
    public Drawable loadIcon(LauncherAppWidgetProviderInfo info, IconCache cache) {
        return info.getIcon(this.mContext, cache);
    }

    @Override // com.android.launcher3.compat.AppWidgetManagerCompat
    public Bitmap getBadgeBitmap(LauncherAppWidgetProviderInfo info, Bitmap bitmap, int imageHeight) {
        if (!info.isCustomWidget && !info.getProfile().equals(Process.myUserHandle())) {
            Resources resources = this.mContext.getResources();
            int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.profile_badge_minimum_top);
            int dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.profile_badge_size);
            if (imageHeight < dimensionPixelSize2) {
                dimensionPixelSize2 -= dimensionPixelSize2 - imageHeight;
            }
            Rect rect = new Rect(0, 0, dimensionPixelSize2, dimensionPixelSize2);
            int iMax = Math.max(imageHeight - dimensionPixelSize2, dimensionPixelSize);
            if (resources.getConfiguration().getLayoutDirection() == 1) {
                rect.offset(0, iMax);
            } else {
                rect.offset(bitmap.getWidth() - dimensionPixelSize2, iMax);
            }
            Drawable userBadgedDrawableForDensity = this.mPm.getUserBadgedDrawableForDensity(new BitmapDrawable(resources, bitmap), info.getProfile(), rect, 0);
            if (userBadgedDrawableForDensity instanceof BitmapDrawable) {
                return ((BitmapDrawable) userBadgedDrawableForDensity).getBitmap();
            }
            bitmap.eraseColor(0);
            Canvas canvas = new Canvas(bitmap);
            userBadgedDrawableForDensity.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            userBadgedDrawableForDensity.draw(canvas);
            canvas.setBitmap(null);
        }
        return bitmap;
    }

    @Override // com.android.launcher3.compat.AppWidgetManagerCompat
    public LauncherAppWidgetProviderInfo findProvider(ComponentName provider, UserHandle user) {
        for (AppWidgetProviderInfo appWidgetProviderInfo : this.mAppWidgetManager.getInstalledProvidersForProfile(user)) {
            if (appWidgetProviderInfo.provider.equals(provider)) {
                return LauncherAppWidgetProviderInfo.fromProviderInfo(this.mContext, appWidgetProviderInfo);
            }
        }
        return null;
    }

    @Override // com.android.launcher3.compat.AppWidgetManagerCompat
    public HashMap<ComponentKey, AppWidgetProviderInfo> getAllProvidersMap() {
        HashMap<ComponentKey, AppWidgetProviderInfo> map = new HashMap<>();
        for (UserHandle userHandle : this.mUserManager.getUserProfiles()) {
            for (AppWidgetProviderInfo appWidgetProviderInfo : this.mAppWidgetManager.getInstalledProvidersForProfile(userHandle)) {
                map.put(new ComponentKey(appWidgetProviderInfo.provider, userHandle), appWidgetProviderInfo);
            }
        }
        return map;
    }
}
