package com.android.wm.shell.pip;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IPipAnimationListener extends IInterface {
    public static final String DESCRIPTOR = "com.android.wm.shell.pip.IPipAnimationListener";

    public static class Default implements IPipAnimationListener {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.android.wm.shell.pip.IPipAnimationListener
        public void onExpandPip() throws RemoteException {
        }

        @Override // com.android.wm.shell.pip.IPipAnimationListener
        public void onPipAnimationStarted() throws RemoteException {
        }

        @Override // com.android.wm.shell.pip.IPipAnimationListener
        public void onPipResourceDimensionsChanged(int i, int i2) throws RemoteException {
        }
    }

    void onExpandPip() throws RemoteException;

    void onPipAnimationStarted() throws RemoteException;

    void onPipResourceDimensionsChanged(int i, int i2) throws RemoteException;

    public static abstract class Stub extends Binder implements IPipAnimationListener {
        static final int TRANSACTION_onExpandPip = 3;
        static final int TRANSACTION_onPipAnimationStarted = 1;
        static final int TRANSACTION_onPipResourceDimensionsChanged = 2;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IPipAnimationListener.DESCRIPTOR);
        }

        public static IPipAnimationListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IPipAnimationListener.DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IPipAnimationListener)) {
                return (IPipAnimationListener) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IPipAnimationListener.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IPipAnimationListener.DESCRIPTOR);
                return true;
            }
            if (i == 1) {
                onPipAnimationStarted();
            } else if (i == 2) {
                int i3 = parcel.readInt();
                int i4 = parcel.readInt();
                parcel.enforceNoDataAvail();
                onPipResourceDimensionsChanged(i3, i4);
            } else if (i == 3) {
                onExpandPip();
            } else {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            return true;
        }

        private static class Proxy implements IPipAnimationListener {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IPipAnimationListener.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.android.wm.shell.pip.IPipAnimationListener
            public void onPipAnimationStarted() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IPipAnimationListener.DESCRIPTOR);
                    this.mRemote.transact(1, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.wm.shell.pip.IPipAnimationListener
            public void onPipResourceDimensionsChanged(int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IPipAnimationListener.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(2, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.wm.shell.pip.IPipAnimationListener
            public void onExpandPip() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IPipAnimationListener.DESCRIPTOR);
                    this.mRemote.transact(3, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }
    }
}
