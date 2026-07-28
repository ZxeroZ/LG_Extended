package com.google.android.material.shape;

/* JADX INFO: loaded from: classes.dex */
public class MaterialShapeUtils {
    static CornerTreatment createCornerTreatment(int i, int i2) {
        if (i == 0) {
            return new RoundedCornerTreatment(i2);
        }
        if (i == 1) {
            return new CutCornerTreatment(i2);
        }
        return createDefaultCornerTreatment();
    }

    static CornerTreatment createDefaultCornerTreatment() {
        return new RoundedCornerTreatment(0.0f);
    }

    static EdgeTreatment createDefaultEdgeTreatment() {
        return new EdgeTreatment();
    }
}
