package com.android.quickstep.views;

import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.icu.text.MeasureFormat;
import android.icu.util.Measure;
import android.icu.util.MeasureUnit;
import android.os.UserHandle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.Executors;
import com.android.systemui.shared.recents.model.Task;
import com.lge.launcher3.R;
import java.time.Duration;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public final class DigitalWellBeingToast {
    static final int MINUTE_MS = 60000;
    static final Intent OPEN_APP_USAGE_SETTINGS_TEMPLATE = new Intent("android.settings.action.APP_USAGE_SETTINGS");
    private static final String TAG = DigitalWellBeingToast.class.getSimpleName();
    private final BaseDraggingActivity mActivity;
    private long mAppRemainingTimeMs;
    private boolean mHasLimit;
    private final LauncherApps mLauncherApps;
    private Task mTask;
    private final TaskView mTaskView;

    public DigitalWellBeingToast(BaseDraggingActivity activity, TaskView taskView) {
        this.mActivity = activity;
        this.mTaskView = taskView;
        this.mLauncherApps = (LauncherApps) activity.getSystemService(LauncherApps.class);
    }

    private void setTaskFooter(View view) {
        View footer = this.mTaskView.setFooter(0, view);
        if (footer != null) {
            footer.setOnClickListener(null);
            this.mActivity.getViewCache().recycleView(R.layout.digital_wellbeing_toast, footer);
        }
    }

    private void setNoLimit() {
        this.mHasLimit = false;
        this.mTaskView.setContentDescription(this.mTask.titleDescription);
        setTaskFooter(null);
        this.mAppRemainingTimeMs = 0L;
    }

    private void setLimit(long appUsageLimitTimeMs, long appRemainingTimeMs) {
        this.mAppRemainingTimeMs = appRemainingTimeMs;
        this.mHasLimit = true;
        TextView textView = (TextView) this.mActivity.getViewCache().getView(R.layout.digital_wellbeing_toast, this.mActivity, this.mTaskView);
        textView.setText(Utilities.prefixTextWithIcon(this.mActivity, R.drawable.ic_hourglass_top, getText()));
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.quickstep.views.-$$Lambda$Tl6ehnnc_8vj5J3CxPkAzSeeJjE
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.openAppUsageSettings(view);
            }
        });
        setTaskFooter(textView);
        this.mTaskView.setContentDescription(getContentDescriptionForTask(this.mTask, appUsageLimitTimeMs, appRemainingTimeMs));
        RecentsView recentsView = this.mTaskView.getRecentsView();
        if (recentsView != null) {
            recentsView.onDigitalWellbeingToastShown();
        }
    }

    public String getText() {
        return getText(this.mAppRemainingTimeMs);
    }

    public boolean hasLimit() {
        return this.mHasLimit;
    }

    public void initialize(final Task task) {
        this.mTask = task;
        if (task.key.userId != UserHandle.myUserId()) {
            setNoLimit();
        } else {
            Executors.THREAD_POOL_EXECUTOR.execute(new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$DigitalWellBeingToast$44KxgI_Mhk_DEQZbyLvQ0fBDgCY
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$initialize$1$DigitalWellBeingToast(task);
                }
            });
        }
    }

    public /* synthetic */ void lambda$initialize$1$DigitalWellBeingToast(Task task) {
        LauncherApps.AppUsageLimit appUsageLimit = this.mLauncherApps.getAppUsageLimit(task.getTopComponent().getPackageName(), UserHandle.of(task.key.userId));
        final long totalUsageLimit = appUsageLimit != null ? appUsageLimit.getTotalUsageLimit() : -1L;
        final long usageRemaining = appUsageLimit != null ? appUsageLimit.getUsageRemaining() : -1L;
        this.mTaskView.post(new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$DigitalWellBeingToast$EFhrzNFiUvOWhHqlDEU7W8f9bDw
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$initialize$0$DigitalWellBeingToast(totalUsageLimit, usageRemaining);
            }
        });
    }

    public /* synthetic */ void lambda$initialize$0$DigitalWellBeingToast(long j, long j2) {
        if (j < 0 || j2 < 0) {
            setNoLimit();
        } else {
            setLimit(j, j2);
        }
    }

    private String getReadableDuration(Duration duration, MeasureFormat.FormatWidth formatWidthHourAndMinute, int durationLessThanOneMinuteStringId, boolean forceFormatWidth) {
        int intExact = Math.toIntExact(duration.toHours());
        int intExact2 = Math.toIntExact(duration.minusHours(intExact).toMinutes());
        if (intExact > 0 && intExact2 > 0) {
            return MeasureFormat.getInstance(Locale.getDefault(), formatWidthHourAndMinute).formatMeasures(new Measure(Integer.valueOf(intExact), MeasureUnit.HOUR), new Measure(Integer.valueOf(intExact2), MeasureUnit.MINUTE));
        }
        if (intExact > 0) {
            Locale locale = Locale.getDefault();
            if (!forceFormatWidth) {
                formatWidthHourAndMinute = MeasureFormat.FormatWidth.WIDE;
            }
            return MeasureFormat.getInstance(locale, formatWidthHourAndMinute).formatMeasures(new Measure(Integer.valueOf(intExact), MeasureUnit.HOUR));
        }
        if (intExact2 > 0) {
            Locale locale2 = Locale.getDefault();
            if (!forceFormatWidth) {
                formatWidthHourAndMinute = MeasureFormat.FormatWidth.WIDE;
            }
            return MeasureFormat.getInstance(locale2, formatWidthHourAndMinute).formatMeasures(new Measure(Integer.valueOf(intExact2), MeasureUnit.MINUTE));
        }
        if (duration.compareTo(Duration.ZERO) > 0) {
            return this.mActivity.getString(durationLessThanOneMinuteStringId);
        }
        Locale locale3 = Locale.getDefault();
        if (!forceFormatWidth) {
            formatWidthHourAndMinute = MeasureFormat.FormatWidth.WIDE;
        }
        return MeasureFormat.getInstance(locale3, formatWidthHourAndMinute).formatMeasures(new Measure(0, MeasureUnit.MINUTE));
    }

    private String getReadableDuration(Duration duration, MeasureFormat.FormatWidth formatWidthHourAndMinute, int durationLessThanOneMinuteStringId) {
        return getReadableDuration(duration, formatWidthHourAndMinute, durationLessThanOneMinuteStringId, false);
    }

    private String getRoundedUpToMinuteReadableDuration(long remainingTime) {
        if (remainingTime > 60000) {
            remainingTime = (((remainingTime + 60000) - 1) / 60000) * 60000;
        }
        return getReadableDuration(Duration.ofMillis(remainingTime), MeasureFormat.FormatWidth.NARROW, R.string.shorter_duration_less_than_one_minute);
    }

    private String getText(long remainingTime) {
        return this.mActivity.getString(R.string.time_left_for_app, new Object[]{getRoundedUpToMinuteReadableDuration(remainingTime)});
    }

    public void openAppUsageSettings(View view) {
        Intent intentAddFlags = new Intent(OPEN_APP_USAGE_SETTINGS_TEMPLATE).putExtra("android.intent.extra.PACKAGE_NAME", this.mTask.getTopComponent().getPackageName()).addFlags(268468224);
        try {
            BaseActivity baseActivityFromContext = BaseActivity.fromContext(view.getContext());
            baseActivityFromContext.startActivity(intentAddFlags, ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight()).toBundle());
            baseActivityFromContext.getUserEventDispatcher().logActionOnControl(0, 18, view);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Failed to open app usage settings for task " + this.mTask.getTopComponent().getPackageName(), e);
        }
    }

    private String getContentDescriptionForTask(Task task, long appUsageLimitTimeMs, long appRemainingTimeMs) {
        if (appUsageLimitTimeMs >= 0 && appRemainingTimeMs >= 0) {
            return this.mActivity.getString(R.string.task_contents_description_with_remaining_time, new Object[]{task.titleDescription, getText(appRemainingTimeMs)});
        }
        return task.titleDescription;
    }
}
