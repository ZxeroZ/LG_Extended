package com.android.wm.shell.back;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.window.IOnBackInvokedCallback;

/* JADX INFO: loaded from: classes.dex */
public interface IBackAnimation extends IInterface {
    public static final String DESCRIPTOR = "com.android.wm.shell.back.IBackAnimation";

    public static class Default implements IBackAnimation {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.android.wm.shell.back.IBackAnimation
        public void clearBackToLauncherCallback() throws RemoteException {
        }

        @Override // com.android.wm.shell.back.IBackAnimation
        public void onBackToLauncherAnimationFinished() throws RemoteException {
        }

        @Override // com.android.wm.shell.back.IBackAnimation
        public void setBackToLauncherCallback(IOnBackInvokedCallback iOnBackInvokedCallback) throws RemoteException {
        }
    }

    void clearBackToLauncherCallback() throws RemoteException;

    void onBackToLauncherAnimationFinished() throws RemoteException;

    void setBackToLauncherCallback(IOnBackInvokedCallback iOnBackInvokedCallback) throws RemoteException;

    public static abstract class Stub extends Binder implements IBackAnimation {
        static final int TRANSACTION_clearBackToLauncherCallback = 2;
        static final int TRANSACTION_onBackToLauncherAnimationFinished = 3;
        static final int TRANSACTION_setBackToLauncherCallback = 1;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IBackAnimation.DESCRIPTOR);
        }

        public static IBackAnimation asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IBackAnimation.DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IBackAnimation)) {
                return (IBackAnimation) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IBackAnimation.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IBackAnimation.DESCRIPTOR);
                return true;
            }
            if (i == 1) {
                IOnBackInvokedCallback iOnBackInvokedCallbackAsInterface = IOnBackInvokedCallback.Stub.asInterface(parcel.readStrongBinder());
                parcel.enforceNoDataAvail();
                setBackToLauncherCallback(iOnBackInvokedCallbackAsInterface);
                parcel2.writeNoException();
            } else if (i == 2) {
                clearBackToLauncherCallback();
                parcel2.writeNoException();
            } else if (i == 3) {
                onBackToLauncherAnimationFinished();
                parcel2.writeNoException();
            } else {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            return true;
        }

        private static class Proxy implements IBackAnimation {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IBackAnimation.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.android.wm.shell.back.IBackAnimation
            public void setBackToLauncherCallback(IOnBackInvokedCallback iOnBackInvokedCallback) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IBackAnimation.DESCRIPTOR);
                    parcelObtain.writeStrongInterface(iOnBackInvokedCallback);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.wm.shell.back.IBackAnimation
            public void clearBackToLauncherCallback() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IBackAnimation.DESCRIPTOR);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.wm.shell.back.IBackAnimation
            public void onBackToLauncherAnimationFinished() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IBackAnimation.DESCRIPTOR);
                    this.mRemote.transact(3, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }
    }
}
