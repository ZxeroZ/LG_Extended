package com.android.wm.shell.stagesplit;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.UserHandle;
import android.view.RemoteAnimationAdapter;
import android.view.RemoteAnimationTarget;
import android.window.RemoteTransition;
import com.android.wm.shell.stagesplit.ISplitScreenListener;

/* JADX INFO: loaded from: classes.dex */
public interface ISplitScreen extends IInterface {
    public static final String DESCRIPTOR = "com.android.wm.shell.stagesplit.ISplitScreen";

    public static class Default implements ISplitScreen {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.android.wm.shell.stagesplit.ISplitScreen
        public void exitSplitScreen(int i) throws RemoteException {
        }

        @Override // com.android.wm.shell.stagesplit.ISplitScreen
        public void exitSplitScreenOnHide(boolean z) throws RemoteException {
        }

        @Override // com.android.wm.shell.stagesplit.ISplitScreen
        public RemoteAnimationTarget[] onGoingToRecentsLegacy(boolean z, RemoteAnimationTarget[] remoteAnimationTargetArr) throws RemoteException {
            return null;
        }

        @Override // com.android.wm.shell.stagesplit.ISplitScreen
        public void registerSplitScreenListener(ISplitScreenListener iSplitScreenListener) throws RemoteException {
        }

        @Override // com.android.wm.shell.stagesplit.ISplitScreen
        public void removeFromSideStage(int i) throws RemoteException {
        }

        @Override // com.android.wm.shell.stagesplit.ISplitScreen
        public void setSideStageVisibility(boolean z) throws RemoteException {
        }

        @Override // com.android.wm.shell.stagesplit.ISplitScreen
        public void startIntent(PendingIntent pendingIntent, Intent intent, int i, int i2, Bundle bundle) throws RemoteException {
        }

        @Override // com.android.wm.shell.stagesplit.ISplitScreen
        public void startShortcut(String str, String str2, int i, int i2, Bundle bundle, UserHandle userHandle) throws RemoteException {
        }

        @Override // com.android.wm.shell.stagesplit.ISplitScreen
        public void startTask(int i, int i2, int i3, Bundle bundle) throws RemoteException {
        }

        @Override // com.android.wm.shell.stagesplit.ISplitScreen
        public void startTasks(int i, Bundle bundle, int i2, Bundle bundle2, int i3, RemoteTransition remoteTransition) throws RemoteException {
        }

        @Override // com.android.wm.shell.stagesplit.ISplitScreen
        public void startTasksWithLegacyTransition(int i, Bundle bundle, int i2, Bundle bundle2, int i3, RemoteAnimationAdapter remoteAnimationAdapter) throws RemoteException {
        }

        @Override // com.android.wm.shell.stagesplit.ISplitScreen
        public void unregisterSplitScreenListener(ISplitScreenListener iSplitScreenListener) throws RemoteException {
        }
    }

    void exitSplitScreen(int i) throws RemoteException;

    void exitSplitScreenOnHide(boolean z) throws RemoteException;

    RemoteAnimationTarget[] onGoingToRecentsLegacy(boolean z, RemoteAnimationTarget[] remoteAnimationTargetArr) throws RemoteException;

    void registerSplitScreenListener(ISplitScreenListener iSplitScreenListener) throws RemoteException;

    void removeFromSideStage(int i) throws RemoteException;

    void setSideStageVisibility(boolean z) throws RemoteException;

    void startIntent(PendingIntent pendingIntent, Intent intent, int i, int i2, Bundle bundle) throws RemoteException;

    void startShortcut(String str, String str2, int i, int i2, Bundle bundle, UserHandle userHandle) throws RemoteException;

    void startTask(int i, int i2, int i3, Bundle bundle) throws RemoteException;

    void startTasks(int i, Bundle bundle, int i2, Bundle bundle2, int i3, RemoteTransition remoteTransition) throws RemoteException;

    void startTasksWithLegacyTransition(int i, Bundle bundle, int i2, Bundle bundle2, int i3, RemoteAnimationAdapter remoteAnimationAdapter) throws RemoteException;

    void unregisterSplitScreenListener(ISplitScreenListener iSplitScreenListener) throws RemoteException;

