package com.android.launcher3.popup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Property;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherAppWidgetHostView;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LogAccelerateInterpolator;
import com.android.launcher3.Utilities;
import com.android.launcher3.accessibility.LauncherAccessibilityDelegate;
import com.android.launcher3.accessibility.ShortcutMenuAccessibilityDelegate;
import com.android.launcher3.anim.PropertyListBuilder;
import com.android.launcher3.anim.PropertyResetListener;
import com.android.launcher3.badge.BadgeInfo;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.dragndrop.DraggableView;
import com.android.launcher3.graphics.TriangleShape;
import com.android.launcher3.logging.LoggerUtils;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.notification.NotificationInfo;
import com.android.launcher3.notification.NotificationItemView;
import com.android.launcher3.notification.NotificationKeyData;
import com.android.launcher3.popup.PopupPopulator;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.shortcuts.DeepShortcutView;
import com.android.launcher3.shortcuts.ShortcutDragPreviewProvider;
import com.android.launcher3.shortcuts.ShortcutsItemView;
import com.android.launcher3.touch.ItemLongClickListener;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.ShortcutUtil;
import com.android.launcher3.util.Themes;
import com.lge.launcher3.R;
import com.lge.launcher3.allapps.AllAppsPagedView;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.OrientationUtils;
import com.lge.launcher3.wing.SwivelAppIconView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/* JADX INFO: loaded from: classes.dex */
public class PopupContainerWithArrow<T extends BaseDraggingActivity> extends ArrowPopup<T> implements DragSource, DragController.DragListener {
    private static final int SYSTEM_SHORTCUT_LARGE_MAX_SIZE = 4;
    private static final int SYSTEM_SHORTCUT_MAX_SIZE = 3;
    private static final String TAG = "PopupContainerWithArrow";
    private LauncherAccessibilityDelegate mAccessibilityDelegate;
    private View mArrow;
    private boolean mDeferContainerRemoval;
    private PointF mInterceptTouchDown;
    protected boolean mIsAboveIcon;
    private boolean mIsCenterAligned;
    private boolean mIsLeftAligned;
    private final boolean mIsRtl;
    protected final Launcher mLauncher;
    private ArrayList<PopupContainerListener> mListeners;
    private int mMovedFromCenter;
    private NotificationItemView mNotificationItemView;
    private int mNumNotifications;
    protected Animator mOpenCloseAnimator;
    protected BubbleTextView mOriginalIcon;
    protected LauncherAppWidgetHostView mOriginalWidget;
    protected PopupItemDragHandler mPopupItemDragHandler;
    private AnimatorSet mReduceHeightAnimatorSet;
    private final List<DeepShortcutView> mShortcuts;
    public ShortcutsItemView mShortcutsItemView;
    private final int mStartDragThreshold;
    public ShortcutsItemView mSystemShortcutsItemView;
    private final Rect mTempRect;

    public interface PopupContainerListener {
        void OnClose(PopupContainerWithArrow popupContainer);
    }

    public interface PopupItemDragHandler extends View.OnLongClickListener, View.OnTouchListener {
    }

    @Override // com.android.launcher3.DragSource
    public float getIntrinsicIconScaleFactor() {
        return 1.0f;
    }

    @Override // com.android.launcher3.AbstractFloatingView
    protected boolean isOfType(int type) {
        return (type & 2) != 0;
    }

    @Override // com.android.launcher3.DragSource
    public void onFlingToDeleteCompleted() {
    }

    @Override // com.android.launcher3.DragSource
    public boolean supportsAppInfoDropTarget() {
        return true;
    }

    @Override // com.android.launcher3.DragSource
    public boolean supportsDeleteDropTarget() {
        return false;
    }

    @Override // com.android.launcher3.DragSource
    public boolean supportsFlingToDelete() {
        return false;
    }

