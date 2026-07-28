package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.appwidget.AppWidgetHostView;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.core.view.ViewCompat;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.Workspace;
import com.android.launcher3.accessibility.DragAndDropAccessibilityDelegate;
import com.android.launcher3.accessibility.FolderAccessibilityHelper;
import com.android.launcher3.accessibility.WorkspaceAccessibilityHelper;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LGFeatureConfig;
import com.lge.launcher3.knockoff.LGHomeGestureDetector;
import com.lge.launcher3.knockoff.LGKnockOnListener;
import com.lge.launcher3.screeneffect.IScreenEffectable;
import com.lge.launcher3.screeneffect.ScreenEffectBase;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.LGUserLog;
import com.lge.launcher3.util.PaintUtils;
import com.lge.launcher3.util.TalkBackUtils;
import com.lge.launcher3.util.VibratorManager;
import com.lge.launcher3.util.WindowUtils;
import com.lge.launcher3.views.CrossHairsGrid;
import com.lge.os.Build;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

/* JADX INFO: loaded from: classes.dex */
public class CellLayout extends ViewGroup implements BubbleTextView.BubbleTextShadowHandler, ViewGroup.OnHierarchyChangeListener, IScreenEffectable {
    private static final int BACKGROUND_ACTIVATE_DURATION = 120;
    private static final float BACKGROUND_BOUND_LINE_MAX_ALPHA = 0.85f;
    private static final boolean DESTRUCTIVE_REORDER = false;
    public static final int FOLDER_ACCESSIBILITY_DRAG = 1;
    private static final String GOOGLE_SEARCH_WIDGET_CLASSNAME = "com.google.android.googlequicksearchbox.SearchWidgetProvider";
    private static final int INVALID_DIRECTION = -100;
    static final int LANDSCAPE = 0;
    private static final float MAX_ICON_PROPORTION = 0.85f;
    public static final int MODE_ACCEPT_DROP = 4;
    public static final int MODE_DRAG_OVER = 1;
    public static final int MODE_ON_DROP = 2;
    public static final int MODE_ON_DROP_EXTERNAL = 3;
    public static final int MODE_SHOW_REORDER_HINT = 0;
    static final int PORTRAIT = 1;
    private static final int REORDER_ANIMATION_DURATION = 150;
    private static final float REORDER_PREVIEW_MAGNITUDE = 0.12f;
    static final String TAG = "CellLayout";
    public static final int WORKSPACE_ACCESSIBILITY_DRAG = 2;
    private final TransitionDrawable mBackground;
    private float mBackgroundAlpha;
    private Paint mBackgroundBoundLinePaint;
    protected int mCellHeight;
    protected int mCellWidth;
    protected int mCountX;
    protected int mCountY;
    private CrossHairsGrid mCrossHairsGrid;
    private InterruptibleInOutAnimator mCrosshairsAnimator;
    private ScreenEffectBase mCustomScreenEffect;
    private ImageButton mDefaultHomeBtn;
    private LinearLayout mDefaultHomeLayout;
    private int mDefaultHomeLayoutHeight;
    private boolean mDefaultHomeSelected;
    private int[] mDirectionVector;
    private float mDistanceForFolderCreationRatio;
    private int mDragBGColor;
    private int mDragBGOutlineColor;
    private final int[] mDragCell;
    private Paint mDragCellBGPaint;
    private Rect mDragCellRect;
    float[] mDragOutlineAlphas;
    private InterruptibleInOutAnimator[] mDragOutlineAnims;
    private int mDragOutlineCurrent;
    private final Paint mDragOutlinePaint;
    Rect[] mDragOutlines;
    public float mDragProgress;
    private boolean mDragging;
    private boolean mDrawGrid;
    private boolean mDrawWidgetPreview;
    private boolean mDropPending;
    private TimeInterpolator mEaseOutInterpolator;
    public boolean mEnableHotwordService;
    private int mFixedCellHeight;
    private int mFixedCellWidth;
    private int mFixedHeight;
    private int mFixedWidth;
    private int[] mFolderLeaveBehindCell;
    private ArrayList<FolderIcon.FolderRingAnimator> mFolderOuterRings;
    public LGHomeGestureDetector mGestures;
    private Bitmap mGlowOutline;
    private Point mGlowOutlineDrawPos;
    private boolean mHasFullscreenItem;
    protected int mHeightGap;
    private float mHotseatScale;
    private View.OnTouchListener mInterceptTouchListener;
    private ArrayList<View> mIntersectingViews;
    private float mInvalidTouchDistance;
    private boolean mIsBackgroundSelected;
    private boolean mIsDragOverlapping;
    private boolean mIsDragTarget;
    private boolean mIsHotseat;
    private boolean mItemPlacementDirty;
    private Launcher mLauncher;
    private float mMaxDistanceForFolderCreation;
    private int mMaxGap;
    protected View mMinusOneScreenPreview;
    boolean[][] mOccupied;
    private Rect mOccupiedRect;
    private int mOriginalHeightGap;
    private int mOriginalWidthGap;
    int[] mPreviousReorderDirection;
    HashMap<LayoutParams, Animator> mReorderAnimators;
    float mReorderPreviewAnimationMagnitude;
    HashMap<View, ReorderPreviewAnimation> mShakeAnimators;
    protected ShortcutAndWidgetContainer mShortcutsAndWidgets;
    private StylusEventHelper mStylusEventHelper;
    final int[] mTempLocation;
    private final Rect mTempRect;
    private final Stack<Rect> mTempRectStack;
    boolean[][] mTmpOccupied;
    final int[] mTmpPoint;
    private final Rect mTmpRect;
    private final ClickShadowView mTouchFeedbackView;
    private DragAndDropAccessibilityDelegate mTouchHelper;
    private boolean mUseTouchHelper;
    private int mVacantCellOrder;
    private boolean mVertical;
    private Drawable mWidgetDropCue;
    protected int mWidthGap;
    private static final boolean DEBUG_VISUALIZE_OCCUPIED = LGFeatureConfig.sDebugOccupiedCell;
    private static final Paint sPaint = new Paint();

