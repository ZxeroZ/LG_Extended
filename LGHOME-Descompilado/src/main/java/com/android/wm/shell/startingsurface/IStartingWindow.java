package com.android.wm.shell.startingsurface;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.android.wm.shell.startingsurface.IStartingWindowListener;

/* JADX INFO: loaded from: classes.dex */
public interface IStartingWindow extends IInterface {
    public static final String DESCRIPTOR = "com.android.wm.shell.startingsurface.IStartingWindow";

    public static class Default implements IStartingWindow {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.android.wm.shell.startingsurface.IStartingWindow
        public void setStartingWindowListener(IStartingWindowListener iStartingWindowListener) throws RemoteException {
        }
    }

    void setStartingWindowListener(IStartingWindowListener iStartingWindowListener) throws RemoteException;

    public static abstract class Stub extends Binder implements IStartingWindow {
        static final int TRANSACTION_setStartingWindowListener = 44;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IStartingWindow.DESCRIPTOR);
        }

        public static IStartingWindow asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IStartingWindow.DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IStartingWindow)) {
                return (IStartingWindow) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IStartingWindow.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IStartingWindow.DESCRIPTOR);
                return true;
            }
            if (i == 44) {
                IStartingWindowListener iStartingWindowListenerAsInterface = IStartingWindowListener.Stub.asInterface(parcel.readStrongBinder());
                parcel.enforceNoDataAvail();
                setStartingWindowListener(iStartingWindowListenerAsInterface);
                return true;
            }
            return super.onTransact(i, parcel, parcel2, i2);
        }

        private static class Proxy implements IStartingWindow {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IStartingWindow.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.android.wm.shell.startingsurface.IStartingWindow
            public void setStartingWindowListener(IStartingWindowListener iStartingWindowListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IStartingWindow.DESCRIPTOR);
                    parcelObtain.writeStrongInterface(iStartingWindowListener);
                    this.mRemote.transact(44, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }
    }
}
