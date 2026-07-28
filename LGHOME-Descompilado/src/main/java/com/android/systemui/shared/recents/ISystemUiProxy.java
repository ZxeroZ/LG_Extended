package com.android.systemui.shared.recents;

import android.graphics.Bitmap;
import android.graphics.Insets;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.MotionEvent;
import com.android.systemui.shared.recents.model.Task;

/* JADX INFO: loaded from: classes.dex */
public interface ISystemUiProxy extends IInterface {
    public static final String DESCRIPTOR = "com.android.systemui.shared.recents.ISystemUiProxy";

    public static class Default implements ISystemUiProxy {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void expandNotificationPanel() throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public Rect getNonMinimizedSplitScreenSecondaryBounds() throws RemoteException {
            return null;
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void handleImageAsScreenshot(Bitmap bitmap, Rect rect, Insets insets, int i) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void handleImageBundleAsScreenshot(Bundle bundle, Rect rect, Insets insets, Task.TaskKey taskKey) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void notifyAccessibilityButtonClicked(int i) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void notifyAccessibilityButtonLongClicked() throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void notifyPrioritizedRotation(int i) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void notifySwipeToHomeFinished() throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void notifySwipeUpGestureStarted() throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void notifyTaskbarAutohideSuspend(boolean z) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void notifyTaskbarStatus(boolean z, boolean z2) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void onAssistantGestureCompletion(float f) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void onAssistantProgress(float f) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void onBackPressed() throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void onImeSwitcherPressed() throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void onOverviewShown(boolean z) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void onStatusBarMotionEvent(MotionEvent motionEvent) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void setHomeRotationEnabled(boolean z) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        /* JADX INFO: renamed from: setNavBarButtonAlpha */
        public void lambda$setNavBarButtonAlpha$2$SystemUiProxy(float f, boolean z) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void setSplitScreenMinimized(boolean z) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void startAssistant(Bundle bundle) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void startFullscreenMode(int i, String str) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void startScreenPinning(int i) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void stopScreenPinning() throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.ISystemUiProxy
        public void toggleNotificationPanel() throws RemoteException {
        }
    }

    void expandNotificationPanel() throws RemoteException;

    @Deprecated
    Rect getNonMinimizedSplitScreenSecondaryBounds() throws RemoteException;

    @Deprecated
    void handleImageAsScreenshot(Bitmap bitmap, Rect rect, Insets insets, int i) throws RemoteException;

    void handleImageBundleAsScreenshot(Bundle bundle, Rect rect, Insets insets, Task.TaskKey taskKey) throws RemoteException;

    void notifyAccessibilityButtonClicked(int i) throws RemoteException;

    void notifyAccessibilityButtonLongClicked() throws RemoteException;

    void notifyPrioritizedRotation(int i) throws RemoteException;

    void notifySwipeToHomeFinished() throws RemoteException;

    void notifySwipeUpGestureStarted() throws RemoteException;

    void notifyTaskbarAutohideSuspend(boolean z) throws RemoteException;

    void notifyTaskbarStatus(boolean z, boolean z2) throws RemoteException;

    void onAssistantGestureCompletion(float f) throws RemoteException;

    void onAssistantProgress(float f) throws RemoteException;

    void onBackPressed() throws RemoteException;

    void onImeSwitcherPressed() throws RemoteException;

    void onOverviewShown(boolean z) throws RemoteException;

    void onStatusBarMotionEvent(MotionEvent motionEvent) throws RemoteException;

    void setHomeRotationEnabled(boolean z) throws RemoteException;

    /* JADX INFO: renamed from: setNavBarButtonAlpha */
    void lambda$setNavBarButtonAlpha$2$SystemUiProxy(float f, boolean z) throws RemoteException;

    @Deprecated
    void setSplitScreenMinimized(boolean z) throws RemoteException;

    void startAssistant(Bundle bundle) throws RemoteException;

    void startFullscreenMode(int i, String str) throws RemoteException;

    void startScreenPinning(int i) throws RemoteException;

    void stopScreenPinning() throws RemoteException;

    void toggleNotificationPanel() throws RemoteException;

    public static abstract class Stub extends Binder implements ISystemUiProxy {
        static final int TRANSACTION_expandNotificationPanel = 30;
        static final int TRANSACTION_getNonMinimizedSplitScreenSecondaryBounds = 8;
        static final int TRANSACTION_handleImageAsScreenshot = 22;
        static final int TRANSACTION_handleImageBundleAsScreenshot = 29;
        static final int TRANSACTION_notifyAccessibilityButtonClicked = 16;
        static final int TRANSACTION_notifyAccessibilityButtonLongClicked = 17;
        static final int TRANSACTION_notifyPrioritizedRotation = 26;
        static final int TRANSACTION_notifySwipeToHomeFinished = 24;
        static final int TRANSACTION_notifySwipeUpGestureStarted = 47;
        static final int TRANSACTION_notifyTaskbarAutohideSuspend = 49;
        static final int TRANSACTION_notifyTaskbarStatus = 48;
        static final int TRANSACTION_onAssistantGestureCompletion = 19;
        static final int TRANSACTION_onAssistantProgress = 13;
        static final int TRANSACTION_onBackPressed = 45;
        static final int TRANSACTION_onImeSwitcherPressed = 50;
        static final int TRANSACTION_onOverviewShown = 7;
        static final int TRANSACTION_onStatusBarMotionEvent = 10;
        static final int TRANSACTION_setHomeRotationEnabled = 46;
        static final int TRANSACTION_setNavBarButtonAlpha = 20;
        static final int TRANSACTION_setSplitScreenMinimized = 23;
        static final int TRANSACTION_startAssistant = 14;
        static final int TRANSACTION_startFullscreenMode = 52;
        static final int TRANSACTION_startScreenPinning = 2;
        static final int TRANSACTION_stopScreenPinning = 18;
        static final int TRANSACTION_toggleNotificationPanel = 51;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, ISystemUiProxy.DESCRIPTOR);
        }

