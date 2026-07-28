package com.google.android.material.shape;

/* JADX INFO: loaded from: classes.dex */
public class CornerTreatment implements Cloneable {
    protected float cornerSize;

    public void getCornerPath(float f, float f2, ShapePath shapePath) {
    }

    public CornerTreatment() {
        this.cornerSize = 0.0f;
    }

    protected CornerTreatment(float f) {
        this.cornerSize = f;
    }

    public float getCornerSize() {
        return this.cornerSize;
    }

    public void setCornerSize(float f) {
        this.cornerSize = f;
    }

    /* JADX DEBUG: Method merged with bridge method: clone()Ljava/lang/Object; */
    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public CornerTreatment m596clone() {
        try {
            return (CornerTreatment) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
