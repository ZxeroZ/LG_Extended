package com.lge.launcher3.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import java.io.File;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class AppCrawler {
    public static void crawl(Context context, File file, ArrayList<String> appList) {
        File[] fileArrListFiles;
        PackageManager packageManager = context.getPackageManager();
        if (file.getName().endsWith(".apk") && !file.isDirectory()) {
            PackageInfo packageArchiveInfo = packageManager.getPackageArchiveInfo(file.getAbsolutePath(), 1);
            if (packageArchiveInfo != null) {
                appList.add(packageArchiveInfo.packageName);
                return;
            }
            return;
        }
        if (!file.isDirectory() || (fileArrListFiles = file.listFiles()) == null) {
            return;
        }
        for (File file2 : fileArrListFiles) {
            crawl(context, file2, appList);
        }
    }
}
