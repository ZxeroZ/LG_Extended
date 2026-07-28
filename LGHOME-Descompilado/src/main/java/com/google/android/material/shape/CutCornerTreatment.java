package com.google.android.material.shape;

/* JADX INFO: loaded from: classes.dex */
public class CutCornerTreatment extends CornerTreatment implements Cloneable {
    public CutCornerTreatment(float f) {
        super(f);
    }

    @Override // com.google.android.material.shape.CornerTreatment
    public void getCornerPath(float f, float f2, ShapePath shapePath) {
        shapePath.reset(0.0f, this.cornerSize * f2, 180.0f, 180.0f - f);
        double d = f2;
        shapePath.lineTo((float) (Math.sin(Math.toRadians(f)) * ((double) this.cornerSize) * d), (float) (Math.sin(Math.toRadians(90.0f - f)) * ((double) this.cornerSize) * d));
    }
}
