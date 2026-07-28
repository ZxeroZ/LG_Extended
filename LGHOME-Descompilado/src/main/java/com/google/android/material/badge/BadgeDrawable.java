package com.google.android.material.badge;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.view.ViewCompat;
import com.google.android.material.R;
import com.google.android.material.internal.TextDrawableHelper;
import com.google.android.material.internal.ThemeEnforcement;
import com.google.android.material.resources.MaterialResources;
import com.google.android.material.resources.TextAppearance;
import com.google.android.material.shape.MaterialShapeDrawable;

/* JADX INFO: loaded from: classes.dex */
public class BadgeDrawable extends Drawable implements TextDrawableHelper.TextDrawableDelegate {
    private static final int BADGE_NUMBER_NONE = -1;
    static final String DEFAULT_EXCEED_MAX_BADGE_NUMBER_SUFFIX = "+";
    private static final int DEFAULT_MAX_BADGE_CHARACTER_COUNT = 4;
    private static final int MAX_CIRCULAR_BADGE_NUMBER_COUNT = 99;
    private final Rect badgeBounds;
    private float badgeCenterX;
    private float badgeCenterY;
    private final float badgeRadius;
    private final float badgeWidePadding;
    private final float badgeWithTextRadius;
    private final Context context;
    private int maxBadgeNumber;
    private final SavedState savedState;
    private final MaterialShapeDrawable shapeDrawable;
    private final TextDrawableHelper textDrawableHelper;
    private final Rect tmpRect;

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return false;
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    public static final class SavedState implements Parcelable {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.google.android.material.badge.BadgeDrawable.SavedState.1
            /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object; */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX DEBUG: Method merged with bridge method: newArray(I)[Ljava/lang/Object; */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        private int alpha;
        private int backgroundColor;
        private int badgeTextColor;
        private CharSequence contentDescriptionNumberless;
        private int contentDescriptionQuantityStrings;
        private int maxCharacterCount;
        private int number;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public SavedState(Context context) {
            this.alpha = 255;
            this.number = -1;
            this.badgeTextColor = new TextAppearance(context, R.style.TextAppearance_MaterialComponents_Badge).textColor.getDefaultColor();
            this.contentDescriptionNumberless = context.getString(R.string.mtrl_badge_numberless_content_description);
            this.contentDescriptionQuantityStrings = R.plurals.mtrl_badge_content_description;
        }

        protected SavedState(Parcel parcel) {
            this.alpha = 255;
            this.number = -1;
            this.backgroundColor = parcel.readInt();
            this.badgeTextColor = parcel.readInt();
            this.alpha = parcel.readInt();
            this.number = parcel.readInt();
            this.maxCharacterCount = parcel.readInt();
            this.contentDescriptionNumberless = parcel.readString();
            this.contentDescriptionQuantityStrings = parcel.readInt();
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.backgroundColor);
            parcel.writeInt(this.badgeTextColor);
            parcel.writeInt(this.alpha);
            parcel.writeInt(this.number);
            parcel.writeInt(this.maxCharacterCount);
            parcel.writeString(this.contentDescriptionNumberless.toString());
            parcel.writeInt(this.contentDescriptionQuantityStrings);
        }
    }

    public SavedState getSavedState() {
        return this.savedState;
    }

    public static BadgeDrawable createFromSavedState(Context context, SavedState savedState) {
        BadgeDrawable badgeDrawable = new BadgeDrawable(context);
        badgeDrawable.restoreFromSavedState(savedState);
        return badgeDrawable;
    }

    public static BadgeDrawable create(Context context) {
        return createFromAttributes(context, null, 0, R.style.Widget_MaterialComponents_Badge);
    }

    public static BadgeDrawable createFromAttributes(Context context, AttributeSet attributeSet, int i, int i2) {
        BadgeDrawable badgeDrawable = new BadgeDrawable(context);
        badgeDrawable.loadDefaultStateFromAttributes(context, attributeSet, i, i2);
        return badgeDrawable;
    }

    private void restoreFromSavedState(SavedState savedState) {
        setMaxCharacterCount(savedState.maxCharacterCount);
        if (savedState.number != -1) {
            setNumber(savedState.number);
        }
        setBackgroundColor(savedState.backgroundColor);
        setBadgeTextColor(savedState.badgeTextColor);
    }

