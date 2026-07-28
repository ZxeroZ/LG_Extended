package com.android.launcher3;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import com.android.launcher3.BaseRecyclerViewFastScrollBar;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.graphics.IconPalette;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.PackageItemInfo;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.android.launcher3.views.IconLabelDotView;
import com.lge.launcher3.R;
import com.lge.launcher3.adaptive.AdaptiveTextInterface;
import com.lge.launcher3.adaptive.AdaptiveTextManager;
import com.lge.launcher3.allapps.AllAppsItemInfo;
import com.lge.launcher3.badge.BadgeTextView;
import com.lge.launcher3.liveicon.LiveIconManager;
import com.lge.launcher3.profile.LGDeviceProfile;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.TextUtils;

/* JADX INFO: loaded from: classes.dex */
public class BubbleTextView extends BadgeTextView implements BaseRecyclerViewFastScrollBar.FastScrollFocusableView, AdaptiveTextInterface, IconCache.ItemInfoUpdateReceiver, IconLabelDotView {
    public static final int DISPLAY_ALL_APPS = 1;
    public static final int DISPLAY_SHORTCUT_POPUP = 4;
    public static final int DISPLAY_WORKSPACE = 0;
    private static final int FAST_SCROLL_FOCUS_FADE_IN_DURATION = 175;
    private static final int FAST_SCROLL_FOCUS_FADE_OUT_DURATION = 125;
    private static final float FAST_SCROLL_FOCUS_MAX_SCALE = 1.15f;
    private static final int FAST_SCROLL_FOCUS_MODE_DRAW_CIRCLE_BG = 2;
    private static final int FAST_SCROLL_FOCUS_MODE_NONE = 0;
    private static final int FAST_SCROLL_FOCUS_MODE_SCALE_ICON = 1;
    private static final int SHADOW_LARGE_ALPHA = Integer.MAX_VALUE;
    private static final int SHADOW_LARGE_COLOUR = 858796080;
    private static final float SHADOW_LARGE_RADIUS = 5.0f;
    private static final int SHADOW_SMALL_ALPHA = 872415231;
    private static final int SHADOW_SMALL_COLOUR = -2144325584;
    private static final float SHADOW_SMALL_RADIUS = 1.5f;
    private static final float SHADOW_SMALL_Y_OFFSET = 2.5f;
    private static final float SHADOW_Y_OFFSET = 0.0f;
    private static SparseArray<Resources.Theme> sPreloaderThemes = new SparseArray<>(2);
    boolean hideBadge;
    protected Drawable mBackground;
    private boolean mBackgroundSizeChanged;
    private Drawable mBadge;
    private IconPalette mBadgePalette;
    private ColorMatrixColorFilter mBaseFilter;
    protected Drawable mBgSpringDrawable;
    private ValueAnimator mBounceAnimator;
    private final boolean mCustomShadowsEnabled;
    private final boolean mDeferShadowGenerationOnTouch;
    private boolean mDisableRelayout;
    private ObjectAnimator mFastScrollFocusAnimator;
    private Paint mFastScrollFocusBgPaint;
    private float mFastScrollFocusFraction;
    private boolean mFastScrollFocused;
    private final int mFastScrollMode;
    private Rect mFgOriginalBounds;
    protected Drawable mFgSpringDrawable;
    protected Drawable mIcon;
    private IconCache.IconLoadRequest mIconLoadRequest;
    private final int mIconSize;
    private boolean mIgnorePressedStateChange;
    private final boolean mIsAdaptiveColor;
    private final BaseDraggingActivity mLauncher;
    private final CheckLongPressHelper mLongPressHelper;
    private final HolographicOutlineHelper mOutlineHelper;
    private Bitmap mPressedBackground;
    protected Path mScaledMaskPath;
    private float mSlop;
    private boolean mStayPressed;
    private final StylusEventHelper mStylusEventHelper;
    private int mTextColor;

    public interface BubbleTextShadowHandler {
        void setPressedIcon(BubbleTextView icon, Bitmap background);
    }

    protected void setSwivelIcon(ShortcutInfo info) {
    }

    public BubbleTextView(Context context) {
        this(context, null, 0);
    }

