package com.google.android.material.card;

import android.R;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.resources.MaterialResources;
import com.google.android.material.ripple.RippleUtils;
import com.google.android.material.shape.CornerTreatment;
import com.google.android.material.shape.CutCornerTreatment;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.RoundedCornerTreatment;
import com.google.android.material.shape.ShapeAppearanceModel;

/* JADX INFO: loaded from: classes.dex */
class MaterialCardViewHelper {
    private static final float CARD_VIEW_SHADOW_MULTIPLIER = 1.5f;
    private static final int CHECKED_ICON_LAYER_INDEX = 2;
    private static final int[] CHECKED_STATE_SET = {R.attr.state_checked};
    private static final double COS_45 = Math.cos(Math.toRadians(45.0d));
    private static final int DEFAULT_STROKE_VALUE = -1;
    private final MaterialShapeDrawable bgDrawable;
    private boolean checkable;
    private Drawable checkedIcon;
    private ColorStateList checkedIconTint;
    private LayerDrawable clickableForegroundDrawable;
    private MaterialShapeDrawable compatRippleDrawable;
    private final MaterialShapeDrawable drawableInsetByStroke;
    private Drawable fgDrawable;
    private final MaterialShapeDrawable foregroundContentDrawable;
    private final MaterialCardView materialCardView;
    private ColorStateList rippleColor;
    private Drawable rippleDrawable;
    private final ShapeAppearanceModel shapeAppearanceModel;
    private final ShapeAppearanceModel shapeAppearanceModelInsetByStroke;
    private ColorStateList strokeColor;
    private int strokeWidth;
    private final Rect userContentPadding = new Rect();
    private final Rect temporaryBounds = new Rect();
    private boolean isBackgroundOverwritten = false;

    public MaterialCardViewHelper(MaterialCardView materialCardView, AttributeSet attributeSet, int i, int i2) {
        this.materialCardView = materialCardView;
        MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable(materialCardView.getContext(), attributeSet, i, i2);
        this.bgDrawable = materialShapeDrawable;
        materialShapeDrawable.initializeElevationOverlay(materialCardView.getContext());
        ShapeAppearanceModel shapeAppearanceModel = materialShapeDrawable.getShapeAppearanceModel();
        this.shapeAppearanceModel = shapeAppearanceModel;
        materialShapeDrawable.setShadowColor(-12303292);
        this.foregroundContentDrawable = new MaterialShapeDrawable(shapeAppearanceModel);
        TypedArray typedArrayObtainStyledAttributes = materialCardView.getContext().obtainStyledAttributes(attributeSet, com.google.android.material.R.styleable.CardView, i, com.google.android.material.R.style.CardView);
        if (typedArrayObtainStyledAttributes.hasValue(com.google.android.material.R.styleable.CardView_cardCornerRadius)) {
            shapeAppearanceModel.setCornerRadius(typedArrayObtainStyledAttributes.getDimension(com.google.android.material.R.styleable.CardView_cardCornerRadius, 0.0f));
        }
        ShapeAppearanceModel shapeAppearanceModel2 = new ShapeAppearanceModel(shapeAppearanceModel);
        this.shapeAppearanceModelInsetByStroke = shapeAppearanceModel2;
        this.drawableInsetByStroke = new MaterialShapeDrawable(shapeAppearanceModel2);
    }

