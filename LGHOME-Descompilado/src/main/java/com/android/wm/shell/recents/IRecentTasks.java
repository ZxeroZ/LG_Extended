package com.android.wm.shell.recents;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.android.wm.shell.recents.IRecentTasksListener;
import com.android.wm.shell.util.GroupedRecentTaskInfo;

/* JADX INFO: loaded from: classes.dex */
public interface IRecentTasks extends IInterface {
    public static final String DESCRIPTOR = "com.android.wm.shell.recents.IRecentTasks";

    public static class Default implements IRecentTasks {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.android.wm.shell.recents.IRecentTasks
        public GroupedRecentTaskInfo[] getRecentTasks(int i, int i2, int i3) throws RemoteException {
            return null;
        }

        @Override // com.android.wm.shell.recents.IRecentTasks
        public void registerRecentTasksListener(IRecentTasksListener iRecentTasksListener) throws RemoteException {
        }

        @Override // com.android.wm.shell.recents.IRecentTasks
        public void unregisterRecentTasksListener(IRecentTasksListener iRecentTasksListener) throws RemoteException {
        }
    }

    GroupedRecentTaskInfo[] getRecentTasks(int i, int i2, int i3) throws RemoteException;

    void registerRecentTasksListener(IRecentTasksListener iRecentTasksListener) throws RemoteException;

    void unregisterRecentTasksListener(IRecentTasksListener iRecentTasksListener) throws RemoteException;

    public static abstract class Stub extends Binder implements IRecentTasks {
        static final int TRANSACTION_getRecentTasks = 4;
        static final int TRANSACTION_registerRecentTasksListener = 2;
        static final int TRANSACTION_unregisterRecentTasksListener = 3;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IRecentTasks.DESCRIPTOR);
        }

        public static IRecentTasks asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IRecentTasks.DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IRecentTasks)) {
                return (IRecentTasks) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IRecentTasks.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IRecentTasks.DESCRIPTOR);
                return true;
            }
            if (i == 2) {
                IRecentTasksListener iRecentTasksListenerAsInterface = IRecentTasksListener.Stub.asInterface(parcel.readStrongBinder());
                parcel.enforceNoDataAvail();
                registerRecentTasksListener(iRecentTasksListenerAsInterface);
            } else if (i == 3) {
                IRecentTasksListener iRecentTasksListenerAsInterface2 = IRecentTasksListener.Stub.asInterface(parcel.readStrongBinder());
                parcel.enforceNoDataAvail();
                unregisterRecentTasksListener(iRecentTasksListenerAsInterface2);
            } else if (i == 4) {
                int i3 = parcel.readInt();
                int i4 = parcel.readInt();
                int i5 = parcel.readInt();
                parcel.enforceNoDataAvail();
                GroupedRecentTaskInfo[] recentTasks = getRecentTasks(i3, i4, i5);
                parcel2.writeNoException();
                parcel2.writeTypedArray(recentTasks, 1);
            } else {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            return true;
        }

        private static class Proxy implements IRecentTasks {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IRecentTasks.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.android.wm.shell.recents.IRecentTasks
            public void registerRecentTasksListener(IRecentTasksListener iRecentTasksListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IRecentTasks.DESCRIPTOR);
                    parcelObtain.writeStrongInterface(iRecentTasksListener);
                    this.mRemote.transact(2, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.wm.shell.recents.IRecentTasks
            public void unregisterRecentTasksListener(IRecentTasksListener iRecentTasksListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IRecentTasks.DESCRIPTOR);
                    parcelObtain.writeStrongInterface(iRecentTasksListener);
                    this.mRemote.transact(3, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.wm.shell.recents.IRecentTasks
            public GroupedRecentTaskInfo[] getRecentTasks(int i, int i2, int i3) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IRecentTasks.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    this.mRemote.transact(4, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return (GroupedRecentTaskInfo[]) parcelObtain2.createTypedArray(GroupedRecentTaskInfo.CREATOR);
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }
    }
}
