package androidx.core.app;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import androidx.core.util.Pair;

/* JADX INFO: loaded from: classes.dex */
public class ActivityOptionsCompat {
    public static final String EXTRA_USAGE_TIME_REPORT = "android.activity.usage_time";
    public static final String EXTRA_USAGE_TIME_REPORT_PACKAGES = "android.usage_time_packages";

    public Rect getLaunchBounds() {
        return null;
    }

    public void requestUsageTimeReport(PendingIntent receiver) {
    }

    public ActivityOptionsCompat setLaunchBounds(Rect screenSpacePixelRect) {
        return this;
    }

    public Bundle toBundle() {
        return null;
    }

    public void update(ActivityOptionsCompat otherOptions) {
    }

    public static ActivityOptionsCompat makeCustomAnimation(Context context, int enterResId, int exitResId) {
        if (Build.VERSION.SDK_INT >= 16) {
            return new ActivityOptionsCompatImpl(ActivityOptions.makeCustomAnimation(context, enterResId, exitResId));
        }
        return new ActivityOptionsCompat();
    }

    public static ActivityOptionsCompat makeScaleUpAnimation(View source, int startX, int startY, int startWidth, int startHeight) {
        if (Build.VERSION.SDK_INT >= 16) {
            return new ActivityOptionsCompatImpl(ActivityOptions.makeScaleUpAnimation(source, startX, startY, startWidth, startHeight));
        }
        return new ActivityOptionsCompat();
    }

    public static ActivityOptionsCompat makeClipRevealAnimation(View source, int startX, int startY, int width, int height) {
        if (Build.VERSION.SDK_INT >= 23) {
            return new ActivityOptionsCompatImpl(ActivityOptions.makeClipRevealAnimation(source, startX, startY, width, height));
        }
        return new ActivityOptionsCompat();
    }

    public static ActivityOptionsCompat makeThumbnailScaleUpAnimation(View source, Bitmap thumbnail, int startX, int startY) {
        if (Build.VERSION.SDK_INT >= 16) {
            return new ActivityOptionsCompatImpl(ActivityOptions.makeThumbnailScaleUpAnimation(source, thumbnail, startX, startY));
        }
        return new ActivityOptionsCompat();
    }

    public static ActivityOptionsCompat makeSceneTransitionAnimation(Activity activity, View sharedElement, String sharedElementName) {
        if (Build.VERSION.SDK_INT >= 21) {
            return new ActivityOptionsCompatImpl(ActivityOptions.makeSceneTransitionAnimation(activity, sharedElement, sharedElementName));
        }
        return new ActivityOptionsCompat();
    }

    public static ActivityOptionsCompat makeSceneTransitionAnimation(Activity activity, Pair<View, String>... sharedElements) {
        if (Build.VERSION.SDK_INT >= 21) {
            android.util.Pair[] pairArr = null;
            if (sharedElements != null) {
                pairArr = new android.util.Pair[sharedElements.length];
                for (int i = 0; i < sharedElements.length; i++) {
                    pairArr[i] = android.util.Pair.create(sharedElements[i].first, sharedElements[i].second);
                }
            }
            return new ActivityOptionsCompatImpl(ActivityOptions.makeSceneTransitionAnimation(activity, pairArr));
        }
        return new ActivityOptionsCompat();
    }

    public static ActivityOptionsCompat makeTaskLaunchBehind() {
        if (Build.VERSION.SDK_INT >= 21) {
            return new ActivityOptionsCompatImpl(ActivityOptions.makeTaskLaunchBehind());
        }
        return new ActivityOptionsCompat();
    }

    public static ActivityOptionsCompat makeBasic() {
        if (Build.VERSION.SDK_INT >= 23) {
            return new ActivityOptionsCompatImpl(ActivityOptions.makeBasic());
        }
        return new ActivityOptionsCompat();
    }

    private static class ActivityOptionsCompatImpl extends ActivityOptionsCompat {
        private final ActivityOptions mActivityOptions;

        ActivityOptionsCompatImpl(ActivityOptions activityOptions) {
            this.mActivityOptions = activityOptions;
        }

        @Override // androidx.core.app.ActivityOptionsCompat
        public Bundle toBundle() {
            return this.mActivityOptions.toBundle();
        }

        @Override // androidx.core.app.ActivityOptionsCompat
        public void update(ActivityOptionsCompat otherOptions) {
            if (otherOptions instanceof ActivityOptionsCompatImpl) {
                this.mActivityOptions.update(((ActivityOptionsCompatImpl) otherOptions).mActivityOptions);
            }
        }

        @Override // androidx.core.app.ActivityOptionsCompat
        public void requestUsageTimeReport(PendingIntent receiver) {
            if (Build.VERSION.SDK_INT >= 23) {
                this.mActivityOptions.requestUsageTimeReport(receiver);
            }
        }

        @Override // androidx.core.app.ActivityOptionsCompat
        public ActivityOptionsCompat setLaunchBounds(Rect screenSpacePixelRect) {
            return Build.VERSION.SDK_INT < 24 ? this : new ActivityOptionsCompatImpl(this.mActivityOptions.setLaunchBounds(screenSpacePixelRect));
        }

        @Override // androidx.core.app.ActivityOptionsCompat
        public Rect getLaunchBounds() {
            if (Build.VERSION.SDK_INT < 24) {
                return null;
            }
            return this.mActivityOptions.getLaunchBounds();
        }
    }

    protected ActivityOptionsCompat() {
    }
}