    public static abstract class Stub extends Binder implements ISplitScreen {
        static final int TRANSACTION_exitSplitScreen = 6;
        static final int TRANSACTION_exitSplitScreenOnHide = 7;
        static final int TRANSACTION_onGoingToRecentsLegacy = 13;
        static final int TRANSACTION_registerSplitScreenListener = 2;
        static final int TRANSACTION_removeFromSideStage = 5;
        static final int TRANSACTION_setSideStageVisibility = 4;
        static final int TRANSACTION_startIntent = 10;
        static final int TRANSACTION_startShortcut = 9;
        static final int TRANSACTION_startTask = 8;
        static final int TRANSACTION_startTasks = 11;
        static final int TRANSACTION_startTasksWithLegacyTransition = 12;
        static final int TRANSACTION_unregisterSplitScreenListener = 3;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, ISplitScreen.DESCRIPTOR);
        }

        public static ISplitScreen asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(ISplitScreen.DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof ISplitScreen)) {
                return (ISplitScreen) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(ISplitScreen.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(ISplitScreen.DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 2:
                    ISplitScreenListener iSplitScreenListenerAsInterface = ISplitScreenListener.Stub.asInterface(parcel.readStrongBinder());
                    parcel.enforceNoDataAvail();
                    registerSplitScreenListener(iSplitScreenListenerAsInterface);
                    return true;
                case 3:
                    ISplitScreenListener iSplitScreenListenerAsInterface2 = ISplitScreenListener.Stub.asInterface(parcel.readStrongBinder());
                    parcel.enforceNoDataAvail();
                    unregisterSplitScreenListener(iSplitScreenListenerAsInterface2);
                    return true;
                case 4:
                    boolean z = parcel.readBoolean();
                    parcel.enforceNoDataAvail();
                    setSideStageVisibility(z);
                    return true;
                case 5:
                    int i3 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    removeFromSideStage(i3);
                    return true;
                case 6:
                    int i4 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    exitSplitScreen(i4);
                    return true;
                case 7:
                    boolean z2 = parcel.readBoolean();
                    parcel.enforceNoDataAvail();
                    exitSplitScreenOnHide(z2);
                    return true;
                case 8:
                    int i5 = parcel.readInt();
                    int i6 = parcel.readInt();
                    int i7 = parcel.readInt();
                    Bundle bundle = (Bundle) parcel.readTypedObject(Bundle.CREATOR);
                    parcel.enforceNoDataAvail();
                    startTask(i5, i6, i7, bundle);
                    return true;
                case 9:
                    String string = parcel.readString();
                    String string2 = parcel.readString();
                    int i8 = parcel.readInt();
                    int i9 = parcel.readInt();
                    Bundle bundle2 = (Bundle) parcel.readTypedObject(Bundle.CREATOR);
                    UserHandle userHandle = (UserHandle) parcel.readTypedObject(UserHandle.CREATOR);
                    parcel.enforceNoDataAvail();
                    startShortcut(string, string2, i8, i9, bundle2, userHandle);
                    return true;
                case 10:
                    PendingIntent pendingIntent = (PendingIntent) parcel.readTypedObject(PendingIntent.CREATOR);
                    Intent intent = (Intent) parcel.readTypedObject(Intent.CREATOR);
                    int i10 = parcel.readInt();
                    int i11 = parcel.readInt();
                    Bundle bundle3 = (Bundle) parcel.readTypedObject(Bundle.CREATOR);
                    parcel.enforceNoDataAvail();
                    startIntent(pendingIntent, intent, i10, i11, bundle3);
                    return true;
                case 11:
                    int i12 = parcel.readInt();
                    Bundle bundle4 = (Bundle) parcel.readTypedObject(Bundle.CREATOR);
                    int i13 = parcel.readInt();
                    Bundle bundle5 = (Bundle) parcel.readTypedObject(Bundle.CREATOR);
                    int i14 = parcel.readInt();
                    RemoteTransition remoteTransition = (RemoteTransition) parcel.readTypedObject(RemoteTransition.CREATOR);
                    parcel.enforceNoDataAvail();
                    startTasks(i12, bundle4, i13, bundle5, i14, remoteTransition);
                    return true;
                case 12:
                    int i15 = parcel.readInt();
                    Bundle bundle6 = (Bundle) parcel.readTypedObject(Bundle.CREATOR);
                    int i16 = parcel.readInt();
                    Bundle bundle7 = (Bundle) parcel.readTypedObject(Bundle.CREATOR);
                    int i17 = parcel.readInt();
                    RemoteAnimationAdapter remoteAnimationAdapter = (RemoteAnimationAdapter) parcel.readTypedObject(RemoteAnimationAdapter.CREATOR);
                    parcel.enforceNoDataAvail();
                    startTasksWithLegacyTransition(i15, bundle6, i16, bundle7, i17, remoteAnimationAdapter);
                    return true;
                case 13:
                    boolean z3 = parcel.readBoolean();
                    RemoteAnimationTarget[] remoteAnimationTargetArr = (RemoteAnimationTarget[]) parcel.createTypedArray(RemoteAnimationTarget.CREATOR);
                    parcel.enforceNoDataAvail();
                    RemoteAnimationTarget[] remoteAnimationTargetArrOnGoingToRecentsLegacy = onGoingToRecentsLegacy(z3, remoteAnimationTargetArr);
                    parcel2.writeNoException();
                    parcel2.writeTypedArray(remoteAnimationTargetArrOnGoingToRecentsLegacy, 1);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        private static class Proxy implements ISplitScreen {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return ISplitScreen.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.android.wm.shell.stagesplit.ISplitScreen
            public void registerSplitScreenListener(ISplitScreenListener iSplitScreenListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    parcelObtain.writeStrongInterface(iSplitScreenListener);
                    this.mRemote.transact(2, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.wm.shell.stagesplit.ISplitScreen
            public void unregisterSplitScreenListener(ISplitScreenListener iSplitScreenListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    parcelObtain.writeStrongInterface(iSplitScreenListener);
                    this.mRemote.transact(3, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.wm.shell.stagesplit.ISplitScreen
            public void setSideStageVisibility(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    parcelObtain.writeBoolean(z);
                    this.mRemote.transact(4, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.wm.shell.stagesplit.ISplitScreen
            public void removeFromSideStage(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(5, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.wm.shell.stagesplit.ISplitScreen
            public void exitSplitScreen(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(6, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.wm.shell.stagesplit.ISplitScreen
            public void exitSplitScreenOnHide(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    parcelObtain.writeBoolean(z);
                    this.mRemote.transact(7, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.wm.shell.stagesplit.ISplitScreen
            public void startTask(int i, int i2, int i3, Bundle bundle) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    parcelObtain.writeTypedObject(bundle, 0);
                    this.mRemote.transact(8, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.wm.shell.stagesplit.ISplitScreen
            public void startShortcut(String str, String str2, int i, int i2, Bundle bundle, UserHandle userHandle) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    parcelObtain.writeString(str);
                    parcelObtain.writeString(str2);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeTypedObject(bundle, 0);
                    parcelObtain.writeTypedObject(userHandle, 0);
                    this.mRemote.transact(9, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.wm.shell.stagesplit.ISplitScreen
            public void startIntent(PendingIntent pendingIntent, Intent intent, int i, int i2, Bundle bundle) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    parcelObtain.writeTypedObject(pendingIntent, 0);
                    parcelObtain.writeTypedObject(intent, 0);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeTypedObject(bundle, 0);
                    this.mRemote.transact(10, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.wm.shell.stagesplit.ISplitScreen
            public void startTasks(int i, Bundle bundle, int i2, Bundle bundle2, int i3, RemoteTransition remoteTransition) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeTypedObject(bundle, 0);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeTypedObject(bundle2, 0);
                    parcelObtain.writeInt(i3);
                    parcelObtain.writeTypedObject(remoteTransition, 0);
                    this.mRemote.transact(11, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.wm.shell.stagesplit.ISplitScreen
            public void startTasksWithLegacyTransition(int i, Bundle bundle, int i2, Bundle bundle2, int i3, RemoteAnimationAdapter remoteAnimationAdapter) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeTypedObject(bundle, 0);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeTypedObject(bundle2, 0);
                    parcelObtain.writeInt(i3);
                    parcelObtain.writeTypedObject(remoteAnimationAdapter, 0);
                    this.mRemote.transact(12, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.wm.shell.stagesplit.ISplitScreen
            public RemoteAnimationTarget[] onGoingToRecentsLegacy(boolean z, RemoteAnimationTarget[] remoteAnimationTargetArr) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISplitScreen.DESCRIPTOR);
                    parcelObtain.writeBoolean(z);
                    parcelObtain.writeTypedArray(remoteAnimationTargetArr, 0);
                    this.mRemote.transact(13, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return (RemoteAnimationTarget[]) parcelObtain2.createTypedArray(RemoteAnimationTarget.CREATOR);
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }
    }
}
