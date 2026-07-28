package com.android.launcher3.proxy;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class StartActivityParams implements Parcelable {
    public static final Parcelable.Creator<StartActivityParams> CREATOR = new Parcelable.Creator<StartActivityParams>() { // from class: com.android.launcher3.proxy.StartActivityParams.1
        /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StartActivityParams createFromParcel(Parcel source) {
            return new StartActivityParams(source);
        }

        /* JADX DEBUG: Method merged with bridge method: newArray(I)[Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StartActivityParams[] newArray(int size) {
            return new StartActivityParams[size];
        }
    };
    private static final String TAG = "StartActivityParams";
    public int extraFlags;
    public Intent fillInIntent;
    public int flagsMask;
    public int flagsValues;
    public Intent intent;
    public IntentSender intentSender;
    private final PendingIntent mPICallback;
    public Bundle options;
    public final int requestCode;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public StartActivityParams(Activity activity, int requestCode) {
        this(activity.createPendingResult(requestCode, new Intent(), 1207959552), requestCode);
    }

    public StartActivityParams(PendingIntent pendingIntent, int requestCode) {
        this.mPICallback = pendingIntent;
        this.requestCode = requestCode;
    }

    private StartActivityParams(Parcel parcel) {
        this.mPICallback = (PendingIntent) parcel.readTypedObject(PendingIntent.CREATOR);
        this.requestCode = parcel.readInt();
        this.intent = (Intent) parcel.readTypedObject(Intent.CREATOR);
        this.intentSender = (IntentSender) parcel.readTypedObject(IntentSender.CREATOR);
        this.fillInIntent = (Intent) parcel.readTypedObject(Intent.CREATOR);
        this.flagsMask = parcel.readInt();
        this.flagsValues = parcel.readInt();
        this.extraFlags = parcel.readInt();
        this.options = parcel.readBundle();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeTypedObject(this.mPICallback, flags);
        parcel.writeInt(this.requestCode);
        parcel.writeTypedObject(this.intent, flags);
        parcel.writeTypedObject(this.intentSender, flags);
        parcel.writeTypedObject(this.fillInIntent, flags);
        parcel.writeInt(this.flagsMask);
        parcel.writeInt(this.flagsValues);
        parcel.writeInt(this.extraFlags);
        parcel.writeBundle(this.options);
    }

    public void deliverResult(Context context, int resultCode, Intent data) {
        try {
            PendingIntent pendingIntent = this.mPICallback;
            if (pendingIntent != null) {
                pendingIntent.send(context, resultCode, data);
            }
        } catch (PendingIntent.CanceledException e) {
            Log.e(TAG, "Unable to send back result", e);
        }
    }
}
