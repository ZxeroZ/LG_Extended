package com.android.launcher3.util;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class ActivityResultInfo implements Parcelable {
    public static final Parcelable.Creator<ActivityResultInfo> CREATOR = new Parcelable.Creator<ActivityResultInfo>() { // from class: com.android.launcher3.util.ActivityResultInfo.1
        /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ActivityResultInfo createFromParcel(Parcel source) {
            return new ActivityResultInfo(source);
        }

        /* JADX DEBUG: Method merged with bridge method: newArray(I)[Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ActivityResultInfo[] newArray(int size) {
            return new ActivityResultInfo[size];
        }
    };
    public final Intent data;
    public final int requestCode;
    public final int resultCode;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ActivityResultInfo(int requestCode, int resultCode, Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }

    private ActivityResultInfo(Parcel parcel) {
        this.requestCode = parcel.readInt();
        this.resultCode = parcel.readInt();
        this.data = parcel.readInt() != 0 ? (Intent) Intent.CREATOR.createFromParcel(parcel) : null;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.requestCode);
        dest.writeInt(this.resultCode);
        if (this.data != null) {
            dest.writeInt(1);
            this.data.writeToParcel(dest, flags);
        } else {
            dest.writeInt(0);
        }
    }
}
