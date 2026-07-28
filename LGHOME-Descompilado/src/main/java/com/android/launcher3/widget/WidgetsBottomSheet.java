package com.android.launcher3.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.DragSource;
import com.android.launcher3.Insettable;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.VerticalPullDetector;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PropertyListBuilder;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.logging.LoggerUtils;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.TouchController;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class WidgetsBottomSheet extends AbstractFloatingView implements Insettable, TouchController, VerticalPullDetector.Listener, View.OnClickListener, View.OnLongClickListener, DragController.DragListener {
    private static final String TAG = "WidgetsBottomSheet";
    private boolean DEBUG;
    private Rect mInsets;
    private Launcher mLauncher;
    private ObjectAnimator mOpenCloseAnimator;
    private ItemInfo mOriginalItemInfo;
    private VerticalPullDetector.ScrollInterpolator mScrollInterpolator;
    private int mTranslationYClosed;
    private int mTranslationYOpen;
    private float mTranslationYRange;
    private VerticalPullDetector mVerticalPullDetector;
    private boolean mWasNavBarLight;

    @Override // com.android.launcher3.AbstractFloatingView
    protected boolean isOfType(int type) {
        return (type & 4) != 0;
    }

    @Override // com.android.launcher3.dragndrop.DragController.DragListener
    public void onDragEnd() {
    }

    @Override // com.android.launcher3.allapps.VerticalPullDetector.Listener
    public void onDragStart(boolean start) {
    }

    public WidgetsBottomSheet(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WidgetsBottomSheet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(new ContextThemeWrapper(context, R.style.WidgetsBottomSheetTheme), attrs, defStyleAttr);
        this.DEBUG = false;
        setWillNotDraw(false);
        this.mLauncher = Launcher.getLauncher(context);
        this.mOpenCloseAnimator = LauncherAnimUtils.ofPropertyValuesHolder(this, new PropertyValuesHolder[0]);
        this.mScrollInterpolator = new VerticalPullDetector.ScrollInterpolator();
        this.mInsets = new Rect();
        VerticalPullDetector verticalPullDetector = new VerticalPullDetector(context);
        this.mVerticalPullDetector = verticalPullDetector;
        verticalPullDetector.skipInOutTouchSlop();
        this.mVerticalPullDetector.setListener(this);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mTranslationYOpen = 0;
        this.mTranslationYClosed = getMeasuredHeight();
        this.mTranslationYRange = r1 - this.mTranslationYOpen;
    }

    public void populateAndShow(ItemInfo itemInfo) {
        this.mOriginalItemInfo = itemInfo;
        ((TextView) findViewById(R.id.title)).setText(getContext().getString(R.string.widgets_bottom_sheet_title, this.mOriginalItemInfo.title));
        ((TextView) findViewById(R.id.title)).setTextColor(getResources().getColor(R.color.bottomup_widget_text_color));
        onWidgetsBound();
        this.mWasNavBarLight = (this.mLauncher.getWindow().getDecorView().getSystemUiVisibility() & 16) != 0;
        this.mLauncher.getDragLayer().addView(this);
        measure(0, 0);
        setTranslationY(this.mTranslationYClosed);
        this.mIsOpen = false;
        open(true);
    }

    @Override // com.android.launcher3.AbstractFloatingView
    protected void onWidgetsBound() {
        List<WidgetItem> widgetsForPackageUser = this.mLauncher.getWidgetsForPackageUser(new PackageUserKey(this.mOriginalItemInfo.getTargetComponent().getPackageName(), this.mOriginalItemInfo.user));
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.widgets);
        ViewGroup viewGroup2 = (ViewGroup) viewGroup.findViewById(R.id.widgets_cell_list);
        viewGroup2.removeAllViews();
        for (int i = 0; i < widgetsForPackageUser.size(); i++) {
            WidgetCell widgetCellAddItemCell = addItemCell(viewGroup2);
            widgetCellAddItemCell.applyFromCellItem(widgetsForPackageUser.get(i), LauncherAppState.getInstance(this.mLauncher).getWidgetCache());
            widgetCellAddItemCell.setHeightForBottomSheet();
            widgetCellAddItemCell.ensurePreview(true);
            widgetCellAddItemCell.setVisibility(0);
            if (i < widgetsForPackageUser.size() - 1) {
                addDivider(viewGroup2);
            }
        }
        if (this.mLauncher.getDeviceProfile().isTablet || this.mLauncher.getDeviceProfile().isLargeTablet) {
            int integer = getResources().getInteger(R.integer.bottomup_widget_default_number_tablet_port);
            if (this.mLauncher.getDeviceProfile().isLandscape) {
                integer = getResources().getInteger(R.integer.bottomup_widget_default_number_tablet_land);
            }
            if (widgetsForPackageUser.size() <= integer) {
                ((LinearLayout.LayoutParams) viewGroup.getLayoutParams()).gravity = 1;
                return;
            } else {
                addPaddingLeftRight(viewGroup2, viewGroup, integer);
                return;
            }
        }
        if (widgetsForPackageUser.size() == 1) {
            ((LinearLayout.LayoutParams) viewGroup.getLayoutParams()).gravity = 1;
        } else {
            addPaddingLeftRight(viewGroup2, viewGroup, 2);
        }
    }

    private void addPaddingLeftRight(ViewGroup widgetCells, ViewGroup widgetRow, int widgetNumber) {
        View viewInflate = LayoutInflater.from(getContext()).inflate(R.layout.widget_list_divider, widgetRow, false);
        viewInflate.getLayoutParams().width = ((this.mLauncher.getDragLayer().getWidth() - (getResources().getDimensionPixelOffset(R.dimen.widget_preview_cell_size) * widgetNumber)) - ((int) Math.ceil(getResources().getDimension(R.dimen.widget_row_divider)))) / 2;
        widgetCells.addView(viewInflate, 0);
        View viewInflate2 = LayoutInflater.from(getContext()).inflate(R.layout.widget_list_divider, widgetRow, false);
        viewInflate2.getLayoutParams().width = ((this.mLauncher.getDragLayer().getWidth() - (getResources().getDimensionPixelOffset(R.dimen.widget_preview_cell_size) * widgetNumber)) - ((int) Math.ceil(getResources().getDimension(R.dimen.widget_row_divider)))) / 2;
        widgetCells.addView(viewInflate2);
    }

    private void addDivider(ViewGroup parent) {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_list_divider, parent, true);
    }

    private WidgetCell addItemCell(ViewGroup parent) {
        WidgetCell widgetCell = (WidgetCell) LayoutInflater.from(getContext()).inflate(R.layout.widget_cell_bottomup, parent, false);
        widgetCell.setWidgetCellSize(getResources().getDimensionPixelOffset(R.dimen.widget_preview_cell_size));
        widgetCell.setPaddingWidgetInBottomUp(getResources().getDimensionPixelOffset(R.dimen.widget_preview_top_padding_bottomup));
        widgetCell.mWidgetImage.getBackground().setAlpha(128);
        widgetCell.setOnClickListener(this);
        widgetCell.setOnLongClickListener(this);
        widgetCell.setAnimatePreview(false);
        parent.addView(widgetCell);
        return widgetCell;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        this.mLauncher.getWidgetsView().handleClick();
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View view) {
        if (this.mLauncher.isSafeMode()) {
            Toast.makeText(getContext(), R.string.safemode_widget_error, 0).show();
            return true;
        }
        if (this.mLauncher.getDragController().getDragListeners().contains(this)) {
            LGLog.i(TAG, "skip long click because DragListener is already added");
            return true;
        }
        this.mLauncher.getDragController().addDragListener(this);
        return this.mLauncher.getWidgetsView().handleLongClick(view);
    }

    private void open(boolean animate) {
        LGLog.d(TAG, "open: mIsOpen = " + this.mIsOpen + ", mOpenCloseAnimator.isRunning() = " + this.mOpenCloseAnimator.isRunning() + ", animate = " + animate + ", " + this);
        if (this.mIsOpen || this.mOpenCloseAnimator.isRunning()) {
            return;
        }
        this.mIsOpen = true;
        setLightNavBar(true);
        if (animate) {
            this.mOpenCloseAnimator.setValues(new PropertyListBuilder().translationY(this.mTranslationYOpen).build());
            this.mOpenCloseAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.widget.WidgetsBottomSheet.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    WidgetsBottomSheet.this.mVerticalPullDetector.finishedScrolling();
                }
            });
            this.mOpenCloseAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
            this.mOpenCloseAnimator.start();
            return;
        }
        setTranslationY(this.mTranslationYOpen);
    }

    protected void onCloseComplete(boolean animate) {
        LGLog.d(TAG, "onCloseComplete: mIsOpen = " + this.mIsOpen + ", mOpenCloseAnimator.isRunning() = " + this.mOpenCloseAnimator.isRunning() + ", animate = " + animate + ", " + this);
        this.mIsOpen = false;
        this.mLauncher.getDragLayer().removeView(this);
        this.mLauncher.getDragController().removeDragListener(this);
    }

    @Override // com.android.launcher3.AbstractFloatingView
    protected void handleClose(final boolean animate) {
        if (!this.mIsOpen) {
            LGLog.i(TAG, "skip handleClose: mIsOpen = " + this.mIsOpen + ", animate = " + animate);
            return;
        }
        if (this.mOpenCloseAnimator.isRunning()) {
            LGLog.i(TAG, "skip handleClose: Animator is running, animate = " + animate);
            this.mOpenCloseAnimator.cancel();
            onCloseComplete(animate);
            return;
        }
        if (animate) {
            this.mOpenCloseAnimator.setValues(new PropertyListBuilder().translationY(this.mTranslationYClosed).build());
            this.mOpenCloseAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.widget.WidgetsBottomSheet.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    WidgetsBottomSheet.this.mIsOpen = false;
                    WidgetsBottomSheet.this.mVerticalPullDetector.finishedScrolling();
                    WidgetsBottomSheet.this.onCloseComplete(animate);
                    WidgetsBottomSheet widgetsBottomSheet = WidgetsBottomSheet.this;
                    widgetsBottomSheet.setLightNavBar(widgetsBottomSheet.mWasNavBarLight);
                }
            });
            this.mOpenCloseAnimator.setInterpolator(this.mVerticalPullDetector.isIdleState() ? Interpolators.FAST_OUT_SLOW_IN : this.mScrollInterpolator);
            this.mOpenCloseAnimator.start();
            return;
        }
        setTranslationY(this.mTranslationYClosed);
        setLightNavBar(this.mWasNavBarLight);
        this.mIsOpen = false;
        onCloseComplete(animate);
    }

    @Override // com.android.launcher3.AbstractFloatingView
    public void logActionCommand(int command) {
        LauncherLogProto.Target targetNewContainerTarget = LoggerUtils.newContainerTarget(5);
        targetNewContainerTarget.cardinality = 1;
        this.mLauncher.getUserEventDispatcher().logActionCommand(command, targetNewContainerTarget);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setLightNavBar(boolean lightNavBar) {
        this.mLauncher.activateLightSystemBars(lightNavBar, false, true);
    }

    public static WidgetsBottomSheet getOpen(Launcher launcher) {
        return (WidgetsBottomSheet) getOpenView(launcher, 4);
    }

    @Override // com.android.launcher3.Insettable
    public void setInsets(Rect insets) {
        int i = insets.left - this.mInsets.left;
        int i2 = insets.right - this.mInsets.right;
        int i3 = insets.bottom - this.mInsets.bottom;
        this.mInsets.set(insets);
        setPadding(getPaddingLeft() + i, getPaddingTop(), getPaddingRight() + i2, getPaddingBottom() + i3);
    }

    @Override // com.android.launcher3.allapps.VerticalPullDetector.Listener
    public boolean onDrag(float displacementX, float displacementY, float velocity) {
        setTranslationY(Utilities.boundToRange(displacementY, this.mTranslationYOpen, this.mTranslationYClosed));
        return true;
    }

    @Override // com.android.launcher3.allapps.VerticalPullDetector.Listener
    public void onDragEnd(float velocity, boolean fling) {
        if ((fling && velocity > 0.0f) || getTranslationY() > this.mTranslationYRange / 2.0f) {
            this.mScrollInterpolator.setVelocityAtZero(velocity);
            this.mOpenCloseAnimator.setDuration(this.mVerticalPullDetector.calculateDuration(velocity, (this.mTranslationYClosed - getTranslationY()) / this.mTranslationYRange));
            close(true);
        } else {
            this.mIsOpen = false;
            this.mOpenCloseAnimator.setDuration(this.mVerticalPullDetector.calculateDuration(velocity, (getTranslationY() - this.mTranslationYOpen) / this.mTranslationYRange));
            open(true);
        }
    }

    @Override // com.android.launcher3.AbstractFloatingView, com.android.launcher3.util.TouchController
    public boolean onControllerTouchEvent(MotionEvent ev) {
        this.mVerticalPullDetector.onTouchEvent(ev);
        if (ev.getAction() == 1 && this.mVerticalPullDetector.isIdleState() && !this.mLauncher.getDragLayer().isEventOverView(this, ev)) {
            close(true);
        }
        return true;
    }

    @Override // com.android.launcher3.util.TouchController
    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        boolean z = false;
        this.mVerticalPullDetector.setDetectableScrollConditions(this.mVerticalPullDetector.isIdleState() ? 2 : 0, false);
        this.mVerticalPullDetector.onTouchEvent(ev);
        boolean z2 = this.mVerticalPullDetector.isDraggingOrSettling() || !this.mLauncher.getDragLayer().isEventOverView(this, ev);
        if (!this.mOpenCloseAnimator.isRunning() && z2) {
            z = true;
        }
        if (this.DEBUG && this.mOpenCloseAnimator.isRunning() && z2) {
            LGLog.d(TAG, "onControllerInterceptTouchEvent() : return = " + z + ". mOpenCloseAnimator.isRunning() = " + this.mOpenCloseAnimator.isRunning() + ", mVerticalPullDetector.isDraggingOrSettling() = " + this.mVerticalPullDetector.isDraggingOrSettling() + ", isEventOverView = " + this.mLauncher.getDragLayer().isEventOverView(this, ev) + ", this = " + this);
        }
        return z;
    }

    @Override // com.android.launcher3.dragndrop.DragController.DragListener
    public void onDragStart(DragSource source, Object info, int dragAction) {
        if (this.mLauncher.isInState(LauncherState.APPS_SPRING_LOADED)) {
            this.mLauncher.getWorkspace().setCheckSwipeUpAppDrawer(false);
            this.mLauncher.getWorkspace().backToWorkspaceFromSwipeUpAppDrawer(false);
        }
        close(true);
    }
}
