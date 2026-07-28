package com.lge.launcher3.wing;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;
import com.lge.launcher3.util.PackageUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/* JADX INFO: loaded from: classes2.dex */
public class AppLoader extends AsyncTaskLoader<ArrayList<ApplicationInfo>> {
    private static final String TAG = "AppLoader";
    ArrayList<ApplicationInfo> appList;
    Context mContext;

    public AppLoader(Context context) {
        super(context);
        this.mContext = context;
    }

    /* JADX DEBUG: Method merged with bridge method: loadInBackground()Ljava/lang/Object; */
    @Override // android.content.AsyncTaskLoader
    public ArrayList<ApplicationInfo> loadInBackground() {
        PackageManager packageManager = this.mContext.getPackageManager();
        HashSet<String> hashSet = new HashSet(0);
        this.appList = new ArrayList<>();
        Intent intent = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN, (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        Iterator<ResolveInfo> it = packageManager.queryIntentActivities(intent, 0).iterator();
        while (it.hasNext()) {
            hashSet.add(it.next().activityInfo.packageName);
        }
        try {
            for (String str : hashSet) {
                if (this.appList.size() < 15) {
                    this.appList.add(packageManager.getApplicationInfo(str, 128));
                }
            }
        } catch (PackageManager.NameNotFoundException unused) {
            Log.d(TAG, "package not found : " + hashSet);
        }
        return this.appList;
    }

    @Override // android.content.Loader
    protected void onStartLoading() {
        super.onStartLoading();
        ArrayList<ApplicationInfo> arrayList = this.appList;
        if (arrayList != null && !arrayList.isEmpty()) {
            deliverResult(this.appList);
        } else {
            forceLoad();
        }
    }

    /* JADX DEBUG: Method merged with bridge method: deliverResult(Ljava/lang/Object;)V */
    @Override // android.content.Loader
    public void deliverResult(ArrayList<ApplicationInfo> data) {
        super.deliverResult(data);
    }
}