    public BubbleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mDisableRelayout = false;
        this.mFastScrollMode = 1;
        this.hideBadge = false;
        BaseDraggingActivity baseDraggingActivity = (BaseDraggingActivity) context;
        this.mLauncher = baseDraggingActivity;
        DeviceProfile deviceProfile = baseDraggingActivity.getDeviceProfile();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attrs, R.styleable.BubbleTextView, defStyle, 0);
        deviceProfile = typedArrayObtainStyledAttributes.getBoolean(6, false) ? InvariantDeviceProfile.INSTANCE.lambda$get$0$MainThreadInitializedObject(getContext()).portraitProfile : deviceProfile;
        boolean z = typedArrayObtainStyledAttributes.getBoolean(1, true);
        this.mCustomShadowsEnabled = z;
        this.mLayoutHorizontal = typedArrayObtainStyledAttributes.getBoolean(5, false);
        this.mDeferShadowGenerationOnTouch = typedArrayObtainStyledAttributes.getBoolean(2, true);
        this.mIsAdaptiveColor = typedArrayObtainStyledAttributes.getBoolean(0, true);
        int integer = typedArrayObtainStyledAttributes.getInteger(3, 0);
        int i = (!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || deviceProfile.isMultiWindowMode) ? deviceProfile.iconSizePx : deviceProfile.allAppsIconSizePx;
        if (integer == 0) {
            if (!deviceProfile.allowRotation && deviceProfile.isLandscape) {
                LGLog.i(TAG, "reset to portraitProfile for workspace item");
                deviceProfile = deviceProfile.inv.portraitProfile;
                i = deviceProfile.iconSizePx;
            }
            if (deviceProfile.isPhone && !deviceProfile.allowRotation) {
                this.mLayoutHorizontal = false;
            }
            setTextSize(0, deviceProfile.iconTextSizePx);
        } else if (integer == 1) {
            if (deviceProfile.isPhone && deviceProfile.isLandscape && !LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
                LGLog.i(TAG, "reset to portraitProfile for workspace item");
                deviceProfile = deviceProfile.inv.portraitProfile;
            }
            if (deviceProfile.isPhone && !deviceProfile.allowRotation) {
                this.mLayoutHorizontal = false;
            }
            setTextSize(0, deviceProfile.allAppsIconTextSizePx);
            if (deviceProfile.isMultiWindowMode) {
                i = deviceProfile.iconSizePx;
            } else {
                i = deviceProfile.allAppsIconSizePx;
            }
        }
        this.mIconSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(4, i);
        typedArrayObtainStyledAttributes.recycle();
        if (z) {
            this.mBackground = getBackground();
            setBackground(null);
        } else {
            this.mBackground = null;
        }
        this.mLongPressHelper = new CheckLongPressHelper(this);
        this.mStylusEventHelper = new StylusEventHelper(new SimpleOnStylusPressListener(this), this);
        this.mOutlineHelper = HolographicOutlineHelper.obtain(getContext());
        if (z) {
            setShadowLayer(SHADOW_LARGE_RADIUS, 0.0f, 0.0f, getAdaptiveTextShadowColor(SHADOW_LARGE_RADIUS, SHADOW_LARGE_COLOUR));
        }
        setAccessibilityDelegate(LauncherAppState.getInstance(context).getAccessibilityDelegate());
        float fontScale = TextUtils.getFontScale(context, attrs, this, 0);
        int displaySize = Utilities.getDisplaySize();
        if (deviceProfile instanceof LGDeviceProfile) {
            if (deviceProfile.isLandscape && LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && displaySize == 2) {
                setMaxLines(1);
            } else {
                setMaxLines(((LGDeviceProfile) deviceProfile).iconTextMaxLines);
            }
        }
        Resources resources = context.getResources();
        float fPxFromDp = Utilities.pxFromDp(resources.getFloat(R.dimen.device_profile_iconTextSize), resources.getDisplayMetrics());
        if ((integer == 0 || integer == 4) && (LGHomeFeature.Config.FEATURE_USE_DEFAULT_560DPI.getValue() || LGHomeFeature.Config.FEATURE_USE_DEFAULT_LOW_DPI.getValue() || deviceProfile.iconTextSizePx == 0)) {
            fPxFromDp = deviceProfile.iconTextSizePx;
        }
        if (integer == 1 && deviceProfile.isMultiWindowMode) {
            setTextSize(0, deviceProfile.iconTextSizePx);
        } else {
            setTextSize(0, fPxFromDp * fontScale);
        }
        addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.android.launcher3.BubbleTextView.1
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                TextView textView = (TextView) v;
                String string = textView.getText().toString();
                TextUtils.resetScale(textView, string, textView.getPaint());
                if (string.length() < 6 || textView.getLineCount() <= 1) {
                    return;
                }
                TextUtils.setTextScaleX(textView);
            }
        });
        setAdapiveTextColor(getAdaptiveTextColor());
    }

    public void applyFromShortcutInfo(ShortcutInfo info, IconCache iconCache) {
        applyFromShortcutInfo(info, iconCache, false);
    }

    public void applyFromShortcutInfo(ShortcutInfo info, IconCache iconCache, boolean promiseStateChanged) {
        Bitmap icon = info.getIcon(iconCache);
        if (info.getUserCustomizedIcon() != null) {
            icon = info.getUserCustomizedIcon();
        }
        FastBitmapDrawable fastBitmapDrawableCreateIconDrawable = Launcher.createIconDrawable(icon, this.mLauncher.getDeviceProfile().iconSizePx);
        if (info.isDisabled()) {
            fastBitmapDrawableCreateIconDrawable.setState(FastBitmapDrawable.State.DISABLED);
            fastBitmapDrawableCreateIconDrawable.setAlpha((int) (getResources().getFloat(R.dimen.config_disabledIconAlpha) * 255.0f));
        }
        setIcon(fastBitmapDrawableCreateIconDrawable, this.mIconSize);
        fastBitmapDrawableCreateIconDrawable.setIsDisabled(info.isDisabled());
        if (info.contentDescription != null) {
            setContentDescription(info.contentDescription);
        }
        setText(info.title);
        setTag(info);
        if (promiseStateChanged || info.isPromise()) {
            applyState(promiseStateChanged);
        }
    }

    public void applyFromCarouselShortcutInfo(ShortcutInfo info) {
        setSwivelIcon(info);
        setText(info.title);
        setTag(info);
        if (info.contentDescription != null) {
            setContentDescription(info.contentDescription);
        }
    }

    public void applyFromApplicationInfo(AppInfo info) {
        FastBitmapDrawable fastBitmapDrawableCreateIconDrawable = Launcher.createIconDrawable(info.iconBitmap, this.mLauncher.getDeviceProfile().iconSizePx);
        if (info.isDisabled()) {
            fastBitmapDrawableCreateIconDrawable.setState(FastBitmapDrawable.State.DISABLED);
        }
        fastBitmapDrawableCreateIconDrawable.setIsDisabled(info.isDisabled());
        setIcon(fastBitmapDrawableCreateIconDrawable, this.mIconSize);
        setText(info.title);
        if (info.contentDescription != null) {
            setContentDescription(info.contentDescription);
        }
        super.setTag(info);
        verifyHighRes();
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            return;
        }
        highlightSearchText((AllAppsItemInfo) info);
    }

    public void applyFromPackageItemInfo(PackageItemInfo info) {
        FastBitmapDrawable fastBitmapDrawableCreateIconDrawable = Launcher.createIconDrawable(info.iconBitmap, this.mLauncher.getDeviceProfile().iconSizePx);
        setIcon(fastBitmapDrawableCreateIconDrawable, this.mIconSize);
        fastBitmapDrawableCreateIconDrawable.setIsDisabled(info.isDisabled());
        setText(info.title);
        if (info.contentDescription != null) {
            setContentDescription(info.contentDescription);
        }
        super.setTag(info);
        verifyHighRes();
    }

    public void setLongPressTimeout(int longPressTimeout) {
        this.mLongPressHelper.setLongPressTimeout(longPressTimeout);
    }

    @Override // android.widget.TextView
    protected boolean setFrame(int left, int top, int right, int bottom) {
        if (getLeft() != left || getRight() != right || getTop() != top || getBottom() != bottom) {
            this.mBackgroundSizeChanged = true;
        }
        return super.setFrame(left, top, right, bottom);
    }

    @Override // android.widget.TextView, android.view.View
    protected boolean verifyDrawable(Drawable who) {
        return who == this.mBackground || super.verifyDrawable(who);
    }

    @Override // com.lge.launcher3.badge.BadgeTextView, android.view.View
    public void setTag(Object tag) {
        if (tag != null) {
            LauncherModel.checkItemInfo((ItemInfo) tag);
        }
        super.setTag(tag);
    }

    @Override // android.view.View
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        if (this.mIgnorePressedStateChange) {
            return;
        }
        updateIconState();
    }

    public Drawable getIcon() {
        return this.mIcon;
    }

    public boolean isLayoutHorizontal() {
        return this.mLayoutHorizontal;
    }

    private void updateIconState() {
        Drawable drawable = this.mIcon;
        if (drawable instanceof FastBitmapDrawable) {
            FastBitmapDrawable fastBitmapDrawable = (FastBitmapDrawable) drawable;
            if ((getTag() instanceof ItemInfo) && ((ItemInfo) getTag()).isDisabled()) {
                fastBitmapDrawable.animateState(FastBitmapDrawable.State.DISABLED);
            } else if (isPressed() || this.mStayPressed) {
                fastBitmapDrawable.animateState(FastBitmapDrawable.State.PRESSED);
            } else {
                fastBitmapDrawable.animateState(FastBitmapDrawable.State.NORMAL);
            }
        }
    }

    public CheckLongPressHelper getLongPressHelper() {
        return this.mLongPressHelper;
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0039  */
    @Override // android.widget.TextView, android.view.View
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
            com.android.launcher3.CheckLongPressHelper r0 = r3.mLongPressHelper
            r0.cancelLongPress()
            r0 = r2
        L13:
            int r1 = r4.getAction()
            if (r1 == 0) goto L48
            if (r1 == r2) goto L39
            r2 = 2
            if (r1 == r2) goto L23
            r4 = 3
            if (r1 == r4) goto L39
            goto La4
        L23:
            float r1 = r4.getX()
            float r4 = r4.getY()
            float r2 = r3.mSlop
            boolean r4 = com.android.launcher3.Utilities.pointInView(r3, r1, r4, r2)
            if (r4 != 0) goto La4
            com.android.launcher3.CheckLongPressHelper r4 = r3.mLongPressHelper
            r4.cancelLongPress()
            goto La4
        L39:
            boolean r4 = r3.isPressed()
            if (r4 != 0) goto L42
            r4 = 0
            r3.mPressedBackground = r4
        L42:
            com.android.launcher3.CheckLongPressHelper r4 = r3.mLongPressHelper
            r4.cancelLongPress()
            goto La4
        L48:
            java.lang.Object r4 = r3.getTag()
            boolean r4 = r4 instanceof com.android.launcher3.ShortcutInfo
            if (r4 != 0) goto L58
            java.lang.Object r4 = r3.getTag()
            boolean r4 = r4 instanceof com.lge.launcher3.allapps.AllAppsItemInfo
            if (r4 == 0) goto L87
        L58:
            java.lang.Object r4 = r3.getTag()
            com.android.launcher3.model.data.ItemInfo r4 = (com.android.launcher3.model.data.ItemInfo) r4
            android.content.ComponentName r1 = r4.getTargetComponent()
            if (r1 == 0) goto L87
            android.content.ComponentName r1 = r4.getTargetComponent()
            java.lang.String r1 = r1.getPackageName()
            if (r1 == 0) goto L87
            android.content.Intent r1 = new android.content.Intent
            java.lang.String r2 = "com.lge.android.intent.action.PRE_LAUNCH_PROC"
            r1.<init>(r2)
            android.content.ComponentName r4 = r4.getTargetComponent()
            java.lang.String r4 = r4.getPackageName()
            java.lang.String r2 = "com.lge.intent.extra.PACKAGE_NAME"
            r1.putExtra(r2, r4)
            com.android.launcher3.BaseDraggingActivity r4 = r3.mLauncher
            r4.sendBroadcast(r1)
        L87:
            boolean r4 = r3.mDeferShadowGenerationOnTouch
            if (r4 != 0) goto L97
            android.graphics.Bitmap r4 = r3.mPressedBackground
            if (r4 != 0) goto L97
            com.android.launcher3.HolographicOutlineHelper r4 = r3.mOutlineHelper
            android.graphics.Bitmap r4 = r4.createMediumDropShadow(r3)
            r3.mPressedBackground = r4
        L97:
            com.android.launcher3.StylusEventHelper r4 = r3.mStylusEventHelper
            boolean r4 = r4.inStylusButtonPressed()
            if (r4 != 0) goto La4
            com.android.launcher3.CheckLongPressHelper r4 = r3.mLongPressHelper
            r4.postCheckForLongPress()
        La4:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.BubbleTextView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setStayPressed(boolean stayPressed) {
        this.mStayPressed = stayPressed;
        if (!stayPressed) {
            this.mPressedBackground = null;
        } else if (this.mPressedBackground == null) {
            this.mPressedBackground = this.mOutlineHelper.createMediumDropShadow(this);
        }
        ViewParent parent = getParent();
        if (parent != null && (parent.getParent() instanceof BubbleTextShadowHandler)) {
            ((BubbleTextShadowHandler) parent.getParent()).setPressedIcon(this, this.mPressedBackground);
        }
        updateIconState();
    }

    void clearPressedBackground() {
        setPressed(false);
        setStayPressed(false);
    }

    @Override // android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!super.onKeyDown(keyCode, event)) {
            return false;
        }
        if (this.mPressedBackground != null) {
            return true;
        }
        this.mPressedBackground = this.mOutlineHelper.createMediumDropShadow(this);
        return true;
    }

    @Override // android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        this.mIgnorePressedStateChange = true;
        boolean zOnKeyUp = super.onKeyUp(keyCode, event);
        this.mPressedBackground = null;
        this.mIgnorePressedStateChange = false;
        updateIconState();
        return zOnKeyUp;
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        if (!this.mCustomShadowsEnabled) {
            super.draw(canvas);
            return;
        }
        Drawable drawable = this.mBackground;
        if (drawable != null) {
            int scrollX = getScrollX();
            int scrollY = getScrollY();
            if (this.mBackgroundSizeChanged) {
                drawable.setBounds(0, 0, getRight() - getLeft(), getBottom() - getTop());
                this.mBackgroundSizeChanged = false;
            }
            if ((scrollX | scrollY) == 0) {
                drawable.draw(canvas);
            } else {
                canvas.translate(scrollX, scrollY);
                drawable.draw(canvas);
                canvas.translate(-scrollX, -scrollY);
            }
        }
        if (getCurrentTextColor() == getResources().getColor(android.R.color.transparent) || (LGHomeFeature.Config.FEATURE_USE_BACKGROUND_OF_ICON_ON_EASYHOME.getValue() && !LGHomeFeature.isDisableEasyHome())) {
            getPaint().clearShadowLayer();
            super.draw(canvas);
            return;
        }
        getPaint().setShadowLayer(SHADOW_LARGE_RADIUS, 0.0f, 0.0f, getAdaptiveTextShadowColor(SHADOW_LARGE_RADIUS, SHADOW_LARGE_COLOUR));
        super.draw(canvas);
        canvas.save(2);
        canvas.clipRect(getScrollX(), getScrollY() + getExtendedPaddingTop(), getScrollX() + getWidth(), getScrollY() + getHeight(), Region.Op.INTERSECT);
        getPaint().setShadowLayer(SHADOW_SMALL_RADIUS, 0.0f, SHADOW_SMALL_Y_OFFSET, getAdaptiveTextShadowColor(SHADOW_SMALL_RADIUS, SHADOW_SMALL_COLOUR));
        super.draw(canvas);
        canvas.restore();
    }

    @Override // com.lge.launcher3.badge.BadgeTextView, android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Drawable drawable = this.mBackground;
        if (drawable != null) {
            drawable.setCallback(this);
        }
        Drawable drawable2 = this.mIcon;
        if (drawable2 instanceof PreloadIconDrawable) {
            ((PreloadIconDrawable) drawable2).applyPreloaderTheme(getPreloaderTheme());
        }
        this.mSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override // com.lge.launcher3.badge.BadgeTextView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Drawable drawable = this.mBackground;
        if (drawable != null) {
            drawable.setCallback(null);
        }
    }

    @Override // android.widget.TextView
    public void setTextColor(int color) {
        this.mTextColor = color;
        super.setTextColor(color);
    }

    @Override // android.widget.TextView
    public void setTextColor(ColorStateList colors) {
        this.mTextColor = colors.getDefaultColor();
        super.setTextColor(colors);
    }

    public void setTextVisibility(boolean visible) {
        Resources resources = getResources();
        if (visible) {
            super.setTextColor(this.mTextColor);
        } else {
            super.setTextColor(resources.getColor(android.R.color.transparent));
        }
    }

    public boolean shouldTextBeVisible() {
        Object tag = getParent() instanceof FolderIcon ? ((View) getParent()).getTag() : getTag();
        ItemInfo itemInfo = tag instanceof ItemInfo ? (ItemInfo) tag : null;
        return itemInfo == null || itemInfo.container != -101;
    }

    @Override // android.widget.TextView, android.view.View
    public void cancelLongPress() {
        super.cancelLongPress();
        this.mLongPressHelper.cancelLongPress();
    }

    public void applyState(boolean promiseStateChanged) {
        int installProgress;
        PreloadIconDrawable preloadIconDrawable;
        if (getTag() instanceof ShortcutInfo) {
            ShortcutInfo shortcutInfo = (ShortcutInfo) getTag();
            if (shortcutInfo.isPromise()) {
                installProgress = shortcutInfo.hasStatusFlag(4) ? shortcutInfo.getInstallProgress() : 0;
            } else {
                installProgress = 100;
            }
            Drawable drawable = this.mIcon;
            if (drawable != null) {
                if (drawable instanceof PreloadIconDrawable) {
                    preloadIconDrawable = (PreloadIconDrawable) drawable;
                } else {
                    preloadIconDrawable = new PreloadIconDrawable(this.mIcon, getPreloaderTheme());
                    setIcon(preloadIconDrawable, this.mIconSize);
                }
                preloadIconDrawable.setLevel(installProgress);
                if (promiseStateChanged) {
                    preloadIconDrawable.maybePerformFinishedAnimation();
                }
            }
        }
    }

    private Resources.Theme getPreloaderTheme() {
        Object tag = getTag();
        int i = (tag == null || !(tag instanceof ShortcutInfo) || ((ShortcutInfo) tag).container < 0) ? R.style.PreloadIcon : R.style.PreloadIcon_Folder;
        Resources.Theme theme = sPreloaderThemes.get(i);
        if (theme != null) {
            return theme;
        }
        Resources.Theme themeNewTheme = getResources().newTheme();
        themeNewTheme.applyStyle(i, true);
        sPreloaderThemes.put(i, themeNewTheme);
        return themeNewTheme;
    }

    public IconPalette getBadgePalette() {
        return this.mBadgePalette;
    }

    protected Drawable setIcon(Drawable icon, int iconSize) {
        this.mIcon = icon;
        if (iconSize != -1) {
            icon.setBounds(0, 0, iconSize, iconSize);
        }
        applyCompoundDrawables(this.mIcon);
        return icon;
    }

    protected void applyCompoundDrawables(Drawable icon) {
        if (this.mLayoutHorizontal) {
            if (Utilities.ATLEAST_JB_MR1) {
                setCompoundDrawablesRelative(icon, null, null, null);
                return;
            } else {
                setCompoundDrawables(icon, null, null, null);
                return;
            }
        }
        setCompoundDrawables(null, icon, null, null);
    }

    @Override // android.view.View
    public void requestLayout() {
        if (this.mDisableRelayout) {
            return;
        }
        super.requestLayout();
    }

    @Override // com.android.launcher3.icons.IconCache.ItemInfoUpdateReceiver
    public void reapplyItemInfo(ItemInfoWithIcon info) {
        View homescreenIconByItemId;
        if (getTag() == info) {
            FastBitmapDrawable.State currentState = FastBitmapDrawable.State.NORMAL;
            Drawable drawable = this.mIcon;
            if (drawable instanceof FastBitmapDrawable) {
                currentState = ((FastBitmapDrawable) drawable).getCurrentState();
            }
            this.mIconLoadRequest = null;
            this.mDisableRelayout = true;
            if (info instanceof AppInfo) {
                applyFromApplicationInfo((AppInfo) info);
            } else if (info instanceof ShortcutInfo) {
                applyFromShortcutInfo((ShortcutInfo) info, LauncherAppState.getInstance(getContext()).getIconCache());
                if (info.rank < 9 && info.container >= 0 && (getContext() instanceof Launcher) && (homescreenIconByItemId = ((Launcher) getContext()).getWorkspace().getHomescreenIconByItemId(info.container)) != null) {
                    homescreenIconByItemId.invalidate();
                }
            } else if (info instanceof PackageItemInfo) {
                applyFromPackageItemInfo((PackageItemInfo) info);
            }
            Drawable drawable2 = this.mIcon;
            if (drawable2 instanceof FastBitmapDrawable) {
                ((FastBitmapDrawable) drawable2).setState(currentState);
            }
            this.mDisableRelayout = false;
        }
    }

    public void verifyHighRes() {
        IconCache.IconLoadRequest iconLoadRequest = this.mIconLoadRequest;
        if (iconLoadRequest != null) {
            iconLoadRequest.cancel();
            this.mIconLoadRequest = null;
        }
        if (getTag() instanceof ItemInfoWithIcon) {
            ItemInfoWithIcon itemInfoWithIcon = (ItemInfoWithIcon) getTag();
            if (itemInfoWithIcon.usingLowResIcon) {
                this.mIconLoadRequest = LauncherAppState.getInstance(getContext()).getIconCache().updateIconInBackground(this, itemInfoWithIcon);
            }
        }
    }

    public void setFastScrollFocus(float fraction) {
        this.mFastScrollFocusFraction = fraction;
        float f = (fraction * 0.14999998f) + 1.0f;
        setScaleX(f);
        setScaleY(f);
    }

    public float getFastScrollFocus() {
        return this.mFastScrollFocusFraction;
    }

    @Override // com.android.launcher3.BaseRecyclerViewFastScrollBar.FastScrollFocusableView
    public void setFastScrollFocused(final boolean focused, boolean animated) {
        if (this.mFastScrollFocused != focused) {
            this.mFastScrollFocused = focused;
            if (animated) {
                ObjectAnimator objectAnimator = this.mFastScrollFocusAnimator;
                if (objectAnimator != null) {
                    objectAnimator.cancel();
                }
                float[] fArr = new float[1];
                fArr[0] = focused ? 1.0f : 0.0f;
                ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(this, "fastScrollFocus", fArr);
                this.mFastScrollFocusAnimator = objectAnimatorOfFloat;
                if (focused) {
                    objectAnimatorOfFloat.setInterpolator(new DecelerateInterpolator());
                } else {
                    objectAnimatorOfFloat.setInterpolator(new AccelerateInterpolator());
                }
                this.mFastScrollFocusAnimator.setDuration(focused ? 175L : 125L);
                this.mFastScrollFocusAnimator.start();
                return;
            }
            this.mFastScrollFocusFraction = focused ? 1.0f : 0.0f;
        }
    }

    @Override // com.android.launcher3.views.IconLabelDotView
    public void setIconVisible(boolean visible) {
        Drawable drawable;
        Drawable colorDrawable = visible ? this.mIcon : new ColorDrawable(0);
        if ((colorDrawable instanceof ColorDrawable) && (drawable = this.mIcon) != null) {
            colorDrawable.setBounds(drawable.getBounds());
        }
        applyCompoundDrawables(colorDrawable);
    }

    @Override // com.android.launcher3.views.IconLabelDotView
    public void setForceHideDot(boolean hide) {
        this.hideBadge = hide;
    }

    private int getModifiedShadowColor(float radius, int resColor) {
        if (Color.alpha(resColor) == 255) {
            return resColor & (radius == SHADOW_LARGE_RADIUS ? Integer.MAX_VALUE : SHADOW_SMALL_ALPHA);
        }
        return resColor;
    }

    @Override // com.lge.launcher3.adaptive.AdaptiveTextInterface
    public void setAdapiveTextColor(int color) {
        if (this.mIsAdaptiveColor) {
            if (getTextColors().getDefaultColor() == 0) {
                setTextColor(color);
                setTextVisibility(false);
            } else {
                setTextColor(color);
            }
        }
    }

    private int getAdaptiveTextShadowColor(float radius, int color) {
        if (!this.mIsAdaptiveColor) {
            return getModifiedShadowColor(radius, color);
        }
        Resources resources = getResources();
        int adaptiveTextColor = getAdaptiveTextColor();
        setAdapiveTextColor(adaptiveTextColor);
        if (adaptiveTextColor == resources.getColor(R.color.workspace_adaptive_color2, null)) {
            return getModifiedShadowColor(radius, resources.getColor(R.color.workspace_adaptive_color2_shadow, null));
        }
        if (adaptiveTextColor == resources.getColor(R.color.workspace_adaptive_color1, null)) {
            return getModifiedShadowColor(radius, resources.getColor(R.color.workspace_adaptive_color1_shadow, null));
        }
        return adaptiveTextColor == resources.getColor(R.color.workspace_icon_text_color, null) ? getModifiedShadowColor(radius, resources.getColor(R.color.workspace_icon_text_color_shadow, null)) : color;
    }

    @Override // android.view.View
    public String toString() {
        return "AppIcon {" + ((Object) getText()) + "}";
    }

    public void highlightSearchText(AllAppsItemInfo itemInfo) {
        if (itemInfo == null) {
            return;
        }
        if (!itemInfo.isSearched || itemInfo.searchPrefix == null || itemInfo.searchBody == null) {
            if (itemInfo != null) {
                setText(itemInfo.title);
                if (itemInfo.contentDescription != null) {
                    setContentDescription(itemInfo.contentDescription);
                    return;
                }
                return;
            }
            return;
        }
        SpannableStringBuilder spannableStringBuilderAppend = new SpannableStringBuilder(itemInfo.searchPrefix).append((CharSequence) itemInfo.searchBody).append((CharSequence) itemInfo.searchPostfix);
        int length = itemInfo.searchPrefix.length();
        int length2 = itemInfo.searchBody.length() + length;
        if (spannableStringBuilderAppend != null) {
            spannableStringBuilderAppend.setSpan(new ForegroundColorSpan(DDTUtils.getLGEColor(this.mContext, "color_accent_ui")), length, length2, 0);
        }
        setText(spannableStringBuilderAppend);
    }

    public void clearHighlight(AllAppsItemInfo itemInfo) {
        if (itemInfo == null) {
            return;
        }
        itemInfo.isSearched = false;
        setText(itemInfo.title);
    }

    public boolean hasDeepShortcuts() {
        return (getContext() instanceof Launcher) && !((Launcher) getContext()).getPopupDataProvider().getShortcutIdsForItem((ItemInfo) getTag()).isEmpty();
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mScaledMaskPath != null) {
            int iSave = canvas.save();
            canvas.clipPath(this.mScaledMaskPath);
            this.mBgSpringDrawable.draw(canvas);
            this.mFgSpringDrawable.draw(canvas);
            canvas.restoreToCount(iSave);
            Drawable drawable = this.mBadge;
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }
        if (this.hideBadge) {
            return;
        }
        drawBadge(canvas);
    }

    public void setItemInfo() {
        if (LGHomeFeature.Config.FEATURE_SUPPORT_ADAPTIVEICON_ANIMATION.getValue()) {
            final ItemInfo itemInfo = (ItemInfo) getTag();
            if ((itemInfo.itemType == 0 || itemInfo.itemType == 6 || itemInfo.itemType == 2) && !LiveIconManager.getInstance(this.mContext).hasLiveIcon(itemInfo.getTargetComponent())) {
                if (getContext() instanceof Launcher) {
                    Launcher launcher = (Launcher) getContext();
                    if (launcher.getWorkspace() != null && launcher.getWorkspace().isInOverviewMode()) {
                        return;
                    }
                    if (launcher.getAllAppsHost() != null && launcher.getAllAppsHost().isInArrangeMode()) {
                        return;
                    }
                }
                if (itemInfo.getIntent() == null) {
                    return;
                }
                new Handler(LauncherModel.getWorkerLooper()).postAtFrontOfQueue(new Runnable() { // from class: com.android.launcher3.BubbleTextView.2
                    @Override // java.lang.Runnable
                    public void run() {
                        LauncherAppState launcherAppState = LauncherAppState.getInstance(BubbleTextView.this.mLauncher);
                        Object[] objArr = new Object[1];
                        Drawable fullDrawable = DragView.getFullDrawable(itemInfo, launcherAppState, objArr, BubbleTextView.this.mLauncher);
                        if (fullDrawable instanceof AdaptiveIconDrawable) {
                            int paddingLeft = BubbleTextView.this.mLayoutHorizontal ? BubbleTextView.this.getPaddingLeft() : (BubbleTextView.this.getWidth() - BubbleTextView.this.mIconSize) / 2;
                            int height = BubbleTextView.this.mLayoutHorizontal ? (BubbleTextView.this.getHeight() - BubbleTextView.this.mIconSize) / 2 : BubbleTextView.this.getPaddingTop();
                            int integer = BubbleTextView.this.getResources().getInteger(R.integer.config_adaptiveicon_additional_length);
                            Rect rect = new Rect(paddingLeft + integer, height + integer, BubbleTextView.this.mIconSize + paddingLeft + integer, BubbleTextView.this.mIconSize + height + integer);
                            Rect rect2 = new Rect(rect);
                            BubbleTextView bubbleTextView = BubbleTextView.this;
                            bubbleTextView.mBadge = DragView.getBadge(itemInfo, launcherAppState, objArr[0], bubbleTextView.mLauncher);
                            BubbleTextView.this.mBadge.setBounds(rect2);
                            Utilities.scaleRectAboutCenter(rect, 0.9599999f);
                            AdaptiveIconDrawable adaptiveIconDrawable = (AdaptiveIconDrawable) fullDrawable;
                            Rect rect3 = new Rect(rect);
                            Utilities.scaleRectAboutCenter(rect3, DragView.ADAPTIVE_ICON_SHRINK_RATIO_FOR_ANTIALIAS);
                            adaptiveIconDrawable.setBounds(rect3);
                            final Path iconMask = adaptiveIconDrawable.getIconMask();
                            rect.inset((int) ((-rect.width()) * AdaptiveIconDrawable.getExtraInsetFraction()), (int) ((-rect.height()) * AdaptiveIconDrawable.getExtraInsetFraction()));
                            BubbleTextView.this.mBgSpringDrawable = adaptiveIconDrawable.getBackground();
                            if (BubbleTextView.this.mBgSpringDrawable == null) {
                                BubbleTextView.this.mBgSpringDrawable = new ColorDrawable(0);
                            }
                            BubbleTextView.this.mBgSpringDrawable.setBounds(rect);
                            BubbleTextView.this.mFgSpringDrawable = adaptiveIconDrawable.getForeground();
                            if (BubbleTextView.this.mFgSpringDrawable == null) {
                                BubbleTextView.this.mFgSpringDrawable = new ColorDrawable(0);
                            }
                            BubbleTextView.this.mFgSpringDrawable.setBounds(rect);
                            new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.android.launcher3.BubbleTextView.2.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    BubbleTextView.this.mScaledMaskPath = iconMask;
                                    if (itemInfo.isDisabled()) {
                                        FastBitmapDrawable fastBitmapDrawable = new FastBitmapDrawable((Bitmap) null);
                                        fastBitmapDrawable.setIsDisabled(true);
                                        BubbleTextView.this.mBaseFilter = (ColorMatrixColorFilter) fastBitmapDrawable.getColorFilter();
                                    }
                                    BubbleTextView.this.updateColorFilter();
                                    BubbleTextView.this.startBounceAnimation();
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateColorFilter() {
        Drawable drawable;
        if (this.mScaledMaskPath != null && (drawable = this.mBgSpringDrawable) != null && this.mFgSpringDrawable != null && this.mBadge != null) {
            drawable.setColorFilter(this.mBaseFilter);
            this.mFgSpringDrawable.setColorFilter(this.mBaseFilter);
            this.mBadge.setColorFilter(this.mBaseFilter);
        }
        invalidate();
    }

    public void startBounceAnimation() {
        ValueAnimator valueAnimatorOfInt = ValueAnimator.ofInt(0, 20);
        this.mBounceAnimator = valueAnimatorOfInt;
        valueAnimatorOfInt.setDuration(300L);
        this.mFgOriginalBounds = new Rect(this.mFgSpringDrawable.getBounds());
        this.mBounceAnimator.setRepeatCount(1);
        this.mBounceAnimator.setRepeatMode(2);
        this.mBounceAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.BubbleTextView.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                if (BubbleTextView.this.mFgSpringDrawable != null) {
                    int i = Integer.parseInt(animation.getAnimatedValue().toString());
                    BubbleTextView.this.mFgSpringDrawable.setBounds(BubbleTextView.this.mFgOriginalBounds.left + i, BubbleTextView.this.mFgOriginalBounds.top + i, BubbleTextView.this.mFgOriginalBounds.right - i, BubbleTextView.this.mFgOriginalBounds.bottom - i);
                }
                BubbleTextView.this.invalidate();
            }
        });
        this.mBounceAnimator.addListener(new Animator.AnimatorListener() { // from class: com.android.launcher3.BubbleTextView.4
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animation) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                BubbleTextView.this.mScaledMaskPath = null;
                BubbleTextView.this.mBgSpringDrawable = null;
                BubbleTextView.this.mFgSpringDrawable = null;
                BubbleTextView.this.invalidate();
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                BubbleTextView.this.mScaledMaskPath = null;
                BubbleTextView.this.mBgSpringDrawable = null;
                BubbleTextView.this.mFgSpringDrawable = null;
            }
        });
        this.mBounceAnimator.start();
    }

    private int getAdaptiveTextColor() {
        if (LGHomeFeature.Config.FEATURE_USE_BACKGROUND_OF_ICON_ON_EASYHOME.getValue() && !LGHomeFeature.isDisableEasyHome()) {
            return getContext().getResources().getColor(R.color.workspace_adaptive_color2);
        }
        return AdaptiveTextManager.getAdaptiveTextColor();
    }

    public void getIconBounds(Rect outBounds) {
        int scaleX = (int) (this.mIconSize * getScaleX());
        int height = this.mLayoutHorizontal ? (getHeight() - scaleX) / 2 : getPaddingTop() + ((this.mIconSize - scaleX) / 2);
        int paddingLeft = this.mLayoutHorizontal ? getPaddingLeft() : (getWidth() - scaleX) / 2;
        outBounds.set(paddingLeft, height, paddingLeft + scaleX, scaleX + height);
    }

    public int getIconSize() {
        return this.mIconSize;
    }

    public int getIconSizePx() {
        return this.mLauncher.getDeviceProfile().iconSizePx;
    }
}
