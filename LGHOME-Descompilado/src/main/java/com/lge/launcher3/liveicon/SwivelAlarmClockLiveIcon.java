package com.lge.launcher3.liveicon;

import android.content.Context;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.android.launcher3.FastBitmapDrawable;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class SwivelAlarmClockLiveIcon extends AlarmClockLiveIcon {
    private static final String TAG = "SwivelLiveIcon.AlarmClock";
    private Drawable mIconForSwivelAdaptiveIcon;
    private AnalogClockView mSwivelAnalogClockView;

    public SwivelAlarmClockLiveIcon(Context context) {
        super(context);
        this.mSwivelAnalogClockView = (AnalogClockView) LayoutInflater.from(context).inflate(R.layout.liveicon_alarmclock_swivel, (ViewGroup) null);
        updateIconImpl();
    }

    @Override // com.lge.launcher3.liveicon.AlarmClockLiveIcon, com.lge.launcher3.liveicon.LiveIcon
    protected Drawable getIcon() {
        return this.mIconForSwivelAdaptiveIcon;
    }

    @Override // com.lge.launcher3.liveicon.AlarmClockLiveIcon, com.lge.launcher3.liveicon.LiveIcon
    protected void updateIconImpl() {
        AnalogClockView analogClockView = this.mSwivelAnalogClockView;
        if (analogClockView == null) {
            return;
        }
        analogClockView.update();
        this.mIcon = Utilities.loadBitmapFromView(this.mSwivelAnalogClockView, true);
        if (LGHomeFeature.Config.FEATURE_SUPPORT_ICON_FRAMES.getValue()) {
            if (this.mIcon != null) {
                this.mIconForSwivelAdaptiveIcon = DDTUtils.convertToCushionIcon(this.mContext, new AdaptiveIconDrawable(new FastBitmapDrawable(this.mIcon), null), this.mContext.getPackageName(), this.mContext.getResources().getIdentifier("lg_iconframe_clock_home_swivel", LauncherConst.RESOURCE_IMAGE_TYPE, this.mContext.getPackageName()));
                this.mIcon = null;
                return;
            }
            LGLog.i(TAG, "AlarmClock icon is null");
        }
    }

    @Override // com.lge.launcher3.liveicon.AlarmClockLiveIcon, com.lge.launcher3.liveicon.LiveIcon
    protected boolean shouldUpdate() {
        return this.mSwivelAnalogClockView.shouldUpdate();
    }
}