    void loadFromAttributes(TypedArray typedArray) {
        ColorStateList colorStateList = MaterialResources.getColorStateList(this.materialCardView.getContext(), typedArray, com.google.android.material.R.styleable.MaterialCardView_strokeColor);
        this.strokeColor = colorStateList;
        if (colorStateList == null) {
            this.strokeColor = ColorStateList.valueOf(-1);
        }
        this.strokeWidth = typedArray.getDimensionPixelSize(com.google.android.material.R.styleable.MaterialCardView_strokeWidth, 0);
        boolean z = typedArray.getBoolean(com.google.android.material.R.styleable.MaterialCardView_android_checkable, false);
        this.checkable = z;
        this.materialCardView.setLongClickable(z);
        this.checkedIconTint = MaterialResources.getColorStateList(this.materialCardView.getContext(), typedArray, com.google.android.material.R.styleable.MaterialCardView_checkedIconTint);
        setCheckedIcon(MaterialResources.getDrawable(this.materialCardView.getContext(), typedArray, com.google.android.material.R.styleable.MaterialCardView_checkedIcon));
        ColorStateList colorStateList2 = MaterialResources.getColorStateList(this.materialCardView.getContext(), typedArray, com.google.android.material.R.styleable.MaterialCardView_rippleColor);
        this.rippleColor = colorStateList2;
        if (colorStateList2 == null) {
            this.rippleColor = ColorStateList.valueOf(MaterialColors.getColor(this.materialCardView, com.google.android.material.R.attr.colorControlHighlight));
        }
        adjustShapeAppearanceModelInsetByStroke();
        ColorStateList colorStateList3 = MaterialResources.getColorStateList(this.materialCardView.getContext(), typedArray, com.google.android.material.R.styleable.MaterialCardView_cardForegroundColor);
        MaterialShapeDrawable materialShapeDrawable = this.foregroundContentDrawable;
        if (colorStateList3 == null) {
            colorStateList3 = ColorStateList.valueOf(0);
        }
        materialShapeDrawable.setFillColor(colorStateList3);
        updateRippleColor();
        updateElevation();
        updateStroke();
        this.materialCardView.setBackgroundInternal(insetDrawable(this.bgDrawable));
        Drawable clickableForeground = this.materialCardView.isClickable() ? getClickableForeground() : this.foregroundContentDrawable;
        this.fgDrawable = clickableForeground;
        this.materialCardView.setForeground(insetDrawable(clickableForeground));
    }

    boolean isBackgroundOverwritten() {
        return this.isBackgroundOverwritten;
    }

    void setBackgroundOverwritten(boolean z) {
        this.isBackgroundOverwritten = z;
    }

    void setStrokeColor(ColorStateList colorStateList) {
        if (this.strokeColor == colorStateList) {
            return;
        }
        this.strokeColor = colorStateList;
        updateStroke();
    }

    int getStrokeColor() {
        ColorStateList colorStateList = this.strokeColor;
        if (colorStateList == null) {
            return -1;
        }
        return colorStateList.getDefaultColor();
    }

    ColorStateList getStrokeColorStateList() {
        return this.strokeColor;
    }

    void setStrokeWidth(int i) {
        if (i == this.strokeWidth) {
            return;
        }
        this.strokeWidth = i;
        adjustShapeAppearanceModelInsetByStroke();
        updateStroke();
    }

    int getStrokeWidth() {
        return this.strokeWidth;
    }

    void setCardBackgroundColor(ColorStateList colorStateList) {
        this.bgDrawable.setFillColor(colorStateList);
    }

    ColorStateList getCardBackgroundColor() {
        return this.bgDrawable.getFillColor();
    }

    void setUserContentPadding(int i, int i2, int i3, int i4) {
        this.userContentPadding.set(i, i2, i3, i4);
        updateContentPadding();
    }

    Rect getUserContentPadding() {
        return this.userContentPadding;
    }

    void updateClickable() {
        Drawable drawable = this.fgDrawable;
        Drawable clickableForeground = this.materialCardView.isClickable() ? getClickableForeground() : this.foregroundContentDrawable;
        this.fgDrawable = clickableForeground;
        if (drawable != clickableForeground) {
            updateInsetForeground(clickableForeground);
        }
    }

    void setCornerRadius(float f) {
        this.shapeAppearanceModel.setCornerRadius(f);
        this.shapeAppearanceModelInsetByStroke.setCornerRadius(f - this.strokeWidth);
        this.bgDrawable.invalidateSelf();
        this.fgDrawable.invalidateSelf();
        if (shouldAddCornerPaddingOutsideCardBackground() || shouldAddCornerPaddingInsideCardBackground()) {
            updateContentPadding();
        }
        if (shouldAddCornerPaddingOutsideCardBackground()) {
            updateInsets();
        }
    }

