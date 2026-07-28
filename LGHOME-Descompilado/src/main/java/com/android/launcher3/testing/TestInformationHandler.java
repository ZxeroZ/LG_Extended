package com.android.launcher3.testing;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherState;
import com.android.launcher3.util.ActivityTracker;
import com.android.launcher3.util.Executors;
import com.android.launcher3.util.ResourceBasedOverride;
import com.lge.launcher3.R;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes.dex */
public class TestInformationHandler implements ResourceBasedOverride {
    protected Context mContext;
    protected DeviceProfile mDeviceProfile;
    protected Launcher mLauncher;
    protected LauncherAppState mLauncherAppState;

    public interface BundleSetter<T> {
        void set(Bundle b, String key, T value);
    }

    public static TestInformationHandler newInstance(Context context) {
        return (TestInformationHandler) ResourceBasedOverride.Overrides.getObject(TestInformationHandler.class, context, R.string.test_information_handler_class);
    }

    public void init(Context context) {
        this.mContext = context;
        this.mDeviceProfile = InvariantDeviceProfile.INSTANCE.lambda$get$0$MainThreadInitializedObject(context).getDeviceProfile(context);
        LauncherAppState instanceNoCreate = LauncherAppState.getInstanceNoCreate();
        this.mLauncherAppState = instanceNoCreate;
        this.mLauncher = instanceNoCreate != null ? (Launcher) instanceNoCreate.getModel().getCallback() : null;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public Bundle call(String method) {
        Bundle bundle;
        bundle = new Bundle();
        method.hashCode();
        switch (method) {
            case "disable-debug-tracing":
                TestProtocol.sDebugTracing = false;
                return bundle;
            case "app-list-freeze-flags":
            case "unfreeze-app-list":
            case "freeze-app-list":
                return bundle;
            case "all-apps-to-overview-swipe-height":
                if (this.mLauncher == null) {
                    return null;
                }
                bundle.putInt(TestProtocol.TEST_INFO_RESPONSE_FIELD, (int) (this.mLauncher.getAllAppsController().getShiftRange() * (LauncherState.OVERVIEW.getVerticalProgress(this.mLauncher) - LauncherState.ALL_APPS.getVerticalProgress(this.mLauncher))));
                return bundle;
            case "enable-debug-tracing":
                TestProtocol.sDebugTracing = true;
                return bundle;
            case "mock-sensor-rotation":
                TestProtocol.sDisableSensorRotation = true;
                return bundle;
            case "home-to-all-apps-swipe-height":
                if (this.mLauncher == null) {
                    return null;
                }
                bundle.putInt(TestProtocol.TEST_INFO_RESPONSE_FIELD, (int) (this.mLauncher.getAllAppsController().getShiftRange() * (LauncherState.NORMAL.getVerticalProgress(this.mLauncher) - LauncherState.ALL_APPS.getVerticalProgress(this.mLauncher))));
                return bundle;
            default:
                return null;
        }
    }

    protected boolean isLauncherInitialized() {
        return Launcher.ACTIVITY_TRACKER.getCreatedActivity() == null || LauncherAppState.getInstance(this.mContext).getModel().isModelLoaded();
    }

    protected Activity getCurrentActivity() {
        return Launcher.ACTIVITY_TRACKER.getCreatedActivity();
    }

    public static <T> Bundle getLauncherUIProperty(BundleSetter<T> bundleSetter, Function<Launcher, T> provider) {
        final ActivityTracker<Launcher> activityTracker = Launcher.ACTIVITY_TRACKER;
        Objects.requireNonNull(activityTracker);
        return getUIProperty(bundleSetter, provider, new Supplier() { // from class: com.android.launcher3.testing.-$$Lambda$f-lB3tpKkIK13c8wZQF7R8kOTAc
            @Override // java.util.function.Supplier
            public final Object get() {
                return (Launcher) activityTracker.getCreatedActivity();
            }
        });
    }

    private static <S, T> Bundle getUIProperty(final BundleSetter<T> bundleSetter, final Function<S, T> function, final Supplier<S> supplier) {
        try {
            return (Bundle) Executors.MAIN_EXECUTOR.submit(new Callable() { // from class: com.android.launcher3.testing.-$$Lambda$TestInformationHandler$HmAeR_cft-8-4rQMnu6UDy9McDg
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    return TestInformationHandler.lambda$getUIProperty$0(supplier, function, bundleSetter);
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    static /* synthetic */ Bundle lambda$getUIProperty$0(Supplier supplier, Function function, BundleSetter bundleSetter) throws Exception {
        Object obj = supplier.get();
        if (obj == null) {
            return null;
        }
        Object objApply = function.apply(obj);
        Bundle bundle = new Bundle();
        bundleSetter.set(bundle, TestProtocol.TEST_INFO_RESPONSE_FIELD, objApply);
        return bundle;
    }
}
