package com.google.android.material.shape;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import com.google.android.material.R;
import java.util.LinkedHashSet;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class ShapeAppearanceModel {
    public static final int PILL = -1;
    private EdgeTreatment bottomEdge;
    private CornerTreatment bottomLeftCorner;
    private CornerTreatment bottomRightCorner;
    private EdgeTreatment leftEdge;
    private final Set<OnChangedListener> onChangedListeners;
    private EdgeTreatment rightEdge;
    private EdgeTreatment topEdge;
    private CornerTreatment topLeftCorner;
    private CornerTreatment topRightCorner;

    public interface OnChangedListener {
        void onShapeAppearanceModelChanged();
    }

    public ShapeAppearanceModel() {
        this.onChangedListeners = new LinkedHashSet();
        setTopLeftCornerInternal(MaterialShapeUtils.createDefaultCornerTreatment());
        setTopRightCornerInternal(MaterialShapeUtils.createDefaultCornerTreatment());
        setBottomRightCornerInternal(MaterialShapeUtils.createDefaultCornerTreatment());
        setBottomLeftCornerInternal(MaterialShapeUtils.createDefaultCornerTreatment());
        setLeftEdgeInternal(MaterialShapeUtils.createDefaultEdgeTreatment());
        setTopEdgeInternal(MaterialShapeUtils.createDefaultEdgeTreatment());
        setRightEdgeInternal(MaterialShapeUtils.createDefaultEdgeTreatment());
        setBottomEdgeInternal(MaterialShapeUtils.createDefaultEdgeTreatment());
        onShapeAppearanceModelChanged();
    }

    public ShapeAppearanceModel(ShapeAppearanceModel shapeAppearanceModel) {
        this.onChangedListeners = new LinkedHashSet();
        setTopLeftCornerInternal(shapeAppearanceModel.getTopLeftCorner().m596clone());
        setTopRightCornerInternal(shapeAppearanceModel.getTopRightCorner().m596clone());
        setBottomRightCornerInternal(shapeAppearanceModel.getBottomRightCorner().m596clone());
        setBottomLeftCornerInternal(shapeAppearanceModel.getBottomLeftCorner().m596clone());
        setLeftEdgeInternal(shapeAppearanceModel.getLeftEdge().m597clone());
        setTopEdgeInternal(shapeAppearanceModel.getTopEdge().m597clone());
        setRightEdgeInternal(shapeAppearanceModel.getRightEdge().m597clone());
        setBottomEdgeInternal(shapeAppearanceModel.getBottomEdge().m597clone());
    }

    public ShapeAppearanceModel(Context context, AttributeSet attributeSet, int i, int i2) {
        this(context, attributeSet, i, i2, 0);
    }

    public ShapeAppearanceModel(Context context, AttributeSet attributeSet, int i, int i2, int i3) {
        this.onChangedListeners = new LinkedHashSet();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.MaterialShape, i, i2);
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.MaterialShape_shapeAppearance, 0);
        int resourceId2 = typedArrayObtainStyledAttributes.getResourceId(R.styleable.MaterialShape_shapeAppearanceOverlay, 0);
        typedArrayObtainStyledAttributes.recycle();
        if (resourceId2 != 0) {
            context = new ContextThemeWrapper(context, resourceId);
            resourceId = resourceId2;
        }
        TypedArray typedArrayObtainStyledAttributes2 = context.obtainStyledAttributes(resourceId, R.styleable.ShapeAppearance);
        int i4 = typedArrayObtainStyledAttributes2.getInt(R.styleable.ShapeAppearance_cornerFamily, 0);
        int i5 = typedArrayObtainStyledAttributes2.getInt(R.styleable.ShapeAppearance_cornerFamilyTopLeft, i4);
        int i6 = typedArrayObtainStyledAttributes2.getInt(R.styleable.ShapeAppearance_cornerFamilyTopRight, i4);
        int i7 = typedArrayObtainStyledAttributes2.getInt(R.styleable.ShapeAppearance_cornerFamilyBottomRight, i4);
        int i8 = typedArrayObtainStyledAttributes2.getInt(R.styleable.ShapeAppearance_cornerFamilyBottomLeft, i4);
        int dimensionPixelSize = typedArrayObtainStyledAttributes2.getDimensionPixelSize(R.styleable.ShapeAppearance_cornerSize, i3);
        int dimensionPixelSize2 = typedArrayObtainStyledAttributes2.getDimensionPixelSize(R.styleable.ShapeAppearance_cornerSizeTopLeft, dimensionPixelSize);
        int dimensionPixelSize3 = typedArrayObtainStyledAttributes2.getDimensionPixelSize(R.styleable.ShapeAppearance_cornerSizeTopRight, dimensionPixelSize);
        int dimensionPixelSize4 = typedArrayObtainStyledAttributes2.getDimensionPixelSize(R.styleable.ShapeAppearance_cornerSizeBottomRight, dimensionPixelSize);
        int dimensionPixelSize5 = typedArrayObtainStyledAttributes2.getDimensionPixelSize(R.styleable.ShapeAppearance_cornerSizeBottomLeft, dimensionPixelSize);
        setTopLeftCornerInternal(MaterialShapeUtils.createCornerTreatment(i5, dimensionPixelSize2));
        setTopRightCornerInternal(MaterialShapeUtils.createCornerTreatment(i6, dimensionPixelSize3));
        setBottomRightCornerInternal(MaterialShapeUtils.createCornerTreatment(i7, dimensionPixelSize4));
        setBottomLeftCornerInternal(MaterialShapeUtils.createCornerTreatment(i8, dimensionPixelSize5));
        setTopEdgeInternal(MaterialShapeUtils.createDefaultEdgeTreatment());
        setRightEdgeInternal(MaterialShapeUtils.createDefaultEdgeTreatment());
        setBottomEdgeInternal(MaterialShapeUtils.createDefaultEdgeTreatment());
        setLeftEdgeInternal(MaterialShapeUtils.createDefaultEdgeTreatment());
        typedArrayObtainStyledAttributes2.recycle();
    }

    public void setAllCorners(int i, int i2) {
        setAllCorners(MaterialShapeUtils.createCornerTreatment(i, i2));
    }

    public void setAllCorners(CornerTreatment cornerTreatment) {
        if (setBottomLeftCornerInternal(cornerTreatment.m596clone()) || ((setTopLeftCornerInternal(cornerTreatment.m596clone()) | setTopRightCornerInternal(cornerTreatment.m596clone())) | setBottomRightCornerInternal(cornerTreatment.m596clone()))) {
            onShapeAppearanceModelChanged();
        }
    }

    public void setCornerRadius(float f) {
        setCornerRadii(f, f, f, f);
    }

    public void setCornerRadii(float f, float f2, float f3, float f4) {
        if ((setTopLeftCornerSizeInternal(f) | setTopRightCornerSizeInternal(f2) | setBottomRightCornerSizeInternal(f3)) || setBottomLeftCornerSizeInternal(f4)) {
            onShapeAppearanceModelChanged();
        }
    }

    private boolean setTopLeftCornerSizeInternal(float f) {
        if (this.topLeftCorner.cornerSize == f) {
            return false;
        }
        this.topLeftCorner.cornerSize = f;
        return true;
    }

    private boolean setTopRightCornerSizeInternal(float f) {
        if (this.topRightCorner.cornerSize == f) {
            return false;
        }
        this.topRightCorner.cornerSize = f;
        return true;
    }

    private boolean setBottomRightCornerSizeInternal(float f) {
        if (this.bottomRightCorner.cornerSize == f) {
            return false;
        }
        this.bottomRightCorner.cornerSize = f;
        return true;
    }

    private boolean setBottomLeftCornerSizeInternal(float f) {
        if (this.bottomLeftCorner.cornerSize == f) {
            return false;
        }
        this.bottomLeftCorner.cornerSize = f;
        return true;
    }

    public void setAllEdges(EdgeTreatment edgeTreatment) {
        if (setBottomEdgeInternal(edgeTreatment.m597clone()) || ((setLeftEdgeInternal(edgeTreatment.m597clone()) | setTopEdgeInternal(edgeTreatment.m597clone())) | setRightEdgeInternal(edgeTreatment.m597clone()))) {
            onShapeAppearanceModelChanged();
        }
    }

    public void setCornerTreatments(CornerTreatment cornerTreatment, CornerTreatment cornerTreatment2, CornerTreatment cornerTreatment3, CornerTreatment cornerTreatment4) {
        if ((setTopLeftCornerInternal(cornerTreatment) | setTopRightCornerInternal(cornerTreatment2) | setBottomRightCornerInternal(cornerTreatment3)) || setBottomLeftCornerInternal(cornerTreatment4)) {
            onShapeAppearanceModelChanged();
        }
    }

    public void setEdgeTreatments(EdgeTreatment edgeTreatment, EdgeTreatment edgeTreatment2, EdgeTreatment edgeTreatment3, EdgeTreatment edgeTreatment4) {
        if ((setLeftEdgeInternal(edgeTreatment) | setTopEdgeInternal(edgeTreatment2) | setRightEdgeInternal(edgeTreatment3)) || setBottomEdgeInternal(edgeTreatment4)) {
            onShapeAppearanceModelChanged();
        }
    }

    public void setTopLeftCorner(int i, int i2) {
        setTopLeftCorner(MaterialShapeUtils.createCornerTreatment(i, i2));
    }

    public void setTopLeftCorner(CornerTreatment cornerTreatment) {
        if (setTopLeftCornerInternal(cornerTreatment)) {
            onShapeAppearanceModelChanged();
        }
    }

    private boolean setTopLeftCornerInternal(CornerTreatment cornerTreatment) {
        if (this.topLeftCorner == cornerTreatment) {
            return false;
        }
        this.topLeftCorner = cornerTreatment;
        return true;
    }

    public CornerTreatment getTopLeftCorner() {
        return this.topLeftCorner;
    }

    public void setTopRightCorner(int i, int i2) {
        setTopRightCorner(MaterialShapeUtils.createCornerTreatment(i, i2));
    }

    public void setTopRightCorner(CornerTreatment cornerTreatment) {
        if (setTopRightCornerInternal(cornerTreatment)) {
            onShapeAppearanceModelChanged();
        }
    }

    private boolean setTopRightCornerInternal(CornerTreatment cornerTreatment) {
        if (this.topRightCorner == cornerTreatment) {
            return false;
        }
        this.topRightCorner = cornerTreatment;
        return true;
    }

    public CornerTreatment getTopRightCorner() {
        return this.topRightCorner;
    }

    public void setBottomRightCorner(int i, int i2) {
        setBottomRightCorner(MaterialShapeUtils.createCornerTreatment(i, i2));
    }

    public void setBottomRightCorner(CornerTreatment cornerTreatment) {
        if (setBottomRightCornerInternal(cornerTreatment)) {
            onShapeAppearanceModelChanged();
        }
    }

    private boolean setBottomRightCornerInternal(CornerTreatment cornerTreatment) {
        if (this.bottomRightCorner == cornerTreatment) {
            return false;
        }
        this.bottomRightCorner = cornerTreatment;
        return true;
    }

    public CornerTreatment getBottomRightCorner() {
        return this.bottomRightCorner;
    }

    public void setBottomLeftCorner(int i, int i2) {
        setBottomLeftCorner(MaterialShapeUtils.createCornerTreatment(i, i2));
    }

    public void setBottomLeftCorner(CornerTreatment cornerTreatment) {
        if (setBottomLeftCornerInternal(cornerTreatment)) {
            onShapeAppearanceModelChanged();
        }
    }

    private boolean setBottomLeftCornerInternal(CornerTreatment cornerTreatment) {
        if (this.bottomLeftCorner == cornerTreatment) {
            return false;
        }
        this.bottomLeftCorner = cornerTreatment;
        return true;
    }

    public CornerTreatment getBottomLeftCorner() {
        return this.bottomLeftCorner;
    }

    public void setLeftEdge(EdgeTreatment edgeTreatment) {
        if (setLeftEdgeInternal(edgeTreatment)) {
            onShapeAppearanceModelChanged();
        }
    }

    private boolean setLeftEdgeInternal(EdgeTreatment edgeTreatment) {
        if (this.leftEdge == edgeTreatment) {
            return false;
        }
        this.leftEdge = edgeTreatment;
        return true;
    }

    public EdgeTreatment getLeftEdge() {
        return this.leftEdge;
    }

    public void setTopEdge(EdgeTreatment edgeTreatment) {
        if (setTopEdgeInternal(edgeTreatment)) {
            onShapeAppearanceModelChanged();
        }
    }

    private boolean setTopEdgeInternal(EdgeTreatment edgeTreatment) {
        if (this.topEdge == edgeTreatment) {
            return false;
        }
        this.topEdge = edgeTreatment;
        return true;
    }

    public EdgeTreatment getTopEdge() {
        return this.topEdge;
    }

    public void setRightEdge(EdgeTreatment edgeTreatment) {
        if (setRightEdgeInternal(edgeTreatment)) {
            onShapeAppearanceModelChanged();
        }
    }

    private boolean setRightEdgeInternal(EdgeTreatment edgeTreatment) {
        if (this.rightEdge == edgeTreatment) {
            return false;
        }
        this.rightEdge = edgeTreatment;
        return true;
    }

    public EdgeTreatment getRightEdge() {
        return this.rightEdge;
    }

    public void setBottomEdge(EdgeTreatment edgeTreatment) {
        if (setBottomEdgeInternal(edgeTreatment)) {
            onShapeAppearanceModelChanged();
        }
    }

    private boolean setBottomEdgeInternal(EdgeTreatment edgeTreatment) {
        if (this.bottomEdge == edgeTreatment) {
            return false;
        }
        this.bottomEdge = edgeTreatment;
        return true;
    }

    public EdgeTreatment getBottomEdge() {
        return this.bottomEdge;
    }

    void addOnChangedListener(OnChangedListener onChangedListener) {
        this.onChangedListeners.add(onChangedListener);
    }

    void removeOnChangedListener(OnChangedListener onChangedListener) {
        this.onChangedListeners.remove(onChangedListener);
    }

    public boolean isUsingPillCorner() {
        return getTopRightCorner().getCornerSize() == -1.0f && getTopLeftCorner().getCornerSize() == -1.0f && getBottomLeftCorner().getCornerSize() == -1.0f && getBottomRightCorner().getCornerSize() == -1.0f;
    }

    private void onShapeAppearanceModelChanged() {
        for (OnChangedListener onChangedListener : this.onChangedListeners) {
            if (onChangedListener != null) {
                onChangedListener.onShapeAppearanceModelChanged();
            }
        }
    }

    public boolean isRoundRect() {
        boolean z = this.leftEdge.getClass().equals(EdgeTreatment.class) && this.rightEdge.getClass().equals(EdgeTreatment.class) && this.topEdge.getClass().equals(EdgeTreatment.class) && this.bottomEdge.getClass().equals(EdgeTreatment.class);
        float cornerSize = this.topLeftCorner.getCornerSize();
        return z && ((this.topRightCorner.getCornerSize() > cornerSize ? 1 : (this.topRightCorner.getCornerSize() == cornerSize ? 0 : -1)) == 0 && (this.bottomLeftCorner.getCornerSize() > cornerSize ? 1 : (this.bottomLeftCorner.getCornerSize() == cornerSize ? 0 : -1)) == 0 && (this.bottomRightCorner.getCornerSize() > cornerSize ? 1 : (this.bottomRightCorner.getCornerSize() == cornerSize ? 0 : -1)) == 0) && ((this.topRightCorner instanceof RoundedCornerTreatment) && (this.topLeftCorner instanceof RoundedCornerTreatment) && (this.bottomRightCorner instanceof RoundedCornerTreatment) && (this.bottomLeftCorner instanceof RoundedCornerTreatment));
    }
}
