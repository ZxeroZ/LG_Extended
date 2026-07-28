package com.google.protobuf.nano.android;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

/* JADX INFO: loaded from: classes.dex */
public final class ParcelableMessageNanoCreator<T extends MessageNano> implements Parcelable.Creator<T> {
    private static final String TAG = "PMNCreator";
    private final Class<T> mClazz;

    public ParcelableMessageNanoCreator(Class<T> cls) {
        this.mClazz = cls;
    }

    /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object; */
    @Override // android.os.Parcelable.Creator
    public T createFromParcel(Parcel parcel) {
        T t;
        String string = parcel.readString();
        byte[] bArrCreateByteArray = parcel.createByteArray();
        T t2 = null;
        try {
            t = (T) Class.forName(string, false, getClass().getClassLoader()).asSubclass(MessageNano.class).getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (InvalidProtocolBufferNanoException e) {
            e = e;
        } catch (ClassNotFoundException e2) {
            e = e2;
        } catch (IllegalAccessException e3) {
            e = e3;
        } catch (InstantiationException e4) {
            e = e4;
        } catch (NoSuchMethodException e5) {
            e = e5;
        } catch (InvocationTargetException e6) {
            e = e6;
        }
        try {
            MessageNano.mergeFrom(t, bArrCreateByteArray);
            return t;
        } catch (InvalidProtocolBufferNanoException e7) {
            e = e7;
            t2 = t;
            Log.e(TAG, "Exception trying to create proto from parcel", e);
            return t2;
        } catch (ClassNotFoundException e8) {
            e = e8;
            t2 = t;
            Log.e(TAG, "Exception trying to create proto from parcel", e);
            return t2;
        } catch (IllegalAccessException e9) {
            e = e9;
            t2 = t;
            Log.e(TAG, "Exception trying to create proto from parcel", e);
            return t2;
        } catch (InstantiationException e10) {
            e = e10;
            t2 = t;
            Log.e(TAG, "Exception trying to create proto from parcel", e);
            return t2;
        } catch (NoSuchMethodException e11) {
            e = e11;
            t2 = t;
            Log.e(TAG, "Exception trying to create proto from parcel", e);
            return t2;
        } catch (InvocationTargetException e12) {
            e = e12;
            t2 = t;
            Log.e(TAG, "Exception trying to create proto from parcel", e);
            return t2;
        }
    }

    /* JADX DEBUG: Method merged with bridge method: newArray(I)[Ljava/lang/Object; */
    @Override // android.os.Parcelable.Creator
    public T[] newArray(int i) {
        return (T[]) ((MessageNano[]) Array.newInstance((Class<?>) this.mClazz, i));
    }

    static <T extends MessageNano> void writeToParcel(Class<T> cls, MessageNano messageNano, Parcel parcel) {
        parcel.writeString(cls.getName());
        parcel.writeByteArray(MessageNano.toByteArray(messageNano));
    }
}
