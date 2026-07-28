package com.android.wm.shell.stagesplit;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface ISplitScreenListener extends IInterface {
    public static final String DESCRIPTOR = "com.android.wm.shell.stagesplit.ISplitScreenListener";

    public static class Default implements ISplitScreenListener {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.android.wm.shell.stagesplit.ISplitScreenListener
        public void onStagePositionChanged(int i, int i2) throws RemoteException {
        }

        @Override // com.android.wm.shell.stagesplit.ISplitScreenListener
        public void onTaskStageChanged(int i, int i2, boolean z) throws RemoteException {
        }
    }

    void onStagePositionChanged(int i, int i2) throws RemoteException;

    void onTaskStageChanged(int i, int i2, boolean z) throws RemoteException;

    public static abstract class Stub extends Binder implements ISplitScreenListener {
        static final int TRANSACTION_onStagePositionChanged = 1;
        static final int TRANSACTION_onTaskStageChanged = 2;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, ISplitScreenListener.DESCRIPTOR);
        }

        public static ISplitScreenListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(ISplitScreenListener.DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof ISplitScreenListener)) {
                return (ISplitScreenListener) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(ISplitScreenListener.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(ISplitScreenListener.DESCRIPTOR);
                return true;
            }
            if (i == 1) {
                int i3 = parcel.readInt();
                int i4 = parcel.readInt();
                parcel.enforceNoDataAvail();
                onStagePositionChanged(i3, i4);
            } else if (i == 2) {
                int i5 = parcel.readInt();
                int i6 = parcel.readInt();
                boolean z = parcel.readBoolean();
                parcel.enforceNoDataAvail();
                onTaskStageChanged(i5, i6, z);
            } else {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            return true;
        }

        private static class Proxy implements ISplitScreenListener {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return ISplitScreenListener.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.android.wm.shell.stagesplit.ISplitScreenListener
            public void onStagePositionChanged(int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISplitScreenListener.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(1, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.wm.shell.stagesplit.ISplitScreenListener
            public void onTaskStageChanged(int i, int i2, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISplitScreenListener.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeBoolean(z);
                    this.mRemote.transact(2, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }
    }
}
