package com.lge.launcher3.smartbulletin.lib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/* JADX INFO: loaded from: classes.dex */
public class Configuration {
    public static final String SMARTBULLETIN_BACKGROUND_COLOR = "background_color";
    public static final String SMARTBULLETIN_COMPONENT_NAME = "component_name";
    public static final String SMARTBULLETIN_CONFIGURATION_SET_COLOR_INTENT = "com.lge.launcher2.smartbulletin.configuration.color";
    public static final String SMARTBULLETIN_TITLE_BACKGROUND_COLOR = "title_background_color";
    public static final String SMARTBULLETIN_TITLE_BACKGROUND_MODE = "title_background_mode";
    public static final String SMARTBULLETIN_TITLE_BACKGROUND_MODE_SPEICAL = "special";
    public static final String SMARTBULLETIN_TITLE_COLOR = "title_color";

    public static void setColor(Context context, ComponentName componentName, int backgroundColor, int titleBackgroundColor, int titleColor) {
        context.sendBroadcast(generateIntent(context, SMARTBULLETIN_CONFIGURATION_SET_COLOR_INTENT, componentName, backgroundColor, titleBackgroundColor, titleColor));
    }

    private static Intent generateIntent(Context context, String action, ComponentName componentName, int backgroundColor, int titleBackgroundColor, int titleColor) {
        Intent intent = new Intent(action);
        intent.putExtra("component_name", componentName.flattenToString());
        intent.putExtra(SMARTBULLETIN_BACKGROUND_COLOR, backgroundColor);
        intent.putExtra(SMARTBULLETIN_TITLE_BACKGROUND_COLOR, titleBackgroundColor);
        intent.putExtra(SMARTBULLETIN_TITLE_COLOR, titleColor);
        return intent;
    }
}