        public static ISystemUiProxy asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(ISystemUiProxy.DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof ISystemUiProxy)) {
                return (ISystemUiProxy) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(ISystemUiProxy.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(ISystemUiProxy.DESCRIPTOR);
                return true;
            }
            if (i == 2) {
                int i3 = parcel.readInt();
                parcel.enforceNoDataAvail();
                startScreenPinning(i3);
                parcel2.writeNoException();
            } else if (i == 10) {
                MotionEvent motionEvent = (MotionEvent) parcel.readTypedObject(MotionEvent.CREATOR);
                parcel.enforceNoDataAvail();
                onStatusBarMotionEvent(motionEvent);
                parcel2.writeNoException();
            } else if (i == 26) {
                int i4 = parcel.readInt();
                parcel.enforceNoDataAvail();
                notifyPrioritizedRotation(i4);
                parcel2.writeNoException();
            } else if (i == 7) {
                boolean z = parcel.readBoolean();
                parcel.enforceNoDataAvail();
                onOverviewShown(z);
                parcel2.writeNoException();
            } else if (i == 8) {
                Rect nonMinimizedSplitScreenSecondaryBounds = getNonMinimizedSplitScreenSecondaryBounds();
                parcel2.writeNoException();
                parcel2.writeTypedObject(nonMinimizedSplitScreenSecondaryBounds, 1);
            } else if (i == 13) {
                float f = parcel.readFloat();
                parcel.enforceNoDataAvail();
                onAssistantProgress(f);
                parcel2.writeNoException();
            } else if (i == 14) {
                Bundle bundle = (Bundle) parcel.readTypedObject(Bundle.CREATOR);
                parcel.enforceNoDataAvail();
                startAssistant(bundle);
                parcel2.writeNoException();
            } else if (i == 29) {
                Bundle bundle2 = (Bundle) parcel.readTypedObject(Bundle.CREATOR);
                Rect rect = (Rect) parcel.readTypedObject(Rect.CREATOR);
                Insets insets = (Insets) parcel.readTypedObject(Insets.CREATOR);
                Task.TaskKey taskKey = (Task.TaskKey) parcel.readTypedObject(Task.TaskKey.CREATOR);
                parcel.enforceNoDataAvail();
                handleImageBundleAsScreenshot(bundle2, rect, insets, taskKey);
                parcel2.writeNoException();
            } else if (i != 30) {
                switch (i) {
                    case 16:
                        int i5 = parcel.readInt();
                        parcel.enforceNoDataAvail();
                        notifyAccessibilityButtonClicked(i5);
                        parcel2.writeNoException();
                        break;
                    case 17:
                        notifyAccessibilityButtonLongClicked();
                        parcel2.writeNoException();
                        break;
                    case 18:
                        stopScreenPinning();
                        parcel2.writeNoException();
                        break;
                    case 19:
                        float f2 = parcel.readFloat();
                        parcel.enforceNoDataAvail();
                        onAssistantGestureCompletion(f2);
                        parcel2.writeNoException();
                        break;
                    case 20:
                        float f3 = parcel.readFloat();
                        boolean z2 = parcel.readBoolean();
                        parcel.enforceNoDataAvail();
                        lambda$setNavBarButtonAlpha$2$SystemUiProxy(f3, z2);
                        parcel2.writeNoException();
                        break;
                    default:
                        switch (i) {
                            case 22:
                                Bitmap bitmap = (Bitmap) parcel.readTypedObject(Bitmap.CREATOR);
                                Rect rect2 = (Rect) parcel.readTypedObject(Rect.CREATOR);
                                Insets insets2 = (Insets) parcel.readTypedObject(Insets.CREATOR);
                                int i6 = parcel.readInt();
                                parcel.enforceNoDataAvail();
                                handleImageAsScreenshot(bitmap, rect2, insets2, i6);
                                parcel2.writeNoException();
                                break;
                            case 23:
                                boolean z3 = parcel.readBoolean();
                                parcel.enforceNoDataAvail();
                                setSplitScreenMinimized(z3);
                                parcel2.writeNoException();
                                break;
                            case 24:
                                notifySwipeToHomeFinished();
                                parcel2.writeNoException();
                                break;
                            default:
                                switch (i) {
                                    case 45:
                                        onBackPressed();
                                        parcel2.writeNoException();
                                        break;
                                    case 46:
                                        boolean z4 = parcel.readBoolean();
                                        parcel.enforceNoDataAvail();
                                        setHomeRotationEnabled(z4);
                                        parcel2.writeNoException();
                                        break;
                                    case 47:
                                        notifySwipeUpGestureStarted();
                                        break;
                                    case 48:
                                        boolean z5 = parcel.readBoolean();
                                        boolean z6 = parcel.readBoolean();
                                        parcel.enforceNoDataAvail();
                                        notifyTaskbarStatus(z5, z6);
                                        break;
                                    case 49:
                                        boolean z7 = parcel.readBoolean();
                                        parcel.enforceNoDataAvail();
                                        notifyTaskbarAutohideSuspend(z7);
                                        break;
                                    case 50:
                                        onImeSwitcherPressed();
                                        parcel2.writeNoException();
                                        break;
                                    case 51:
                                        toggleNotificationPanel();
                                        parcel2.writeNoException();
                                        break;
                                    case 52:
                                        int i7 = parcel.readInt();
                                        String string = parcel.readString();
                                        parcel.enforceNoDataAvail();
                                        startFullscreenMode(i7, string);
                                        parcel2.writeNoException();
                                        break;
                                    default:
                                        return super.onTransact(i, parcel, parcel2, i2);
                                }
                                break;
                        }
                        break;
                }
            } else {
                expandNotificationPanel();
                parcel2.writeNoException();
            }
            return true;
        }

        private static class Proxy implements ISystemUiProxy {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return ISystemUiProxy.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public void startScreenPinning(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(2, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public void onOverviewShown(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    parcelObtain.writeBoolean(z);
                    this.mRemote.transact(7, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public Rect getNonMinimizedSplitScreenSecondaryBounds() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    this.mRemote.transact(8, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                    return (Rect) parcelObtain2.readTypedObject(Rect.CREATOR);
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            /* JADX INFO: renamed from: setNavBarButtonAlpha */
            public void lambda$setNavBarButtonAlpha$2$SystemUiProxy(float f, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    parcelObtain.writeFloat(f);
                    parcelObtain.writeBoolean(z);
                    this.mRemote.transact(20, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public void onStatusBarMotionEvent(MotionEvent motionEvent) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    parcelObtain.writeTypedObject(motionEvent, 0);
                    this.mRemote.transact(10, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public void onAssistantProgress(float f) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    parcelObtain.writeFloat(f);
                    this.mRemote.transact(13, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public void onAssistantGestureCompletion(float f) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    parcelObtain.writeFloat(f);
                    this.mRemote.transact(19, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public void startAssistant(Bundle bundle) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    parcelObtain.writeTypedObject(bundle, 0);
                    this.mRemote.transact(14, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public void notifyAccessibilityButtonClicked(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(16, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public void notifyAccessibilityButtonLongClicked() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    this.mRemote.transact(17, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public void stopScreenPinning() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    this.mRemote.transact(18, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public void handleImageAsScreenshot(Bitmap bitmap, Rect rect, Insets insets, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    parcelObtain.writeTypedObject(bitmap, 0);
                    parcelObtain.writeTypedObject(rect, 0);
                    parcelObtain.writeTypedObject(insets, 0);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(22, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public void setSplitScreenMinimized(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    parcelObtain.writeBoolean(z);
                    this.mRemote.transact(23, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public void notifySwipeToHomeFinished() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    this.mRemote.transact(24, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public void notifyPrioritizedRotation(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(26, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public void handleImageBundleAsScreenshot(Bundle bundle, Rect rect, Insets insets, Task.TaskKey taskKey) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    parcelObtain.writeTypedObject(bundle, 0);
                    parcelObtain.writeTypedObject(rect, 0);
                    parcelObtain.writeTypedObject(insets, 0);
                    parcelObtain.writeTypedObject(taskKey, 0);
                    this.mRemote.transact(29, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public void expandNotificationPanel() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    this.mRemote.transact(30, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public void onBackPressed() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    this.mRemote.transact(45, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public void setHomeRotationEnabled(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    parcelObtain.writeBoolean(z);
                    this.mRemote.transact(46, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public void notifySwipeUpGestureStarted() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    this.mRemote.transact(47, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public void notifyTaskbarStatus(boolean z, boolean z2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    parcelObtain.writeBoolean(z);
                    parcelObtain.writeBoolean(z2);
                    this.mRemote.transact(48, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public void notifyTaskbarAutohideSuspend(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    parcelObtain.writeBoolean(z);
                    this.mRemote.transact(49, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public void onImeSwitcherPressed() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    this.mRemote.transact(50, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public void toggleNotificationPanel() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    this.mRemote.transact(51, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.ISystemUiProxy
            public void startFullscreenMode(int i, String str) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISystemUiProxy.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeString(str);
                    this.mRemote.transact(52, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }
        }
    }
}
