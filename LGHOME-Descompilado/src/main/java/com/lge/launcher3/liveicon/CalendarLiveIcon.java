package com.lge.launcher3.liveicon;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.launcher3.FastBitmapDrawable;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class CalendarLiveIcon extends LiveIcon {
    private static final String ACTIVITY_COMPONENT_NAME = "com.android.calendar/.AllInOneActivity";
    private static final String LG_SlimNumber_Bold = "LG_SlimNumber-Bold.ttf";
    private static final String LG_SlimNumber_Medium = "LG_SlimNumber-Medium.ttf";
    private static final String TAG = "LiveIcon.Calendar";
    private ComponentName mActivityComponentName;
    private ViewGroup mCalendarView;
    private Context mContext;
    private TextView mDateView;
    private TextView mDayOfWeekView;

    public String toString() {
        return TAG;
    }

    public CalendarLiveIcon(Context context) {
        super(context);
        this.mContext = context;
        this.mActivityComponentName = ComponentName.unflattenFromString(ACTIVITY_COMPONENT_NAME);
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.liveicon_calendar, (ViewGroup) null);
        this.mCalendarView = viewGroup;
        this.mDayOfWeekView = (TextView) viewGroup.findViewById(R.id.dayofweek);
        this.mDateView = (TextView) this.mCalendarView.findViewById(R.id.date);
        if (Utilities.isLGUI8_0()) {
            this.mDayOfWeekView.setVisibility(8);
            this.mCalendarView.setPadding(0, context.getResources().getDimensionPixelSize(R.dimen.liveIcon_top_padding), 0, 0);
            try {
                this.mDateView.setTypeface(Typeface.createFromAsset(context.getAssets(), LG_SlimNumber_Bold));
                return;
            } catch (Exception e) {
                LGLog.e(TAG, e.toString());
                return;
            }
        }
        this.mDateView.setTypeface(Typeface.createFromAsset(context.getAssets(), LG_SlimNumber_Medium));
    }

    @Override // com.lge.launcher3.liveicon.LiveIcon
    public ComponentName getComponentName() {
        return this.mActivityComponentName;
    }

    @Override // com.lge.launcher3.liveicon.LiveIcon
    protected Drawable getIcon() {
        if (LGHomeFeature.Config.FEATURE_SUPPORT_ICON_FRAMES.getValue()) {
            return DDTUtils.convertToCushionIcon(this.mContext, new AdaptiveIconDrawable(new FastBitmapDrawable(Utilities.loadBitmapFromView(this.mCalendarView, true)), null), this.mContext.getPackageName(), this.mContext.getResources().getIdentifier("lg_iconframe_calendar_home", LauncherConst.RESOURCE_IMAGE_TYPE, this.mContext.getPackageName()));
        }
        return new FastBitmapDrawable(Utilities.loadBitmapFromView(this.mCalendarView, true));
    }

    private String getDayOfWeek() {
        SimpleDateFormat simpleDateFormat;
        Locale locale = Locale.getDefault();
        if (Locale.KOREA.equals(locale) || Locale.KOREAN.equals(locale) || Locale.JAPAN.equals(locale) || Locale.JAPANESE.equals(locale)) {
            simpleDateFormat = new SimpleDateFormat("EEEE", locale);
        } else {
            simpleDateFormat = new SimpleDateFormat("EEE", locale);
        }
        return simpleDateFormat.format(new Date());
    }

    private String getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return String.format(Locale.getDefault(), "%d", Integer.valueOf(calendar.get(5)));
    }

    @Override // com.lge.launcher3.liveicon.LiveIcon
    protected boolean shouldUpdate() {
        return (getDayOfWeek().equals(this.mDayOfWeekView.getText()) && getDate().equals(this.mDateView.getText())) ? false : true;
    }

    @Override // com.lge.launcher3.liveicon.LiveIcon
    protected void updateIconImpl() {
        String dayOfWeek = getDayOfWeek();
        if (!dayOfWeek.equals(this.mDayOfWeekView.getText())) {
            this.mDayOfWeekView.setText(dayOfWeek);
        }
        String date = getDate();
        if (date.equals(this.mDateView.getText())) {
            return;
        }
        this.mDateView.setText(date);
    }

    @Override // com.lge.launcher3.liveicon.LiveIcon
    protected IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIME_SET");
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        intentFilter.addAction("android.intent.action.DATE_CHANGED");
        intentFilter.addAction("android.intent.action.LOCALE_CHANGED");
        return intentFilter;
    }

    @Override // com.lge.launcher3.liveicon.LiveIcon
    public void startEventListening() {
        super.startEventListening();
        startTimeTickUpdate();
    }

    @Override // com.lge.launcher3.liveicon.LiveIcon
    public void stopTimeTickUpdate() {
        super.stopTimeTickUpdate();
        stopTimeTickUpdate();
    }
}