    public PopupContainerWithArrow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mShortcuts = new ArrayList();
        this.mTempRect = new Rect();
        this.mInterceptTouchDown = new PointF();
        this.mIsCenterAligned = false;
        this.mMovedFromCenter = 0;
        this.mListeners = new ArrayList<>();
        Launcher launcher = Launcher.getLauncher(context);
        this.mLauncher = launcher;
        this.mStartDragThreshold = getResources().getDimensionPixelSize(R.dimen.deep_shortcuts_start_drag_threshold);
        this.mAccessibilityDelegate = new ShortcutMenuAccessibilityDelegate(launcher);
        this.mIsRtl = Utilities.isRtl(getResources());
    }

    public PopupContainerWithArrow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PopupContainerWithArrow(Context context) {
        this(context, null, 0);
    }

    /* JADX DEBUG: Method merged with bridge method: getAccessibilityDelegate()Landroid/view/View$AccessibilityDelegate; */
    @Override // android.view.View
    public LauncherAccessibilityDelegate getAccessibilityDelegate() {
        return this.mAccessibilityDelegate;
    }

    @Override // com.android.launcher3.AbstractFloatingView
    public void logActionCommand(int command) {
        this.mLauncher.getUserEventDispatcher().logActionCommand(command, this.mOriginalIcon, 9);
    }

    public View.OnClickListener getItemClickListener() {
        return new View.OnClickListener() { // from class: com.android.launcher3.popup.-$$Lambda$PopupContainerWithArrow$7Yg3n8A4ACeWnZ1lpcmp1SfSlec
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$getItemClickListener$0$PopupContainerWithArrow(view);
            }
        };
    }

    public /* synthetic */ void lambda$getItemClickListener$0$PopupContainerWithArrow(View view) {
        this.mLauncher.getItemOnClickListener().onClick(view);
        close(true);
    }

    public PopupItemDragHandler getItemDragHandler() {
        return this.mPopupItemDragHandler;
    }

    @Override // com.android.launcher3.util.TouchController
    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() != 0) {
            return false;
        }
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        if (dragLayer.isEventOverView(this, ev)) {
            return false;
        }
        this.mLauncher.getUserEventDispatcher().logActionTapOutside(LoggerUtils.newContainerTarget(9));
        close(true);
        BubbleTextView bubbleTextView = this.mOriginalIcon;
        return bubbleTextView == null || !dragLayer.isEventOverView(bubbleTextView, ev);
    }

    public static boolean canShow(View icon, ItemInfo item) {
        return (icon instanceof BubbleTextView) && ShortcutUtil.supportsShortcuts(item);
    }

    public static PopupContainerWithArrow showForIcon(BubbleTextView icon) {
        final Launcher launcher = Launcher.getLauncher(icon.getContext());
        if (getOpen(launcher) != null) {
            icon.clearFocus();
            return null;
        }
        final ItemInfo itemInfo = (ItemInfo) icon.getTag();
        if (!canShow(icon, itemInfo) && (!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || itemInfo.itemType != 6)) {
            return null;
        }
        PopupDataProvider popupDataProvider = launcher.getPopupDataProvider();
        List<String> shortcutIdsForItem = popupDataProvider.getShortcutIdsForItem(itemInfo);
        List<NotificationKeyData> notificationKeysForItem = popupDataProvider.getNotificationKeysForItem(itemInfo);
        String[] stringArray = launcher.getResources().getStringArray(R.array.exclude_noti_badge_and_popup);
        int length = stringArray.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            if (itemInfo.getTargetComponent().getClassName().equals(stringArray[i])) {
                notificationKeysForItem = Collections.EMPTY_LIST;
                break;
            }
            i++;
        }
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && itemInfo.itemType == 6) {
            shortcutIdsForItem.clear();
            notificationKeysForItem.clear();
        }
        PopupContainerWithArrow popupContainerWithArrow = (PopupContainerWithArrow) launcher.getLayoutInflater().inflate(R.layout.popup_container, (ViewGroup) launcher.getDragLayer(), false);
        popupContainerWithArrow.setVisibility(4);
        launcher.getDragLayer().addView(popupContainerWithArrow);
        popupContainerWithArrow.configureForLauncher(launcher);
        popupContainerWithArrow.populateAndShow(icon, popupDataProvider.getShortcutIdsForItem(itemInfo), popupDataProvider.getNotificationKeysForItem(itemInfo), (List) launcher.getSupportedShortcuts().map(new Function() { // from class: com.android.launcher3.popup.-$$Lambda$PopupContainerWithArrow$aa5Cn0_KAQNkFgmVh_mge9CPGzk
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((SystemShortcut.Factory) obj).getShortcut(launcher, itemInfo);
            }
        }).filter($$Lambda$PopupContainerWithArrow$5DTpxP45pqMGaPreGfSqTyTDkt0.INSTANCE).collect(Collectors.toList()), null);
        launcher.refreshAndBindWidgetsForPackageUser(PackageUserKey.fromItemInfo(itemInfo));
        return popupContainerWithArrow;
    }

    public static PopupContainerWithArrow showForWidget(LauncherAppWidgetHostView widget, boolean showSettingIcon) {
        final Launcher launcher = Launcher.getLauncher(widget.getContext());
        if (getOpen(launcher) != null) {
            widget.clearFocus();
            return null;
        }
        final ItemInfo itemInfo = (ItemInfo) widget.getTag();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        PopupContainerWithArrow popupContainerWithArrow = (PopupContainerWithArrow) launcher.getLayoutInflater().inflate(R.layout.popup_container, (ViewGroup) launcher.getDragLayer(), false);
        popupContainerWithArrow.setVisibility(4);
        launcher.getDragLayer().addView(popupContainerWithArrow);
        popupContainerWithArrow.configureForLauncher(launcher);
        popupContainerWithArrow.populateAndShow(null, arrayList, arrayList2, (List) launcher.getSupportedShortcutsForWidget(showSettingIcon).map(new Function() { // from class: com.android.launcher3.popup.-$$Lambda$PopupContainerWithArrow$4BTapKLMMh5tPPgD6zg4fGMNaiA
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((SystemShortcut.Factory) obj).getShortcut(launcher, itemInfo);
            }
        }).filter($$Lambda$PopupContainerWithArrow$5DTpxP45pqMGaPreGfSqTyTDkt0.INSTANCE).collect(Collectors.toList()), widget);
        launcher.refreshAndBindWidgetsForPackageUser(PackageUserKey.fromWidgetInfo(widget.getAppWidgetInfo()));
        return popupContainerWithArrow;
    }

    private void configureForLauncher(Launcher launcher) {
        this.mPopupItemDragHandler = new LauncherPopupItemDragHandler(launcher, this);
        this.mAccessibilityDelegate = new ShortcutMenuAccessibilityDelegate(launcher);
        launcher.getDragController().addDragListener(this);
    }

    public void populateAndShow(final BubbleTextView originalIcon, final List<String> shortcutIds, final List<NotificationKeyData> notificationKeys, List<SystemShortcut> systemShortcuts, final LauncherAppWidgetHostView originalWidget) {
        ItemInfo itemInfo;
        AppWidgetProviderInfo appWidgetInfo;
        List<DeepShortcutView> deepShortcutViews;
        List<View> systemShortcutViews;
        int dimensionPixelSize;
        Resources resources = getResources();
        int dimensionPixelSize2 = com.lge.launcher3.util.Utilities.isLGUI10_0() ? resources.getDimensionPixelSize(R.dimen.popup_arrow_width_ux10_0) : resources.getDimensionPixelSize(R.dimen.popup_arrow_width);
        int dimensionPixelSize3 = com.lge.launcher3.util.Utilities.isLGUI10_0() ? resources.getDimensionPixelSize(R.dimen.popup_arrow_height_ux10_0) : resources.getDimensionPixelSize(R.dimen.popup_arrow_height);
        int dimensionPixelSize4 = resources.getDimensionPixelSize(R.dimen.popup_arrow_vertical_offset);
        this.mMovedFromCenter = 0;
        this.mNumNotifications = notificationKeys.size();
        this.mOriginalIcon = originalIcon;
        this.mOriginalWidget = originalWidget;
        PopupPopulator.Item[] itemsToPopulate = PopupPopulator.getItemsToPopulate(shortcutIds, notificationKeys, systemShortcuts);
        addDummyViews(itemsToPopulate, notificationKeys.size() > 1);
        calculateSystemShortcutWidth();
        if (com.lge.launcher3.util.Utilities.isLGUI10_0()) {
            setPadding(getPaddingLeft(), getResources().getDimensionPixelSize(R.dimen.deep_shortcuts_elevation_padding_bottom_ux10_0), getPaddingRight(), 0);
        }
        measure(0, 0);
        if (originalIcon != null) {
            orientAboutIcon(originalIcon, dimensionPixelSize3 + dimensionPixelSize4);
        } else {
            orientAboutWidget(originalWidget, dimensionPixelSize3 + dimensionPixelSize4);
        }
        boolean z = this.mIsAboveIcon;
        if (z) {
            removeAllViews();
            this.mNotificationItemView = null;
            this.mShortcutsItemView = null;
            this.mSystemShortcutsItemView = null;
            addDummyViews(PopupPopulator.reverseItems(itemsToPopulate), notificationKeys.size() > 1);
            calculateSystemShortcutWidth();
            if (com.lge.launcher3.util.Utilities.isLGUI10_0()) {
                setPadding(getPaddingLeft(), 0, getPaddingRight(), getResources().getDimensionPixelSize(R.dimen.deep_shortcuts_elevation_padding_bottom_ux10_0));
            }
            measure(0, 0);
            if (originalIcon != null) {
                orientAboutIcon(originalIcon, dimensionPixelSize3 + dimensionPixelSize4);
            } else {
                orientAboutWidget(originalWidget, dimensionPixelSize3 + dimensionPixelSize4);
            }
        }
        if (originalIcon != null) {
            itemInfo = (ItemInfo) originalIcon.getTag();
            appWidgetInfo = null;
        } else {
            itemInfo = (ItemInfo) originalWidget.getTag();
            appWidgetInfo = originalWidget.getAppWidgetInfo();
        }
        ShortcutsItemView shortcutsItemView = this.mShortcutsItemView;
        if (shortcutsItemView == null) {
            deepShortcutViews = Collections.EMPTY_LIST;
        } else {
            deepShortcutViews = shortcutsItemView.getDeepShortcutViews(z);
        }
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || com.lge.launcher3.util.Utilities.isLGUI10_0()) {
            ShortcutsItemView shortcutsItemView2 = this.mSystemShortcutsItemView;
            if (shortcutsItemView2 == null) {
                systemShortcutViews = Collections.EMPTY_LIST;
            } else {
                systemShortcutViews = shortcutsItemView2.getSystemShortcutViews(z);
            }
        } else {
            ShortcutsItemView shortcutsItemView3 = this.mShortcutsItemView;
            if (shortcutsItemView3 == null) {
                systemShortcutViews = Collections.EMPTY_LIST;
            } else {
                systemShortcutViews = shortcutsItemView3.getSystemShortcutViews(z);
            }
        }
        List<View> list = systemShortcutViews;
        if (this.mNotificationItemView != null) {
            updateNotificationHeader();
        }
        int size = deepShortcutViews.size() + list.size();
        int size2 = notificationKeys.size();
        if (originalIcon == null || originalIcon.getContentDescription() == null) {
            if (originalWidget != null && originalWidget.getContentDescription() != null) {
                if (size2 == 0) {
                    setContentDescription(getContext().getString(R.string.shortcuts_menu_description, Integer.valueOf(size), originalWidget.getContentDescription().toString()));
                } else {
                    setContentDescription(getContext().getString(R.string.shortcuts_menu_with_notifications_description, Integer.valueOf(size), Integer.valueOf(size2), originalWidget.getContentDescription().toString()));
                }
            }
        } else if (size2 == 0) {
            setContentDescription(getContext().getString(R.string.shortcuts_menu_description, Integer.valueOf(size), originalIcon.getContentDescription().toString()));
        } else {
            setContentDescription(getContext().getString(R.string.shortcuts_menu_with_notifications_description, Integer.valueOf(size), Integer.valueOf(size2), originalIcon.getContentDescription().toString()));
        }
        boolean value = LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue();
        int i = R.dimen.popup_arrow_horizontal_offset_end;
        if (value && !this.mLauncher.isAllAppsVisible()) {
            if (isAlignedWithStart()) {
                i = R.dimen.popup_arrow_horizontal_offset_start_swivel;
            }
            dimensionPixelSize = resources.getDimensionPixelSize(i);
        } else if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && this.mLauncher.isAllAppsVisible() && !OrientationUtils.isPortrait(this.mLauncher.getApplicationContext())) {
            dimensionPixelSize = resources.getDimensionPixelSize(isAlignedWithStart() ? R.dimen.popup_arrow_horizontal_offset_start_swivel_app_drawer : R.dimen.popup_arrow_horizontal_offset_end_swivel_app_drawer);
        } else if (com.lge.launcher3.util.Utilities.isLGUI10_0()) {
            if (this.mIsCenterAligned) {
                dimensionPixelSize = ((getMeasuredWidth() / 2) - dimensionPixelSize2) + resources.getDimensionPixelSize(R.dimen.popup_arrow_horizontal_offset_center_ux10_0) + this.mMovedFromCenter;
            } else if (this.mLauncher.getDeviceProfile().isLandscape) {
                dimensionPixelSize = resources.getDimensionPixelSize(isAlignedWithStart() ? R.dimen.popup_arrow_horizontal_offset_start_ux10_0_land : R.dimen.popup_arrow_horizontal_offset_end_ux10_0_land);
            } else {
                dimensionPixelSize = resources.getDimensionPixelSize(isAlignedWithStart() ? R.dimen.popup_arrow_horizontal_offset_start_ux10_0 : R.dimen.popup_arrow_horizontal_offset_end_ux10_0);
            }
        } else {
            if (isAlignedWithStart()) {
                i = R.dimen.popup_arrow_horizontal_offset_start;
            }
            dimensionPixelSize = resources.getDimensionPixelSize(i);
        }
        View viewAddArrowView = addArrowView(dimensionPixelSize, dimensionPixelSize4, dimensionPixelSize2, dimensionPixelSize3);
        this.mArrow = viewAddArrowView;
        viewAddArrowView.setPivotX(dimensionPixelSize2 / 2);
        this.mArrow.setPivotY(this.mIsAboveIcon ? 0.0f : dimensionPixelSize3);
        animateOpen();
        new Handler(LauncherModel.getWorkerLooper()).postAtFrontOfQueue(PopupPopulator.createUpdateRunnable(this.mLauncher, itemInfo, new Handler(Looper.getMainLooper()), this, shortcutIds, deepShortcutViews, notificationKeys, this.mNotificationItemView, systemShortcuts, list, appWidgetInfo));
    }

    @Override // com.android.launcher3.popup.ArrowPopup
    protected void getTargetObjectLocation(Rect outPos) {
        int height;
        getPopupContainer().getDescendantRectRelativeToSelf(this.mOriginalIcon, outPos);
        outPos.top += this.mOriginalIcon.getPaddingTop();
        outPos.left += this.mOriginalIcon.getPaddingLeft();
        outPos.right -= this.mOriginalIcon.getPaddingRight();
        int i = outPos.top;
        if (this.mOriginalIcon.getIcon() != null) {
            height = this.mOriginalIcon.getIcon().getBounds().height();
        } else {
            height = this.mOriginalIcon.getHeight();
        }
        outPos.bottom = i + height;
    }

    public void applyNotificationInfos(List<NotificationInfo> notificationInfos) {
        NotificationItemView notificationItemView = this.mNotificationItemView;
        if (notificationItemView != null) {
            notificationItemView.applyNotificationInfos(notificationInfos);
        }
    }

    public View getOriginalIcon() {
        return this.mOriginalIcon;
    }

    public View getOriginalWidget() {
        return this.mOriginalWidget;
    }

    /* JADX WARN: Removed duplicated region for block: B:51:0x00ca  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void addDummyViews(com.android.launcher3.popup.PopupPopulator.Item[] r13, boolean r14) {
        /*
            r12 = this;
            android.content.res.Resources r0 = r12.getResources()
            boolean r1 = com.lge.launcher3.util.Utilities.isLGUI10_0()
            if (r1 == 0) goto L12
            r1 = 2131166156(0x7f0703cc, float:1.794655E38)
            int r1 = r0.getDimensionPixelSize(r1)
            goto L19
        L12:
            r1 = 2131166155(0x7f0703cb, float:1.7946547E38)
            int r1 = r0.getDimensionPixelSize(r1)
        L19:
            com.android.launcher3.Launcher r2 = r12.mLauncher
            android.view.LayoutInflater r2 = r2.getLayoutInflater()
            int r3 = r13.length
            r4 = 0
            r5 = r4
        L22:
            if (r5 >= r3) goto L15b
            r6 = r13[r5]
            int r7 = r3 + (-1)
            if (r5 >= r7) goto L2f
            int r7 = r5 + 1
            r7 = r13[r7]
            goto L30
        L2f:
            r7 = 0
        L30:
            int r8 = r6.layoutId
            android.view.View r8 = r2.inflate(r8, r12, r4)
            com.android.launcher3.popup.PopupPopulator$Item r9 = com.android.launcher3.popup.PopupPopulator.Item.NOTIFICATION
            if (r6 == r9) goto L69
            com.lge.launcher3.util.LGHomeFeature$Config r9 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT
            boolean r9 = r9.getValue()
            if (r9 != 0) goto L48
            boolean r9 = com.lge.launcher3.util.Utilities.isLGUI10_0()
            if (r9 == 0) goto L4d
        L48:
            com.android.launcher3.popup.PopupPopulator$Item r9 = com.android.launcher3.popup.PopupPopulator.Item.NOTIFICATION_SWIVEL
            if (r6 != r9) goto L4d
            goto L69
        L4d:
            com.android.launcher3.popup.PopupPopulator$Item r9 = com.android.launcher3.popup.PopupPopulator.Item.SHORTCUT
            if (r6 == r9) goto L63
            com.lge.launcher3.util.LGHomeFeature$Config r9 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT
            boolean r9 = r9.getValue()
            if (r9 != 0) goto L5f
            boolean r9 = com.lge.launcher3.util.Utilities.isLGUI10_0()
            if (r9 == 0) goto Lb0
        L5f:
            com.android.launcher3.popup.PopupPopulator$Item r9 = com.android.launcher3.popup.PopupPopulator.Item.SHORTCUT_SWIVEL
            if (r6 != r9) goto Lb0
        L63:
            com.android.launcher3.accessibility.LauncherAccessibilityDelegate r9 = r12.mAccessibilityDelegate
            r8.setAccessibilityDelegate(r9)
            goto Lb0
        L69:
            r9 = r8
            com.android.launcher3.notification.NotificationItemView r9 = (com.android.launcher3.notification.NotificationItemView) r9
            r12.mNotificationItemView = r9
            if (r14 == 0) goto L78
            r9 = 2131166033(0x7f070351, float:1.79463E38)
            int r9 = r0.getDimensionPixelSize(r9)
            goto L79
        L78:
            r9 = r4
        L79:
            if (r14 == 0) goto L98
            com.lge.launcher3.util.LGHomeFeature$Config r10 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT
            boolean r10 = r10.getValue()
            if (r10 == 0) goto L8b
            r9 = 2131166034(0x7f070352, float:1.7946302E38)
            int r9 = r0.getDimensionPixelSize(r9)
            goto L98
        L8b:
            boolean r10 = com.lge.launcher3.util.Utilities.isLGUI10_0()
            if (r10 == 0) goto L98
            r9 = 2131166035(0x7f070353, float:1.7946304E38)
            int r9 = r0.getDimensionPixelSize(r9)
        L98:
            r10 = 2131296527(0x7f09010f, float:1.8210973E38)
            android.view.View r10 = r8.findViewById(r10)
            android.view.ViewGroup$LayoutParams r10 = r10.getLayoutParams()
            r10.height = r9
            com.android.launcher3.notification.NotificationItemView r9 = r12.mNotificationItemView
            com.android.launcher3.notification.NotificationMainView r9 = r9.getMainView()
            com.android.launcher3.accessibility.LauncherAccessibilityDelegate r10 = r12.mAccessibilityDelegate
            r9.setAccessibilityDelegate(r10)
        Lb0:
            com.lge.launcher3.util.LGHomeFeature$Config r9 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT
            boolean r9 = r9.getValue()
            r10 = 1
            if (r9 != 0) goto Lcc
            boolean r9 = com.lge.launcher3.util.Utilities.isLGUI10_0()
            if (r9 == 0) goto Lc0
            goto Lcc
        Lc0:
            if (r7 == 0) goto Lca
            boolean r9 = r6.isShortcut
            boolean r7 = r7.isShortcut
            r7 = r7 ^ r9
            if (r7 == 0) goto Lca
            goto Ld0
        Lca:
            r10 = r4
            goto Ld0
        Lcc:
            if (r7 == 0) goto Lca
            if (r6 == r7) goto Lca
        Ld0:
            boolean r7 = r6.isShortcut
            if (r7 == 0) goto L14a
            com.lge.launcher3.util.LGHomeFeature$Config r7 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT
            boolean r7 = r7.getValue()
            r9 = 2131493083(0x7f0c00db, float:1.8609636E38)
            r11 = 2131493082(0x7f0c00da, float:1.8609634E38)
            if (r7 != 0) goto Le8
            boolean r7 = com.lge.launcher3.util.Utilities.isLGUI10_0()
            if (r7 == 0) goto L115
        Le8:
            com.android.launcher3.popup.PopupPopulator$Item r7 = com.android.launcher3.popup.PopupPopulator.Item.SYSTEM_SHORTCUT_ICON_SWIVEL
            if (r6 != r7) goto L115
            com.android.launcher3.shortcuts.ShortcutsItemView r7 = r12.mSystemShortcutsItemView
            if (r7 != 0) goto L103
            boolean r7 = com.lge.launcher3.util.Utilities.isLGUI10_0()
            if (r7 == 0) goto Lf7
            goto Lf8
        Lf7:
            r9 = r11
        Lf8:
            android.view.View r7 = r2.inflate(r9, r12, r4)
            com.android.launcher3.shortcuts.ShortcutsItemView r7 = (com.android.launcher3.shortcuts.ShortcutsItemView) r7
            r12.mSystemShortcutsItemView = r7
            r12.addView(r7)
        L103:
            com.android.launcher3.shortcuts.ShortcutsItemView r7 = r12.mSystemShortcutsItemView
            r7.addShortcutView(r8, r6)
            if (r10 == 0) goto L157
            com.android.launcher3.shortcuts.ShortcutsItemView r6 = r12.mSystemShortcutsItemView
            android.view.ViewGroup$LayoutParams r6 = r6.getLayoutParams()
            android.widget.LinearLayout$LayoutParams r6 = (android.widget.LinearLayout.LayoutParams) r6
            r6.bottomMargin = r1
            goto L157
        L115:
            com.android.launcher3.shortcuts.ShortcutsItemView r7 = r12.mShortcutsItemView
            if (r7 != 0) goto L138
            com.lge.launcher3.util.LGHomeFeature$Config r7 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT
            boolean r7 = r7.getValue()
            if (r7 == 0) goto L123
            r9 = r11
            goto L12d
        L123:
            boolean r7 = com.lge.launcher3.util.Utilities.isLGUI10_0()
            if (r7 == 0) goto L12a
            goto L12d
        L12a:
            r9 = 2131493081(0x7f0c00d9, float:1.8609632E38)
        L12d:
            android.view.View r7 = r2.inflate(r9, r12, r4)
            com.android.launcher3.shortcuts.ShortcutsItemView r7 = (com.android.launcher3.shortcuts.ShortcutsItemView) r7
            r12.mShortcutsItemView = r7
            r12.addView(r7)
        L138:
            com.android.launcher3.shortcuts.ShortcutsItemView r7 = r12.mShortcutsItemView
            r7.addShortcutView(r8, r6)
            if (r10 == 0) goto L157
            com.android.launcher3.shortcuts.ShortcutsItemView r6 = r12.mShortcutsItemView
            android.view.ViewGroup$LayoutParams r6 = r6.getLayoutParams()
            android.widget.LinearLayout$LayoutParams r6 = (android.widget.LinearLayout.LayoutParams) r6
            r6.bottomMargin = r1
            goto L157
        L14a:
            r12.addView(r8)
            if (r10 == 0) goto L157
            android.view.ViewGroup$LayoutParams r6 = r8.getLayoutParams()
            android.widget.LinearLayout$LayoutParams r6 = (android.widget.LinearLayout.LayoutParams) r6
            r6.bottomMargin = r1
        L157:
            int r5 = r5 + 1
            goto L22
        L15b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.popup.PopupContainerWithArrow.addDummyViews(com.android.launcher3.popup.PopupPopulator$Item[], boolean):void");
    }

    protected PopupItemView getItemViewAt(int index) {
        if (!this.mIsAboveIcon) {
            index++;
        }
        return (PopupItemView) getChildAt(index);
    }

    protected int getItemCount() {
        return getChildCount() - 1;
    }

    private void animateOpen() {
        long j;
        int i;
        setVisibility(0);
        this.mIsOpen = true;
        AnimatorSet animatorSetCreateAnimatorSet = LauncherAnimUtils.createAnimatorSet();
        int itemCount = getItemCount();
        long integer = getResources().getInteger(R.integer.config_deepShortcutOpenDuration);
        long integer2 = getResources().getInteger(R.integer.config_deepShortcutArrowOpenDuration);
        long j2 = integer - integer2;
        long integer3 = getResources().getInteger(R.integer.config_deepShortcutOpenStagger);
        LogAccelerateInterpolator logAccelerateInterpolator = new LogAccelerateInterpolator(100, 0);
        DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
        int i2 = 0;
        while (i2 < itemCount) {
            final PopupItemView itemViewAt = getItemViewAt(i2);
            long j3 = integer2;
            itemViewAt.setVisibility(4);
            itemViewAt.setAlpha(0.0f);
            Animator animatorCreateOpenAnimation = itemViewAt.createOpenAnimation(this.mIsAboveIcon, this.mIsLeftAligned);
            animatorCreateOpenAnimation.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.popup.PopupContainerWithArrow.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    itemViewAt.setVisibility(0);
                }
            });
            animatorCreateOpenAnimation.setDuration(integer);
            if (this.mIsAboveIcon) {
                i = (itemCount - i2) - 1;
                j = j2;
            } else {
                j = j2;
                i = i2;
            }
            animatorCreateOpenAnimation.setStartDelay(((long) i) * integer3);
            animatorCreateOpenAnimation.setInterpolator(decelerateInterpolator);
            animatorSetCreateAnimatorSet.play(animatorCreateOpenAnimation);
            ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(itemViewAt, (Property<PopupItemView, Float>) View.ALPHA, 1.0f);
            objectAnimatorOfFloat.setInterpolator(logAccelerateInterpolator);
            long j4 = j;
            objectAnimatorOfFloat.setDuration(j4);
            animatorSetCreateAnimatorSet.play(objectAnimatorOfFloat);
            i2++;
            j2 = j4;
            integer2 = j3;
        }
        animatorSetCreateAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.popup.PopupContainerWithArrow.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PopupContainerWithArrow.this.mOpenCloseAnimator = null;
                PopupContainerWithArrow popupContainerWithArrow = PopupContainerWithArrow.this;
                Utilities.sendCustomAccessibilityEvent(popupContainerWithArrow, 32, popupContainerWithArrow.getContext().getString(R.string.action_deep_shortcut));
            }
        });
        this.mArrow.setScaleX(0.0f);
        this.mArrow.setScaleY(0.0f);
        ObjectAnimator duration = createArrowScaleAnim(1.0f).setDuration(integer2);
        duration.setStartDelay(j2);
        animatorSetCreateAnimatorSet.play(duration);
        this.mOpenCloseAnimator = animatorSetCreateAnimatorSet;
        animatorSetCreateAnimatorSet.start();
    }

    /* JADX WARN: Removed duplicated region for block: B:109:0x023b  */
    /* JADX WARN: Removed duplicated region for block: B:120:0x0290  */
    /* JADX WARN: Removed duplicated region for block: B:124:0x029e  */
    /* JADX WARN: Removed duplicated region for block: B:135:0x02bb  */
    /* JADX WARN: Removed duplicated region for block: B:138:0x02cc  */
    /* JADX WARN: Removed duplicated region for block: B:163:0x039e  */
    /* JADX WARN: Removed duplicated region for block: B:166:0x03a8  */
    /* JADX WARN: Removed duplicated region for block: B:168:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:81:0x01b7  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x01bf  */
    /* JADX WARN: Removed duplicated region for block: B:85:0x01c6  */
    /* JADX WARN: Removed duplicated region for block: B:88:0x01e5  */
    /* JADX WARN: Removed duplicated region for block: B:89:0x01e7  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x01ec  */
    /* JADX WARN: Removed duplicated region for block: B:98:0x020b  */
    /* JADX WARN: Removed duplicated region for block: B:99:0x020f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void orientAboutIcon(com.android.launcher3.BubbleTextView r18, int r19) {
        /*
            r17 = this;
            r0 = r17
            int r1 = r17.getMeasuredWidth()
            int r2 = r17.getMeasuredHeight()
            int r2 = r2 + r19
            com.android.launcher3.Launcher r3 = r0.mLauncher
            com.android.launcher3.dragndrop.DragLayer r3 = r3.getDragLayer()
            android.graphics.Rect r4 = r0.mTempRect
            r5 = r18
            r3.getDescendantRectRelativeToSelf(r5, r4)
            android.graphics.Rect r4 = r3.getInsets()
            boolean r6 = r18.isLayoutHorizontal()
            if (r6 == 0) goto L41
            android.graphics.Rect r6 = r0.mTempRect
            int r7 = r6.left
            int r8 = r18.getPaddingLeft()
            int r7 = r7 + r8
            r6.left = r7
            android.graphics.Rect r6 = r0.mTempRect
            int r7 = r6.left
            android.graphics.drawable.Drawable r8 = r18.getIcon()
            android.graphics.Rect r8 = r8.getBounds()
            int r8 = r8.width()
            int r7 = r7 + r8
            r6.right = r7
        L41:
            android.graphics.Rect r6 = r0.mTempRect
            int r6 = r6.left
            android.graphics.Rect r7 = r0.mTempRect
            int r7 = r7.right
            int r7 = r7 - r1
            java.lang.Object r8 = r18.getTag()
            boolean r8 = r8 instanceof com.android.launcher3.ShortcutInfo
            r9 = 0
            r10 = 1
            if (r8 == 0) goto Lbb
            java.lang.Object r8 = r18.getTag()
            com.android.launcher3.ShortcutInfo r8 = (com.android.launcher3.ShortcutInfo) r8
            long r11 = r8.container
            r13 = -101(0xffffffffffffff9b, double:NaN)
            int r8 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r8 != 0) goto Lbb
            android.graphics.Rect r8 = r0.mTempRect
            int r8 = r8.left
            android.graphics.Rect r11 = r0.mTempRect
            int r11 = r11.right
            int r8 = r8 + r11
            com.android.launcher3.Launcher r11 = r0.mLauncher
            com.android.launcher3.DeviceProfile r11 = r11.getDeviceProfile()
            int r11 = r11.hotseatIconSizePx
            int r8 = r8 - r11
            int r8 = r8 / 2
            int r8 = r8 + r1
            int r11 = r3.getRight()
            if (r8 >= r11) goto L7f
            r8 = r10
            goto L80
        L7f:
            r8 = r9
        L80:
            android.graphics.Rect r11 = r0.mTempRect
            int r11 = r11.left
            android.graphics.Rect r12 = r0.mTempRect
            int r12 = r12.right
            int r11 = r11 + r12
            com.android.launcher3.Launcher r12 = r0.mLauncher
            com.android.launcher3.DeviceProfile r12 = r12.getDeviceProfile()
            int r12 = r12.hotseatIconSizePx
            int r11 = r11 + r12
            int r11 = r11 / 2
            int r11 = r11 - r1
            int r12 = r3.getLeft()
            if (r11 <= r12) goto L9d
            r11 = r10
            goto L9e
        L9d:
            r11 = r9
        L9e:
            if (r8 == 0) goto La9
            boolean r8 = r0.mIsRtl
            if (r8 == 0) goto La7
            if (r11 == 0) goto La7
            goto La9
        La7:
            r8 = r6
            goto Laa
        La9:
            r8 = r7
        Laa:
            if (r8 != r6) goto Lae
            r11 = r10
            goto Laf
        Lae:
            r11 = r9
        Laf:
            r0.mIsLeftAligned = r11
            boolean r11 = r0.mIsRtl
            if (r11 == 0) goto L13e
            int r11 = r3.getWidth()
            goto L13c
        Lbb:
            int r8 = r17.getPaddingRight()
            int r8 = r1 - r8
            int r11 = r17.getPaddingLeft()
            int r8 = r8 - r11
            int r8 = r8 + r6
            int r11 = r4.left
            int r8 = r8 + r11
            int r11 = r3.getRight()
            int r12 = r4.right
            int r11 = r11 - r12
            if (r8 >= r11) goto Ld5
            r8 = r10
            goto Ld6
        Ld5:
            r8 = r9
        Ld6:
            int r11 = r3.getLeft()
            int r12 = r4.left
            int r11 = r11 + r12
            if (r7 <= r11) goto Le1
            r11 = r10
            goto Le2
        Le1:
            r11 = r9
        Le2:
            com.android.launcher3.Launcher r12 = r0.mLauncher
            com.android.launcher3.DeviceProfile r12 = r12.getDeviceProfile()
            boolean r12 = r12.isLandscape
            if (r12 == 0) goto L121
            android.graphics.drawable.Drawable r8 = r18.getIcon()
            android.graphics.Rect r8 = r8.getBounds()
            int r8 = r8.width()
            int r8 = r1 - r8
            int r8 = r8 / 2
            int r11 = r6 + r8
            int r11 = r11 + r1
            int r12 = r4.left
            int r11 = r11 + r12
            int r12 = r3.getRight()
            int r13 = r4.right
            int r12 = r12 - r13
            if (r11 >= r12) goto L10d
            r11 = r10
            goto L10e
        L10d:
            r11 = r9
        L10e:
            int r8 = r7 - r8
            int r12 = r3.getLeft()
            int r13 = r4.left
            int r12 = r12 + r13
            if (r8 <= r12) goto L11b
            r8 = r10
            goto L11c
        L11b:
            r8 = r9
        L11c:
            r16 = r11
            r11 = r8
            r8 = r16
        L121:
            if (r8 == 0) goto L12c
            boolean r8 = r0.mIsRtl
            if (r8 == 0) goto L12a
            if (r11 == 0) goto L12a
            goto L12c
        L12a:
            r8 = r6
            goto L12d
        L12c:
            r8 = r7
        L12d:
            if (r8 != r6) goto L131
            r11 = r10
            goto L132
        L131:
            r11 = r9
        L132:
            r0.mIsLeftAligned = r11
            boolean r11 = r0.mIsRtl
            if (r11 == 0) goto L13e
            int r11 = r3.getWidth()
        L13c:
            int r11 = r11 - r1
            int r8 = r8 - r11
        L13e:
            boolean r11 = r18.isLayoutHorizontal()
            if (r11 == 0) goto L14b
            android.graphics.Rect r11 = r0.mTempRect
            int r11 = r11.width()
            goto L160
        L14b:
            int r11 = r18.getWidth()
            int r12 = r18.getTotalPaddingLeft()
            int r11 = r11 - r12
            int r12 = r18.getTotalPaddingRight()
            int r11 = r11 - r12
            float r11 = (float) r11
            float r12 = r18.getScaleX()
            float r11 = r11 * r12
            int r11 = (int) r11
        L160:
            android.content.res.Resources r12 = r17.getResources()
            boolean r13 = r17.isAlignedWithStart()
            if (r13 == 0) goto L19d
            boolean r13 = r18.isLayoutHorizontal()
            if (r13 == 0) goto L18a
            boolean r12 = r0.mIsRtl
            if (r12 != 0) goto L17f
            android.graphics.drawable.Drawable r12 = r18.getIcon()
            android.graphics.Rect r12 = r12.getBounds()
            int r12 = r12.left
            goto L1b2
        L17f:
            android.graphics.drawable.Drawable r12 = r18.getIcon()
            android.graphics.Rect r12 = r12.getBounds()
            int r12 = r12.right
            goto L1b2
        L18a:
            r13 = 2131165472(0x7f070120, float:1.7945162E38)
            int r13 = r12.getDimensionPixelSize(r13)
            r14 = 2131166160(0x7f0703d0, float:1.7946558E38)
            int r12 = r12.getDimensionPixelSize(r14)
            int r14 = r11 / 2
            int r13 = r13 / 2
            goto L1af
        L19d:
            r13 = 2131165465(0x7f070119, float:1.7945148E38)
            int r13 = r12.getDimensionPixelSize(r13)
            r14 = 2131166157(0x7f0703cd, float:1.7946551E38)
            int r12 = r12.getDimensionPixelSize(r14)
            int r14 = r11 / 2
            int r13 = r13 / 2
        L1af:
            int r14 = r14 - r13
            int r12 = r14 - r12
        L1b2:
            boolean r13 = r0.mIsLeftAligned
            if (r13 == 0) goto L1b7
            goto L1b8
        L1b7:
            int r12 = -r12
        L1b8:
            int r8 = r8 + r12
            boolean r12 = r18.isLayoutHorizontal()
            if (r12 == 0) goto L1c6
            android.graphics.Rect r12 = r0.mTempRect
            int r12 = r12.height()
            goto L1d2
        L1c6:
            android.graphics.drawable.Drawable r12 = r18.getIcon()
            android.graphics.Rect r12 = r12.getBounds()
            int r12 = r12.height()
        L1d2:
            android.graphics.Rect r13 = r0.mTempRect
            int r13 = r13.top
            int r14 = r18.getPaddingTop()
            int r13 = r13 + r14
            int r13 = r13 - r2
            int r14 = r3.getTop()
            int r15 = r4.top
            int r14 = r14 + r15
            if (r13 <= r14) goto L1e7
            r14 = r10
            goto L1e8
        L1e7:
            r14 = r9
        L1e8:
            r0.mIsAboveIcon = r14
            if (r14 != 0) goto L207
            boolean r13 = r18.isLayoutHorizontal()
            if (r13 == 0) goto L1fd
            android.graphics.Rect r12 = r0.mTempRect
            int r12 = r12.bottom
            int r5 = r18.getPaddingBottom()
            int r13 = r12 - r5
            goto L207
        L1fd:
            android.graphics.Rect r13 = r0.mTempRect
            int r13 = r13.top
            int r5 = r18.getPaddingTop()
            int r13 = r13 + r5
            int r13 = r13 + r12
        L207:
            boolean r5 = r0.mIsRtl
            if (r5 == 0) goto L20f
            int r5 = r4.right
            int r8 = r8 + r5
            goto L212
        L20f:
            int r5 = r4.left
            int r8 = r8 - r5
        L212:
            int r5 = r4.top
            int r13 = r13 - r5
            int r5 = r3.getTop()
            if (r13 < r5) goto L23b
            int r2 = r2 + r13
            int r5 = r4.top
            int r2 = r2 + r5
            int r5 = r3.getBottom()
            if (r2 <= r5) goto L226
            goto L23b
        L226:
            android.content.res.Resources r2 = r17.getResources()
            r4 = 2131165478(0x7f070126, float:1.7945174E38)
            int r2 = r2.getDimensionPixelOffset(r4)
            boolean r4 = r0.mIsLeftAligned
            if (r4 == 0) goto L238
            int r8 = r8 - r2
            goto L2ad
        L238:
            int r8 = r8 + r2
            goto L2ad
        L23b:
            android.view.ViewGroup$LayoutParams r2 = r17.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            r5 = 16
            r2.gravity = r5
            int r6 = r6 + r11
            int r2 = r4.left
            int r6 = r6 - r2
            int r7 = r7 - r11
            int r2 = r4.left
            int r7 = r7 - r2
            com.lge.launcher3.util.LGHomeFeature$Config r2 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT
            boolean r2 = r2.getValue()
            if (r2 != 0) goto L25b
            boolean r2 = com.lge.launcher3.util.Utilities.isLGUI10_0()
            if (r2 == 0) goto L28c
        L25b:
            com.android.launcher3.Launcher r2 = r0.mLauncher
            boolean r2 = r2.isAllAppsVisible()
            if (r2 != r10) goto L28c
            com.android.launcher3.Launcher r2 = r0.mLauncher
            com.android.launcher3.Workspace r2 = r2.getWorkspace()
            com.android.launcher3.folder.Folder r2 = r2.getOpenFolder()
            if (r2 == 0) goto L28c
            android.content.Context r2 = r17.getContext()
            android.content.res.Resources r2 = r2.getResources()
            r4 = 2131166287(0x7f07044f, float:1.7946815E38)
            int r2 = r2.getDimensionPixelSize(r4)
            int r6 = r6 + r2
            android.content.Context r2 = r17.getContext()
            android.content.res.Resources r2 = r2.getResources()
            int r2 = r2.getDimensionPixelSize(r4)
            int r7 = r7 - r2
        L28c:
            boolean r2 = r0.mIsRtl
            if (r2 != 0) goto L29e
            int r2 = r6 + r1
            int r4 = r3.getRight()
            if (r2 >= r4) goto L29b
            r0.mIsLeftAligned = r10
            goto L2aa
        L29b:
            r0.mIsLeftAligned = r9
            goto L2a6
        L29e:
            int r2 = r3.getLeft()
            if (r7 <= r2) goto L2a8
            r0.mIsLeftAligned = r9
        L2a6:
            r8 = r7
            goto L2ab
        L2a8:
            r0.mIsLeftAligned = r10
        L2aa:
            r8 = r6
        L2ab:
            r0.mIsAboveIcon = r10
        L2ad:
            int r2 = r3.getLeft()
            if (r8 < r2) goto L2bb
            int r2 = r8 + r1
            int r4 = r3.getRight()
            if (r2 <= r4) goto L2c6
        L2bb:
            android.view.ViewGroup$LayoutParams r2 = r17.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r2 = (android.widget.FrameLayout.LayoutParams) r2
            int r4 = r2.gravity
            r4 = r4 | r10
            r2.gravity = r4
        L2c6:
            boolean r2 = com.lge.launcher3.util.Utilities.isLGUI10_0()
            if (r2 == 0) goto L390
            boolean r2 = r0.mIsRtl
            if (r2 == 0) goto L33d
            int r2 = r3.getLeft()
            if (r8 > r2) goto L2e0
            int r2 = r8 - r1
            int r4 = r3.getRight()
            int r4 = r4 * (-1)
            if (r2 >= r4) goto L390
        L2e0:
            r0.mIsCenterAligned = r10
            android.graphics.Rect r2 = r0.mTempRect
            int r2 = r2.left
            android.graphics.Rect r4 = r0.mTempRect
            int r4 = r4.right
            int r2 = r2 + r4
            int r2 = r2 / 2
            int r4 = r1 / 2
            int r2 = r2 - r4
            int r2 = r2 + r1
            int r4 = r3.getRight()
            int r8 = r2 - r4
            int r2 = r8 - r1
            int r4 = r3.getRight()
            int r4 = r4 * (-1)
            if (r2 >= r4) goto L31e
            int r4 = r3.getRight()
            int r4 = r4 * (-1)
            int r4 = r4 - r2
            int r2 = java.lang.Math.abs(r4)
            int r2 = -r2
            r0.mMovedFromCenter = r2
            r0.mIsLeftAligned = r10
            int r2 = r3.getLeft()
            int r2 = r2 + r1
            int r1 = r3.getRight()
        L31a:
            int r8 = r2 - r1
            goto L390
        L31e:
            int r2 = r3.getLeft()
            if (r8 <= r2) goto L390
            int r2 = r3.getLeft()
            int r2 = r2 - r8
            int r2 = java.lang.Math.abs(r2)
            int r2 = -r2
            r0.mMovedFromCenter = r2
            r0.mIsLeftAligned = r9
            int r2 = r3.getRight()
            int r2 = r2 - r1
            int r2 = r2 + r1
            int r1 = r3.getRight()
            goto L31a
        L33d:
            int r2 = r3.getLeft()
            if (r8 < r2) goto L34b
            int r2 = r8 + r1
            int r4 = r3.getRight()
            if (r2 <= r4) goto L390
        L34b:
            r0.mIsCenterAligned = r10
            android.graphics.Rect r2 = r0.mTempRect
            int r2 = r2.left
            android.graphics.Rect r4 = r0.mTempRect
            int r4 = r4.right
            int r2 = r2 + r4
            int r2 = r2 / 2
            int r4 = r1 / 2
            int r8 = r2 - r4
            int r2 = r3.getLeft()
            if (r8 >= r2) goto L375
            int r1 = r3.getLeft()
            int r1 = r1 - r8
            int r1 = java.lang.Math.abs(r1)
            int r1 = -r1
            r0.mMovedFromCenter = r1
            r0.mIsLeftAligned = r10
            int r8 = r3.getLeft()
            goto L390
        L375:
            int r2 = r8 + r1
            int r4 = r3.getRight()
            if (r2 <= r4) goto L390
            int r4 = r3.getRight()
            int r4 = r4 - r2
            int r2 = java.lang.Math.abs(r4)
            int r2 = -r2
            r0.mMovedFromCenter = r2
            r0.mIsLeftAligned = r9
            int r2 = r3.getRight()
            goto L31a
        L390:
            android.view.ViewGroup$LayoutParams r1 = r17.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r1 = r1.gravity
            boolean r2 = android.view.Gravity.isHorizontal(r1)
            if (r2 != 0) goto L3a2
            float r2 = (float) r8
            r0.setX(r2)
        L3a2:
            boolean r1 = android.view.Gravity.isVertical(r1)
            if (r1 != 0) goto L3ac
            float r1 = (float) r13
            r0.setY(r1)
        L3ac:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.popup.PopupContainerWithArrow.orientAboutIcon(com.android.launcher3.BubbleTextView, int):void");
    }

    private void orientAboutWidget(LauncherAppWidgetHostView widget, int arrowHeight) {
        int right;
        int right2;
        int right3;
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight() + arrowHeight;
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        dragLayer.getDescendantRectRelativeToSelf(widget, this.mTempRect);
        Rect insets = dragLayer.getInsets();
        int i = this.mTempRect.top - measuredHeight;
        boolean z = i > dragLayer.getTop() + insets.top;
        this.mIsAboveIcon = z;
        if (!z) {
            i = this.mTempRect.bottom;
        }
        int i2 = i - insets.top;
        if (this.mIsRtl) {
            this.mIsCenterAligned = true;
            right = ((((this.mTempRect.left + this.mTempRect.right) / 2) - (measuredWidth / 2)) + measuredWidth) - dragLayer.getRight();
            int i3 = right - measuredWidth;
            if (i3 < dragLayer.getRight() * (-1)) {
                this.mMovedFromCenter = -Math.abs((dragLayer.getRight() * (-1)) - i3);
                this.mIsLeftAligned = true;
                right2 = dragLayer.getLeft() + measuredWidth;
                right3 = dragLayer.getRight();
            } else if (right > dragLayer.getLeft()) {
                this.mMovedFromCenter = -Math.abs(dragLayer.getLeft() - right);
                this.mIsLeftAligned = false;
                right2 = (dragLayer.getRight() - measuredWidth) + measuredWidth;
                right3 = dragLayer.getRight();
            }
            right = right2 - right3;
        } else {
            this.mIsCenterAligned = true;
            right = ((this.mTempRect.left + this.mTempRect.right) / 2) - (measuredWidth / 2);
            if (right < dragLayer.getLeft()) {
                this.mMovedFromCenter = -Math.abs(dragLayer.getLeft() - right);
                this.mIsLeftAligned = true;
                right = dragLayer.getLeft();
            } else {
                int i4 = right + measuredWidth;
                if (i4 > dragLayer.getRight()) {
                    this.mMovedFromCenter = -Math.abs(dragLayer.getRight() - i4);
                    this.mIsLeftAligned = false;
                    right = dragLayer.getRight() - measuredWidth;
                }
            }
        }
        int i5 = ((FrameLayout.LayoutParams) getLayoutParams()).gravity;
        if (!Gravity.isHorizontal(i5)) {
            setX(right);
        }
        if (Gravity.isVertical(i5)) {
            return;
        }
        setY(i2);
    }

    @Override // com.android.launcher3.popup.ArrowPopup
    protected boolean isAlignedWithStart() {
        boolean z = this.mIsLeftAligned;
        return (z && !this.mIsRtl) || (!z && this.mIsRtl);
    }

    private View addArrowView(int horizontalOffset, int verticalOffset, int width, int height) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        if (this.mIsLeftAligned) {
            layoutParams.gravity = 3;
            layoutParams.leftMargin = horizontalOffset;
        } else {
            layoutParams.gravity = 5;
            layoutParams.rightMargin = horizontalOffset;
        }
        if (this.mIsAboveIcon) {
            layoutParams.topMargin = verticalOffset;
        } else {
            layoutParams.bottomMargin = verticalOffset;
        }
        View view = new View(getContext());
        if (Gravity.isVertical(((FrameLayout.LayoutParams) getLayoutParams()).gravity)) {
            view.setVisibility(4);
            if (com.lge.launcher3.util.Utilities.isLGUI10_0()) {
                setPadding(getPaddingLeft(), 0, getPaddingRight(), 0);
            }
        } else {
            ShapeDrawable shapeDrawable = new ShapeDrawable(TriangleShape.create(width, height, !this.mIsAboveIcon));
            Paint paint = shapeDrawable.getPaint();
            paint.setColor(Themes.getAttrColor(getContext(), R.attr.popupColorPrimary));
            paint.setPathEffect(new CornerPathEffect(com.lge.launcher3.util.Utilities.isLGUI10_0() ? 0 : getResources().getDimensionPixelSize(R.dimen.popup_arrow_corner_radius)));
            view.setBackground(shapeDrawable);
            view.setElevation(getElevation());
        }
        addView(view, this.mIsAboveIcon ? getChildCount() : 0, layoutParams);
        return view;
    }

    public DragOptions.PreDragCondition createPreDragCondition() {
        return new DragOptions.PreDragCondition() { // from class: com.android.launcher3.popup.PopupContainerWithArrow.3
            @Override // com.android.launcher3.dragndrop.DragOptions.PreDragCondition
            public boolean shouldStartDrag(double distanceDragged) {
                return distanceDragged > ((double) PopupContainerWithArrow.this.mStartDragThreshold);
            }

            @Override // com.android.launcher3.dragndrop.DragOptions.PreDragCondition
            public void onPreDragStart(DropTarget.DragObject dragObject) {
                if (PopupContainerWithArrow.this.mOriginalIcon != null) {
                    PopupContainerWithArrow.this.mOriginalIcon.setVisibility(4);
                }
            }

            @Override // com.android.launcher3.dragndrop.DragOptions.PreDragCondition
            public void onPreDragEnd(DropTarget.DragObject dragObject, boolean dragStarted) {
                if (dragStarted || PopupContainerWithArrow.this.mOriginalIcon == null) {
                    return;
                }
                PopupContainerWithArrow.this.mOriginalIcon.setVisibility(0);
                if ((PopupContainerWithArrow.this.mOriginalIcon instanceof BubbleTextView) && (dragObject.dragSource instanceof AllAppsPagedView)) {
                    PopupContainerWithArrow.this.mOriginalIcon.setItemInfo();
                }
                if (PopupContainerWithArrow.this.mIsAboveIcon || !PopupContainerWithArrow.this.isOpen() || PopupContainerWithArrow.this.mOriginalIcon.isLayoutHorizontal()) {
                    return;
                }
                PopupContainerWithArrow.this.mOriginalIcon.setTextVisibility(false);
            }
        };
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() != 0) {
            return Math.hypot((double) (this.mInterceptTouchDown.x - ev.getX()), (double) (this.mInterceptTouchDown.y - ev.getY())) > ((double) ViewConfiguration.get(getContext()).getScaledTouchSlop());
        }
        this.mInterceptTouchDown.set(ev.getX(), ev.getY());
        return false;
    }

    public void updateNotificationHeader(Set<PackageUserKey> updatedBadges) {
        BubbleTextView bubbleTextView = this.mOriginalIcon;
        if (bubbleTextView == null || !updatedBadges.contains(PackageUserKey.fromItemInfo((ItemInfo) bubbleTextView.getTag()))) {
            return;
        }
        updateNotificationHeader();
    }

    private void updateNotificationHeader() {
        BubbleTextView bubbleTextView = this.mOriginalIcon;
        if (bubbleTextView != null) {
            BadgeInfo badgeInfoForItem = this.mLauncher.getPopupDataProvider().getBadgeInfoForItem((ItemInfo) bubbleTextView.getTag());
            if (this.mNotificationItemView == null || badgeInfoForItem == null) {
                return;
            }
            this.mNotificationItemView.updateHeader(badgeInfoForItem.getNotificationCount(), this.mOriginalIcon.getBadgePalette());
        }
    }

    public void notificationFullRefresh(Map<PackageUserKey, BadgeInfo> updatedBadges) {
        List<NotificationKeyData> notificationKeysForItem;
        if (this.mNotificationItemView == null || this.mOriginalIcon == null) {
            return;
        }
        if ((updatedBadges == null || updatedBadges.isEmpty()) && (notificationKeysForItem = this.mLauncher.getPopupDataProvider().getNotificationKeysForItem((ItemInfo) this.mOriginalIcon.getTag())) != null && !notificationKeysForItem.isEmpty()) {
            LGLog.i(TAG, "notificationFullRefresh() notificationKeys.size() = " + notificationKeysForItem.size());
            this.mNotificationItemView.trimNotifications(NotificationKeyData.extractKeysOnly(notificationKeysForItem));
            return;
        }
        trimNotifications(updatedBadges);
    }

    public void trimNotifications(Map<PackageUserKey, BadgeInfo> updatedBadges) {
        BubbleTextView bubbleTextView;
        if (this.mNotificationItemView == null || (bubbleTextView = this.mOriginalIcon) == null) {
            return;
        }
        BadgeInfo badgeInfo = updatedBadges.get(PackageUserKey.fromItemInfo((ItemInfo) bubbleTextView.getTag()));
        if (badgeInfo == null || badgeInfo.getNotificationKeys().size() == 0) {
            AnimatorSet animatorSetCreateAnimatorSet = LauncherAnimUtils.createAnimatorSet();
            int integer = getResources().getInteger(R.integer.config_removeNotificationViewDuration);
            final int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.popup_items_spacing);
            animatorSetCreateAnimatorSet.play(reduceNotificationViewHeight(this.mNotificationItemView.getHeightMinusFooter() + dimensionPixelSize, integer));
            final View itemViewAt = this.mIsAboveIcon ? getItemViewAt(getItemCount() - 2) : this.mNotificationItemView;
            if (itemViewAt != null) {
                ValueAnimator duration = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(integer);
                duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.popup.PopupContainerWithArrow.4
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ((ViewGroup.MarginLayoutParams) itemViewAt.getLayoutParams()).bottomMargin = (int) (dimensionPixelSize * ((Float) valueAnimator.getAnimatedValue()).floatValue());
                    }
                });
                animatorSetCreateAnimatorSet.play(duration);
            }
            ObjectAnimator duration2 = ObjectAnimator.ofFloat(this.mNotificationItemView, (Property<NotificationItemView, Float>) ALPHA, 0.0f).setDuration(integer);
            duration2.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.popup.PopupContainerWithArrow.5
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    PopupContainerWithArrow popupContainerWithArrow = PopupContainerWithArrow.this;
                    popupContainerWithArrow.removeView(popupContainerWithArrow.mNotificationItemView);
                    PopupContainerWithArrow.this.mNotificationItemView = null;
                    if (PopupContainerWithArrow.this.getItemCount() == 0) {
                        PopupContainerWithArrow.this.close(false);
                    }
                }
            });
            animatorSetCreateAnimatorSet.play(duration2);
            long integer2 = getResources().getInteger(R.integer.config_deepShortcutArrowOpenDuration);
            ObjectAnimator duration3 = createArrowScaleAnim(0.0f).setDuration(integer2);
            duration3.setStartDelay(0L);
            ObjectAnimator duration4 = createArrowScaleAnim(1.0f).setDuration(integer2);
            duration4.setStartDelay((long) (((double) integer) - (integer2 * 1.5d)));
            animatorSetCreateAnimatorSet.playSequentially(duration3, duration4);
            animatorSetCreateAnimatorSet.start();
            return;
        }
        this.mNotificationItemView.trimNotifications(NotificationKeyData.extractKeysOnly(badgeInfo.getNotificationKeys()));
    }

    @Override // com.android.launcher3.AbstractFloatingView
    protected void onWidgetsBound() {
        ShortcutsItemView shortcutsItemView = this.mShortcutsItemView;
        if (shortcutsItemView != null) {
            shortcutsItemView.enableWidgetsIfExist(this.mOriginalIcon);
        }
    }

    private ObjectAnimator createArrowScaleAnim(float scale) {
        return LauncherAnimUtils.ofPropertyValuesHolder(this.mArrow, new PropertyListBuilder().scale(scale).build());
    }

    public Animator reduceNotificationViewHeight(int heightToRemove, int duration) {
        AnimatorSet animatorSet = this.mReduceHeightAnimatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        final int i = this.mIsAboveIcon ? heightToRemove : -heightToRemove;
        AnimatorSet animatorSetCreateAnimatorSet = LauncherAnimUtils.createAnimatorSet();
        this.mReduceHeightAnimatorSet = animatorSetCreateAnimatorSet;
        animatorSetCreateAnimatorSet.play(this.mNotificationItemView.animateHeightRemoval(heightToRemove));
        PropertyResetListener propertyResetListener = new PropertyResetListener(TRANSLATION_Y, Float.valueOf(0.0f));
        for (int i2 = 0; i2 < getItemCount(); i2++) {
            PopupItemView itemViewAt = getItemViewAt(i2);
            if (this.mIsAboveIcon || itemViewAt != this.mNotificationItemView) {
                ObjectAnimator duration2 = ObjectAnimator.ofFloat(itemViewAt, (Property<PopupItemView, Float>) TRANSLATION_Y, itemViewAt.getTranslationY() + i).setDuration(duration);
                duration2.addListener(propertyResetListener);
                this.mReduceHeightAnimatorSet.play(duration2);
            }
        }
        this.mReduceHeightAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.popup.PopupContainerWithArrow.6
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (PopupContainerWithArrow.this.mIsAboveIcon) {
                    PopupContainerWithArrow popupContainerWithArrow = PopupContainerWithArrow.this;
                    popupContainerWithArrow.setTranslationY(popupContainerWithArrow.getTranslationY() + i);
                }
                PopupContainerWithArrow.this.mReduceHeightAnimatorSet = null;
            }
        });
        return this.mReduceHeightAnimatorSet;
    }

    @Override // com.android.launcher3.DragSource
    public void onDropCompleted(View target, DropTarget.DragObject d, boolean isFlingToDelete, boolean success) {
        if (success) {
            return;
        }
        d.dragView.remove();
        this.mLauncher.getWorkspace().removeExtraEmptyScreen(false, false);
        this.mLauncher.showWorkspace(true);
        this.mLauncher.getSearchBar().onDragEnd();
    }

    @Override // com.android.launcher3.dragndrop.DragController.DragListener
    public void onDragStart(DragSource source, Object info, int dragAction) {
        this.mDeferContainerRemoval = true;
        animateClose();
        Iterator<PopupContainerListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().OnClose(this);
        }
    }

    @Override // com.android.launcher3.dragndrop.DragController.DragListener
    public void onDragEnd() {
        if (this.mIsOpen) {
            return;
        }
        if (this.mOpenCloseAnimator != null) {
            this.mDeferContainerRemoval = false;
        } else if (this.mDeferContainerRemoval) {
            closeComplete();
        }
    }

    @Override // com.android.launcher3.logging.UserEventDispatcher.LogContainerProvider
    public void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent) {
        target.itemType = 5;
        targetParent.containerType = 9;
    }

    @Override // com.android.launcher3.popup.ArrowPopup, com.android.launcher3.AbstractFloatingView
    protected void handleClose(boolean animate) {
        if (animate) {
            animateClose();
        } else {
            closeComplete();
        }
    }

    @Override // com.android.launcher3.popup.ArrowPopup
    protected void animateClose() {
        if (this.mIsOpen) {
            Animator animator = this.mOpenCloseAnimator;
            if (animator != null) {
                animator.cancel();
            }
            this.mIsOpen = false;
            AnimatorSet animatorSetCreateAnimatorSet = LauncherAnimUtils.createAnimatorSet();
            int itemCount = getItemCount();
            int i = 0;
            for (int i2 = 0; i2 < itemCount; i2++) {
                if (getItemViewAt(i2).isOpenOrOpening()) {
                    i++;
                }
            }
            long integer = getResources().getInteger(R.integer.config_deepShortcutCloseDuration);
            long integer2 = getResources().getInteger(R.integer.config_deepShortcutArrowOpenDuration);
            long integer3 = getResources().getInteger(R.integer.config_deepShortcutCloseStagger);
            LogAccelerateInterpolator logAccelerateInterpolator = new LogAccelerateInterpolator(100, 0);
            int i3 = this.mIsAboveIcon ? itemCount - i : 0;
            int i4 = i3;
            while (i4 < i3 + i) {
                final PopupItemView itemViewAt = getItemViewAt(i4);
                Animator animatorCreateCloseAnimation = itemViewAt.createCloseAnimation(this.mIsAboveIcon, this.mIsLeftAligned, integer);
                long j = ((long) (this.mIsAboveIcon ? i4 - i3 : (i - i4) - 1)) * integer3;
                animatorCreateCloseAnimation.setStartDelay(j);
                int i5 = i3;
                ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(itemViewAt, (Property<PopupItemView, Float>) View.ALPHA, 0.0f);
                objectAnimatorOfFloat.setStartDelay(j + integer2);
                objectAnimatorOfFloat.setDuration(integer - integer2);
                objectAnimatorOfFloat.setInterpolator(logAccelerateInterpolator);
                animatorSetCreateAnimatorSet.play(objectAnimatorOfFloat);
                animatorCreateCloseAnimation.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.popup.PopupContainerWithArrow.7
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        itemViewAt.setVisibility(4);
                    }
                });
                animatorSetCreateAnimatorSet.play(animatorCreateCloseAnimation);
                i4++;
                i3 = i5;
                i = i;
            }
            ObjectAnimator duration = createArrowScaleAnim(0.0f).setDuration(integer2);
            duration.setStartDelay(0L);
            animatorSetCreateAnimatorSet.play(duration);
            animatorSetCreateAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.android.launcher3.popup.PopupContainerWithArrow.8
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    PopupContainerWithArrow.this.mOpenCloseAnimator = null;
                    if (PopupContainerWithArrow.this.mDeferContainerRemoval) {
                        PopupContainerWithArrow.this.setVisibility(4);
                    } else {
                        PopupContainerWithArrow.this.closeComplete();
                    }
                }
            });
            BubbleTextView bubbleTextView = this.mOriginalIcon;
            if (bubbleTextView != null) {
                boolean zShouldTextBeVisible = bubbleTextView.shouldTextBeVisible();
                LGLog.d(TAG, "shouldTextBeVisible = " + zShouldTextBeVisible + ", " + this.mOriginalIcon.getTag() + ", " + this.mOriginalIcon);
                this.mOriginalIcon.setTextVisibility(zShouldTextBeVisible);
            }
            this.mOpenCloseAnimator = animatorSetCreateAnimatorSet;
            animatorSetCreateAnimatorSet.start();
        }
    }

    @Override // com.android.launcher3.popup.ArrowPopup
    protected void closeComplete() {
        LGLog.i(TAG, "closeComplete()");
        Animator animator = this.mOpenCloseAnimator;
        if (animator != null) {
            animator.cancel();
            this.mOpenCloseAnimator = null;
        }
        this.mIsOpen = false;
        this.mDeferContainerRemoval = false;
        BubbleTextView bubbleTextView = this.mOriginalIcon;
        if (bubbleTextView != null) {
            if (((ItemInfo) bubbleTextView.getTag()).container == -101) {
                this.mOriginalIcon.setTextVisibility(!LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue() && this.mLauncher.getDeviceProfile().isLandscape && this.mLauncher.getDeviceProfile().allowRotation);
            } else {
                this.mOriginalIcon.setTextVisibility(true);
            }
            if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
                BubbleTextView bubbleTextView2 = this.mOriginalIcon;
                if (bubbleTextView2 instanceof SwivelAppIconView) {
                    bubbleTextView2.setTextVisibility(false);
                }
            }
        }
        if (this.mLauncher.getDragController() != null) {
            this.mLauncher.getDragController().removeDragListener(this);
        }
        this.mLauncher.getDragLayer().removeView(this);
        Iterator<PopupContainerListener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().OnClose(this);
        }
    }

    public static PopupContainerWithArrow getOpen(Launcher launcher) {
        return (PopupContainerWithArrow) getOpenView(launcher, 2);
    }

    public static class LauncherPopupItemDragHandler implements PopupItemDragHandler {
        private final PopupContainerWithArrow mContainer;
        protected final Point mIconLastTouchPos = new Point();
        private final Launcher mLauncher;

        LauncherPopupItemDragHandler(Launcher launcher, PopupContainerWithArrow container) {
            this.mLauncher = launcher;
            this.mContainer = container;
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent ev) {
            int action = ev.getAction();
            if (action != 0 && action != 2) {
                return false;
            }
            this.mIconLastTouchPos.set((int) ev.getX(), (int) ev.getY());
            return false;
        }

        @Override // android.view.View.OnLongClickListener
        public boolean onLongClick(View v) {
            if (!ItemLongClickListener.canStartDrag(this.mLauncher) || !(v.getParent() instanceof DeepShortcutView)) {
                return false;
            }
            DeepShortcutView deepShortcutView = (DeepShortcutView) v.getParent();
            deepShortcutView.setWillDrawIcon(false);
            Point point = new Point();
            point.x = this.mIconLastTouchPos.x - deepShortcutView.getIconCenter().x;
            point.y = this.mIconLastTouchPos.y - this.mLauncher.getDeviceProfile().iconSizePx;
            DraggableView.ofType(0);
            this.mLauncher.getWorkspace().beginDragSharedDeepShortcut(deepShortcutView.getBubbleText(), this.mContainer, deepShortcutView.getFinalInfo(), new ShortcutDragPreviewProvider(deepShortcutView.getIconView(), point), new DragOptions()).animateShift(-point.x, -point.y);
            AbstractFloatingView.closeOpenContainer(this.mLauncher, 1);
            return false;
        }
    }

    public void setListener(PopupContainerListener listener) {
        if (listener != null) {
            this.mListeners.add(listener);
        }
    }

    public void removeListener(PopupContainerListener listener) {
        if (listener != null) {
            this.mListeners.remove(listener);
        }
    }

    private void calculateSystemShortcutWidth() {
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            ShortcutsItemView shortcutsItemView = this.mSystemShortcutsItemView;
            if (shortcutsItemView == null || shortcutsItemView.getSystemShortcutViewsSize() >= 3 || this.mShortcutsItemView != null || this.mNotificationItemView != null) {
                return;
            }
            ViewGroup.LayoutParams layoutParams = this.mSystemShortcutsItemView.getLayoutParams();
            layoutParams.width = (getContext().getResources().getDimensionPixelSize(R.dimen.system_shortcut_header_width_swivel) + (getContext().getResources().getDimensionPixelSize(R.dimen.system_shortcut_icon_padding_swivel) * 2)) * this.mSystemShortcutsItemView.getSystemShortcutViewsSize();
            this.mSystemShortcutsItemView.setLayoutParams(layoutParams);
            return;
        }
        if (com.lge.launcher3.util.Utilities.isLGUI10_0()) {
            ShortcutsItemView shortcutsItemView2 = this.mSystemShortcutsItemView;
            if (shortcutsItemView2 != null && shortcutsItemView2.getSystemShortcutViewsSize() < 3 && this.mShortcutsItemView == null && this.mNotificationItemView == null) {
                ViewGroup.LayoutParams layoutParams2 = this.mSystemShortcutsItemView.getLayoutParams();
                int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(R.dimen.system_shortcut_header_padding_ux10_0) * 2;
                layoutParams2.width = (((getContext().getResources().getDimensionPixelSize(R.dimen.bg_popup_item_width_ux10_0) - dimensionPixelSize) / 3) * this.mSystemShortcutsItemView.getSystemShortcutViewsSize()) + dimensionPixelSize;
                this.mSystemShortcutsItemView.setLayoutParams(layoutParams2);
                return;
            }
            ShortcutsItemView shortcutsItemView3 = this.mSystemShortcutsItemView;
            if (shortcutsItemView3 == null || shortcutsItemView3.getSystemShortcutViewsSize() != 4) {
                return;
            }
            ViewGroup.LayoutParams layoutParams3 = this.mSystemShortcutsItemView.getLayoutParams();
            layoutParams3.width = getContext().getResources().getDimensionPixelSize(R.dimen.bg_popup_item_width_ux10_0_large);
            this.mSystemShortcutsItemView.setLayoutParams(layoutParams3);
            ShortcutsItemView shortcutsItemView4 = this.mShortcutsItemView;
            if (shortcutsItemView4 != null) {
                ViewGroup.LayoutParams layoutParams4 = shortcutsItemView4.getLayoutParams();
                layoutParams4.width = getContext().getResources().getDimensionPixelSize(R.dimen.bg_popup_item_width_ux10_0_large);
                this.mShortcutsItemView.setLayoutParams(layoutParams4);
            }
            NotificationItemView notificationItemView = this.mNotificationItemView;
            if (notificationItemView != null) {
                ViewGroup.LayoutParams layoutParams5 = notificationItemView.getLayoutParams();
                layoutParams5.width = getContext().getResources().getDimensionPixelSize(R.dimen.bg_popup_item_width_ux10_0_large);
                this.mNotificationItemView.setLayoutParams(layoutParams5);
            }
        }
    }
}
