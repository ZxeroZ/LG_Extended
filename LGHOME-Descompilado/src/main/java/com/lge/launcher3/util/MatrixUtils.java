package com.lge.launcher3.util;

import android.graphics.Matrix;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class MatrixUtils {
    public static final float getScaleX(Matrix matrix, float[] recyle) {
        return getValue(matrix, recyle, 0);
    }

    public static final float getScaleY(Matrix matrix, float[] recyle) {
        return getValue(matrix, recyle, 4);
    }

    public static final float getTranslationX(Matrix matrix, float[] recyle) {
        return getValue(matrix, recyle, 2);
    }

    public static final float getTranslationY(Matrix matrix, float[] recyle) {
        return getValue(matrix, recyle, 5);
    }

    private static final float getValue(Matrix matrix, float[] values, int index) {
        Objects.requireNonNull(matrix, "matrix is null");
        if (values == null) {
            values = new float[9];
        }
        matrix.getValues(values);
        return values[index];
    }

    public static final Matrix getFlipHorizontalMatrix(Matrix matrix) {
        if (matrix == null) {
            matrix = new Matrix();
        }
        matrix.postScale(-1.0f, 1.0f);
        return matrix;
    }

    public static final Matrix getFlipVerticallMatrix(Matrix matrix) {
        if (matrix == null) {
            matrix = new Matrix();
        }
        matrix.postScale(1.0f, -1.0f);
        return matrix;
    }
}