    float getCornerRadius() {
        return this.shapeAppearanceModel.getTopLeftCorner().getCornerSize();
    }

    void updateElevation() {
        this.bgDrawable.setElevation(this.materialCardView.getCardElevation());
    }

    void updateInsets() {
        if (!isBackgroundOverwritten()) {
            this.materialCardView.setBackgroundInternal(insetDrawable(this.bgDrawable));
        }
        this.materialCardView.setForeground(insetDrawable(this.fgDrawable));
    }

    void updateStroke() {
        this.foregroundContentDrawable.setStroke(this.strokeWidth, this.strokeColor);
    }

    void createOutlineProvider(View view) {
        if (view == null) {
            return;
        }
        this.materialCardView.setClipToOutline(false);
        if (canClipToOutline()) {
            view.setClipToOutline(true);
            view.setOutlineProvider(new ViewOutlineProvider() { // from class: com.google.android.material.card.MaterialCardViewHelper.1
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view2, Outline outline) {
                    MaterialCardViewHelper.this.temporaryBounds.set(MaterialCardViewHelper.this.strokeWidth, MaterialCardViewHelper.this.strokeWidth, view2.getWidth() - MaterialCardViewHelper.this.strokeWidth, view2.getHeight() - MaterialCardViewHelper.this.strokeWidth);
                    MaterialCardViewHelper.this.drawableInsetByStroke.setBounds(MaterialCardViewHelper.this.temporaryBounds);
                    MaterialCardViewHelper.this.drawableInsetByStroke.getOutline(outline);
                }
            });
        } else {
            view.setClipToOutline(false);
            view.setOutlineProvider(null);
        }
    }

    void updateContentPadding() {
        int iCalculateActualCornerPadding = (int) ((shouldAddCornerPaddingInsideCardBackground() || shouldAddCornerPaddingOutsideCardBackground() ? calculateActualCornerPadding() : 0.0f) - getParentCardViewCalculatedCornerPadding());
        this.materialCardView.setContentLayoutPadding(this.userContentPadding.left + iCalculateActualCornerPadding, this.userContentPadding.top + iCalculateActualCornerPadding, this.userContentPadding.right + iCalculateActualCornerPadding, this.userContentPadding.bottom + iCalculateActualCornerPadding);
    }

    void setCheckable(boolean z) {
        this.checkable = z;
    }

    boolean isCheckable() {
        return this.checkable;
    }

    void setRippleColor(ColorStateList colorStateList) {
        this.rippleColor = colorStateList;
        updateRippleColor();
    }

    void setCheckedIconTint(ColorStateList colorStateList) {
        this.checkedIconTint = colorStateList;
        Drawable drawable = this.checkedIcon;
        if (drawable != null) {
            DrawableCompat.setTintList(drawable, colorStateList);
        }
    }

    ColorStateList getCheckedIconTint() {
        return this.checkedIconTint;
    }

    ColorStateList getRippleColor() {
        return this.rippleColor;
    }

    Drawable getCheckedIcon() {
        return this.checkedIcon;
    }

    void setCheckedIcon(Drawable drawable) {
        this.checkedIcon = drawable;
        if (drawable != null) {
            Drawable drawableWrap = DrawableCompat.wrap(drawable.mutate());
            this.checkedIcon = drawableWrap;
            DrawableCompat.setTintList(drawableWrap, this.checkedIconTint);
        }
        if (this.clickableForegroundDrawable != null) {
            this.clickableForegroundDrawable.setDrawableByLayerId(com.google.android.material.R.id.mtrl_card_checked_layer_id, createCheckedIconLayer());
        }
    }

    void onMeasure(int i, int i2) {
        int i3;
        int i4;
        if (!this.materialCardView.isCheckable() || this.clickableForegroundDrawable == null) {
            return;
        }
        Resources resources = this.materialCardView.getResources();
        int dimensionPixelSize = resources.getDimensionPixelSize(com.google.android.material.R.dimen.mtrl_card_checked_icon_margin);
        int dimensionPixelSize2 = resources.getDimensionPixelSize(com.google.android.material.R.dimen.mtrl_card_checked_icon_size);
        int i5 = (i - dimensionPixelSize) - dimensionPixelSize2;
        int i6 = (i2 - dimensionPixelSize) - dimensionPixelSize2;
        if (ViewCompat.getLayoutDirection(this.materialCardView) == 1) {
            i4 = i5;
            i3 = dimensionPixelSize;
        } else {
            i3 = i5;
            i4 = dimensionPixelSize;
        }
        this.clickableForegroundDrawable.setLayerInset(2, i3, dimensionPixelSize, i4, i6);
    }

    void forceRippleRedraw() {
        Drawable drawable = this.rippleDrawable;
        if (drawable != null) {
            Rect bounds = drawable.getBounds();
            int i = bounds.bottom;
            this.rippleDrawable.setBounds(bounds.left, bounds.top, bounds.right, i - 1);
            this.rippleDrawable.setBounds(bounds.left, bounds.top, bounds.right, i);
        }
    }

    private void adjustShapeAppearanceModelInsetByStroke() {
        this.shapeAppearanceModelInsetByStroke.getTopLeftCorner().setCornerSize(this.shapeAppearanceModel.getTopLeftCorner().getCornerSize() - this.strokeWidth);
        this.shapeAppearanceModelInsetByStroke.getTopRightCorner().setCornerSize(this.shapeAppearanceModel.getTopRightCorner().getCornerSize() - this.strokeWidth);
        this.shapeAppearanceModelInsetByStroke.getBottomRightCorner().setCornerSize(this.shapeAppearanceModel.getBottomRightCorner().getCornerSize() - this.strokeWidth);
        this.shapeAppearanceModelInsetByStroke.getBottomLeftCorner().setCornerSize(this.shapeAppearanceModel.getBottomLeftCorner().getCornerSize() - this.strokeWidth);
    }

    private void updateInsetForeground(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 23 && (this.materialCardView.getForeground() instanceof InsetDrawable)) {
            ((InsetDrawable) this.materialCardView.getForeground()).setDrawable(drawable);
        } else {
            this.materialCardView.setForeground(insetDrawable(drawable));
        }
    }

    private Drawable insetDrawable(Drawable drawable) {
        int iCeil;
        int i;
        if ((Build.VERSION.SDK_INT < 21) || this.materialCardView.getUseCompatPadding()) {
            int iCeil2 = (int) Math.ceil(calculateVerticalBackgroundPadding());
            iCeil = (int) Math.ceil(calculateHorizontalBackgroundPadding());
            i = iCeil2;
        } else {
            iCeil = 0;
            i = 0;
        }
        return new InsetDrawable(drawable, iCeil, i, iCeil, i) { // from class: com.google.android.material.card.MaterialCardViewHelper.2
            @Override // android.graphics.drawable.InsetDrawable, android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
            public boolean getPadding(Rect rect) {
                return false;
            }
        };
    }

    private float calculateVerticalBackgroundPadding() {
        return (this.materialCardView.getMaxCardElevation() * CARD_VIEW_SHADOW_MULTIPLIER) + (shouldAddCornerPaddingOutsideCardBackground() ? calculateActualCornerPadding() : 0.0f);
    }

    private float calculateHorizontalBackgroundPadding() {
        return this.materialCardView.getMaxCardElevation() + (shouldAddCornerPaddingOutsideCardBackground() ? calculateActualCornerPadding() : 0.0f);
    }

    private boolean canClipToOutline() {
        return Build.VERSION.SDK_INT >= 21 && this.shapeAppearanceModel.isRoundRect();
    }

    private float getParentCardViewCalculatedCornerPadding() {
        if (!this.materialCardView.getPreventCornerOverlap()) {
            return 0.0f;
        }
        if (Build.VERSION.SDK_INT < 21 || this.materialCardView.getUseCompatPadding()) {
            return (float) ((1.0d - COS_45) * ((double) this.materialCardView.getCardViewRadius()));
        }
        return 0.0f;
    }

    private boolean shouldAddCornerPaddingInsideCardBackground() {
        return this.materialCardView.getPreventCornerOverlap() && !canClipToOutline();
    }

    private boolean shouldAddCornerPaddingOutsideCardBackground() {
        return this.materialCardView.getPreventCornerOverlap() && canClipToOutline() && this.materialCardView.getUseCompatPadding();
    }

    private float calculateActualCornerPadding() {
        return Math.max(Math.max(calculateCornerPaddingForCornerTreatment(this.shapeAppearanceModel.getTopLeftCorner()), calculateCornerPaddingForCornerTreatment(this.shapeAppearanceModel.getTopRightCorner())), Math.max(calculateCornerPaddingForCornerTreatment(this.shapeAppearanceModel.getBottomRightCorner()), calculateCornerPaddingForCornerTreatment(this.shapeAppearanceModel.getBottomLeftCorner())));
    }

    private float calculateCornerPaddingForCornerTreatment(CornerTreatment cornerTreatment) {
        if (cornerTreatment instanceof RoundedCornerTreatment) {
            return (float) ((1.0d - COS_45) * ((double) cornerTreatment.getCornerSize()));
        }
        if (cornerTreatment instanceof CutCornerTreatment) {
            return cornerTreatment.getCornerSize() / 2.0f;
        }
        return 0.0f;
    }

    private Drawable getClickableForeground() {
        if (this.rippleDrawable == null) {
            this.rippleDrawable = createForegroundRippleDrawable();
        }
        if (this.clickableForegroundDrawable == null) {
            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{this.rippleDrawable, this.foregroundContentDrawable, createCheckedIconLayer()});
            this.clickableForegroundDrawable = layerDrawable;
            layerDrawable.setId(2, com.google.android.material.R.id.mtrl_card_checked_layer_id);
        }
        return this.clickableForegroundDrawable;
    }

    private Drawable createForegroundRippleDrawable() {
        if (RippleUtils.USE_FRAMEWORK_RIPPLE) {
            return new RippleDrawable(this.rippleColor, null, createForegroundShapeDrawable());
        }
        return createCompatRippleDrawable();
    }

    private Drawable createCompatRippleDrawable() {
        StateListDrawable stateListDrawable = new StateListDrawable();
        MaterialShapeDrawable materialShapeDrawableCreateForegroundShapeDrawable = createForegroundShapeDrawable();
        this.compatRippleDrawable = materialShapeDrawableCreateForegroundShapeDrawable;
        materialShapeDrawableCreateForegroundShapeDrawable.setFillColor(this.rippleColor);
        stateListDrawable.addState(new int[]{R.attr.state_pressed}, this.compatRippleDrawable);
        return stateListDrawable;
    }

    private void updateRippleColor() {
        Drawable drawable;
        if (RippleUtils.USE_FRAMEWORK_RIPPLE && (drawable = this.rippleDrawable) != null) {
            ((RippleDrawable) drawable).setColor(this.rippleColor);
            return;
        }
        MaterialShapeDrawable materialShapeDrawable = this.compatRippleDrawable;
        if (materialShapeDrawable != null) {
            materialShapeDrawable.setFillColor(this.rippleColor);
        }
    }

    private Drawable createCheckedIconLayer() {
        StateListDrawable stateListDrawable = new StateListDrawable();
        Drawable drawable = this.checkedIcon;
        if (drawable != null) {
            stateListDrawable.addState(CHECKED_STATE_SET, drawable);
        }
        return stateListDrawable;
    }

    private MaterialShapeDrawable createForegroundShapeDrawable() {
        return new MaterialShapeDrawable(this.shapeAppearanceModel);
    }
}
