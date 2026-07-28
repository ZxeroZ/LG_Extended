package com.android.systemui.shared.rotation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.IRotationWatcher;
import android.view.IWindowManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManagerGlobal;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.logging.UiEventLoggerImpl;
import com.android.internal.view.RotationPolicy;
import com.android.systemui.shared.recents.utilities.Utilities;
import com.android.systemui.shared.recents.utilities.ViewRippler;
import com.android.systemui.shared.rotation.RotationButton;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.system.TaskStackChangeListeners;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes.dex */
public class RotationButtonController {
    private static final int BUTTON_FADE_IN_OUT_DURATION_MS = 100;
    private static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
    private static final int NAVBAR_HIDDEN_PENDING_ICON_TIMEOUT_MS = 20000;
    private static final int NUM_ACCEPTED_ROTATION_SUGGESTIONS_FOR_INTRODUCTION = 3;
    private static final String TAG = "StatusBar/RotationButtonController";
    private final AccessibilityManager mAccessibilityManager;
    private final Context mContext;
    private final int mDarkIconColor;
    private boolean mHomeRotationEnabled;
    private boolean mHoveringRotationSuggestion;
    private final int mIconCcwStart0ResId;
    private final int mIconCcwStart90ResId;
    private final int mIconCwStart0ResId;
    private final int mIconCwStart90ResId;
    private int mIconResId;
    private boolean mIsNavigationBarShowing;
    private boolean mIsRecentsAnimationRunning;
    private int mLastRotationSuggestion;
    private final int mLightIconColor;
    private boolean mPendingRotationSuggestion;
    private Consumer<Integer> mRotWatcherListener;
    private Animator mRotateHideAnimator;
    private RotationButton mRotationButton;
    private boolean mShouldHide;
    private boolean mSkipOverrideUserLockPrefsOnce;
    private final Supplier<Integer> mWindowRotationProvider;
    private final String ROTATION_BUTTON_SETTING = "hide_rotate_button";
    private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
    private final UiEventLogger mUiEventLogger = new UiEventLoggerImpl();
    private final ViewRippler mViewRippler = new ViewRippler();
    private boolean mListenersRegistered = false;
    private int mBehavior = 1;
    private final Runnable mRemoveRotationProposal = new Runnable() { // from class: com.android.systemui.shared.rotation.-$$Lambda$RotationButtonController$YjnDy7AOTuEVJEKSHSjIm0l-gOY
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.lambda$new$0$RotationButtonController();
        }
    };
    private final Runnable mCancelPendingRotationProposal = new Runnable() { // from class: com.android.systemui.shared.rotation.-$$Lambda$RotationButtonController$TN26GBhO0iajE6nDwh6BEL4qwA0
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.lambda$new$1$RotationButtonController();
        }
    };
    private final IRotationWatcher.Stub mRotationWatcher = new AnonymousClass1();
    private final ContentObserver mRotationButtonSettingObserver = new AnonymousClass2(new Handler());
    private final ContentObserver mAccelerometerRotationObserver = new AnonymousClass3(new Handler());
    private final TaskStackListenerImpl mTaskStackListener = new TaskStackListenerImpl(this, null);

    public static boolean hasDisable2RotateSuggestionFlag(int i) {
        return (i & 16) != 0;
    }

    public /* synthetic */ void lambda$new$0$RotationButtonController() {
        setRotateSuggestionButtonState(false);
    }

    public /* synthetic */ void lambda$new$1$RotationButtonController() {
        this.mPendingRotationSuggestion = false;
    }

    /* JADX INFO: renamed from: com.android.systemui.shared.rotation.RotationButtonController$1, reason: invalid class name */
    class AnonymousClass1 extends IRotationWatcher.Stub {
        AnonymousClass1() {
        }

        public void onRotationChanged(final int i) {
            RotationButtonController.this.mMainThreadHandler.postAtFrontOfQueue(new Runnable() { // from class: com.android.systemui.shared.rotation.-$$Lambda$RotationButtonController$1$tbcz1Y9L5CjhHvmrEgBc0MOywbA
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onRotationChanged$0$RotationButtonController$1(i);
                }
            });
        }

        public /* synthetic */ void lambda$onRotationChanged$0$RotationButtonController$1(int i) {
            if (RotationButtonController.this.isRotationLocked()) {
                if (RotationButtonController.this.shouldOverrideUserLockPrefs(i)) {
                    RotationButtonController.this.setRotationLockedAtAngle(i);
                }
                RotationButtonController.this.setRotateSuggestionButtonState(false, true);
            }
            if (RotationButtonController.this.mRotWatcherListener != null) {
                RotationButtonController.this.mRotWatcherListener.accept(Integer.valueOf(i));
            }
        }
    }

    /* JADX INFO: renamed from: com.android.systemui.shared.rotation.RotationButtonController$2, reason: invalid class name */
    class AnonymousClass2 extends ContentObserver {
        AnonymousClass2(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            RotationButtonController rotationButtonController = RotationButtonController.this;
            rotationButtonController.mShouldHide = Settings.Global.getInt(rotationButtonController.mContext.getContentResolver(), "hide_rotate_button", 0) == 1;
            if (RotationButtonController.this.mShouldHide) {
                RotationButtonController.this.mMainThreadHandler.post(new Runnable() { // from class: com.android.systemui.shared.rotation.-$$Lambda$RotationButtonController$2$ppcbfSEvT1bERgCcNg8V4jQHvAs
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onChange$0$RotationButtonController$2();
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onChange$0$RotationButtonController$2() {
            RotationButtonController.this.mRotationButton.hide();
        }
    }

    /* JADX INFO: renamed from: com.android.systemui.shared.rotation.RotationButtonController$3, reason: invalid class name */
    class AnonymousClass3 extends ContentObserver {
        AnonymousClass3(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            if (Settings.System.getInt(RotationButtonController.this.mContext.getContentResolver(), "accelerometer_rotation", 0) == 1) {
                RotationButtonController.this.mMainThreadHandler.post(new Runnable() { // from class: com.android.systemui.shared.rotation.-$$Lambda$RotationButtonController$3$00HZUlxS-DHfb0R9Zy-YJHq33pU
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onChange$0$RotationButtonController$3();
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onChange$0$RotationButtonController$3() {
            RotationButtonController.this.mRotationButton.hide();
        }
    }

    public RotationButtonController(Context context, int i, int i2, int i3, int i4, int i5, int i6, Supplier<Integer> supplier) {
        this.mContext = context;
        this.mLightIconColor = i;
        this.mDarkIconColor = i2;
        this.mIconCcwStart0ResId = i3;
        this.mIconCcwStart90ResId = i4;
        this.mIconCwStart0ResId = i5;
        this.mIconCwStart90ResId = i6;
        this.mIconResId = i4;
        this.mAccessibilityManager = AccessibilityManager.getInstance(context);
        this.mWindowRotationProvider = supplier;
    }

    public void setRotationButton(RotationButton rotationButton, RotationButton.RotationButtonUpdatesCallback rotationButtonUpdatesCallback) {
        this.mRotationButton = rotationButton;
        rotationButton.setRotationButtonController(this);
        this.mRotationButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.systemui.shared.rotation.-$$Lambda$RotationButtonController$zgkIWtDwDdf8jAM6lj_qLTw8at8
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.onRotateSuggestionClick(view);
            }
        });
        this.mRotationButton.setOnHoverListener(new View.OnHoverListener() { // from class: com.android.systemui.shared.rotation.-$$Lambda$RotationButtonController$YeUFWgBZr8oqH7B3joPsfE2WF6o
            @Override // android.view.View.OnHoverListener
            public final boolean onHover(View view, MotionEvent motionEvent) {
                return this.f$0.onRotateSuggestionHover(view, motionEvent);
            }
        });
        this.mRotationButton.setUpdatesCallback(rotationButtonUpdatesCallback);
        RotationButton rotationButton2 = this.mRotationButton;
        if (rotationButton2 instanceof FloatingRotationButton) {
            rotationButton2.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.systemui.shared.rotation.-$$Lambda$RotationButtonController$iGvjZS9ot7nd-n-5N7F-mMIaOCQ
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    return this.f$0.onTouchListener(view, motionEvent);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onTouchListener(View view, MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() != 3) {
            return false;
        }
        onRotateSuggestionClick(view);
        return false;
    }

    public Context getContext() {
        return this.mContext;
    }

    public void init() {
        registerListeners();
        if (this.mContext.getDisplay().getDisplayId() != 0) {
            onDisable2FlagChanged(16);
        }
    }

    public void onDestroy() {
        unregisterListeners();
    }

    public void registerListeners() {
        if (this.mListenersRegistered || getContext().getPackageManager().hasSystemFeature("android.hardware.type.pc")) {
            return;
        }
        this.mListenersRegistered = true;
        try {
            WindowManagerGlobal.getWindowManagerService().watchRotation(this.mRotationWatcher, 0);
            TaskStackChangeListeners.getInstance().registerTaskStackListener(this.mTaskStackListener);
            this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("hide_rotate_button"), true, this.mRotationButtonSettingObserver, -1);
            this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("accelerometer_rotation"), true, this.mAccelerometerRotationObserver, -1);
            this.mRotationButtonSettingObserver.onChange(true);
        } catch (RemoteException e) {
            Log.e(TAG, "RegisterListeners caught a RemoteException", e);
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
            this.mListenersRegistered = false;
            Log.w(TAG, "RegisterListeners for the display failed");
        }
    }

    public void unregisterListeners() {
        if (this.mListenersRegistered) {
            this.mListenersRegistered = false;
            try {
                WindowManagerGlobal.getWindowManagerService().removeRotationWatcher(this.mRotationWatcher);
                TaskStackChangeListeners.getInstance().unregisterTaskStackListener(this.mTaskStackListener);
                this.mContext.getContentResolver().unregisterContentObserver(this.mRotationButtonSettingObserver);
                this.mContext.getContentResolver().unregisterContentObserver(this.mAccelerometerRotationObserver);
            } catch (RemoteException e) {
                Log.e(TAG, "UnregisterListeners caught a RemoteException", e);
            }
        }
    }

    public void setRotationCallback(Consumer<Integer> consumer) {
        this.mRotWatcherListener = consumer;
    }

    public void setRotationLockedAtAngle(int i) {
        IWindowManager iWindowManagerAsInterface = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        try {
            iWindowManagerAsInterface.freezeDisplayRotation(0, i);
            iWindowManagerAsInterface.freezeDisplayRotation(1, i);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NullPointerException e2) {
            e2.printStackTrace();
        }
    }

    public boolean isRotationLocked() {
        return RotationPolicy.isRotationLocked(this.mContext);
    }

    public void setRotateSuggestionButtonState(boolean z) {
        setRotateSuggestionButtonState(z, false);
    }

    void setRotateSuggestionButtonState(boolean z, boolean z2) {
        View currentView;
        if (this.mShouldHide) {
            return;
        }
        if ((z || this.mRotationButton.isVisible()) && (currentView = this.mRotationButton.getCurrentView()) != null) {
            this.mPendingRotationSuggestion = false;
            this.mMainThreadHandler.removeCallbacks(this.mCancelPendingRotationProposal);
            if (z) {
                Animator animator = this.mRotateHideAnimator;
                if (animator != null && animator.isRunning()) {
                    this.mRotateHideAnimator.cancel();
                }
                this.mRotateHideAnimator = null;
                currentView.setAlpha(1.0f);
                if (!isRotateSuggestionIntroduced()) {
                    this.mViewRippler.start(currentView);
                }
                this.mRotationButton.show();
                return;
            }
            this.mViewRippler.stop();
            if (z2) {
                Animator animator2 = this.mRotateHideAnimator;
                if (animator2 != null && animator2.isRunning()) {
                    this.mRotateHideAnimator.pause();
                }
                this.mRotationButton.hide();
                return;
            }
            Animator animator3 = this.mRotateHideAnimator;
            if (animator3 == null || !animator3.isRunning()) {
                ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(currentView, "alpha", 0.0f);
                objectAnimatorOfFloat.setDuration(100L);
                objectAnimatorOfFloat.setInterpolator(LINEAR_INTERPOLATOR);
                objectAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.shared.rotation.RotationButtonController.4
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator4) {
                        RotationButtonController.this.mRotationButton.hide();
                    }
                });
                this.mRotateHideAnimator = objectAnimatorOfFloat;
                objectAnimatorOfFloat.start();
            }
        }
    }

    public void setDarkIntensity(float f) {
        this.mRotationButton.setDarkIntensity(f);
    }

    public void setRecentsAnimationRunning(boolean z) {
        this.mIsRecentsAnimationRunning = z;
        updateRotationButtonStateInOverview();
    }

    public void setHomeRotationEnabled(boolean z) {
        this.mHomeRotationEnabled = z;
        updateRotationButtonStateInOverview();
    }

    private void updateRotationButtonStateInOverview() {
        if (!this.mIsRecentsAnimationRunning || this.mHomeRotationEnabled) {
            return;
        }
        setRotateSuggestionButtonState(false, true);
    }

    public void onRotationProposal(int i, int i2, boolean z) {
        int iIntValue = this.mWindowRotationProvider.get().intValue();
        if (this.mRotationButton.acceptRotationProposal()) {
            if (this.mHomeRotationEnabled || !this.mIsRecentsAnimationRunning) {
                if (!z) {
                    setRotateSuggestionButtonState(false);
                    return;
                }
                if (i == iIntValue) {
                    this.mMainThreadHandler.removeCallbacks(this.mRemoveRotationProposal);
                    setRotateSuggestionButtonState(false);
                    return;
                }
                RotationButton rotationButton = this.mRotationButton;
                if (rotationButton instanceof FloatingRotationButton) {
                    ((FloatingRotationButton) rotationButton).onRotationProposal(i, i2);
                }
                this.mLastRotationSuggestion = i;
                boolean zIsRotationAnimationCCW = Utilities.isRotationAnimationCCW(iIntValue, i);
                if (iIntValue == 0 || iIntValue == 2) {
                    this.mIconResId = zIsRotationAnimationCCW ? this.mIconCcwStart0ResId : this.mIconCwStart0ResId;
                } else {
                    this.mIconResId = zIsRotationAnimationCCW ? this.mIconCcwStart90ResId : this.mIconCwStart90ResId;
                }
                this.mRotationButton.updateIcon(this.mLightIconColor, this.mDarkIconColor);
                if (canShowRotationButton()) {
                    showAndLogRotationSuggestion();
                    return;
                }
                this.mPendingRotationSuggestion = true;
                this.mMainThreadHandler.removeCallbacks(this.mCancelPendingRotationProposal);
                this.mMainThreadHandler.postDelayed(this.mCancelPendingRotationProposal, 20000L);
            }
        }
    }

    public void onDisable2FlagChanged(int i) {
        if (hasDisable2RotateSuggestionFlag(i)) {
            onRotationSuggestionsDisabled();
        }
    }

    public void onBehaviorChanged(int i, int i2) {
        if (i == 0 && this.mBehavior != i2) {
            this.mBehavior = i2;
            showPendingRotationButtonIfNeeded();
        }
    }

    public void onNavigationBarWindowVisibilityChange(boolean z) {
        if (this.mIsNavigationBarShowing != z) {
            this.mIsNavigationBarShowing = z;
            showPendingRotationButtonIfNeeded();
        }
    }

    public void onTaskbarStateChange(boolean z, boolean z2) {
        if (getRotationButton() == null) {
            return;
        }
        getRotationButton().onTaskbarStateChanged(z, z2);
    }

    private void showPendingRotationButtonIfNeeded() {
        if (canShowRotationButton() && this.mPendingRotationSuggestion) {
            showAndLogRotationSuggestion();
        }
    }

    private boolean canShowRotationButton() {
        return this.mIsNavigationBarShowing || this.mBehavior == 1;
    }

    public int getIconResId() {
        return this.mIconResId;
    }

    public int getLightIconColor() {
        return this.mLightIconColor;
    }

    public int getDarkIconColor() {
        return this.mDarkIconColor;
    }

    public void dumpLogs(String str, PrintWriter printWriter) {
        printWriter.println(str + "RotationButtonController:");
        printWriter.println(String.format("%s\tmIsRecentsAnimationRunning=%b", str, Boolean.valueOf(this.mIsRecentsAnimationRunning)));
        printWriter.println(String.format("%s\tmHomeRotationEnabled=%b", str, Boolean.valueOf(this.mHomeRotationEnabled)));
        printWriter.println(String.format("%s\tmLastRotationSuggestion=%d", str, Integer.valueOf(this.mLastRotationSuggestion)));
        printWriter.println(String.format("%s\tmPendingRotationSuggestion=%b", str, Boolean.valueOf(this.mPendingRotationSuggestion)));
        printWriter.println(String.format("%s\tmHoveringRotationSuggestion=%b", str, Boolean.valueOf(this.mHoveringRotationSuggestion)));
        printWriter.println(String.format("%s\tmListenersRegistered=%b", str, Boolean.valueOf(this.mListenersRegistered)));
        printWriter.println(String.format("%s\tmIsNavigationBarShowing=%b", str, Boolean.valueOf(this.mIsNavigationBarShowing)));
        printWriter.println(String.format("%s\tmBehavior=%d", str, Integer.valueOf(this.mBehavior)));
        printWriter.println(String.format("%s\tmSkipOverrideUserLockPrefsOnce=%b", str, Boolean.valueOf(this.mSkipOverrideUserLockPrefsOnce)));
        printWriter.println(String.format("%s\tmLightIconColor=0x%s", str, Integer.toHexString(this.mLightIconColor)));
        printWriter.println(String.format("%s\tmDarkIconColor=0x%s", str, Integer.toHexString(this.mDarkIconColor)));
    }

    public RotationButton getRotationButton() {
        return this.mRotationButton;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onRotateSuggestionClick(View view) {
        this.mUiEventLogger.log(RotationButtonEvent.ROTATION_SUGGESTION_ACCEPTED);
        incrementNumAcceptedRotationSuggestionsIfNeeded();
        setRotationLockedAtAngle(this.mLastRotationSuggestion);
        view.performHapticFeedback(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onRotateSuggestionHover(View view, MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        this.mHoveringRotationSuggestion = actionMasked == 9 || actionMasked == 7;
        rescheduleRotationTimeout(true);
        return false;
    }

    private void onRotationSuggestionsDisabled() {
        setRotateSuggestionButtonState(false, true);
        this.mMainThreadHandler.removeCallbacks(this.mRemoveRotationProposal);
    }

    private void showAndLogRotationSuggestion() {
        setRotateSuggestionButtonState(true);
        rescheduleRotationTimeout(false);
        this.mUiEventLogger.log(RotationButtonEvent.ROTATION_SUGGESTION_SHOWN);
    }

    public void setSkipOverrideUserLockPrefsOnce() {
        this.mSkipOverrideUserLockPrefsOnce = !this.mIsRecentsAnimationRunning;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldOverrideUserLockPrefs(int i) {
        if (!this.mSkipOverrideUserLockPrefsOnce) {
            return i == 0;
        }
        this.mSkipOverrideUserLockPrefsOnce = false;
        return false;
    }

    private void rescheduleRotationTimeout(boolean z) {
        Animator animator;
        if (!z || (((animator = this.mRotateHideAnimator) == null || !animator.isRunning()) && this.mRotationButton.isVisible())) {
            this.mMainThreadHandler.removeCallbacks(this.mRemoveRotationProposal);
            this.mMainThreadHandler.postDelayed(this.mRemoveRotationProposal, computeRotationProposalTimeout());
        }
    }

    private int computeRotationProposalTimeout() {
        return this.mAccessibilityManager.getRecommendedTimeoutMillis(this.mHoveringRotationSuggestion ? 16000 : 5000, 4);
    }

    private boolean isRotateSuggestionIntroduced() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "num_rotation_suggestions_accepted", 0) >= 3;
    }

    private void incrementNumAcceptedRotationSuggestionsIfNeeded() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        int i = Settings.Secure.getInt(contentResolver, "num_rotation_suggestions_accepted", 0);
        if (i < 3) {
            Settings.Secure.putInt(contentResolver, "num_rotation_suggestions_accepted", i + 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    class TaskStackListenerImpl implements TaskStackChangeListener {
        private TaskStackListenerImpl() {
        }

        /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR (r1v0 com.android.systemui.shared.rotation.RotationButtonController) A[MD:(com.android.systemui.shared.rotation.RotationButtonController):void (m)] (LINE:659) call: com.android.systemui.shared.rotation.RotationButtonController.TaskStackListenerImpl.<init>(com.android.systemui.shared.rotation.RotationButtonController):void type: THIS */
        /* synthetic */ TaskStackListenerImpl(RotationButtonController rotationButtonController, AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onTaskStackChanged() {
            RotationButtonController.this.setRotateSuggestionButtonState(false);
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onTaskRemoved(int i) {
            RotationButtonController.this.setRotateSuggestionButtonState(false);
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onTaskMovedToFront(int i) {
            RotationButtonController.this.setRotateSuggestionButtonState(false);
        }

        @Override // com.android.systemui.shared.system.TaskStackChangeListener
        public void onActivityRequestedOrientationChanged(final int i, int i2) {
            Optional.ofNullable(ActivityManagerWrapper.getInstance()).map(new Function() { // from class: com.android.systemui.shared.rotation.-$$Lambda$6C9JU6u-zYg5T9BlQa8suAkJ9sY
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((ActivityManagerWrapper) obj).getRunningTask();
                }
            }).ifPresent(new Consumer() { // from class: com.android.systemui.shared.rotation.-$$Lambda$RotationButtonController$TaskStackListenerImpl$ng6C5VVizl0ne23uFS5AodFayDY
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    this.f$0.lambda$onActivityRequestedOrientationChanged$0$RotationButtonController$TaskStackListenerImpl(i, (ActivityManager.RunningTaskInfo) obj);
                }
            });
        }

        public /* synthetic */ void lambda$onActivityRequestedOrientationChanged$0$RotationButtonController$TaskStackListenerImpl(int i, ActivityManager.RunningTaskInfo runningTaskInfo) {
            if (runningTaskInfo.id == i) {
                RotationButtonController.this.setRotateSuggestionButtonState(false);
            }
        }
    }

    enum RotationButtonEvent implements UiEventLogger.UiEventEnum {
        ROTATION_SUGGESTION_SHOWN(206),
        ROTATION_SUGGESTION_ACCEPTED(207);

        private final int mId;

        RotationButtonEvent(int i) {
            this.mId = i;
        }

        public int getId() {
            return this.mId;
        }
    }
}
