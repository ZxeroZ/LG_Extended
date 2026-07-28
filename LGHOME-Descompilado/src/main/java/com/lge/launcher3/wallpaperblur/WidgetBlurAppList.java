package com.lge.launcher3.wallpaperblur;

import android.content.Context;
import com.lge.launcher3.R;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class WidgetBlurAppList {
    private static WidgetBlurAppList sInstance;
    private ArrayList<String> mAppList = new ArrayList<>();

    public static WidgetBlurAppList getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new WidgetBlurAppList(context.getApplicationContext());
        }
        return sInstance;
    }

    private WidgetBlurAppList(Context context) {
        makeAppList(context);
    }

    private void makeAppList(Context context) {
        this.mAppList.clear();
        String[] stringArray = context.getResources().getStringArray(R.array.widget_blur_apps);
        if (stringArray == null) {
            return;
        }
        for (String str : stringArray) {
            if (!this.mAppList.contains(str)) {
                this.mAppList.add(str);
            }
        }
    }

    public boolean contains(String className) {
        return this.mAppList.contains(className);
    }

    public void destroy() {
        ArrayList<String> arrayList = this.mAppList;
        if (arrayList != null) {
            arrayList.clear();
            this.mAppList = null;
        }
        sInstance = null;
    }
}
