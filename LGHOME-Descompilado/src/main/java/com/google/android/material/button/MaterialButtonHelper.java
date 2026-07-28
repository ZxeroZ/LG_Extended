package com.google.android.material.button;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import com.google.android.material.R;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.internal.ViewUtils;
import com.google.android.material.resources.MaterialResources;
import com.google.android.material.ripple.RippleUtils;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

/* JADX INFO: loaded from: classes.dex */
class MaterialButtonHelper {
    private static final boolean IS_LOLLIPOP;
    private ColorStateList backgroundTint;
    private PorterDuff.Mode backgroundTintMode;
    private boolean checkable;
    private int cornerRadius;
    private int insetBottom;
    private int insetLeft;
    private int insetRight;
    private int insetTop;
    private MaterialShapeDrawable maskDrawable;
    private final MaterialButton materialButton;
    private ColorStateList rippleColor;
    private LayerDrawable rippleDrawable;
    private ShapeAppearanceModel shapeAppearanceModel;
    private ColorStateList strokeColor;
    private int strokeWidth;
    private boolean shouldDrawSurfaceColorStroke = false;
    private boolean backgroundOverwritten = false;
    private boolean cornerRadiusSet = false;

    static {
        IS_LOLLIPOP = Build.VERSION.SDK_INT >= 21;
    }

    MaterialButtonHelper(MaterialButton materialButton, ShapeAppearanceModel shapeAppearanceModel) {
        this.materialButton = materialButton;
        this.shapeAppearanceModel = shapeAppearanceModel;
    }

    void loadFromAttributes(TypedArray typedArray) {
        this.insetLeft = typedArray.getDimensionPixelOffset(R.styleable.MaterialButton_android_insetLeft, 0);
        this.insetRight = typedArray.getDimensionPixelOffset(R.styleable.MaterialButton_android_insetRight, 0);
        this.insetTop = typedArray.getDimensionPixelOffset(R.styleable.MaterialButton_android_insetTop, 0);
        this.insetBottom = typedArray.getDimensionPixelOffset(R.styleable.MaterialButton_android_insetBottom, 0);
        if (typedArray.hasValue(R.styleable.MaterialButton_cornerRadius)) {
            int dimensionPixelSize = typedArray.getDimensionPixelSize(R.styleable.MaterialButton_cornerRadius, -1);
            this.cornerRadius = dimensionPixelSize;
            this.shapeAppearanceModel.setCornerRadius(dimensionPixelSize);
            this.cornerRadiusSet = true;
        }
        this.strokeWidth = typedArray.getDimensionPixelSize(R.styleable.MaterialButton_strokeWidth, 0);
        this.backgroundTintMode = ViewUtils.parseTintMode(typedArray.getInt(R.styleable.MaterialButton_backgroundTintMode, -1), PorterDuff.Mode.SRC_IN);
        this.backgroundTint = MaterialResources.getColorStateList(this.materialButton.getContext(), typedArray, R.styleable.MaterialButton_backgroundTint);
        this.strokeColor = MaterialResources.getColorStateList(this.materialButton.getContext(), typedArray, R.styleable.MaterialButton_strokeColor);
        this.rippleColor = MaterialResources.getColorStateList(this.materialButton.getContext(), typedArray, R.styleable.MaterialButton_rippleColor);
        this.checkable = typedArray.getBoolean(R.styleable.MaterialButton_android_checkable, false);
        int dimensionPixelSize2 = typedArray.getDimensionPixelSize(R.styleable.MaterialButton_elevation, 0);
        int paddingStart = ViewCompat.getPaddingStart(this.materialButton);
        int paddingTop = this.materialButton.getPaddingTop();
        int paddingEnd = ViewCompat.getPaddingEnd(this.materialButton);
        int paddingBottom = this.materialButton.getPaddingBottom();
        this.materialButton.setInternalBackground(createBackground());
        MaterialShapeDrawable materialShapeDrawable = getMaterialShapeDrawable();
        if (materialShapeDrawable != null) {
            materialShapeDrawable.setElevation(dimensionPixelSize2);
        }
        ViewCompat.setPaddingRelative(this.materialButton, paddingStart + this.insetLeft, paddingTop + this.insetTop, paddingEnd + this.insetRight, paddingBottom + this.insetBottom);
    }

    void setBackgroundOverwritten() {
        this.backgroundOverwritten = true;
        this.materialButton.setSupportBackgroundTintList(this.backgroundTint);
        this.materialButton.setSupportBackgroundTintMode(this.backgroundTintMode);
    }

    boolean isBackgroundOverwritten() {
        return this.backgroundOverwritten;
    }

    private InsetDrawable wrapDrawableWithInset(Drawable drawable) {
        return new InsetDrawable(drawable, this.insetLeft, this.insetTop, this.insetRight, this.insetBottom);
    }

