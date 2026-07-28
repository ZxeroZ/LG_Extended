package com.android.launcher3.graphics;

import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.drawable.shapes.PathShape;

/* JADX INFO: loaded from: classes.dex */
public class TriangleShape extends PathShape {
    private Path mTriangularPath;

    public TriangleShape(Path path, float stdWidth, float stdHeight) {
        super(path, stdWidth, stdHeight);
        this.mTriangularPath = path;
    }

    public static TriangleShape create(float width, float height, boolean isPointingUp) {
        Path path = new Path();
        if (isPointingUp) {
            path.moveTo(0.0f, height);
            path.lineTo(width, height);
            path.lineTo(width / 2.0f, 0.0f);
            path.close();
        } else {
            path.moveTo(0.0f, 0.0f);
            path.lineTo(width / 2.0f, height);
            path.lineTo(width, 0.0f);
            path.close();
        }
        return new TriangleShape(path, width, height);
    }

    @Override // android.graphics.drawable.shapes.Shape
    public void getOutline(Outline outline) {
        outline.setConvexPath(this.mTriangularPath);
    }
}
