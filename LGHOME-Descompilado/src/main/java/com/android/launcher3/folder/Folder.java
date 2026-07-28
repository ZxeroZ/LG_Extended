package com.android.launcher3.folder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Selection;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Alarm;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherState;
import com.android.launcher3.LogDecelerateInterpolator;
import com.android.launcher3.OnAlarmListener;
import com.android.launcher3.PageIndicator;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.UninstallDropTarget;
import com.android.launcher3.Workspace;
import com.android.launcher3.accessibility.AccessibleDragListenerAdapter;
import com.android.launcher3.accessibility.LauncherAccessibilityDelegate;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.logging.LoggerUtils;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.util.UiThreadCircularReveal;
import com.android.launcher3.views.BaseDragLayer;
import com.android.quickstep.SysUINavigationMode;
import com.lge.launcher3.R;
import com.lge.launcher3.allapps.AllAppsFolder;
import com.lge.launcher3.badge.uninstall.IUninstallBadgeView;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.folder.FolderColorPickerDialog;
import com.lge.launcher3.folder.FolderColorUtil;
import com.lge.launcher3.folder.FolderStateTransitionWatcher;
import com.lge.launcher3.folderplus.FolderPlusActivity;
import com.lge.launcher3.memory.MemoryUtils;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.IMEUtils;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.ManagedProfileUtils;
import com.lge.launcher3.util.TalkBackUtils;
import com.lge.launcher3.util.Utilities;
import com.lge.launcher3.util.WindowUtils;
import com.lge.launcher3.views.WorkGuideView;
import com.lge.launcher3.wallpaperblur.HomescreenBlurManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class Folder extends AbstractFloatingView implements DragSource, View.OnClickListener, View.OnLongClickListener, DropTarget, FolderInfo.FolderListener, TextView.OnEditorActionListener, View.OnFocusChangeListener, DragController.DragListener, UninstallDropTarget.UninstallSource, LauncherAccessibilityDelegate.AccessibilityDragSource {
    private static final boolean DEBUG_DATA_MODEL = false;
    public static final boolean FOLDER_DEBUG = false;
    private static final int FOLDER_NAME_ANIMATION_DURATION = 633;
    private static final float ICON_OVERSCROLL_WIDTH_FACTOR = 0.45f;
    private static final int MIN_CONTENT_DIMEN = 5;
    private static final int ON_EXIT_CLOSE_DELAY = 400;
    private static final int REORDER_DELAY = 250;
    public static final int SCROLL_HINT_DURATION = 500;
    public static final int STATE_ANIMATING = 1;
    public static final int STATE_NONE = -1;
    public static final int STATE_OPEN = 2;
    public static final int STATE_SMALL = 0;
    private static final String TAG = "Launcher.Folder";
    private static final int WAITDELAY_CREATION_WORKFOLDER = 1200;
    private static String sDefaultFolderName;
    private static String sHintText;
    private ActionMode.Callback mActionModeCallback;
    private View mBackground;
    private View mButtonLayer;
    private int mButtonLayerHeight;
    private int mButtonLayerWidth;
    private View mColor;
    private ImageView mColorButton;
    private View mColorButtonBlue;
    private ImageView mColorButtonDelete;
    private View mColorButtonMore;
    private View mColorButtonPink;
    private View mColorButtonSky;
    private ImageView mColorButtonStroke;
    private View mColorButtonWhite;
    private View mColorButtonYellow;
    private View mColorPalette;
    private int mColorPaletteHeight;
    public FolderPagedView mContent;
    View mContentWrapper;
    protected ShortcutInfo mCurrentDragInfo;
    protected View mCurrentDragView;
    int mCurrentScrollDir;
    private DataModel mDataModel;
    protected boolean mDeferDropAfterUninstall;
    protected Runnable mDeferredAction;
    protected boolean mDeleteFolderOnDropCompleted;
    protected boolean mDestroyed;
    protected DragController mDragController;
    protected boolean mDragInProgress;
    private int mDrawableAlpha;
    protected int mEmptyCellRank;
    private final int mExpandDuration;
    private float mFolderCenterY;
    public FolderIcon mFolderIcon;
    float mFolderIconPivotX;
    float mFolderIconPivotY;
    private View mFolderLayout;
    public FolderEditText mFolderName;
    public ImageView mFolderPlusButton;
    private View mFolderWrapper;
    private View mFooter;
    private int mFooterHeight;
    private ArrayList<View> mImageViews;
    public FolderInfo mInfo;
    private final InputMethodManager mInputMethodManager;
    private boolean mIsEditingName;
    protected boolean mIsExternalDrag;
    protected boolean mItemAddedBackToSelfViaIcon;
    final ArrayList<View> mItemsInReadingOrder;
    protected boolean mItemsInvalidated;
    protected final Launcher mLauncher;
    private final int mMaterialExpandDuration;
    private final int mMaterialExpandStagger;
    private float mMultiWindowFolderCenterY;
    protected final Alarm mOnExitAlarm;
    OnAlarmListener mOnExitAlarmListener;
    protected final Alarm mOnScrollHintAlarm;
    protected int mPrevTargetRank;
    private boolean mRearrangeOnClose;
    private FolderColorPickerDialog mRenameDialog;
    protected final Alarm mReorderAlarm;
    protected OnAlarmListener mReorderAlarmListener;
    private int mScrollAreaOffset;
    int mScrollHintDir;
    protected final Alarm mScrollPauseAlarm;
    private final int mShrinkDuration;
    int mState;
    protected boolean mSuppressFolderDeletion;
    protected boolean mSuppressOnAdd;
    protected int mTargetRank;
    public View mTitle;
    private final int mTitleCloseDuration;
    public int mTitleHeight;
    protected boolean mUninstallSuccessful;
    private String[] talkbackList;
    private static final Rect sTempRect = new Rect();
    private static final Comparator<ItemInfo> ITEM_POS_COMPARATOR = new Comparator<ItemInfo>() { // from class: com.android.launcher3.folder.Folder.17
        /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
        @Override // java.util.Comparator
        public int compare(ItemInfo lhs, ItemInfo rhs) {
            int i;
            int i2;
            if (lhs.rank != rhs.rank) {
                i = lhs.rank;
                i2 = rhs.rank;
            } else if (lhs.cellY != rhs.cellY) {
                i = lhs.cellY;
                i2 = rhs.cellY;
            } else {
                i = lhs.cellX;
                i2 = rhs.cellX;
            }
            return i - i2;
        }
    };

    public interface DataModel {
        void addItemToDatabase(Context context, final ItemInfo item, final long container, final long screenId, final int cellX, final int cellY);

        void addOrMoveItemInDatabase(Context context, ItemInfo item, long container, long screenId, int cellX, int cellY);

        void deleteItemFromDatabase(Context context, final ItemInfo item);

        void moveItemsInDatabase(Context context, final ArrayList<ItemInfo> items, final long container, final int screen);

        void updateItemInDatabase(Context context, final ItemInfo item);
    }

    @Override // android.view.View
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return true;
    }

    @Override // com.android.launcher3.DragSource
    public float getIntrinsicIconScaleFactor() {
        return 1.0f;
    }

    @Override // com.android.launcher3.DropTarget
    public boolean isDropEnabled() {
        return true;
    }

    @Override // com.android.launcher3.AbstractFloatingView
    protected boolean isOfType(int type) {
        return (type & 1) != 0;
    }

    @Override // com.android.launcher3.dragndrop.DragController.DragListener
    public void onDragStart(DragSource source, Object info, int dragAction) {
    }

    @Override // com.android.launcher3.DropTarget
    public void onFlingToDelete(DropTarget.DragObject d, PointF vec) {
    }

    @Override // com.android.launcher3.DragSource
    public void onFlingToDeleteCompleted() {
    }

    @Override // com.android.launcher3.AbstractFloatingView, android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override // com.android.launcher3.DragSource
    public boolean supportsAppInfoDropTarget() {
        return false;
    }

    @Override // com.android.launcher3.DragSource
    public boolean supportsDeleteDropTarget() {
        return true;
    }

    @Override // com.android.launcher3.DragSource
    public boolean supportsFlingToDelete() {
        return false;
    }

    public Folder(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mReorderAlarm = new Alarm();
        this.mOnExitAlarm = new Alarm();
        this.mOnScrollHintAlarm = new Alarm();
        this.mScrollPauseAlarm = new Alarm();
        this.mItemsInReadingOrder = new ArrayList<>();
        this.mFolderCenterY = 1.15f;
        this.mMultiWindowFolderCenterY = 2.2f;
        this.mDrawableAlpha = 127;
        this.mState = -1;
        this.mRearrangeOnClose = false;
        this.mItemsInvalidated = false;
        this.mSuppressOnAdd = false;
        this.mDragInProgress = false;
        this.mDeleteFolderOnDropCompleted = false;
        this.mSuppressFolderDeletion = false;
        this.mItemAddedBackToSelfViaIcon = false;
        this.mIsEditingName = false;
        this.mScrollHintDir = -1;
        this.mCurrentScrollDir = -1;
        this.mActionModeCallback = new ActionMode.Callback() { // from class: com.android.launcher3.folder.Folder.3
            @Override // android.view.ActionMode.Callback
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override // android.view.ActionMode.Callback
            public void onDestroyActionMode(ActionMode mode) {
            }

            @Override // android.view.ActionMode.Callback
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override // android.view.ActionMode.Callback
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return mode.getType() == 1;
            }
        };
        this.mReorderAlarmListener = new OnAlarmListener() { // from class: com.android.launcher3.folder.Folder.11
            @Override // com.android.launcher3.OnAlarmListener
            public void onAlarm(Alarm alarm) {
                Folder.this.mContent.realTimeReorder(Folder.this.mEmptyCellRank, Folder.this.mTargetRank);
                Folder folder = Folder.this;
                folder.mEmptyCellRank = folder.mTargetRank;
                Folder folder2 = Folder.this;
                folder2.sendCustomAccessibilityEvent(32, folder2.getContext().getString(R.string.talkback_grid_locate_folder, Integer.valueOf((Folder.this.mTargetRank % Folder.this.mContent.mMaxCountX) + 1), Integer.valueOf((Folder.this.mTargetRank / Folder.this.mContent.mMaxCountY) + 1)));
            }
        };
        this.mOnExitAlarmListener = new OnAlarmListener() { // from class: com.android.launcher3.folder.Folder.12
            @Override // com.android.launcher3.OnAlarmListener
            public void onAlarm(Alarm alarm) {
                Folder.this.completeDragExit();
            }
        };
        this.mDataModel = null;
        setAlwaysDrawnWithCacheEnabled(false);
        this.mInputMethodManager = (InputMethodManager) getContext().getSystemService("input_method");
        Resources resources = getResources();
        this.mExpandDuration = LGHomeFeature.Config.FEATURE_SUPPORT_FOLDER_ENLARGE_ANIMATION.getValue() ? resources.getInteger(R.integer.config_folderEnlargeDuration) : resources.getInteger(R.integer.config_materialFolderExpandDuration);
        this.mMaterialExpandDuration = LGHomeFeature.Config.FEATURE_SUPPORT_FOLDER_ENLARGE_ANIMATION.getValue() ? resources.getInteger(R.integer.config_materialFolderExpandDuration) : resources.getInteger(R.integer.config_folderExpandDuration);
        this.mShrinkDuration = LGHomeFeature.Config.FEATURE_SUPPORT_FOLDER_ENLARGE_ANIMATION.getValue() ? resources.getInteger(R.integer.config_folderShrinkDuration) : resources.getInteger(R.integer.config_folderExpandDuration);
        this.mMaterialExpandStagger = LGHomeFeature.Config.FEATURE_SUPPORT_FOLDER_ENLARGE_ANIMATION.getValue() ? resources.getInteger(R.integer.config_folderEnlargeDelayTime) : resources.getInteger(R.integer.config_materialFolderExpandStagger);
        this.mTitleCloseDuration = resources.getInteger(R.integer.config_folderTitleDownDuration);
        if (sDefaultFolderName == null) {
            sDefaultFolderName = resources.getString(R.string.folder_name);
        }
        if (sHintText == null) {
            sHintText = resources.getString(R.string.folder_hint_text);
        }
        this.mLauncher = (Launcher) context;
        setFocusableInTouchMode(true);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mContentWrapper = findViewById(R.id.folder_content_wrapper);
        View viewFindViewById = findViewById(R.id.folder_background);
        this.mBackground = viewFindViewById;
        viewFindViewById.getBackground().setAlpha(this.mDrawableAlpha);
        FolderPagedView folderPagedView = (FolderPagedView) findViewById(R.id.folder_content);
        this.mContent = folderPagedView;
        folderPagedView.setFolder(this);
        FolderEditText folderEditText = (FolderEditText) findViewById(R.id.folder_name);
        this.mFolderName = folderEditText;
        folderEditText.setFolder(this);
        this.mFolderName.setOnFocusChangeListener(this);
        this.mFolderName.setCustomSelectionActionModeCallback(this.mActionModeCallback);
        this.mFolderName.setOnEditorActionListener(this);
        this.mFolderName.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.launcher3.folder.Folder.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() != 1) {
                    return false;
                }
                Folder.this.folderNameEditmode(true);
                return false;
            }
        });
        this.mFolderName.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.android.launcher3.folder.Folder.2
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View v) {
                Folder.this.folderNameEditmode(true);
                Folder.this.mFolderName.selectAll();
                Folder.this.mFolderName.startActionMode(Folder.this.mActionModeCallback, 1);
                IMEUtils.showInputMethodDelayed(Folder.this.mFolderName, 100);
                return false;
            }
        });
        this.mFooter = findViewById(R.id.folder_footer);
        this.mBackground.measure(0, 0);
        this.mFooter.measure(0, 0);
        this.mFooterHeight = this.mFooter.getMeasuredHeight();
        this.mButtonLayer = findViewById(R.id.folder_plus_button_layout);
        if (this.mLauncher.getDeviceProfile().allowRotation && this.mLauncher.getDeviceProfile().isLandscape && LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && !this.mLauncher.getDeviceProfile().isMultiWindowMode) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mContentWrapper.getLayoutParams();
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mButtonLayer.getLayoutParams();
            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.folder_content_wrapper_height_swivel_land);
            this.mContentWrapper.setLayoutParams(layoutParams);
            this.mButtonLayer.setLayoutParams(layoutParams2);
        }
        this.mColorPalette = findViewById(R.id.folder_color_pallette);
        this.mButtonLayer.measure(0, 0);
        this.mButtonLayerWidth = this.mButtonLayer.getMeasuredWidth();
        this.mButtonLayerHeight = this.mButtonLayer.getMeasuredHeight();
        this.mColorPalette.measure(0, 0);
        this.mColorPaletteHeight = (int) getResources().getDimension(R.dimen.folder_color_pallette_height);
        setFolderTitle();
        setColorPallette();
        if (this.mLauncher.getDeviceProfile().allowRotation && this.mLauncher.getDeviceProfile().isLandscape) {
            this.mFolderWrapper = findViewById(R.id.folder_wrapper);
            View viewFindViewById2 = findViewById(R.id.folder_layout);
            this.mFolderLayout = viewFindViewById2;
            if (viewFindViewById2 == null && this.mFolderWrapper == null) {
                return;
            }
            this.mFolderWrapper.measure(0, 0);
            this.mFolderLayout.measure(0, 0);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r3v0, resolved type: android.view.View */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v.getTag() instanceof ShortcutInfo) {
            this.mLauncher.onClick(v);
            if (!LGHomeFeature.Config.FEATURE_SUPPORT_FOLDER_EDITMODE_UI.getValue() && this.mLauncher.isInState(LauncherState.WIDGETS_SPRING_LOADED) && ((IUninstallBadgeView) v).getUninstallType() == null) {
                Toast.makeText(this.mLauncher, R.string.sp_app_cannot_deleted, 0).show();
            }
        }
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View v) {
        if (!MemoryUtils.hasAvailableFileSystemMemory(this.mContext, true)) {
            LGLog.i(TAG, "Memory is full. so onLongClick() is canceled.");
            return false;
        }
        if (this.mLauncher.isInMultiWindowMode()) {
            Toast.makeText(getContext(), getResources().getString(R.string.home_screen_lock_in_multiwindow), 0).show();
            return true;
        }
        if (HomeSettingsSharedPreferences.getHomescreenLockEnabled(getContext())) {
            Toast.makeText(getContext(), HomeSettingsSharedPreferences.getHomeLockDisableGuideText(getContext()), 0).show();
            return true;
        }
        if (!this.mLauncher.isDraggingEnabled()) {
            return true;
        }
        if (this.mLauncher.isLongClickFromKeyEnter) {
            this.mLauncher.isLongClickFromKeyEnter = false;
            return true;
        }
        return startDragDeepShortcut(v, new DragOptions());
    }

    public boolean startDragDeepShortcut(View v, DragOptions options) {
        Object tag = v.getTag();
        int i = 1;
        if (tag instanceof ShortcutInfo) {
            ShortcutInfo shortcutInfo = (ShortcutInfo) tag;
            this.mLauncher.getWorkspace().beginDragSharedDeepShortcut(v, this, options);
            if (LGHomeFeature.isEnableDefaultHome() && options != null && options.isDragFromAllAps) {
                LGLog.d(TAG, "Skip drag in appdrawer because it is not appdrawer home");
            } else {
                this.mCurrentDragInfo = shortcutInfo;
                this.mEmptyCellRank = shortcutInfo.rank;
                this.mCurrentDragView = v;
                this.mContent.removeItem(v);
                this.mInfo.remove(this.mCurrentDragInfo);
                this.mDragInProgress = true;
                this.mItemAddedBackToSelfViaIcon = false;
                this.mContent.snapToDestination();
                this.mContent.showAllCrossHair(true);
                HomescreenBlurManager.getInstance(this.mContext).startDimAnimation();
                this.mDragController.addDragListener(this);
                if (options.isAccessibleDrag) {
                    this.mDragController.addDragListener(new AccessibleDragListenerAdapter(this.mContent, i) { // from class: com.android.launcher3.folder.Folder.4
                        @Override // com.android.launcher3.accessibility.AccessibleDragListenerAdapter
                        protected void enableAccessibleDrag(boolean enable) {
                            super.enableAccessibleDrag(enable);
                            Folder.this.mFooter.setImportantForAccessibility(enable ? 4 : 0);
                        }
                    });
                }
            }
        }
        return true;
    }

    protected boolean beginDrag(View v, boolean accessible) {
        Object tag = v.getTag();
        if (tag instanceof ShortcutInfo) {
            ShortcutInfo shortcutInfo = (ShortcutInfo) tag;
            this.mLauncher.getWorkspace().beginDragShared(v, new Point(), this, accessible);
            this.mCurrentDragInfo = shortcutInfo;
            this.mEmptyCellRank = shortcutInfo.rank;
            this.mCurrentDragView = v;
            this.mContent.removeItem(v);
            this.mInfo.remove(this.mCurrentDragInfo);
            this.mDragInProgress = true;
            this.mItemAddedBackToSelfViaIcon = false;
            this.mContent.snapToDestination();
            this.mLauncher.setState(LauncherState.SPRING_LOADED);
            this.mLauncher.enterSpringLoadedDragMode();
            this.mContent.showAllCrossHair(true);
            HomescreenBlurManager.getInstance(this.mContext).startDimAnimation();
        }
        return true;
    }

    @Override // com.android.launcher3.accessibility.LauncherAccessibilityDelegate.AccessibilityDragSource
    public void startDrag(CellLayout.CellInfo cellInfo, boolean accessible) {
        beginDrag(cellInfo.cell, accessible);
    }

    @Override // com.android.launcher3.accessibility.LauncherAccessibilityDelegate.AccessibilityDragSource
    public void enableAccessibleDrag(boolean enable) {
        this.mLauncher.getSearchBar().enableAccessibleDrag(enable);
        for (int i = 0; i < this.mContent.getChildCount(); i++) {
            this.mContent.getPageAt(i).enableAccessibleDrag(enable, 1);
        }
        this.mFooter.setImportantForAccessibility(enable ? 4 : 0);
        this.mLauncher.getWorkspace().setAddNewPageOnDrag(!enable);
    }

    public boolean isEditingName() {
        return this.mIsEditingName;
    }

    public void startEditingFolderName() {
        this.mFolderName.setHint("");
        this.mIsEditingName = true;
    }

    public void dismissEditingName() {
        this.mInputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
        folderNameEditmode(false);
        doneEditingFolderName(true);
    }

    public void doneEditingFolderName(boolean commit) {
        String strTrim = this.mFolderName.getText().toString().trim();
        this.mInfo.setTitle(strTrim);
        updateItemInDatabase(this.mLauncher, this.mInfo);
        if (strTrim.isEmpty()) {
            this.mFolderName.setHint(sHintText);
        } else {
            this.mFolderName.setHint("");
        }
        if (commit) {
            sendCustomAccessibilityEvent(32, String.format(getContext().getString(R.string.folder_renamed), strTrim));
        }
        requestFocus();
        Selection.setSelection(this.mFolderName.getText(), 0, 0);
        this.mIsEditingName = false;
    }

    @Override // android.widget.TextView.OnEditorActionListener
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId != 6) {
            return false;
        }
        dismissEditingName();
        return true;
    }

    public View getEditTextRegion() {
        return this.mFolderName;
    }

    @Override // com.android.launcher3.AbstractFloatingView
    protected void handleClose(boolean animate) {
        this.mIsOpen = false;
        if (this.mLauncher != null) {
            LGLog.d(TAG, "Folder: handleClose - call close folder, animate = " + animate);
            if (this.mColorPalette.getVisibility() == 0) {
                ColorPaletteEnd();
            }
            this.mLauncher.closeFolder(animate);
        }
    }

    public void setDragController(DragController dragController) {
        this.mDragController = dragController;
    }

    public void setFolderIcon(FolderIcon icon) {
        this.mFolderIcon = icon;
    }

    public FolderInfo getInfo() {
        return this.mInfo;
    }

    void bind(FolderInfo info) {
        this.mInfo = info;
        ArrayList<ShortcutInfo> arrayList = info.contents;
        Collections.sort(arrayList, ITEM_POS_COMPARATOR);
        for (ShortcutInfo shortcutInfo : this.mContent.bindItems(arrayList)) {
            this.mInfo.remove(shortcutInfo);
            deleteItemFromDatabase(this.mLauncher, shortcutInfo);
        }
        if (((BaseDragLayer.LayoutParams) getLayoutParams()) == null) {
            BaseDragLayer.LayoutParams layoutParams = new BaseDragLayer.LayoutParams(0, 0);
            layoutParams.customPosition = true;
            setLayoutParams(layoutParams);
        }
        centerAboutIcon();
        this.mItemsInvalidated = true;
        updateTextViewFocus();
        this.mInfo.addListener(this);
        if (!sDefaultFolderName.contentEquals(this.mInfo.title)) {
            this.mFolderName.setText(this.mInfo.title);
            if (this.mInfo.title.length() == 0) {
                this.mFolderName.setText("");
                this.mFolderName.setHint(sHintText);
                Log.d(TAG, "Unnamed folder. so insert Hint");
            }
        } else {
            this.mFolderName.setText("");
        }
        this.mFolderIcon.postDelayed(new Runnable() { // from class: com.android.launcher3.folder.Folder.5
            @Override // java.lang.Runnable
            public void run() {
                if (Folder.this.getItemCount() <= 1) {
                    Folder.this.replaceFolderWithFinalItem();
                }
            }
        }, info.hasOption(2) ? WAITDELAY_CREATION_WORKFOLDER : 0);
        if (this.mInfo.folderColor != 0 && !DDTUtils.isAdditionalThemeApplied(getContext()) && !DDTUtils.isAdditionalIconThemeApplied(getContext())) {
            this.mBackground.getBackground().setColorFilter(FolderColorUtil.getFolderBGColor(getContext(), getInfo().folderColor), PorterDuff.Mode.SRC_ATOP);
            this.mColorButton.setColorFilter(FolderColorUtil.getFolderBGColor(getContext(), getInfo().folderColor));
        }
        ((FolderEditText) getEditTextRegion()).setTextColor(FolderColorUtil.getFolderTextColor(getContext(), 0));
        Drawable drawable = getContext().getResources().getDrawable(R.drawable.btn_homescreen_folder_open_add);
        drawable.setTint(FolderColorUtil.getFolderBGColor(getContext(), 0));
        this.mFolderPlusButton.setImageDrawable(drawable);
        if (this.mInfo.title != null) {
            this.mFolderName.setText(this.mInfo.title);
        }
        if (DDTUtils.isAdditionalThemeApplied(getContext()) || DDTUtils.isAdditionalIconThemeApplied(getContext())) {
            this.mColorButton.setVisibility(8);
            this.mColorButtonStroke.setVisibility(8);
        }
        colorPickerCheck(FolderColorUtil.getFolderBGColor(getContext(), getInfo().folderColor));
    }

    static Folder fromXml(Launcher launcher) {
        if (Utilities.isLGUI10_0()) {
            return (Folder) launcher.getLayoutInflater().inflate(R.layout.user_folder_ux10_0, (ViewGroup) null);
        }
        return (Folder) launcher.getLayoutInflater().inflate(R.layout.user_folder, (ViewGroup) null);
    }

    private void positionAndSizeAsIcon() {
        if (getParent() instanceof DragLayer) {
            setScaleX(0.8f);
            setScaleY(0.8f);
            setAlpha(0.0f);
            this.mState = 0;
        }
    }

    private void prepareReveal() {
        if (LGHomeFeature.Config.FEATURE_SUPPORT_FOLDER_ENLARGE_ANIMATION.getValue()) {
            setScaleX(0.6f);
            setScaleY(0.6f);
            setAlpha(0.0f);
        } else {
            setScaleX(1.0f);
            setScaleY(1.0f);
            setAlpha(1.0f);
        }
        this.mState = 0;
    }

    public void animateOpen() {
        int desiredWidth;
        ObjectAnimator objectAnimatorOfPropertyValuesHolder;
        String str;
        ObjectAnimator objectAnimatorOfFloat;
        final Runnable runnable;
        Animator animator;
        if (!(getParent() instanceof DragLayer)) {
            Log.d(TAG, "!(getParent() instanceof DragLayer)");
            return;
        }
        this.mIsOpen = true;
        this.mContent.completePendingPageChanges();
        if (!this.mDragInProgress) {
            this.mContent.snapToPageImmediately(0);
        }
        if (!com.android.launcher3.Utilities.isLmpOrAbove()) {
            positionAndSizeAsIcon();
            centerAboutIcon();
            ObjectAnimator objectAnimatorOfPropertyValuesHolder2 = LauncherAnimUtils.ofPropertyValuesHolder(this, PropertyValuesHolder.ofFloat("alpha", 1.0f), PropertyValuesHolder.ofFloat("scaleX", 1.0f), PropertyValuesHolder.ofFloat("scaleY", 1.0f));
            objectAnimatorOfPropertyValuesHolder2.setDuration(this.mExpandDuration);
            setLayerType(2, null);
            runnable = new Runnable() { // from class: com.android.launcher3.folder.Folder.6
                @Override // java.lang.Runnable
                public void run() {
                    Folder.this.setLayerType(0, null);
                }
            };
            str = TAG;
            animator = objectAnimatorOfPropertyValuesHolder2;
        } else {
            prepareReveal();
            centerAboutIcon();
            AnimatorSet animatorSetCreateAnimatorSet = LauncherAnimUtils.createAnimatorSet();
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mButtonLayer.getLayoutParams();
            if (this.mLauncher.getDeviceProfile().allowRotation && this.mLauncher.getDeviceProfile().isLandscape) {
                desiredWidth = getPaddingLeft() + getPaddingRight() + this.mContent.getDesiredWidth() + this.mButtonLayerWidth + layoutParams.leftMargin + layoutParams.rightMargin;
            } else {
                desiredWidth = this.mContent.getDesiredWidth() + getPaddingLeft() + getPaddingRight();
            }
            int folderHeight = getFolderHeight();
            if (LGHomeFeature.Config.FEATURE_SUPPORT_FOLDER_ENLARGE_ANIMATION.getValue()) {
                objectAnimatorOfPropertyValuesHolder = LauncherAnimUtils.ofPropertyValuesHolder(this, PropertyValuesHolder.ofFloat("alpha", 0.8f, 1.0f), PropertyValuesHolder.ofFloat("scaleX", 0.25f, 1.0f), PropertyValuesHolder.ofFloat("scaleY", 0.15f, 1.0f));
            } else {
                float pivotX = ((desiredWidth / 2) - getPivotX()) * (-0.075f);
                float pivotY = ((folderHeight / 2) - getPivotY()) * (-0.075f);
                setTranslationX(pivotX);
                setTranslationY(pivotY);
                objectAnimatorOfPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(this, PropertyValuesHolder.ofFloat("translationX", pivotX, 0.0f), PropertyValuesHolder.ofFloat("translationY", pivotY, 0.0f));
            }
            objectAnimatorOfPropertyValuesHolder.setDuration(this.mExpandDuration - 0);
            objectAnimatorOfPropertyValuesHolder.setStartDelay(0);
            objectAnimatorOfPropertyValuesHolder.setInterpolator(new LogDecelerateInterpolator(100, 0));
            int iMax = (int) Math.max(Math.max(desiredWidth - getPivotX(), 0.0f), getPivotX());
            int iMax2 = (int) Math.max(Math.max(folderHeight - getPivotY(), 0.0f), getPivotY());
            double d = iMax;
            str = TAG;
            float fHypot = (float) Math.hypot(d, iMax2);
            if (LGHomeFeature.Config.FEATURE_SUPPORT_FOLDER_ENLARGE_ANIMATION.getValue()) {
                this.mContentWrapper.setAlpha(1.0f);
                objectAnimatorOfFloat = ObjectAnimator.ofFloat(this.mContentWrapper, "alpha", 1.0f, 1.0f);
            } else {
                this.mContentWrapper.setAlpha(0.0f);
                objectAnimatorOfFloat = ObjectAnimator.ofFloat(this.mContentWrapper, "alpha", 0.0f, 1.0f);
            }
            objectAnimatorOfFloat.setDuration(this.mMaterialExpandDuration);
            objectAnimatorOfFloat.setStartDelay(this.mMaterialExpandStagger);
            objectAnimatorOfFloat.setInterpolator(new AccelerateInterpolator(1.5f));
            this.mFooter.setAlpha(0.0f);
            ObjectAnimator objectAnimatorOfFloat2 = ObjectAnimator.ofFloat(this.mFooter, "alpha", 0.0f, 1.0f);
            ObjectAnimator objectAnimator = objectAnimatorOfPropertyValuesHolder;
            objectAnimatorOfFloat2.setDuration(this.mMaterialExpandDuration);
            objectAnimatorOfFloat2.setStartDelay(this.mMaterialExpandStagger);
            objectAnimatorOfFloat2.setInterpolator(new AccelerateInterpolator(1.5f));
            this.mTitle.setAlpha(0.0f);
            ObjectAnimator objectAnimatorOfFloat3 = ObjectAnimator.ofFloat(this.mTitle, "alpha", 0.0f, 1.0f);
            objectAnimatorOfFloat3.setDuration(this.mMaterialExpandDuration);
            objectAnimatorOfFloat3.setStartDelay(this.mMaterialExpandDuration);
            this.mFolderPlusButton.setAlpha(0.0f);
            ObjectAnimator objectAnimatorOfFloat4 = ObjectAnimator.ofFloat(this.mFolderPlusButton, "alpha", 0.0f, 1.0f);
            objectAnimatorOfFloat4.setDuration(this.mMaterialExpandDuration);
            objectAnimatorOfFloat4.setStartDelay(this.mMaterialExpandDuration);
            animatorSetCreateAnimatorSet.play(objectAnimatorOfFloat3);
            animatorSetCreateAnimatorSet.play(objectAnimatorOfFloat4);
            animatorSetCreateAnimatorSet.play(objectAnimator);
            animatorSetCreateAnimatorSet.play(objectAnimatorOfFloat);
            animatorSetCreateAnimatorSet.play(objectAnimatorOfFloat2);
            if (!LGHomeFeature.Config.FEATURE_SUPPORT_FOLDER_ENLARGE_ANIMATION.getValue()) {
                ValueAnimator valueAnimatorCreateCircularReveal = UiThreadCircularReveal.createCircularReveal(this, (int) getPivotX(), (int) getPivotY(), 0.0f, fHypot);
                valueAnimatorCreateCircularReveal.setDuration(this.mMaterialExpandDuration);
                valueAnimatorCreateCircularReveal.setInterpolator(new LogDecelerateInterpolator(100, 0));
                animatorSetCreateAnimatorSet.play(valueAnimatorCreateCircularReveal);
            }
            this.mContentWrapper.setLayerType(2, null);
            this.mFooter.setLayerType(2, null);
            runnable = new Runnable() { // from class: com.android.launcher3.folder.Folder.7
                @Override // java.lang.Runnable
                public void run() {
                    Folder.this.mContentWrapper.setLayerType(0, null);
                    Folder.this.mFooter.setLayerType(0, null);
                    Folder.this.setFocusOnFirstChild();
                    BaseDragLayer.LayoutParams layoutParams2 = (BaseDragLayer.LayoutParams) Folder.this.getLayoutParams();
                    View childAt = ((CellLayout) Folder.this.mContent.getChildAt(0)).getShortcutsAndWidgets().getChildAt(0);
                    if (ManagedProfileUtils.isAFW(Folder.this.getContext()) && Folder.this.mInfo.hasOption(2) && Folder.this.mIsOpen) {
                        WorkGuideView.showGuide(Folder.this.mLauncher, false, layoutParams2.x + (childAt.getWidth() / 4), layoutParams2.y + Folder.this.mTitleHeight + childAt.getHeight(), false);
                    }
                }
            };
            animator = animatorSetCreateAnimatorSet;
        }
        animator.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.folder.Folder.8
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                Folder folder = Folder.this;
                folder.sendCustomAccessibilityEvent(32, folder.mContent.getAccessibilityDescription());
                Folder.this.mState = 1;
                FolderStateTransitionWatcher.getInstance().setState(Folder.this, FolderStateTransitionWatcher.FolderState.OPEN_START);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                Folder.this.mState = 2;
                Runnable runnable2 = runnable;
                if (runnable2 != null) {
                    runnable2.run();
                }
                Folder.this.mContent.setFocusOnFirstChild();
                FolderStateTransitionWatcher.getInstance().setState(Folder.this, FolderStateTransitionWatcher.FolderState.OPEN_END);
                if (Float.compare(1.0f, Folder.this.getAlpha()) != 0) {
                    Folder.this.setAlpha(1.0f);
                    Folder.this.setScaleX(1.0f);
                    Folder.this.setScaleY(1.0f);
                }
            }
        });
        if (this.mContent.getPageCount() > 1 && !this.mInfo.hasOption(4)) {
            float desiredWidth2 = (((this.mContent.getDesiredWidth() - this.mFooter.getPaddingLeft()) - this.mFooter.getPaddingRight()) - this.mFolderName.getPaint().measureText(this.mFolderName.getText().toString())) / 2.0f;
            FolderEditText folderEditText = this.mFolderName;
            if (this.mContent.mIsRtl) {
                desiredWidth2 = -desiredWidth2;
            }
            folderEditText.setTranslationX(desiredWidth2);
            this.mContent.setMarkerScale(0.0f);
            final boolean z = !this.mDragInProgress;
            animator.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.folder.Folder.9
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    TimeInterpolator logDecelerateInterpolator;
                    ViewPropertyAnimator viewPropertyAnimatorTranslationX = Folder.this.mFolderName.animate().setDuration(633L).translationX(0.0f);
                    if (com.android.launcher3.Utilities.isLmpOrAbove()) {
                        logDecelerateInterpolator = AnimationUtils.loadInterpolator(Folder.this.mLauncher, 17563661);
                    } else {
                        logDecelerateInterpolator = new LogDecelerateInterpolator(100, 0);
                    }
                    viewPropertyAnimatorTranslationX.setInterpolator(logDecelerateInterpolator);
                    Folder.this.mContent.animateMarkers();
                    if (z) {
                        Folder.this.mInfo.setOption(4, true, Folder.this.mLauncher);
                    }
                }
            });
        } else {
            this.mFolderName.setTranslationX(0.0f);
            this.mContent.setMarkerScale(1.0f);
        }
        animator.setStartDelay(this.mExpandDuration / 30);
        animator.start();
        Log.d(str, "openFolderAnim start, openFolderAnim : " + animator);
        if (this.mDragController.isDragging()) {
            this.mDragController.forceTouchMove();
        }
        FolderPagedView folderPagedView = this.mContent;
        folderPagedView.verifyVisibleHighResIcons(folderPagedView.getNextPage());
        folderPagedView.showAllCrossHair(this.mLauncher.getWorkspace().getState() == Workspace.State.SPRING_LOADED || this.mLauncher.getWorkspace().getState() == Workspace.State.OVERVIEW);
        if (HomeSettingsSharedPreferences.getHomescreenLockEnabled(getContext()) || this.mLauncher.isInMultiWindowMode() || ((this instanceof AllAppsFolder) && ManagedProfileUtils.isAFW(this.mLauncher))) {
            this.mFolderPlusButton.setEnabled(false);
        } else {
            this.mFolderPlusButton.setEnabled(true);
        }
    }

    public void setFocusOnFirstChild() {
        View childAt;
        if (getVisibility() == 0 && (childAt = ((CellLayout) this.mContent.getChildAt(0)).getShortcutsAndWidgets().getChildAt(0)) != null) {
            childAt.requestFocus();
            childAt.requestAccessibilityFocus();
        }
    }

    public void beginExternalDrag(ShortcutInfo item) {
        this.mCurrentDragInfo = item;
        this.mEmptyCellRank = this.mContent.allocateRankForNewItem(item);
        this.mIsExternalDrag = true;
        this.mDragInProgress = true;
        this.mDragController.addDragListener(this);
    }

    @Override // com.android.launcher3.dragndrop.DragController.DragListener
    public void onDragEnd() {
        if (this.mIsExternalDrag && this.mDragInProgress) {
            completeDragExit();
        }
        this.mDragController.removeDragListener(this);
    }

    void sendCustomAccessibilityEvent(int type, String text) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) getContext().getSystemService("accessibility");
        if (accessibilityManager.isEnabled()) {
            AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(type);
            onInitializeAccessibilityEvent(accessibilityEventObtain);
            accessibilityEventObtain.getText().add(text);
            accessibilityManager.sendAccessibilityEvent(accessibilityEventObtain);
        }
    }

    public void animateClosed(boolean... animate) {
        if (getParent() instanceof DragLayer) {
            AnimatorSet animatorSetCreateAnimatorSet = LauncherAnimUtils.createAnimatorSet();
            ObjectAnimator objectAnimatorOfPropertyValuesHolder = LauncherAnimUtils.ofPropertyValuesHolder(this, PropertyValuesHolder.ofFloat("alpha", 0.0f), PropertyValuesHolder.ofFloat("scaleX", 0.2f), PropertyValuesHolder.ofFloat("scaleY", 0.2f));
            objectAnimatorOfPropertyValuesHolder.setDuration(this.mShrinkDuration);
            objectAnimatorOfPropertyValuesHolder.setInterpolator(new LogDecelerateInterpolator(100, 0));
            this.mTitle.setAlpha(1.0f);
            ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(this.mTitle, "alpha", 1.0f, 0.0f);
            objectAnimatorOfFloat.setDuration(this.mTitleCloseDuration);
            this.mFolderPlusButton.setAlpha(1.0f);
            ObjectAnimator objectAnimatorOfFloat2 = ObjectAnimator.ofFloat(this.mFolderPlusButton, "alpha", 1.0f, 0.0f);
            objectAnimatorOfFloat2.setDuration(this.mTitleCloseDuration);
            animatorSetCreateAnimatorSet.play(objectAnimatorOfPropertyValuesHolder);
            animatorSetCreateAnimatorSet.play(objectAnimatorOfFloat);
            animatorSetCreateAnimatorSet.play(objectAnimatorOfFloat2);
            animatorSetCreateAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.folder.Folder.10
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    LGLog.i(Folder.TAG, "Folder: close onAnimationEnd. opened = " + Folder.this.mInfo.opened);
                    if (!Folder.this.mInfo.opened) {
                        Folder.this.onCloseComplete();
                        Folder.this.setLayerType(0, null);
                        Folder.this.mState = 0;
                        FolderStateTransitionWatcher.getInstance().setState(Folder.this, FolderStateTransitionWatcher.FolderState.CLOSE_END);
                    }
                    Folder.this.mInfo.isCloseAnimating = false;
                    if (ManagedProfileUtils.isAFW(Folder.this.getContext()) && Folder.this.mInfo.hasOption(2) && WorkGuideView.getWorkGuideView() != null) {
                        WorkGuideView.getWorkGuideView().removeGuide();
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    LGLog.i(Folder.TAG, "close onAnimationStart");
                    Folder.this.mInfo.isCloseAnimating = true;
                    Folder folder = Folder.this;
                    folder.sendCustomAccessibilityEvent(32, folder.getContext().getString(R.string.folder_closed));
                    Folder.this.mState = 1;
                    FolderStateTransitionWatcher.getInstance().setState(Folder.this, FolderStateTransitionWatcher.FolderState.CLOSE_START);
                }
            });
            if (animate.length == 1 && !animate[0]) {
                LGLog.i(TAG, "close without animation duration");
                close();
                return;
            }
            LGLog.i(TAG, "Folder: close with animation duration:" + this.mShrinkDuration);
            setLayerType(2, null);
            animatorSetCreateAnimatorSet.start();
        }
    }

    @Override // com.android.launcher3.DropTarget
    public boolean acceptDrop(DropTarget.DragObject d) {
        int i = ((ItemInfo) d.dragInfo).itemType;
        return ((i != 0 && i != 1 && i != 6) || isFull() || this.mCurrentDragInfo == null) ? false : true;
    }

    @Override // com.android.launcher3.DropTarget
    public void onDragEnter(DropTarget.DragObject d) {
        this.mPrevTargetRank = -1;
        this.mOnExitAlarm.cancelAlarm();
        this.mScrollAreaOffset = (d.dragView.getDragRegionWidth() / 2) - d.xOffset;
    }

    public boolean isLayoutRtl() {
        return getLayoutDirection() == 1;
    }

    @Override // com.android.launcher3.DropTarget
    public void onDragOver(DropTarget.DragObject d) {
        onDragOver(d, 250);
    }

    protected int getTargetRank(DropTarget.DragObject d, float[] recycle) {
        float[] visualCenter = d.getVisualCenter(recycle);
        if (this.mLauncher.isInMultiWindowModeCompat()) {
            visualCenter[1] = visualCenter[1] - getResources().getDimensionPixelSize(R.dimen.folder_top_dimen_resizable_launcher);
        }
        return this.mContent.findNearestArea(((int) visualCenter[0]) - getPaddingLeft(), (int) ((((visualCenter[1] - getPaddingTop()) - this.mContentWrapper.getTop()) - this.mBackground.getTop()) - this.mContent.getPaddingTop()));
    }

    void onDragOver(DropTarget.DragObject d, int reorderDelay) {
        if (this.mScrollPauseAlarm.alarmPending()) {
            return;
        }
        float[] fArr = new float[2];
        int targetRank = getTargetRank(d, fArr);
        this.mTargetRank = targetRank;
        if (targetRank != this.mPrevTargetRank) {
            this.mReorderAlarm.cancelAlarm();
            this.mReorderAlarm.setOnAlarmListener(this.mReorderAlarmListener);
            this.mReorderAlarm.setAlarm(250L);
            this.mPrevTargetRank = this.mTargetRank;
        }
        float f = fArr[0];
        int nextPage = this.mContent.getNextPage();
        float cellWidth = this.mContent.getCurrentCellLayout().getCellWidth() * ICON_OVERSCROLL_WIDTH_FACTOR;
        boolean z = f < cellWidth;
        boolean z2 = f > ((float) getWidth()) - cellWidth;
        if (nextPage > 0 && (!this.mContent.mIsRtl ? z : z2)) {
            showScrollHint(0, d);
            return;
        }
        if (nextPage < this.mContent.getPageCount() - 1 && (!this.mContent.mIsRtl ? z2 : z)) {
            showScrollHint(1, d);
            return;
        }
        this.mOnScrollHintAlarm.cancelAlarm();
        if (this.mScrollHintDir != -1) {
            this.mContent.clearScrollHint();
            this.mScrollHintDir = -1;
        }
    }

    private void showScrollHint(int direction, DropTarget.DragObject d) {
        if (this.mScrollHintDir != direction) {
            this.mContent.showScrollHint(direction);
            this.mScrollHintDir = direction;
        }
        if (this.mOnScrollHintAlarm.alarmPending() && this.mCurrentScrollDir == direction) {
            return;
        }
        this.mCurrentScrollDir = direction;
        this.mOnScrollHintAlarm.cancelAlarm();
        this.mOnScrollHintAlarm.setOnAlarmListener(new OnScrollHintListener(d));
        this.mOnScrollHintAlarm.setAlarm(500L);
        this.mReorderAlarm.cancelAlarm();
        this.mTargetRank = this.mEmptyCellRank;
    }

    public void completeDragExit() {
        if (this.mInfo.opened) {
            this.mLauncher.closeFolder(new boolean[0]);
            this.mRearrangeOnClose = true;
        } else if (this.mState == 1) {
            this.mRearrangeOnClose = true;
        } else {
            rearrangeChildren();
            clearDragInfo();
        }
    }

    private void clearDragInfo() {
        this.mCurrentDragInfo = null;
        this.mCurrentDragView = null;
        this.mSuppressOnAdd = false;
        this.mIsExternalDrag = false;
    }

    @Override // com.android.launcher3.DropTarget
    public void onDragExit(DropTarget.DragObject d) {
        if (!d.dragComplete) {
            this.mOnExitAlarm.setOnAlarmListener(this.mOnExitAlarmListener);
            this.mOnExitAlarm.setAlarm(400L);
        } else {
            sendCustomAccessibilityEvent(32, getContext().getString(R.string.sp_moved_NORMAL));
        }
        this.mReorderAlarm.cancelAlarm();
        this.mOnScrollHintAlarm.cancelAlarm();
        this.mScrollPauseAlarm.cancelAlarm();
        if (this.mScrollHintDir != -1) {
            this.mContent.clearScrollHint();
            this.mScrollHintDir = -1;
        }
    }

    @Override // com.android.launcher3.DropTarget
    public void prepareAccessibilityDrop() {
        if (this.mReorderAlarm.alarmPending()) {
            this.mReorderAlarm.cancelAlarm();
            this.mReorderAlarmListener.onAlarm(this.mReorderAlarm);
        }
    }

    @Override // com.android.launcher3.DragSource
    public void onDropCompleted(final View target, final DropTarget.DragObject d, final boolean isFlingToDelete, final boolean success) {
        if (isFlingToDelete || !success) {
            this.mLauncher.exitSpringLoadedDragModeDelayed(true, 300, null);
        }
        if (this.mDeferDropAfterUninstall) {
            Log.d(TAG, "Deferred handling drop because waiting for uninstall.");
            this.mDeferredAction = new Runnable() { // from class: com.android.launcher3.folder.Folder.13
                @Override // java.lang.Runnable
                public void run() {
                    Folder.this.onDropCompleted(target, d, isFlingToDelete, success);
                    Folder.this.mDeferredAction = null;
                }
            };
            return;
        }
        boolean z = success && (!(this.mDeferredAction != null) || this.mUninstallSuccessful);
        if (z) {
            if (this.mDeleteFolderOnDropCompleted && !this.mItemAddedBackToSelfViaIcon && target != this) {
                replaceFolderWithFinalItem();
            }
        } else {
            ShortcutInfo shortcutInfo = (ShortcutInfo) d.dragInfo;
            boolean z2 = this instanceof AllAppsFolder;
            if (z2 && getInfo().contents.contains(shortcutInfo)) {
                LGLog.d(TAG, "The item cannot be added because AllApps Folder has a duplicate item(" + shortcutInfo + ")");
                d.deferDragViewCleanupPostAnimation = false;
            } else {
                View view = this.mCurrentDragView;
                View viewCreateNewView = (view == null || view.getTag() != shortcutInfo) ? this.mContent.createNewView(shortcutInfo) : this.mCurrentDragView;
                ArrayList<View> itemsInReadingOrder = getItemsInReadingOrder();
                if (!z2 && this.mLauncher.getWorkspace().getState() == Workspace.State.OVERVIEW) {
                    UninstallModeManager.getInstance(getContext()).setUninstallTypeForBadgeView(viewCreateNewView);
                }
                itemsInReadingOrder.add(shortcutInfo.rank, viewCreateNewView);
                this.mContent.arrangeChildren(itemsInReadingOrder, itemsInReadingOrder.size());
                this.mItemsInvalidated = true;
                this.mSuppressOnAdd = true;
                this.mFolderIcon.onDrop(d);
                this.mSuppressOnAdd = false;
            }
        }
        if (target != this && this.mOnExitAlarm.alarmPending()) {
            this.mOnExitAlarm.cancelAlarm();
            if (!z) {
                this.mSuppressFolderDeletion = true;
            }
            this.mScrollPauseAlarm.cancelAlarm();
            completeDragExit();
        }
        this.mDeleteFolderOnDropCompleted = false;
        this.mDragInProgress = false;
        this.mItemAddedBackToSelfViaIcon = false;
        this.mCurrentDragInfo = null;
        this.mCurrentDragView = null;
        this.mSuppressOnAdd = false;
        updateItemLocationsInDatabaseBatch();
        if (getItemCount() <= this.mContent.itemsPerPage()) {
            this.mInfo.setOption(4, false, this.mLauncher);
        }
    }

    @Override // com.android.launcher3.UninstallDropTarget.UninstallSource
    public void deferCompleteDropAfterUninstallActivity() {
        this.mDeferDropAfterUninstall = true;
    }

    @Override // com.android.launcher3.UninstallDropTarget.UninstallSource
    public void onUninstallActivityReturned(boolean success) {
        this.mDeferDropAfterUninstall = false;
        this.mUninstallSuccessful = success;
        Runnable runnable = this.mDeferredAction;
        if (runnable != null) {
            runnable.run();
        }
    }

    protected void updateItemLocationsInDatabaseBatch() {
        ArrayList<View> itemsInReadingOrder = getItemsInReadingOrder();
        ArrayList<ItemInfo> arrayList = new ArrayList<>();
        for (int i = 0; i < itemsInReadingOrder.size(); i++) {
            ItemInfo itemInfo = (ItemInfo) itemsInReadingOrder.get(i).getTag();
            itemInfo.rank = i;
            arrayList.add(itemInfo);
        }
        moveItemsInDatabase(this.mLauncher, arrayList, this.mInfo.id, 0);
    }

    public void addItemLocationsInDatabase() {
        ArrayList<View> itemsInReadingOrder = getItemsInReadingOrder();
        for (int i = 0; i < itemsInReadingOrder.size(); i++) {
            ItemInfo itemInfo = (ItemInfo) itemsInReadingOrder.get(i).getTag();
            addItemToDatabase(this.mLauncher, itemInfo, this.mInfo.id, 0L, itemInfo.cellX, itemInfo.cellY);
        }
    }

    public void notifyDrop() {
        if (this.mDragInProgress) {
            this.mItemAddedBackToSelfViaIcon = true;
        }
    }

    public boolean isFull() {
        return this.mContent.isFull();
    }

    private void centerAboutIcon() {
        if (LGHomeFeature.Config.FEATURE_SUPPORT_FOLDER_ENLARGE_ANIMATION.getValue()) {
            setCenterPosition();
        } else {
            centerAboutIconModify();
        }
    }

    public float getPivotXForIconAnimation() {
        return this.mFolderIconPivotX;
    }

    public float getPivotYForIconAnimation() {
        return this.mFolderIconPivotY;
    }

    private int getContentAreaHeight() {
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        Rect workspacePadding = deviceProfile.getWorkspacePadding(this.mContent.mIsRtl);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mFooter.getLayoutParams();
        return Math.max(Math.min((((deviceProfile.availableHeightPx - workspacePadding.top) - workspacePadding.bottom) - this.mFooterHeight) + layoutParams.topMargin, this.mContent.getDesiredHeight() + this.mFooterHeight + layoutParams.topMargin), 5);
    }

    private int getContentAreaWidth() {
        return Math.max(this.mContent.getDesiredWidth(), 5);
    }

    private int getFolderHeight() {
        return getFolderHeight(getBackgroundHeight(getContentAreaHeight()));
    }

    private int getFolderHeight(int backgroundHeight) {
        int paddingTop;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mButtonLayer.getLayoutParams();
        if (Utilities.isLGUI10_0()) {
            if (this.mLauncher.getDeviceProfile().allowRotation && this.mLauncher.getDeviceProfile().isLandscape) {
                paddingTop = getPaddingTop() + getPaddingBottom() + backgroundHeight + this.mTitleHeight;
                backgroundHeight = this.mColor.getMeasuredHeight();
            } else {
                return getPaddingTop() + getPaddingBottom() + backgroundHeight + this.mTitleHeight + this.mColor.getMeasuredHeight() + this.mButtonLayerHeight + layoutParams.topMargin;
            }
        } else if (this.mLauncher.getDeviceProfile().allowRotation && this.mLauncher.getDeviceProfile().isLandscape) {
            paddingTop = getPaddingTop() + getPaddingBottom();
        } else {
            return getPaddingTop() + getPaddingBottom() + backgroundHeight + this.mButtonLayerHeight + layoutParams.topMargin;
        }
        return paddingTop + backgroundHeight;
    }

    private int getBackgroundHeight(int contentAreaHeight) {
        if (Utilities.isLGUI10_0()) {
            return contentAreaHeight;
        }
        int dimension = this.mLauncher.mDeviceProfile.isLandscape ? 0 : (int) getResources().getDimension(R.dimen.folder_title_bottom_margin);
        ((FrameLayout.LayoutParams) this.mTitle.getLayoutParams()).bottomMargin = dimension;
        return contentAreaHeight + this.mTitleHeight + dimension;
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int backgroundHeight;
        int paddingLeft;
        int contentAreaWidth = getContentAreaWidth();
        int contentAreaHeight = getContentAreaHeight();
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(contentAreaWidth, 1073741824);
        int iMakeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(contentAreaHeight, 1073741824);
        this.mContent.setFixedSize(contentAreaWidth, contentAreaHeight);
        this.mContentWrapper.measure(iMakeMeasureSpec, iMakeMeasureSpec2);
        if (this.mContent.getChildCount() > 0) {
            int cellWidth = (this.mContent.getPageAt(0).getCellWidth() - this.mLauncher.getDeviceProfile().iconSizePx) / 2;
            this.mFooter.setPadding(this.mContent.getPaddingLeft() + cellWidth, this.mFooter.getPaddingTop(), this.mContent.getPaddingRight() + cellWidth, this.mFooter.getPaddingBottom());
        }
        this.mFooter.measure(iMakeMeasureSpec, View.MeasureSpec.makeMeasureSpec(this.mFooterHeight, 1073741824));
        DragLayer dragLayer = (DragLayer) this.mLauncher.findViewById(R.id.drag_layer);
        if (this.mLauncher.getDeviceProfile().allowRotation && this.mLauncher.getDeviceProfile().isLandscape) {
            backgroundHeight = Math.min(getResources().getDimensionPixelSize(R.dimen.folder_height), (dragLayer.getHeight() - SysUINavigationMode.getMode(getContext()).height) - WindowUtils.getStatusBarHeight(getContext()));
        } else {
            backgroundHeight = getBackgroundHeight(iMakeMeasureSpec2);
        }
        this.mBackground.measure(iMakeMeasureSpec, backgroundHeight);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mButtonLayer.getLayoutParams();
        if (this.mLauncher.getDeviceProfile().allowRotation && this.mLauncher.getDeviceProfile().isLandscape) {
            paddingLeft = getPaddingLeft() + getPaddingRight() + contentAreaWidth + this.mButtonLayerWidth + layoutParams.leftMargin + layoutParams.rightMargin;
        } else {
            paddingLeft = getPaddingLeft() + getPaddingRight() + contentAreaWidth;
        }
        int folderHeight = getFolderHeight(backgroundHeight);
        setMeasuredDimension(paddingLeft, folderHeight);
        this.mTitle.measure(View.MeasureSpec.makeMeasureSpec(getContentAreaWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(this.mTitle.getMeasuredHeight(), 1073741824));
        if (this.mLauncher.getDeviceProfile().allowRotation && this.mLauncher.getDeviceProfile().isLandscape) {
            if (this.mFolderWrapper != null || this.mFolderLayout != null) {
                this.mFolderLayout.measure(View.MeasureSpec.makeMeasureSpec(paddingLeft, 1073741824), View.MeasureSpec.makeMeasureSpec(folderHeight, 1073741824));
                this.mFolderWrapper.measure(iMakeMeasureSpec, View.MeasureSpec.makeMeasureSpec(folderHeight, 1073741824));
                this.mButtonLayer.measure(View.MeasureSpec.makeMeasureSpec(this.mButtonLayerWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(folderHeight, 1073741824));
            }
        } else {
            this.mButtonLayer.measure(iMakeMeasureSpec, View.MeasureSpec.makeMeasureSpec(this.mButtonLayerHeight, 1073741824));
        }
        if (Utilities.isLGUI10_0()) {
            this.mColor.measure(View.MeasureSpec.makeMeasureSpec(getContentAreaWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(this.mColor.getMeasuredHeight(), 1073741824));
        }
        this.mColorPalette.measure(iMakeMeasureSpec, View.MeasureSpec.makeMeasureSpec(this.mColorPaletteHeight, 1073741824));
    }

    public void rearrangeChildren() {
        rearrangeChildren(-1);
    }

    public void rearrangeChildren(int itemCount) {
        ArrayList<View> itemsInReadingOrder = getItemsInReadingOrder();
        this.mContent.arrangeChildren(itemsInReadingOrder, Math.max(itemCount, itemsInReadingOrder.size()));
        this.mItemsInvalidated = true;
    }

    public ViewGroup getContent() {
        return this.mContent;
    }

    public int getItemCount() {
        return this.mContent.getItemCount();
    }

    protected void onCloseComplete() {
        dismissRenameDialog();
        DragLayer dragLayer = (DragLayer) getParent();
        if (dragLayer != null) {
            LGLog.i(TAG, "Folder: onCloseComplete - removeView");
            dragLayer.removeView(this);
        }
        this.mDragController.removeDropTarget(this);
        clearFocus();
        if (getItemCount() > 1) {
            this.mFolderIcon.requestFocus();
            this.mFolderIcon.setIconVisible(true);
        }
        if (this.mRearrangeOnClose) {
            rearrangeChildren();
            this.mRearrangeOnClose = false;
        }
        if (getItemCount() <= 1) {
            boolean z = this.mDragInProgress;
            if (!z && !this.mSuppressFolderDeletion) {
                replaceFolderWithFinalItem();
            } else if (z) {
                this.mDeleteFolderOnDropCompleted = true;
            }
        }
        this.mSuppressFolderDeletion = false;
        clearDragInfo();
        closeTalkback();
        this.mLauncher.getRootView().setDisallowBackGesture(true);
    }

    protected void replaceFolderWithFinalItem() {
        Runnable runnable = new Runnable() { // from class: com.android.launcher3.folder.Folder.14
            @Override // java.lang.Runnable
            public void run() {
                CellLayout cellLayout = Folder.this.mLauncher.getCellLayout(Folder.this.mInfo.container, Folder.this.mInfo.screenId);
                if (cellLayout == null || !(cellLayout.getChildAt(Folder.this.mInfo.cellX, Folder.this.mInfo.cellY) instanceof FolderIcon)) {
                    return;
                }
                View viewCreateShortcut = null;
                if (Folder.this.getItemCount() == 1) {
                    ShortcutInfo shortcutInfo = Folder.this.mInfo.contents.get(0);
                    viewCreateShortcut = Folder.this.mLauncher.createShortcut(cellLayout, shortcutInfo);
                    Folder folder = Folder.this;
                    folder.addOrMoveItemInDatabase(folder.mLauncher, shortcutInfo, Folder.this.mInfo.container, Folder.this.mInfo.screenId, Folder.this.mInfo.cellX, Folder.this.mInfo.cellY);
                }
                View view = viewCreateShortcut;
                if (Folder.this.getItemCount() <= 1) {
                    Folder folder2 = Folder.this;
                    folder2.deleteItemFromDatabase(folder2.mLauncher, Folder.this.mInfo);
                    if (cellLayout != null) {
                        cellLayout.removeView(Folder.this.mFolderIcon);
                    }
                    if (Folder.this.mFolderIcon instanceof DropTarget) {
                        Folder.this.mDragController.removeDropTarget((DropTarget) Folder.this.mFolderIcon);
                    }
                    Folder.this.mLauncher.removeFolder(Folder.this.mInfo);
                }
                if (view != null) {
                    Folder.this.mLauncher.getWorkspace().addInScreenFromBind(view, Folder.this.mInfo.container, Folder.this.mInfo.screenId, Folder.this.mInfo.cellX, Folder.this.mInfo.cellY, Folder.this.mInfo.spanX, Folder.this.mInfo.spanY);
                }
            }
        };
        View lastItem = this.mContent.getLastItem();
        if (lastItem != null) {
            this.mFolderIcon.performDestroyAnimation(lastItem, runnable);
        } else {
            runnable.run();
        }
        this.mDestroyed = true;
    }

    public boolean isDestroyed() {
        return this.mDestroyed;
    }

    public void updateTextViewFocus() {
        View lastItem = this.mContent.getLastItem();
        if (lastItem != null) {
            this.mFolderName.setNextFocusDownId(lastItem.getId());
            this.mFolderName.setNextFocusRightId(lastItem.getId());
            this.mFolderName.setNextFocusLeftId(lastItem.getId());
            this.mFolderName.setNextFocusUpId(lastItem.getId());
        }
    }

    @Override // com.android.launcher3.DropTarget
    public void onDrop(DropTarget.DragObject d) {
        View viewCreateAndAddViewForRank;
        if (!this.mContent.rankOnCurrentPage(this.mEmptyCellRank)) {
            this.mTargetRank = getTargetRank(d, null);
            this.mReorderAlarmListener.onAlarm(this.mReorderAlarm);
            this.mOnScrollHintAlarm.cancelAlarm();
            this.mScrollPauseAlarm.cancelAlarm();
        }
        this.mContent.completePendingPageChanges();
        ItemInfo itemInfo = new ItemInfo(this.mCurrentDragInfo);
        ShortcutInfo shortcutInfo = this.mCurrentDragInfo;
        int itemCount = this.mEmptyCellRank > getItemCount() ? getItemCount() : this.mEmptyCellRank;
        if (this.mIsExternalDrag) {
            if ((d.dragSource instanceof AllAppsFolder) && !(this instanceof AllAppsFolder)) {
                shortcutInfo = new ShortcutInfo(this.mCurrentDragInfo);
                shortcutInfo.container = -1L;
            }
            ShortcutInfo shortcutInfo2 = shortcutInfo;
            viewCreateAndAddViewForRank = this.mContent.createAndAddViewForRank(shortcutInfo2, itemCount);
            addOrMoveItemInDatabase(this.mLauncher, shortcutInfo2, this.mInfo.id, 0L, shortcutInfo2.cellX, shortcutInfo2.cellY);
            if (d.dragSource != this) {
                updateItemLocationsInDatabaseBatch();
            }
            this.mIsExternalDrag = false;
            shortcutInfo = shortcutInfo2;
        } else {
            viewCreateAndAddViewForRank = this.mCurrentDragView;
            this.mContent.addViewForRank(viewCreateAndAddViewForRank, shortcutInfo, itemCount);
        }
        Runnable runnableExitSpringLoadedDragModeOnDrop = exitSpringLoadedDragModeOnDrop(d.dragSource, this, shortcutInfo, itemInfo);
        if (d.dragView.hasDrawn()) {
            float scaleX = getScaleX();
            float scaleY = getScaleY();
            setScaleX(1.0f);
            setScaleY(1.0f);
            this.mLauncher.getDragLayer().animateViewIntoPosition(d.dragView, viewCreateAndAddViewForRank, runnableExitSpringLoadedDragModeOnDrop, null);
            setScaleX(scaleX);
            setScaleY(scaleY);
        } else {
            d.deferDragViewCleanupPostAnimation = false;
            viewCreateAndAddViewForRank.setVisibility(0);
        }
        this.mItemsInvalidated = true;
        rearrangeChildren();
        this.mSuppressOnAdd = true;
        this.mInfo.add(shortcutInfo);
        this.mSuppressOnAdd = false;
        this.mCurrentDragInfo = null;
        this.mDragInProgress = false;
        if (this.mContent.getPageCount() > 1) {
            this.mInfo.setOption(4, true, this.mLauncher);
        }
        this.mContent.showAllCrossHair(this.mLauncher.getWorkspace().getState() == Workspace.State.OVERVIEW);
    }

    public void hideItem(ShortcutInfo info) {
        View viewForInfo = getViewForInfo(info);
        if (viewForInfo != null) {
            viewForInfo.setVisibility(4);
        }
    }

    public void showItem(ShortcutInfo info) {
        View viewForInfo = getViewForInfo(info);
        if (viewForInfo != null) {
            viewForInfo.setVisibility(0);
        }
    }

    @Override // com.android.launcher3.model.data.FolderInfo.FolderListener
    public void onAdd(ShortcutInfo item) {
        if (this.mSuppressOnAdd) {
            return;
        }
        FolderPagedView folderPagedView = this.mContent;
        folderPagedView.createAndAddViewForRank(item, folderPagedView.allocateRankForNewItem(item));
        this.mItemsInvalidated = true;
        addOrMoveItemInDatabase(this.mLauncher, item, this.mInfo.id, 0L, item.cellX, item.cellY);
    }

    @Override // com.android.launcher3.model.data.FolderInfo.FolderListener
    public void onRemove(ShortcutInfo item) {
        this.mItemsInvalidated = true;
        if (item == this.mCurrentDragInfo) {
            return;
        }
        this.mContent.removeItem(getViewForInfo(item));
        if (this.mState == 1) {
            this.mRearrangeOnClose = true;
        } else {
            rearrangeChildren();
        }
        if (getItemCount() <= 1) {
            if (this.mInfo.opened) {
                this.mLauncher.closeFolder(new boolean[0]);
            }
            replaceFolderWithFinalItem();
        }
    }

    @Override // com.android.launcher3.model.data.FolderInfo.FolderListener
    public void onAdd(List<ShortcutInfo> items) {
        if (this.mSuppressOnAdd) {
            return;
        }
        this.mContent.createAndAddViews(items);
        this.mItemsInvalidated = true;
        for (ShortcutInfo shortcutInfo : items) {
            addOrMoveItemInDatabase(this.mLauncher, shortcutInfo, this.mInfo.id, 0L, shortcutInfo.cellX, shortcutInfo.cellY);
        }
    }

    @Override // com.android.launcher3.model.data.FolderInfo.FolderListener
    public void onRemove(List<ShortcutInfo> items) {
        this.mItemsInvalidated = true;
        Iterator<ShortcutInfo> it = items.iterator();
        while (it.hasNext()) {
            this.mContent.removeItem(getViewForInfo(it.next()));
        }
        rearrangeChildren();
        if (getItemCount() <= 1) {
            if (this.mInfo.opened) {
                this.mLauncher.closeFolder(new boolean[0]);
            }
            replaceFolderWithFinalItem();
        }
    }

    public View getViewForInfo(final ShortcutInfo item) {
        return this.mContent.iterateOverItems(new Workspace.ItemOperator() { // from class: com.android.launcher3.folder.Folder.15
            @Override // com.android.launcher3.Workspace.ItemOperator
            public boolean evaluate(ItemInfo info, View view, View parent) {
                return info == item;
            }
        });
    }

    @Override // com.android.launcher3.model.data.FolderInfo.FolderListener
    public void onItemsChanged() {
        updateTextViewFocus();
    }

    @Override // com.android.launcher3.model.data.FolderInfo.FolderListener
    public void onTitleChanged(CharSequence title) {
        applyFolderName(title);
    }

    public ArrayList<View> getItemsInReadingOrder() {
        if (this.mItemsInvalidated) {
            this.mItemsInReadingOrder.clear();
            this.mContent.iterateOverItems(new Workspace.ItemOperator() { // from class: com.android.launcher3.folder.Folder.16
                @Override // com.android.launcher3.Workspace.ItemOperator
                public boolean evaluate(ItemInfo info, View view, View parent) {
                    Folder.this.mItemsInReadingOrder.add(view);
                    return false;
                }
            });
            this.mItemsInvalidated = false;
        }
        return this.mItemsInReadingOrder;
    }

    @Override // com.android.launcher3.DropTarget
    public void getLocationInDragLayer(int[] loc) {
        this.mLauncher.getDragLayer().getLocationInDragLayer(this, loc);
    }

    @Override // android.view.View.OnFocusChangeListener
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == this.mFolderName && hasFocus) {
            startEditingFolderName();
        }
    }

    @Override // com.android.launcher3.DropTarget
    public void getHitRectRelativeToDragLayer(Rect outRect) {
        getHitRect(outRect);
        if (this.mButtonLayer != null && !this.mLauncher.getDeviceProfile().isLandscape) {
            outRect.bottom -= this.mButtonLayer.getHeight();
        }
        outRect.left -= this.mScrollAreaOffset;
        outRect.right += this.mScrollAreaOffset;
    }

    @Override // com.android.launcher3.logging.UserEventDispatcher.LogContainerProvider
    public void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent) {
        target.gridX = info.cellX;
        target.gridY = info.cellY;
        target.pageIndex = this.mContent.getCurrentPage();
        targetParent.containerType = 3;
    }

    @Override // com.android.launcher3.util.TouchController
    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            DragLayer dragLayer = this.mLauncher.getDragLayer();
            if (this.mInputMethodManager.isAcceptingText() && ((!dragLayer.isEventOverView(this, ev) || dragLayer.isEventOverView(this.mContentWrapper, ev) || dragLayer.isEventOverView(this.mColorPalette, ev)) && dragLayer.getBottom() - ev.getY() > SysUINavigationMode.getMode(getContext()).height)) {
                dismissEditingName();
                return true;
            }
            if (dragLayer.isEventOverView(this.mFolderPlusButton, ev)) {
                if (this.mLauncher.isInMultiWindowMode()) {
                    Toast.makeText(getContext(), getResources().getString(R.string.home_screen_lock_in_multiwindow), 0).show();
                    return true;
                }
                if (HomeSettingsSharedPreferences.getHomescreenLockEnabled(getContext())) {
                    Toast.makeText(getContext(), HomeSettingsSharedPreferences.getHomeLockDisableGuideText(getContext()), 0).show();
                    return true;
                }
            }
            if (!dragLayer.isEventOverView(this, ev) || (dragLayer.isEventOverView(this.mButtonLayer, ev) && !dragLayer.isEventOverView(this.mFolderPlusButton, ev))) {
                if (LauncherAppState.getInstance(getContext()).getAccessibilityDelegate() != null && LauncherAppState.getInstance(getContext()).getAccessibilityDelegate().isInAccessibleDrag()) {
                    if (!dragLayer.isEventOverView(this.mLauncher.getSearchBar(), ev)) {
                        return true;
                    }
                } else {
                    if (SysUINavigationMode.getMode(getContext()) != SysUINavigationMode.Mode.TWO_BUTTONS) {
                        SysUINavigationMode.getMode(getContext());
                        SysUINavigationMode.Mode mode = SysUINavigationMode.Mode.NO_BUTTON;
                    }
                    this.mLauncher.getUserEventDispatcher().logActionTapOutside(LoggerUtils.newContainerTarget(3));
                    close(true);
                    return true;
                }
            }
        }
        return false;
    }

    private class OnScrollHintListener implements OnAlarmListener {
        private final DropTarget.DragObject mDragObject;

        OnScrollHintListener(DropTarget.DragObject object) {
            this.mDragObject = object;
        }

        @Override // com.android.launcher3.OnAlarmListener
        public void onAlarm(Alarm alarm) {
            if (Folder.this.mCurrentScrollDir == 0) {
                Folder.this.mContent.scrollLeft();
                Folder.this.mScrollHintDir = -1;
            } else {
                if (Folder.this.mCurrentScrollDir != 1) {
                    return;
                }
                Folder.this.mContent.scrollRight();
                Folder.this.mScrollHintDir = -1;
            }
            Folder.this.mCurrentScrollDir = -1;
            Folder.this.mScrollPauseAlarm.setOnAlarmListener(Folder.this.new OnScrollFinishedListener(this.mDragObject));
            Folder.this.mScrollPauseAlarm.setAlarm(750L);
        }
    }

    private class OnScrollFinishedListener implements OnAlarmListener {
        private final DropTarget.DragObject mDragObject;

        OnScrollFinishedListener(DropTarget.DragObject object) {
            this.mDragObject = object;
        }

        @Override // com.android.launcher3.OnAlarmListener
        public void onAlarm(Alarm alarm) {
            Folder.this.onDragOver(this.mDragObject, 1);
        }
    }

    @Override // com.android.launcher3.model.data.FolderInfo.FolderListener
    public void onColorChanged() {
        this.mBackground.getBackground().setColorFilter(FolderColorUtil.getFolderBGColor(getContext(), getInfo().folderColor), PorterDuff.Mode.SRC_ATOP);
        this.mFolderName.getBackground().setColorFilter(FolderColorUtil.getFolderBGColor(getContext(), getInfo().folderColor), PorterDuff.Mode.SRC_ATOP);
        this.mColorButton.getDrawable().setColorFilter(FolderColorUtil.getFolderBGColor(getContext(), getInfo().folderColor), PorterDuff.Mode.SRC_ATOP);
        int folderTextColor = FolderColorUtil.getFolderTextColor(getContext(), 0);
        ((FolderEditText) getEditTextRegion()).setTextColor(folderTextColor);
        changeChildColor(folderTextColor);
        PageIndicator pageIndicator = this.mContent.getPageIndicator();
        if (pageIndicator != null) {
            for (int i = 0; i < this.mContent.getChildCount(); i++) {
                pageIndicator.updateMarker(i, this.mContent.getPageIndicatorMarker(i));
            }
        }
        invalidate();
        updateItemInDatabase(getContext(), this.mInfo);
        colorPickerCheck(FolderColorUtil.getFolderBGColor(getContext(), getInfo().folderColor));
    }

    public void changeChildColor(int color) {
        ViewGroup content = getContent();
        for (int i = 0; i < content.getChildCount(); i++) {
            ViewGroup viewGroup = (ViewGroup) content.getChildAt(i);
            for (int i2 = 0; i2 < viewGroup.getChildCount(); i2++) {
                View childAt = viewGroup.getChildAt(i2);
                if (childAt instanceof ViewGroup) {
                    int i3 = 0;
                    while (true) {
                        ViewGroup viewGroup2 = (ViewGroup) childAt;
                        if (i3 < viewGroup2.getChildCount()) {
                            View childAt2 = viewGroup2.getChildAt(i3);
                            if (childAt2 instanceof TextView) {
                                ((TextView) childAt2).setTextColor(color);
                            }
                            i3++;
                        }
                    }
                }
            }
        }
    }

    public void dismissRenameDialog() {
        FolderColorPickerDialog folderColorPickerDialog = this.mRenameDialog;
        if (folderColorPickerDialog != null) {
            folderColorPickerDialog.onDismiss(null);
            this.mRenameDialog = null;
        }
    }

    public void callRenameDialog() {
        FragmentManager fragmentManager = this.mLauncher.getFragmentManager();
        FragmentTransaction fragmentTransactionBeginTransaction = fragmentManager.beginTransaction();
        FolderColorPickerDialog folderColorPickerDialog = (FolderColorPickerDialog) fragmentManager.findFragmentByTag("LGFolderRename");
        this.mRenameDialog = folderColorPickerDialog;
        if (folderColorPickerDialog != null) {
            folderColorPickerDialog.dismiss();
            fragmentManager.executePendingTransactions();
        }
        FolderColorPickerDialog folderColorPickerDialog2 = FolderColorPickerDialog.getInstance(this.mLauncher);
        this.mRenameDialog = folderColorPickerDialog2;
        folderColorPickerDialog2.setFolderInfo(this.mInfo);
        try {
            this.mRenameDialog.show(fragmentManager, "LGFolderRename");
        } catch (IllegalStateException e) {
            LGLog.w("FolderColorAspect", e.toString(), new int[0]);
        }
        fragmentManager.executePendingTransactions();
        fragmentTransactionBeginTransaction.commitAllowingStateLoss();
    }

    private void closeTalkback() {
        Workspace workspace = this.mLauncher.getWorkspace();
        if (workspace != null) {
            TalkBackUtils.sendAccessibilityEvent((Context) this.mLauncher, String.format(getContext().getString(R.string.sp_talkback_workspace_info), Integer.valueOf((workspace.mNextPage != -1 ? workspace.mNextPage : workspace.mCurrentPage) + 1), Integer.valueOf(workspace.getChildCount())), true);
        }
    }

    private void applyFolderName(CharSequence title) {
        this.mFolderName.setText(title);
        if (title.length() == 0) {
            this.mFolderName.setHint(sHintText);
        } else {
            this.mFolderName.setHint("");
        }
        updateItemInDatabase(this.mLauncher, this.mInfo);
    }

    public Runnable exitSpringLoadedDragModeOnDrop(final DragSource dragSource, final DropTarget dropTarget, final ItemInfo oldItemInfo, final ItemInfo newItemInfo) {
        return new Runnable() { // from class: com.android.launcher3.folder.Folder.18
            @Override // java.lang.Runnable
            public void run() {
                UninstallModeManager uninstallModeManager = UninstallModeManager.getInstance(Folder.this.getContext());
                boolean zIsInUninstallMode = uninstallModeManager.isInUninstallMode();
                if (!uninstallModeManager.checkToEnterUninstallMode(Folder.this.mLauncher, dragSource, dropTarget, oldItemInfo, newItemInfo)) {
                    Folder.this.mLauncher.exitSpringLoadedDragModeDelayed(true, 300, null);
                    return;
                }
                if (!zIsInUninstallMode) {
                    Folder.this.mLauncher.exitSpringLoadedDragModeDelayed(true, 300, null);
                } else {
                    if (Folder.this.mLauncher.getWorkspace() == null || !Folder.this.mLauncher.getWorkspace().isInOverviewMode() || Folder.this.mLauncher.isInState(LauncherState.NORMAL)) {
                        return;
                    }
                    Folder.this.mLauncher.getStateManager().setStateOnly(LauncherState.NORMAL);
                }
            }
        };
    }

    public int getExpandDuration() {
        return this.mExpandDuration;
    }

    public void setFolderTitle() {
        ((LinearLayout) this.mFooter).setGravity(17);
        ImageView imageView = (ImageView) findViewById(R.id.folder_plus_button);
        this.mFolderPlusButton = imageView;
        imageView.setOnClickListener(new View.OnClickListener() { // from class: com.android.launcher3.folder.Folder.19
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Folder.this.startFolderPlus();
            }
        });
        View viewFindViewById = findViewById(R.id.folder_title);
        this.mTitle = viewFindViewById;
        viewFindViewById.measure(0, 0);
        this.mTitleHeight = this.mTitle.getMeasuredHeight();
        this.mFolderName.getBackground().setAlpha(this.mDrawableAlpha);
    }

    public void startFolderPlus() {
        try {
            int i = getResources().getConfiguration().orientation;
            Intent intent = new Intent(getContext(), (Class<?>) FolderPlusActivity.class);
            intent.putExtra("folderId", this.mInfo.id);
            intent.putExtra("isAllApps", false);
            intent.putExtra("folderOrientation", i);
            this.mLauncher.startActivityForResult(intent, LauncherConst.REQUEST_FOLDERPLUS);
            this.mLauncher.mSuppressCloseFolder = true;
        } catch (ActivityNotFoundException e) {
            LGLog.e(TAG, "ActivityNotFoundException - ", e);
        }
    }

    private void centerAboutIconModify() {
        int desiredWidth;
        BaseDragLayer.LayoutParams layoutParams = (BaseDragLayer.LayoutParams) getLayoutParams();
        layoutParams.gravity = 17;
        layoutParams.topMargin = 0;
        layoutParams.bottomMargin = 0;
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mButtonLayer.getLayoutParams();
        if (this.mLauncher.getDeviceProfile().allowRotation && this.mLauncher.getDeviceProfile().isLandscape) {
            desiredWidth = getPaddingLeft() + getPaddingRight() + this.mContent.getDesiredWidth() + this.mButtonLayerWidth + layoutParams2.leftMargin + layoutParams2.rightMargin;
        } else {
            desiredWidth = this.mContent.getDesiredWidth() + getPaddingLeft() + getPaddingRight();
        }
        int folderHeight = getFolderHeight();
        DragLayer dragLayer = (DragLayer) this.mLauncher.findViewById(R.id.drag_layer);
        float f = this.mLauncher.getDeviceProfile().isMultiWindowMode ? this.mMultiWindowFolderCenterY : this.mFolderCenterY;
        setPivotX(desiredWidth / 2);
        setPivotY(folderHeight / 2);
        layoutParams.width = desiredWidth;
        layoutParams.height = folderHeight;
        layoutParams.x = (dragLayer.getWidth() - desiredWidth) / 2;
        layoutParams.y = (int) ((dragLayer.getHeight() - folderHeight) / f);
        FolderIcon folderIcon = this.mFolderIcon;
        Rect rect = sTempRect;
        dragLayer.getDescendantRectRelativeToSelf(folderIcon, rect);
        float fCenterX = rect.centerX();
        float fCenterY = rect.centerY();
        this.mLauncher.getWorkspace().getPageAreaRelativeToDragLayer(rect);
        this.mFolderIconPivotX = (this.mFolderIcon.getMeasuredWidth() / 2) + ((fCenterX - rect.centerX()) / 3.0f);
        this.mFolderIconPivotY = (this.mFolderIcon.getMeasuredHeight() / 2) + ((fCenterY - rect.centerY()) / 3.0f);
    }

    private void setCenterPosition() {
        int paddingLeft;
        int desiredWidth;
        int folderHeight;
        DragLayer dragLayer = (DragLayer) this.mLauncher.findViewById(R.id.drag_layer);
        BaseDragLayer.LayoutParams layoutParams = (BaseDragLayer.LayoutParams) getLayoutParams();
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mButtonLayer.getLayoutParams();
        if (this.mLauncher.getDeviceProfile().allowRotation && this.mLauncher.getDeviceProfile().isLandscape) {
            paddingLeft = getPaddingLeft() + getPaddingRight() + this.mContent.getDesiredWidth() + this.mButtonLayerWidth + layoutParams2.leftMargin;
            desiredWidth = layoutParams2.rightMargin;
        } else {
            paddingLeft = getPaddingLeft() + getPaddingRight();
            desiredWidth = this.mContent.getDesiredWidth();
        }
        int i = paddingLeft + desiredWidth;
        if (this.mLauncher.getDeviceProfile().allowRotation && this.mLauncher.getDeviceProfile().isLandscape) {
            folderHeight = Math.min(getFolderHeight(getResources().getDimensionPixelSize(R.dimen.folder_height)), (dragLayer.getHeight() - SysUINavigationMode.getMode(getContext()).height) - WindowUtils.getStatusBarHeight(getContext()));
        } else {
            folderHeight = getFolderHeight();
        }
        float f = this.mLauncher.getDeviceProfile().isMultiWindowMode ? this.mMultiWindowFolderCenterY : this.mFolderCenterY;
        layoutParams.gravity = 17;
        layoutParams.topMargin = 0;
        layoutParams.bottomMargin = 0;
        layoutParams.width = i;
        layoutParams.height = folderHeight;
        layoutParams.x = (dragLayer.getWidth() - i) / 2;
        if (this.mLauncher.getDeviceProfile().allowRotation && this.mLauncher.getDeviceProfile().isLandscape && !this.mLauncher.getDeviceProfile().isMultiWindowMode) {
            if (this.mContent.mIsRtl) {
                layoutParams.x -= (this.mButtonLayerWidth + layoutParams2.rightMargin) / 2;
            } else {
                layoutParams.x += (this.mButtonLayerWidth + layoutParams2.leftMargin) / 2;
            }
        }
        if (this.mLauncher.getDeviceProfile().allowRotation && this.mLauncher.getDeviceProfile().isLandscape) {
            layoutParams.y = Math.max(WindowUtils.getStatusBarHeight(getContext()), (dragLayer.getHeight() - SysUINavigationMode.getMode(getContext()).height) - folderHeight);
        } else if (this.mLauncher.getDeviceProfile().isMultiWindowMode) {
            layoutParams.y = Math.max(0, (int) (((dragLayer.getHeight() - SysUINavigationMode.getMode(getContext()).height) - folderHeight) / f));
        } else {
            layoutParams.y = Math.max(WindowUtils.getStatusBarHeight(getContext()), (int) (((dragLayer.getHeight() - SysUINavigationMode.getMode(getContext()).height) - folderHeight) / f));
        }
        FolderIcon folderIcon = this.mFolderIcon;
        Rect rect = sTempRect;
        dragLayer.getDescendantRectRelativeToSelf(folderIcon, rect);
        float fCenterX = rect.centerX();
        float fCenterY = rect.centerY();
        if (this.mLauncher.getWorkspace() == null) {
            return;
        }
        this.mLauncher.getWorkspace().getPageAreaRelativeToDragLayer(rect);
        float height = this.mLauncher.getHotseat().getHeight() / 2;
        float height2 = this.mLauncher.getPageindicator().getHeight() / 2;
        float fCenterX2 = rect.centerX();
        float fCenterY2 = rect.centerY() + height + height2;
        this.mFolderIconPivotX = (this.mFolderIcon.getMeasuredWidth() / 2) + ((fCenterX - fCenterX2) / 4.0f);
        this.mFolderIconPivotY = (int) ((this.mFolderIcon.getMeasuredHeight() / 2) + ((fCenterY - fCenterY2) / 4.0f));
        setPivotX(fCenterX - layoutParams.x);
        setPivotY(fCenterY - layoutParams.y);
    }

    public void setDataModel(DataModel dataModel) {
        this.mDataModel = dataModel;
    }

    private void addItemToDatabase(Context context, final ItemInfo item, final long container, final long screenId, final int cellX, final int cellY) {
        DataModel dataModel = this.mDataModel;
        if (dataModel != null) {
            dataModel.addItemToDatabase(context, item, container, screenId, cellX, cellY);
        } else {
            LauncherModel.addItemToDatabase(context, item, container, screenId, cellX, cellY);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addOrMoveItemInDatabase(Context context, ItemInfo item, long container, long screenId, int cellX, int cellY) {
        DataModel dataModel = this.mDataModel;
        if (dataModel != null) {
            dataModel.addOrMoveItemInDatabase(context, item, container, screenId, cellX, cellY);
        } else {
            LauncherModel.addOrMoveItemInDatabase(context, item, container, screenId, cellX, cellY);
        }
    }

    private void updateItemInDatabase(Context context, final ItemInfo item) {
        DataModel dataModel = this.mDataModel;
        if (dataModel != null) {
            dataModel.updateItemInDatabase(context, item);
        } else {
            LauncherModel.updateItemInDatabase(context, item);
        }
    }

    protected void deleteItemFromDatabase(Context context, final ItemInfo item) {
        DataModel dataModel = this.mDataModel;
        if (dataModel != null) {
            dataModel.deleteItemFromDatabase(context, item);
        } else {
            LauncherModel.deleteItemFromDatabase(context, item);
        }
    }

    private void moveItemsInDatabase(Context context, final ArrayList<ItemInfo> items, final long container, final int screen) {
        DataModel dataModel = this.mDataModel;
        if (dataModel != null) {
            dataModel.moveItemsInDatabase(this.mLauncher, items, this.mInfo.id, 0);
        } else {
            LauncherModel.moveItemsInDatabase(this.mLauncher, items, this.mInfo.id, 0);
        }
    }

    protected boolean beginDrag(View v, boolean accessible, boolean enterSpringLoaded) {
        Object tag = v.getTag();
        if (tag instanceof ShortcutInfo) {
            ShortcutInfo shortcutInfo = (ShortcutInfo) tag;
            this.mLauncher.getWorkspace().beginDragShared(v, new Point(), this, accessible);
            this.mCurrentDragInfo = shortcutInfo;
            this.mEmptyCellRank = shortcutInfo.rank;
            this.mCurrentDragView = v;
            this.mContent.removeItem(v);
            this.mInfo.remove(this.mCurrentDragInfo);
            this.mDragInProgress = true;
            this.mItemAddedBackToSelfViaIcon = false;
            this.mContent.snapToDestination();
            if (enterSpringLoaded) {
                this.mLauncher.setState(LauncherState.SPRING_LOADED);
                this.mLauncher.enterSpringLoadedDragMode();
            }
        }
        return true;
    }

    public int getState() {
        return this.mState;
    }

    public void close() {
        onCloseComplete();
        this.mState = 0;
        FolderStateTransitionWatcher.getInstance().setState(this, FolderStateTransitionWatcher.FolderState.CLOSE_END);
    }

    @Override // com.android.launcher3.AbstractFloatingView
    public void logActionCommand(int command) {
        this.mLauncher.getUserEventDispatcher().logActionCommand(command, getFolderIcon(), 3);
    }

    public FolderIcon getFolderIcon() {
        return this.mFolderIcon;
    }

    public void setColorPallette() {
        if (Utilities.isLGUI10_0()) {
            View viewFindViewById = findViewById(R.id.folder_color);
            this.mColor = viewFindViewById;
            viewFindViewById.measure(0, 0);
        }
        ImageView imageView = (ImageView) findViewById(R.id.folder_color_button);
        this.mColorButton = imageView;
        imageView.measure(0, 0);
        ImageView imageView2 = (ImageView) findViewById(R.id.folder_color_button_stroke);
        this.mColorButtonStroke = imageView2;
        imageView2.measure(0, 0);
        this.mColorButtonWhite = findViewById(R.id.folder_color_1);
        this.mColorButtonBlue = findViewById(R.id.folder_color_2);
        this.mColorButtonSky = findViewById(R.id.folder_color_3);
        this.mColorButtonYellow = findViewById(R.id.folder_color_4);
        this.mColorButtonPink = findViewById(R.id.folder_color_5);
        this.mColorButtonMore = findViewById(R.id.folder_color_6);
        this.mColorButtonDelete = (ImageView) findViewById(R.id.folder_color_delete);
        ArrayList<View> arrayList = new ArrayList<>();
        this.mImageViews = arrayList;
        arrayList.add(this.mColorButtonWhite);
        this.mImageViews.add(this.mColorButtonBlue);
        this.mImageViews.add(this.mColorButtonSky);
        this.mImageViews.add(this.mColorButtonYellow);
        this.mImageViews.add(this.mColorButtonPink);
        this.mImageViews.add(this.mColorButtonMore);
        this.mColorButton.setColorFilter(FolderColorUtil.getFolderBGColor(getContext(), 0));
        setTalkbackResString();
        for (int i = 0; i < this.mImageViews.size(); i++) {
            View view = this.mImageViews.get(i);
            View view2 = this.mColorButtonMore;
            if (view == view2) {
                ((ImageView) view2.findViewById(R.id.background_image)).setImageDrawable(getResources().getDrawable(R.drawable.btn_homescreen_color_picker_edit_02));
                this.mColorButtonMore.setContentDescription(getResources().getString(R.string.sp_custom_color_NORMAL));
                this.mImageViews.get(i).setTag(Integer.valueOf(FolderColorUtil.getColorMax()));
            } else {
                ((ImageView) this.mImageViews.get(i).findViewById(R.id.background_image)).setColorFilter(FolderColorUtil.getFolderBGColor(getContext(), i));
                ((ImageView) this.mImageViews.get(i).findViewById(R.id.inner_check_image)).setColorFilter(FolderColorUtil.getFolderBGColor(getContext(), i));
                ((ImageView) this.mImageViews.get(i).findViewById(R.id.outer_check_image)).setColorFilter(FolderColorUtil.getFolderBGColor(getContext(), i));
                this.mImageViews.get(i).setTag(Integer.valueOf(i));
                this.mImageViews.get(i).setContentDescription(this.talkbackList[i]);
            }
            this.mImageViews.get(i).findViewById(R.id.background_image_stroke).setVisibility(0);
        }
        this.mColorButtonDelete.setImageDrawable(getResources().getDrawable(R.drawable.btn_homescreen_delete));
        this.mColorButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.launcher3.folder.Folder.20
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Folder.this.ColorPaletteStart();
            }
        });
    }

    public void ColorPaletteStart() {
        this.mColorPalette.setAlpha(0.0f);
        this.mColorPalette.setVisibility(0);
        this.mTitle.setVisibility(4);
        if (Utilities.isLGUI10_0()) {
            this.mColorButton.setVisibility(4);
            this.mColorButtonStroke.setVisibility(4);
        }
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(this.mColorPalette, "alpha", 1.0f);
        objectAnimatorOfFloat.setDuration(200L);
        objectAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.folder.Folder.21
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                Folder.this.mColorPalette.setVisibility(0);
            }
        });
        objectAnimatorOfFloat.start();
        ColorButtonListener();
        this.mColorButtonDelete.setOnClickListener(new View.OnClickListener() { // from class: com.android.launcher3.folder.Folder.22
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Folder.this.ColorPaletteEnd();
            }
        });
        this.mColorButtonMore.setOnClickListener(new View.OnClickListener() { // from class: com.android.launcher3.folder.Folder.23
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (Folder.this.mInputMethodManager.isAcceptingText()) {
                    Folder.this.dismissEditingName();
                }
                Folder.this.callRenameDialog();
            }
        });
    }

    public void ColorPaletteEnd() {
        this.mColorPalette.setAlpha(1.0f);
        this.mTitle.setVisibility(0);
        if (Utilities.isLGUI10_0()) {
            this.mColorButton.setVisibility(0);
            this.mColorButtonStroke.setVisibility(0);
        }
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(this.mColorPalette, "alpha", 0.0f);
        objectAnimatorOfFloat.setDuration(200L);
        objectAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.folder.Folder.24
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                Folder.this.mColorPalette.setVisibility(4);
            }
        });
        objectAnimatorOfFloat.start();
    }

    public void ColorButtonListener() {
        View.OnClickListener onClickListener = new View.OnClickListener() { // from class: com.android.launcher3.folder.Folder.25
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                int folderBGColor = FolderColorUtil.getFolderBGColor(Folder.this.getContext(), ((Integer) v.getTag()).intValue());
                Folder.this.mInfo.changeFolderColor(folderBGColor);
                Folder.this.onColorChanged();
                Folder.this.colorPickerCheck(folderBGColor);
            }
        };
        this.mColorButtonWhite.setOnClickListener(onClickListener);
        this.mColorButtonBlue.setOnClickListener(onClickListener);
        this.mColorButtonSky.setOnClickListener(onClickListener);
        this.mColorButtonYellow.setOnClickListener(onClickListener);
        this.mColorButtonPink.setOnClickListener(onClickListener);
    }

    public void folderNameEditmode(boolean sw) {
        FolderEditText folderEditText = this.mFolderName;
        folderEditText.setInputType(folderEditText.getInputType() | 524288 | 8192);
        this.mFolderName.setPrivateImeOptions("com.lge.android.editmode.noContent");
        this.mFolderName.requestFocus();
        this.mFolderName.setSelected(true);
        if (sw) {
            return;
        }
        this.mFolderName.setKeyListener(null);
        this.mFolderName.clearFocus();
        this.mFolderName.setSelected(false);
    }

    public void colorPickerCheck(int color) {
        boolean z = false;
        for (int i = 0; i < this.mImageViews.size(); i++) {
            if (color == FolderColorUtil.getFolderBGColor(getContext(), ((Integer) this.mImageViews.get(i).getTag()).intValue())) {
                this.mImageViews.get(i).findViewById(R.id.inner_check_image).setVisibility(0);
                this.mImageViews.get(i).findViewById(R.id.inner_check_image_border).setVisibility(0);
                this.mImageViews.get(i).findViewById(R.id.outer_check_image).setVisibility(0);
                this.mImageViews.get(i).findViewById(R.id.outer_check_image_border).setVisibility(0);
                this.mImageViews.get(i).findViewById(R.id.background_image).setVisibility(4);
                this.mImageViews.get(i).findViewById(R.id.background_image_stroke).setVisibility(4);
                z = true;
            } else {
                this.mImageViews.get(i).findViewById(R.id.inner_check_image).setVisibility(4);
                this.mImageViews.get(i).findViewById(R.id.inner_check_image_border).setVisibility(4);
                this.mImageViews.get(i).findViewById(R.id.outer_check_image).setVisibility(4);
                this.mImageViews.get(i).findViewById(R.id.outer_check_image_border).setVisibility(4);
                this.mImageViews.get(i).findViewById(R.id.inner_check_image_custom_color).setVisibility(4);
                this.mImageViews.get(i).findViewById(R.id.outer_check_image_custom_color).setVisibility(4);
                this.mImageViews.get(i).findViewById(R.id.background_image).setVisibility(0);
                this.mImageViews.get(i).findViewById(R.id.background_image_stroke).setVisibility(0);
            }
            if (!z) {
                this.mColorButtonMore.findViewById(R.id.inner_check_image_custom_color).setVisibility(0);
                this.mColorButtonMore.findViewById(R.id.inner_check_image_border).setVisibility(0);
                this.mColorButtonMore.findViewById(R.id.outer_check_image_custom_color).setVisibility(0);
                this.mColorButtonMore.findViewById(R.id.outer_check_image_border).setVisibility(0);
                this.mColorButtonMore.findViewById(R.id.background_image).setVisibility(4);
                this.mColorButtonMore.findViewById(R.id.background_image_stroke).setVisibility(4);
            }
        }
    }

    private void setTalkbackResString() {
        Resources resources = getContext().getResources();
        String[] stringArray = resources.getStringArray(R.array.lg_folder_color_list);
        this.talkbackList = new String[stringArray.length];
        String[] stringArray2 = resources.getStringArray(R.array.lg_folder_color_list);
        for (int i = 0; i < this.mImageViews.size(); i++) {
            int identifier = resources.getIdentifier(stringArray[i], "string", getContext().getPackageName());
            if (identifier > 0) {
                this.talkbackList[i] = resources.getString(identifier);
            } else {
                this.talkbackList[i] = stringArray2[i];
            }
        }
    }

    public boolean hasWorkProfileItem() {
        return ManagedProfileUtils.isAdminApplication(this.mLauncher, getInfo().getContents().get(0).getTargetComponent());
    }
}
