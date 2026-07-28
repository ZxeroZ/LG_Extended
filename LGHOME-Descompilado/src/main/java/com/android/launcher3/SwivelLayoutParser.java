package com.android.launcher3;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import com.android.launcher3.AutoInstallsLayout;
import com.android.launcher3.LauncherSettings;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.silentota.SilentOTA_Extension;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;
import java.io.IOException;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class SwivelLayoutParser extends AutoInstallsLayout {
    private static final boolean LOGD = false;
    public static final String TAG = "SwivelLayoutParser";

    public SwivelLayoutParser(Context context, AutoInstallsLayout.LayoutParserCallback callback, Resources res, int layoutId, String rootTag) {
        super(context, callback, res, layoutId, rootTag);
    }

    public int loadLayout(SQLiteDatabase db) {
        try {
            return parseLayout(db, this.mLayoutId);
        } catch (Exception e) {
            LGLog.w(TAG, "Got exception parsing layout.", e, new int[0]);
            return -1;
        }
    }

    protected int parseLayout(SQLiteDatabase db, int layoutId) throws XmlPullParserException, IOException {
        XmlResourceParser xml = this.mSourceRes.getXml(layoutId);
        beginDocument(xml, this.mRootTag);
        LGLog.i(TAG, "update promising app list for Swivel layout including PAI");
        mPromisingPackageSet = SilentOTA_Extension.getAddedPromisingPackage();
        int depth = xml.getDepth();
        HashMap<String, AutoInstallsLayout.TagParser> layoutElementsMap = getLayoutElementsMap();
        int andAddNode = 0;
        while (true) {
            int next = xml.next();
            if ((next == 3 && xml.getDepth() <= depth) || next == 1) {
                break;
            }
            if (next == 2) {
                andAddNode += parseAndAddNode(db, xml, layoutElementsMap);
            }
        }
        return andAddNode;
    }

    protected int parseAndAddNode(SQLiteDatabase db, XmlResourceParser parser, HashMap<String, AutoInstallsLayout.TagParser> tagParserMap) throws XmlPullParserException, IOException {
        this.mDb = db;
        this.mValues.clear();
        AutoInstallsLayout.TagParser tagParser = tagParserMap.get(parser.getName());
        if (tagParser == null) {
            return 0;
        }
        if (tagParser.parseAndAdd(parser) >= 0) {
            return 1;
        }
        LGLog.d(TAG, "newElementId is 0");
        return 0;
    }

    @Override // com.android.launcher3.AutoInstallsLayout
    protected HashMap<String, AutoInstallsLayout.TagParser> getLayoutElementsMap() {
        HashMap<String, AutoInstallsLayout.TagParser> map = new HashMap<>();
        map.put("appicon", new AppShortcutParser());
        map.put("autoinstall", new AutoInstallsLayout.AutoInstallParser());
        return map;
    }

    protected long addShortcut(String title, Intent intent) {
        long jGenerateNewItemId = this.mCallback.generateNewItemId();
        this.mValues.put(LauncherSettings.BaseLauncherColumns.INTENT, intent.toUri(0));
        this.mValues.put("title", title);
        this.mValues.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, (Integer) 0);
        this.mValues.put("_id", Long.valueOf(jGenerateNewItemId));
        this.mValues.put("swivelPosition", Long.valueOf(jGenerateNewItemId - 1));
        if (this.mCallback.insertAndCheck(this.mDb, this.mValues) < 0) {
            return -1L;
        }
        return jGenerateNewItemId;
    }

    protected class AppShortcutParser implements AutoInstallsLayout.TagParser {
        protected AppShortcutParser() {
        }

        @Override // com.android.launcher3.AutoInstallsLayout.TagParser
        public long parseAndAdd(XmlResourceParser parser) {
            return parseAndAdd(parser, null);
        }

        @Override // com.android.launcher3.AutoInstallsLayout.TagParser
        public long parseAndAdd(XmlPullParser parser, String sourcePackageName) {
            ActivityInfo activityInfo;
            String string;
            String attributeValue = AutoInstallsLayout.getAttributeValue(parser, LauncherConst.EXTRA_PACKAGE_NAME);
            String attributeValue2 = AutoInstallsLayout.getAttributeValue(parser, LauncherConst.EXTRA_CLASS_NAME);
            if (!TextUtils.isEmpty(attributeValue) && !TextUtils.isEmpty(attributeValue2)) {
                try {
                    ComponentName componentName = new ComponentName(attributeValue, attributeValue2);
                    if (AutoInstallsLayout.mPromisingPackageSet != null && AutoInstallsLayout.mPromisingPackageSet.contains(attributeValue)) {
                        string = AutoInstallsLayout.getAttributeValue(parser, "title");
                        SwivelLayoutParser.this.mValues.put(LauncherSettings.Favorites.RESTORED, (Integer) 256);
                        if (string == null) {
                            string = SwivelLayoutParser.this.mContext.getString(R.string.package_state_unknown);
                        }
                    } else {
                        try {
                        } catch (PackageManager.NameNotFoundException unused) {
                            componentName = new ComponentName(SwivelLayoutParser.this.mPackageManager.currentToCanonicalPackageNames(new String[]{attributeValue})[0], attributeValue2);
                            activityInfo = SwivelLayoutParser.this.mPackageManager.getActivityInfo(componentName, 0);
                        }
                        if (SwivelLayoutParser.this.mHasDeviceOwner && SwivelLayoutParser.this.mAppsListForDeviceOwner.contains(componentName.flattenToString())) {
                            LGLog.i(SwivelLayoutParser.TAG, "Unable to add (" + componentName + ") because device owner");
                            return -1L;
                        }
                        activityInfo = SwivelLayoutParser.this.mPackageManager.getActivityInfo(componentName, 0);
                        string = activityInfo.loadLabel(SwivelLayoutParser.this.mPackageManager).toString();
                    }
                    return SwivelLayoutParser.this.addShortcut(string, new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN, (Uri) null).addCategory("android.intent.category.LAUNCHER").setComponent(componentName).setFlags(270532608));
                } catch (PackageManager.NameNotFoundException e) {
                    LGLog.w(SwivelLayoutParser.TAG, "Unable to add favorite: " + attributeValue + "/" + attributeValue2, e, new int[0]);
                    return -1L;
                }
            }
            return invalidPackageOrClass(parser);
        }

        protected long invalidPackageOrClass(XmlPullParser parser) {
            LGLog.d(SwivelLayoutParser.TAG, "Skipping invalid <favorite> with no component");
            return -1L;
        }
    }
}
