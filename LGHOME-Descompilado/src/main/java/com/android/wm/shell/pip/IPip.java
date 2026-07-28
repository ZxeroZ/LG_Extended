package com.android.wm.shell.pip;

import android.app.PictureInPictureParams;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.SurfaceControl;
import com.android.wm.shell.pip.IPipAnimationListener;

/* JADX INFO: loaded from: classes.dex */
public interface IPip extends IInterface {
    public static final String DESCRIPTOR = "com.android.wm.shell.pip.IPip";

    public static class Default implements IPip {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.android.wm.shell.pip.IPip
        public void setPinnedStackAnimationListener(IPipAnimationListener iPipAnimationListener) throws RemoteException {
        }

        @Override // com.android.wm.shell.pip.IPip
        public void setShelfHeight(boolean z, int i) throws RemoteException {
        }

        @Override // com.android.wm.shell.pip.IPip
        public Rect startSwipePipToHome(ComponentName componentName, ActivityInfo activityInfo, PictureInPictureParams pictureInPictureParams, int i, int i2) throws RemoteException {
            return null;
        }

        @Override // com.android.wm.shell.pip.IPip
        public void stopSwipePipToHome(int i, ComponentName componentName, Rect rect, SurfaceControl surfaceControl) throws RemoteException {
        }
    }

    void setPinnedStackAnimationListener(IPipAnimationListener iPipAnimationListener) throws RemoteException;

    void setShelfHeight(boolean z, int i) throws RemoteException;

    Rect startSwipePipToHome(ComponentName componentName, ActivityInfo activityInfo, PictureInPictureParams pictureInPictureParams, int i, int i2) throws RemoteException;

    void stopSwipePipToHome(int i, ComponentName componentName, Rect rect, SurfaceControl surfaceControl) throws RemoteException;

    public static abstract class Stub extends Binder implements IPip {
        static final int TRANSACTION_setPinnedStackAnimationListener = 4;
        static final int TRANSACTION_setShelfHeight = 5;
        static final int TRANSACTION_startSwipePipToHome = 2;
        static final int TRANSACTION_stopSwipePipToHome = 3;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IPip.DESCRIPTOR);
        }

        public static IPip asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IPip.DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IPip)) {
                return (IPip) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IPip.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IPip.DESCRIPTOR);
                return true;
            }
            if (i == 2) {
                ComponentName componentName = (ComponentName) parcel.readTypedObject(ComponentName.CREATOR);
                ActivityInfo activityInfo = (ActivityInfo) parcel.readTypedObject(ActivityInfo.CREATOR);
                PictureInPictureParams pictureInPictureParams = (PictureInPictureParams) parcel.readTypedObject(PictureInPictureParams.CREATOR);
                int i3 = parcel.readInt();
                int i4 = parcel.readInt();
                parcel.enforceNoDataAvail();
                Rect rectStartSwipePipToHome = startSwipePipToHome(componentName, activityInfo, pictureInPictureParams, i3, i4);
                parcel2.writeNoException();
                parcel2.writeTypedObject(rectStartSwipePipToHome, 1);
            } else if (i == 3) {
                int i5 = parcel.readInt();
                ComponentName componentName2 = (ComponentName) parcel.readTypedObject(ComponentName.CREATOR);
                Rect rect = (Rect) parcel.readTypedObject(Rect.CREATOR);
                SurfaceControl surfaceControl = (SurfaceControl) parcel.readTypedObject(SurfaceControl.CREATOR);
                parcel.enforceNoDataAvail();
                stopSwipePipToHome(i5, componentName2, rect, surfaceControl);
            } else if (i == 4) {
                IPipAnimationListener iPipAnimationListenerAsInterface = IPipAnimationListener.Stub.asInterface(parcel.readStrongBinder());
                parcel.enforceNoDataAvail();
                setPinnedStackAnimationListener(iPipAnimationListenerAsInterface);
            } else if (i == 5) {
                boolean z = parcel.readBoolean();
                int i6 = parcel.readInt();
                parcel.enforceNoDataAvail();
                setShelfHeight(z, i6);
            } else {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            return true;
        }

        private static class Proxy implements IPip {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IPip.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.android.wm.shell.pip.IPip
            public Rect startSwipePipToHome(ComponentName componentName, ActivityInfo activityInfo, PictureInPictureParams pictureInPictureParams, int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IPip.DESCRIPTOR);
                    parcelObtain.writeTypedObject(componentName, 0);
                    parcelObtain.writeTypedObject(activityInfo, 0);
                    parcelObtain.writeTypedObject(pictureInPictureParams, 0);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return (Rect) parcelObtain2.readTypedObject(Rect.CREATOR);
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.wm.shell.pip.IPip
            public void stopSwipePipToHome(int i, ComponentName componentName, Rect rect, SurfaceControl surfaceControl) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IPip.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeTypedObject(componentName, 0);
                    parcelObtain.writeTypedObject(rect, 0);
                    parcelObtain.writeTypedObject(surfaceControl, 0);
                    this.mRemote.transact(3, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.wm.shell.pip.IPip
            public void setPinnedStackAnimationListener(IPipAnimationListener iPipAnimationListener) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IPip.DESCRIPTOR);
                    parcelObtain.writeStrongInterface(iPipAnimationListener);
                    this.mRemote.transact(4, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.wm.shell.pip.IPip
            public void setShelfHeight(boolean z, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IPip.DESCRIPTOR);
                    parcelObtain.writeBoolean(z);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(5, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }
    }
}