    private void loadDefaultStateFromAttributes(Context context, AttributeSet attributeSet, int i, int i2) {
        TypedArray typedArrayObtainStyledAttributes = ThemeEnforcement.obtainStyledAttributes(context, attributeSet, R.styleable.Badge, i, i2, new int[0]);
        setMaxCharacterCount(typedArrayObtainStyledAttributes.getInt(R.styleable.Badge_maxCharacterCount, 4));
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.Badge_number)) {
            setNumber(typedArrayObtainStyledAttributes.getInt(R.styleable.Badge_number, 0));
        }
        setBackgroundColor(readColorFromAttributes(context, typedArrayObtainStyledAttributes, R.styleable.Badge_backgroundColor));
        if (typedArrayObtainStyledAttributes.hasValue(R.styleable.Badge_badgeTextColor)) {
            setBadgeTextColor(readColorFromAttributes(context, typedArrayObtainStyledAttributes, R.styleable.Badge_badgeTextColor));
        }
        typedArrayObtainStyledAttributes.recycle();
    }

    private static int readColorFromAttributes(Context context, TypedArray typedArray, int i) {
        return MaterialResources.getColorStateList(context, typedArray, i).getDefaultColor();
    }

    private BadgeDrawable(Context context) {
        this.context = context;
        ThemeEnforcement.checkMaterialTheme(context);
        Resources resources = context.getResources();
        this.tmpRect = new Rect();
        this.badgeBounds = new Rect();
        this.shapeDrawable = new MaterialShapeDrawable();
        this.badgeRadius = resources.getDimensionPixelSize(R.dimen.mtrl_badge_radius);
        this.badgeWidePadding = resources.getDimensionPixelSize(R.dimen.mtrl_badge_long_text_horizontal_padding);
        this.badgeWithTextRadius = resources.getDimensionPixelSize(R.dimen.mtrl_badge_with_text_radius);
        TextDrawableHelper textDrawableHelper = new TextDrawableHelper(this);
        this.textDrawableHelper = textDrawableHelper;
        textDrawableHelper.getTextPaint().setTextAlign(Paint.Align.CENTER);
        this.savedState = new SavedState(context);
        setTextAppearanceResource(R.style.TextAppearance_MaterialComponents_Badge);
    }

    public void updateBadgeCoordinates(View view, ViewGroup viewGroup) {
        calculateBadgeCenterCoordinates(view, viewGroup);
        updateBounds();
        invalidateSelf();
    }

    public int getBackgroundColor() {
        return this.shapeDrawable.getFillColor().getDefaultColor();
    }

    public void setBackgroundColor(int i) {
        this.savedState.backgroundColor = i;
        ColorStateList colorStateListValueOf = ColorStateList.valueOf(i);
        if (this.shapeDrawable.getFillColor() != colorStateListValueOf) {
            this.shapeDrawable.setFillColor(colorStateListValueOf);
            invalidateSelf();
        }
    }

    public int getBadgeTextColor() {
        return this.textDrawableHelper.getTextPaint().getColor();
    }

    public void setBadgeTextColor(int i) {
        this.savedState.badgeTextColor = i;
        if (this.textDrawableHelper.getTextPaint().getColor() != i) {
            this.textDrawableHelper.getTextPaint().setColor(i);
            invalidateSelf();
        }
    }

    public boolean hasNumber() {
        return this.savedState.number != -1;
    }

    public int getNumber() {
        if (hasNumber()) {
            return this.savedState.number;
        }
        return 0;
    }

    public void setNumber(int i) {
        int iMax = Math.max(0, i);
        if (this.savedState.number != iMax) {
            this.savedState.number = iMax;
            this.textDrawableHelper.setTextWidthDirty(true);
            updateBounds();
            invalidateSelf();
        }
    }

    public void clearBadgeNumber() {
        this.savedState.number = -1;
        invalidateSelf();
    }

    public int getMaxCharacterCount() {
        return this.savedState.maxCharacterCount;
    }

    public void setMaxCharacterCount(int i) {
        if (this.savedState.maxCharacterCount != i) {
            this.savedState.maxCharacterCount = i;
            updateMaxBadgeNumber();
            this.textDrawableHelper.setTextWidthDirty(true);
            updateBounds();
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.savedState.alpha;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.savedState.alpha = i;
        this.textDrawableHelper.getTextPaint().setAlpha(i);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.badgeBounds.height();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.badgeBounds.width();
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (getBounds().isEmpty() || getAlpha() == 0 || !isVisible()) {
            return;
        }
        this.shapeDrawable.draw(canvas);
        if (hasNumber()) {
            drawText(canvas);
        }
    }

    @Override // com.google.android.material.internal.TextDrawableHelper.TextDrawableDelegate
    public void onTextSizeChange() {
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable, com.google.android.material.internal.TextDrawableHelper.TextDrawableDelegate
    public boolean onStateChange(int[] iArr) {
        return super.onStateChange(iArr);
    }

    public void setContentDescriptionNumberless(CharSequence charSequence) {
        this.savedState.contentDescriptionNumberless = charSequence;
    }

    public void setContentDescriptionQuantityStringsResource(int i) {
        this.savedState.contentDescriptionQuantityStrings = i;
    }

    public CharSequence getContentDescription(Context context) {
        if (!isVisible()) {
            return null;
        }
        if (hasNumber()) {
            if (this.savedState.contentDescriptionQuantityStrings > 0) {
                return context.getResources().getQuantityString(this.savedState.contentDescriptionQuantityStrings, getNumber(), Integer.valueOf(getNumber()));
            }
            return null;
        }
        return this.savedState.contentDescriptionNumberless;
    }

    private void setTextAppearanceResource(int i) {
        setTextAppearance(new TextAppearance(this.context, i));
    }

    private void setTextAppearance(TextAppearance textAppearance) {
        if (this.textDrawableHelper.getTextAppearance() == textAppearance) {
            return;
        }
        this.textDrawableHelper.setTextAppearance(textAppearance, this.context);
        updateBounds();
    }

    private void updateBounds() {
        float f;
        this.tmpRect.set(this.badgeBounds);
        if (getNumber() <= 99) {
            f = !hasNumber() ? this.badgeRadius : this.badgeWithTextRadius;
            BadgeUtils.updateBadgeBounds(this.badgeBounds, this.badgeCenterX, this.badgeCenterY, f, f);
        } else {
            f = this.badgeWithTextRadius;
            BadgeUtils.updateBadgeBounds(this.badgeBounds, this.badgeCenterX, this.badgeCenterY, (this.textDrawableHelper.getTextWidth(getBadgeText()) / 2.0f) + this.badgeWidePadding, f);
        }
        this.shapeDrawable.setCornerRadius(f);
        if (this.tmpRect.equals(this.badgeBounds)) {
            return;
        }
        this.shapeDrawable.setBounds(this.badgeBounds);
    }

    private void drawText(Canvas canvas) {
        Rect rect = new Rect();
        String badgeText = getBadgeText();
        this.textDrawableHelper.getTextPaint().getTextBounds(badgeText, 0, badgeText.length(), rect);
        canvas.drawText(badgeText, this.badgeCenterX, this.badgeCenterY + (rect.height() / 2), this.textDrawableHelper.getTextPaint());
    }

    private String getBadgeText() {
        if (getNumber() <= this.maxBadgeNumber) {
            return Integer.toString(getNumber());
        }
        return this.context.getString(R.string.mtrl_exceed_max_badge_number_suffix, Integer.valueOf(this.maxBadgeNumber), DEFAULT_EXCEED_MAX_BADGE_NUMBER_SUFFIX);
    }

    private void updateMaxBadgeNumber() {
        this.maxBadgeNumber = ((int) Math.pow(10.0d, ((double) getMaxCharacterCount()) - 1.0d)) - 1;
    }

    private void calculateBadgeCenterCoordinates(View view, ViewGroup viewGroup) {
        Resources resources = this.context.getResources();
        Rect rect = new Rect();
        view.getDrawingRect(rect);
        rect.top += resources.getDimensionPixelSize(R.dimen.mtrl_badge_vertical_offset);
        if (viewGroup != null || Build.VERSION.SDK_INT < 18) {
            if (viewGroup == null) {
                viewGroup = (ViewGroup) view.getParent();
            }
            viewGroup.offsetDescendantRectToMyCoords(view, rect);
        }
        this.badgeCenterX = ViewCompat.getLayoutDirection(view) == 0 ? rect.right : rect.left;
        this.badgeCenterY = rect.top;
    }
}
