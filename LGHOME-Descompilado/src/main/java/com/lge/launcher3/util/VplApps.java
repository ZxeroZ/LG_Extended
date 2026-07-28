package com.lge.launcher3.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import com.lge.launcher3.R;
import java.io.File;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class VplApps {
    private static ArrayList<String> sVplApps = new ArrayList<>();

    public static void init(Context context) {
        makeAppList(context);
    }

    private static void makeAppList(Context context) {
        sVplApps.clear();
        Resources resources = context.getResources();
        AppCrawler.crawl(context, new File(Environment.getRootDirectory(), resources.getString(R.string.vpl_folder_name)), sVplApps);
        String[] stringArray = resources.getStringArray(R.array.vpl_apps);
        if (stringArray == null) {
            return;
        }
        for (String str : stringArray) {
            if (!sVplApps.contains(str)) {
                sVplApps.add(str);
            }
        }
    }

    public static boolean contains(String packageName) {
        return sVplApps.contains(packageName);
    }
}