    @Override // android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public CellLayout(Context context) {
        this(context, null);
    }

    public CellLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CellLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mDropPending = false;
        this.mIsDragTarget = true;
        this.mHasFullscreenItem = false;
        this.mTmpPoint = new int[2];
        this.mTempLocation = new int[2];
        this.mFolderOuterRings = new ArrayList<>();
        this.mFolderLeaveBehindCell = new int[]{-1, -1};
        this.mFixedWidth = -1;
        this.mFixedHeight = -1;
        this.mIsDragOverlapping = false;
        this.mIsBackgroundSelected = false;
        Rect[] rectArr = new Rect[4];
        this.mDragOutlines = rectArr;
        this.mDragOutlineAlphas = new float[rectArr.length];
        this.mDragOutlineAnims = new InterruptibleInOutAnimator[rectArr.length];
        this.mDragOutlineCurrent = 0;
        this.mDragOutlinePaint = new Paint();
        this.mReorderAnimators = new HashMap<>();
        this.mShakeAnimators = new HashMap<>();
        this.mItemPlacementDirty = false;
        this.mDragCell = new int[]{-1, -1};
        this.mDragging = false;
        this.mIsHotseat = false;
        this.mHotseatScale = 1.0f;
        this.mIntersectingViews = new ArrayList<>();
        this.mOccupiedRect = new Rect();
        this.mDirectionVector = new int[2];
        this.mPreviousReorderDirection = new int[2];
        this.mTempRect = new Rect();
        this.mUseTouchHelper = false;
        this.mDrawGrid = true;
        this.mDragProgress = 0.0f;
        this.mTempRectStack = new Stack<>();
        this.mCrossHairsGrid = null;
        this.mCrosshairsAnimator = null;
        this.mTmpRect = new Rect();
        this.mDrawWidgetPreview = false;
        this.mCustomScreenEffect = null;
        this.mBackgroundBoundLinePaint = null;
        this.mVacantCellOrder = -1;
        this.mVertical = false;
        this.mGestures = null;
        this.mEnableHotwordService = false;
        setWillNotDraw(false);
        setClipToPadding(false);
        Launcher launcher = (Launcher) context;
        this.mLauncher = launcher;
        DeviceProfile deviceProfile = launcher.getDeviceProfile();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attrs, R.styleable.CellLayout, defStyle, 0);
        this.mCellHeight = -1;
        this.mCellWidth = -1;
        this.mFixedCellHeight = -1;
        this.mFixedCellWidth = -1;
        this.mOriginalHeightGap = 0;
        this.mHeightGap = 0;
        this.mMaxGap = Integer.MAX_VALUE;
        this.mCountX = deviceProfile.inv.numColumns;
        int i = deviceProfile.inv.numRows;
        this.mCountY = i;
        this.mOccupied = (boolean[][]) Array.newInstance((Class<?>) boolean.class, this.mCountX, i);
        this.mTmpOccupied = (boolean[][]) Array.newInstance((Class<?>) boolean.class, this.mCountX, this.mCountY);
        int[] iArr = this.mPreviousReorderDirection;
        iArr[0] = -100;
        iArr[1] = -100;
        typedArrayObtainStyledAttributes.recycle();
        setAlwaysDrawnWithCacheEnabled(false);
        Resources resources = getResources();
        this.mHotseatScale = deviceProfile.hotseatIconSizePx / deviceProfile.iconSizePx;
        TransitionDrawable transitionDrawable = (TransitionDrawable) resources.getDrawable(R.drawable.bg_screenpanel);
        this.mBackground = transitionDrawable;
        transitionDrawable.setCallback(this);
        this.mReorderPreviewAnimationMagnitude = deviceProfile.iconSizePx * 0.12f;
        this.mEaseOutInterpolator = new DecelerateInterpolator(2.5f);
        int i2 = 0;
        while (true) {
            Rect[] rectArr2 = this.mDragOutlines;
            if (i2 >= rectArr2.length) {
                break;
            }
            rectArr2[i2] = new Rect(-1, -1, -1, -1);
            i2++;
        }
        int integer = resources.getInteger(R.integer.config_dragOutlineFadeTime);
        float integer2 = resources.getInteger(R.integer.config_dragOutlineMaxAlpha);
        Arrays.fill(this.mDragOutlineAlphas, 0.0f);
        for (final int i3 = 0; i3 < this.mDragOutlineAnims.length; i3++) {
            final InterruptibleInOutAnimator interruptibleInOutAnimator = new InterruptibleInOutAnimator(this, integer, 0.0f, integer2);
            interruptibleInOutAnimator.getAnimator().setInterpolator(this.mEaseOutInterpolator);
            interruptibleInOutAnimator.getAnimator().addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.CellLayout.1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (((Bitmap) interruptibleInOutAnimator.getTag()) == null) {
                        animation.cancel();
                        return;
                    }
                    CellLayout.this.mDragOutlineAlphas[i3] = ((Float) animation.getAnimatedValue()).floatValue();
                    CellLayout cellLayout = CellLayout.this;
                    cellLayout.invalidate(cellLayout.mDragOutlines[i3]);
                }
            });
            interruptibleInOutAnimator.getAnimator().addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.CellLayout.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (((Float) ((ValueAnimator) animation).getAnimatedValue()).floatValue() == 0.0f) {
                        interruptibleInOutAnimator.setTag(null);
                    }
                }
            });
            this.mDragOutlineAnims[i3] = interruptibleInOutAnimator;
        }
        this.mShortcutsAndWidgets = new ShortcutAndWidgetContainer(context);
        if (com.lge.launcher3.util.Utilities.isLGUI7_1()) {
            this.mDefaultHomeLayout = new LinearLayout(context);
            this.mDefaultHomeLayoutHeight = context.getResources().getDimensionPixelOffset(R.dimen.overview_default_screen_layout_height);
            ImageButton imageButton = new ImageButton(context);
            this.mDefaultHomeBtn = imageButton;
            imageButton.setImageResource(R.drawable.btn_homescreen_home_edit_off);
            this.mDefaultHomeBtn.setBackgroundColor(0);
            this.mDefaultHomeBtn.setContentDescription(getResources().getString(R.string.set_default_screen));
            this.mDefaultHomeBtn.setOnClickListener(new View.OnClickListener() { // from class: com.android.launcher3.CellLayout.3
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    Workspace workspace = CellLayout.this.mLauncher.getWorkspace();
                    if (CellLayout.this.mDefaultHomeSelected || workspace.isSwitchingState() || workspace.isScrolling()) {
                        return;
                    }
                    workspace.setDefaultPage(workspace.getCurrentPage());
                    LGUserLog.send(v.getContext(), LGUserLog.FEATURENAME_DEFAULTSCREEN, workspace.getCurrentPage());
                }
            });
            this.mDefaultHomeLayout.setGravity(17);
            this.mDefaultHomeLayout.addView(this.mDefaultHomeBtn);
            this.mDefaultHomeLayout.setAlpha(0.0f);
            this.mDefaultHomeSelected = false;
            addView(this.mDefaultHomeLayout);
        }
        this.mShortcutsAndWidgets.setCellDimensions(this.mCellWidth, this.mCellHeight, this.mWidthGap, this.mHeightGap, this.mCountX, this.mCountY);
        this.mStylusEventHelper = new StylusEventHelper(new SimpleOnStylusPressListener(this), this);
        ClickShadowView clickShadowView = new ClickShadowView(context);
        this.mTouchFeedbackView = clickShadowView;
        addView(clickShadowView);
        addView(this.mShortcutsAndWidgets);
        this.mGlowOutlineDrawPos = new Point();
        this.mDragCellBGPaint = new Paint();
        this.mDragBGColor = resources.getColor(R.color.workspace_drag_cellLayout_bg_color);
        this.mDragBGOutlineColor = resources.getColor(R.color.workspace_drag_cellLayout_bg_outline_color);
        if (this.mGestures == null && LGHomeFeature.Config.FEATURE_USE_KNOCK_OFF.getValue()) {
            this.mGestures = new LGHomeGestureDetector(context, new LGKnockOnListener(context));
        }
        onCellLayoutCreated();
    }

    public void enableAccessibleDrag(boolean enable, int dragType) {
        this.mUseTouchHelper = enable;
        if (!enable) {
            ViewCompat.setAccessibilityDelegate(this, null);
            setImportantForAccessibility(2);
            getShortcutsAndWidgets().setImportantForAccessibility(2);
            setOnClickListener(this.mLauncher);
        } else {
            if (dragType == 2 && !(this.mTouchHelper instanceof WorkspaceAccessibilityHelper)) {
                this.mTouchHelper = new WorkspaceAccessibilityHelper(this);
            } else if (dragType == 1 && !(this.mTouchHelper instanceof FolderAccessibilityHelper)) {
                this.mTouchHelper = new FolderAccessibilityHelper(this);
            }
            ViewCompat.setAccessibilityDelegate(this, this.mTouchHelper);
            setImportantForAccessibility(1);
            getShortcutsAndWidgets().setImportantForAccessibility(1);
            setOnClickListener(this.mTouchHelper);
        }
        if (getParent() != null) {
            getParent().notifySubtreeAccessibilityStateChanged(this, this, 1);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchHoverEvent(MotionEvent event) {
        if (this.mUseTouchHelper && this.mTouchHelper.dispatchHoverEvent(event)) {
            return true;
        }
        return super.dispatchHoverEvent(event);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        View.OnTouchListener onTouchListener;
        return (isHotseat() && !isValidTouchArea(ev)) || this.mUseTouchHelper || ((onTouchListener = this.mInterceptTouchListener) != null && onTouchListener.onTouch(this, ev));
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        LGHomeGestureDetector lGHomeGestureDetector;
        Workspace.State workspaceState = getWorkspaceState();
        if (workspaceState != null && workspaceState == Workspace.State.NORMAL && (lGHomeGestureDetector = this.mGestures) != null && lGHomeGestureDetector.onTouchEvent(ev)) {
            return true;
        }
        boolean zOnTouchEvent = super.onTouchEvent(ev);
        if (this.mLauncher.mWorkspace.isInOverviewMode() && this.mStylusEventHelper.onMotionEvent(ev)) {
            return true;
        }
        return zOnTouchEvent;
    }

    public void enableHardwareLayer(boolean hasLayer) {
        this.mShortcutsAndWidgets.setLayerType(hasLayer ? 2 : 0, sPaint);
    }

    public void buildHardwareLayer() {
        this.mShortcutsAndWidgets.buildLayer();
    }

    public float getChildrenScale() {
        if (this.mIsHotseat) {
            return this.mHotseatScale;
        }
        return 1.0f;
    }

    public void setCellDimensions(int width, int height) {
        this.mCellWidth = width;
        this.mFixedCellWidth = width;
        this.mCellHeight = height;
        this.mFixedCellHeight = height;
        this.mShortcutsAndWidgets.setCellDimensions(width, height, this.mWidthGap, this.mHeightGap, this.mCountX, this.mCountY);
    }

    public void setGridSize(int x, int y) {
        this.mCountX = x;
        this.mCountY = y;
        this.mOccupied = (boolean[][]) Array.newInstance((Class<?>) boolean.class, x, y);
        this.mTmpOccupied = (boolean[][]) Array.newInstance((Class<?>) boolean.class, this.mCountX, this.mCountY);
        this.mTempRectStack.clear();
        this.mShortcutsAndWidgets.setCellDimensions(this.mCellWidth, this.mCellHeight, this.mWidthGap, this.mHeightGap, this.mCountX, this.mCountY);
        requestLayout();
    }

    public void setInvertIfRtl(boolean invert) {
        this.mShortcutsAndWidgets.setInvertIfRtl(invert);
    }

    public void setDropPending(boolean pending) {
        this.mDropPending = pending;
    }

    public boolean isDropPending() {
        return this.mDropPending;
    }

    @Override // com.android.launcher3.BubbleTextView.BubbleTextShadowHandler
    public void setPressedIcon(BubbleTextView icon, Bitmap background) {
        if (LGHomeFeature.Config.FEATURE_USE_LAUNCH_ANIMATION.getValue() || icon == null || background == null) {
            this.mTouchFeedbackView.setBitmap(null);
            this.mTouchFeedbackView.animate().cancel();
        } else if (this.mTouchFeedbackView.setBitmap(background)) {
            this.mTouchFeedbackView.alignWithIconView(icon, this.mShortcutsAndWidgets);
            this.mTouchFeedbackView.animateShadow();
        }
    }

    void disableDragTarget() {
        this.mIsDragTarget = false;
    }

    public boolean isDragTarget() {
        return this.mIsDragTarget;
    }

    void setIsDragOverlapping(boolean isDragOverlapping) {
        if (this.mIsDragOverlapping != isDragOverlapping) {
            this.mIsDragOverlapping = isDragOverlapping;
            if (isDragOverlapping) {
                this.mBackground.startTransition(120);
            } else {
                this.mBackground.reverseTransition(120);
            }
            invalidate();
        }
        if (this.mIsDragOverlapping) {
            return;
        }
        clearRect(isDragOverlapping);
        this.mDragCellRect = null;
        this.mGlowOutline = null;
    }

    public boolean getIsDragOverlapping() {
        return this.mIsDragOverlapping;
    }

    public void setDrawGrid(Boolean drawGrid) {
        this.mDrawGrid = drawGrid.booleanValue();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        CrossHairsGrid crossHairsGrid;
        if (this.mBackgroundAlpha > 0.0f || ((crossHairsGrid = this.mCrossHairsGrid) != null && crossHairsGrid.visible())) {
            initDrawable();
        }
        if (this.mDrawGrid) {
            drawGrid(canvas);
        }
        if (this.mIsDragTarget) {
            if (this.mBackgroundAlpha > 0.0f) {
                this.mBackground.draw(canvas);
                drawBackgroundOutlineInOverviewMode(canvas, this.mBackground);
            }
            Paint paint = this.mDragOutlinePaint;
            int i = 0;
            while (true) {
                Rect[] rectArr = this.mDragOutlines;
                if (i >= rectArr.length) {
                    break;
                }
                float f = this.mDragOutlineAlphas[i];
                if (f > 0.0f) {
                    this.mTempRect.set(rectArr[i]);
                    Utilities.scaleRectAboutCenter(this.mTempRect, getChildrenScale());
                    Bitmap bitmap = (Bitmap) this.mDragOutlineAnims[i].getTag();
                    paint.setAlpha((int) (f + 0.5f));
                    canvas.drawBitmap(bitmap, (Rect) null, this.mTempRect, paint);
                }
                i++;
            }
            if (DEBUG_VISUALIZE_OCCUPIED) {
                int[] iArr = new int[2];
                ColorDrawable colorDrawable = new ColorDrawable(570490624);
                colorDrawable.setBounds(0, 0, this.mCellWidth, this.mCellHeight);
                for (int i2 = 0; i2 < this.mCountX; i2++) {
                    for (int i3 = 0; i3 < this.mCountY; i3++) {
                        if (this.mOccupied[i2][i3]) {
                            cellToPoint(i2, i3, iArr);
                            canvas.save();
                            canvas.translate(iArr[0], iArr[1]);
                            colorDrawable.draw(canvas);
                            canvas.restore();
                        }
                    }
                }
            }
            int i4 = FolderIcon.FolderRingAnimator.sPreviewSize;
            DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
            for (int i5 = 0; i5 < this.mFolderOuterRings.size(); i5++) {
                FolderIcon.FolderRingAnimator folderRingAnimator = this.mFolderOuterRings.get(i5);
                cellToPoint(folderRingAnimator.mCellX, folderRingAnimator.mCellY, this.mTempLocation);
                View childAt = getChildAt(folderRingAnimator.mCellX, folderRingAnimator.mCellY);
                if (childAt != null) {
                    int[] iArr2 = this.mTempLocation;
                    int i6 = iArr2[0] + (this.mCellWidth / 2);
                    int paddingTop = iArr2[1] + (i4 / 2) + childAt.getPaddingTop() + deviceProfile.folderBackgroundOffset;
                    Drawable drawable = FolderIcon.FolderRingAnimator.sSharedInnerRingDrawable;
                    int innerRingSize = (int) (folderRingAnimator.getInnerRingSize() * getChildrenScale());
                    canvas.save();
                    int i7 = innerRingSize / 2;
                    canvas.translate(i6 - i7, paddingTop - i7);
                    drawable.setBounds(0, 0, innerRingSize, innerRingSize);
                    drawable.draw(canvas);
                    canvas.restore();
                }
            }
            int[] iArr3 = this.mFolderLeaveBehindCell;
            if (iArr3[0] < 0 || iArr3[1] < 0) {
                return;
            }
            Drawable drawable2 = FolderIcon.sSharedFolderLeaveBehind;
            int intrinsicWidth = drawable2.getIntrinsicWidth();
            int intrinsicHeight = drawable2.getIntrinsicHeight();
            int[] iArr4 = this.mFolderLeaveBehindCell;
            cellToPoint(iArr4[0], iArr4[1], this.mTempLocation);
            int[] iArr5 = this.mFolderLeaveBehindCell;
            View childAt2 = getChildAt(iArr5[0], iArr5[1]);
            if (childAt2 != null) {
                int[] iArr6 = this.mTempLocation;
                int i8 = iArr6[0] + (this.mCellWidth / 2);
                int paddingTop2 = iArr6[1] + (i4 / 2) + childAt2.getPaddingTop() + deviceProfile.folderBackgroundOffset;
                canvas.save();
                int i9 = intrinsicWidth / 2;
                canvas.translate(i8 - i9, paddingTop2 - i9);
                drawable2.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
                drawable2.draw(canvas);
                canvas.restore();
            }
        }
    }

    public void showFolderAccept(FolderIcon.FolderRingAnimator fra) {
        this.mFolderOuterRings.add(fra);
    }

    public void hideFolderAccept(FolderIcon.FolderRingAnimator fra) {
        if (this.mFolderOuterRings.contains(fra)) {
            this.mFolderOuterRings.remove(fra);
        }
        invalidate();
    }

    public void setFolderLeaveBehindCell(int x, int y) {
        int[] iArr = this.mFolderLeaveBehindCell;
        iArr[0] = x;
        iArr[1] = y;
        invalidate();
    }

    public void clearFolderLeaveBehind() {
        int[] iArr = this.mFolderLeaveBehindCell;
        iArr[0] = -1;
        iArr[1] = -1;
        invalidate();
    }

    public void restoreInstanceState(SparseArray<Parcelable> states) {
        try {
            dispatchRestoreInstanceState(states);
        } catch (IllegalArgumentException e) {
            if (LauncherAppState.isDogfoodBuild()) {
                throw e;
            }
            Log.e(TAG, "Ignoring an error while restoring a view instance state", e);
        }
    }

    @Override // android.view.View
    public void cancelLongPress() {
        super.cancelLongPress();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).cancelLongPress();
        }
    }

    public void setOnInterceptTouchListener(View.OnTouchListener listener) {
        this.mInterceptTouchListener = listener;
    }

    public int getCountX() {
        return this.mCountX;
    }

    public int getCountY() {
        return this.mCountY;
    }

    public void setIsHotseat(boolean isHotseat) {
        this.mIsHotseat = isHotseat;
        this.mShortcutsAndWidgets.setIsHotseat(isHotseat);
        if (isHotseat) {
            adjustGridSize();
            getShortcutsAndWidgets().setClipChildren(false);
            DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
            TypedValue typedValue = new TypedValue();
            getResources().getValue(R.dimen.config_folderDistanceRatioSmall_hotseat, typedValue, true);
            float f = typedValue.getFloat();
            this.mDistanceForFolderCreationRatio = f;
            this.mMaxDistanceForFolderCreation = f * deviceProfile.iconSizePx;
        }
    }

    public boolean isHotseat() {
        return this.mIsHotseat;
    }

    public boolean addViewToCellLayout(View child, int index, int childId, LayoutParams params, boolean markCells) {
        if (isHotseat()) {
            return addViewToHotseatCellLayout(child, index, childId, params);
        }
        this.mHasFullscreenItem = params.isFullscreen;
        if (child instanceof BubbleTextView) {
            BubbleTextView bubbleTextView = (BubbleTextView) child;
            if (this.mIsHotseat) {
                bubbleTextView.setTextVisibility(this.mLauncher.getDeviceProfile().isLandscape && this.mLauncher.getDeviceProfile().allowRotation);
            } else {
                bubbleTextView.setTextVisibility(true);
            }
        }
        child.setScaleX(getChildrenScale());
        child.setScaleY(getChildrenScale());
        if (params.cellX < 0 || params.cellX > this.mCountX - 1 || params.cellY < 0 || params.cellY > this.mCountY - 1) {
            return false;
        }
        if (params.cellHSpan < 0) {
            params.cellHSpan = this.mCountX;
        }
        if (params.cellVSpan < 0) {
            params.cellVSpan = this.mCountY;
        }
        child.setId(childId);
        this.mShortcutsAndWidgets.addView(child, index, params);
        if (markCells) {
            markCellsAsOccupiedForView(child);
        }
        this.mLauncher.getHotword().updateHotwordDetection(this);
        return true;
    }

    @Override // android.view.ViewGroup
    public void removeAllViews() {
        clearOccupiedCells();
        this.mShortcutsAndWidgets.removeAllViews();
        this.mLauncher.getHotword().updateHotwordDetection(this);
    }

    @Override // android.view.ViewGroup
    public void removeAllViewsInLayout() {
        if (this.mShortcutsAndWidgets.getChildCount() > 0) {
            clearOccupiedCells();
            this.mShortcutsAndWidgets.removeAllViewsInLayout();
        }
        this.mLauncher.getHotword().updateHotwordDetection(this);
    }

    @Override // android.view.ViewGroup, android.view.ViewManager
    public void removeView(View view) {
        markCellsAsUnoccupiedForView(view);
        this.mShortcutsAndWidgets.removeView(view);
        this.mLauncher.getHotword().updateHotwordDetection(this);
        if (isHotseat()) {
            rearrangeChildren(false, false);
        }
    }

    @Override // android.view.ViewGroup
    public void removeViewAt(int index) {
        markCellsAsUnoccupiedForView(this.mShortcutsAndWidgets.getChildAt(index));
        this.mShortcutsAndWidgets.removeViewAt(index);
        this.mLauncher.getHotword().updateHotwordDetection(this);
    }

    @Override // android.view.ViewGroup
    public void removeViewInLayout(View view) {
        markCellsAsUnoccupiedForView(view);
        this.mShortcutsAndWidgets.removeViewInLayout(view);
        if (isHotseat()) {
            rearrangeChildren(false, false);
        }
        this.mLauncher.getHotword().updateHotwordDetection(this);
    }

    @Override // android.view.ViewGroup
    public void removeViews(int start, int count) {
        for (int i = start; i < start + count; i++) {
            markCellsAsUnoccupiedForView(this.mShortcutsAndWidgets.getChildAt(i));
        }
        this.mShortcutsAndWidgets.removeViews(start, count);
        this.mLauncher.getHotword().updateHotwordDetection(this);
    }

    @Override // android.view.ViewGroup
    public void removeViewsInLayout(int start, int count) {
        for (int i = start; i < start + count; i++) {
            markCellsAsUnoccupiedForView(this.mShortcutsAndWidgets.getChildAt(i));
        }
        this.mShortcutsAndWidgets.removeViewsInLayout(start, count);
        this.mLauncher.getHotword().updateHotwordDetection(this);
    }

    public void pointToCellExact(int x, int y, int[] result) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        result[0] = (x - paddingLeft) / (this.mCellWidth + this.mWidthGap);
        result[1] = (y - paddingTop) / (this.mCellHeight + this.mHeightGap);
        int i = this.mCountX;
        int i2 = this.mCountY;
        if (result[0] < 0) {
            result[0] = 0;
        }
        if (result[0] >= i) {
            result[0] = i - 1;
        }
        if (result[1] < 0) {
            result[1] = 0;
        }
        if (result[1] >= i2) {
            result[1] = i2 - 1;
        }
    }

    void pointToCellRounded(int x, int y, int[] result) {
        pointToCellExact(x + (this.mCellWidth / 2), y + (this.mCellHeight / 2), result);
    }

    void cellToPoint(int cellX, int cellY, int[] result) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        Workspace.State workspaceState = getWorkspaceState();
        result[0] = paddingLeft + (cellX * (this.mCellWidth + this.mWidthGap));
        if (com.lge.launcher3.util.Utilities.isLGUI7_1() && workspaceState != null && (workspaceState == Workspace.State.OVERVIEW || workspaceState == Workspace.State.OVERVIEW_HIDDEN)) {
            result[1] = paddingTop + (cellY * (this.mCellHeight + this.mHeightGap)) + this.mDefaultHomeLayoutHeight;
        } else {
            result[1] = paddingTop + (cellY * (this.mCellHeight + this.mHeightGap));
        }
    }

    void cellToCenterPoint(int cellX, int cellY, int[] result) {
        regionToCenterPoint(cellX, cellY, 1, 1, result);
    }

    void regionToCenterPoint(int cellX, int cellY, int spanX, int spanY, int[] result) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        View childAt = getChildAt(cellX, cellY);
        int i = 0;
        if (this.mShortcutsAndWidgets.isLayoutHorizontal(childAt)) {
            result[0] = paddingLeft + (cellX * (this.mCellWidth + this.mWidthGap)) + childAt.getPaddingStart() + (this.mIsHotseat ? this.mLauncher.getDeviceProfile().hotseatIconSizePx / 2 : this.mLauncher.getDeviceProfile().iconSizePx / 2);
        } else {
            int i2 = this.mCellWidth;
            int i3 = this.mWidthGap;
            result[0] = paddingLeft + (cellX * (i2 + i3)) + (((i2 * spanX) + ((spanX - 1) * i3)) / 2);
        }
        Workspace.State workspaceState = getWorkspaceState();
        if (com.lge.launcher3.util.Utilities.isLGUI7_1() && workspaceState != null && (workspaceState == Workspace.State.OVERVIEW || workspaceState == Workspace.State.OVERVIEW_HIDDEN)) {
            i = this.mDefaultHomeLayoutHeight;
        }
        int i4 = this.mCellHeight;
        int i5 = this.mHeightGap;
        result[1] = paddingTop + (cellY * (i4 + i5)) + (((i4 * spanY) + ((spanY - 1) * i5)) / 2) + i;
    }

    void regionToRect(int cellX, int cellY, int spanX, int spanY, Rect result) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int i = paddingLeft + (cellX * (this.mCellWidth + this.mWidthGap));
        Workspace.State workspaceState = getWorkspaceState();
        int i2 = (com.lge.launcher3.util.Utilities.isLGUI7_1() && workspaceState != null && (workspaceState == Workspace.State.OVERVIEW || workspaceState == Workspace.State.OVERVIEW_HIDDEN)) ? this.mDefaultHomeLayoutHeight : 0;
        int i3 = this.mCellHeight;
        int i4 = this.mHeightGap;
        int i5 = paddingTop + (cellY * (i3 + i4)) + i2;
        result.set(i, i5, (this.mCellWidth * spanX) + ((spanX - 1) * this.mWidthGap) + i, (i3 * spanY) + ((spanY - 1) * i4) + i5);
    }

    public float getDistanceFromCell(float x, float y, int[] cell) {
        double dAbs;
        cellToCenterPoint(cell[0], cell[1], this.mTmpPoint);
        if (this.mLauncher.getDeviceProfile().isLandscape || this.mIsHotseat) {
            dAbs = y - this.mTmpPoint[1];
        } else {
            dAbs = ((double) (y - this.mTmpPoint[1])) - (((double) Math.abs(this.mCellHeight - this.mCellWidth)) / 2.5d);
        }
        return (float) Math.hypot(x - this.mTmpPoint[0], dAbs);
    }

    public int getCellWidth() {
        return this.mCellWidth;
    }

    public int getCellHeight() {
        return this.mCellHeight;
    }

    public int getWidthGap() {
        return this.mWidthGap;
    }

    public int getHeightGap() {
        return this.mHeightGap;
    }

    public void setFixedSize(int width, int height) {
        this.mFixedWidth = width;
        this.mFixedHeight = height;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i;
        int i2;
        int mode = View.MeasureSpec.getMode(widthMeasureSpec);
        int mode2 = View.MeasureSpec.getMode(heightMeasureSpec);
        int size = View.MeasureSpec.getSize(widthMeasureSpec);
        int size2 = View.MeasureSpec.getSize(heightMeasureSpec);
        int paddingLeft = size - (getPaddingLeft() + getPaddingRight());
        int paddingTop = size2 - (getPaddingTop() + getPaddingBottom());
        Workspace.State workspaceState = getWorkspaceState();
        if (com.lge.launcher3.util.Utilities.isLGUI7_1() && !this.mIsHotseat && !this.mHasFullscreenItem && workspaceState != null && (workspaceState == Workspace.State.OVERVIEW || workspaceState == Workspace.State.OVERVIEW_HIDDEN)) {
            measureCellDimension(paddingLeft, paddingTop - this.mDefaultHomeLayoutHeight);
        } else {
            measureCellDimension(paddingLeft, paddingTop);
        }
        int i3 = this.mFixedWidth;
        if (i3 <= 0 || (i = this.mFixedHeight) <= 0) {
            if (mode == 0 || mode2 == 0) {
                throw new RuntimeException("CellLayout cannot have UNSPECIFIED dimensions");
            }
            i3 = paddingLeft;
            i = paddingTop;
        }
        int i4 = this.mCountX;
        int i5 = i4 - 1;
        int i6 = this.mCountY;
        int i7 = i6 - 1;
        int i8 = this.mOriginalWidthGap;
        if (i8 < 0 || (i2 = this.mOriginalHeightGap) < 0) {
            int i9 = paddingLeft - (i4 * this.mCellWidth);
            int i10 = paddingTop - (i6 * this.mCellHeight);
            this.mWidthGap = Math.min(this.mMaxGap, i5 > 0 ? i9 / i5 : 0);
            int iMin = Math.min(this.mMaxGap, i7 > 0 ? i10 / i7 : 0);
            this.mHeightGap = iMin;
            this.mShortcutsAndWidgets.setCellDimensions(this.mCellWidth, this.mCellHeight, this.mWidthGap, iMin, this.mCountX, this.mCountY);
        } else {
            this.mWidthGap = i8;
            this.mHeightGap = i2;
        }
        ClickShadowView clickShadowView = this.mTouchFeedbackView;
        clickShadowView.measure(View.MeasureSpec.makeMeasureSpec(this.mCellWidth + clickShadowView.getExtraSize(), 1073741824), View.MeasureSpec.makeMeasureSpec(this.mCellHeight + this.mTouchFeedbackView.getExtraSize(), 1073741824));
        if (com.lge.launcher3.util.Utilities.isLGUI7_1() && !this.mIsHotseat && !this.mHasFullscreenItem && workspaceState != null && (workspaceState == Workspace.State.OVERVIEW || workspaceState == Workspace.State.OVERVIEW_HIDDEN)) {
            this.mDefaultHomeLayout.measure(View.MeasureSpec.makeMeasureSpec(i3, 1073741824), View.MeasureSpec.makeMeasureSpec(this.mDefaultHomeLayoutHeight, 1073741824));
            this.mShortcutsAndWidgets.measure(View.MeasureSpec.makeMeasureSpec(i3, 1073741824), View.MeasureSpec.makeMeasureSpec(i - this.mDefaultHomeLayoutHeight, 1073741824));
        } else {
            this.mShortcutsAndWidgets.measure(View.MeasureSpec.makeMeasureSpec(i3, 1073741824), View.MeasureSpec.makeMeasureSpec(i, 1073741824));
        }
        int measuredWidth = this.mShortcutsAndWidgets.getMeasuredWidth();
        int measuredHeight = this.mShortcutsAndWidgets.getMeasuredHeight();
        View view = this.mMinusOneScreenPreview;
        if (view != null) {
            view.measure(View.MeasureSpec.makeMeasureSpec(i3, 1073741824), View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
        }
        if (this.mFixedWidth > 0 && this.mFixedHeight > 0) {
            setMeasuredDimension(measuredWidth, measuredHeight);
        } else {
            setMeasuredDimension(size, size2);
        }
    }

    private void measureCellDimension(int childWidthSize, int childHeightSize) {
        if (this.mFixedCellWidth < 0 || this.mFixedCellHeight < 0) {
            int iCalculateCellWidth = DeviceProfile.calculateCellWidth(childWidthSize, this.mCountX);
            int iCalculateCellHeight = DeviceProfile.calculateCellHeight(childHeightSize, this.mCountY);
            if (iCalculateCellWidth != this.mCellWidth || iCalculateCellHeight != this.mCellHeight) {
                this.mCellWidth = iCalculateCellWidth;
                this.mCellHeight = iCalculateCellHeight;
                this.mShortcutsAndWidgets.setCellDimensions(iCalculateCellWidth, iCalculateCellHeight, this.mWidthGap, this.mHeightGap, this.mCountX, this.mCountY);
            }
        }
        calculateChildrenScale();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int displayWidth = this.mCountX * this.mCellWidth;
        if (this.mHasFullscreenItem && LGHomeFeature.Config.FEATURE_USE_QMEMOPLUS_PANEL.getValue()) {
            displayWidth = WindowUtils.getDisplayWidth((Activity) this.mLauncher);
        }
        int paddingLeft = getPaddingLeft() + ((int) Math.ceil((((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - displayWidth) / 2.0f));
        int paddingTop = getPaddingTop();
        ClickShadowView clickShadowView = this.mTouchFeedbackView;
        clickShadowView.layout(paddingLeft, paddingTop, clickShadowView.getMeasuredWidth() + paddingLeft, this.mTouchFeedbackView.getMeasuredHeight() + paddingTop);
        if (com.lge.launcher3.util.Utilities.isLGUI7_1() && !this.mIsHotseat) {
            Workspace.State workspaceState = getWorkspaceState();
            if (!this.mHasFullscreenItem && workspaceState != null && (workspaceState == Workspace.State.OVERVIEW || workspaceState == Workspace.State.OVERVIEW_HIDDEN)) {
                LinearLayout linearLayout = this.mDefaultHomeLayout;
                int i = (paddingLeft + r) - l;
                linearLayout.layout(paddingLeft, paddingTop, i, linearLayout.getMeasuredHeight() + paddingTop);
                this.mShortcutsAndWidgets.layout(paddingLeft, this.mDefaultHomeLayout.getMeasuredHeight() + paddingTop, i, (paddingTop + b) - t);
            } else {
                this.mShortcutsAndWidgets.layout(paddingLeft, paddingTop, (paddingLeft + r) - l, (paddingTop + b) - t);
            }
        } else {
            this.mShortcutsAndWidgets.layout(paddingLeft, paddingTop, (paddingLeft + r) - l, (paddingTop + b) - t);
        }
        View view = this.mMinusOneScreenPreview;
        if (view != null) {
            view.layout(paddingLeft, paddingTop, (r + paddingLeft) - l, (b + paddingTop) - t);
        }
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mBackground.getPadding(this.mTempRect);
        this.mBackground.setBounds(-this.mTempRect.left, -this.mTempRect.top, w + this.mTempRect.right, h + this.mTempRect.bottom);
    }

    @Override // android.view.ViewGroup
    protected void setChildrenDrawingCacheEnabled(boolean enabled) {
        this.mShortcutsAndWidgets.setChildrenDrawingCacheEnabled(enabled);
    }

    @Override // android.view.ViewGroup
    protected void setChildrenDrawnWithCacheEnabled(boolean enabled) {
        this.mShortcutsAndWidgets.setChildrenDrawnWithCacheEnabled(enabled);
    }

    public Drawable getScrimBackground() {
        return this.mBackground;
    }

    public float getBackgroundAlpha() {
        return this.mBackgroundAlpha;
    }

    public void setBackgroundAlpha(float alpha) {
        if (this.mBackgroundAlpha != alpha) {
            this.mBackgroundAlpha = alpha;
            this.mBackground.setAlpha((int) (alpha * 255.0f));
        }
        View view = this.mMinusOneScreenPreview;
        if (view != null) {
            view.setAlpha((int) (this.mBackgroundAlpha * 255.0f));
        }
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || (this.mIsDragTarget && who == this.mBackground);
    }

    @Override // com.lge.launcher3.screeneffect.IScreenEffectable
    public void setShortcutAndWidgetAlpha(float alpha) {
        this.mShortcutsAndWidgets.setAlpha(alpha);
    }

    public ShortcutAndWidgetContainer getShortcutsAndWidgets() {
        return this.mShortcutsAndWidgets;
    }

    public View getChildAt(int x, int y) {
        return this.mShortcutsAndWidgets.getChildAt(x, y);
    }

    public boolean animateChildToPosition(final View child, int cellX, int cellY, int duration, int delay, boolean permanent, boolean adjustOccupied) {
        boolean z;
        ShortcutAndWidgetContainer shortcutsAndWidgets = getShortcutsAndWidgets();
        boolean[][] zArr = this.mOccupied;
        if (!permanent) {
            zArr = this.mTmpOccupied;
        }
        if (shortcutsAndWidgets.indexOfChild(child) == -1) {
            return false;
        }
        final LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
        ItemInfo itemInfo = (ItemInfo) child.getTag();
        if (this.mReorderAnimators.containsKey(layoutParams)) {
            this.mReorderAnimators.get(layoutParams).cancel();
            this.mReorderAnimators.remove(layoutParams);
        }
        final int i = layoutParams.x;
        final int i2 = layoutParams.y;
        final int i3 = layoutParams.width;
        final int i4 = layoutParams.height;
        final float scaleX = child.getScaleX();
        final float scaleY = child.getScaleY();
        if (adjustOccupied) {
            zArr[layoutParams.cellX][layoutParams.cellY] = false;
            z = true;
            zArr[cellX][cellY] = true;
        } else {
            z = true;
        }
        layoutParams.isLockedToGrid = z;
        if (permanent) {
            itemInfo.cellX = cellX;
            layoutParams.cellX = cellX;
            itemInfo.cellY = cellY;
            layoutParams.cellY = cellY;
        } else {
            layoutParams.tmpCellX = cellX;
            layoutParams.tmpCellY = cellY;
        }
        shortcutsAndWidgets.setupLp(layoutParams);
        layoutParams.isLockedToGrid = false;
        final int i5 = layoutParams.x;
        final int i6 = layoutParams.y;
        final int i7 = layoutParams.width;
        final int i8 = layoutParams.height;
        final float childrenScale = getChildrenScale();
        final float childrenScale2 = getChildrenScale();
        layoutParams.x = i;
        layoutParams.y = i2;
        layoutParams.width = i3;
        layoutParams.height = i4;
        if (i == i5 && i2 == i6 && i3 == i7 && i4 == i8) {
            layoutParams.isLockedToGrid = true;
            return false;
        }
        ValueAnimator valueAnimatorOfFloat = LauncherAnimUtils.ofFloat(child, 0.0f, 1.0f);
        valueAnimatorOfFloat.setDuration(duration);
        valueAnimatorOfFloat.setInterpolator(new OvershootInterpolator(1.0f));
        this.mReorderAnimators.put(layoutParams, valueAnimatorOfFloat);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.CellLayout.4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                float fFloatValue = ((Float) animation.getAnimatedValue()).floatValue();
                float f = 1.0f - fFloatValue;
                layoutParams.x = (int) ((i * f) + (i5 * fFloatValue));
                layoutParams.y = (int) ((i2 * f) + (i6 * fFloatValue));
                layoutParams.width = (int) ((i3 * f) + (i7 * fFloatValue));
                layoutParams.height = (int) ((i4 * f) + (i8 * fFloatValue));
                child.setScaleX((scaleX * f) + (childrenScale * fFloatValue));
                child.setScaleY((f * scaleY) + (fFloatValue * childrenScale2));
                child.requestLayout();
            }
        });
        valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.CellLayout.5
            boolean cancelled = false;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (!this.cancelled) {
                    layoutParams.isLockedToGrid = true;
                    child.requestLayout();
                }
                if (CellLayout.this.mReorderAnimators.containsKey(layoutParams)) {
                    CellLayout.this.mReorderAnimators.remove(layoutParams);
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                this.cancelled = true;
            }
        });
        valueAnimatorOfFloat.setStartDelay(delay);
        valueAnimatorOfFloat.start();
        return true;
    }

    void visualizeDropLocation(View v, Bitmap dragOutline, int originX, int originY, int cellX, int cellY, int spanX, int spanY, boolean resize, Point dragOffset, Rect dragRegion) {
        int[] iArr = this.mDragCell;
        int i = iArr[0];
        int i2 = iArr[1];
        if (isHotseat()) {
            return;
        }
        if (dragOutline == null && v == null) {
            return;
        }
        if (cellX == i && cellY == i2) {
            return;
        }
        int[] iArr2 = this.mDragCell;
        iArr2[0] = cellX;
        iArr2[1] = cellY;
        int[] iArr3 = this.mTmpPoint;
        cellToPoint(cellX, cellY, iArr3);
        int width = iArr3[0];
        int height = iArr3[1];
        if (this.mDragCellRect == null) {
            this.mDragCellRect = this.mTmpRect;
        }
        int[] iArrCellSpansToSize = cellSpansToSize(spanX, spanY);
        this.mDragCellRect.set(width, height, iArrCellSpansToSize[0] + width, iArrCellSpansToSize[1] + height);
        if (v != null && dragOffset == null) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            int i3 = width + marginLayoutParams.leftMargin;
            height = height + marginLayoutParams.topMargin + ((v.getHeight() - dragOutline.getHeight()) / 2);
            width = i3 + ((((this.mCellWidth * spanX) + ((spanX - 1) * this.mWidthGap)) - dragOutline.getWidth()) / 2);
        } else if (dragOffset != null && dragRegion != null) {
            if ((!(v instanceof BubbleTextView) || !((BubbleTextView) v).isLayoutHorizontal()) && (!(v instanceof FolderIcon) || !((FolderIcon) v).isLayoutHorizontal())) {
                width += dragOffset.x + ((((this.mCellWidth * spanX) + ((spanX - 1) * this.mWidthGap)) - dragRegion.width()) / 2);
                height += dragOffset.y + ((int) Math.max(0.0f, (this.mCellHeight - getShortcutsAndWidgets().getCellContentHeight()) / 2.0f));
            }
        } else {
            width += (((this.mCellWidth * spanX) + ((spanX - 1) * this.mWidthGap)) - dragOutline.getWidth()) / 2;
            height += (((this.mCellHeight * spanY) + ((spanY - 1) * this.mHeightGap)) - dragOutline.getHeight()) / 2;
        }
        int i4 = this.mDragOutlineCurrent;
        this.mDragOutlineAnims[i4].animateOut();
        Rect[] rectArr = this.mDragOutlines;
        int length = (i4 + 1) % rectArr.length;
        this.mDragOutlineCurrent = length;
        Rect rect = rectArr[length];
        rect.set(width, height, dragOutline.getWidth() + width, dragOutline.getHeight() + height);
        if (resize) {
            cellToRect(cellX, cellY, spanX, spanY, rect);
        }
        this.mDragOutlineAnims[this.mDragOutlineCurrent].setTag(dragOutline);
        this.mDragOutlineAnims[this.mDragOutlineCurrent].animateIn();
    }

    public void clearDragOutlines() {
        this.mDragOutlineAnims[this.mDragOutlineCurrent].animateOut();
        int[] iArr = this.mDragCell;
        iArr[1] = -1;
        iArr[0] = -1;
    }

    int[] findNearestVacantArea(int pixelX, int pixelY, int spanX, int spanY, int[] result) {
        return findNearestVacantArea(pixelX, pixelY, spanX, spanY, spanX, spanY, result, null);
    }

    int[] findNearestVacantArea(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX, int spanY, int[] result, int[] resultSpan) {
        return findNearestArea(pixelX, pixelY, minSpanX, minSpanY, spanX, spanY, true, result, resultSpan);
    }

    private void lazyInitTempRectStack() {
        if (this.mTempRectStack.isEmpty()) {
            for (int i = 0; i < this.mCountX * this.mCountY; i++) {
                this.mTempRectStack.push(new Rect());
            }
        }
    }

    private void recycleTempRects(Stack<Rect> used) {
        while (!used.isEmpty()) {
            this.mTempRectStack.push(used.pop());
        }
    }

    private int[] findNearestArea(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX, int spanY, boolean ignoreOccupied, int[] result, int[] resultSpan) {
        int i;
        int i2;
        int[] iArr;
        Rect rect;
        boolean z;
        Rect rect2;
        int i3;
        int i4;
        Rect rect3;
        int i5 = minSpanX;
        int i6 = minSpanY;
        int i7 = spanX;
        int i8 = spanY;
        lazyInitTempRectStack();
        int i9 = (int) (pixelX - (((this.mCellWidth + this.mWidthGap) * (i7 - 1)) / 2.0f));
        int i10 = (int) (pixelY - (((this.mCellHeight + this.mHeightGap) * (i8 - 1)) / 2.0f));
        int[] iArr2 = result != null ? result : new int[2];
        Rect rect4 = new Rect(-1, -1, -1, -1);
        Stack<Rect> stack = new Stack<>();
        int i11 = this.mCountX;
        int i12 = this.mCountY;
        if (i5 <= 0 || i6 <= 0 || i7 <= 0 || i8 <= 0 || i7 < i5 || i8 < i6) {
            return iArr2;
        }
        int i13 = 0;
        double d = Double.MAX_VALUE;
        while (i13 < i12 - (i6 - 1)) {
            int i14 = 0;
            while (i14 < i11 - (i5 - 1)) {
                if (ignoreOccupied) {
                    for (int i15 = 0; i15 < i5; i15++) {
                        int i16 = 0;
                        while (i16 < i6) {
                            iArr = iArr2;
                            if (this.mOccupied[i14 + i15][i13 + i16]) {
                                i = i9;
                                i2 = i10;
                                rect2 = rect4;
                                break;
                            }
                            i16++;
                            iArr2 = iArr;
                        }
                    }
                    iArr = iArr2;
                    boolean z2 = i5 >= i7;
                    boolean z3 = i6 >= i8;
                    boolean z4 = z2;
                    boolean z5 = true;
                    while (true) {
                        if (z4 && z3) {
                            break;
                        }
                        if (!z5 || z4) {
                            i3 = i9;
                            i4 = i10;
                            rect3 = rect4;
                            if (!z3) {
                                for (int i17 = 0; i17 < i5; i17++) {
                                    int i18 = i13 + i6;
                                    if (i18 > i12 - 1 || this.mOccupied[i14 + i17][i18]) {
                                        z3 = true;
                                    }
                                }
                                if (!z3) {
                                    i6++;
                                }
                            }
                        } else {
                            rect3 = rect4;
                            int i19 = 0;
                            while (i19 < i6) {
                                int i20 = i10;
                                int i21 = i14 + i5;
                                int i22 = i9;
                                if (i21 > i11 - 1 || this.mOccupied[i21][i13 + i19]) {
                                    z4 = true;
                                }
                                i19++;
                                i10 = i20;
                                i9 = i22;
                            }
                            i3 = i9;
                            i4 = i10;
                            if (!z4) {
                                i5++;
                            }
                        }
                        z4 |= i5 >= i7;
                        z3 |= i6 >= i8;
                        z5 = !z5;
                        rect4 = rect3;
                        i10 = i4;
                        i9 = i3;
                    }
                    i = i9;
                    i2 = i10;
                    rect = rect4;
                } else {
                    i = i9;
                    i2 = i10;
                    iArr = iArr2;
                    rect = rect4;
                    i5 = -1;
                    i6 = -1;
                }
                cellToCenterPoint(i14, i13, this.mTmpPoint);
                Rect rectPop = this.mTempRectStack.pop();
                rectPop.set(i14, i13, i14 + i5, i13 + i6);
                Iterator<Rect> it = stack.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        z = false;
                        break;
                    }
                    if (it.next().contains(rectPop)) {
                        z = true;
                        break;
                    }
                }
                stack.push(rectPop);
                double dHypot = Math.hypot(r5[0] - i, r5[1] - i2);
                if (dHypot > d || z) {
                    rect2 = rect;
                    if (rectPop.contains(rect2)) {
                    }
                    i14++;
                    i5 = minSpanX;
                    i6 = minSpanY;
                    i7 = spanX;
                    i8 = spanY;
                    rect4 = rect2;
                    iArr2 = iArr;
                    i10 = i2;
                    i9 = i;
                } else {
                    rect2 = rect;
                }
                iArr[0] = i14;
                iArr[1] = i13;
                if (resultSpan != null) {
                    resultSpan[0] = i5;
                    resultSpan[1] = i6;
                }
                rect2.set(rectPop);
                d = dHypot;
                i14++;
                i5 = minSpanX;
                i6 = minSpanY;
                i7 = spanX;
                i8 = spanY;
                rect4 = rect2;
                iArr2 = iArr;
                i10 = i2;
                i9 = i;
            }
            i13++;
            i5 = minSpanX;
            i6 = minSpanY;
            i7 = spanX;
            i8 = spanY;
            i9 = i9;
        }
        int[] iArr3 = iArr2;
        if (d == Double.MAX_VALUE) {
            iArr3[0] = -1;
            iArr3[1] = -1;
        }
        recycleTempRects(stack);
        return iArr3;
    }

    private int[] findNearestArea(int cellX, int cellY, int spanX, int spanY, int[] direction, boolean[][] occupied, boolean[][] blockOccupied, int[] result) {
        int i;
        int[] iArr = result != null ? result : new int[2];
        int i2 = Integer.MIN_VALUE;
        int i3 = this.mCountX;
        int i4 = this.mCountY;
        int i5 = 0;
        float f = Float.MAX_VALUE;
        while (i5 < i4 - (spanY - 1)) {
            int i6 = 0;
            while (i6 < i3 - (spanX - 1)) {
                for (int i7 = 0; i7 < spanX; i7++) {
                    for (int i8 = 0; i8 < spanY; i8++) {
                        if (occupied[i6 + i7][i5 + i8] && (blockOccupied == null || blockOccupied[i7][i8])) {
                            i = i5;
                            break;
                        }
                    }
                }
                int i9 = i6 - cellX;
                int i10 = i5 - cellY;
                i = i5;
                float fHypot = (float) Math.hypot(i9, i10);
                int[] iArr2 = this.mTmpPoint;
                computeDirectionVector(i9, i10, iArr2);
                int i11 = (direction[0] * iArr2[0]) + (direction[1] * iArr2[1]);
                if (direction[0] != iArr2[0] || direction[0] == iArr2[0]) {
                }
                if (Float.compare(fHypot, f) < 0 || (Float.compare(fHypot, f) == 0 && i11 > i2)) {
                    iArr[0] = i6;
                    iArr[1] = i;
                    f = fHypot;
                    i2 = i11;
                }
                i6++;
                i5 = i;
            }
            i5++;
        }
        if (f == Float.MAX_VALUE) {
            iArr[0] = -1;
            iArr[1] = -1;
        }
        return iArr;
    }

    private boolean addViewToTempLocation(View v, Rect rectOccupiedByPotentialDrop, int[] direction, ItemConfiguration currentState) {
        CellAndSpan cellAndSpan = currentState.map.get(v);
        markCellsForView(cellAndSpan.x, cellAndSpan.y, cellAndSpan.spanX, cellAndSpan.spanY, this.mTmpOccupied, false);
        boolean z = true;
        markCellsForRect(rectOccupiedByPotentialDrop, this.mTmpOccupied, true);
        findNearestArea(cellAndSpan.x, cellAndSpan.y, cellAndSpan.spanX, cellAndSpan.spanY, direction, this.mTmpOccupied, null, this.mTempLocation);
        int[] iArr = this.mTempLocation;
        if (iArr[0] < 0 || iArr[1] < 0) {
            z = false;
        } else {
            cellAndSpan.x = iArr[0];
            cellAndSpan.y = this.mTempLocation[1];
        }
        markCellsForView(cellAndSpan.x, cellAndSpan.y, cellAndSpan.spanX, cellAndSpan.spanY, this.mTmpOccupied, true);
        return z;
    }

    private class ViewCluster {
        static final int BOTTOM = 3;
        static final int LEFT = 0;
        static final int RIGHT = 2;
        static final int TOP = 1;
        int[] bottomEdge;
        boolean bottomEdgeDirty;
        boolean boundingRectDirty;
        ItemConfiguration config;
        int[] leftEdge;
        boolean leftEdgeDirty;
        int[] rightEdge;
        boolean rightEdgeDirty;
        int[] topEdge;
        boolean topEdgeDirty;
        ArrayList<View> views;
        Rect boundingRect = new Rect();
        PositionComparator comparator = new PositionComparator();

        public ViewCluster(ArrayList<View> views, ItemConfiguration config) {
            this.leftEdge = new int[CellLayout.this.mCountY];
            this.rightEdge = new int[CellLayout.this.mCountY];
            this.topEdge = new int[CellLayout.this.mCountX];
            this.bottomEdge = new int[CellLayout.this.mCountX];
            this.views = (ArrayList) views.clone();
            this.config = config;
            resetEdges();
        }

        void resetEdges() {
            for (int i = 0; i < CellLayout.this.mCountX; i++) {
                this.topEdge[i] = -1;
                this.bottomEdge[i] = -1;
            }
            for (int i2 = 0; i2 < CellLayout.this.mCountY; i2++) {
                this.leftEdge[i2] = -1;
                this.rightEdge[i2] = -1;
            }
            this.leftEdgeDirty = true;
            this.rightEdgeDirty = true;
            this.bottomEdgeDirty = true;
            this.topEdgeDirty = true;
            this.boundingRectDirty = true;
        }

        void computeEdge(int which, int[] edge) {
            int size = this.views.size();
            for (int i = 0; i < size; i++) {
                CellAndSpan cellAndSpan = this.config.map.get(this.views.get(i));
                if (which == 0) {
                    int i2 = cellAndSpan.x;
                    for (int i3 = cellAndSpan.y; i3 < cellAndSpan.y + cellAndSpan.spanY; i3++) {
                        if (i2 < edge[i3] || edge[i3] < 0) {
                            edge[i3] = i2;
                        }
                    }
                } else if (which == 1) {
                    int i4 = cellAndSpan.y;
                    for (int i5 = cellAndSpan.x; i5 < cellAndSpan.x + cellAndSpan.spanX; i5++) {
                        if (i4 < edge[i5] || edge[i5] < 0) {
                            edge[i5] = i4;
                        }
                    }
                } else if (which == 2) {
                    int i6 = cellAndSpan.x + cellAndSpan.spanX;
                    for (int i7 = cellAndSpan.y; i7 < cellAndSpan.y + cellAndSpan.spanY; i7++) {
                        if (i6 > edge[i7]) {
                            edge[i7] = i6;
                        }
                    }
                } else if (which == 3) {
                    int i8 = cellAndSpan.y + cellAndSpan.spanY;
                    for (int i9 = cellAndSpan.x; i9 < cellAndSpan.x + cellAndSpan.spanX; i9++) {
                        if (i8 > edge[i9]) {
                            edge[i9] = i8;
                        }
                    }
                }
            }
        }

        boolean isViewTouchingEdge(View v, int whichEdge) {
            CellAndSpan cellAndSpan = this.config.map.get(v);
            int[] edge = getEdge(whichEdge);
            if (whichEdge == 0) {
                for (int i = cellAndSpan.y; i < cellAndSpan.y + cellAndSpan.spanY; i++) {
                    if (edge[i] == cellAndSpan.x + cellAndSpan.spanX) {
                        return true;
                    }
                }
                return false;
            }
            if (whichEdge == 1) {
                for (int i2 = cellAndSpan.x; i2 < cellAndSpan.x + cellAndSpan.spanX; i2++) {
                    if (edge[i2] == cellAndSpan.y + cellAndSpan.spanY) {
                        return true;
                    }
                }
                return false;
            }
            if (whichEdge == 2) {
                for (int i3 = cellAndSpan.y; i3 < cellAndSpan.y + cellAndSpan.spanY; i3++) {
                    if (edge[i3] == cellAndSpan.x) {
                        return true;
                    }
                }
                return false;
            }
            if (whichEdge != 3) {
                return false;
            }
            for (int i4 = cellAndSpan.x; i4 < cellAndSpan.x + cellAndSpan.spanX; i4++) {
                if (edge[i4] == cellAndSpan.y) {
                    return true;
                }
            }
            return false;
        }

        void shift(int whichEdge, int delta) {
            Iterator<View> it = this.views.iterator();
            while (it.hasNext()) {
                CellAndSpan cellAndSpan = this.config.map.get(it.next());
                if (whichEdge == 0) {
                    cellAndSpan.x -= delta;
                } else if (whichEdge == 1) {
                    cellAndSpan.y -= delta;
                } else if (whichEdge == 2) {
                    cellAndSpan.x += delta;
                } else {
                    cellAndSpan.y += delta;
                }
            }
            resetEdges();
        }

        public void addView(View v) {
            this.views.add(v);
            resetEdges();
        }

        public Rect getBoundingRect() {
            if (this.boundingRectDirty) {
                boolean z = true;
                Iterator<View> it = this.views.iterator();
                while (it.hasNext()) {
                    CellAndSpan cellAndSpan = this.config.map.get(it.next());
                    if (z) {
                        this.boundingRect.set(cellAndSpan.x, cellAndSpan.y, cellAndSpan.x + cellAndSpan.spanX, cellAndSpan.y + cellAndSpan.spanY);
                        z = false;
                    } else {
                        this.boundingRect.union(cellAndSpan.x, cellAndSpan.y, cellAndSpan.x + cellAndSpan.spanX, cellAndSpan.y + cellAndSpan.spanY);
                    }
                }
            }
            return this.boundingRect;
        }

        public int[] getEdge(int which) {
            if (which == 0) {
                return getLeftEdge();
            }
            if (which == 1) {
                return getTopEdge();
            }
            if (which == 2) {
                return getRightEdge();
            }
            return getBottomEdge();
        }

        public int[] getLeftEdge() {
            if (this.leftEdgeDirty) {
                computeEdge(0, this.leftEdge);
            }
            return this.leftEdge;
        }

        public int[] getRightEdge() {
            if (this.rightEdgeDirty) {
                computeEdge(2, this.rightEdge);
            }
            return this.rightEdge;
        }

        public int[] getTopEdge() {
            if (this.topEdgeDirty) {
                computeEdge(1, this.topEdge);
            }
            return this.topEdge;
        }

        public int[] getBottomEdge() {
            if (this.bottomEdgeDirty) {
                computeEdge(3, this.bottomEdge);
            }
            return this.bottomEdge;
        }

        class PositionComparator implements Comparator<View> {
            int whichEdge = 0;

            PositionComparator() {
            }

            /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
            @Override // java.util.Comparator
            public int compare(View left, View right) {
                int i;
                int i2;
                int i3;
                int i4;
                int i5;
                CellAndSpan cellAndSpan = ViewCluster.this.config.map.get(left);
                CellAndSpan cellAndSpan2 = ViewCluster.this.config.map.get(right);
                int i6 = this.whichEdge;
                if (i6 == 0) {
                    i = cellAndSpan2.x + cellAndSpan2.spanX;
                    i2 = cellAndSpan.x;
                    i3 = cellAndSpan.spanX;
                } else {
                    if (i6 != 1) {
                        if (i6 == 2) {
                            i4 = cellAndSpan.x;
                            i5 = cellAndSpan2.x;
                        } else {
                            i4 = cellAndSpan.y;
                            i5 = cellAndSpan2.y;
                        }
                        return i4 - i5;
                    }
                    i = cellAndSpan2.y + cellAndSpan2.spanY;
                    i2 = cellAndSpan.y;
                    i3 = cellAndSpan.spanY;
                }
                return i - (i2 + i3);
            }
        }

        public void sortConfigurationForEdgePush(int edge) {
            this.comparator.whichEdge = edge;
            Collections.sort(this.config.sortedViews, this.comparator);
        }
    }

    private boolean pushViewsToTempLocation(ArrayList<View> views, Rect rectOccupiedByPotentialDrop, int[] direction, View dragView, ItemConfiguration currentState) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        ViewCluster viewCluster = new ViewCluster(views, currentState);
        Rect boundingRect = viewCluster.getBoundingRect();
        boolean z = false;
        if (direction[0] < 0) {
            i4 = boundingRect.right - rectOccupiedByPotentialDrop.left;
            i5 = 0;
        } else {
            if (direction[0] > 0) {
                i = 2;
                i2 = rectOccupiedByPotentialDrop.right;
                i3 = boundingRect.left;
            } else if (direction[1] < 0) {
                i4 = boundingRect.bottom - rectOccupiedByPotentialDrop.top;
                i5 = 1;
            } else {
                i = 3;
                i2 = rectOccupiedByPotentialDrop.bottom;
                i3 = boundingRect.top;
            }
            i4 = i2 - i3;
            i5 = i;
        }
        if (i4 <= 0) {
            return false;
        }
        Iterator<View> it = views.iterator();
        while (it.hasNext()) {
            CellAndSpan cellAndSpan = currentState.map.get(it.next());
            markCellsForView(cellAndSpan.x, cellAndSpan.y, cellAndSpan.spanX, cellAndSpan.spanY, this.mTmpOccupied, false);
        }
        currentState.save();
        viewCluster.sortConfigurationForEdgePush(i5);
        boolean z2 = false;
        while (i4 > 0 && !z2) {
            Iterator<View> it2 = currentState.sortedViews.iterator();
            while (true) {
                if (it2.hasNext()) {
                    View next = it2.next();
                    if (!viewCluster.views.contains(next) && next != dragView && viewCluster.isViewTouchingEdge(next, i5)) {
                        if (!((LayoutParams) next.getLayoutParams()).canReorder) {
                            z2 = true;
                            break;
                        }
                        viewCluster.addView(next);
                        CellAndSpan cellAndSpan2 = currentState.map.get(next);
                        markCellsForView(cellAndSpan2.x, cellAndSpan2.y, cellAndSpan2.spanX, cellAndSpan2.spanY, this.mTmpOccupied, false);
                    }
                }
            }
            i4--;
            viewCluster.shift(i5, 1);
        }
        Rect boundingRect2 = viewCluster.getBoundingRect();
        if (z2 || boundingRect2.left < 0 || boundingRect2.right > this.mCountX || boundingRect2.top < 0 || boundingRect2.bottom > this.mCountY) {
            currentState.restore();
        } else {
            z = true;
        }
        Iterator<View> it3 = viewCluster.views.iterator();
        while (it3.hasNext()) {
            CellAndSpan cellAndSpan3 = currentState.map.get(it3.next());
            markCellsForView(cellAndSpan3.x, cellAndSpan3.y, cellAndSpan3.spanX, cellAndSpan3.spanY, this.mTmpOccupied, true);
        }
        return z;
    }

    private boolean addViewsToTempLocation(ArrayList<View> views, Rect rectOccupiedByPotentialDrop, int[] direction, View dragView, ItemConfiguration currentState) {
        boolean z = true;
        if (views.size() == 0) {
            return true;
        }
        Iterator<View> it = views.iterator();
        Rect rect = null;
        while (it.hasNext()) {
            CellAndSpan cellAndSpan = currentState.map.get(it.next());
            if (rect == null) {
                rect = new Rect(cellAndSpan.x, cellAndSpan.y, cellAndSpan.x + cellAndSpan.spanX, cellAndSpan.y + cellAndSpan.spanY);
            } else {
                rect.union(cellAndSpan.x, cellAndSpan.y, cellAndSpan.x + cellAndSpan.spanX, cellAndSpan.y + cellAndSpan.spanY);
            }
        }
        Iterator<View> it2 = views.iterator();
        while (it2.hasNext()) {
            CellAndSpan cellAndSpan2 = currentState.map.get(it2.next());
            markCellsForView(cellAndSpan2.x, cellAndSpan2.y, cellAndSpan2.spanX, cellAndSpan2.spanY, this.mTmpOccupied, false);
        }
        boolean[][] zArr = (boolean[][]) Array.newInstance((Class<?>) boolean.class, rect.width(), rect.height());
        int i = rect.top;
        int i2 = rect.left;
        Iterator<View> it3 = views.iterator();
        while (it3.hasNext()) {
            CellAndSpan cellAndSpan3 = currentState.map.get(it3.next());
            markCellsForView(cellAndSpan3.x - i2, cellAndSpan3.y - i, cellAndSpan3.spanX, cellAndSpan3.spanY, zArr, true);
        }
        markCellsForRect(rectOccupiedByPotentialDrop, this.mTmpOccupied, true);
        findNearestArea(rect.left, rect.top, rect.width(), rect.height(), direction, this.mTmpOccupied, zArr, this.mTempLocation);
        int[] iArr = this.mTempLocation;
        if (iArr[0] < 0 || iArr[1] < 0) {
            z = false;
        } else {
            int i3 = iArr[0] - rect.left;
            int i4 = this.mTempLocation[1] - rect.top;
            Iterator<View> it4 = views.iterator();
            while (it4.hasNext()) {
                CellAndSpan cellAndSpan4 = currentState.map.get(it4.next());
                cellAndSpan4.x += i3;
                cellAndSpan4.y += i4;
            }
        }
        Iterator<View> it5 = views.iterator();
        while (it5.hasNext()) {
            CellAndSpan cellAndSpan5 = currentState.map.get(it5.next());
            markCellsForView(cellAndSpan5.x, cellAndSpan5.y, cellAndSpan5.spanX, cellAndSpan5.spanY, this.mTmpOccupied, true);
        }
        return z;
    }

    private void markCellsForRect(Rect r, boolean[][] occupied, boolean value) {
        markCellsForView(r.left, r.top, r.width(), r.height(), occupied, value);
    }

    private boolean attemptPushInDirection(ArrayList<View> intersectingViews, Rect occupied, int[] direction, View ignoreView, ItemConfiguration solution) {
        if (Math.abs(direction[0]) + Math.abs(direction[1]) > 1) {
            int i = direction[1];
            direction[1] = 0;
            if (pushViewsToTempLocation(intersectingViews, occupied, direction, ignoreView, solution)) {
                return true;
            }
            direction[1] = i;
            int i2 = direction[0];
            direction[0] = 0;
            if (pushViewsToTempLocation(intersectingViews, occupied, direction, ignoreView, solution)) {
                return true;
            }
            direction[0] = i2;
            direction[0] = direction[0] * (-1);
            direction[1] = direction[1] * (-1);
            int i3 = direction[1];
            direction[1] = 0;
            if (pushViewsToTempLocation(intersectingViews, occupied, direction, ignoreView, solution)) {
                return true;
            }
            direction[1] = i3;
            int i4 = direction[0];
            direction[0] = 0;
            if (pushViewsToTempLocation(intersectingViews, occupied, direction, ignoreView, solution)) {
                return true;
            }
            direction[0] = i4;
            direction[0] = direction[0] * (-1);
            direction[1] = direction[1] * (-1);
        } else {
            if (pushViewsToTempLocation(intersectingViews, occupied, direction, ignoreView, solution)) {
                return true;
            }
            direction[0] = direction[0] * (-1);
            direction[1] = direction[1] * (-1);
            if (pushViewsToTempLocation(intersectingViews, occupied, direction, ignoreView, solution)) {
                return true;
            }
            direction[0] = direction[0] * (-1);
            direction[1] = direction[1] * (-1);
            int i5 = direction[1];
            direction[1] = direction[0];
            direction[0] = i5;
            if (pushViewsToTempLocation(intersectingViews, occupied, direction, ignoreView, solution)) {
                return true;
            }
            direction[0] = direction[0] * (-1);
            direction[1] = direction[1] * (-1);
            if (pushViewsToTempLocation(intersectingViews, occupied, direction, ignoreView, solution)) {
                return true;
            }
            direction[0] = direction[0] * (-1);
            direction[1] = direction[1] * (-1);
            int i6 = direction[1];
            direction[1] = direction[0];
            direction[0] = i6;
        }
        return false;
    }

    private boolean rearrangementExists(int cellX, int cellY, int spanX, int spanY, int[] direction, View ignoreView, ItemConfiguration solution) {
        CellAndSpan cellAndSpan;
        if (cellX < 0 || cellY < 0) {
            return false;
        }
        this.mIntersectingViews.clear();
        int i = spanX + cellX;
        int i2 = spanY + cellY;
        this.mOccupiedRect.set(cellX, cellY, i, i2);
        if (ignoreView != null && (cellAndSpan = solution.map.get(ignoreView)) != null) {
            cellAndSpan.x = cellX;
            cellAndSpan.y = cellY;
        }
        Rect rect = new Rect(cellX, cellY, i, i2);
        Rect rect2 = new Rect();
        for (View view : solution.map.keySet()) {
            if (view != ignoreView) {
                CellAndSpan cellAndSpan2 = solution.map.get(view);
                LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                rect2.set(cellAndSpan2.x, cellAndSpan2.y, cellAndSpan2.x + cellAndSpan2.spanX, cellAndSpan2.y + cellAndSpan2.spanY);
                if (!Rect.intersects(rect, rect2)) {
                    continue;
                } else {
                    if (!layoutParams.canReorder) {
                        return false;
                    }
                    this.mIntersectingViews.add(view);
                }
            }
        }
        solution.intersectingViews = new ArrayList<>(this.mIntersectingViews);
        if (attemptPushInDirection(this.mIntersectingViews, this.mOccupiedRect, direction, ignoreView, solution) || addViewsToTempLocation(this.mIntersectingViews, this.mOccupiedRect, direction, ignoreView, solution)) {
            return true;
        }
        Iterator<View> it = this.mIntersectingViews.iterator();
        while (it.hasNext()) {
            if (!addViewToTempLocation(it.next(), this.mOccupiedRect, direction, solution)) {
                return false;
            }
        }
        return true;
    }

    private void computeDirectionVector(float deltaX, float deltaY, int[] result) {
        double dAtan = Math.atan(deltaY / deltaX);
        result[0] = 0;
        result[1] = 0;
        if (Math.abs(Math.cos(dAtan)) > 0.5d) {
            result[0] = (int) Math.signum(deltaX);
        }
        if (Math.abs(Math.sin(dAtan)) > 0.5d) {
            result[1] = (int) Math.signum(deltaY);
        }
    }

    private void copyOccupiedArray(boolean[][] occupied) {
        for (int i = 0; i < this.mCountX; i++) {
            for (int i2 = 0; i2 < this.mCountY; i2++) {
                occupied[i][i2] = this.mOccupied[i][i2];
            }
        }
    }

    private ItemConfiguration findReorderSolution(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX, int spanY, int[] direction, View dragView, boolean decX, ItemConfiguration solution) {
        copyCurrentStateToSolution(solution, false);
        copyOccupiedArray(this.mTmpOccupied);
        int[] iArrFindNearestArea = findNearestArea(pixelX, pixelY, spanX, spanY, new int[2]);
        if (rearrangementExists(iArrFindNearestArea[0], iArrFindNearestArea[1], spanX, spanY, direction, dragView, solution)) {
            solution.isSolution = true;
            solution.dragViewX = iArrFindNearestArea[0];
            solution.dragViewY = iArrFindNearestArea[1];
            solution.dragViewSpanX = spanX;
            solution.dragViewSpanY = spanY;
        } else {
            if (spanX > minSpanX && (minSpanY == spanY || decX)) {
                return findReorderSolution(pixelX, pixelY, minSpanX, minSpanY, spanX - 1, spanY, direction, dragView, false, solution);
            }
            if (spanY > minSpanY) {
                return findReorderSolution(pixelX, pixelY, minSpanX, minSpanY, spanX, spanY - 1, direction, dragView, true, solution);
            }
            solution.isSolution = false;
        }
        return solution;
    }

    private void copyCurrentStateToSolution(ItemConfiguration solution, boolean temp) {
        CellAndSpan cellAndSpan;
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mShortcutsAndWidgets.getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            if (temp) {
                cellAndSpan = new CellAndSpan(layoutParams.tmpCellX, layoutParams.tmpCellY, layoutParams.cellHSpan, layoutParams.cellVSpan);
            } else {
                cellAndSpan = new CellAndSpan(layoutParams.cellX, layoutParams.cellY, layoutParams.cellHSpan, layoutParams.cellVSpan);
            }
            solution.add(childAt, cellAndSpan);
        }
    }

    private void copySolutionToTempState(ItemConfiguration solution, View dragView) {
        for (int i = 0; i < this.mCountX; i++) {
            for (int i2 = 0; i2 < this.mCountY; i2++) {
                this.mTmpOccupied[i][i2] = false;
            }
        }
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = this.mShortcutsAndWidgets.getChildAt(i3);
            if (childAt != dragView) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                CellAndSpan cellAndSpan = solution.map.get(childAt);
                if (cellAndSpan != null) {
                    layoutParams.tmpCellX = cellAndSpan.x;
                    layoutParams.tmpCellY = cellAndSpan.y;
                    layoutParams.cellHSpan = cellAndSpan.spanX;
                    layoutParams.cellVSpan = cellAndSpan.spanY;
                    markCellsForView(cellAndSpan.x, cellAndSpan.y, cellAndSpan.spanX, cellAndSpan.spanY, this.mTmpOccupied, true);
                }
            }
        }
        markCellsForView(solution.dragViewX, solution.dragViewY, solution.dragViewSpanX, solution.dragViewSpanY, this.mTmpOccupied, true);
    }

    private void animateItemsToSolution(ItemConfiguration solution, View dragView, boolean commitDragView) {
        CellAndSpan cellAndSpan;
        boolean[][] zArr = this.mTmpOccupied;
        for (int i = 0; i < this.mCountX; i++) {
            for (int i2 = 0; i2 < this.mCountY; i2++) {
                zArr[i][i2] = false;
            }
        }
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        boolean z = false;
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = this.mShortcutsAndWidgets.getChildAt(i3);
            if (childAt != dragView && (cellAndSpan = solution.map.get(childAt)) != null) {
                if (animateChildToPosition(childAt, cellAndSpan.x, cellAndSpan.y, 150, 0, false, false)) {
                    z = true;
                }
                markCellsForView(cellAndSpan.x, cellAndSpan.y, cellAndSpan.spanX, cellAndSpan.spanY, zArr, true);
            }
        }
        if (commitDragView) {
            markCellsForView(solution.dragViewX, solution.dragViewY, solution.dragViewSpanX, solution.dragViewSpanY, zArr, true);
        }
        if (z) {
            VibratorManager.performHapticFeedback(this.mLauncher, 65541);
        }
    }

    private void beginOrAdjustReorderPreviewAnimations(ItemConfiguration solution, View dragView, int delay, int mode) {
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mShortcutsAndWidgets.getChildAt(i);
            if (childAt != dragView) {
                CellAndSpan cellAndSpan = solution.map.get(childAt);
                boolean z = (mode != 0 || solution.intersectingViews == null || solution.intersectingViews.contains(childAt)) ? false : true;
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (cellAndSpan != null && !z) {
                    new ReorderPreviewAnimation(childAt, mode, layoutParams.cellX, layoutParams.cellY, cellAndSpan.x, cellAndSpan.y, cellAndSpan.spanX, cellAndSpan.spanY).animate();
                }
            }
        }
    }

    class ReorderPreviewAnimation {
        private static final int HINT_DURATION = 350;
        public static final int MODE_HINT = 0;
        public static final int MODE_PREVIEW = 1;
        private static final int PREVIEW_DURATION = 300;
        Animator a;
        View child;
        float finalDeltaX;
        float finalDeltaY;
        float finalScale;
        float initDeltaX;
        float initDeltaY;
        float initScale;
        int mode;
        boolean repeating = false;

        public ReorderPreviewAnimation(View child, int mode, int cellX0, int cellY0, int cellX1, int cellY1, int spanX, int spanY) {
            CellLayout.this.regionToCenterPoint(cellX0, cellY0, spanX, spanY, CellLayout.this.mTmpPoint);
            int i = CellLayout.this.mTmpPoint[0];
            int i2 = CellLayout.this.mTmpPoint[1];
            CellLayout.this.regionToCenterPoint(cellX1, cellY1, spanX, spanY, CellLayout.this.mTmpPoint);
            int i3 = CellLayout.this.mTmpPoint[0] - i;
            int i4 = CellLayout.this.mTmpPoint[1] - i2;
            this.finalDeltaX = 0.0f;
            this.finalDeltaY = 0.0f;
            int i5 = mode == 0 ? -1 : 1;
            if (i3 != i4 || i3 != 0) {
                if (i4 == 0) {
                    this.finalDeltaX = (-i5) * Math.signum(i3) * CellLayout.this.mReorderPreviewAnimationMagnitude;
                } else if (i3 == 0) {
                    this.finalDeltaY = (-i5) * Math.signum(i4) * CellLayout.this.mReorderPreviewAnimationMagnitude;
                } else {
                    float f = i4;
                    float f2 = i3;
                    double dAtan = Math.atan(f / f2);
                    float f3 = -i5;
                    this.finalDeltaX = (int) (((double) (Math.signum(f2) * f3)) * Math.abs(Math.cos(dAtan) * ((double) CellLayout.this.mReorderPreviewAnimationMagnitude)));
                    this.finalDeltaY = (int) (((double) (f3 * Math.signum(f))) * Math.abs(Math.sin(dAtan) * ((double) CellLayout.this.mReorderPreviewAnimationMagnitude)));
                }
            }
            this.mode = mode;
            this.initDeltaX = child.getTranslationX();
            this.initDeltaY = child.getTranslationY();
            this.finalScale = CellLayout.this.getChildrenScale() - (4.0f / child.getWidth());
            this.initScale = child.getScaleX();
            this.child = child;
        }

        void animate() {
            if (CellLayout.this.mShakeAnimators.containsKey(this.child)) {
                CellLayout.this.mShakeAnimators.get(this.child).cancel();
                CellLayout.this.mShakeAnimators.remove(this.child);
                if (this.finalDeltaX == 0.0f && this.finalDeltaY == 0.0f) {
                    completeAnimationImmediately();
                    return;
                }
            }
            if (this.finalDeltaX == 0.0f && this.finalDeltaY == 0.0f) {
                return;
            }
            ValueAnimator valueAnimatorOfFloat = LauncherAnimUtils.ofFloat(this.child, 0.0f, 1.0f);
            this.a = valueAnimatorOfFloat;
            valueAnimatorOfFloat.setRepeatMode(2);
            valueAnimatorOfFloat.setRepeatCount(-1);
            valueAnimatorOfFloat.setDuration(this.mode == 0 ? 350L : 300L);
            valueAnimatorOfFloat.setStartDelay((int) (Math.random() * 60.0d));
            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.CellLayout.ReorderPreviewAnimation.1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    float fFloatValue = ((Float) animation.getAnimatedValue()).floatValue();
                    float f = (ReorderPreviewAnimation.this.mode == 0 && ReorderPreviewAnimation.this.repeating) ? 1.0f : fFloatValue;
                    float f2 = 1.0f - f;
                    float f3 = (ReorderPreviewAnimation.this.finalDeltaX * f) + (ReorderPreviewAnimation.this.initDeltaX * f2);
                    float f4 = (f * ReorderPreviewAnimation.this.finalDeltaY) + (f2 * ReorderPreviewAnimation.this.initDeltaY);
                    ReorderPreviewAnimation.this.child.setTranslationX(f3);
                    ReorderPreviewAnimation.this.child.setTranslationY(f4);
                    float f5 = (ReorderPreviewAnimation.this.finalScale * fFloatValue) + ((1.0f - fFloatValue) * ReorderPreviewAnimation.this.initScale);
                    ReorderPreviewAnimation.this.child.setScaleX(f5);
                    ReorderPreviewAnimation.this.child.setScaleY(f5);
                }
            });
            valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.CellLayout.ReorderPreviewAnimation.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationRepeat(Animator animation) {
                    ReorderPreviewAnimation.this.initDeltaX = 0.0f;
                    ReorderPreviewAnimation.this.initDeltaY = 0.0f;
                    ReorderPreviewAnimation reorderPreviewAnimation = ReorderPreviewAnimation.this;
                    reorderPreviewAnimation.initScale = CellLayout.this.getChildrenScale();
                    ReorderPreviewAnimation.this.repeating = true;
                }
            });
            CellLayout.this.mShakeAnimators.put(this.child, this);
            valueAnimatorOfFloat.start();
        }

        private void cancel() {
            Animator animator = this.a;
            if (animator != null) {
                animator.cancel();
            }
        }

        void completeAnimationImmediately() {
            Animator animator = this.a;
            if (animator != null) {
                animator.cancel();
            }
            AnimatorSet animatorSetCreateAnimatorSet = LauncherAnimUtils.createAnimatorSet();
            this.a = animatorSetCreateAnimatorSet;
            animatorSetCreateAnimatorSet.playTogether(LauncherAnimUtils.ofFloat(this.child, "scaleX", CellLayout.this.getChildrenScale()), LauncherAnimUtils.ofFloat(this.child, "scaleY", CellLayout.this.getChildrenScale()), LauncherAnimUtils.ofFloat(this.child, "translationX", 0.0f), LauncherAnimUtils.ofFloat(this.child, "translationY", 0.0f));
            animatorSetCreateAnimatorSet.setDuration(150L);
            animatorSetCreateAnimatorSet.setInterpolator(new DecelerateInterpolator(1.5f));
            animatorSetCreateAnimatorSet.start();
        }
    }

    private void completeAndClearReorderPreviewAnimations() {
        Iterator<ReorderPreviewAnimation> it = this.mShakeAnimators.values().iterator();
        while (it.hasNext()) {
            it.next().completeAnimationImmediately();
        }
        this.mShakeAnimators.clear();
    }

    private void commitTempPlacement() {
        for (int i = 0; i < this.mCountX; i++) {
            for (int i2 = 0; i2 < this.mCountY; i2++) {
                this.mOccupied[i][i2] = this.mTmpOccupied[i][i2];
            }
        }
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = this.mShortcutsAndWidgets.getChildAt(i3);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            ItemInfo itemInfo = (ItemInfo) childAt.getTag();
            if (itemInfo != null) {
                if (itemInfo.cellX != layoutParams.tmpCellX || itemInfo.cellY != layoutParams.tmpCellY || itemInfo.spanX != layoutParams.cellHSpan || itemInfo.spanY != layoutParams.cellVSpan) {
                    itemInfo.requiresDbUpdate = true;
                }
                int i4 = layoutParams.tmpCellX;
                layoutParams.cellX = i4;
                itemInfo.cellX = i4;
                int i5 = layoutParams.tmpCellY;
                layoutParams.cellY = i5;
                itemInfo.cellY = i5;
                itemInfo.spanX = layoutParams.cellHSpan;
                itemInfo.spanY = layoutParams.cellVSpan;
            }
        }
        this.mLauncher.getWorkspace().updateItemLocationsInDatabase(this);
    }

    private void setUseTempCoords(boolean useTempCoords) {
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ((LayoutParams) this.mShortcutsAndWidgets.getChildAt(i).getLayoutParams()).useTmpCoords = useTempCoords;
        }
    }

    private ItemConfiguration findConfigurationNoShuffle(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX, int spanY, View dragView, ItemConfiguration solution) {
        int[] iArr = new int[2];
        int[] iArr2 = new int[2];
        findNearestVacantArea(pixelX, pixelY, minSpanX, minSpanY, spanX, spanY, iArr, iArr2);
        if (iArr[0] >= 0 && iArr[1] >= 0) {
            copyCurrentStateToSolution(solution, false);
            solution.dragViewX = iArr[0];
            solution.dragViewY = iArr[1];
            solution.dragViewSpanX = iArr2[0];
            solution.dragViewSpanY = iArr2[1];
            solution.isSolution = true;
        } else {
            solution.isSolution = false;
        }
        return solution;
    }

    public void prepareChildForDrag(View child) {
        markCellsAsUnoccupiedForView(child);
    }

    private void getDirectionVectorForDrop(int dragViewCenterX, int dragViewCenterY, int spanX, int spanY, View dragView, int[] resultDirection) {
        int[] iArr = new int[2];
        findNearestArea(dragViewCenterX, dragViewCenterY, spanX, spanY, iArr);
        Rect rect = new Rect();
        regionToRect(iArr[0], iArr[1], spanX, spanY, rect);
        rect.offset(dragViewCenterX - rect.centerX(), dragViewCenterY - rect.centerY());
        Rect rect2 = new Rect();
        getViewsIntersectingRegion(iArr[0], iArr[1], spanX, spanY, dragView, rect2, this.mIntersectingViews);
        int iWidth = rect2.width();
        int iHeight = rect2.height();
        regionToRect(rect2.left, rect2.top, rect2.width(), rect2.height(), rect2);
        int iCenterX = (rect2.centerX() - dragViewCenterX) / spanX;
        int iCenterY = (rect2.centerY() - dragViewCenterY) / spanY;
        int i = this.mCountX;
        if (iWidth == i || spanX == i) {
            iCenterX = 0;
        }
        int i2 = this.mCountY;
        if (iHeight == i2 || spanY == i2) {
            iCenterY = 0;
        }
        if (iCenterX == 0 && iCenterY == 0) {
            resultDirection[0] = 1;
            resultDirection[1] = 0;
        } else {
            computeDirectionVector(iCenterX, iCenterY, resultDirection);
        }
    }

    private void getViewsIntersectingRegion(int cellX, int cellY, int spanX, int spanY, View dragView, Rect boundingRect, ArrayList<View> intersectingViews) {
        if (boundingRect != null) {
            boundingRect.set(cellX, cellY, cellX + spanX, cellY + spanY);
        }
        intersectingViews.clear();
        Rect rect = new Rect(cellX, cellY, spanX + cellX, spanY + cellY);
        Rect rect2 = new Rect();
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mShortcutsAndWidgets.getChildAt(i);
            if (childAt != dragView) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                rect2.set(layoutParams.cellX, layoutParams.cellY, layoutParams.cellX + layoutParams.cellHSpan, layoutParams.cellY + layoutParams.cellVSpan);
                if (Rect.intersects(rect, rect2)) {
                    this.mIntersectingViews.add(childAt);
                    if (boundingRect != null) {
                        boundingRect.union(rect2);
                    }
                }
            }
        }
    }

    boolean isNearestDropLocationOccupied(int pixelX, int pixelY, int spanX, int spanY, View dragView, int[] result) {
        int[] iArrFindNearestArea = findNearestArea(pixelX, pixelY, spanX, spanY, result);
        getViewsIntersectingRegion(iArrFindNearestArea[0], iArrFindNearestArea[1], spanX, spanY, dragView, null, this.mIntersectingViews);
        return !this.mIntersectingViews.isEmpty();
    }

    void revertTempState() {
        completeAndClearReorderPreviewAnimations();
        if (isItemPlacementDirty()) {
            int childCount = this.mShortcutsAndWidgets.getChildCount();
            boolean z = false;
            for (int i = 0; i < childCount; i++) {
                View childAt = this.mShortcutsAndWidgets.getChildAt(i);
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.tmpCellX != layoutParams.cellX || layoutParams.tmpCellY != layoutParams.cellY) {
                    layoutParams.tmpCellX = layoutParams.cellX;
                    layoutParams.tmpCellY = layoutParams.cellY;
                    if (animateChildToPosition(childAt, layoutParams.cellX, layoutParams.cellY, 150, 0, false, false)) {
                        z = true;
                    }
                }
            }
            if (z) {
                VibratorManager.performHapticFeedback(this.mLauncher, 65541);
            }
            setItemPlacementDirty(false);
        }
    }

    public boolean createAreaForResize(int cellX, int cellY, int spanX, int spanY, View dragView, int[] direction, boolean commit) {
        if (this.mIsHotseat) {
            if (cellX < 0 || cellX > this.mCountX || !canAddVacantCell()) {
                return false;
            }
            return (spanX == 1) & (spanY == 1);
        }
        int[] iArr = new int[2];
        regionToCenterPoint(cellX, cellY, spanX, spanY, iArr);
        ItemConfiguration itemConfigurationFindReorderSolution = findReorderSolution(iArr[0], iArr[1], spanX, spanY, spanX, spanY, direction, dragView, true, new ItemConfiguration());
        setUseTempCoords(true);
        if (itemConfigurationFindReorderSolution != null && itemConfigurationFindReorderSolution.isSolution) {
            copySolutionToTempState(itemConfigurationFindReorderSolution, dragView);
            setItemPlacementDirty(true);
            animateItemsToSolution(itemConfigurationFindReorderSolution, dragView, commit);
            if (commit) {
                commitTempPlacement();
                completeAndClearReorderPreviewAnimations();
                setItemPlacementDirty(false);
            } else {
                beginOrAdjustReorderPreviewAnimations(itemConfigurationFindReorderSolution, dragView, 150, 1);
            }
            this.mShortcutsAndWidgets.requestLayout();
        }
        return itemConfigurationFindReorderSolution.isSolution;
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x0070  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    int[] performReorder(int r22, int r23, int r24, int r25, int r26, int r27, android.view.View r28, int[] r29, int[] r30, int r31) {
        /*
            r21 = this;
            r11 = r21
            r12 = r28
            r13 = r31
            boolean r0 = r11.mIsHotseat
            r14 = 1
            r15 = 0
            if (r0 == 0) goto L2f
            r10 = r22
            r9 = r23
            r5 = r29
            int[] r8 = r11.findInsertArea(r10, r9, r5)
            r1 = r8[r15]
            r2 = r8[r14]
            r0 = r21
            r3 = r24
            r4 = r25
            r5 = r26
            r6 = r27
            r7 = r28
            r9 = r30
            r10 = r31
            int[] r0 = r0.performReorderForHotseat(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)
            return r0
        L2f:
            r10 = r22
            r9 = r23
            r5 = r29
            r0 = r21
            r1 = r22
            r2 = r23
            r3 = r26
            r4 = r27
            int[] r16 = r0.findNearestArea(r1, r2, r3, r4, r5)
            r8 = 2
            if (r30 != 0) goto L4b
            int[] r0 = new int[r8]
            r17 = r0
            goto L4d
        L4b:
            r17 = r30
        L4d:
            r7 = 3
            if (r13 == r8) goto L55
            if (r13 == r7) goto L55
            r0 = 4
            if (r13 != r0) goto L70
        L55:
            int[] r0 = r11.mPreviousReorderDirection
            r1 = r0[r15]
            r2 = -100
            if (r1 == r2) goto L70
            int[] r1 = r11.mDirectionVector
            r3 = r0[r15]
            r1[r15] = r3
            r3 = r0[r14]
            r1[r14] = r3
            if (r13 == r8) goto L6b
            if (r13 != r7) goto L8d
        L6b:
            r0[r15] = r2
            r0[r14] = r2
            goto L8d
        L70:
            int[] r6 = r11.mDirectionVector
            r0 = r21
            r1 = r22
            r2 = r23
            r3 = r26
            r4 = r27
            r5 = r28
            r0.getDirectionVectorForDrop(r1, r2, r3, r4, r5, r6)
            int[] r0 = r11.mPreviousReorderDirection
            int[] r1 = r11.mDirectionVector
            r2 = r1[r15]
            r0[r15] = r2
            r1 = r1[r14]
            r0[r14] = r1
        L8d:
            int[] r6 = r11.mDirectionVector
            r18 = 1
            com.android.launcher3.CellLayout$ItemConfiguration r5 = new com.android.launcher3.CellLayout$ItemConfiguration
            r5.<init>()
            r0 = r21
            r1 = r22
            r2 = r23
            r3 = r24
            r4 = r25
            r19 = r5
            r5 = r26
            r20 = r6
            r6 = r27
            r7 = r20
            r8 = r28
            r9 = r18
            r10 = r19
            com.android.launcher3.CellLayout$ItemConfiguration r9 = r0.findReorderSolution(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)
            com.android.launcher3.CellLayout$ItemConfiguration r8 = new com.android.launcher3.CellLayout$ItemConfiguration
            r8.<init>()
            r7 = r28
            com.android.launcher3.CellLayout$ItemConfiguration r0 = r0.findConfigurationNoShuffle(r1, r2, r3, r4, r5, r6, r7, r8)
            r1 = 0
            boolean r2 = r9.isSolution
            if (r2 == 0) goto Lcf
            int r2 = r9.area()
            int r3 = r0.area()
            if (r2 < r3) goto Lcf
            goto Ld6
        Lcf:
            boolean r2 = r0.isSolution
            if (r2 == 0) goto Ld5
            r9 = r0
            goto Ld6
        Ld5:
            r9 = r1
        Ld6:
            r0 = -1
            if (r13 != 0) goto Lf8
            if (r9 == 0) goto Lef
            r11.beginOrAdjustReorderPreviewAnimations(r9, r12, r15, r15)
            int r0 = r9.dragViewX
            r16[r15] = r0
            int r0 = r9.dragViewY
            r16[r14] = r0
            int r0 = r9.dragViewSpanX
            r17[r15] = r0
            int r0 = r9.dragViewSpanY
            r17[r14] = r0
            goto Lf7
        Lef:
            r17[r14] = r0
            r17[r15] = r0
            r16[r14] = r0
            r16[r15] = r0
        Lf7:
            return r16
        Lf8:
            r11.setUseTempCoords(r14)
            if (r9 == 0) goto L13b
            int r0 = r9.dragViewX
            r16[r15] = r0
            int r0 = r9.dragViewY
            r16[r14] = r0
            int r0 = r9.dragViewSpanX
            r17[r15] = r0
            int r0 = r9.dragViewSpanY
            r17[r14] = r0
            if (r13 == r14) goto L116
            r1 = 2
            r0 = 3
            if (r13 == r1) goto L118
            if (r13 != r0) goto L145
            goto L118
        L116:
            r0 = 3
            r1 = 2
        L118:
            r11.copySolutionToTempState(r9, r12)
            r11.setItemPlacementDirty(r14)
            if (r13 != r1) goto L122
            r2 = r14
            goto L123
        L122:
            r2 = r15
        L123:
            r11.animateItemsToSolution(r9, r12, r2)
            if (r13 == r1) goto L131
            if (r13 != r0) goto L12b
            goto L131
        L12b:
            r0 = 150(0x96, float:2.1E-43)
            r11.beginOrAdjustReorderPreviewAnimations(r9, r12, r0, r14)
            goto L145
        L131:
            r21.commitTempPlacement()
            r21.completeAndClearReorderPreviewAnimations()
            r11.setItemPlacementDirty(r15)
            goto L145
        L13b:
            r1 = 2
            r17[r14] = r0
            r17[r15] = r0
            r16[r14] = r0
            r16[r15] = r0
            r14 = r15
        L145:
            if (r13 == r1) goto L149
            if (r14 != 0) goto L14c
        L149:
            r11.setUseTempCoords(r15)
        L14c:
            com.android.launcher3.ShortcutAndWidgetContainer r0 = r11.mShortcutsAndWidgets
            r0.requestLayout()
            return r16
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.CellLayout.performReorder(int, int, int, int, int, int, android.view.View, int[], int[], int):int[]");
    }

    void setItemPlacementDirty(boolean dirty) {
        this.mItemPlacementDirty = dirty;
    }

    boolean isItemPlacementDirty() {
        return this.mItemPlacementDirty;
    }

    class ItemConfiguration {
        int dragViewSpanX;
        int dragViewSpanY;
        int dragViewX;
        int dragViewY;
        ArrayList<View> intersectingViews;
        HashMap<View, CellAndSpan> map = new HashMap<>();
        private HashMap<View, CellAndSpan> savedMap = new HashMap<>();
        ArrayList<View> sortedViews = new ArrayList<>();
        boolean isSolution = false;

        ItemConfiguration() {
        }

        void save() {
            for (View view : this.map.keySet()) {
                this.map.get(view).copy(this.savedMap.get(view));
            }
        }

        void restore() {
            for (View view : this.savedMap.keySet()) {
                this.savedMap.get(view).copy(this.map.get(view));
            }
        }

        void add(View v, CellAndSpan cs) {
            this.map.put(v, cs);
            this.savedMap.put(v, CellLayout.this.new CellAndSpan());
            this.sortedViews.add(v);
        }

        int area() {
            return this.dragViewSpanX * this.dragViewSpanY;
        }
    }

    private class CellAndSpan {
        int spanX;
        int spanY;
        int x;
        int y;

        public CellAndSpan() {
        }

        public void copy(CellAndSpan copy) {
            copy.x = this.x;
            copy.y = this.y;
            copy.spanX = this.spanX;
            copy.spanY = this.spanY;
        }

        public CellAndSpan(int x, int y, int spanX, int spanY) {
            this.x = x;
            this.y = y;
            this.spanX = spanX;
            this.spanY = spanY;
        }

        public String toString() {
            return "(" + this.x + ", " + this.y + ": " + this.spanX + ", " + this.spanY + ")";
        }
    }

    public int[] findNearestArea(int pixelX, int pixelY, int spanX, int spanY, int[] result) {
        return findNearestArea(pixelX, pixelY, spanX, spanY, spanX, spanY, false, result, null);
    }

    boolean existsEmptyCell() {
        return findCellForSpan(null, 1, 1);
    }

    public boolean findCellForSpan(int[] cellXY, int spanX, int spanY) {
        int i;
        int i2 = this.mCountX - (spanX - 1);
        int i3 = this.mCountY - (spanY - 1);
        boolean z = false;
        for (int i4 = 0; i4 < i3 && !z; i4++) {
            int i5 = 0;
            while (true) {
                if (i5 < i2) {
                    for (int i6 = 0; i6 < spanX; i6++) {
                        for (int i7 = 0; i7 < spanY; i7++) {
                            i = i5 + i6;
                            if (this.mOccupied[i][i4 + i7]) {
                                break;
                            }
                        }
                    }
                    if (cellXY != null) {
                        cellXY[0] = i5;
                        cellXY[1] = i4;
                    }
                    z = true;
                }
                i5 = i + 1;
            }
        }
        return z;
    }

    void onDragEnter() {
        this.mDragging = true;
    }

    void onDragExit() {
        if (this.mDragging) {
            this.mDragging = false;
        }
        int[] iArr = this.mDragCell;
        iArr[1] = -1;
        iArr[0] = -1;
        this.mDragOutlineAnims[this.mDragOutlineCurrent].animateOut();
        this.mDragOutlineCurrent = (this.mDragOutlineCurrent + 1) % this.mDragOutlineAnims.length;
        revertTempState();
        setIsDragOverlapping(false);
    }

    void onDropChild(View child) {
        if (child != null) {
            ((LayoutParams) child.getLayoutParams()).dropped = true;
            child.requestLayout();
        }
    }

    public void cellToRect(int cellX, int cellY, int cellHSpan, int cellVSpan, Rect resultRect) {
        int i = this.mCountX;
        if (i < cellHSpan) {
            cellHSpan = i;
        }
        Integer numValueOf = Integer.valueOf(cellHSpan);
        int i2 = this.mCountY;
        if (i2 < cellVSpan) {
            cellVSpan = i2;
        }
        Integer numValueOf2 = Integer.valueOf(cellVSpan);
        int iIntValue = numValueOf.intValue();
        int iIntValue2 = numValueOf2.intValue();
        int i3 = this.mCellWidth;
        int i4 = this.mCellHeight;
        int i5 = this.mWidthGap;
        int i6 = this.mHeightGap;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int i7 = (iIntValue * i3) + ((iIntValue - 1) * i5);
        int i8 = (iIntValue2 * i4) + ((iIntValue2 - 1) * i6);
        int i9 = paddingLeft + (cellX * (i3 + i5));
        int i10 = 0;
        Workspace.State workspaceState = getWorkspaceState();
        if (com.lge.launcher3.util.Utilities.isLGUI7_1() && workspaceState != null && (workspaceState == Workspace.State.OVERVIEW || workspaceState == Workspace.State.OVERVIEW_HIDDEN)) {
            i10 = this.mDefaultHomeLayoutHeight;
        }
        int i11 = paddingTop + (cellY * (i4 + i6)) + i10;
        resultRect.set(i9, i11, i7 + i9, i8 + i11);
    }

    public static int[] rectToCell(Launcher launcher, int width, int height, int[] result) {
        return rectToCell(launcher.getDeviceProfile(), launcher, width, height, result);
    }

    public static int[] rectToCell(DeviceProfile grid, Context context, int width, int height, int[] result) {
        Rect workspacePadding = grid.getWorkspacePadding(Utilities.isRtl(context.getResources()));
        float fMin = Math.min(DeviceProfile.calculateCellWidth((grid.widthPx - workspacePadding.left) - workspacePadding.right, grid.inv.numColumns), DeviceProfile.calculateCellHeight((grid.heightPx - workspacePadding.top) - workspacePadding.bottom, grid.inv.numRows));
        int iCeil = (int) Math.ceil(width / fMin);
        int iCeil2 = (int) Math.ceil(height / fMin);
        if (result == null) {
            return new int[]{iCeil, iCeil2};
        }
        result[0] = iCeil;
        result[1] = iCeil2;
        return result;
    }

    public void calculateSpans(ItemInfo info) {
        int i;
        int i2;
        if (info instanceof LauncherAppWidgetInfo) {
            LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) info;
            i = launcherAppWidgetInfo.minWidth;
            i2 = launcherAppWidgetInfo.minHeight;
        } else if (info instanceof PendingAddWidgetInfo) {
            PendingAddWidgetInfo pendingAddWidgetInfo = (PendingAddWidgetInfo) info;
            i = pendingAddWidgetInfo.minWidth;
            i2 = pendingAddWidgetInfo.minHeight;
        } else {
            info.spanY = 1;
            info.spanX = 1;
            return;
        }
        int[] iArrRectToCell = rectToCell(this.mLauncher, i, i2, null);
        info.spanX = iArrRectToCell[0];
        info.spanY = iArrRectToCell[1];
    }

    private void clearOccupiedCells() {
        for (int i = 0; i < this.mCountX; i++) {
            for (int i2 = 0; i2 < this.mCountY; i2++) {
                this.mOccupied[i][i2] = false;
            }
        }
    }

    public void markCellsAsOccupiedForView(View view) {
        if (view == null || view.getParent() != this.mShortcutsAndWidgets) {
            return;
        }
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        markCellsForView(layoutParams.cellX, layoutParams.cellY, layoutParams.cellHSpan, layoutParams.cellVSpan, this.mOccupied, true);
    }

    public void markCellsAsUnoccupiedForView(View view) {
        if (view == null || view.getParent() != this.mShortcutsAndWidgets) {
            return;
        }
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        markCellsForView(layoutParams.cellX, layoutParams.cellY, layoutParams.cellHSpan, layoutParams.cellVSpan, this.mOccupied, false);
    }

    private void markCellsForView(int cellX, int cellY, int spanX, int spanY, boolean[][] occupied, boolean value) {
        if (cellX < 0 || cellY < 0) {
            return;
        }
        for (int i = cellX; i < cellX + spanX && i < this.mCountX; i++) {
            for (int i2 = cellY; i2 < cellY + spanY && i2 < this.mCountY; i2++) {
                occupied[i][i2] = value;
            }
        }
    }

    public int getDesiredWidth() {
        int paddingLeft = getPaddingLeft() + getPaddingRight();
        int i = this.mCountX;
        return paddingLeft + (this.mCellWidth * i) + (Math.max(i - 1, 0) * this.mWidthGap);
    }

    public int getDesiredHeight() {
        int paddingTop = getPaddingTop() + getPaddingBottom();
        int i = this.mCountY;
        return paddingTop + (this.mCellHeight * i) + (Math.max(i - 1, 0) * this.mHeightGap);
    }

    public boolean isOccupied(int x, int y) {
        if (x < this.mCountX && y < this.mCountY) {
            return this.mOccupied[x][y];
        }
        throw new RuntimeException("Position exceeds the bound of this CellLayout");
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public boolean canReorder;

        @ViewDebug.ExportedProperty
        public int cellHSpan;

        @ViewDebug.ExportedProperty
        public int cellVSpan;

        @ViewDebug.ExportedProperty
        public int cellX;

        @ViewDebug.ExportedProperty
        public int cellY;
        boolean dropped;
        public boolean isFullscreen;
        public boolean isLockedToGrid;
        public int tmpCellX;
        public int tmpCellY;
        public boolean useTmpCoords;

        @ViewDebug.ExportedProperty
        public int x;

        @ViewDebug.ExportedProperty
        public int y;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.isLockedToGrid = true;
            this.isFullscreen = false;
            this.canReorder = true;
            this.cellHSpan = 1;
            this.cellVSpan = 1;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            this.isLockedToGrid = true;
            this.isFullscreen = false;
            this.canReorder = true;
            this.cellHSpan = 1;
            this.cellVSpan = 1;
        }

        public LayoutParams(LayoutParams source) {
            super((ViewGroup.MarginLayoutParams) source);
            this.isLockedToGrid = true;
            this.isFullscreen = false;
            this.canReorder = true;
            this.cellX = source.cellX;
            this.cellY = source.cellY;
            this.cellHSpan = source.cellHSpan;
            this.cellVSpan = source.cellVSpan;
        }

        public LayoutParams(int cellX, int cellY, int cellHSpan, int cellVSpan) {
            super(-1, -1);
            this.isLockedToGrid = true;
            this.isFullscreen = false;
            this.canReorder = true;
            this.cellX = cellX;
            this.cellY = cellY;
            this.cellHSpan = cellHSpan;
            this.cellVSpan = cellVSpan;
        }

        public void setup(int cellWidth, int cellHeight, int widthGap, int heightGap, boolean invertHorizontally, int colCount) {
            if (this.isLockedToGrid) {
                int i = this.cellHSpan;
                int i2 = this.cellVSpan;
                boolean z = this.useTmpCoords;
                int i3 = z ? this.tmpCellX : this.cellX;
                int i4 = z ? this.tmpCellY : this.cellY;
                if (invertHorizontally) {
                    i3 = (colCount - i3) - i;
                }
                this.width = (((i * cellWidth) + ((i - 1) * widthGap)) - this.leftMargin) - this.rightMargin;
                this.height = (((i2 * cellHeight) + ((i2 - 1) * heightGap)) - this.topMargin) - this.bottomMargin;
                this.x = (i3 * (cellWidth + widthGap)) + this.leftMargin;
                this.y = (i4 * (cellHeight + heightGap)) + this.topMargin;
            }
        }

        public String toString() {
            return "(" + this.cellX + ", " + this.cellY + ")";
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getWidth() {
            return this.width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getHeight() {
            return this.height;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getX() {
            return this.x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getY() {
            return this.y;
        }
    }

    public static final class CellInfo {
        public View cell;
        int cellX;
        int cellY;
        long container;
        long screenId;
        int spanX;
        int spanY;

        public CellInfo(View v, ItemInfo info) {
            this.cellX = -1;
            this.cellY = -1;
            this.cell = v;
            this.cellX = info.cellX;
            this.cellY = info.cellY;
            this.spanX = info.spanX;
            this.spanY = info.spanY;
            this.screenId = info.screenId;
            this.container = info.container;
        }

        public String toString() {
            View view = this.cell;
            return "Cell[view=" + (view == null ? "null" : view.getClass()) + ", x=" + this.cellX + ", y=" + this.cellY + "]";
        }

        public ItemInfo getItemInfo() {
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.cellX = this.cellX;
            itemInfo.cellY = this.cellY;
            itemInfo.spanX = this.spanX;
            itemInfo.spanY = this.spanY;
            itemInfo.screenId = this.screenId;
            itemInfo.container = this.container;
            return itemInfo;
        }
    }

    public boolean findVacantCell(int spanX, int spanY, int[] outXY) {
        return Utilities.findVacantCell(outXY, spanX, spanY, this.mCountX, this.mCountY, this.mOccupied);
    }

    public boolean isRegionVacant(int x, int y, int spanX, int spanY) {
        int i = (spanX + x) - 1;
        int i2 = (spanY + y) - 1;
        if (x < 0 || y < 0 || i >= this.mCountX || i2 >= this.mCountY) {
            return false;
        }
        while (x <= i) {
            for (int i3 = y; i3 <= i2; i3++) {
                if (this.mOccupied[x][i3]) {
                    return false;
                }
            }
            x++;
        }
        return true;
    }

    public void removeViewWithoutMarkingCells(View view) {
        this.mShortcutsAndWidgets.removeView(view);
        this.mLauncher.getHotword().updateHotwordDetection(this);
    }

    public boolean[][] getOccupied() {
        return this.mOccupied;
    }

    public void createCrossHairsGrid() {
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            return;
        }
        this.mCrossHairsGrid = new CrossHairsGrid(getContext());
    }

    public void createCrossHairsGrid(int countX, int countY) {
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            return;
        }
        this.mCrossHairsGrid = new CrossHairsGrid(getContext(), countX, countY);
    }

    private void initDrawable() {
        CrossHairsGrid crossHairsGrid = this.mCrossHairsGrid;
        if (crossHairsGrid != null) {
            crossHairsGrid.initDrawables(getContext());
        }
    }

    private void drawGrid(Canvas canvas) {
        CrossHairsGrid crossHairsGrid = this.mCrossHairsGrid;
        if (crossHairsGrid == null || !crossHairsGrid.visible()) {
            return;
        }
        Workspace.State workspaceState = getWorkspaceState();
        if (com.lge.launcher3.util.Utilities.isLGUI7_1() && workspaceState != null && (workspaceState == Workspace.State.OVERVIEW || workspaceState == Workspace.State.OVERVIEW_HIDDEN)) {
            this.mCrossHairsGrid.draw(canvas, this, this.mDefaultHomeLayoutHeight);
        } else {
            this.mCrossHairsGrid.draw(canvas, this, 0);
        }
    }

    public void setCrosshairsVisibility(float crosshairsVisibility) {
        CrossHairsGrid crossHairsGrid = this.mCrossHairsGrid;
        if (crossHairsGrid != null) {
            crossHairsGrid.setAlpha(crosshairsVisibility);
        }
    }

    public void setCrosshairAnimation(boolean available) {
        CrossHairsGrid crossHairsGrid = this.mCrossHairsGrid;
        if (crossHairsGrid == null) {
            return;
        }
        InterruptibleInOutAnimator animator = crossHairsGrid.setAnimator(this, 0);
        this.mCrosshairsAnimator = animator;
        if (available) {
            animator.animateIn();
        } else {
            animator.animateOut();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mCrossHairsGrid == null) {
        }
    }

    private void drawGlowOutlineDropCue(Canvas canvas) {
        Paint paint = this.mDragCellBGPaint;
        if (paint != null) {
            paint.setStyle(Paint.Style.FILL);
            this.mDragCellBGPaint.setColor(this.mDragBGColor);
            canvas.drawRect(this.mDragCellRect, this.mDragCellBGPaint);
            this.mDragCellBGPaint.setStyle(Paint.Style.STROKE);
            this.mDragCellBGPaint.setColor(this.mDragBGOutlineColor);
            this.mDragCellBGPaint.setStrokeWidth(4.0f);
            canvas.drawRect(this.mDragCellRect, this.mDragCellBGPaint);
        }
        Bitmap bitmap = this.mGlowOutline;
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, this.mGlowOutlineDrawPos.x, this.mGlowOutlineDrawPos.y, (Paint) null);
        }
    }

    private void drawWidgetDropCue(Canvas canvas) {
        Drawable drawable = this.mWidgetDropCue;
        if (drawable != null) {
            drawable.setBounds(this.mDragCellRect);
            this.mWidgetDropCue.draw(canvas);
        }
    }

    public void clearRect(boolean isDragOverlapping) {
        if (this.mIsDragOverlapping != isDragOverlapping) {
            this.mIsDragOverlapping = isDragOverlapping;
            invalidate();
        }
        if (this.mIsDragOverlapping) {
            return;
        }
        this.mDragCellRect = null;
        this.mGlowOutline = null;
    }

    public int[] cellSpansToSize(int hSpans, int vSpans) {
        return new int[]{(this.mCellWidth * hSpans) + ((hSpans - 1) * this.mWidthGap), (this.mCellHeight * vSpans) + ((vSpans - 1) * this.mHeightGap)};
    }

    public void updateGridSize(int x, int y) {
        int i = this.mCellWidth * this.mCountX;
        int i2 = this.mCellHeight * this.mCountY;
        this.mCountX = x;
        this.mCountY = y;
        this.mOccupied = (boolean[][]) Array.newInstance((Class<?>) boolean.class, x, y);
        this.mTmpOccupied = (boolean[][]) Array.newInstance((Class<?>) boolean.class, this.mCountX, this.mCountY);
        this.mTempRectStack.clear();
        ShortcutAndWidgetContainer shortcutAndWidgetContainer = this.mShortcutsAndWidgets;
        int i3 = this.mCountX;
        int i4 = this.mCountY;
        shortcutAndWidgetContainer.setCellDimensions(i / i3, i2 / i4, this.mWidthGap, this.mHeightGap, i3, i4);
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), Integer.MIN_VALUE);
        int iMakeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), Integer.MIN_VALUE);
        requestLayout();
        measure(iMakeMeasureSpec, iMakeMeasureSpec2);
        CrossHairsGrid crossHairsGrid = this.mCrossHairsGrid;
        if (crossHairsGrid != null) {
            crossHairsGrid.updateCrossPoint(this.mCountX, this.mCountY);
            getGlobalVisibleRect(new Rect());
            this.mShortcutsAndWidgets.requestLayout();
        }
        updateCurrentOccupied(this.mOccupied, this.mCountX, this.mCountY);
    }

    private void updateCurrentOccupied(boolean[][] occupied, int countX, int countY) {
        for (int i = 0; i < countX; i++) {
            for (int i2 = 0; i2 < countY; i2++) {
                occupied[i][i2] = false;
            }
        }
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            LayoutParams layoutParams = (LayoutParams) this.mShortcutsAndWidgets.getChildAt(i3).getLayoutParams();
            markCellsForView(layoutParams.cellX, layoutParams.cellY, layoutParams.cellHSpan, layoutParams.cellVSpan, occupied, true);
        }
    }

    public void animationChangeGrid(boolean animate) {
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mShortcutsAndWidgets.getChildAt(i);
            if (animate) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                animateChildToPosition(childAt, layoutParams.cellX, layoutParams.cellY, 400, 100, false, false);
            } else {
                childAt.setScaleX(getChildrenScale());
                childAt.setScaleY(getChildrenScale());
            }
        }
    }

    @Override // com.lge.launcher3.screeneffect.IScreenEffectable
    public int getShortcutAndWidgetLayer() {
        return this.mShortcutsAndWidgets.getLayerType();
    }

    @Override // com.lge.launcher3.screeneffect.IScreenEffectable
    public Bitmap getChildrenDrawingCache(boolean autoScale) {
        if (getShortcutAndWidgetLayer() == 1) {
            return this.mShortcutsAndWidgets.getDrawingCache(true);
        }
        return null;
    }

    @Override // com.lge.launcher3.screeneffect.IScreenEffectable
    public ScreenEffectBase getCustomScreenEffect() {
        return this.mCustomScreenEffect;
    }

    @Override // com.lge.launcher3.screeneffect.IScreenEffectable
    public void setCustomScreenEffect(ScreenEffectBase screenEffect) {
        this.mCustomScreenEffect = screenEffect;
    }

    public Workspace.State getWorkspaceState() {
        ViewParent parent = getParent();
        if (parent == null || !(parent instanceof Workspace)) {
            return null;
        }
        return ((Workspace) parent).getState();
    }

    public void drawBackgroundOutlineInOverviewMode(Canvas canvas, Drawable background) {
        Workspace.State workspaceState;
        if (Build.LGUI_VERSION.RELEASE < 6 && (workspaceState = getWorkspaceState()) != null && workspaceState == Workspace.State.OVERVIEW) {
            if (this.mBackgroundBoundLinePaint == null) {
                this.mBackgroundBoundLinePaint = PaintUtils.getStrokePaint(com.lge.launcher3.util.Utilities.sWhite, (int) getContext().getResources().getDimension(R.dimen.celllayout_background_outline_width), 255, null);
            }
            this.mBackgroundBoundLinePaint.setAlpha((int) ((background != null ? background.getAlpha() / 255.0f : 1.0f) * 0.85f * 255.0f));
            canvas.drawRect(0.0f, 0.0f, getWidth(), getHeight(), this.mBackgroundBoundLinePaint);
        }
    }

    public boolean findAppWidgetByComponentName(ComponentName componentName) {
        LauncherAppWidgetInfo launcherAppWidgetInfo;
        ShortcutAndWidgetContainer shortcutsAndWidgets = getShortcutsAndWidgets();
        for (int i = 0; i < shortcutsAndWidgets.getChildCount(); i++) {
            View childAt = shortcutsAndWidgets.getChildAt(i);
            if ((childAt instanceof LauncherAppWidgetHostView) && (launcherAppWidgetInfo = (LauncherAppWidgetInfo) childAt.getTag()) != null && launcherAppWidgetInfo.providerName != null && componentName != null && componentName.equals(launcherAppWidgetInfo.providerName)) {
                return true;
            }
        }
        return false;
    }

    private void onCellLayoutCreated() {
        this.mShortcutsAndWidgets.setOnHierarchyChangeListener(this);
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        float f = (deviceProfile.availableWidthPx / deviceProfile.inv.numColumns) / 2.0f;
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.folder_distance_ratio_margin);
        TypedValue typedValue = new TypedValue();
        getResources().getValue(R.dimen.config_folderDistanceRatio, typedValue, true);
        float f2 = typedValue.getFloat();
        this.mDistanceForFolderCreationRatio = f2;
        if (f <= (f2 * deviceProfile.iconSizePx) + dimensionPixelSize) {
            getResources().getValue(R.dimen.config_folderDistanceRatioSmall, typedValue, true);
            this.mDistanceForFolderCreationRatio = typedValue.getFloat();
        }
        this.mMaxDistanceForFolderCreation = this.mDistanceForFolderCreationRatio * deviceProfile.iconSizePx;
        this.mInvalidTouchDistance = deviceProfile.iconSizePx * 0.85f;
        this.mVertical = deviceProfile.isVerticalBarLayout();
    }

    public int getMaxCount() {
        if (!this.mIsHotseat) {
            return this.mCountX;
        }
        return this.mLauncher.getDeviceProfile().inv.numHotseatIcons;
    }

    public float getMaxDistanceForFolderCreation() {
        return this.mMaxDistanceForFolderCreation;
    }

    @Override // android.view.ViewGroup.OnHierarchyChangeListener
    public void onChildViewAdded(View parent, View child) {
        if (this.mIsHotseat && parent == this.mShortcutsAndWidgets) {
            LGLog.d(TAG, "Child view " + child + " is added");
            adjustGridSize();
        }
    }

    @Override // android.view.ViewGroup.OnHierarchyChangeListener
    public void onChildViewRemoved(View parent, View child) {
        if (this.mIsHotseat && parent == this.mShortcutsAndWidgets) {
            LGLog.d(TAG, "Child view " + child + " is removed");
            adjustGridSize();
        }
    }

    private int getHotseatGridSize() {
        if (this.mVertical) {
            return this.mCountY;
        }
        return this.mCountX;
    }

    private void setHotseatGridSize(int size) {
        if (this.mVertical) {
            this.mCountY = size;
        } else {
            this.mCountX = size;
        }
    }

    private int getLayoutOrderInHotseat(LayoutParams params) {
        return this.mVertical ? params.cellY : params.cellX;
    }

    private void setLayoutOrderInHotseat(LayoutParams params, int order) {
        if (this.mVertical) {
            params.cellX = 0;
            params.cellY = order;
        } else {
            params.cellX = order;
            params.cellY = 0;
        }
    }

    private long getOrderInHotseat(ItemInfo info) {
        return info.screenId;
    }

    private void setOrderInHotseat(ItemInfo info, int order) {
        if (this.mVertical) {
            info.cellY = order;
        } else {
            info.cellX = order;
        }
        info.screenId = order;
    }

    private void adjustGridSize() {
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        if (this.mVacantCellOrder != -1) {
            childCount++;
        }
        int iMax = Math.max(1, childCount);
        if (getHotseatGridSize() == iMax) {
            return;
        }
        LGLog.d(TAG, "Adjust grid size: " + getHotseatGridSize() + " -> " + iMax);
        setHotseatGridSize(iMax);
        this.mOccupied = (boolean[][]) Array.newInstance((Class<?>) boolean.class, this.mCountX, this.mCountY);
        this.mTmpOccupied = (boolean[][]) Array.newInstance((Class<?>) boolean.class, this.mCountX, this.mCountY);
        this.mTempRectStack.clear();
        measureCellDimension(getMeasuredWidth() - (getPaddingLeft() + getPaddingRight()), getMeasuredHeight() - (getPaddingTop() + getPaddingBottom()));
        this.mShortcutsAndWidgets.setCellDimensions(this.mCellWidth, this.mCellHeight, this.mWidthGap, this.mHeightGap, this.mCountX, this.mCountY);
    }

    private boolean addViewToHotseatCellLayout(View child, int index, int childId, LayoutParams params) {
        ItemInfo itemInfo;
        if (child instanceof BubbleTextView) {
            BubbleTextView bubbleTextView = (BubbleTextView) child;
            if (this.mIsHotseat) {
                if (this.mLauncher.mOrientationOfCurrentLayout == 0) {
                    bubbleTextView.setTextVisibility(!this.mIsHotseat);
                } else {
                    bubbleTextView.setTextVisibility(this.mLauncher.getDeviceProfile().isLandscape && this.mLauncher.getDeviceProfile().allowRotation);
                }
            } else {
                bubbleTextView.setTextVisibility(true);
            }
        }
        child.setScaleX(getChildrenScale());
        child.setScaleY(getChildrenScale());
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        int i = 0;
        for (int i2 = 0; i2 < childCount && getLayoutOrderInHotseat((LayoutParams) this.mShortcutsAndWidgets.getChildAt(i).getLayoutParams()) < getLayoutOrderInHotseat(params); i2++) {
            i++;
        }
        LGLog.d(TAG, "Add view " + child + " at " + i);
        int childCount2 = this.mShortcutsAndWidgets.getChildCount();
        if (child != null && (itemInfo = (ItemInfo) child.getTag()) != null) {
            TalkBackUtils.sendAccessibilityEvent(getContext(), itemInfo.title + "," + String.format(getContext().getString(R.string.moved_hotseat_item), Integer.valueOf(i + 1), Integer.valueOf(childCount2 + 1)), true);
        }
        child.setId(childId);
        params.useTmpCoords = false;
        this.mShortcutsAndWidgets.addView(child, i, params);
        rearrangeChildren(false, true);
        return true;
    }

    public int[] findInsertAreaInVerticalLayout(int pixelX, int pixelY, int[] result) {
        int i;
        int paddingTop = (int) (getPaddingTop() + (this.mCellHeight / 2) + this.mMaxDistanceForFolderCreation);
        int i2 = 0;
        int i3 = 0;
        while (true) {
            i = this.mCountY;
            if (i2 < i) {
                if (i2 == this.mVacantCellOrder) {
                    paddingTop = (int) (paddingTop + ((this.mCellHeight + this.mHeightGap) - (this.mMaxDistanceForFolderCreation * 2.0f)));
                }
                if (i3 <= pixelY && pixelY < paddingTop) {
                    result[0] = 0;
                    result[1] = i2;
                    break;
                }
                i2++;
                i3 = paddingTop;
                paddingTop = this.mCellHeight + this.mHeightGap + paddingTop;
            } else {
                break;
            }
        }
        if (result[0] == -1) {
            result[0] = 0;
            result[1] = i;
        }
        return result;
    }

    public int[] findInsertAreaInHorizontalLayout(int pixelX, int pixelY, int[] result) {
        int i;
        int paddingLeft = (int) (getPaddingLeft() + (this.mCellWidth / 2) + this.mMaxDistanceForFolderCreation);
        int i2 = 0;
        int i3 = 0;
        while (true) {
            i = this.mCountX;
            if (i2 < i) {
                if (i2 == this.mVacantCellOrder) {
                    paddingLeft = (int) (paddingLeft + ((this.mCellWidth + this.mWidthGap) - (this.mMaxDistanceForFolderCreation * 2.0f)));
                }
                if (i3 <= pixelX && pixelX < paddingLeft) {
                    result[0] = i2;
                    result[1] = 0;
                    break;
                }
                i2++;
                i3 = paddingLeft;
                paddingLeft = this.mCellWidth + this.mWidthGap + paddingLeft;
            } else {
                break;
            }
        }
        if (result[0] == -1) {
            result[0] = i;
            result[1] = 0;
        }
        return result;
    }

    public int[] findInsertArea(int pixelX, int pixelY, int[] result) {
        if (result == null) {
            result = new int[2];
        }
        result[1] = -1;
        result[0] = -1;
        if (this.mShortcutsAndWidgets.getChildCount() == 0) {
            result[1] = 0;
            result[0] = 0;
            return result;
        }
        if (this.mVertical) {
            return findInsertAreaInVerticalLayout(pixelX, pixelY, result);
        }
        return findInsertAreaInHorizontalLayout(pixelX, pixelY, result);
    }

    private boolean hasVacantCell() {
        return this.mVacantCellOrder != -1;
    }

    private boolean canReorderChildren() {
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (!((LayoutParams) this.mShortcutsAndWidgets.getChildAt(i).getLayoutParams()).canReorder) {
                return false;
            }
        }
        return true;
    }

    private void addVacantCell(int order) {
        if (order < 0 || order > getHotseatGridSize()) {
            LGLog.w(TAG, "Failed to add vacant cell: invalid order " + order, new int[0]);
            return;
        }
        if (canAddVacantCell() && canReorderChildren()) {
            LGLog.d(TAG, "Add vacant cell at " + order);
            this.mVacantCellOrder = order;
            adjustGridSize();
            rearrangeChildren(true, false);
        }
    }

    private void moveVacantCell(int order) {
        if (order < 0 || order >= getHotseatGridSize()) {
            LGLog.w(TAG, "Failed to move vacant cell: invalid order " + order, new int[0]);
            return;
        }
        if (this.mVacantCellOrder != order && canReorderChildren()) {
            LGLog.d(TAG, "Move vacant cell: " + this.mVacantCellOrder + " -> " + order);
            this.mVacantCellOrder = order;
            rearrangeChildren(true, false);
        }
    }

    public void rearrangeChildren(boolean animate, boolean commit) {
        LGLog.d(TAG, "Rearrange children");
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        int i = 0;
        for (int i2 = 0; i2 < childCount; i2++) {
            if (i == this.mVacantCellOrder) {
                i++;
            }
            View childAt = this.mShortcutsAndWidgets.getChildAt(i2);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            ItemInfo itemInfo = (ItemInfo) childAt.getTag();
            if (itemInfo != null) {
                if (HomeSettingsSharedPreferences.getEnableAppDrawerButton(getContext()) || getOrderInHotseat(itemInfo) != i) {
                    itemInfo.requiresDbUpdate = true;
                }
                setOrderInHotseat(itemInfo, i);
                setLayoutOrderInHotseat(layoutParams, i);
                LGLog.d(TAG, "[" + i + "] " + childAt);
            }
            if (animate) {
                animateChildToPosition(childAt, layoutParams.cellX, layoutParams.cellY, 300, 0, true, false);
            } else {
                childAt.setScaleX(getChildrenScale());
                childAt.setScaleY(getChildrenScale());
            }
            i++;
        }
        if (!animate) {
            this.mShortcutsAndWidgets.requestLayout();
        }
        if (commit) {
            LGLog.d(TAG, "Commit rearrangement");
            Workspace workspace = this.mLauncher.getWorkspace();
            if (workspace != null) {
                workspace.updateItemLocationsInDatabase(this);
            }
        }
    }

    public int[] performReorderForHotseat(int cellX, int cellY, int minSpanX, int minSpanY, int spanX, int spanY, View dragView, int[] result, int[] resultSpan, int mode) {
        if (result == null) {
            result = new int[2];
        }
        if (resultSpan == null) {
            resultSpan = new int[2];
        }
        if ((spanX != 1 && spanY != 1) || (dragView instanceof AppWidgetHostView) || mode == 0) {
            resultSpan[1] = -1;
            resultSpan[0] = -1;
            result[1] = -1;
            result[0] = -1;
            return result;
        }
        if (this.mVertical) {
            cellX = cellY;
        }
        if (hasVacantCell()) {
            moveVacantCell(cellX);
        } else {
            addVacantCell(cellX);
        }
        int i = this.mVacantCellOrder;
        if (i == -1) {
            resultSpan[1] = -1;
            resultSpan[0] = -1;
            result[1] = -1;
            result[0] = -1;
        } else {
            if (this.mVertical) {
                result[0] = 0;
                result[1] = i;
            } else {
                result[0] = i;
                result[1] = 0;
            }
            resultSpan[1] = 1;
            resultSpan[0] = 1;
        }
        return result;
    }

    public void cleanupVacantCell(boolean aniamte) {
        if (this.mVacantCellOrder != -1) {
            this.mVacantCellOrder = -1;
            adjustGridSize();
            rearrangeChildren(aniamte, true);
        }
    }

    public boolean canAddVacantCell() {
        return getHotseatGridSize() < getMaxCount();
    }

    public void returnToOriginalPosition(CellInfo dragInfo) {
        LGLog.d(TAG, "Return to original position");
        View view = dragInfo.cell;
        addViewToCellLayout(view, 0, this.mLauncher.getViewIdForItem((ItemInfo) view.getTag()), (LayoutParams) view.getLayoutParams(), false);
        cleanupVacantCell(true);
        rearrangeChildren(true, true);
    }

    public boolean isValidTouchArea(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        int[] iArrFindNearestArea = findNearestArea(x, y, 1, 1, new int[2]);
        float distanceFromCell = getDistanceFromCell(x, y, iArrFindNearestArea);
        View childAt = getChildAt(iArrFindNearestArea[0], iArrFindNearestArea[1]);
        return this.mShortcutsAndWidgets.isLayoutHorizontal(childAt) ? this.mIsHotseat ? distanceFromCell < ((float) getCellWidth()) : distanceFromCell < ((float) ((this.mLauncher.getDeviceProfile().iconSizePx / 2) + childAt.getPaddingStart())) : distanceFromCell < this.mInvalidTouchDistance;
    }

    public void onChildrenScaleChanged(float scale) {
        this.mMaxDistanceForFolderCreation = this.mDistanceForFolderCreationRatio * this.mLauncher.getDeviceProfile().iconSizePx * scale;
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.mShortcutsAndWidgets.getChildAt(i);
            childAt.setScaleX(scale);
            childAt.setScaleY(scale);
        }
    }

    public void calculateChildrenScale() {
        float f;
        int i;
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        float f2 = deviceProfile.iconSizePx;
        int i2 = this.mCellWidth;
        if (f2 / i2 > 0.85f) {
            f = i2 * 0.85f;
            i = deviceProfile.iconSizePx;
        } else {
            f = deviceProfile.hotseatIconSizePx;
            i = deviceProfile.iconSizePx;
        }
        float f3 = f / i;
        if (this.mHotseatScale != f3) {
            this.mHotseatScale = f3;
            onChildrenScaleChanged(f3);
        }
    }

    public void enableHotwordServiceIfNeeded() {
        this.mEnableHotwordService = hasOkGoogleWidget();
    }

    public boolean hasOkGoogleWidget() {
        LauncherAppWidgetInfo launcherAppWidgetInfo;
        ShortcutAndWidgetContainer shortcutsAndWidgets = getShortcutsAndWidgets();
        for (int i = 0; i < shortcutsAndWidgets.getChildCount(); i++) {
            View childAt = shortcutsAndWidgets.getChildAt(i);
            if ((childAt instanceof LauncherAppWidgetHostView) && (launcherAppWidgetInfo = (LauncherAppWidgetInfo) childAt.getTag()) != null && launcherAppWidgetInfo.providerName != null && GOOGLE_SEARCH_WIDGET_CLASSNAME.equals(launcherAppWidgetInfo.providerName.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasFullscreenItem() {
        return this.mHasFullscreenItem;
    }

    public void setBackgroundSelected(boolean isDefaultScreen) {
        if (com.lge.launcher3.util.Utilities.isLGUI7_1() || this.mIsBackgroundSelected == isDefaultScreen) {
            return;
        }
        if (isDefaultScreen) {
            this.mBackground.startTransition(120);
        } else {
            this.mBackground.reverseTransition(120);
        }
        this.mIsBackgroundSelected = isDefaultScreen;
        invalidate();
    }

    public LinearLayout getDefaultHomeLayout() {
        return this.mDefaultHomeLayout;
    }

    public int getDefaultHomeLayoutHeight() {
        return this.mDefaultHomeLayoutHeight;
    }

    public void setDefaultHomeSelected(boolean selected) {
        if (com.lge.launcher3.util.Utilities.isLGUI7_1()) {
            if (selected) {
                this.mDefaultHomeBtn.setSelected(true);
                this.mDefaultHomeBtn.setImageResource(R.drawable.btn_homescreen_home_edit_on);
                this.mDefaultHomeSelected = true;
            } else {
                this.mDefaultHomeBtn.setSelected(false);
                this.mDefaultHomeBtn.setImageResource(R.drawable.btn_homescreen_home_edit_off);
                this.mDefaultHomeSelected = false;
            }
        }
    }

    public void setVertical(boolean isVertical) {
        this.mVertical = isVertical;
    }

    public void setMinusOneScreenPreview(View child) {
        this.mMinusOneScreenPreview = child;
    }

    public View getMinusOneScreenPreview() {
        return this.mMinusOneScreenPreview;
    }

    public void superRemoveView(View view) {
        removeView(view);
        super.removeView(view);
    }
}
