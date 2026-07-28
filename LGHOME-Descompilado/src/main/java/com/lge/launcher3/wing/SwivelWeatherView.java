package com.lge.launcher3.wing;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import com.android.launcher3.Utilities;
import com.lge.launcher3.R;
import com.lge.launcher3.adaptive.AdaptiveTextInterface;
import com.lge.launcher3.adaptive.AdaptiveTextUtil;
import com.lge.launcher3.util.LGLog;
import java.util.Date;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class SwivelWeatherView extends LinearLayout implements AdaptiveTextInterface {
    private static final int SHADOW_LARGE_ALPHA = Integer.MAX_VALUE;
    private static final int SHADOW_LARGE_COLOUR = 858796080;
    private static final float SHADOW_LARGE_RADIUS = 5.0f;
    private static final int SHADOW_SMALL_ALPHA = 872415231;
    private static final int SHADOW_SMALL_COLOUR = -2144325584;
    private static final float SHADOW_SMALL_RADIUS = 1.5f;
    private static final float SHADOW_SMALL_Y_OFFSET = 2.5f;
    private static final float SHADOW_Y_OFFSET = 0.0f;
    public static final String TAG = "SwivelWeatherView";
    private Context mContext;
    private TextView mDateView;
    private boolean mIsAdaptiveColor;
    private TextView mTemperatureView;
    private TextClock mTimeView;
    private ImageView mWeatherImageView;

    public SwivelWeatherView(Context context) {
        this(context, null, 0);
    }

    public SwivelWeatherView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwivelWeatherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        this.mIsAdaptiveColor = context.obtainStyledAttributes(attrs, R.styleable.SwivelWeatherView, defStyleAttr, 0).getBoolean(0, true);
        initView();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void initView() {
        removeAllViewsInLayout();
        addView(((LayoutInflater) this.mContext.getSystemService("layout_inflater")).inflate(R.layout.swivel_weather_view, (ViewGroup) this, false));
        this.mTimeView = (TextClock) findViewById(R.id.time_text);
        this.mTemperatureView = (TextView) findViewById(R.id.temperature_view);
        this.mDateView = (TextView) findViewById(R.id.date_text);
        this.mWeatherImageView = (ImageView) findViewById(R.id.weather_image_view);
        setAdaptiveColorForWeatherView();
        LGLog.d(TAG, "initView");
    }

    public void setDate() {
        this.mDateView.setText(DateFormat.format(getResources().getString(R.string.keyguard_notification_date), new Date()));
    }

    public void setTemperatureText(String tempTemperature) {
        String str;
        if (tempTemperature == null || tempTemperature.isEmpty()) {
            return;
        }
        if (Utilities.isArabicFarsi()) {
            str = String.format(Locale.getDefault(), "%d", Long.valueOf(Math.round(Double.parseDouble(tempTemperature)))) + getResources().getString(R.string.swivel_weather_temperature_symbol);
        } else {
            str = Math.round(Double.parseDouble(tempTemperature)) + getResources().getString(R.string.swivel_weather_temperature_symbol);
        }
        this.mTemperatureView.setText(str);
    }

    public void setWeatherImageView(int resId) {
        this.mWeatherImageView.setImageResource(resId);
    }

    private int getAdaptiveTextColor() {
        return AdaptiveTextUtil.getAdaptiveSwivelWeatherColor(getContext());
    }

    @Override // com.lge.launcher3.adaptive.AdaptiveTextInterface
    public void setAdapiveTextColor(int color) {
        if (this.mIsAdaptiveColor) {
            this.mTimeView.setTextColor(color);
            this.mDateView.setTextColor(color);
            this.mTemperatureView.setTextColor(color);
        }
    }

    private int getAdaptiveTextShadowColor(float radius, int color, int adaptiveColor) {
        if (!this.mIsAdaptiveColor) {
            return getModifiedShadowColor(radius, color);
        }
        Resources resources = getResources();
        if (adaptiveColor == resources.getColor(R.color.workspace_adaptive_color2, null)) {
            return getModifiedShadowColor(radius, resources.getColor(R.color.workspace_adaptive_color2_shadow, null));
        }
        if (adaptiveColor == resources.getColor(R.color.workspace_adaptive_color1, null)) {
            return getModifiedShadowColor(radius, resources.getColor(R.color.workspace_adaptive_color1_shadow, null));
        }
        return adaptiveColor == resources.getColor(R.color.workspace_icon_text_color, null) ? getModifiedShadowColor(radius, resources.getColor(R.color.workspace_icon_text_color_shadow, null)) : color;
    }

    private int getModifiedShadowColor(float radius, int resColor) {
        if (Color.alpha(resColor) == 255) {
            return resColor & (radius == SHADOW_LARGE_RADIUS ? Integer.MAX_VALUE : SHADOW_SMALL_ALPHA);
        }
        return resColor;
    }

    public void setAdaptiveColorForWeatherView() {
        int adaptiveTextColor = getAdaptiveTextColor();
        setAdapiveTextColor(adaptiveTextColor);
        LGLog.d(TAG, "setAdaptiveColorForWeatherView : " + adaptiveTextColor);
        int adaptiveTextShadowColor = getAdaptiveTextShadowColor(SHADOW_LARGE_RADIUS, SHADOW_LARGE_COLOUR, adaptiveTextColor);
        this.mTimeView.setShadowLayer(SHADOW_LARGE_RADIUS, 0.0f, 0.0f, adaptiveTextShadowColor);
        this.mDateView.setShadowLayer(SHADOW_LARGE_RADIUS, 0.0f, 0.0f, adaptiveTextShadowColor);
        this.mTemperatureView.setShadowLayer(SHADOW_LARGE_RADIUS, 0.0f, 0.0f, adaptiveTextShadowColor);
    }
}
