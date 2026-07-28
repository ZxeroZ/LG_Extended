package com.android.systemui.shared.system;

import android.graphics.Rect;
import com.android.systemui.shared.recents.model.ThumbnailData;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public interface RecentsAnimationListener {
    void onAnimationCanceled(HashMap<Integer, ThumbnailData> map);

    void onAnimationStart(RecentsAnimationControllerCompat recentsAnimationControllerCompat, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, Rect rect, Rect rect2);

    default void onAnimationStart(RecentsAnimationControllerCompat recentsAnimationControllerCompat, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr, RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr2, Rect rect, Rect rect2, int i) {
    }

    default boolean onSwitchToScreenshot(Runnable runnable) {
        return false;
    }

    void onTasksAppeared(RemoteAnimationTargetCompat[] remoteAnimationTargetCompatArr);
}
