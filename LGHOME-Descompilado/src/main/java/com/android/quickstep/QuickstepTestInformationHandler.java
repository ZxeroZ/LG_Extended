package com.android.quickstep;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.testing.TestInformationHandler;
import com.android.launcher3.testing.TestProtocol;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.uioverrides.touchcontrollers.PortraitStatesTouchController;
import com.android.quickstep.util.LayoutUtils;
import java.util.function.Function;

/* JADX INFO: loaded from: classes.dex */
public class QuickstepTestInformationHandler extends TestInformationHandler {
    protected final Context mContext;

    public QuickstepTestInformationHandler(Context context) {
        this.mContext = context;
    }

    @Override // com.android.launcher3.testing.TestInformationHandler
    public Bundle call(String method) {
        Bundle bundle;
        bundle = new Bundle();
        method.hashCode();
        switch (method) {
            case "overview-actions-enabled":
                bundle.putBoolean(TestProtocol.TEST_INFO_RESPONSE_FIELD, FeatureFlags.ENABLE_OVERVIEW_ACTIONS.get());
                return bundle;
            case "hotseat-top":
                return getLauncherUIProperty(new TestInformationHandler.BundleSetter() { // from class: com.android.quickstep.-$$Lambda$QuickstepTestInformationHandler$D6S0dcEO_uKDVVM8mvy9GY8ogPw
                    @Override // com.android.launcher3.testing.TestInformationHandler.BundleSetter
                    public final void set(Bundle bundle2, String str, Object obj) {
                        bundle2.putInt(str, ((Integer) obj).intValue());
                    }
                }, new Function() { // from class: com.android.quickstep.-$$Lambda$fOsbpkslhg8EsoDdrijuxD3oMJw
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        return Integer.valueOf(PortraitStatesTouchController.getHotseatTop((Launcher) obj));
                    }
                });
            case "all-apps-to-overview-swipe-height":
                return getLauncherUIProperty(new TestInformationHandler.BundleSetter() { // from class: com.android.quickstep.-$$Lambda$QuickstepTestInformationHandler$D6S0dcEO_uKDVVM8mvy9GY8ogPw
                    @Override // com.android.launcher3.testing.TestInformationHandler.BundleSetter
                    public final void set(Bundle bundle2, String str, Object obj) {
                        bundle2.putInt(str, ((Integer) obj).intValue());
                    }
                }, new Function() { // from class: com.android.quickstep.-$$Lambda$QuickstepTestInformationHandler$nN0LX2JaOsTH1G6cWAnwY1yubHw
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        Launcher launcher = (Launcher) obj;
                        return Integer.valueOf((int) (launcher.getAllAppsController().getShiftRange() * (LauncherState.OVERVIEW.getVerticalProgress(launcher) - LauncherState.ALL_APPS.getVerticalProgress(launcher))));
                    }
                });
            case "background-to-overview-swipe-height":
                bundle.putInt(TestProtocol.TEST_INFO_RESPONSE_FIELD, LayoutUtils.getShelfTrackingDistance(this.mContext, this.mDeviceProfile, PagedOrientationHandler.PORTRAIT));
                return bundle;
            case "home-to-overview-swipe-height":
                bundle.putInt(TestProtocol.TEST_INFO_RESPONSE_FIELD, (int) LayoutUtils.getDefaultSwipeHeight(this.mContext, this.mDeviceProfile));
                return bundle;
            default:
                return super.call(method);
        }
    }

    @Override // com.android.launcher3.testing.TestInformationHandler
    protected Activity getCurrentActivity() {
        RecentsAnimationDeviceState recentsAnimationDeviceState = new RecentsAnimationDeviceState(this.mContext);
        OverviewComponentObserver overviewComponentObserver = new OverviewComponentObserver(this.mContext, recentsAnimationDeviceState);
        try {
            return overviewComponentObserver.getActivityInterface().getCreatedActivity();
        } finally {
            overviewComponentObserver.onDestroy();
            recentsAnimationDeviceState.destroy();
        }
    }

    @Override // com.android.launcher3.testing.TestInformationHandler
    protected boolean isLauncherInitialized() {
        return super.isLauncherInitialized() && TouchInteractionService.isInitialized();
    }
}
