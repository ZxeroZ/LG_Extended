package com.lge.launcher3.smartbulletin.widgetlibrary;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Parcel;
import android.util.SizeF;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import com.lge.launcher3.smartbulletin.log.SBLog;
import java.lang.reflect.Field;

/* JADX INFO: loaded from: classes.dex */
public class MyRemoteViews extends RemoteViews {
    private static final String TAG = "LgeRemoteViews";
    private Context mContext;

    public MyRemoteViews(Parcel parcel) {
        super(parcel);
        this.mContext = null;
    }

    public static MyRemoteViews getLgeRemoteViewsFromRemoteViews(RemoteViews remoteView) {
        Parcel parcelObtain = Parcel.obtain();
        remoteView.writeToParcel(parcelObtain, 0);
        parcelObtain.setDataPosition(0);
        MyRemoteViews myRemoteViews = new MyRemoteViews(parcelObtain);
        parcelObtain.recycle();
        return myRemoteViews;
    }

    @Override // android.widget.RemoteViews
    public View apply(Context context, ViewGroup parent) {
        return super.apply(getCustomContext(context), parent);
    }

    @Override // android.widget.RemoteViews
    public void reapply(Context context, View v) {
        super.reapply(getCustomContext(context), v);
    }

    public View apply(Context context, ViewGroup parent, RemoteViews.InteractionHandler handler) {
        return super.apply(getCustomContext(context), parent, handler);
    }

    public void reapply(Context context, View v, RemoteViews.InteractionHandler handler) {
        super.reapply(getCustomContext(context), v, handler);
    }

    public View apply(Context context, ViewGroup parent, RemoteViews.InteractionHandler handler, SizeF size) {
        return super.apply(getCustomContext(context), parent, handler, size);
    }

    public View apply(Context context, ViewGroup parent, RemoteViews.InteractionHandler handler, SizeF size, RemoteViews.ColorResources colorResources) {
        return super.apply(getCustomContext(context), parent, handler, size, colorResources);
    }

    public void reapply(Context context, View v, RemoteViews.InteractionHandler handler, SizeF size, RemoteViews.ColorResources colorResources) {
        super.reapply(getCustomContext(context), v, handler, size, colorResources);
    }

    private Context getCustomContext(Context context) {
        if (this.mContext == null) {
            try {
                this.mContext = new MyWidgetContext(context.createApplicationContext((ApplicationInfo) getPrivateField(RemoteViews.class, this, "mApplication"), 3));
            } catch (Exception unused) {
                this.mContext = context;
            }
        }
        return this.mContext;
    }

    private static Object getPrivateField(Class<?> clazz, Object target, String name) {
        try {
            Field declaredField = clazz.getDeclaredField(name);
            declaredField.setAccessible(true);
            return declaredField.get(target);
        } catch (IllegalAccessException e) {
            SBLog.d(TAG, "IllegalAccessException, getPrivateField failed, field = " + name);
            throw new RuntimeException(e.getCause());
        } catch (IllegalArgumentException e2) {
            SBLog.d(TAG, "IllegalArgumentException, getPrivateField failed, field = " + name);
            throw new RuntimeException(e2.getCause());
        } catch (NoSuchFieldException e3) {
            SBLog.d(TAG, "NoSuchFieldException, getPrivateField failed, field = " + name);
            throw new RuntimeException(e3.getCause());
        }
    }
}
