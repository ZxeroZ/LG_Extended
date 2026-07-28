package com.lge.launcher3.allapps;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.CellLayout;
import com.android.launcher3.model.data.ItemInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;
import java.util.ArrayList;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsPagedCellLayout extends CellLayout implements Page {
    public static final boolean DEBUG_getIndexForCheckingPosition = false;
    private static final int REARRANGE_ANIM_DURATION = 200;
    static final String TAG = "AllAppsPagedCellLayout";
    private boolean bShrinkEffect;
    private boolean mArrangeMode;
    private AllAppsPagedCellLayoutChildren mChildren;
    private final HashMap<View, Animator> mReorderAnimators;
    private Drawable mZoomBg;
    private int mZoomBg_Padding_Bottom;
    private int mZoomBg_Padding_Left;
    private int mZoomBg_Padding_Right;
    private int mZoomBg_Padding_Top;

    public void allowHardwareLayerCreation() {
    }

    public AllAppsPagedCellLayout(Context context) {
        this(context, null);
    }

    public AllAppsPagedCellLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AllAppsPagedCellLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.bShrinkEffect = false;
        this.mArrangeMode = false;
        this.mReorderAnimators = new HashMap<>();
        Resources resources = getResources();
        if (resources != null) {
            this.mZoomBg_Padding_Left = (int) resources.getDimension(R.dimen.pagedview_cellLayout_bg_padding_left);
            this.mZoomBg_Padding_Top = (int) resources.getDimension(R.dimen.pagedview_cellLayout_bg_padding_top);
            this.mZoomBg_Padding_Right = (int) resources.getDimension(R.dimen.pagedview_cellLayout_bg_padding_right);
            this.mZoomBg_Padding_Bottom = (int) resources.getDimension(R.dimen.pagedview_cellLayout_bg_padding_bottom);
        }
        if (Utilities.isLGUI7_1()) {
            getDefaultHomeLayout().setVisibility(8);
        }
    }

    @Override // android.view.View
    public void setAlpha(float alpha) {
        this.mShortcutsAndWidgets.setAlpha(alpha);
    }

    public void swapViewOnPageAt(final int from, final int dest) {
        View childOnPageId = getChildOnPageId(from);
        View childOnPageId2 = getChildOnPageId(dest);
        ArrayList arrayList = new ArrayList();
        if (childOnPageId == null || childOnPageId2 == null) {
            return;
        }
        AllAppsPagedCellLayoutParam allAppsPagedCellLayoutParam = (AllAppsPagedCellLayoutParam) childOnPageId.getLayoutParams();
        allAppsPagedCellLayoutParam.cellX = dest % this.mCountX;
        allAppsPagedCellLayoutParam.cellY = dest / this.mCountX;
        ((ItemInfo) childOnPageId.getTag()).cellX = allAppsPagedCellLayoutParam.cellX;
        ((ItemInfo) childOnPageId.getTag()).cellY = allAppsPagedCellLayoutParam.cellY;
        ((ItemInfo) childOnPageId.getTag()).requiresDbUpdate = true;
        int i = 0;
        allAppsPagedCellLayoutParam.isLockedToGrid = false;
        childOnPageId.setX((this.mCellWidth + this.mWidthGap) * allAppsPagedCellLayoutParam.cellX);
        childOnPageId.setY((this.mCellHeight + this.mHeightGap) * allAppsPagedCellLayoutParam.cellY);
        if (from < dest) {
            for (int i2 = from + 1; i2 < dest + 1; i2++) {
                View childOnPageId3 = getChildOnPageId(i2);
                if (childOnPageId3 == null) {
                    return;
                }
                int i3 = (this.mCellHeight + this.mHeightGap) * (i2 / this.mCountX);
                int i4 = (this.mCellWidth + this.mWidthGap) * (i2 % this.mCountX);
                AllAppsPagedCellLayoutParam allAppsPagedCellLayoutParam2 = (AllAppsPagedCellLayoutParam) childOnPageId3.getLayoutParams();
                if (allAppsPagedCellLayoutParam2.cellX == 0) {
                    allAppsPagedCellLayoutParam2.cellX = this.mCountX - 1;
                    allAppsPagedCellLayoutParam2.cellY--;
                    ((ItemInfo) childOnPageId3.getTag()).cellX = allAppsPagedCellLayoutParam2.cellX;
                    ((ItemInfo) childOnPageId3.getTag()).cellY = allAppsPagedCellLayoutParam2.cellY;
                    ((ItemInfo) childOnPageId3.getTag()).requiresDbUpdate = true;
                    arrayList.add(getAnimator(childOnPageId3, i4, i4 + ((this.mCellWidth + this.mWidthGap) * (this.mCountX - 1)), i3, (i3 - this.mCellHeight) - this.mHeightGap, i));
                } else {
                    allAppsPagedCellLayoutParam2.cellX--;
                    ((ItemInfo) childOnPageId3.getTag()).cellX = allAppsPagedCellLayoutParam2.cellX;
                    ((ItemInfo) childOnPageId3.getTag()).requiresDbUpdate = true;
                    arrayList.add(getAnimator(childOnPageId3, i4, (i4 - this.mCellWidth) - this.mWidthGap, i3, i3, i));
                }
                i += 10;
                childOnPageId3.setId(i2 - 1);
            }
        } else {
            int i5 = 0;
            for (int i6 = from - 1; i6 >= dest; i6--) {
                View childOnPageId4 = getChildOnPageId(i6);
                if (childOnPageId4 == null) {
                    return;
                }
                AllAppsPagedCellLayoutParam allAppsPagedCellLayoutParam3 = (AllAppsPagedCellLayoutParam) childOnPageId4.getLayoutParams();
                int i7 = (this.mCellHeight + this.mHeightGap) * (i6 / this.mCountX);
                int i8 = (i6 % this.mCountX) * (this.mCellWidth + this.mWidthGap);
                if (allAppsPagedCellLayoutParam3.cellX == this.mCountX - 1) {
                    allAppsPagedCellLayoutParam3.cellX = 0;
                    allAppsPagedCellLayoutParam3.cellY++;
                    ((ItemInfo) childOnPageId4.getTag()).cellX = 0;
                    ((ItemInfo) childOnPageId4.getTag()).cellY = allAppsPagedCellLayoutParam3.cellY;
                    ((ItemInfo) childOnPageId4.getTag()).requiresDbUpdate = true;
                    arrayList.add(getAnimator(childOnPageId4, i8, 0, i7, this.mCellHeight + i7 + this.mHeightGap, i5));
                } else {
                    allAppsPagedCellLayoutParam3.cellX++;
                    ((ItemInfo) childOnPageId4.getTag()).cellX = allAppsPagedCellLayoutParam3.cellX;
                    ((ItemInfo) childOnPageId4.getTag()).requiresDbUpdate = true;
                    arrayList.add(getAnimator(childOnPageId4, i8, this.mCellWidth + i8 + this.mWidthGap, i7, i7, i5));
                }
                i5 += 10;
                childOnPageId4.setId(i6 + 1);
            }
        }
        childOnPageId.setId(dest);
        if (arrayList.size() > 0) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(200L);
            animatorSet.playTogether(arrayList);
            animatorSet.start();
        }
    }

    public Animator getAnimator(Object target, final int fromx, final int tox, final int fromy, final int toy, final int delay) {
        Animator animator;
        final View view = (View) target;
        final AllAppsPagedCellLayoutParam allAppsPagedCellLayoutParam = (AllAppsPagedCellLayoutParam) view.getLayoutParams();
        allAppsPagedCellLayoutParam.isLockedToGrid = false;
        if (this.mReorderAnimators.containsKey(view) && (animator = this.mReorderAnimators.get(view)) != null) {
            animator.end();
            this.mReorderAnimators.remove(view);
        }
        ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(230L);
        this.mReorderAnimators.put(view, duration);
        duration.addListener(new AnimatorListenerAdapter() { // from class: com.lge.launcher3.allapps.AllAppsPagedCellLayout.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                allAppsPagedCellLayoutParam.isLockedToGrid = true;
                if (AllAppsPagedCellLayout.this.mReorderAnimators.containsKey(view)) {
                    AllAppsPagedCellLayout.this.mReorderAnimators.remove(view);
                }
            }
        });
        duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.lge.launcher3.allapps.AllAppsPagedCellLayout.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                Float f = (Float) animation.getAnimatedValue();
                Float fValueOf = Float.valueOf(1.0f - f.floatValue());
                allAppsPagedCellLayoutParam.isLockedToGrid = false;
                view.setX((fromx * fValueOf.floatValue()) + (tox * f.floatValue()));
                view.setY((fromy * fValueOf.floatValue()) + (toy * f.floatValue()));
            }
        });
        duration.setStartDelay(delay);
        return duration;
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x006e, code lost:
    
        r4 = 512;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int getIndex(int r10, int r11, int r12, boolean r13) {
        /*
            r9 = this;
            r9.getScaleX()
            com.android.launcher3.ShortcutAndWidgetContainer r0 = r9.getShortcutsAndWidgets()
            r0.getScaleX()
            android.content.Context r0 = r9.getContext()
            android.content.res.Resources r0 = r0.getResources()
            r1 = 2131165340(0x7f07009c, float:1.7944894E38)
            float r0 = r0.getDimension(r1)
            int r0 = (int) r0
            int r1 = r9.mCellWidth
            int r1 = r1 - r0
            int r1 = r1 / 2
            r2 = 0
            if (r13 != 0) goto L34
            int r13 = r9.getPaddingStart()
            int r1 = r9.mCellWidth
            float r1 = (float) r1
            float r3 = (float) r0
            r4 = 1067869798(0x3fa66666, float:1.3)
            float r3 = r3 * r4
            float r1 = r1 - r3
            r3 = 1073741824(0x40000000, float:2.0)
            float r1 = r1 / r3
            int r1 = (int) r1
            goto L35
        L34:
            r13 = r2
        L35:
            r3 = -1
            r4 = r2
            r5 = r3
        L38:
            int r6 = r9.mCountX
            r7 = 256(0x100, float:3.59E-43)
            if (r2 >= r6) goto L85
            if (r13 >= r10) goto L44
            int r6 = r13 + r1
            if (r6 > r10) goto L4f
        L44:
            if (r2 != 0) goto L51
            int r6 = r13 + r1
            if (r6 >= r10) goto L51
            int r8 = r0 / 4
            int r6 = r6 + r8
            if (r6 <= r10) goto L51
        L4f:
            r4 = r7
            goto L86
        L51:
            int r6 = r13 + r1
            if (r6 >= r10) goto L71
            int r6 = r9.mCellWidth
            int r6 = r6 + r13
            int r6 = r6 - r1
            if (r6 <= r10) goto L71
            int r4 = r9.mCountX
            int r4 = r4 + (-1)
            if (r2 != r4) goto L6e
            int r4 = r9.mCellWidth
            int r4 = r4 + r13
            int r4 = r4 - r1
            int r5 = r0 / 4
            int r4 = r4 - r5
            if (r4 >= r10) goto L6e
            int r5 = r2 + 1
            r4 = r7
            goto L7f
        L6e:
            r4 = 512(0x200, float:7.17E-43)
            goto L86
        L71:
            int r6 = r9.mCellWidth
            int r6 = r6 + r13
            int r6 = r6 - r1
            if (r6 >= r10) goto L7f
            int r6 = r9.mCellWidth
            int r6 = r6 + r13
            if (r10 >= r6) goto L7f
            int r2 = r2 + 1
            goto L4f
        L7f:
            int r6 = r9.mCellWidth
            int r13 = r13 + r6
            int r2 = r2 + 1
            goto L38
        L85:
            r2 = r5
        L86:
            int r10 = r9.mCellHeight
            int r11 = r11 / r10
            r10 = 65280(0xff00, float:9.1477E-41)
            r10 = r10 & r4
            if (r10 != r7) goto Lb3
            if (r12 < 0) goto Lb3
            int r10 = r9.mCountX
            int r10 = r12 / r10
            if (r11 != r10) goto La0
            int r10 = r9.mCountX
            int r10 = r12 % r10
            if (r2 <= r10) goto La0
        L9d:
            int r2 = r2 + (-1)
            goto Lb3
        La0:
            int r10 = r9.mCountX
            int r10 = r12 / r10
            if (r11 <= r10) goto Lb3
            if (r2 <= 0) goto Lb3
            int r10 = r9.mCountX
            int r10 = r10 * r11
            int r10 = r10 + r2
            int r13 = r9.getPageChildCount()
            if (r10 == r13) goto Lb3
            goto L9d
        Lb3:
            int r10 = r9.mCountX
            if (r2 != r10) goto Lb9
            int r2 = r2 + (-1)
        Lb9:
            int r10 = r9.mCountX
            int r10 = r10 * r11
            int r10 = r10 + r2
            android.view.View r10 = r9.getChildOnPageId(r10)
            if (r10 != 0) goto Ld5
            int r10 = r9.getPageChildCount()
            int r11 = r9.mCountX
            int r12 = r9.mCountY
            int r11 = r11 * r12
            if (r10 >= r11) goto Ld4
            int r10 = r9.getPageChildCount()
            r10 = r10 | r7
            return r10
        Ld4:
            return r3
        Ld5:
            if (r2 == r3) goto Le4
            if (r11 != r3) goto Lda
            goto Le4
        Lda:
            if (r12 < 0) goto Ldd
            r7 = r4
        Ldd:
            int r10 = r9.mCountX
            int r11 = r11 * r10
            int r2 = r2 + r11
            r10 = r7 | r2
            return r10
        Le4:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.allapps.AllAppsPagedCellLayout.getIndex(int, int, int, boolean):int");
    }

    public View getChildOnPageId(int id) {
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mShortcutsAndWidgets.getChildAt(i);
            if (childAt != null && childAt.getId() == id) {
                return childAt;
            }
        }
        return null;
    }

    public void removeViewOnPageId(int id) {
        View childOnPageId = getChildOnPageId(id);
        if (childOnPageId != null) {
            this.mShortcutsAndWidgets.removeView(childOnPageId);
            childOnPageId.setId(-1);
        }
    }

    public void setShrinkEffect(boolean bEffect) {
        this.bShrinkEffect = bEffect;
    }

    @Override // com.android.launcher3.CellLayout, android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        if (this.bShrinkEffect || getScaleX() < 1.0f) {
            if (this.mZoomBg == null) {
                this.mZoomBg = getResources().getDrawable(R.drawable.bg_homescreen_spring_loaded_normal);
            }
            Rect rect = new Rect();
            this.mZoomBg.getPadding(rect);
            int left = this.mShortcutsAndWidgets.getLeft();
            int right = this.mShortcutsAndWidgets.getRight();
            this.mZoomBg.setBounds(left - rect.left, (this.mShortcutsAndWidgets.getTop() - rect.top) - this.mZoomBg_Padding_Top, right + rect.right, this.mShortcutsAndWidgets.getBottom() + rect.bottom + this.mZoomBg_Padding_Bottom);
            this.mZoomBg.draw(canvas);
        }
        super.dispatchDraw(canvas);
    }

    public int removeNarrangePage(int index, boolean animated) {
        AllAppsPagedCellLayoutParam allAppsPagedCellLayoutParam;
        View view;
        View view2;
        int i = -1;
        if (index == -1) {
            return 0;
        }
        int pageChildCount = getPageChildCount();
        removeViewOnPageId(index);
        ArrayList<Animator> arrayList = new ArrayList<>();
        int i2 = index + 1;
        int i3 = 0;
        while (i2 < pageChildCount) {
            View childOnPageId = getChildOnPageId(i2);
            if (childOnPageId == null) {
                return i;
            }
            int i4 = (this.mCellHeight + this.mHeightGap) * (i2 / this.mCountX);
            int i5 = (this.mCellWidth + this.mWidthGap) * (i2 % this.mCountX);
            AllAppsPagedCellLayoutParam allAppsPagedCellLayoutParam2 = (AllAppsPagedCellLayoutParam) childOnPageId.getLayoutParams();
            if (animated) {
                if (allAppsPagedCellLayoutParam2.cellX == 0) {
                    allAppsPagedCellLayoutParam2.cellX = this.mCountX - 1;
                    allAppsPagedCellLayoutParam2.cellY--;
                    allAppsPagedCellLayoutParam = allAppsPagedCellLayoutParam2;
                    view = childOnPageId;
                    arrayList.add(getAnimator(childOnPageId, i5, i5 + ((this.mCellWidth + this.mWidthGap) * (this.mCountX - 1)), i4, (i4 - this.mCellHeight) - this.mHeightGap, i3));
                } else {
                    allAppsPagedCellLayoutParam = allAppsPagedCellLayoutParam2;
                    view = childOnPageId;
                    allAppsPagedCellLayoutParam.cellX--;
                    arrayList.add(getAnimator(view, i5, i5 - (this.mCellWidth + this.mWidthGap), i4, i4, i3));
                }
                i3 += 10;
            } else {
                allAppsPagedCellLayoutParam = allAppsPagedCellLayoutParam2;
                view = childOnPageId;
            }
            if (animated) {
                view2 = view;
            } else {
                int i6 = i2 - 1;
                allAppsPagedCellLayoutParam.cellX = i6 % this.mCountX;
                allAppsPagedCellLayoutParam.cellY = i6 / this.mCountX;
                allAppsPagedCellLayoutParam.isLockedToGrid = true;
                view2 = view;
                view2.setTranslationX(0.0f);
                view2.setTranslationY(0.0f);
                view2.requestLayout();
            }
            int i7 = i2 - 1;
            ((ItemInfo) view2.getTag()).cellX = i7 % this.mCountX;
            ((ItemInfo) view2.getTag()).cellY = i7 / this.mCountX;
            ((ItemInfo) view2.getTag()).requiresDbUpdate = true;
            view2.setId(i7);
            i2++;
            i = -1;
        }
        removeNarrowPageAnimation(animated, arrayList);
        if (animated) {
            return i3 + 200;
        }
        return 0;
    }

    private void removeNarrowPageAnimation(boolean animated, ArrayList<Animator> itemanimation) {
        if (!animated || itemanimation == null || itemanimation.size() <= 0) {
            return;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200L);
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.lge.launcher3.allapps.AllAppsPagedCellLayout.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator arg0) {
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator arg0) {
                AllAppsPagedCellLayout.this.enableHardwareLayer(false);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator arg0) {
                AllAppsPagedCellLayout.this.enableHardwareLayer(true);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator arg0) {
                AllAppsPagedCellLayout.this.enableHardwareLayer(true);
            }
        });
        animatorSet.playTogether(itemanimation);
        animatorSet.start();
    }

    public Rect getPositionWithId(int id) {
        Rect rect = new Rect();
        int i = id % this.mCountX;
        int i2 = id / this.mCountX;
        rect.left = (this.mCellWidth * i) + (i * this.mWidthGap) + getPaddingStart();
        rect.top = (this.mCellHeight * i2) + (i2 * this.mHeightGap) + getPaddingTop();
        rect.right = rect.left + this.mCellWidth;
        rect.bottom = rect.top + this.mCellHeight;
        return rect;
    }

    public void enableCenteredContent(boolean enabled) {
        if (this.mShortcutsAndWidgets != null) {
            this.mShortcutsAndWidgets.enableCenteredContent(enabled);
        }
    }

    public void setCellCount(int xCount, int yCount) {
        this.mCountX = xCount;
        this.mCountY = yCount;
        requestLayout();
    }

    public void setBGAlpha(int alpha) {
        if (this.mZoomBg == null) {
            this.mZoomBg = getResources().getDrawable(R.drawable.bg_homescreen_spring_loaded_normal);
        }
        this.mZoomBg.setAlpha(Math.min(255, alpha));
    }

    public void setArrangeModeBg(boolean isArranging) {
        this.mArrangeMode = isArranging;
    }

    public boolean isInArrangeMode() {
        return this.mArrangeMode;
    }

    public void endAnimation() {
        if (this.mReorderAnimators != null) {
            for (int i = 0; i < getPageChildCount(); i++) {
                Animator animator = this.mReorderAnimators.get(getChildOnPageAt(i));
                if (animator != null) {
                    animator.end();
                }
            }
            this.mReorderAnimators.clear();
        }
    }

    @Override // com.android.launcher3.CellLayout, android.view.View
    public void cancelLongPress() {
        super.cancelLongPress();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt != null) {
                childAt.cancelLongPress();
            }
        }
    }

    public int getChildrenLayerType() {
        if (this.mShortcutsAndWidgets != null) {
            return this.mShortcutsAndWidgets.getLayerType();
        }
        return 0;
    }

    public void setChildFocus() {
        View childOnPageAt = getChildOnPageAt(0, 0);
        if (childOnPageAt != null) {
            childOnPageAt.requestFocusFromTouch();
        }
    }

    public boolean addViewToCellLayout(View child, int index, int childId, AllAppsPagedCellLayoutParam params) {
        child.setOnKeyListener(new AllAppsKeyEventListener());
        if (this.mShortcutsAndWidgets == null || params.cellX < 0 || params.cellX > this.mCountX - 1 || params.cellY < 0 || params.cellY > this.mCountY - 1) {
            return false;
        }
        if (params.cellHSpan < 0) {
            params.cellHSpan = this.mCountX;
        }
        if (params.cellVSpan < 0) {
            params.cellVSpan = this.mCountY;
        }
        child.setId(childId);
        if (child.getParent() != null) {
            ((ViewGroup) child.getParent()).removeView(child);
        }
        this.mShortcutsAndWidgets.addView(child, index, params);
        child.setHapticFeedbackEnabled(false);
        return true;
    }

    @Override // com.lge.launcher3.allapps.Page
    public int getPageChildCount() {
        return this.mShortcutsAndWidgets.getChildCount();
    }

    public AllAppsPagedCellLayoutChildren getChildrenLayout() {
        return this.mChildren;
    }

    @Override // com.lge.launcher3.allapps.Page
    public View getChildOnPageAt(int i) {
        return this.mShortcutsAndWidgets.getChildAt(i);
    }

    public View getChildOnPageAt(int cellX, int cellY) {
        for (int i = 0; i < this.mShortcutsAndWidgets.getChildCount(); i++) {
            View childAt = this.mShortcutsAndWidgets.getChildAt(i);
            AllAppsPagedCellLayoutParam allAppsPagedCellLayoutParam = (AllAppsPagedCellLayoutParam) childAt.getLayoutParams();
            if (allAppsPagedCellLayoutParam.cellX == cellX && allAppsPagedCellLayoutParam.cellY == cellY) {
                return childAt;
            }
        }
        return null;
    }

    @Override // com.lge.launcher3.allapps.Page
    public void removeAllViewsOnPage() {
        this.mShortcutsAndWidgets.removeAllViews();
        enableHardwareLayer(false);
    }

    @Override // com.lge.launcher3.allapps.Page
    public void removeViewOnPageAt(int i) {
        this.mShortcutsAndWidgets.removeViewAt(i);
    }

    @Override // com.lge.launcher3.allapps.Page
    public int indexOfChildOnPage(View v) {
        return this.mShortcutsAndWidgets.indexOfChild(v);
    }

    @Override // com.android.launcher3.CellLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = View.MeasureSpec.getMode(widthMeasureSpec);
        int size = View.MeasureSpec.getSize(widthMeasureSpec);
        int mode2 = View.MeasureSpec.getMode(heightMeasureSpec);
        int size2 = View.MeasureSpec.getSize(heightMeasureSpec);
        if (mode == 0 || mode2 == 0) {
            LGLog.i(TAG, "CellLayout cannot have UNSPECIFIED dimensions. widthSpecSize = " + size + ", heightSpecSize = " + size2);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int i = this.mCountX - 1;
        int i2 = this.mCountY - 1;
        int i3 = (size - this.mPaddingLeft) - this.mPaddingRight;
        int i4 = (size2 - this.mPaddingTop) - this.mPaddingBottom;
        this.mWidthGap = getWidthGap();
        this.mHeightGap = getHeightGap();
        this.mCellWidth = (i3 - (i * this.mWidthGap)) / this.mCountX;
        this.mCellHeight = (i4 - (i2 * this.mHeightGap)) / this.mCountY;
        this.mShortcutsAndWidgets.setCellDimensions(this.mCellWidth, this.mCellHeight, this.mWidthGap, this.mHeightGap, this.mCountX, this.mCountY);
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i3, 1073741824);
        int iMakeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(i4, 1073741824);
        int childCount = getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            if (childAt != null) {
                childAt.measure(iMakeMeasureSpec, iMakeMeasureSpec2);
            }
        }
        setMeasuredDimension(size, size2);
    }

    @Override // com.android.launcher3.CellLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt != null) {
                childAt.layout(this.mPaddingLeft, this.mPaddingTop, (r - l) - this.mPaddingRight, (b - t) - this.mPaddingBottom);
            }
        }
    }

    @Override // com.android.launcher3.CellLayout, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        View childOnPageAt;
        boolean zOnTouchEvent = super.onTouchEvent(event);
        int pageChildCount = getPageChildCount();
        if (pageChildCount <= 0 || (childOnPageAt = getChildOnPageAt(pageChildCount - 1)) == null) {
            return zOnTouchEvent;
        }
        int bottom = childOnPageAt.getBottom();
        if (((int) Math.ceil(getPageChildCount() / this.mCountX)) < this.mCountY) {
            bottom += this.mCellHeight / 2;
        }
        return zOnTouchEvent || event.getY() < ((float) bottom);
    }

    @Override // com.android.launcher3.CellLayout, android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new AllAppsPagedCellLayoutParam(getContext(), attrs);
    }

    @Override // com.android.launcher3.CellLayout, android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof AllAppsPagedCellLayoutParam;
    }

    @Override // com.android.launcher3.CellLayout, android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new AllAppsPagedCellLayoutParam(p);
    }
}
