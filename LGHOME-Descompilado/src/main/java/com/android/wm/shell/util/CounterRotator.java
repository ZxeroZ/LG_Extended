package com.android.wm.shell.util;

import android.graphics.Point;
import android.util.RotationUtils;
import android.view.SurfaceControl;

/* JADX INFO: loaded from: classes.dex */
public class CounterRotator {
    private SurfaceControl mSurface = null;

    public SurfaceControl getSurface() {
        return this.mSurface;
    }

    public void setup(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl, int i, float f, float f2) {
        if (i == 0) {
            return;
        }
        SurfaceControl surfaceControlBuild = new SurfaceControl.Builder().setName("Transition Unrotate").setContainerLayer().setParent(surfaceControl).build();
        this.mSurface = surfaceControlBuild;
        RotationUtils.rotateSurface(transaction, surfaceControlBuild, i);
        Point point = new Point(0, 0);
        if (i % 2 != 0) {
            f2 = f;
            f = f2;
        }
        RotationUtils.rotatePoint(point, i, (int) f, (int) f2);
        transaction.setPosition(this.mSurface, point.x, point.y);
        transaction.show(this.mSurface);
    }

    public void addChild(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl) {
        SurfaceControl surfaceControl2 = this.mSurface;
        if (surfaceControl2 == null) {
            return;
        }
        transaction.reparent(surfaceControl, surfaceControl2);
    }

    public void cleanUp(SurfaceControl.Transaction transaction) {
        SurfaceControl surfaceControl = this.mSurface;
        if (surfaceControl == null) {
            return;
        }
        transaction.remove(surfaceControl);
    }
}
