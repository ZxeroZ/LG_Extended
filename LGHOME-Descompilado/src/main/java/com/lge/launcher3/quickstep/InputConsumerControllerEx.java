package com.lge.launcher3.quickstep;

import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.view.BatchedInputEventReceiver;
import android.view.Choreographer;
import android.view.IWindowManager;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.WindowManagerGlobal;
import com.android.systemui.shared.system.InputConsumerController;
import com.lge.launcher3.util.LGLog;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public class InputConsumerControllerEx extends InputConsumerController {
    private static final boolean DEBUG = false;
    private static final String TAG = "InputConsumerControllerEx";
    private InputEventReceiver mInputEventReceiver;
    private InputConsumerController.InputListener mListener;
    private final String mName;
    private final IBinder mToken;
    private final IWindowManager mWindowManager;

    private final class InputEventReceiver extends BatchedInputEventReceiver {
        public InputEventReceiver(InputChannel inputChannel, Looper looper) {
            super(inputChannel, looper, Choreographer.getSfInstance());
        }

        public void onInputEvent(InputEvent event) {
            try {
                boolean zOnInputEvent = InputConsumerControllerEx.this.mListener != null ? InputConsumerControllerEx.this.mListener.onInputEvent(event) : true;
            } finally {
                finishInputEvent(event, true);
            }
        }
    }

    public InputConsumerControllerEx(IWindowManager windowManager, String name) {
        super(windowManager, name);
        this.mWindowManager = windowManager;
        this.mToken = new Binder();
        this.mName = name;
    }

    public static InputConsumerControllerEx getRecentsAnimationInputConsumer() {
        return new InputConsumerControllerEx(WindowManagerGlobal.getWindowManagerService(), "recents_animation_input_consumer");
    }

    @Override // com.android.systemui.shared.system.InputConsumerController
    public void setInputListener(InputConsumerController.InputListener listener) {
        this.mListener = listener;
    }

    @Override // com.android.systemui.shared.system.InputConsumerController
    public boolean isRegistered() {
        return this.mInputEventReceiver != null;
    }

    public void registerInputConsumer(int displayId) {
        if (this.mInputEventReceiver == null) {
            InputChannel inputChannel = new InputChannel();
            try {
                this.mWindowManager.destroyInputConsumer(this.mName, displayId);
                this.mWindowManager.createInputConsumer(this.mToken, this.mName, displayId, inputChannel);
                LGLog.i(TAG, "registerInputConsumer : displayId = " + displayId + ", success");
            } catch (RemoteException e) {
                LGLog.e(TAG, "Failed to create input consumer", e);
            }
            try {
                this.mInputEventReceiver = new InputEventReceiver(inputChannel, Looper.myLooper());
            } catch (Exception e2) {
                LGLog.e(TAG, "Failed to create InputEventReceiver", e2);
            }
        }
    }

    public void unregisterInputConsumer(int displayId) {
        if (this.mInputEventReceiver != null) {
            try {
                this.mWindowManager.destroyInputConsumer(this.mName, displayId);
                LGLog.i(TAG, "unregisterInputConsumer : displayId = " + displayId + ", success");
            } catch (RemoteException e) {
                LGLog.e(TAG, "Failed to destroy input consumer", e);
            }
            try {
                this.mInputEventReceiver.dispose();
            } catch (Exception e2) {
                LGLog.e(TAG, "Failed to dispose mInputEventReceiver", e2);
            }
            this.mInputEventReceiver = null;
        }
    }

    @Override // com.android.systemui.shared.system.InputConsumerController
    public void dump(PrintWriter pw, String prefix) {
        String str = prefix + "  ";
        pw.println(prefix + TAG);
        pw.println(str + "registered=" + (this.mInputEventReceiver != null));
    }
}
