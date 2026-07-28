package com.android.launcher3.widget;

import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.util.PendingRequestArgs;

/* JADX INFO: loaded from: classes.dex */
public class WidgetAddFlowHandler implements Parcelable {
    public static final Parcelable.Creator<WidgetAddFlowHandler> CREATOR = new Parcelable.Creator<WidgetAddFlowHandler>() { // from class: com.android.launcher3.widget.WidgetAddFlowHandler.1
        /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WidgetAddFlowHandler createFromParcel(Parcel source) {
            return new WidgetAddFlowHandler(source);
        }

        /* JADX DEBUG: Method merged with bridge method: newArray(I)[Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WidgetAddFlowHandler[] newArray(int size) {
            return new WidgetAddFlowHandler[size];
        }
    };
    private final AppWidgetProviderInfo mProviderInfo;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public WidgetAddFlowHandler(AppWidgetProviderInfo providerInfo) {
        this.mProviderInfo = providerInfo;
    }

    protected WidgetAddFlowHandler(Parcel parcel) {
        this.mProviderInfo = (AppWidgetProviderInfo) AppWidgetProviderInfo.CREATOR.createFromParcel(parcel);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        this.mProviderInfo.writeToParcel(parcel, i);
    }

    public void startBindFlow(Launcher launcher, int appWidgetId, ItemInfo info, int requestCode) {
        launcher.setWaitingForResult(PendingRequestArgs.forWidgetInfo(appWidgetId, this, info));
        Intent intent = new Intent("android.appwidget.action.APPWIDGET_BIND");
        intent.putExtra("appWidgetId", appWidgetId);
        intent.putExtra(LauncherSettings.Favorites.APPWIDGET_PROVIDER, this.mProviderInfo.provider);
        intent.putExtra("appWidgetProviderProfile", this.mProviderInfo.getProfile());
        launcher.startActivityForResult(intent, requestCode);
    }

    public boolean startConfigActivity(Launcher launcher, LauncherAppWidgetInfo info, int requestCode) {
        return startConfigActivity(launcher, info.appWidgetId, info, requestCode);
    }

    public boolean startConfigActivity(Launcher launcher, int appWidgetId, ItemInfo info, int requestCode) {
        if (!needsConfigure()) {
            return false;
        }
        launcher.setWaitingForResult(PendingRequestArgs.forWidgetInfo(appWidgetId, this, info));
        AppWidgetManagerCompat.getInstance(launcher).startConfigActivity(this.mProviderInfo, appWidgetId, launcher, launcher.getAppWidgetHost(), requestCode);
        return true;
    }

    public boolean needsConfigure() {
        return this.mProviderInfo.configure != null;
    }

    public LauncherAppWidgetProviderInfo getProviderInfo(Context context) {
        return LauncherAppWidgetProviderInfo.fromProviderInfo(context, this.mProviderInfo);
    }
}
