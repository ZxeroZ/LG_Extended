package com.lge.contextenginelibrary.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.LauncherIcons;
import android.util.Log;
import com.lge.contextenginelibrary.LibConstant;
import com.lge.launcher3.config.LauncherConst;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class FrequentAppsInfo {
    public static final String TAG = LibConstant.TAG_PREFIX + FrequentAppsInfo.class.getSimpleName();
    public ArrayList<AppInfo> appInfos = new ArrayList<>();
    public String date;
    public FavoriteAppType favoriteAppType;
    private Context mContext;
    PackageManager mPackageManager;

    public FrequentAppsInfo(Context context, String str, FavoriteAppType favoriteAppType, ArrayList<String> arrayList, boolean z) {
        CharSequence charSequence;
        CharSequence charSequenceLoadLabel;
        Drawable drawableWrapIconDrawableWithShadow;
        Drawable drawableLoadUnbadgedFramedIcon;
        this.date = null;
        this.favoriteAppType = FavoriteAppType.BASIC;
        this.mContext = context;
        this.date = str;
        this.favoriteAppType = favoriteAppType;
        this.mPackageManager = context.getPackageManager();
        for (String str2 : arrayList) {
            try {
                if (str2.equals("com.android.contacts")) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(str2, LauncherConst.ANDROID_PHONE_CLASS_NAME));
                    charSequenceLoadLabel = this.mPackageManager.resolveActivity(intent, 0).loadLabel(this.mPackageManager);
                } else {
                    charSequenceLoadLabel = this.mPackageManager.getPackageInfo(str2, 0).applicationInfo.loadLabel(this.mPackageManager);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e = e;
                charSequence = null;
            } catch (Exception e2) {
                e = e2;
                charSequence = null;
            }
            if (z) {
                try {
                    if (str2.equals("com.android.contacts")) {
                        Intent intent2 = new Intent();
                        intent2.setComponent(new ComponentName(str2, LauncherConst.ANDROID_PHONE_CLASS_NAME));
                        ResolveInfo resolveInfoResolveActivity = this.mPackageManager.resolveActivity(intent2, 0);
                        drawableLoadUnbadgedFramedIcon = this.mPackageManager.getIconDrawableAsIconFrameTheme(resolveInfoResolveActivity.loadIcon(this.mPackageManager), str2, resolveInfoResolveActivity.getIconResource());
                    } else {
                        ApplicationInfo applicationInfo = this.mPackageManager.getPackageInfo(str2, 0).applicationInfo;
                        drawableLoadUnbadgedFramedIcon = this.mPackageManager.loadUnbadgedFramedIcon(applicationInfo, applicationInfo);
                        Log.d(TAG, "FrequentAppsInfo2 : " + drawableLoadUnbadgedFramedIcon.getIntrinsicWidth() + ", " + drawableLoadUnbadgedFramedIcon.getIntrinsicHeight());
                    }
                    drawableWrapIconDrawableWithShadow = new LauncherIcons(this.mContext).wrapIconDrawableWithShadow(drawableLoadUnbadgedFramedIcon);
                } catch (PackageManager.NameNotFoundException e3) {
                    charSequence = charSequenceLoadLabel;
                    e = e3;
                    Log.w(TAG, "NameNotFoundException : " + e);
                    charSequenceLoadLabel = charSequence;
                    drawableWrapIconDrawableWithShadow = null;
                } catch (Exception e4) {
                    charSequence = charSequenceLoadLabel;
                    e = e4;
                    Log.w(TAG, "Exception : " + e);
                    e.printStackTrace();
                    charSequenceLoadLabel = charSequence;
                    drawableWrapIconDrawableWithShadow = null;
                }
            } else {
                drawableWrapIconDrawableWithShadow = null;
            }
            if (charSequenceLoadLabel != null) {
                this.appInfos.add(new AppInfo(str2, charSequenceLoadLabel, drawableWrapIconDrawableWithShadow));
            }
        }
    }

    public static class AppInfo {
        public Drawable appIcon;
        public CharSequence appName;
        public String packageName;

        public AppInfo(String str, CharSequence charSequence, Drawable drawable) {
            this.packageName = null;
            this.appName = null;
            this.appIcon = null;
            this.packageName = str;
            this.appName = charSequence;
            this.appIcon = drawable;
        }
    }
}
