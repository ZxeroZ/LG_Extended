package com.android.systemui.shared.system;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.util.Size;
import android.view.SurfaceControl;
import android.view.SurfaceControlViewHost;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowlessWindowManager;

/* JADX INFO: loaded from: classes.dex */
public class SurfaceViewRequestReceiver {
    private final int mOpacity;
    private SurfaceControlViewHost mSurfaceControlViewHost;

    public SurfaceViewRequestReceiver() {
        this(-2);
    }

    public SurfaceViewRequestReceiver(int i) {
        this.mOpacity = i;
    }

    public void onReceive(Context context, Bundle bundle, View view) {
        onReceive(context, bundle, view, null);
    }

    public void onReceive(Context context, Bundle bundle, View view, Size size) {
        SurfaceControlViewHost surfaceControlViewHost = this.mSurfaceControlViewHost;
        if (surfaceControlViewHost != null) {
            surfaceControlViewHost.release();
        }
        SurfaceControl surfaceControl = SurfaceViewRequestUtils.getSurfaceControl(bundle);
        if (surfaceControl != null) {
            if (size == null) {
                size = new Size(surfaceControl.getWidth(), surfaceControl.getHeight());
            }
            this.mSurfaceControlViewHost = new SurfaceControlViewHost(context, ((DisplayManager) context.getSystemService("display")).getDisplay(SurfaceViewRequestUtils.getDisplayId(bundle)), new WindowlessWindowManager(context.getResources().getConfiguration(), surfaceControl, SurfaceViewRequestUtils.getHostToken(bundle)));
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(size.getWidth(), size.getHeight(), 2, 16777216, this.mOpacity);
            float fMin = Math.min(surfaceControl.getWidth() / size.getWidth(), surfaceControl.getHeight() / size.getHeight());
            view.setScaleX(fMin);
            view.setScaleY(fMin);
            view.setPivotX(0.0f);
            view.setPivotY(0.0f);
            view.setTranslationX((surfaceControl.getWidth() - (size.getWidth() * fMin)) / 2.0f);
            view.setTranslationY((surfaceControl.getHeight() - (fMin * size.getHeight())) / 2.0f);
            this.mSurfaceControlViewHost.setView(view, layoutParams);
        }
    }
}
