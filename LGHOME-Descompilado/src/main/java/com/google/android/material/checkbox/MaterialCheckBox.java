package com.google.android.material.checkbox;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.widget.CompoundButtonCompat;
import com.google.android.material.R;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.internal.ThemeEnforcement;

/* JADX INFO: loaded from: classes.dex */
public class MaterialCheckBox extends AppCompatCheckBox {
    private static final int DEF_STYLE_RES = R.style.Widget_MaterialComponents_CompoundButton_CheckBox;
    private static final int[][] ENABLED_CHECKED_STATES = {new int[]{android.R.attr.state_enabled, android.R.attr.state_checked}, new int[]{android.R.attr.state_enabled, -16842912}, new int[]{-16842910, android.R.attr.state_checked}, new int[]{-16842910, -16842912}};
    private ColorStateList materialThemeColorsTintList;

    public MaterialCheckBox(Context context) {
        this(context, null);
    }

    public MaterialCheckBox(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.checkboxStyle);
    }

    /* JADX WARN: Illegal instructions before constructor call */
    public MaterialCheckBox(Context context, AttributeSet attributeSet, int i) {
        int i2 = DEF_STYLE_RES;
        super(ThemeEnforcement.createThemedContext(context, attributeSet, i, i2), attributeSet, i);
        TypedArray typedArrayObtainStyledAttributes = ThemeEnforcement.obtainStyledAttributes(getContext(), attributeSet, R.styleable.MaterialCheckBox, i, i2, new int[0]);
        boolean z = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MaterialCheckBox_useMaterialThemeColors, false);
        typedArrayObtainStyledAttributes.recycle();
        if (z && CompoundButtonCompat.getButtonTintList(this) == null) {
            setUseMaterialThemeColors(true);
        }
    }

    public void setUseMaterialThemeColors(boolean z) {
        if (z) {
            CompoundButtonCompat.setButtonTintList(this, getMaterialThemeColorsTintList());
        } else {
            CompoundButtonCompat.setButtonTintList(this, null);
        }
    }

    public boolean isUseMaterialThemeColors() {
        ColorStateList colorStateList = this.materialThemeColorsTintList;
        return colorStateList != null && colorStateList.equals(CompoundButtonCompat.getButtonTintList(this));
    }

    private ColorStateList getMaterialThemeColorsTintList() {
        if (this.materialThemeColorsTintList == null) {
            int[][] iArr = ENABLED_CHECKED_STATES;
            int[] iArr2 = new int[iArr.length];
            int color = MaterialColors.getColor(this, R.attr.colorControlActivated);
            int color2 = MaterialColors.getColor(this, R.attr.colorSurface);
            int color3 = MaterialColors.getColor(this, R.attr.colorOnSurface);
            iArr2[0] = MaterialColors.layer(color2, color, 1.0f);
            iArr2[1] = MaterialColors.layer(color2, color3, 0.54f);
            iArr2[2] = MaterialColors.layer(color2, color3, 0.38f);
            iArr2[3] = MaterialColors.layer(color2, color3, 0.38f);
            this.materialThemeColorsTintList = new ColorStateList(iArr, iArr2);
        }
        return this.materialThemeColorsTintList;
    }
}
