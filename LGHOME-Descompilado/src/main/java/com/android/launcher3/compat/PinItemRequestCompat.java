package com.android.launcher3.compat;

import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.launcher3.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class PinItemRequestCompat implements Parcelable {
    public static final Parcelable.Creator<PinItemRequestCompat> CREATOR = new Parcelable.Creator<PinItemRequestCompat>() { // from class: com.android.launcher3.compat.PinItemRequestCompat.1
        /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PinItemRequestCompat createFromParcel(Parcel source) {
            return new PinItemRequestCompat(source.readParcelable(null));
        }

        /* JADX DEBUG: Method merged with bridge method: newArray(I)[Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PinItemRequestCompat[] newArray(int size) {
            return new PinItemRequestCompat[size];
        }
    };
    public static final String EXTRA_PIN_ITEM_REQUEST = "android.content.pm.extra.PIN_ITEM_REQUEST";
    public static final int REQUEST_TYPE_APPWIDGET = 2;
    public static final int REQUEST_TYPE_SHORTCUT = 1;
    private final Parcelable mObject;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    private PinItemRequestCompat(Parcelable object) {
        this.mObject = object;
    }

    public int getRequestType() {
        return ((Integer) invokeMethod("getRequestType")).intValue();
    }

    public ShortcutInfo getShortcutInfo() {
        return (ShortcutInfo) invokeMethod("getShortcutInfo");
    }

    public AppWidgetProviderInfo getAppWidgetProviderInfo(Context context) {
        try {
            return (AppWidgetProviderInfo) this.mObject.getClass().getDeclaredMethod("getAppWidgetProviderInfo", Context.class).invoke(this.mObject, context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isValid() {
        return ((Boolean) invokeMethod("isValid")).booleanValue();
    }

    public boolean accept() {
        return ((Boolean) invokeMethod("accept")).booleanValue();
    }

    public boolean accept(Bundle options) {
        try {
            return ((Boolean) this.mObject.getClass().getDeclaredMethod("accept", Bundle.class).invoke(this.mObject, options)).booleanValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Bundle getExtras() {
        try {
            return (Bundle) this.mObject.getClass().getDeclaredMethod("getExtras", new Class[0]).invoke(this.mObject, new Object[0]);
        } catch (Exception unused) {
            return null;
        }
    }

    private Object invokeMethod(String methodName) {
        try {
            return this.mObject.getClass().getDeclaredMethod(methodName, new Class[0]).invoke(this.mObject, new Object[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.mObject, i);
    }

    public static PinItemRequestCompat getPinItemRequest(Intent intent) {
        Parcelable parcelableExtra;
        if (Utilities.isAtLeastO() && (parcelableExtra = intent.getParcelableExtra(EXTRA_PIN_ITEM_REQUEST)) != null) {
            return new PinItemRequestCompat(parcelableExtra);
        }
        return null;
    }
}
