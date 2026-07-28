package com.google.android.material.floatingactionbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.util.Preconditions;
import androidx.core.view.ViewCompat;
import com.google.android.material.R;
import com.google.android.material.animation.AnimatorSetCompat;
import com.google.android.material.animation.MotionSpec;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.internal.DescendantOffsetUtils;
import com.google.android.material.internal.ThemeEnforcement;
import com.google.android.material.shape.ShapeAppearanceModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ExtendedFloatingActionButton extends MaterialButton implements CoordinatorLayout.AttachedBehavior {
    private static final int ANIM_STATE_HIDING = 1;
    private static final int ANIM_STATE_NONE = 0;
    private static final int ANIM_STATE_SHOWING = 2;
    private int animState;
    private final CoordinatorLayout.Behavior<ExtendedFloatingActionButton> behavior;
    private Animator currentCollapseExpandAnimator;
    private Animator currentShowHideAnimator;
    private MotionSpec defaultExtendMotionSpec;
    private MotionSpec defaultHideMotionSpec;
    private MotionSpec defaultShowMotionSpec;
    private MotionSpec defaultShrinkMotionSpec;
    private ArrayList<Animator.AnimatorListener> extendListeners;
    private MotionSpec extendMotionSpec;
    private ArrayList<Animator.AnimatorListener> hideListeners;
    private MotionSpec hideMotionSpec;
    private boolean isExtended;
    private boolean isUsingPillCorner;
    private final Rect shadowPadding;
    private ArrayList<Animator.AnimatorListener> showListeners;
    private MotionSpec showMotionSpec;
    private ArrayList<Animator.AnimatorListener> shrinkListeners;
    private MotionSpec shrinkMotionSpec;
    private int userSetVisibility;
    private static final int DEF_STYLE_RES = R.style.Widget_MaterialComponents_ExtendedFloatingActionButton_Icon;
    private static final Property<View, Float> WIDTH = new Property<View, Float>(Float.class, "width") { // from class: com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton.4
        /* JADX DEBUG: Method merged with bridge method: set(Ljava/lang/Object;Ljava/lang/Object;)V */
        @Override // android.util.Property
        public void set(View view, Float f) {
            view.getLayoutParams().width = f.intValue();
            view.requestLayout();
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(View view) {
            return Float.valueOf(view.getLayoutParams().width);
        }
    };
    private static final Property<View, Float> HEIGHT = new Property<View, Float>(Float.class, "height") { // from class: com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton.5
        /* JADX DEBUG: Method merged with bridge method: set(Ljava/lang/Object;Ljava/lang/Object;)V */
        @Override // android.util.Property
        public void set(View view, Float f) {
            view.getLayoutParams().height = f.intValue();
            view.requestLayout();
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(View view) {
            return Float.valueOf(view.getLayoutParams().height);
        }
    };
    private static final Property<View, Float> CORNER_RADIUS = new Property<View, Float>(Float.class, "cornerRadius") { // from class: com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton.6
        /* JADX DEBUG: Method merged with bridge method: set(Ljava/lang/Object;Ljava/lang/Object;)V */
        @Override // android.util.Property
        public void set(View view, Float f) {
            ((ExtendedFloatingActionButton) view).getShapeAppearanceModel().setCornerRadius(f.intValue());
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(View view) {
            return Float.valueOf(((ExtendedFloatingActionButton) view).getShapeAppearanceModel().getTopRightCorner().getCornerSize());
        }
    };

    public static abstract class OnChangedListener {
        public void onExtended(ExtendedFloatingActionButton extendedFloatingActionButton) {
        }

        public void onHidden(ExtendedFloatingActionButton extendedFloatingActionButton) {
        }

        public void onShown(ExtendedFloatingActionButton extendedFloatingActionButton) {
        }

        public void onShrunken(ExtendedFloatingActionButton extendedFloatingActionButton) {
        }
    }

    public ExtendedFloatingActionButton(Context context) {
        this(context, null);
    }

    public ExtendedFloatingActionButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.extendedFloatingActionButtonStyle);
    }

    public ExtendedFloatingActionButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.shadowPadding = new Rect();
        this.animState = 0;
        this.isExtended = true;
        this.isUsingPillCorner = true;
        this.behavior = new ExtendedFloatingActionButtonBehavior(context, attributeSet);
        this.userSetVisibility = getVisibility();
        int[] iArr = R.styleable.ExtendedFloatingActionButton;
        int i2 = DEF_STYLE_RES;
        TypedArray typedArrayObtainStyledAttributes = ThemeEnforcement.obtainStyledAttributes(context, attributeSet, iArr, i, i2, new int[0]);
        this.showMotionSpec = MotionSpec.createFromAttribute(context, typedArrayObtainStyledAttributes, R.styleable.ExtendedFloatingActionButton_showMotionSpec);
        this.hideMotionSpec = MotionSpec.createFromAttribute(context, typedArrayObtainStyledAttributes, R.styleable.ExtendedFloatingActionButton_hideMotionSpec);
        this.extendMotionSpec = MotionSpec.createFromAttribute(context, typedArrayObtainStyledAttributes, R.styleable.ExtendedFloatingActionButton_extendMotionSpec);
        this.shrinkMotionSpec = MotionSpec.createFromAttribute(context, typedArrayObtainStyledAttributes, R.styleable.ExtendedFloatingActionButton_shrinkMotionSpec);
        typedArrayObtainStyledAttributes.recycle();
        setShapeAppearanceModel(new ShapeAppearanceModel(context, attributeSet, i, i2, -1));
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.isExtended && TextUtils.isEmpty(getText()) && getIcon() != null) {
            this.isExtended = false;
            shrinkNow();
        }
    }

    @Override // com.google.android.material.button.MaterialButton, android.widget.TextView, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.isUsingPillCorner) {
            getShapeAppearanceModel().setCornerRadius(getAdjustedRadius(getMeasuredHeight()));
        }
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.AttachedBehavior
    public CoordinatorLayout.Behavior<ExtendedFloatingActionButton> getBehavior() {
        return this.behavior;
    }

    @Override // com.google.android.material.button.MaterialButton, com.google.android.material.shape.Shapeable
    public void setShapeAppearanceModel(ShapeAppearanceModel shapeAppearanceModel) {
        this.isUsingPillCorner = shapeAppearanceModel.isUsingPillCorner();
        super.setShapeAppearanceModel(shapeAppearanceModel);
    }

    @Override // com.google.android.material.button.MaterialButton
    public void setCornerRadius(int i) {
        boolean z = i == -1;
        this.isUsingPillCorner = z;
        if (z) {
            i = getAdjustedRadius(getMeasuredHeight());
        } else if (i < 0) {
            i = 0;
        }
        super.setCornerRadius(i);
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        internalSetVisibility(i, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void internalSetVisibility(int i, boolean z) {
        super.setVisibility(i);
        if (z) {
            this.userSetVisibility = i;
        }
    }

    public final int getUserSetVisibility() {
        return this.userSetVisibility;
    }

    public final boolean isExtended() {
        return this.isExtended;
    }

    public void addOnShowAnimationListener(Animator.AnimatorListener animatorListener) {
        if (this.showListeners == null) {
            this.showListeners = new ArrayList<>();
        }
        this.showListeners.add(animatorListener);
    }

    public void removeOnShowAnimationListener(Animator.AnimatorListener animatorListener) {
        ArrayList<Animator.AnimatorListener> arrayList = this.showListeners;
        if (arrayList == null) {
            return;
        }
        arrayList.remove(animatorListener);
    }

    public void addOnHideAnimationListener(Animator.AnimatorListener animatorListener) {
        if (this.hideListeners == null) {
            this.hideListeners = new ArrayList<>();
        }
        this.hideListeners.add(animatorListener);
    }

    public void removeOnHideAnimationListener(Animator.AnimatorListener animatorListener) {
        ArrayList<Animator.AnimatorListener> arrayList = this.hideListeners;
        if (arrayList == null) {
            return;
        }
        arrayList.remove(animatorListener);
    }

    public void addOnShrinkAnimationListener(Animator.AnimatorListener animatorListener) {
        if (this.shrinkListeners == null) {
            this.shrinkListeners = new ArrayList<>();
        }
        this.shrinkListeners.add(animatorListener);
    }

    public void removeOnShrinkAnimationListener(Animator.AnimatorListener animatorListener) {
        ArrayList<Animator.AnimatorListener> arrayList = this.shrinkListeners;
        if (arrayList == null) {
            return;
        }
        arrayList.remove(animatorListener);
    }

    public void addOnExtendAnimationListener(Animator.AnimatorListener animatorListener) {
        if (this.extendListeners == null) {
            this.extendListeners = new ArrayList<>();
        }
        this.extendListeners.add(animatorListener);
    }

    public void removeOnExtendAnimationListener(Animator.AnimatorListener animatorListener) {
        ArrayList<Animator.AnimatorListener> arrayList = this.extendListeners;
        if (arrayList == null) {
            return;
        }
        arrayList.remove(animatorListener);
    }

    public void hide() {
        hide(true);
    }

    public void hide(boolean z) {
        hide(true, z, null);
    }

    public void hide(OnChangedListener onChangedListener) {
        hide(true, true, onChangedListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hide(final boolean z, boolean z2, final OnChangedListener onChangedListener) {
        if (isOrWillBeHidden()) {
            return;
        }
        Animator animator = this.currentShowHideAnimator;
        if (animator != null) {
            animator.cancel();
        }
        if (z2 && shouldAnimateVisibilityChange()) {
            AnimatorSet animatorSetCreateAnimator = createAnimator(getCurrentHideMotionSpec());
            animatorSetCreateAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton.1
                private boolean cancelled;

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator2) {
                    ExtendedFloatingActionButton.this.internalSetVisibility(0, z);
                    ExtendedFloatingActionButton.this.animState = 1;
                    ExtendedFloatingActionButton.this.currentShowHideAnimator = animator2;
                    this.cancelled = false;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator2) {
                    this.cancelled = true;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator2) {
                    ExtendedFloatingActionButton.this.animState = 0;
                    ExtendedFloatingActionButton.this.currentShowHideAnimator = null;
                    if (this.cancelled) {
                        return;
                    }
                    ExtendedFloatingActionButton extendedFloatingActionButton = ExtendedFloatingActionButton.this;
                    boolean z3 = z;
                    extendedFloatingActionButton.internalSetVisibility(z3 ? 8 : 4, z3);
                    OnChangedListener onChangedListener2 = onChangedListener;
                    if (onChangedListener2 != null) {
                        onChangedListener2.onHidden(ExtendedFloatingActionButton.this);
                    }
                }
            });
            ArrayList<Animator.AnimatorListener> arrayList = this.hideListeners;
            if (arrayList != null) {
                Iterator<Animator.AnimatorListener> it = arrayList.iterator();
                while (it.hasNext()) {
                    animatorSetCreateAnimator.addListener(it.next());
                }
            }
            animatorSetCreateAnimator.start();
            return;
        }
        internalSetVisibility(z ? 8 : 4, z);
        if (onChangedListener != null) {
            onChangedListener.onHidden(this);
        }
    }

    public void show() {
        show(true);
    }

    public void show(boolean z) {
        show(true, z, null);
    }

    public void show(OnChangedListener onChangedListener) {
        show(true, true, onChangedListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void show(final boolean z, boolean z2, final OnChangedListener onChangedListener) {
        if (isOrWillBeShown()) {
            return;
        }
        Animator animator = this.currentShowHideAnimator;
        if (animator != null) {
            animator.cancel();
        }
        if (z2 && shouldAnimateVisibilityChange()) {
            AnimatorSet animatorSetCreateAnimator = createAnimator(getCurrentShowMotionSpec());
            animatorSetCreateAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator2) {
                    ExtendedFloatingActionButton.this.internalSetVisibility(0, z);
                    ExtendedFloatingActionButton.this.animState = 2;
                    ExtendedFloatingActionButton.this.currentShowHideAnimator = animator2;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator2) {
                    ExtendedFloatingActionButton.this.animState = 0;
                    ExtendedFloatingActionButton.this.currentShowHideAnimator = null;
                    OnChangedListener onChangedListener2 = onChangedListener;
                    if (onChangedListener2 != null) {
                        onChangedListener2.onShown(ExtendedFloatingActionButton.this);
                    }
                }
            });
            ArrayList<Animator.AnimatorListener> arrayList = this.showListeners;
            if (arrayList != null) {
                Iterator<Animator.AnimatorListener> it = arrayList.iterator();
                while (it.hasNext()) {
                    animatorSetCreateAnimator.addListener(it.next());
                }
            }
            animatorSetCreateAnimator.start();
            return;
        }
        internalSetVisibility(0, z);
        setAlpha(1.0f);
        setScaleY(1.0f);
        setScaleX(1.0f);
        if (onChangedListener != null) {
            onChangedListener.onShown(this);
        }
    }

    public void extend() {
        extend(true);
    }

    public void extend(boolean z) {
        setExtended(true, z, null);
    }

    public void extend(OnChangedListener onChangedListener) {
        setExtended(true, true, onChangedListener);
    }

    public void shrink() {
        shrink(true);
    }

    public void shrink(boolean z) {
        setExtended(false, z, null);
    }

    public void shrink(OnChangedListener onChangedListener) {
        setExtended(false, true, onChangedListener);
    }

    public MotionSpec getShowMotionSpec() {
        return this.showMotionSpec;
    }

    public void setShowMotionSpec(MotionSpec motionSpec) {
        this.showMotionSpec = motionSpec;
    }

    public void setShowMotionSpecResource(int i) {
        setShowMotionSpec(MotionSpec.createFromResource(getContext(), i));
    }

    public MotionSpec getHideMotionSpec() {
        return this.hideMotionSpec;
    }

    public void setHideMotionSpec(MotionSpec motionSpec) {
        this.hideMotionSpec = motionSpec;
    }

    public void setHideMotionSpecResource(int i) {
        setHideMotionSpec(MotionSpec.createFromResource(getContext(), i));
    }

    public MotionSpec getExtendMotionSpec() {
        return this.extendMotionSpec;
    }

    public void setExtendMotionSpec(MotionSpec motionSpec) {
        this.extendMotionSpec = motionSpec;
    }

    public void setExtendMotionSpecResource(int i) {
        setExtendMotionSpec(MotionSpec.createFromResource(getContext(), i));
    }

    public MotionSpec getShrinkMotionSpec() {
        return this.shrinkMotionSpec;
    }

    public void setShrinkMotionSpec(MotionSpec motionSpec) {
        this.shrinkMotionSpec = motionSpec;
    }

    public void setShrinkMotionSpecResource(int i) {
        setShrinkMotionSpec(MotionSpec.createFromResource(getContext(), i));
    }

    private void setExtended(final boolean z, boolean z2, final OnChangedListener onChangedListener) {
        if (z == this.isExtended || getIcon() == null || TextUtils.isEmpty(getText())) {
            return;
        }
        this.isExtended = z;
        Animator animator = this.currentCollapseExpandAnimator;
        if (animator != null) {
            animator.cancel();
        }
        if (z2 && shouldAnimateVisibilityChange()) {
            measure(0, 0);
            AnimatorSet animatorSetCreateShrinkExtendAnimator = createShrinkExtendAnimator(this.isExtended ? getCurrentExtendMotionSpec() : getCurrentShrinkMotionSpec(), !this.isExtended);
            animatorSetCreateShrinkExtendAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton.3
                private boolean cancelled;

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator2) {
                    ExtendedFloatingActionButton.this.setHorizontallyScrolling(true);
                    ExtendedFloatingActionButton.this.currentCollapseExpandAnimator = animator2;
                    this.cancelled = false;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator2) {
                    this.cancelled = true;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator2) {
                    OnChangedListener onChangedListener2;
                    ExtendedFloatingActionButton.this.setHorizontallyScrolling(false);
                    ExtendedFloatingActionButton.this.currentCollapseExpandAnimator = null;
                    if (this.cancelled || (onChangedListener2 = onChangedListener) == null) {
                        return;
                    }
                    if (z) {
                        onChangedListener2.onExtended(ExtendedFloatingActionButton.this);
                    } else {
                        onChangedListener2.onShrunken(ExtendedFloatingActionButton.this);
                    }
                }
            });
            ArrayList<Animator.AnimatorListener> arrayList = z ? this.extendListeners : this.shrinkListeners;
            if (arrayList != null) {
                Iterator<Animator.AnimatorListener> it = arrayList.iterator();
                while (it.hasNext()) {
                    animatorSetCreateShrinkExtendAnimator.addListener(it.next());
                }
            }
            animatorSetCreateShrinkExtendAnimator.start();
            return;
        }
        if (z) {
            extendNow();
            if (onChangedListener != null) {
                onChangedListener.onExtended(this);
                return;
            }
            return;
        }
        shrinkNow();
        if (onChangedListener != null) {
            onChangedListener.onShrunken(this);
        }
    }

    private AnimatorSet createAnimator(MotionSpec motionSpec) {
        ArrayList arrayList = new ArrayList();
        if (motionSpec.hasPropertyValues("opacity")) {
            arrayList.add(motionSpec.getAnimator("opacity", this, View.ALPHA));
        }
        if (motionSpec.hasPropertyValues("scale")) {
            arrayList.add(motionSpec.getAnimator("scale", this, View.SCALE_Y));
            arrayList.add(motionSpec.getAnimator("scale", this, View.SCALE_X));
        }
        if (motionSpec.hasPropertyValues("width")) {
            arrayList.add(motionSpec.getAnimator("width", this, WIDTH));
        }
        if (motionSpec.hasPropertyValues("height")) {
            arrayList.add(motionSpec.getAnimator("height", this, HEIGHT));
        }
        if (motionSpec.hasPropertyValues("cornerRadius") && !this.isUsingPillCorner) {
            arrayList.add(motionSpec.getAnimator("cornerRadius", this, CORNER_RADIUS));
        }
        AnimatorSet animatorSet = new AnimatorSet();
        AnimatorSetCompat.playTogether(animatorSet, arrayList);
        return animatorSet;
    }

    private AnimatorSet createShrinkExtendAnimator(MotionSpec motionSpec, boolean z) {
        int collapsedSize = getCollapsedSize();
        if (motionSpec.hasPropertyValues("width")) {
            PropertyValuesHolder[] propertyValues = motionSpec.getPropertyValues("width");
            if (z) {
                propertyValues[0].setFloatValues(getMeasuredWidth(), collapsedSize);
            } else {
                propertyValues[0].setFloatValues(getWidth(), getMeasuredWidth());
            }
            motionSpec.setPropertyValues("width", propertyValues);
        }
        if (motionSpec.hasPropertyValues("height")) {
            PropertyValuesHolder[] propertyValues2 = motionSpec.getPropertyValues("height");
            if (z) {
                propertyValues2[0].setFloatValues(getMeasuredHeight(), collapsedSize);
            } else {
                propertyValues2[0].setFloatValues(getHeight(), getMeasuredHeight());
            }
            motionSpec.setPropertyValues("height", propertyValues2);
        }
        return createAnimator(motionSpec);
    }

    private boolean isOrWillBeShown() {
        return getVisibility() != 0 ? this.animState == 2 : this.animState != 1;
    }

    private boolean isOrWillBeHidden() {
        return getVisibility() == 0 ? this.animState == 1 : this.animState != 2;
    }

    private boolean shouldAnimateVisibilityChange() {
        return ViewCompat.isLaidOut(this) && !isInEditMode();
    }

    private void shrinkNow() {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams == null) {
            return;
        }
        int collapsedSize = getCollapsedSize();
        layoutParams.width = collapsedSize;
        layoutParams.height = collapsedSize;
        requestLayout();
    }

    private void extendNow() {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams == null) {
            return;
        }
        measure(0, 0);
        layoutParams.width = getMeasuredWidth();
        layoutParams.height = getMeasuredHeight();
        requestLayout();
    }

    private MotionSpec getCurrentShowMotionSpec() {
        MotionSpec motionSpec = this.showMotionSpec;
        if (motionSpec != null) {
            return motionSpec;
        }
        if (this.defaultShowMotionSpec == null) {
            this.defaultShowMotionSpec = MotionSpec.createFromResource(getContext(), R.animator.mtrl_extended_fab_show_motion_spec);
        }
        return (MotionSpec) Preconditions.checkNotNull(this.defaultShowMotionSpec);
    }

    private MotionSpec getCurrentHideMotionSpec() {
        MotionSpec motionSpec = this.hideMotionSpec;
        if (motionSpec != null) {
            return motionSpec;
        }
        if (this.defaultHideMotionSpec == null) {
            this.defaultHideMotionSpec = MotionSpec.createFromResource(getContext(), R.animator.mtrl_extended_fab_hide_motion_spec);
        }
        return (MotionSpec) Preconditions.checkNotNull(this.defaultHideMotionSpec);
    }

    private MotionSpec getCurrentExtendMotionSpec() {
        MotionSpec motionSpec = this.extendMotionSpec;
        if (motionSpec != null) {
            return motionSpec;
        }
        if (this.defaultExtendMotionSpec == null) {
            this.defaultExtendMotionSpec = MotionSpec.createFromResource(getContext(), R.animator.mtrl_extended_fab_extend_motion_spec);
        }
        return (MotionSpec) Preconditions.checkNotNull(this.defaultExtendMotionSpec);
    }

    private MotionSpec getCurrentShrinkMotionSpec() {
        MotionSpec motionSpec = this.shrinkMotionSpec;
        if (motionSpec != null) {
            return motionSpec;
        }
        if (this.defaultShrinkMotionSpec == null) {
            this.defaultShrinkMotionSpec = MotionSpec.createFromResource(getContext(), R.animator.mtrl_extended_fab_shrink_motion_spec);
        }
        return (MotionSpec) Preconditions.checkNotNull(this.defaultShrinkMotionSpec);
    }

    private int getAdjustedRadius(int i) {
        return (i - 1) / 2;
    }

    private int getCollapsedSize() {
        return (Math.min(ViewCompat.getPaddingStart(this), ViewCompat.getPaddingEnd(this)) * 2) + getIconSize();
    }

    protected static class ExtendedFloatingActionButtonBehavior<T extends ExtendedFloatingActionButton> extends CoordinatorLayout.Behavior<T> {
        private static final boolean AUTO_HIDE_DEFAULT = false;
        private static final boolean AUTO_SHRINK_DEFAULT = true;
        private boolean autoHideEnabled;
        private boolean autoShrinkEnabled;
        private OnChangedListener internalAutoHideListener;
        private OnChangedListener internalAutoShrinkListener;
        private Rect tmpRect;

        public ExtendedFloatingActionButtonBehavior() {
            this.autoHideEnabled = false;
            this.autoShrinkEnabled = true;
        }

        public ExtendedFloatingActionButtonBehavior(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ExtendedFloatingActionButton_Behavior_Layout);
            this.autoHideEnabled = typedArrayObtainStyledAttributes.getBoolean(R.styleable.ExtendedFloatingActionButton_Behavior_Layout_behavior_autoHide, false);
            this.autoShrinkEnabled = typedArrayObtainStyledAttributes.getBoolean(R.styleable.ExtendedFloatingActionButton_Behavior_Layout_behavior_autoShrink, true);
            typedArrayObtainStyledAttributes.recycle();
        }

        public void setAutoHideEnabled(boolean z) {
            this.autoHideEnabled = z;
        }

        public boolean isAutoHideEnabled() {
            return this.autoHideEnabled;
        }

        public void setAutoShrinkEnabled(boolean z) {
            this.autoShrinkEnabled = z;
        }

        public boolean isAutoShrinkEnabled() {
            return this.autoShrinkEnabled;
        }

        @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
        public void onAttachedToLayoutParams(CoordinatorLayout.LayoutParams layoutParams) {
            if (layoutParams.dodgeInsetEdges == 0) {
                layoutParams.dodgeInsetEdges = 80;
            }
        }

        /* JADX DEBUG: Method merged with bridge method: onDependentViewChanged(Landroidx/coordinatorlayout/widget/CoordinatorLayout;Landroid/view/View;Landroid/view/View;)Z */
        @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
        public boolean onDependentViewChanged(CoordinatorLayout coordinatorLayout, ExtendedFloatingActionButton extendedFloatingActionButton, View view) {
            if (view instanceof AppBarLayout) {
                updateFabVisibilityForAppBarLayout(coordinatorLayout, (AppBarLayout) view, extendedFloatingActionButton);
                return false;
            }
            if (!isBottomSheet(view)) {
                return false;
            }
            updateFabVisibilityForBottomSheet(view, extendedFloatingActionButton);
            return false;
        }

        private static boolean isBottomSheet(View view) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams instanceof CoordinatorLayout.LayoutParams) {
                return ((CoordinatorLayout.LayoutParams) layoutParams).getBehavior() instanceof BottomSheetBehavior;
            }
            return false;
        }

        public void setInternalAutoHideListener(OnChangedListener onChangedListener) {
            this.internalAutoHideListener = onChangedListener;
        }

        public void setInternalAutoShrinkListener(OnChangedListener onChangedListener) {
            this.internalAutoShrinkListener = onChangedListener;
        }

        private boolean shouldUpdateVisibility(View view, ExtendedFloatingActionButton extendedFloatingActionButton) {
            return (this.autoHideEnabled || this.autoShrinkEnabled) && ((CoordinatorLayout.LayoutParams) extendedFloatingActionButton.getLayoutParams()).getAnchorId() == view.getId() && extendedFloatingActionButton.getUserSetVisibility() == 0;
        }

        private boolean updateFabVisibilityForAppBarLayout(CoordinatorLayout coordinatorLayout, AppBarLayout appBarLayout, ExtendedFloatingActionButton extendedFloatingActionButton) {
            if (!shouldUpdateVisibility(appBarLayout, extendedFloatingActionButton)) {
                return false;
            }
            if (this.tmpRect == null) {
                this.tmpRect = new Rect();
            }
            Rect rect = this.tmpRect;
            DescendantOffsetUtils.getDescendantRect(coordinatorLayout, appBarLayout, rect);
            if (rect.bottom <= appBarLayout.getMinimumHeightForVisibleOverlappingContent()) {
                shrinkOrHide(extendedFloatingActionButton);
                return true;
            }
            extendOrShow(extendedFloatingActionButton);
            return true;
        }

        private boolean updateFabVisibilityForBottomSheet(View view, ExtendedFloatingActionButton extendedFloatingActionButton) {
            if (!shouldUpdateVisibility(view, extendedFloatingActionButton)) {
                return false;
            }
            if (view.getTop() < (extendedFloatingActionButton.getHeight() / 2) + ((CoordinatorLayout.LayoutParams) extendedFloatingActionButton.getLayoutParams()).topMargin) {
                shrinkOrHide(extendedFloatingActionButton);
                return true;
            }
            extendOrShow(extendedFloatingActionButton);
            return true;
        }

        protected void shrinkOrHide(ExtendedFloatingActionButton extendedFloatingActionButton) {
            if (this.autoShrinkEnabled) {
                extendedFloatingActionButton.shrink(this.internalAutoShrinkListener);
            } else if (this.autoHideEnabled) {
                extendedFloatingActionButton.hide(false, true, this.internalAutoHideListener);
            }
        }

        protected void extendOrShow(ExtendedFloatingActionButton extendedFloatingActionButton) {
            if (this.autoShrinkEnabled) {
                extendedFloatingActionButton.extend(this.internalAutoShrinkListener);
            } else if (this.autoHideEnabled) {
                extendedFloatingActionButton.show(false, true, this.internalAutoHideListener);
            }
        }

        /* JADX DEBUG: Method merged with bridge method: onLayoutChild(Landroidx/coordinatorlayout/widget/CoordinatorLayout;Landroid/view/View;I)Z */
        @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
        public boolean onLayoutChild(CoordinatorLayout coordinatorLayout, ExtendedFloatingActionButton extendedFloatingActionButton, int i) {
            List<View> dependencies = coordinatorLayout.getDependencies(extendedFloatingActionButton);
            int size = dependencies.size();
            for (int i2 = 0; i2 < size; i2++) {
                View view = dependencies.get(i2);
                if (view instanceof AppBarLayout) {
                    if (updateFabVisibilityForAppBarLayout(coordinatorLayout, (AppBarLayout) view, extendedFloatingActionButton)) {
                        break;
                    }
                } else {
                    if (isBottomSheet(view) && updateFabVisibilityForBottomSheet(view, extendedFloatingActionButton)) {
                        break;
                    }
                }
            }
            coordinatorLayout.onLayoutChild(extendedFloatingActionButton, i);
            offsetIfNeeded(coordinatorLayout, extendedFloatingActionButton);
            return true;
        }

        /* JADX DEBUG: Method merged with bridge method: getInsetDodgeRect(Landroidx/coordinatorlayout/widget/CoordinatorLayout;Landroid/view/View;Landroid/graphics/Rect;)Z */
        @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
        public boolean getInsetDodgeRect(CoordinatorLayout coordinatorLayout, ExtendedFloatingActionButton extendedFloatingActionButton, Rect rect) {
            Rect rect2 = extendedFloatingActionButton.shadowPadding;
            rect.set(extendedFloatingActionButton.getLeft() + rect2.left, extendedFloatingActionButton.getTop() + rect2.top, extendedFloatingActionButton.getRight() - rect2.right, extendedFloatingActionButton.getBottom() - rect2.bottom);
            return true;
        }

        private void offsetIfNeeded(CoordinatorLayout coordinatorLayout, ExtendedFloatingActionButton extendedFloatingActionButton) {
            int i;
            Rect rect = extendedFloatingActionButton.shadowPadding;
            if (rect == null || rect.centerX() <= 0 || rect.centerY() <= 0) {
                return;
            }
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) extendedFloatingActionButton.getLayoutParams();
            int i2 = 0;
            if (extendedFloatingActionButton.getRight() >= coordinatorLayout.getWidth() - layoutParams.rightMargin) {
                i = rect.right;
            } else {
                i = extendedFloatingActionButton.getLeft() <= layoutParams.leftMargin ? -rect.left : 0;
            }
            if (extendedFloatingActionButton.getBottom() >= coordinatorLayout.getHeight() - layoutParams.bottomMargin) {
                i2 = rect.bottom;
            } else if (extendedFloatingActionButton.getTop() <= layoutParams.topMargin) {
                i2 = -rect.top;
            }
            if (i2 != 0) {
                ViewCompat.offsetTopAndBottom(extendedFloatingActionButton, i2);
            }
            if (i != 0) {
                ViewCompat.offsetLeftAndRight(extendedFloatingActionButton, i);
            }
        }
    }
}
