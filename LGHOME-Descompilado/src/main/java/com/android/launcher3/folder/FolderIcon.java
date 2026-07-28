package com.android.launcher3.folder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.launcher3.Alarm;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.CheckLongPressHelper;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DropTarget;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.FocusIndicatorView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.OnAlarmListener;
import com.android.launcher3.PreloadIconDrawable;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.SimpleOnStylusPressListener;
import com.android.launcher3.StylusEventHelper;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.views.IconLabelDotView;
import com.lge.launcher3.R;
import com.lge.launcher3.adaptive.AdaptiveTextInterface;
import com.lge.launcher3.allapps.AllAppsFolder;
import com.lge.launcher3.allapps.AllAppsItemInfo;
import com.lge.launcher3.badge.BadgeFolderIcon;
import com.lge.launcher3.badge.appnotifier.AppNotifierData;
import com.lge.launcher3.badge.appnotifier.AppNotifierDrawer;
import com.lge.launcher3.badge.appnotifier.AppNotifierManager;
import com.lge.launcher3.badge.appnotifier.IAppNotifierGroup;
import com.lge.launcher3.folder.FolderColorUtil;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class FolderIcon extends BadgeFolderIcon implements FolderInfo.FolderListener, AdaptiveTextInterface, IAppNotifierGroup, IconLabelDotView {
    private static final int CONSUMPTION_ANIMATION_DURATION = 100;
    private static final int DROP_IN_ANIMATION_DURATION = 400;
    private static final int FINAL_ITEM_ANIMATION_DURATION = 200;
    public static final boolean HAS_OUTER_RING = false;
    private static final int INITIAL_ITEM_ANIMATION_DURATION = 350;
    private static final float INNER_RING_GROWTH_FACTOR = 0.15f;
    public static final int NUM_ITEMS_IN_PREVIEW = 9;
    private static final int ON_OPEN_DELAY = 800;
    private static final float OUTER_RING_GROWTH_FACTOR = 0.3f;
    private static final float PERSPECTIVE_SCALE_FACTOR = 0.35f;
    private static final float PERSPECTIVE_SHIFT_FACTOR = 0.18f;
    public static final boolean SPRING_LOADING_ENABLED = true;
    private static Bitmap mDefaultFolderBitmap = null;
    public static Drawable sSharedFolderLeaveBehind = null;
    static boolean sStaticValuesDirty = true;
    PreviewItemDrawingParams mAnimParams;
    boolean mAnimating;
    private int mAvailableSpaceInPreview;
    private boolean mBackgroundIsVisible;
    private float mBaselineIconScale;
    private int mBaselineIconSize;
    ItemInfo mDragInfo;
    private int mDrawableSize;
    public Folder mFolder;
    BubbleTextView mFolderName;
    FolderRingAnimator mFolderRingAnimator;
    ArrayList<ShortcutInfo> mHiddenItems;
    private FolderInfo mInfo;
    private int mIntrinsicIconSize;
    Launcher mLauncher;
    private boolean mLayoutHorizontal;
    private CheckLongPressHelper mLongPressHelper;
    private float mMaxPerspectiveShift;
    private Rect mOldBounds;
    OnAlarmListener mOnOpenListener;
    private Alarm mOpenAlarm;
    private PreviewItemDrawingParams mParams;
    public ImageView mPreviewBackground;
    private int mPreviewOffsetX;
    private int mPreviewOffsetY;
    private float mSlop;
    private StylusEventHelper mStylusEventHelper;
    private int mTotalSize;

    public void onDragOver(Object dragInfo) {
    }

    @Override // com.android.launcher3.views.IconLabelDotView
    public void setForceHideDot(boolean hide) {
    }

    public FolderIcon(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FolderIcon(Context context) {
        this(context, null, 0);
    }

    public FolderIcon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mFolderRingAnimator = null;
        this.mTotalSize = -1;
        this.mAnimating = false;
        this.mOldBounds = new Rect();
        this.mParams = new PreviewItemDrawingParams(0.0f, 0.0f, 0.0f, 0);
        this.mAnimParams = new PreviewItemDrawingParams(0.0f, 0.0f, 0.0f, 0);
        this.mHiddenItems = new ArrayList<>();
        this.mOpenAlarm = new Alarm();
        this.mBackgroundIsVisible = true;
        this.mOnOpenListener = new OnAlarmListener() { // from class: com.android.launcher3.folder.FolderIcon.1
            @Override // com.android.launcher3.OnAlarmListener
            public void onAlarm(Alarm alarm) {
                ShortcutInfo shortcutInfo;
                if (FolderIcon.this.mDragInfo instanceof AppInfo) {
                    shortcutInfo = new ShortcutInfo((AppInfo) FolderIcon.this.mDragInfo);
                } else {
                    shortcutInfo = (ShortcutInfo) FolderIcon.this.mDragInfo;
                }
                FolderIcon.this.mFolder.beginExternalDrag(shortcutInfo);
                FolderIcon.this.mLauncher.openFolder(FolderIcon.this);
            }
        };
        init();
        this.mLayoutHorizontal = context.obtainStyledAttributes(attrs, R.styleable.FolderIcon, defStyle, 0).getBoolean(0, false);
        if (context instanceof Launcher) {
            DeviceProfile deviceProfile = ((Launcher) context).getDeviceProfile();
            if (!deviceProfile.isPhone || deviceProfile.allowRotation) {
                return;
            }
            this.mLayoutHorizontal = false;
        }
    }

    private void init() {
        this.mLongPressHelper = new CheckLongPressHelper(this);
        this.mStylusEventHelper = new StylusEventHelper(new SimpleOnStylusPressListener(this), this);
        setAccessibilityDelegate(LauncherAppState.getInstance(getContext()).getAccessibilityDelegate());
    }

    public boolean isDropEnabled() {
        return !((Workspace) ((ViewGroup) ((ViewGroup) getParent()).getParent()).getParent()).workspaceInModalState();
    }

    public static FolderIcon fromXml(int resId, Launcher launcher, ViewGroup group, FolderInfo folderInfo, IconCache iconCache) {
        DeviceProfile deviceProfile = launcher.getDeviceProfile();
        FolderIcon folderIcon = (FolderIcon) LayoutInflater.from(launcher).inflate(resId, group, false);
        folderIcon.setClipToPadding(false);
        BubbleTextView bubbleTextView = (BubbleTextView) folderIcon.findViewById(R.id.folder_icon_name);
        folderIcon.mFolderName = bubbleTextView;
        bubbleTextView.setText(folderInfo.title);
        folderIcon.mFolderName.setCompoundDrawablePadding(0);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) folderIcon.mFolderName.getLayoutParams();
        if (folderIcon.isLayoutHorizontal()) {
            if (Utilities.isRtl(launcher.getResources())) {
                layoutParams.rightMargin = deviceProfile.folderIconSizePx;
            } else {
                layoutParams.leftMargin = deviceProfile.folderIconSizePx;
            }
        } else {
            layoutParams.topMargin = deviceProfile.iconSizePx + deviceProfile.iconDrawablePaddingPx;
        }
        ImageView imageView = (ImageView) folderIcon.findViewById(R.id.preview_background);
        folderIcon.mPreviewBackground = imageView;
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) imageView.getLayoutParams();
        if (!folderIcon.isLayoutHorizontal()) {
            layoutParams2.topMargin = deviceProfile.folderBackgroundOffset;
        }
        layoutParams2.width = deviceProfile.folderIconSizePx;
        layoutParams2.height = deviceProfile.folderIconSizePx;
        folderIcon.setTag(folderInfo);
        folderIcon.setOnClickListener(launcher);
        folderIcon.mInfo = folderInfo;
        folderIcon.mLauncher = launcher;
        if (folderInfo.title.length() > 0) {
            folderIcon.setContentDescription(folderInfo.title);
        } else {
            folderIcon.mFolderName.setContentDescription(launcher.getString(R.string.folder_hint_text));
        }
        Folder folderFromXml = Folder.fromXml(launcher);
        folderFromXml.setDragController(launcher.getDragController());
        folderFromXml.setFolderIcon(folderIcon);
        folderFromXml.bind(folderInfo);
        folderIcon.mFolder = folderFromXml;
        folderIcon.mFolderRingAnimator = new FolderRingAnimator(launcher, folderIcon);
        folderInfo.addListener(folderIcon);
        folderIcon.setOnFocusChangeListener(launcher.mFocusHandler);
        setFolderIconColor(folderIcon.getContext(), folderIcon.mPreviewBackground, folderIcon.getFolderInfo().folderColor);
        folderIcon.setAppNotifierDrawer();
        if (!LGHomeFeature.isEnableDefaultHome()) {
            UninstallModeManager.getInstance(launcher).setUninstallTypeForBadgeView(folderIcon);
        }
        return folderIcon;
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        sStaticValuesDirty = true;
        return super.onSaveInstanceState();
    }

    @Override // com.android.launcher3.views.IconLabelDotView
    public void setIconVisible(boolean visible) {
        this.mBackgroundIsVisible = visible;
        this.mPreviewBackground.setVisibility(visible ? 0 : 4);
        invalidate();
    }

    public static class FolderRingAnimator {
        private static ValueAnimator mAcceptAnimator = null;
        private static ValueAnimator mNeutralAnimator = null;
        public static int sPreviewPadding = -1;
        public static int sPreviewSize = -1;
        public static Drawable sSharedInnerRingDrawable;
        public static Drawable sSharedOuterRingDrawable;
        CellLayout mCellLayout;
        public int mCellX;
        public int mCellY;
        private final Context mContext;
        public FolderIcon mFolderIcon;
        public float mInnerRingSize;
        public float mOuterRingSize;

        public FolderRingAnimator(Launcher launcher, FolderIcon folderIcon) {
            this.mFolderIcon = null;
            this.mContext = launcher;
            this.mFolderIcon = folderIcon;
            Resources resources = launcher.getResources();
            if (FolderIcon.sStaticValuesDirty) {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    throw new RuntimeException("FolderRingAnimator loading drawables on non-UI thread " + Thread.currentThread());
                }
                DeviceProfile deviceProfile = launcher.getDeviceProfile();
                sPreviewSize = (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() ? deviceProfile.inv.portraitProfile : deviceProfile).folderIconSizePx;
                sPreviewPadding = (int) (((sPreviewSize - (resources.getDimensionPixelSize(R.dimen.folder_preview_padding) * 2)) * 0.3f) / 2.0f);
                sSharedOuterRingDrawable = resources.getDrawable(R.drawable.portal_ring_outer);
                sSharedInnerRingDrawable = resources.getDrawable(R.drawable.folder_icon_shape);
                FolderIcon.sSharedFolderLeaveBehind = resources.getDrawable(R.drawable.portal_ring_rest);
                FolderIcon.sStaticValuesDirty = false;
            }
        }

        public void animateToAcceptState() {
            ValueAnimator valueAnimator = mNeutralAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator valueAnimatorOfFloat = LauncherAnimUtils.ofFloat(this.mCellLayout, 0.0f, 1.0f);
            mAcceptAnimator = valueAnimatorOfFloat;
            valueAnimatorOfFloat.setDuration(100L);
            final int i = sPreviewSize;
            mAcceptAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.folder.FolderIcon.FolderRingAnimator.1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    float fFloatValue = ((Float) animation.getAnimatedValue()).floatValue();
                    float f = FolderRingAnimator.this.mCellLayout.isHotseat() ? 0.9f : 1.0f;
                    FolderRingAnimator.this.mOuterRingSize = ((0.3f * fFloatValue) + 1.0f) * i * f;
                    FolderRingAnimator.this.mInnerRingSize = ((fFloatValue * FolderIcon.INNER_RING_GROWTH_FACTOR) + 1.0f) * i * f;
                    if (FolderRingAnimator.this.mCellLayout != null) {
                        FolderRingAnimator.this.mCellLayout.invalidate();
                    }
                }
            });
            mAcceptAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.folder.FolderIcon.FolderRingAnimator.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    if (FolderRingAnimator.this.mFolderIcon != null) {
                        FolderRingAnimator.this.mFolderIcon.mPreviewBackground.setVisibility(4);
                    }
                }
            });
            setAnimationBGColor();
            mAcceptAnimator.start();
        }

        public void animateToNaturalState() {
            final int dimension;
            ValueAnimator valueAnimator = mAcceptAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator valueAnimatorOfFloat = LauncherAnimUtils.ofFloat(this.mCellLayout, 0.0f, 1.0f);
            mNeutralAnimator = valueAnimatorOfFloat;
            valueAnimatorOfFloat.setDuration(100L);
            CellLayout cellLayout = this.mCellLayout;
            if (cellLayout != null) {
                dimension = (int) (sPreviewSize - (cellLayout.getResources().getDimension(R.dimen.dynamic_grid_edge_margin) * 2.0f));
            } else {
                dimension = 0;
                LGLog.d("FolderIcon", "mCellLayout is null in animateToNaturalState");
            }
            mNeutralAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.folder.FolderIcon.FolderRingAnimator.3
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    float fFloatValue = 1.0f - ((Float) animation.getAnimatedValue()).floatValue();
                    FolderRingAnimator.this.mOuterRingSize = ((0.3f * fFloatValue) + 1.0f) * dimension;
                    FolderRingAnimator.this.mInnerRingSize = ((fFloatValue * FolderIcon.INNER_RING_GROWTH_FACTOR) + 1.0f) * dimension;
                    if (FolderRingAnimator.this.mCellLayout != null) {
                        FolderRingAnimator.this.mCellLayout.invalidate();
                    }
                }
            });
            mNeutralAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.folder.FolderIcon.FolderRingAnimator.4
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (FolderRingAnimator.this.mCellLayout != null) {
                        FolderRingAnimator.this.mCellLayout.hideFolderAccept(FolderRingAnimator.this);
                    }
                    if (FolderRingAnimator.this.mFolderIcon != null) {
                        FolderRingAnimator.this.mFolderIcon.mPreviewBackground.setVisibility(0);
                    }
                }
            });
            mNeutralAnimator.start();
        }

        public void getCell(int[] loc) {
            loc[0] = this.mCellX;
            loc[1] = this.mCellY;
        }

        public void setCell(int x, int y) {
            this.mCellX = x;
            this.mCellY = y;
        }

        public void setCellLayout(CellLayout layout) {
            this.mCellLayout = layout;
        }

        public float getOuterRingSize() {
            return this.mOuterRingSize;
        }

        public float getInnerRingSize() {
            return this.mInnerRingSize;
        }

        public void setAnimationBGColor() {
            FolderIcon folderIcon = this.mFolderIcon;
            if (folderIcon != null) {
                ((GradientDrawable) sSharedInnerRingDrawable).setColor(FolderColorUtil.getFolderBGColor(this.mContext, folderIcon.getColor()));
            } else {
                ((GradientDrawable) sSharedInnerRingDrawable).setColor(FolderColorUtil.getFolderBGColor(this.mContext, this.mContext.getResources().getInteger(R.integer.default_folder_color_index)));
            }
        }
    }

    public Folder getFolder() {
        return this.mFolder;
    }

    public FolderInfo getFolderInfo() {
        return this.mInfo;
    }

    private boolean willAcceptItem(ItemInfo item) {
        FolderInfo folderInfo;
        int i = item.itemType;
        return ((i != 0 && i != 1 && i != 6) || this.mFolder.isFull() || item == (folderInfo = this.mInfo) || folderInfo.opened) ? false : true;
    }

    public boolean acceptDrop(Object dragInfo) {
        return !this.mFolder.isDestroyed() && willAcceptItem((ItemInfo) dragInfo);
    }

    public void addItem(ShortcutInfo item) {
        this.mInfo.add(item);
    }

    public void onDragEnter(Object dragInfo) {
        if (this.mFolder.isDestroyed()) {
            return;
        }
        ItemInfo itemInfo = (ItemInfo) dragInfo;
        if (willAcceptItem(itemInfo)) {
            CellLayout.LayoutParams layoutParams = (CellLayout.LayoutParams) getLayoutParams();
            CellLayout cellLayout = (CellLayout) getParent().getParent();
            this.mFolderRingAnimator.setCell(layoutParams.cellX, layoutParams.cellY);
            this.mFolderRingAnimator.setCellLayout(cellLayout);
            this.mFolderRingAnimator.animateToAcceptState();
            cellLayout.showFolderAccept(this.mFolderRingAnimator);
            this.mOpenAlarm.setOnAlarmListener(this.mOnOpenListener);
            if ((dragInfo instanceof AppInfo) || (dragInfo instanceof ShortcutInfo)) {
                this.mOpenAlarm.setAlarm(800L);
            }
            this.mDragInfo = itemInfo;
        }
    }

    public void performCreateAnimation(final ShortcutInfo destInfo, final View destView, final ShortcutInfo srcInfo, final DragView srcView, Rect dstRect, float scaleRelativeToDragLayer, Runnable postAnimationRunnable) {
        Drawable topDrawable = getTopDrawable((TextView) destView);
        computePreviewDrawingParams(topDrawable.getIntrinsicWidth(), destView.getMeasuredWidth());
        animateFirstItem(topDrawable, 350, false, null);
        if (!(destView.getTag() instanceof AllAppsItemInfo)) {
            addItem(destInfo);
        }
        onDrop(srcInfo, srcView, dstRect, scaleRelativeToDragLayer, 1, postAnimationRunnable, null);
    }

    public void performDestroyAnimation(final View finalView, Runnable onCompleteRunnable) {
        Drawable topDrawable = getTopDrawable((TextView) finalView);
        computePreviewDrawingParams(topDrawable.getIntrinsicWidth(), finalView.getMeasuredWidth());
        animateFirstItem(topDrawable, 200, true, onCompleteRunnable);
        AppNotifierManager.getInstance(this.mContext).unregisterAppNotifierGroup(this);
    }

    public void onDragExit(Object dragInfo) {
        onDragExit();
    }

    public void onDragExit() {
        this.mFolderRingAnimator.animateToNaturalState();
        this.mOpenAlarm.cancelAlarm();
    }

    private void onDrop(final ShortcutInfo item, DragView animateView, Rect finalRect, float scaleRelativeToDragLayer, int index, Runnable postAnimationRunnable, DropTarget.DragObject d) {
        Rect rect;
        float descendantRectRelativeToSelf;
        item.cellX = -1;
        item.cellY = -1;
        if (animateView != null) {
            DragLayer dragLayer = this.mLauncher.getDragLayer();
            Rect rect2 = new Rect();
            dragLayer.getViewRectRelativeToSelf(animateView, rect2);
            if (finalRect == null) {
                rect = new Rect();
                Workspace workspace = this.mLauncher.getWorkspace();
                workspace.setFinalTransitionTransform((CellLayout) getParent().getParent());
                float scaleX = getScaleX();
                float scaleY = getScaleY();
                setScaleX(1.0f);
                setScaleY(1.0f);
                descendantRectRelativeToSelf = dragLayer.getDescendantRectRelativeToSelf(this, rect);
                setScaleX(scaleX);
                setScaleY(scaleY);
                workspace.resetTransitionTransform((CellLayout) getParent().getParent());
            } else {
                rect = finalRect;
                descendantRectRelativeToSelf = scaleRelativeToDragLayer;
            }
            int[] iArr = new int[2];
            float localCenterForIndex = getLocalCenterForIndex(index, iArr);
            iArr[0] = Math.round(iArr[0] * descendantRectRelativeToSelf);
            iArr[1] = Math.round(iArr[1] * descendantRectRelativeToSelf);
            rect.offset(iArr[0] - (animateView.getMeasuredWidth() / 2), iArr[1] - (animateView.getMeasuredHeight() / 2));
            float f = descendantRectRelativeToSelf * localCenterForIndex;
            dragLayer.animateView(animateView, rect2, rect, index < 9 ? 0.5f : 0.0f, 1.0f, 1.0f, f, f, DROP_IN_ANIMATION_DURATION, new DecelerateInterpolator(2.0f), new AccelerateInterpolator(2.0f), postAnimationRunnable, 0, null);
            addItem(item);
            this.mHiddenItems.add(item);
            this.mFolder.hideItem(item);
            postDelayed(new Runnable() { // from class: com.android.launcher3.folder.FolderIcon.2
                @Override // java.lang.Runnable
                public void run() {
                    FolderIcon.this.mHiddenItems.remove(item);
                    FolderIcon.this.mFolder.showItem(item);
                    FolderIcon.this.invalidate();
                }
            }, 400L);
            return;
        }
        addItem(item);
    }

    public void onDrop(DropTarget.DragObject d) {
        ShortcutInfo shortcutInfo;
        if (d.dragInfo instanceof AppInfo) {
            shortcutInfo = ((AppInfo) d.dragInfo).makeShortcut();
        } else if ((d.dragSource instanceof AllAppsFolder) && !(this.mFolder instanceof AllAppsFolder)) {
            shortcutInfo = new ShortcutInfo((ShortcutInfo) d.dragInfo);
            shortcutInfo.container = -1L;
        } else {
            shortcutInfo = (ShortcutInfo) d.dragInfo;
        }
        ShortcutInfo shortcutInfo2 = shortcutInfo;
        this.mFolder.notifyDrop();
        onDrop(shortcutInfo2, d.dragView, null, 1.0f, this.mInfo.contents.size(), d.postAnimationRunnable, d);
    }

    private void computePreviewDrawingParams(int drawableSize, int totalSize) {
        if (this.mDrawableSize == drawableSize && this.mTotalSize == totalSize) {
            return;
        }
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        this.mDrawableSize = drawableSize;
        this.mTotalSize = totalSize;
        this.mIntrinsicIconSize = drawableSize;
        int i = this.mPreviewBackground.getLayoutParams().height;
        int i2 = (int) ((i * 0.3f) / 2.0f);
        int i3 = i - (i2 * 2);
        this.mAvailableSpaceInPreview = i3;
        int i4 = this.mIntrinsicIconSize;
        float f = (i3 * 1.0f) / i4;
        this.mBaselineIconScale = f;
        int i5 = (int) (i4 * f);
        this.mBaselineIconSize = i5;
        this.mMaxPerspectiveShift = i5 * PERSPECTIVE_SHIFT_FACTOR;
        if (this.mLayoutHorizontal) {
            this.mPreviewOffsetX = this.mPreviewBackground.getLeft() + i2;
            this.mPreviewOffsetY = (this.mPreviewBackground.getTop() + i2) - getPaddingTop();
        } else {
            this.mPreviewOffsetX = (this.mTotalSize - i3) / 2;
            this.mPreviewOffsetY = i2 + deviceProfile.folderBackgroundOffset;
        }
    }

    private void computePreviewDrawingParams(Drawable d) {
        if (this.mLayoutHorizontal) {
            computePreviewDrawingParams(d.getIntrinsicHeight(), getMeasuredHeight());
        } else {
            computePreviewDrawingParams(d.getIntrinsicWidth(), getMeasuredWidth());
        }
    }

    class PreviewItemDrawingParams {
        Drawable drawable;
        int overlayAlpha;
        float scale;
        float transX;
        float transY;

        PreviewItemDrawingParams(float transX, float transY, float scale, int overlayAlpha) {
            this.transX = transX;
            this.transY = transY;
            this.scale = scale;
            this.overlayAlpha = overlayAlpha;
        }
    }

    private float getLocalCenterForIndex(int index, int[] center) {
        PreviewItemDrawingParams previewItemDrawingParamsComputePreviewItemDrawingParams = computePreviewItemDrawingParams(Math.min(9, index), this.mParams);
        this.mParams = previewItemDrawingParamsComputePreviewItemDrawingParams;
        previewItemDrawingParamsComputePreviewItemDrawingParams.transX += this.mPreviewOffsetX;
        this.mParams.transY += this.mPreviewOffsetY;
        float f = this.mParams.transX + ((this.mParams.scale * this.mIntrinsicIconSize) / 3.0f);
        float f2 = this.mParams.transY + ((this.mParams.scale * this.mIntrinsicIconSize) / 3.0f);
        center[0] = Math.round(f);
        center[1] = Math.round(f2);
        return this.mParams.scale;
    }

    private PreviewItemDrawingParams computePreviewItemDrawingParams(int index, PreviewItemDrawingParams params) {
        return computePreviewItemDrawingParams(index, params, null);
    }

    private PreviewItemDrawingParams computePreviewItemDrawingParams(int index, PreviewItemDrawingParams params, Rect externalBound) {
        return reComputePreviewItemDrawingParams(index, params, externalBound);
    }

    private void drawPreviewItem(Canvas canvas, PreviewItemDrawingParams params, boolean isExternal) {
        canvas.save();
        if (isExternal) {
            canvas.translate(params.transX, params.transY);
        } else {
            canvas.translate(params.transX + this.mPreviewOffsetX, params.transY + this.mPreviewOffsetY);
        }
        canvas.scale(params.scale, params.scale);
        Drawable drawable = params.drawable;
        if (drawable != null) {
            this.mOldBounds.set(drawable.getBounds());
            int i = this.mIntrinsicIconSize;
            drawable.setBounds(0, 0, i, i);
            if (drawable instanceof FastBitmapDrawable) {
                FastBitmapDrawable fastBitmapDrawable = (FastBitmapDrawable) drawable;
                float brightness = fastBitmapDrawable.getBrightness();
                fastBitmapDrawable.setBrightness(params.overlayAlpha);
                drawable.draw(canvas);
                fastBitmapDrawable.setBrightness(brightness);
            } else {
                drawable.setColorFilter(Color.argb(params.overlayAlpha, 255, 255, 255), PorterDuff.Mode.SRC_ATOP);
                drawable.draw(canvas);
                drawable.clearColorFilter();
            }
            drawable.setBounds(this.mOldBounds);
        }
        canvas.restore();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        Folder folder;
        super.dispatchDraw(canvas);
        if (this.mBackgroundIsVisible && (folder = this.mFolder) != null) {
            if (folder.getItemCount() != 0 || this.mAnimating) {
                ArrayList<View> itemsInReadingOrder = this.mFolder.getItemsInReadingOrder();
                if (this.mAnimating) {
                    computePreviewDrawingParams(this.mAnimParams.drawable);
                } else {
                    computePreviewDrawingParams(getTopDrawable((TextView) itemsInReadingOrder.get(0)));
                }
                int iMin = Math.min(itemsInReadingOrder.size(), 9);
                if (this.mAnimating) {
                    drawPreviewItem(canvas, this.mAnimParams, false);
                } else {
                    for (int i = iMin - 1; i >= 0; i--) {
                        TextView textView = (TextView) itemsInReadingOrder.get(i);
                        if (!this.mHiddenItems.contains(textView.getTag())) {
                            Drawable topDrawable = getTopDrawable(textView);
                            PreviewItemDrawingParams previewItemDrawingParamsComputePreviewItemDrawingParams = computePreviewItemDrawingParams(i, this.mParams);
                            this.mParams = previewItemDrawingParamsComputePreviewItemDrawingParams;
                            previewItemDrawingParamsComputePreviewItemDrawingParams.drawable = topDrawable;
                            drawPreviewItem(canvas, this.mParams, false);
                        }
                    }
                }
                drawBadge(canvas, this.mPreviewBackground);
            }
        }
    }

    private Drawable getTopDrawable(TextView v) {
        if (v == null) {
            return null;
        }
        Drawable[] compoundDrawablesRelative = v.getCompoundDrawablesRelative();
        for (int i = 0; i < compoundDrawablesRelative.length; i++) {
            if (compoundDrawablesRelative[i] != null) {
                return compoundDrawablesRelative[i] instanceof PreloadIconDrawable ? ((PreloadIconDrawable) compoundDrawablesRelative[i]).mIcon : compoundDrawablesRelative[i];
            }
        }
        return null;
    }

    private void animateFirstItem(final Drawable d, int duration, final boolean reverse, final Runnable onCompleteRunnable) {
        final PreviewItemDrawingParams previewItemDrawingParamsComputePreviewItemDrawingParams = computePreviewItemDrawingParams(0, null);
        float f = this.mLauncher.getDeviceProfile().iconSizePx;
        final float intrinsicWidth = f / d.getIntrinsicWidth();
        int i = this.mAvailableSpaceInPreview;
        final float f2 = (i - f) / 2.0f;
        final float paddingTop = ((i - f) / 2.0f) + getPaddingTop();
        this.mAnimParams.drawable = d;
        ValueAnimator valueAnimatorOfFloat = LauncherAnimUtils.ofFloat(this, 0.0f, 1.0f);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.folder.FolderIcon.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                float fFloatValue = ((Float) animation.getAnimatedValue()).floatValue();
                if (reverse) {
                    fFloatValue = 1.0f - fFloatValue;
                    FolderIcon.this.mPreviewBackground.setAlpha(fFloatValue);
                }
                FolderIcon.this.mAnimParams.transX = f2 + ((previewItemDrawingParamsComputePreviewItemDrawingParams.transX - f2) * fFloatValue);
                FolderIcon.this.mAnimParams.transY = paddingTop + ((previewItemDrawingParamsComputePreviewItemDrawingParams.transY - paddingTop) * fFloatValue);
                FolderIcon.this.mAnimParams.scale = intrinsicWidth + (fFloatValue * (previewItemDrawingParamsComputePreviewItemDrawingParams.scale - intrinsicWidth));
                FolderIcon.this.invalidate();
            }
        });
        valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.folder.FolderIcon.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                FolderIcon.this.mAnimating = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                FolderIcon.this.mAnimating = false;
                Runnable runnable = onCompleteRunnable;
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
        valueAnimatorOfFloat.setDuration(duration);
        valueAnimatorOfFloat.start();
    }

    public void setTextVisible(boolean hide) {
        if (hide) {
            this.mFolderName.setVisibility(0);
        } else {
            this.mFolderName.setVisibility(4);
        }
    }

    public boolean getTextVisible() {
        return this.mFolderName.getVisibility() == 0;
    }

    @Override // com.android.launcher3.model.data.FolderInfo.FolderListener
    public void onItemsChanged() {
        invalidate();
        requestLayout();
    }

    @Override // com.android.launcher3.model.data.FolderInfo.FolderListener
    public void onAdd(ShortcutInfo item) {
        setAppNotifierDrawer();
        invalidate();
        requestLayout();
    }

    @Override // com.android.launcher3.model.data.FolderInfo.FolderListener
    public void onRemove(ShortcutInfo item) {
        setAppNotifierDrawer();
        invalidate();
        requestLayout();
    }

    @Override // com.android.launcher3.model.data.FolderInfo.FolderListener
    public void onAdd(List<ShortcutInfo> items) {
        setAppNotifierDrawer();
        invalidate();
        requestLayout();
    }

    @Override // com.android.launcher3.model.data.FolderInfo.FolderListener
    public void onRemove(List<ShortcutInfo> items) {
        setAppNotifierDrawer();
        invalidate();
        requestLayout();
    }

    @Override // com.android.launcher3.model.data.FolderInfo.FolderListener
    public void onTitleChanged(CharSequence title) {
        this.mFolderName.setText(title);
        if (title.length() > 0) {
            setContentDescription(((Object) title) + getContext().getString(R.string.folder_name));
            return;
        }
        setContentDescription(getContext().getString(R.string.folder_hint_text));
        this.mFolderName.setContentDescription(getContext().getString(R.string.folder_hint_text));
    }

    /* JADX WARN: Removed duplicated region for block: B:17:0x0038  */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onTouchEvent(android.view.MotionEvent r4) {
        /*
            r3 = this;
            boolean r0 = super.onTouchEvent(r4)
            com.android.launcher3.StylusEventHelper r1 = r3.mStylusEventHelper
            boolean r1 = r1.onMotionEvent(r4)
            r2 = 1
            if (r1 == 0) goto L13
            com.android.launcher3.CheckLongPressHelper r4 = r3.mLongPressHelper
            r4.cancelLongPress()
            return r2
        L13:
            int r1 = r4.getAction()
            if (r1 == 0) goto L3e
            if (r1 == r2) goto L38
            r2 = 2
            if (r1 == r2) goto L22
            r4 = 3
            if (r1 == r4) goto L38
            goto L43
        L22:
            float r1 = r4.getX()
            float r4 = r4.getY()
            float r2 = r3.mSlop
            boolean r4 = com.android.launcher3.Utilities.pointInView(r3, r1, r4, r2)
            if (r4 != 0) goto L43
            com.android.launcher3.CheckLongPressHelper r4 = r3.mLongPressHelper
            r4.cancelLongPress()
            goto L43
        L38:
            com.android.launcher3.CheckLongPressHelper r4 = r3.mLongPressHelper
            r4.cancelLongPress()
            goto L43
        L3e:
            com.android.launcher3.CheckLongPressHelper r4 = r3.mLongPressHelper
            r4.postCheckForLongPress()
        L43:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.folder.FolderIcon.onTouchEvent(android.view.MotionEvent):boolean");
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        setAppNotifierDrawer();
    }

    @Override // android.view.View
    public void cancelLongPress() {
        super.cancelLongPress();
        this.mLongPressHelper.cancelLongPress();
    }

    @Override // com.android.launcher3.model.data.FolderInfo.FolderListener
    public void onColorChanged() {
        setFolderIconColor(getContext(), this.mPreviewBackground, this.mInfo.folderColor);
    }

    public static void setFolderIconColor(Context context, ImageView folderIconBG, int colorIndex) {
        if (Utilities.isAtLeastO() && !DDTUtils.isAdditionalThemeApplied(context) && !DDTUtils.isAdditionalIconThemeApplied(context)) {
            setFolderIconColorAndShape(context, folderIconBG, colorIndex);
            return;
        }
        Bitmap folderIconMask = FolderColorUtil.getFolderIconMask(context);
        if (!DDTUtils.isAdditionalThemeApplied(context) && !DDTUtils.isAdditionalIconThemeApplied(context)) {
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(folderIconMask.getWidth(), folderIconMask.getHeight(), Bitmap.Config.ARGB_8888);
            bitmapCreateBitmap.eraseColor(FolderColorUtil.getFolderBGColor(context, colorIndex));
            Canvas canvas = new Canvas(bitmapCreateBitmap);
            Paint paint = new Paint(1);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            canvas.drawBitmap(folderIconMask, 0.0f, 0.0f, paint);
            paint.setXfermode(null);
            folderIconMask = bitmapCreateBitmap;
        }
        folderIconBG.setImageBitmap(folderIconMask);
        folderIconBG.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    public static void setFolderIconColorAndShape(Context context, ImageView folderIconBG, int colorIndex) {
        if (mDefaultFolderBitmap == null && colorIndex == 0) {
            mDefaultFolderBitmap = LauncherIcons.createIconBitmap(IconCache.getShadowIconIfNeeded(context, DDTUtils.convertToCushionIcon(context, changeFolderIconColor(context, 0), "com.lge.launcher3", R.drawable.bg_homescreen_foldericon_01), (Boolean) true), context, 1.0f);
        }
        Bitmap bitmapCreateIconBitmap = mDefaultFolderBitmap;
        if (colorIndex != 0) {
            bitmapCreateIconBitmap = LauncherIcons.createIconBitmap(IconCache.getShadowIconIfNeeded(context, DDTUtils.convertToCushionIcon(context, changeFolderIconColor(context, colorIndex), "com.lge.launcher3", R.drawable.bg_homescreen_foldericon_01), (Boolean) true), context, 1.0f);
        }
        folderIconBG.setImageBitmap(bitmapCreateIconBitmap);
    }

    private static Drawable changeFolderIconColor(Context context, int colorIndex) {
        int folderBGColor = FolderColorUtil.getFolderBGColor(context, colorIndex);
        AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) context.getDrawable(R.mipmap.lg_iconframe_folder_icon).mutate();
        adaptiveIconDrawable.getBackground().setTint(folderBGColor);
        return adaptiveIconDrawable;
    }

    public BubbleTextView getFolderName() {
        return this.mFolderName;
    }

    public int getColor() {
        return this.mInfo.folderColor;
    }

    /* JADX WARN: Removed duplicated region for block: B:31:0x0096  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00a1 A[PHI: r1
      0x00a1: PHI (r1v12 float) = (r1v11 float), (r1v14 float) binds: [B:30:0x0094, B:32:0x009a] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:36:0x00a4  */
    /* JADX WARN: Removed duplicated region for block: B:37:0x00ad  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.android.launcher3.folder.FolderIcon.PreviewItemDrawingParams reComputePreviewItemDrawingParams(int r13, com.android.launcher3.folder.FolderIcon.PreviewItemDrawingParams r14, android.graphics.Rect r15) {
        /*
            r12 = this;
            android.content.res.Resources r0 = r12.getResources()
            r1 = 2131361844(0x7f0a0034, float:1.8343452E38)
            int r0 = r0.getInteger(r1)
            android.content.res.Resources r1 = r12.getResources()
            r2 = 2131361845(0x7f0a0035, float:1.8343454E38)
            int r1 = r1.getInteger(r2)
            r2 = 2131165734(0x7f070226, float:1.7945693E38)
            r3 = 2
            if (r0 != r1) goto L26
            android.content.res.Resources r0 = r12.getResources()
            int r0 = r0.getDimensionPixelOffset(r2)
            int r0 = r0 * r3
            goto L2e
        L26:
            android.content.res.Resources r0 = r12.getResources()
            int r0 = r0.getDimensionPixelOffset(r2)
        L2e:
            int r1 = r12.mAvailableSpaceInPreview
            int r1 = r1 - r0
            r2 = 3
            int r1 = r1 / r2
            float r1 = (float) r1
            if (r15 == 0) goto L3b
            float r4 = r12.getScaleX()
            float r1 = r1 * r4
        L3b:
            int r4 = r12.mIntrinsicIconSize
            float r4 = (float) r4
            float r9 = r1 / r4
            r4 = 1056964608(0x3f000000, float:0.5)
            if (r15 != 0) goto L49
            int r5 = r12.mAvailableSpaceInPreview
            int r5 = r5 / r3
            float r5 = (float) r5
            goto L54
        L49:
            int r5 = r15.width()
            float r5 = (float) r5
            float r5 = r5 * r4
            float r6 = r12.getScaleX()
            float r5 = r5 * r6
        L54:
            if (r15 != 0) goto L5b
            int r4 = r12.mAvailableSpaceInPreview
            int r4 = r4 / r3
            float r4 = (float) r4
            goto L66
        L5b:
            int r6 = r15.height()
            float r6 = (float) r6
            float r6 = r6 * r4
            float r4 = r12.getScaleX()
            float r4 = r4 * r6
        L66:
            r6 = 0
            if (r15 != 0) goto L6e
            int r15 = r12.getPaddingTop()
            goto L6f
        L6e:
            r15 = r6
        L6f:
            float r7 = r5 - r1
            r8 = 1073741824(0x40000000, float:2.0)
            float r8 = r1 / r8
            float r7 = r7 - r8
            float r0 = (float) r0
            float r7 = r7 - r0
            float r1 = r4 - r1
            float r15 = (float) r15
            float r1 = r1 + r15
            float r1 = r1 - r8
            float r1 = r1 - r0
            int r10 = r13 % 3
            r11 = 1
            if (r10 != r11) goto L86
            float r5 = r5 - r8
        L84:
            r7 = r5
            goto L94
        L86:
            boolean r11 = com.lge.launcher3.util.TextUtils.isRToLLanguage()
            if (r11 == 0) goto L8f
            if (r10 != 0) goto L94
            goto L91
        L8f:
            if (r10 != r3) goto L94
        L91:
            float r5 = r5 + r8
            float r5 = r5 + r0
            goto L84
        L94:
            if (r13 < r2) goto La1
            float r1 = r4 - r8
            float r1 = r1 + r15
            r2 = 6
            if (r13 < r2) goto La1
            float r4 = r4 + r8
            float r4 = r4 + r0
            float r4 = r4 + r15
            r8 = r4
            goto La2
        La1:
            r8 = r1
        La2:
            if (r14 != 0) goto Lad
            com.android.launcher3.folder.FolderIcon$PreviewItemDrawingParams r14 = new com.android.launcher3.folder.FolderIcon$PreviewItemDrawingParams
            r10 = 0
            r5 = r14
            r6 = r12
            r5.<init>(r7, r8, r9, r10)
            goto Lb5
        Lad:
            r14.transX = r7
            r14.transY = r8
            r14.scale = r9
            r14.overlayAlpha = r6
        Lb5:
            return r14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.folder.FolderIcon.reComputePreviewItemDrawingParams(int, com.android.launcher3.folder.FolderIcon$PreviewItemDrawingParams, android.graphics.Rect):com.android.launcher3.folder.FolderIcon$PreviewItemDrawingParams");
    }

    @Override // android.view.View
    public String toString() {
        return "FolderIcon {" + ((Object) this.mFolderName.getText()) + "}";
    }

    @Override // com.lge.launcher3.adaptive.AdaptiveTextInterface
    public void setAdapiveTextColor(int color) {
        this.mFolderName.setTextColor(color);
    }

    public void setAppNotifierDrawer() {
        this.mAppNotifierDrawer = registerAppNotifier(this, getAppNotifierDatas(this.mInfo));
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindowInternal();
        Folder folder = this.mFolder;
        if (folder != null && folder.mContent != null) {
            this.mFolder.mContent.unregisterAppNotifier();
        }
        AppNotifierManager.getInstance(getContext()).unregisterAppNotifierGroup(this);
    }

    @Override // com.lge.launcher3.badge.BadgeFolderIcon, com.lge.launcher3.badge.appnotifier.IAppNotifierGroup
    public AppNotifierDrawer registerAppNotifier(IAppNotifierGroup view, ArrayList<AppNotifierData> components) {
        return super.registerAppNotifier(view, components);
    }

    @Override // com.lge.launcher3.badge.appnotifier.IAppNotifierGroup
    public void onUpdateAppNotifier(int count) {
        LGLog.i("FolderIcon", "Update: [" + count + "] " + getTag());
        super.onUpdateAppNotifier(count, this.mFolderName);
    }

    public static FolderIcon fromXml(int resId, Launcher launcher, ViewGroup group, FolderInfo folderInfo, IconCache iconCache, Folder folder, FocusIndicatorView focusIndicatorView) {
        DeviceProfile deviceProfile = launcher.getDeviceProfile();
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && !deviceProfile.isMultiWindowMode) {
            deviceProfile = deviceProfile.inv.portraitProfile;
        }
        FolderIcon folderIcon = (FolderIcon) LayoutInflater.from(launcher).inflate(resId, group, false);
        folderIcon.setClipToPadding(false);
        BubbleTextView bubbleTextView = (BubbleTextView) folderIcon.findViewById(R.id.folder_icon_name);
        folderIcon.mFolderName = bubbleTextView;
        bubbleTextView.setText(folderInfo.title);
        folderIcon.mFolderName.setCompoundDrawablePadding(0);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) folderIcon.mFolderName.getLayoutParams();
        if (folderIcon.isLayoutHorizontal()) {
            if (Utilities.isRtl(launcher.getResources())) {
                layoutParams.rightMargin = deviceProfile.iconSizePx;
            } else {
                layoutParams.leftMargin = deviceProfile.iconSizePx;
            }
        } else {
            layoutParams.topMargin = deviceProfile.iconSizePx + deviceProfile.folderBackgroundOffset;
        }
        ImageView imageView = (ImageView) folderIcon.findViewById(R.id.preview_background);
        folderIcon.mPreviewBackground = imageView;
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) imageView.getLayoutParams();
        if (!folderIcon.isLayoutHorizontal()) {
            layoutParams2.topMargin = deviceProfile.folderBackgroundOffset;
        }
        layoutParams2.width = deviceProfile.folderIconSizePx;
        layoutParams2.height = deviceProfile.folderIconSizePx;
        folderIcon.setTag(folderInfo);
        folderIcon.setOnClickListener(launcher);
        folderIcon.mInfo = folderInfo;
        folderIcon.mLauncher = launcher;
        if (folderInfo.title.length() > 0) {
            folderIcon.setContentDescription(folderInfo.title);
        } else {
            folderIcon.mFolderName.setContentDescription(launcher.getString(R.string.folder_hint_text));
        }
        folder.setDragController(launcher.getDragController());
        folder.setFolderIcon(folderIcon);
        folder.bind(folderInfo);
        folderIcon.mFolder = folder;
        folderIcon.mFolderRingAnimator = new FolderRingAnimator(launcher, folderIcon);
        folderInfo.addListener(folderIcon);
        folderIcon.setOnFocusChangeListener(focusIndicatorView);
        setFolderIconColor(folderIcon.getContext(), folderIcon.mPreviewBackground, folderIcon.getFolderInfo().folderColor);
        folderIcon.setAppNotifierDrawer();
        return folderIcon;
    }

    public static void clearFolderCache() {
        mDefaultFolderBitmap = null;
    }

    public void getPreviewBounds(Rect outBounds) {
        if (this.mPreviewBackground != null) {
            int width = (int) (r0.getWidth() * getScaleX());
            int height = (int) (this.mPreviewBackground.getHeight() * getScaleX());
            int left = this.mPreviewBackground.getLeft() + ((int) ((this.mPreviewBackground.getWidth() - width) * 0.5f));
            int top = this.mPreviewBackground.getTop() + ((int) ((this.mPreviewBackground.getHeight() - height) * 0.5f));
            outBounds.set(left, top, width + left, height + top);
        }
    }

    public void drawPreviewFromExternal(Canvas canvas) {
        ArrayList<View> itemsInReadingOrder = this.mFolder.getItemsInReadingOrder();
        int iMin = Math.min(itemsInReadingOrder.size(), 9);
        computePreviewDrawingParams(getTopDrawable((TextView) itemsInReadingOrder.get(0)));
        for (int i = iMin - 1; i >= 0; i--) {
            TextView textView = (TextView) itemsInReadingOrder.get(i);
            if (!this.mHiddenItems.contains(textView.getTag())) {
                Drawable topDrawable = getTopDrawable(textView);
                PreviewItemDrawingParams previewItemDrawingParamsComputePreviewItemDrawingParams = computePreviewItemDrawingParams(i, this.mParams, topDrawable.getBounds());
                this.mParams = previewItemDrawingParamsComputePreviewItemDrawingParams;
                previewItemDrawingParamsComputePreviewItemDrawingParams.drawable = topDrawable;
                drawPreviewItem(canvas, this.mParams, true);
            }
        }
    }

    public boolean isLayoutHorizontal() {
        return this.mLayoutHorizontal;
    }
}