    void setSupportBackgroundTintList(ColorStateList colorStateList) {
        if (this.backgroundTint != colorStateList) {
            this.backgroundTint = colorStateList;
            if (getMaterialShapeDrawable() != null) {
                DrawableCompat.setTintList(getMaterialShapeDrawable(), this.backgroundTint);
            }
        }
    }

    ColorStateList getSupportBackgroundTintList() {
        return this.backgroundTint;
    }

    void setSupportBackgroundTintMode(PorterDuff.Mode mode) {
        if (this.backgroundTintMode != mode) {
            this.backgroundTintMode = mode;
            if (getMaterialShapeDrawable() == null || this.backgroundTintMode == null) {
                return;
            }
            DrawableCompat.setTintMode(getMaterialShapeDrawable(), this.backgroundTintMode);
        }
    }

    PorterDuff.Mode getSupportBackgroundTintMode() {
        return this.backgroundTintMode;
    }

    void setShouldDrawSurfaceColorStroke(boolean z) {
        this.shouldDrawSurfaceColorStroke = z;
        updateStroke();
    }

    private Drawable createBackground() {
        MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable(this.shapeAppearanceModel);
        materialShapeDrawable.initializeElevationOverlay(this.materialButton.getContext());
        DrawableCompat.setTintList(materialShapeDrawable, this.backgroundTint);
        PorterDuff.Mode mode = this.backgroundTintMode;
        if (mode != null) {
            DrawableCompat.setTintMode(materialShapeDrawable, mode);
        }
        materialShapeDrawable.setStroke(this.strokeWidth, this.strokeColor);
        MaterialShapeDrawable materialShapeDrawable2 = new MaterialShapeDrawable(this.shapeAppearanceModel);
        materialShapeDrawable2.setTint(0);
        materialShapeDrawable2.setStroke(this.strokeWidth, this.shouldDrawSurfaceColorStroke ? MaterialColors.getColor(this.materialButton, R.attr.colorSurface) : 0);
        MaterialShapeDrawable materialShapeDrawable3 = new MaterialShapeDrawable(this.shapeAppearanceModel);
        this.maskDrawable = materialShapeDrawable3;
        if (IS_LOLLIPOP) {
            if (this.strokeWidth > 0) {
                ShapeAppearanceModel shapeAppearanceModel = new ShapeAppearanceModel(this.shapeAppearanceModel);
                adjustShapeAppearanceModelCornerRadius(shapeAppearanceModel, this.strokeWidth / 2.0f);
                materialShapeDrawable.setShapeAppearanceModel(shapeAppearanceModel);
                materialShapeDrawable2.setShapeAppearanceModel(shapeAppearanceModel);
                this.maskDrawable.setShapeAppearanceModel(shapeAppearanceModel);
            }
            DrawableCompat.setTint(this.maskDrawable, -1);
            RippleDrawable rippleDrawable = new RippleDrawable(RippleUtils.convertToRippleDrawableColor(this.rippleColor), wrapDrawableWithInset(new LayerDrawable(new Drawable[]{materialShapeDrawable2, materialShapeDrawable})), this.maskDrawable);
            this.rippleDrawable = rippleDrawable;
            return rippleDrawable;
        }
        DrawableCompat.setTintList(materialShapeDrawable3, RippleUtils.convertToRippleDrawableColor(this.rippleColor));
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{materialShapeDrawable2, materialShapeDrawable, this.maskDrawable});
        this.rippleDrawable = layerDrawable;
        return wrapDrawableWithInset(layerDrawable);
    }

    void updateMaskBounds(int i, int i2) {
        MaterialShapeDrawable materialShapeDrawable = this.maskDrawable;
        if (materialShapeDrawable != null) {
            materialShapeDrawable.setBounds(this.insetLeft, this.insetTop, i2 - this.insetRight, i - this.insetBottom);
        }
    }

    void setBackgroundColor(int i) {
        if (getMaterialShapeDrawable() != null) {
            getMaterialShapeDrawable().setTint(i);
        }
    }

    void setRippleColor(ColorStateList colorStateList) {
        if (this.rippleColor != colorStateList) {
            this.rippleColor = colorStateList;
            boolean z = IS_LOLLIPOP;
            if (z && (this.materialButton.getBackground() instanceof RippleDrawable)) {
                ((RippleDrawable) this.materialButton.getBackground()).setColor(RippleUtils.convertToRippleDrawableColor(colorStateList));
            } else {
                if (z || getMaskDrawable() == null) {
                    return;
                }
                DrawableCompat.setTintList(getMaskDrawable(), RippleUtils.convertToRippleDrawableColor(colorStateList));
            }
        }
    }

    ColorStateList getRippleColor() {
        return this.rippleColor;
    }

    void setStrokeColor(ColorStateList colorStateList) {
        if (this.strokeColor != colorStateList) {
            this.strokeColor = colorStateList;
            updateStroke();
        }
    }

    ColorStateList getStrokeColor() {
        return this.strokeColor;
    }

    void setStrokeWidth(int i) {
        if (this.strokeWidth != i) {
            this.strokeWidth = i;
            updateStroke();
        }
    }

    int getStrokeWidth() {
        return this.strokeWidth;
    }

    private void updateStroke() {
        MaterialShapeDrawable materialShapeDrawable = getMaterialShapeDrawable();
        MaterialShapeDrawable surfaceColorStrokeDrawable = getSurfaceColorStrokeDrawable();
        if (materialShapeDrawable != null) {
            materialShapeDrawable.setStroke(this.strokeWidth, this.strokeColor);
            if (surfaceColorStrokeDrawable != null) {
                surfaceColorStrokeDrawable.setStroke(this.strokeWidth, this.shouldDrawSurfaceColorStroke ? MaterialColors.getColor(this.materialButton, R.attr.colorSurface) : 0);
            }
            if (IS_LOLLIPOP) {
                ShapeAppearanceModel shapeAppearanceModel = new ShapeAppearanceModel(this.shapeAppearanceModel);
                adjustShapeAppearanceModelCornerRadius(shapeAppearanceModel, this.strokeWidth / 2.0f);
                updateButtonShape(shapeAppearanceModel);
                MaterialShapeDrawable materialShapeDrawable2 = this.maskDrawable;
                if (materialShapeDrawable2 != null) {
                    materialShapeDrawable2.setShapeAppearanceModel(shapeAppearanceModel);
                }
            }
        }
    }

    void setCornerRadius(int i) {
        if (this.cornerRadiusSet && this.cornerRadius == i) {
            return;
        }
        this.cornerRadius = i;
        this.cornerRadiusSet = true;
        this.shapeAppearanceModel.setCornerRadius(i + (this.strokeWidth / 2.0f));
        updateButtonShape(this.shapeAppearanceModel);
    }

    int getCornerRadius() {
        return this.cornerRadius;
    }

    private void adjustShapeAppearanceModelCornerRadius(ShapeAppearanceModel shapeAppearanceModel, float f) {
        shapeAppearanceModel.getTopLeftCorner().setCornerSize(shapeAppearanceModel.getTopLeftCorner().getCornerSize() + f);
        shapeAppearanceModel.getTopRightCorner().setCornerSize(shapeAppearanceModel.getTopRightCorner().getCornerSize() + f);
        shapeAppearanceModel.getBottomRightCorner().setCornerSize(shapeAppearanceModel.getBottomRightCorner().getCornerSize() + f);
        shapeAppearanceModel.getBottomLeftCorner().setCornerSize(shapeAppearanceModel.getBottomLeftCorner().getCornerSize() + f);
    }

    private MaterialShapeDrawable getMaterialShapeDrawable(boolean z) {
        LayerDrawable layerDrawable = this.rippleDrawable;
        if (layerDrawable == null || layerDrawable.getNumberOfLayers() <= 0) {
            return null;
        }
        if (IS_LOLLIPOP) {
            return (MaterialShapeDrawable) ((LayerDrawable) ((InsetDrawable) this.rippleDrawable.getDrawable(0)).getDrawable()).getDrawable(!z ? 1 : 0);
        }
        return (MaterialShapeDrawable) this.rippleDrawable.getDrawable(!z ? 1 : 0);
    }

    MaterialShapeDrawable getMaterialShapeDrawable() {
        return getMaterialShapeDrawable(false);
    }

    void setCheckable(boolean z) {
        this.checkable = z;
    }

    boolean isCheckable() {
        return this.checkable;
    }

    private MaterialShapeDrawable getSurfaceColorStrokeDrawable() {
        return getMaterialShapeDrawable(true);
    }

    private void updateButtonShape(ShapeAppearanceModel shapeAppearanceModel) {
        if (getMaterialShapeDrawable() != null) {
            getMaterialShapeDrawable().setShapeAppearanceModel(shapeAppearanceModel);
        }
        if (getSurfaceColorStrokeDrawable() != null) {
            getSurfaceColorStrokeDrawable().setShapeAppearanceModel(shapeAppearanceModel);
        }
        if (getMaskDrawable() != null) {
            getMaskDrawable().setShapeAppearanceModel(shapeAppearanceModel);
        }
    }

    public MaterialShapeDrawable getMaskDrawable() {
        LayerDrawable layerDrawable = this.rippleDrawable;
        if (layerDrawable == null || layerDrawable.getNumberOfLayers() <= 1) {
            return null;
        }
        if (this.rippleDrawable.getNumberOfLayers() > 2) {
            return (MaterialShapeDrawable) this.rippleDrawable.getDrawable(2);
        }
        return (MaterialShapeDrawable) this.rippleDrawable.getDrawable(1);
    }

    void setShapeAppearanceModel(ShapeAppearanceModel shapeAppearanceModel) {
        this.shapeAppearanceModel = shapeAppearanceModel;
        updateButtonShape(shapeAppearanceModel);
    }

    ShapeAppearanceModel getShapeAppearanceModel() {
        return this.shapeAppearanceModel;
    }
}
