package com.android.launcher3.touch;

import android.content.res.Resources;
import android.graphics.PointF;
import android.view.View;
import com.android.launcher3.Utilities;
import com.android.launcher3.touch.SingleAxisSwipeDetector;

/* JADX INFO: loaded from: classes.dex */
public class SeascapePagedViewHandler extends LandscapePagedViewHandler {
    @Override // com.android.launcher3.touch.LandscapePagedViewHandler, com.android.launcher3.touch.PagedOrientationHandler
    public float getDegreesRotated() {
        return 270.0f;
    }

    @Override // com.android.launcher3.touch.LandscapePagedViewHandler, com.android.launcher3.touch.PagedOrientationHandler
    public int getRotation() {
        return 3;
    }

    @Override // com.android.launcher3.touch.LandscapePagedViewHandler, com.android.launcher3.touch.PagedOrientationHandler
    public int getTaskDismissDirectionFactor() {
        return -1;
    }

    @Override // com.android.launcher3.touch.LandscapePagedViewHandler, com.android.launcher3.touch.PagedOrientationHandler
    public int getTaskDragDisplacementFactor(boolean isRtl) {
        return isRtl ? -1 : 1;
    }

    @Override // com.android.launcher3.touch.LandscapePagedViewHandler, com.android.launcher3.touch.PagedOrientationHandler
    public float getTaskMenuX(float x, View thumbnailView) {
        return x;
    }

    @Override // com.android.launcher3.touch.LandscapePagedViewHandler, com.android.launcher3.touch.PagedOrientationHandler
    public boolean isGoingUp(float displacement, boolean isRtl) {
        if (isRtl) {
            if (displacement > 0.0f) {
                return true;
            }
        } else if (displacement < 0.0f) {
            return true;
        }
        return false;
    }

    @Override // com.android.launcher3.touch.LandscapePagedViewHandler, com.android.launcher3.touch.PagedOrientationHandler
    public boolean getRecentsRtlSetting(Resources resources) {
        return Utilities.isRtl(resources);
    }

    @Override // com.android.launcher3.touch.LandscapePagedViewHandler, com.android.launcher3.touch.PagedOrientationHandler
    public void adjustFloatingIconStartVelocity(PointF velocity) {
        velocity.set(velocity.y, -velocity.x);
    }

    @Override // com.android.launcher3.touch.LandscapePagedViewHandler, com.android.launcher3.touch.PagedOrientationHandler
    public float getTaskMenuY(float y, View thumbnailView) {
        return y + thumbnailView.getMeasuredHeight();
    }

    @Override // com.android.launcher3.touch.LandscapePagedViewHandler, com.android.launcher3.touch.PagedOrientationHandler
    public void setPrimaryAndResetSecondaryTranslate(View view, float translation) {
        view.setTranslationX(0.0f);
        view.setTranslationY(translation);
    }

    @Override // com.android.launcher3.touch.LandscapePagedViewHandler, com.android.launcher3.touch.PagedOrientationHandler
    public SingleAxisSwipeDetector.Direction getOppositeSwipeDirection(boolean isRtl) {
        return isRtl ? SingleAxisSwipeDetector.HORIZONTAL : SingleAxisSwipeDetector.SEASCAPE_HORIZONTAL;
    }
}
