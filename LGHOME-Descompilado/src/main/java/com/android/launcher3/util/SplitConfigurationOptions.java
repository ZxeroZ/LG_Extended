package com.android.launcher3.util;

import android.graphics.Rect;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
public final class SplitConfigurationOptions {
    public static final float DEFAULT_SPLIT_RATIO = 0.5f;
    public static final int STAGE_POSITION_BOTTOM_OR_RIGHT = 1;
    public static final int STAGE_POSITION_TOP_OR_LEFT = 0;
    public static final int STAGE_POSITION_UNDEFINED = -1;
    public static final int STAGE_TYPE_MAIN = 0;
    public static final int STAGE_TYPE_SIDE = 1;
    public static final int STAGE_TYPE_UNDEFINED = -1;

    @Retention(RetentionPolicy.SOURCE)
    public @interface StagePosition {
    }

    public @interface StageType {
    }

    public static class StagedSplitTaskPosition {
        public int taskId = -1;
        public int stagePosition = -1;
        public int stageType = -1;
    }

    public static class SplitPositionOption {
        public final int iconResId;
        public final int mStageType;
        public final int stagePosition;
        public final int textResId;

        public SplitPositionOption(int iconResId, int textResId, int stagePosition, int stageType) {
            this.iconResId = iconResId;
            this.textResId = textResId;
            this.stagePosition = stagePosition;
            this.mStageType = stageType;
        }
    }

    public static class StagedSplitBounds {
        public final boolean appsStackedVertically;
        public final float dividerHeightPercent;
        public final float dividerWidthPercent;
        public final boolean initiatedFromSeascape;
        public final float leftTaskPercent;
        public final Rect leftTopBounds;
        public final int leftTopTaskId;
        public final Rect rightBottomBounds;
        public final int rightBottomTaskId;
        public final float topTaskPercent;
        public final Rect visualDividerBounds;

        public StagedSplitBounds(Rect leftTopBounds, Rect rightBottomBounds, int leftTopTaskId, int rightBottomTaskId) {
            this.leftTopBounds = leftTopBounds;
            this.rightBottomBounds = rightBottomBounds;
            this.leftTopTaskId = leftTopTaskId;
            this.rightBottomTaskId = rightBottomTaskId;
            if (rightBottomBounds.top > leftTopBounds.top) {
                this.visualDividerBounds = new Rect(leftTopBounds.left, leftTopBounds.bottom, leftTopBounds.right, rightBottomBounds.top);
                this.appsStackedVertically = true;
                this.initiatedFromSeascape = false;
            } else {
                this.visualDividerBounds = new Rect(leftTopBounds.right, leftTopBounds.top, rightBottomBounds.left, leftTopBounds.bottom);
                this.appsStackedVertically = false;
                if (rightBottomBounds.width() > leftTopBounds.width()) {
                    this.initiatedFromSeascape = true;
                } else {
                    this.initiatedFromSeascape = false;
                }
            }
            float f = rightBottomBounds.right - leftTopBounds.left;
            float f2 = rightBottomBounds.bottom - leftTopBounds.top;
            this.leftTaskPercent = leftTopBounds.width() / f;
            this.topTaskPercent = leftTopBounds.height() / f2;
            this.dividerWidthPercent = this.visualDividerBounds.width() / f;
            this.dividerHeightPercent = this.visualDividerBounds.height() / f2;
        }
    }
}
