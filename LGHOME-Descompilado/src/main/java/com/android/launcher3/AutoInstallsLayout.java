package com.android.launcher3;

import android.appwidget.AppWidgetHost;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import com.android.launcher3.LauncherProvider;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.model.data.ItemInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.folder.FolderColorUtil;
import com.lge.launcher3.receiver.DefaultWorkspaceLoader;
import com.lge.launcher3.silentota.SilentOTA_Extension;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.ManagedProfileUtils;
import com.lge.launcher3.util.PackageUtils;
import com.lge.os.Build;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class AutoInstallsLayout {
    private static final String ACTION_APPWIDGET_DEFAULT_WORKSPACE_CONFIGURE = "com.android.launcher.action.APPWIDGET_DEFAULT_WORKSPACE_CONFIGURE";
    static final String ACTION_LAUNCHER_CUSTOMIZATION = "android.autoinstalls.config.action.PLAY_AUTO_INSTALL";
    protected static final String ATTR_CLASS_NAME = "className";
    private static final String ATTR_COLOR = "color";
    private static final String ATTR_CONTAINER = "container";
    private static final String ATTR_ICON = "icon";
    private static final String ATTR_KEY = "key";
    protected static final String ATTR_PACKAGE_NAME = "packageName";
    private static final String ATTR_RANK = "rank";
    private static final String ATTR_SCREEN = "screen";
    private static final String ATTR_SPAN_X = "spanX";
    private static final String ATTR_SPAN_Y = "spanY";
    protected static final String ATTR_TITLE = "title";
    private static final String ATTR_URL = "url";
    private static final String ATTR_VALUE = "value";
    protected static final String ATTR_WORKSPACE = "workspace";
    protected static final String ATTR_X = "x";
    protected static final String ATTR_Y = "y";
    private static final String FORMATTED_LAYOUT_RES = "default_layout_%dx%d";
    private static final String FORMATTED_LAYOUT_RES_WITH_HOSTEAT = "default_layout_%dx%d_h%s";
    private static final String HOTSEAT_ADDED = "A";
    private static final String HOTSEAT_DELETED = "D";
    private static final String HOTSEAT_EMPTY = "E";
    private static final String LAYOUT_RES = "default_layout";
    private static final boolean LOGD = false;
    private static final String TAG = "AutoInstalls";
    private static final String TAG_APPWIDGET = "appwidget";
    protected static final String TAG_APP_ICON = "appicon";
    protected static final String TAG_AUTO_INSTALL = "autoinstall";
    private static final String TAG_EXTRA = "extra";
    private static final String TAG_FOLDER = "folder";
    protected static final String TAG_INCLUDE = "include";
    private static final String TAG_SHORTCUT = "shortcut";
    private static final String TAG_WORKSPACE = "workspace";
    private static final String WORKSPACE_ALLAPPS_LAYOUT_RES = "default_workspace_items_allapps";
    private static final String WORKSPACE_LAYOUT_RES = "default_workspace_items";
    private static final String WORKSPACE_SWIVEL_LAYOUT_RES = "default_workspace_items_swivel";
    final AppWidgetHost mAppWidgetHost;
    protected TreeSet<String> mAppsListForDeviceOwner;
    protected final LayoutParserCallback mCallback;
    final Context mContext;
    protected SQLiteDatabase mDb;
    protected boolean mHasDeviceOwner;
    private final int mHotseatAllAppsRank;
    private ArrayList<String> mHotseatLocationInfo;
    protected final int mLayoutId;
    protected final PackageManager mPackageManager;
    protected final String mRootTag;
    protected final Resources mSourceRes;
    private final long[] mTemp;
    protected final ContentValues mValues;
    private static final String HOTSEAT_CONTAINER_NAME = LauncherSettings.Favorites.containerToString(LauncherSettings.Favorites.CONTAINER_HOTSEAT);
    protected static HashSet<String> mPromisingPackageSet = new HashSet<>();

    public interface LayoutParserCallback {
        long generateNewItemId();

        long insertAndCheck(SQLiteDatabase db, ContentValues values);
    }

    protected interface TagParser {
        long parseAndAdd(XmlResourceParser parser) throws XmlPullParserException, IOException;

        long parseAndAdd(XmlPullParser parser, String sourcePackageName) throws XmlPullParserException, IOException;
    }

    static AutoInstallsLayout get(Context context, AppWidgetHost appWidgetHost, LayoutParserCallback callback) {
        Pair<String, Resources> pairFindSystemApk = Utilities.findSystemApk(ACTION_LAUNCHER_CUSTOMIZATION, context.getPackageManager());
        if (pairFindSystemApk == null) {
            return null;
        }
        return get(context, (String) pairFindSystemApk.first, (Resources) pairFindSystemApk.second, appWidgetHost, callback);
    }

    static AutoInstallsLayout get(Context context, String pkg, Resources targetRes, AppWidgetHost appWidgetHost, LayoutParserCallback callback) {
        LauncherAppState.getIDP(context);
        if ("KR".equals(Build.CA_TARGET.COUNTRY) || LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
            if (PackageUtils.isPackageInstalled(context, "com.lge.appbox.client")) {
                mPromisingPackageSet = SilentOTA_Extension.makeAddedPromisingPackage(context);
            } else {
                LGLog.i(TAG, "kidd skip to create promising app list");
            }
        }
        String layoutNameFromCAList = DefaultWorkspaceLoader.getLayoutNameFromCAList(context);
        int identifier = !TextUtils.isEmpty(layoutNameFromCAList) ? targetRes.getIdentifier(layoutNameFromCAList, "xml", pkg) : 0;
        if (identifier == 0 && !LGHomeFeature.isDisableAllApps()) {
            LGLog.d(TAG, "Trying All-Apps layout : " + pkg);
            identifier = targetRes.getIdentifier(WORKSPACE_ALLAPPS_LAYOUT_RES, "xml", pkg);
        }
        if (identifier == 0) {
            LGLog.d(TAG, layoutNameFromCAList + " not found. Trying the default workspace layout");
            identifier = targetRes.getIdentifier(WORKSPACE_LAYOUT_RES, "xml", pkg);
        }
        if (identifier == 0) {
            LGLog.d(TAG, layoutNameFromCAList + " not found. Trying the default layout");
            identifier = targetRes.getIdentifier(LAYOUT_RES, "xml", pkg);
        }
        int i = identifier;
        if (i == 0 || LGHomeFeature.Config.FEATURE_USE_LGHOME_LAYOUT.getValue()) {
            LGLog.e(TAG, "Layout definition not found in package: " + pkg);
            return null;
        }
        return new AutoInstallsLayout(context, appWidgetHost, callback, targetRes, i, "workspace");
    }

    public AutoInstallsLayout(Context context, AppWidgetHost appWidgetHost, LayoutParserCallback callback, Resources res, int layoutId, String rootTag) {
        this(context, appWidgetHost, callback, res, layoutId, rootTag, LauncherAppState.getIDP(context).hotseatAllAppsRank);
    }

    public AutoInstallsLayout(Context context, AppWidgetHost appWidgetHost, LayoutParserCallback callback, Resources res, int layoutId, String rootTag, int hotseatAllAppsRank) {
        this.mTemp = new long[2];
        this.mHasDeviceOwner = false;
        this.mAppsListForDeviceOwner = null;
        this.mHotseatLocationInfo = new ArrayList<>();
        this.mContext = context;
        this.mAppWidgetHost = appWidgetHost;
        this.mCallback = callback;
        this.mPackageManager = context.getPackageManager();
        this.mValues = new ContentValues();
        this.mRootTag = rootTag;
        this.mSourceRes = res;
        this.mLayoutId = layoutId;
        this.mHotseatAllAppsRank = hotseatAllAppsRank;
        int i = (int) context.getResources().getFloat(R.dimen.device_profile_numHotseatIcons);
        for (int i2 = 0; i2 < i; i2++) {
            this.mHotseatLocationInfo.add(HOTSEAT_EMPTY);
        }
        initExcludeListForDeviceOwner();
    }

    public AutoInstallsLayout(Context context, LayoutParserCallback callback, Resources res, int layoutId, String rootTag) {
        this.mTemp = new long[2];
        this.mHasDeviceOwner = false;
        this.mAppsListForDeviceOwner = null;
        this.mHotseatLocationInfo = new ArrayList<>();
        this.mContext = context;
        this.mAppWidgetHost = null;
        this.mCallback = callback;
        this.mPackageManager = context.getPackageManager();
        this.mValues = new ContentValues();
        this.mRootTag = rootTag;
        this.mSourceRes = res;
        this.mLayoutId = layoutId;
        this.mHotseatAllAppsRank = 0;
        initExcludeListForDeviceOwner();
    }

    private void initExcludeListForDeviceOwner() {
        this.mHasDeviceOwner = ManagedProfileUtils.hasDeviceOwner(this.mContext);
        String[] stringArray = this.mContext.getResources().getStringArray(R.array.lg_exclude_apps_device_owner);
        this.mAppsListForDeviceOwner = new TreeSet<>();
        if (stringArray != null) {
            for (String str : stringArray) {
                if (str != null && str.length() != 0) {
                    this.mAppsListForDeviceOwner.add(str);
                }
            }
        }
    }

    public int loadLayout(SQLiteDatabase db, ArrayList<Long> screenIds) {
        this.mDb = db;
        try {
            return parseLayout(this.mLayoutId, screenIds);
        } catch (Exception e) {
            Log.w(TAG, "Got exception parsing layout.", e);
            return -1;
        }
    }

    protected int parseLayout(int layoutId, ArrayList<Long> screenIds) throws XmlPullParserException, IOException {
        XmlResourceParser xml = this.mSourceRes.getXml(layoutId);
        beginDocument(xml, this.mRootTag);
        int depth = xml.getDepth();
        HashMap<String, TagParser> layoutElementsMap = getLayoutElementsMap();
        int andAddNode = 0;
        while (true) {
            int next = xml.next();
            if ((next == 3 && xml.getDepth() <= depth) || next == 1) {
                break;
            }
            if (next == 2) {
                andAddNode += parseAndAddNode(xml, layoutElementsMap, screenIds);
            }
        }
        return andAddNode;
    }

    protected void parseContainerAndScreen(XmlResourceParser parser, long[] out) {
        if (HOTSEAT_CONTAINER_NAME.equals(getAttributeValue(parser, "container"))) {
            out[0] = -101;
            out[1] = Long.parseLong(getAttributeValue(parser, "rank"));
        } else {
            out[0] = -100;
            out[1] = Long.parseLong(getAttributeValue(parser, "screen"));
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x00ff A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0101  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected int parseAndAddNode(android.content.res.XmlResourceParser r21, java.util.HashMap<java.lang.String, com.android.launcher3.AutoInstallsLayout.TagParser> r22, java.util.ArrayList<java.lang.Long> r23) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            r20 = this;
            r0 = r20
            r1 = r21
            r2 = r23
            java.lang.String r3 = r21.getName()
            java.lang.String r4 = "include"
            boolean r3 = r4.equals(r3)
            r4 = 0
            if (r3 == 0) goto L22
            java.lang.String r3 = "workspace"
            int r1 = getAttributeResourceValue(r1, r3, r4)
            if (r1 == 0) goto L21
            int r1 = r0.parseLayout(r1, r2)
            return r1
        L21:
            return r4
        L22:
            android.content.ContentValues r3 = r0.mValues
            r3.clear()
            long[] r3 = r0.mTemp
            r0.parseContainerAndScreen(r1, r3)
            long[] r3 = r0.mTemp
            r5 = r3[r4]
            r7 = 1
            r8 = r3[r7]
            android.content.ContentValues r3 = r0.mValues
            java.lang.Long r10 = java.lang.Long.valueOf(r5)
            java.lang.String r11 = "container"
            r3.put(r11, r10)
            android.content.ContentValues r3 = r0.mValues
            java.lang.Long r10 = java.lang.Long.valueOf(r8)
            java.lang.String r11 = "screen"
            r3.put(r11, r10)
            java.lang.String r3 = "x"
            java.lang.String r3 = getAttributeValue(r1, r3)
            int r10 = java.lang.Integer.parseInt(r3)
            r12 = -101(0xffffffffffffff9b, double:NaN)
            int r12 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
            java.lang.String r13 = "D"
            java.lang.String r14 = "]["
            java.lang.String r15 = "packageName"
            java.lang.String r7 = "cellX"
            java.lang.String r4 = "]"
            if (r12 != 0) goto Ld8
            r16 = r12
            java.util.ArrayList<java.lang.String> r12 = r0.mHotseatLocationInfo
            int r12 = r12.size()
            r17 = r5
            if (r10 >= r12) goto Ldc
            r5 = 0
            r12 = 0
        L72:
            if (r12 >= r10) goto L85
            java.util.ArrayList<java.lang.String> r6 = r0.mHotseatLocationInfo
            java.lang.Object r6 = r6.get(r12)
            boolean r6 = r13.equals(r6)
            if (r6 == 0) goto L82
            int r5 = r5 + 1
        L82:
            int r12 = r12 + 1
            goto L72
        L85:
            if (r5 <= 0) goto Ld0
            int r3 = r10 - r5
            android.content.ContentValues r6 = r0.mValues
            java.lang.String r12 = java.lang.Integer.toString(r3)
            r6.put(r7, r12)
            android.content.ContentValues r6 = r0.mValues
            r19 = r13
            long r12 = (long) r3
            java.lang.Long r7 = java.lang.Long.valueOf(r12)
            r6.put(r11, r7)
            java.lang.String r6 = getAttributeValue(r1, r15)
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r11 = "modified["
            r7.append(r11)
            r7.append(r3)
            java.lang.String r3 = "="
            r7.append(r3)
            r7.append(r10)
            java.lang.String r3 = "-"
            r7.append(r3)
            r7.append(r5)
            r7.append(r14)
            r7.append(r6)
            r7.append(r4)
            java.lang.String r3 = r7.toString()
            r0.printHotseatInfo(r3)
            goto Le3
        Ld0:
            r19 = r13
            android.content.ContentValues r5 = r0.mValues
            r5.put(r7, r3)
            goto Le3
        Ld8:
            r17 = r5
            r16 = r12
        Ldc:
            r19 = r13
            android.content.ContentValues r5 = r0.mValues
            r5.put(r7, r3)
        Le3:
            android.content.ContentValues r3 = r0.mValues
            java.lang.String r5 = "y"
            java.lang.String r5 = getAttributeValue(r1, r5)
            java.lang.String r6 = "cellY"
            r3.put(r6, r5)
            java.lang.String r3 = r21.getName()
            r5 = r22
            java.lang.Object r3 = r5.get(r3)
            com.android.launcher3.AutoInstallsLayout$TagParser r3 = (com.android.launcher3.AutoInstallsLayout.TagParser) r3
            if (r3 != 0) goto L101
            r5 = 0
            return r5
        L101:
            long r5 = r3.parseAndAdd(r1)
            r11 = 0
            int r3 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
            java.lang.String r5 = "A"
            if (r3 < 0) goto L156
            java.lang.Long r3 = java.lang.Long.valueOf(r8)
            boolean r3 = r2.contains(r3)
            if (r3 != 0) goto L124
            r6 = -100
            int r3 = (r17 > r6 ? 1 : (r17 == r6 ? 0 : -1))
            if (r3 != 0) goto L124
            java.lang.Long r3 = java.lang.Long.valueOf(r8)
            r2.add(r3)
        L124:
            if (r16 != 0) goto L154
            java.util.ArrayList<java.lang.String> r2 = r0.mHotseatLocationInfo
            int r2 = r2.size()
            if (r10 >= r2) goto L154
            java.util.ArrayList<java.lang.String> r2 = r0.mHotseatLocationInfo
            r2.set(r10, r5)
            java.lang.String r1 = getAttributeValue(r1, r15)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "add["
            r2.append(r3)
            r2.append(r10)
            r2.append(r14)
            r2.append(r1)
            r2.append(r4)
            java.lang.String r1 = r2.toString()
            r0.printHotseatInfo(r1)
        L154:
            r1 = 1
            return r1
        L156:
            if (r16 != 0) goto L1ac
            java.lang.String r1 = getAttributeValue(r1, r15)
            java.util.ArrayList<java.lang.String> r2 = r0.mHotseatLocationInfo
            int r2 = r2.size()
            if (r10 >= r2) goto L195
            java.util.ArrayList<java.lang.String> r2 = r0.mHotseatLocationInfo
            java.lang.Object r2 = r2.get(r10)
            boolean r2 = r5.equals(r2)
            if (r2 != 0) goto L195
            java.util.ArrayList<java.lang.String> r2 = r0.mHotseatLocationInfo
            r3 = r19
            r2.set(r10, r3)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "deleted["
            r2.append(r3)
            r2.append(r10)
            r2.append(r14)
            r2.append(r1)
            r2.append(r4)
            java.lang.String r1 = r2.toString()
            r0.printHotseatInfo(r1)
            goto L1ac
        L195:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "deleted[empty]["
            r2.append(r3)
            r2.append(r1)
            r2.append(r4)
            java.lang.String r1 = r2.toString()
            r0.printHotseatInfo(r1)
        L1ac:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.AutoInstallsLayout.parseAndAddNode(android.content.res.XmlResourceParser, java.util.HashMap, java.util.ArrayList):int");
    }

    void printHotseatInfo(String from) {
        StringBuilder sb = new StringBuilder(from + ": ");
        for (int i = 0; i < this.mHotseatLocationInfo.size(); i++) {
            sb.append("[" + this.mHotseatLocationInfo.get(i) + "]");
        }
        LGLog.i(TAG, "[HOTSEAT_INFO] " + ((Object) sb));
    }

    protected long addShortcut(String title, Intent intent, int type) {
        long jGenerateNewItemId = this.mCallback.generateNewItemId();
        this.mValues.put(LauncherSettings.BaseLauncherColumns.INTENT, intent.toUri(0));
        this.mValues.put("title", title);
        this.mValues.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, Integer.valueOf(type));
        this.mValues.put("_id", Long.valueOf(jGenerateNewItemId));
        this.mValues.put("spanX", (Integer) 1);
        this.mValues.put("spanY", (Integer) 1);
        if (this.mCallback.insertAndCheck(this.mDb, this.mValues) < 0) {
            return -1L;
        }
        return jGenerateNewItemId;
    }

    protected HashMap<String, TagParser> getFolderElementsMap() {
        HashMap<String, TagParser> map = new HashMap<>();
        map.put(TAG_APP_ICON, new AppShortcutParser());
        map.put(TAG_AUTO_INSTALL, new AutoInstallParser());
        map.put(TAG_SHORTCUT, new ShortcutParser(this.mSourceRes));
        return map;
    }

    protected HashMap<String, TagParser> getLayoutElementsMap() {
        HashMap<String, TagParser> map = new HashMap<>();
        map.put(TAG_APP_ICON, new AppShortcutParser());
        map.put(TAG_AUTO_INSTALL, new AutoInstallParser());
        map.put(TAG_FOLDER, new FolderParser(this));
        map.put(TAG_APPWIDGET, new PendingWidgetParser());
        map.put(TAG_SHORTCUT, new ShortcutParser(this.mSourceRes));
        return map;
    }

    protected class AppShortcutParser implements TagParser {
        protected long invalidPackageOrClass(XmlPullParser parser) {
            return -1L;
        }

        protected AppShortcutParser() {
        }

        @Override // com.android.launcher3.AutoInstallsLayout.TagParser
        public long parseAndAdd(XmlResourceParser parser) {
            return parseAndAdd(parser, null);
        }

        @Override // com.android.launcher3.AutoInstallsLayout.TagParser
        public long parseAndAdd(XmlPullParser parser, String sourcePackageName) {
            String attributeValue;
            ActivityInfo activityInfo;
            String attributeValue2 = AutoInstallsLayout.getAttributeValue(parser, "packageName");
            String attributeValue3 = AutoInstallsLayout.getAttributeValue(parser, "className");
            boolean zEquals = "true".equals(AutoInstallsLayout.getAttributeValue(parser, "promised"));
            if (!TextUtils.isEmpty(attributeValue2) && !TextUtils.isEmpty(attributeValue3)) {
                try {
                    ComponentName componentName = new ComponentName(attributeValue2, attributeValue3);
                    if (zEquals || ("KR".equals(Build.CA_TARGET.COUNTRY) && AutoInstallsLayout.mPromisingPackageSet != null && AutoInstallsLayout.mPromisingPackageSet.contains(attributeValue2))) {
                        attributeValue = AutoInstallsLayout.getAttributeValue(parser, "title");
                        AutoInstallsLayout.this.mValues.put(LauncherSettings.Favorites.RESTORED, (Integer) 256);
                        if (attributeValue == null) {
                            attributeValue = AutoInstallsLayout.this.mContext.getString(R.string.package_state_unknown);
                        }
                        LGLog.i(AutoInstallsLayout.TAG, "Add promising app list from Update Center : " + attributeValue2 + " / " + attributeValue);
                    } else {
                        try {
                        } catch (PackageManager.NameNotFoundException unused) {
                            componentName = new ComponentName(AutoInstallsLayout.this.mPackageManager.currentToCanonicalPackageNames(new String[]{attributeValue2})[0], attributeValue3);
                            activityInfo = AutoInstallsLayout.this.mPackageManager.getActivityInfo(componentName, 0);
                        }
                        if (AutoInstallsLayout.this.mHasDeviceOwner && AutoInstallsLayout.this.mAppsListForDeviceOwner.contains(componentName.flattenToString())) {
                            LGLog.i(AutoInstallsLayout.TAG, "Unable to add (" + componentName + ") because device owner");
                            return -1L;
                        }
                        activityInfo = AutoInstallsLayout.this.mPackageManager.getActivityInfo(componentName, 0);
                        attributeValue = activityInfo.loadLabel(AutoInstallsLayout.this.mPackageManager).toString();
                    }
                    return AutoInstallsLayout.this.addShortcut(attributeValue, new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN, (Uri) null).addCategory("android.intent.category.LAUNCHER").setComponent(componentName).setFlags(270532608), 0);
                } catch (PackageManager.NameNotFoundException unused2) {
                    return -1L;
                }
            }
            return invalidPackageOrClass(parser);
        }
    }

    protected class AutoInstallParser implements TagParser {
        protected AutoInstallParser() {
        }

        @Override // com.android.launcher3.AutoInstallsLayout.TagParser
        public long parseAndAdd(XmlResourceParser parser) {
            return parseAndAdd(parser, null);
        }

        @Override // com.android.launcher3.AutoInstallsLayout.TagParser
        public long parseAndAdd(XmlPullParser parser, String sourcePackageName) {
            String attributeValue = AutoInstallsLayout.getAttributeValue(parser, "packageName");
            String attributeValue2 = AutoInstallsLayout.getAttributeValue(parser, "className");
            if (TextUtils.isEmpty(attributeValue) || TextUtils.isEmpty(attributeValue2)) {
                return -1L;
            }
            AutoInstallsLayout.this.mValues.put(LauncherSettings.Favorites.RESTORED, (Integer) 2);
            if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
                SilentOTA_Extension.addPromisingPackageFromPAI(attributeValue);
                LGLog.i(AutoInstallsLayout.TAG, "Add promising app list from PAI : " + attributeValue);
            }
            Intent flags = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN, (Uri) null).addCategory("android.intent.category.LAUNCHER").setComponent(new ComponentName(attributeValue, attributeValue2)).setFlags(270532608);
            AutoInstallsLayout autoInstallsLayout = AutoInstallsLayout.this;
            return autoInstallsLayout.addShortcut(autoInstallsLayout.mContext.getString(R.string.package_state_unknown), flags, 0);
        }
    }

    protected class ShortcutParser implements TagParser {
        private final Resources mIconRes;

        public ShortcutParser(Resources iconRes) {
            this.mIconRes = iconRes;
        }

        @Override // com.android.launcher3.AutoInstallsLayout.TagParser
        public long parseAndAdd(XmlResourceParser parser) {
            Intent intent;
            Drawable drawable;
            int attributeResourceValue = AutoInstallsLayout.getAttributeResourceValue(parser, "title", 0);
            int attributeResourceValue2 = AutoInstallsLayout.getAttributeResourceValue(parser, "icon", 0);
            if (attributeResourceValue == 0 || attributeResourceValue2 == 0 || (intent = parseIntent(parser)) == null || (drawable = this.mIconRes.getDrawable(attributeResourceValue2)) == null) {
                return -1L;
            }
            ItemInfo.writeBitmap(AutoInstallsLayout.this.mValues, Utilities.createIconBitmap(drawable, AutoInstallsLayout.this.mContext));
            AutoInstallsLayout.this.mValues.put(LauncherSettings.BaseLauncherColumns.ICON_TYPE, (Integer) 0);
            AutoInstallsLayout.this.mValues.put(LauncherSettings.BaseLauncherColumns.ICON_PACKAGE, this.mIconRes.getResourcePackageName(attributeResourceValue2));
            AutoInstallsLayout.this.mValues.put(LauncherSettings.BaseLauncherColumns.ICON_RESOURCE, this.mIconRes.getResourceName(attributeResourceValue2));
            intent.setFlags(270532608);
            AutoInstallsLayout autoInstallsLayout = AutoInstallsLayout.this;
            return autoInstallsLayout.addShortcut(autoInstallsLayout.mSourceRes.getString(attributeResourceValue), intent, 1);
        }

        @Override // com.android.launcher3.AutoInstallsLayout.TagParser
        public long parseAndAdd(XmlPullParser parser, String sourcePackageName) {
            String attributeValue = AutoInstallsLayout.getAttributeValue(parser, "title");
            String attributeValue2 = AutoInstallsLayout.getAttributeValue(parser, "icon");
            if (attributeValue2 != null && attributeValue != null) {
                int identifier = AutoInstallsLayout.this.mSourceRes.getIdentifier(attributeValue, "string", sourcePackageName);
                int identifier2 = AutoInstallsLayout.this.mSourceRes.getIdentifier(attributeValue2, LauncherConst.RESOURCE_IMAGE_TYPE, sourcePackageName);
                if (identifier != 0 && identifier2 != 0) {
                    Drawable drawable = this.mIconRes.getDrawable(identifier2);
                    if (drawable == null) {
                        return -1L;
                    }
                    String string = AutoInstallsLayout.this.mSourceRes.getString(identifier);
                    AutoInstallsLayout.this.mValues.put(LauncherSettings.BaseLauncherColumns.ICON_TYPE, (Integer) 0);
                    AutoInstallsLayout.this.mValues.put(LauncherSettings.BaseLauncherColumns.ICON_PACKAGE, this.mIconRes.getResourcePackageName(identifier2));
                    AutoInstallsLayout.this.mValues.put(LauncherSettings.BaseLauncherColumns.ICON_RESOURCE, this.mIconRes.getResourceName(identifier2));
                    ItemInfo.writeBitmap(AutoInstallsLayout.this.mValues, Utilities.createIconBitmap(drawable, AutoInstallsLayout.this.mContext));
                    attributeValue = string;
                } else {
                    try {
                        byte[] bArrDecode = Base64.getDecoder().decode(attributeValue2);
                        AutoInstallsLayout.this.mValues.put(LauncherSettings.BaseLauncherColumns.ICON_TYPE, (Integer) 1);
                        ItemInfo.writeBitmap(AutoInstallsLayout.this.mValues, ImageDecoder.decodeBitmap(ImageDecoder.createSource(bArrDecode)));
                    } catch (Exception unused) {
                    }
                }
                Intent intent = parseIntent(parser);
                if (intent == null) {
                    return -1L;
                }
                intent.setFlags(270532608);
                return AutoInstallsLayout.this.addShortcut(attributeValue, intent, 1);
            }
            return -1L;
        }

        protected Intent parseIntent(XmlPullParser parser) {
            String attributeValue = AutoInstallsLayout.getAttributeValue(parser, AutoInstallsLayout.ATTR_URL);
            if (TextUtils.isEmpty(attributeValue) || !Patterns.WEB_URL.matcher(attributeValue).matches()) {
                return null;
            }
            return new Intent("android.intent.action.VIEW", (Uri) null).setData(Uri.parse(attributeValue));
        }
    }

    protected class PendingWidgetParser implements TagParser {
        protected PendingWidgetParser() {
        }

        @Override // com.android.launcher3.AutoInstallsLayout.TagParser
        public long parseAndAdd(XmlResourceParser parser) throws XmlPullParserException, IOException {
            return parseAndAdd(parser, null);
        }

        @Override // com.android.launcher3.AutoInstallsLayout.TagParser
        public long parseAndAdd(XmlPullParser parser, String sourcePackageName) throws XmlPullParserException, IOException {
            String attributeValue = AutoInstallsLayout.getAttributeValue(parser, "packageName");
            String attributeValue2 = AutoInstallsLayout.getAttributeValue(parser, "className");
            if (TextUtils.isEmpty(attributeValue) || TextUtils.isEmpty(attributeValue2)) {
                return -1L;
            }
            AutoInstallsLayout.this.mValues.put("spanX", AutoInstallsLayout.getAttributeValue(parser, "spanX"));
            AutoInstallsLayout.this.mValues.put("spanY", AutoInstallsLayout.getAttributeValue(parser, "spanY"));
            AutoInstallsLayout.this.mValues.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, (Integer) 4);
            boolean zEquals = "true".equals(AutoInstallsLayout.getAttributeValue(parser, "promised"));
            Bundle bundle = new Bundle();
            if (!zEquals) {
                int depth = parser.getDepth();
                while (true) {
                    int next = parser.next();
                    if (next == 3 && parser.getDepth() <= depth) {
                        break;
                    }
                    if (next == 2) {
                        if (AutoInstallsLayout.TAG_EXTRA.equals(parser.getName())) {
                            String attributeValue3 = AutoInstallsLayout.getAttributeValue(parser, "key");
                            String attributeValue4 = AutoInstallsLayout.getAttributeValue(parser, "value");
                            if (attributeValue3 == null || attributeValue4 == null) {
                                break;
                            }
                            bundle.putString(attributeValue3, attributeValue4);
                        } else {
                            throw new RuntimeException("Widgets can contain only extras");
                        }
                    }
                }
                throw new RuntimeException("Widget extras must have a key and value");
            }
            return verifyAndInsert(new ComponentName(attributeValue, attributeValue2), bundle, zEquals);
        }

        protected long verifyAndInsert(ComponentName cn, Bundle extras) {
            return verifyAndInsert(cn, extras, false);
        }

        protected long verifyAndInsert(ComponentName cn, Bundle extras, boolean isPromised) {
            AutoInstallsLayout.this.mValues.put(LauncherSettings.Favorites.APPWIDGET_PROVIDER, cn.flattenToString());
            AutoInstallsLayout.this.mValues.put(LauncherSettings.Favorites.RESTORED, (Integer) 35);
            AutoInstallsLayout.this.mValues.put("_id", Long.valueOf(AutoInstallsLayout.this.mCallback.generateNewItemId()));
            if (!extras.isEmpty()) {
                AutoInstallsLayout.this.mValues.put(LauncherSettings.BaseLauncherColumns.INTENT, new Intent().putExtras(extras).toUri(0));
            }
            long jInsertAndCheck = AutoInstallsLayout.this.mCallback.insertAndCheck(AutoInstallsLayout.this.mDb, AutoInstallsLayout.this.mValues);
            if (jInsertAndCheck < 0) {
                return -1L;
            }
            return jInsertAndCheck;
        }
    }

    protected class FolderParser implements TagParser {
        private final HashMap<String, TagParser> mFolderElements;

        public FolderParser(final AutoInstallsLayout this$0) {
            this(this$0.getFolderElementsMap());
        }

        public FolderParser(HashMap<String, TagParser> elements) {
            this.mFolderElements = elements;
        }

        @Override // com.android.launcher3.AutoInstallsLayout.TagParser
        public long parseAndAdd(XmlResourceParser parser) throws XmlPullParserException, IOException {
            String string;
            int attributeResourceValue = AutoInstallsLayout.getAttributeResourceValue(parser, "title", 0);
            if (attributeResourceValue != 0) {
                string = AutoInstallsLayout.this.mSourceRes.getString(attributeResourceValue);
            } else {
                int identifier = AutoInstallsLayout.this.mContext.getResources().getIdentifier(AutoInstallsLayout.getAttributeValue(parser, "title"), "string", AutoInstallsLayout.this.mContext.getPackageName());
                if (identifier != 0) {
                    string = AutoInstallsLayout.this.mContext.getResources().getString(identifier);
                } else {
                    string = AutoInstallsLayout.this.mContext.getResources().getString(R.string.folder_name);
                }
            }
            AutoInstallsLayout.this.mValues.put("title", string);
            AutoInstallsLayout.this.mValues.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, (Integer) 2);
            AutoInstallsLayout.this.mValues.put("spanX", (Integer) 1);
            AutoInstallsLayout.this.mValues.put("spanY", (Integer) 1);
            AutoInstallsLayout.this.mValues.put("_id", Long.valueOf(AutoInstallsLayout.this.mCallback.generateNewItemId()));
            setFolderColor(parser);
            long jInsertAndCheck = AutoInstallsLayout.this.mCallback.insertAndCheck(AutoInstallsLayout.this.mDb, AutoInstallsLayout.this.mValues);
            if (jInsertAndCheck < 0) {
                return -1L;
            }
            ContentValues contentValues = new ContentValues(AutoInstallsLayout.this.mValues);
            ArrayList arrayList = new ArrayList();
            int depth = parser.getDepth();
            int i = 0;
            while (true) {
                int next = parser.next();
                if (next == 3 && parser.getDepth() <= depth) {
                    if (arrayList.size() >= 2) {
                        return jInsertAndCheck;
                    }
                    LauncherProvider.SqlArguments sqlArguments = new LauncherProvider.SqlArguments(LauncherSettings.Favorites.getContentUri(jInsertAndCheck), null, null);
                    AutoInstallsLayout.this.mDb.delete(sqlArguments.table, sqlArguments.where, sqlArguments.args);
                    if (arrayList.size() != 1) {
                        return -1L;
                    }
                    ContentValues contentValues2 = new ContentValues();
                    AutoInstallsLayout.copyInteger(contentValues, contentValues2, "container");
                    AutoInstallsLayout.copyInteger(contentValues, contentValues2, "screen");
                    AutoInstallsLayout.copyInteger(contentValues, contentValues2, LauncherSettings.Favorites.CELLX);
                    AutoInstallsLayout.copyInteger(contentValues, contentValues2, LauncherSettings.Favorites.CELLY);
                    long jLongValue = ((Long) arrayList.get(0)).longValue();
                    AutoInstallsLayout.this.mDb.update("favorites", contentValues2, "_id=" + jLongValue, null);
                    return jLongValue;
                }
                if (next == 2) {
                    AutoInstallsLayout.this.mValues.clear();
                    AutoInstallsLayout.this.mValues.put("container", Long.valueOf(jInsertAndCheck));
                    AutoInstallsLayout.this.mValues.put("rank", Integer.valueOf(i));
                    TagParser tagParser = this.mFolderElements.get(parser.getName());
                    if (tagParser != null) {
                        long andAdd = tagParser.parseAndAdd(parser);
                        if (andAdd >= 0) {
                            arrayList.add(Long.valueOf(andAdd));
                            i++;
                        }
                    } else {
                        throw new RuntimeException("Invalid folder item " + parser.getName());
                    }
                }
            }
        }

        @Override // com.android.launcher3.AutoInstallsLayout.TagParser
        public long parseAndAdd(XmlPullParser parser, String sourcePackageName) throws XmlPullParserException, IOException {
            String attributeValue = AutoInstallsLayout.getAttributeValue(parser, "title");
            if (attributeValue != null && attributeValue.contains("@string")) {
                int identifier = AutoInstallsLayout.this.mSourceRes.getIdentifier(attributeValue, "string", sourcePackageName);
                attributeValue = identifier != 0 ? AutoInstallsLayout.this.mSourceRes.getString(identifier) : AutoInstallsLayout.this.mContext.getResources().getString(R.string.folder_name);
            }
            AutoInstallsLayout.this.mValues.put("title", attributeValue);
            AutoInstallsLayout.this.mValues.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, (Integer) 2);
            AutoInstallsLayout.this.mValues.put("spanX", (Integer) 1);
            AutoInstallsLayout.this.mValues.put("spanY", (Integer) 1);
            AutoInstallsLayout.this.mValues.put("_id", Long.valueOf(AutoInstallsLayout.this.mCallback.generateNewItemId()));
            setFolderColor(parser);
            long jInsertAndCheck = AutoInstallsLayout.this.mCallback.insertAndCheck(AutoInstallsLayout.this.mDb, AutoInstallsLayout.this.mValues);
            if (jInsertAndCheck < 0) {
                return -1L;
            }
            ContentValues contentValues = new ContentValues(AutoInstallsLayout.this.mValues);
            ArrayList arrayList = new ArrayList();
            int depth = parser.getDepth();
            int i = 0;
            while (true) {
                int next = parser.next();
                if (next == 3 && parser.getDepth() <= depth) {
                    if (arrayList.size() >= 2) {
                        return jInsertAndCheck;
                    }
                    LauncherProvider.SqlArguments sqlArguments = new LauncherProvider.SqlArguments(LauncherSettings.Favorites.getContentUri(jInsertAndCheck), null, null);
                    AutoInstallsLayout.this.mDb.delete(sqlArguments.table, sqlArguments.where, sqlArguments.args);
                    if (arrayList.size() != 1) {
                        return -1L;
                    }
                    ContentValues contentValues2 = new ContentValues();
                    AutoInstallsLayout.copyInteger(contentValues, contentValues2, "container");
                    AutoInstallsLayout.copyInteger(contentValues, contentValues2, "screen");
                    AutoInstallsLayout.copyInteger(contentValues, contentValues2, LauncherSettings.Favorites.CELLX);
                    AutoInstallsLayout.copyInteger(contentValues, contentValues2, LauncherSettings.Favorites.CELLY);
                    long jLongValue = ((Long) arrayList.get(0)).longValue();
                    AutoInstallsLayout.this.mDb.update("favorites", contentValues2, "_id=" + jLongValue, null);
                    return jLongValue;
                }
                if (next == 2) {
                    AutoInstallsLayout.this.mValues.clear();
                    AutoInstallsLayout.this.mValues.put("container", Long.valueOf(jInsertAndCheck));
                    AutoInstallsLayout.this.mValues.put("rank", Integer.valueOf(i));
                    TagParser tagParser = this.mFolderElements.get(parser.getName());
                    if (tagParser != null) {
                        long andAdd = tagParser.parseAndAdd(parser, sourcePackageName);
                        if (andAdd >= 0) {
                            arrayList.add(Long.valueOf(andAdd));
                            i++;
                        }
                    } else {
                        throw new RuntimeException("Invalid folder item " + parser.getName());
                    }
                }
            }
        }

        private void setFolderColor(XmlPullParser parser) {
            String attributeValue = AutoInstallsLayout.getAttributeValue(parser, AutoInstallsLayout.ATTR_COLOR);
            long j = attributeValue == null ? -1L : Long.parseLong(attributeValue);
            if (j >= 0 && j < FolderColorUtil.getColorMax()) {
                AutoInstallsLayout.this.mValues.put(LauncherSettings.BaseLauncherColumns.ICON_TYPE, Long.valueOf(j));
            } else {
                AutoInstallsLayout.this.mValues.put(LauncherSettings.BaseLauncherColumns.ICON_TYPE, Integer.valueOf(AutoInstallsLayout.this.mContext.getResources().getInteger(R.integer.default_folder_color_index)));
            }
        }
    }

    protected static final void beginDocument(XmlPullParser parser, String firstElementName) throws XmlPullParserException, IOException {
        int next;
        do {
            next = parser.next();
            if (next == 2) {
                break;
            }
        } while (next != 1);
        if (next != 2) {
            throw new XmlPullParserException("No start tag found");
        }
        if (parser.getName().equals(firstElementName)) {
            return;
        }
        throw new XmlPullParserException("Unexpected start tag: found " + parser.getName() + ", expected " + firstElementName);
    }

    protected static String getAttributeValue(XmlPullParser parser, String attribute) {
        String attributeValue = parser.getAttributeValue("http://schemas.android.com/apk/res-auto/com.android.launcher3", attribute);
        return attributeValue == null ? parser.getAttributeValue(null, attribute) : attributeValue;
    }

    protected static int getAttributeResourceValue(XmlResourceParser parser, String attribute, int defaultValue) {
        int attributeResourceValue = parser.getAttributeResourceValue("http://schemas.android.com/apk/res-auto/com.android.launcher3", attribute, defaultValue);
        return attributeResourceValue == defaultValue ? parser.getAttributeResourceValue(null, attribute, defaultValue) : attributeResourceValue;
    }

    static void copyInteger(ContentValues from, ContentValues to, String key) {
        to.put(key, from.getAsInteger(key));
    }
}
