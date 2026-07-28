package com.android.systemui.plugins.shared;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import java.io.PrintWriter;

/* JADX INFO: loaded from: classes.dex */
public interface LauncherOverlayManager extends Application.ActivityLifecycleCallbacks {

    public interface LauncherOverlay {
        void onScrollChange(float progress, boolean rtl);

        void onScrollInteractionBegin();

        void onScrollInteractionEnd();

        void setOverlayCallbacks(LauncherOverlayCallbacks callbacks);
    }

    public interface LauncherOverlayCallbacks {
        void onScrollChanged(float progress);
    }

    default void dump(String prefix, PrintWriter w) {
    }

    default void hideOverlay(int duration) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    default void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    default void onActivityDestroyed(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    default void onActivityPaused(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    default void onActivityResumed(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    default void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    default void onActivityStarted(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    default void onActivityStopped(Activity activity) {
    }

    default void onAttachedToWindow() {
    }

    default void onDetachedFromWindow() {
    }

    default void onDeviceProvideChanged() {
    }

    default void openOverlay() {
    }

    default boolean startSearch(byte[] config, Bundle extras) {
        return false;
    }

    default void hideOverlay(boolean animate) {
        hideOverlay(animate ? 200 : 0);
    }
}
