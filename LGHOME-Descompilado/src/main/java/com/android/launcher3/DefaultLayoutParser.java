package com.android.launcher3;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.android.launcher3.AutoInstallsLayout;
import com.android.launcher3.LauncherSettings;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.homesettings.SBHomeDataBaseUtil;
import com.lge.launcher3.util.LGHomeFeature;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class DefaultLayoutParser extends AutoInstallsLayout {
    private static final String ACTION_APPWIDGET_DEFAULT_WORKSPACE_CONFIGURE = "com.android.launcher.action.APPWIDGET_DEFAULT_WORKSPACE_CONFIGURE";
    protected static final String ATTR_CONTAINER = "container";
    private static final String ATTR_FOLDER_ITEMS = "folderItems";
    protected static final String ATTR_SCREEN = "screen";
    protected static final String ATTR_URI = "uri";
    private static final String TAG = "DefaultLayoutParser";
    private static final String TAG_APPWIDGET = "appwidget";
    protected static final String TAG_FAVORITE = "favorite";
    private static final String TAG_FAVORITES = "favorites";
    protected static final String TAG_FOLDER = "folder";
    private static final String TAG_FULLSCREEN_ITEM = "fullscreen_item";
    private static final String TAG_PARTNER_FOLDER = "partner-folder";
    protected static final String TAG_RESOLVE = "resolve";
    private static final String TAG_SHORTCUT = "shortcut";

    public DefaultLayoutParser(Context context, AppWidgetHost appWidgetHost, AutoInstallsLayout.LayoutParserCallback callback, Resources sourceRes, int layoutId) {
        super(context, appWidgetHost, callback, sourceRes, layoutId, "favorites");
    }

    public DefaultLayoutParser(Context context, AppWidgetHost appWidgetHost, AutoInstallsLayout.LayoutParserCallback callback, Resources sourceRes, int layoutId, String rootTag, int hotseatAllAppsRank) {
        super(context, appWidgetHost, callback, sourceRes, layoutId, rootTag, hotseatAllAppsRank);
    }

    @Override // com.android.launcher3.AutoInstallsLayout
    protected HashMap<String, AutoInstallsLayout.TagParser> getFolderElementsMap() {
        return getFolderElementsMap(this.mSourceRes);
    }

    HashMap<String, AutoInstallsLayout.TagParser> getFolderElementsMap(Resources res) {
        HashMap<String, AutoInstallsLayout.TagParser> map = new HashMap<>();
        map.put(TAG_FAVORITE, new AppShortcutWithUriParser());
        map.put(TAG_SHORTCUT, new UriShortcutParser(res));
        return map;
    }

    @Override // com.android.launcher3.AutoInstallsLayout
    protected HashMap<String, AutoInstallsLayout.TagParser> getLayoutElementsMap() {
        HashMap<String, AutoInstallsLayout.TagParser> map = new HashMap<>();
        map.put(TAG_FAVORITE, new AppShortcutWithUriParser());
        map.put(TAG_APPWIDGET, new AppWidgetParser());
        map.put(TAG_SHORTCUT, new UriShortcutParser(this.mSourceRes));
        map.put(TAG_RESOLVE, new ResolveParser());
        map.put(TAG_FOLDER, new MyFolderParser());
        map.put(TAG_PARTNER_FOLDER, new PartnerFolderParser());
        map.put(TAG_FULLSCREEN_ITEM, new FullscreenItemParser());
        return map;
    }

    @Override // com.android.launcher3.AutoInstallsLayout
    protected void parseContainerAndScreen(XmlResourceParser parser, long[] out) {
        out[0] = -100;
        String attributeValue = getAttributeValue(parser, "container");
        if (attributeValue != null) {
            out[0] = Long.valueOf(attributeValue).longValue();
        }
        out[1] = Long.parseLong(getAttributeValue(parser, "screen"));
    }

    class AppShortcutWithUriParser extends AutoInstallsLayout.AppShortcutParser {
        AppShortcutWithUriParser() {
            super();
        }

        @Override // com.android.launcher3.AutoInstallsLayout.AppShortcutParser
        protected long invalidPackageOrClass(XmlPullParser parser) {
            String attributeValue = AutoInstallsLayout.getAttributeValue(parser, DefaultLayoutParser.ATTR_URI);
            if (TextUtils.isEmpty(attributeValue)) {
                Log.e(DefaultLayoutParser.TAG, "Skipping invalid <favorite> with no component or uri");
                return -1L;
            }
            try {
                Intent uri = Intent.parseUri(attributeValue, 0);
                ResolveInfo resolveInfoResolveActivity = DefaultLayoutParser.this.mPackageManager.resolveActivity(uri, 65536);
                List<ResolveInfo> listQueryIntentActivities = DefaultLayoutParser.this.mPackageManager.queryIntentActivities(uri, 65536);
                if (wouldLaunchResolverActivity(resolveInfoResolveActivity, listQueryIntentActivities) && (resolveInfoResolveActivity = getSingleSystemActivity(listQueryIntentActivities)) == null) {
                    Log.w(DefaultLayoutParser.TAG, "No preference or single system activity found for ");
                    return -1L;
                }
                ActivityInfo activityInfo = resolveInfoResolveActivity.activityInfo;
                Intent launchIntentForPackage = DefaultLayoutParser.this.mPackageManager.getLaunchIntentForPackage(activityInfo.packageName);
                if (launchIntentForPackage == null) {
                    return -1L;
                }
                launchIntentForPackage.setFlags(270532608);
                DefaultLayoutParser defaultLayoutParser = DefaultLayoutParser.this;
                return defaultLayoutParser.addShortcut(activityInfo.loadLabel(defaultLayoutParser.mPackageManager).toString(), launchIntentForPackage, 0);
            } catch (URISyntaxException e) {
                Log.e(DefaultLayoutParser.TAG, "Unable to add meta-favorite: ", e);
                return -1L;
            }
        }

        private ResolveInfo getSingleSystemActivity(List<ResolveInfo> appList) {
            int size = appList.size();
            ResolveInfo resolveInfo = null;
            for (int i = 0; i < size; i++) {
                try {
                    if ((DefaultLayoutParser.this.mPackageManager.getApplicationInfo(appList.get(i).activityInfo.packageName, 0).flags & 1) != 0) {
                        if (resolveInfo != null) {
                            return null;
                        }
                        resolveInfo = appList.get(i);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Log.w(DefaultLayoutParser.TAG, "Unable to get info about resolve results", e);
                    return null;
                }
            }
            return resolveInfo;
        }

        private boolean wouldLaunchResolverActivity(ResolveInfo resolved, List<ResolveInfo> appList) {
            for (int i = 0; i < appList.size(); i++) {
                ResolveInfo resolveInfo = appList.get(i);
                if (resolveInfo.activityInfo.name.equals(resolved.activityInfo.name) && resolveInfo.activityInfo.packageName.equals(resolved.activityInfo.packageName)) {
                    return false;
                }
            }
            return true;
        }
    }

    private class UriShortcutParser extends AutoInstallsLayout.ShortcutParser {
        public UriShortcutParser(Resources iconRes) {
            super(iconRes);
        }

        @Override // com.android.launcher3.AutoInstallsLayout.ShortcutParser
        protected Intent parseIntent(XmlPullParser parser) {
            try {
                return Intent.parseUri(AutoInstallsLayout.getAttributeValue(parser, DefaultLayoutParser.ATTR_URI), 0);
            } catch (URISyntaxException unused) {
                Log.w(DefaultLayoutParser.TAG, "Shortcut has malformed uri: ");
                return null;
            }
        }
    }

    protected class ResolveParser implements AutoInstallsLayout.TagParser {
        private final AppShortcutWithUriParser mChildParser;

        @Override // com.android.launcher3.AutoInstallsLayout.TagParser
        public long parseAndAdd(XmlPullParser parser, String sourcePackageName) throws XmlPullParserException, IOException {
            return -1L;
        }

        protected ResolveParser() {
            this.mChildParser = DefaultLayoutParser.this.new AppShortcutWithUriParser();
        }

        @Override // com.android.launcher3.AutoInstallsLayout.TagParser
        public long parseAndAdd(XmlResourceParser parser) throws XmlPullParserException, IOException {
            int depth = parser.getDepth();
            long andAdd = -1;
            while (true) {
                int next = parser.next();
                if (next == 3 && parser.getDepth() <= depth) {
                    return andAdd;
                }
                if (next == 2 && andAdd <= -1) {
                    String name = parser.getName();
                    if (DefaultLayoutParser.TAG_FAVORITE.equals(name)) {
                        andAdd = this.mChildParser.parseAndAdd(parser);
                    } else {
                        Log.e(DefaultLayoutParser.TAG, "Fallback groups can contain only favorites, found " + name);
                    }
                }
            }
        }
    }

    class PartnerFolderParser implements AutoInstallsLayout.TagParser {
        @Override // com.android.launcher3.AutoInstallsLayout.TagParser
        public long parseAndAdd(XmlPullParser parser, String sourcePackageName) throws XmlPullParserException, IOException {
            return -1L;
        }

        PartnerFolderParser() {
        }

        @Override // com.android.launcher3.AutoInstallsLayout.TagParser
        public long parseAndAdd(XmlResourceParser parser) throws XmlPullParserException, IOException {
            Resources resources;
            int identifier;
            Partner partner = Partner.get(DefaultLayoutParser.this.mPackageManager);
            if (partner == null || (identifier = (resources = partner.getResources()).getIdentifier(Partner.RES_FOLDER, "xml", partner.getPackageName())) == 0) {
                return -1L;
            }
            XmlResourceParser xml = resources.getXml(identifier);
            AutoInstallsLayout.beginDocument(xml, DefaultLayoutParser.TAG_FOLDER);
            DefaultLayoutParser defaultLayoutParser = DefaultLayoutParser.this;
            return new AutoInstallsLayout.FolderParser(defaultLayoutParser.getFolderElementsMap(resources)).parseAndAdd(xml);
        }
    }

    class MyFolderParser extends AutoInstallsLayout.FolderParser {
        MyFolderParser() {
            super(DefaultLayoutParser.this);
        }

        @Override // com.android.launcher3.AutoInstallsLayout.FolderParser, com.android.launcher3.AutoInstallsLayout.TagParser
        public long parseAndAdd(XmlResourceParser parser) throws XmlPullParserException, IOException {
            int attributeResourceValue = AutoInstallsLayout.getAttributeResourceValue(parser, DefaultLayoutParser.ATTR_FOLDER_ITEMS, 0);
            if (attributeResourceValue != 0) {
                parser = DefaultLayoutParser.this.mSourceRes.getXml(attributeResourceValue);
                AutoInstallsLayout.beginDocument(parser, DefaultLayoutParser.TAG_FOLDER);
            }
            return super.parseAndAdd(parser);
        }
    }

    protected class AppWidgetParser extends AutoInstallsLayout.PendingWidgetParser {
        protected AppWidgetParser() {
            super();
        }

        @Override // com.android.launcher3.AutoInstallsLayout.PendingWidgetParser
        protected long verifyAndInsert(ComponentName cn, Bundle extras) {
            int iAllocateAppWidgetId;
            long jInsertAndCheck = -1;
            try {
                DefaultLayoutParser.this.mPackageManager.getReceiverInfo(cn, 0);
            } catch (Exception unused) {
                ComponentName componentName = new ComponentName(DefaultLayoutParser.this.mPackageManager.currentToCanonicalPackageNames(new String[]{cn.getPackageName()})[0], cn.getClassName());
                try {
                    DefaultLayoutParser.this.mPackageManager.getReceiverInfo(componentName, 0);
                    cn = componentName;
                } catch (Exception unused2) {
                    Log.d(DefaultLayoutParser.TAG, "Can't find widget provider: " + componentName.getClassName());
                    return -1L;
                }
            }
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(DefaultLayoutParser.this.mContext);
            try {
                iAllocateAppWidgetId = DefaultLayoutParser.this.mAppWidgetHost.allocateAppWidgetId();
            } catch (RuntimeException e) {
                Log.e(DefaultLayoutParser.TAG, "Problem allocating appWidgetId", e);
            }
            if (!appWidgetManager.bindAppWidgetIdIfAllowed(iAllocateAppWidgetId, cn)) {
                Log.e(DefaultLayoutParser.TAG, "Unable to bind app widget id " + cn);
                DefaultLayoutParser.this.mAppWidgetHost.deleteAppWidgetId(iAllocateAppWidgetId);
                return -1L;
            }
            DefaultLayoutParser.this.mValues.put("appWidgetId", Integer.valueOf(iAllocateAppWidgetId));
            DefaultLayoutParser.this.mValues.put(LauncherSettings.Favorites.APPWIDGET_PROVIDER, cn.flattenToString());
            DefaultLayoutParser.this.mValues.put("_id", Long.valueOf(DefaultLayoutParser.this.mCallback.generateNewItemId()));
            jInsertAndCheck = DefaultLayoutParser.this.mCallback.insertAndCheck(DefaultLayoutParser.this.mDb, DefaultLayoutParser.this.mValues);
            if (jInsertAndCheck < 0) {
                DefaultLayoutParser.this.mAppWidgetHost.deleteAppWidgetId(iAllocateAppWidgetId);
                return jInsertAndCheck;
            }
            if (!extras.isEmpty()) {
                Intent intent = new Intent(DefaultLayoutParser.ACTION_APPWIDGET_DEFAULT_WORKSPACE_CONFIGURE);
                intent.setComponent(cn);
                intent.putExtras(extras);
                intent.putExtra("appWidgetId", iAllocateAppWidgetId);
                DefaultLayoutParser.this.mContext.sendBroadcast(intent);
            }
            return jInsertAndCheck;
        }

        @Override // com.android.launcher3.AutoInstallsLayout.PendingWidgetParser
        protected long verifyAndInsert(ComponentName cn, Bundle extras, boolean isPromised) {
            int iAllocateAppWidgetId;
            long jInsertAndCheck = -1;
            if (!isPromised) {
                try {
                    DefaultLayoutParser.this.mPackageManager.getReceiverInfo(cn, 0);
                } catch (Exception unused) {
                    ComponentName componentName = new ComponentName(DefaultLayoutParser.this.mPackageManager.currentToCanonicalPackageNames(new String[]{cn.getPackageName()})[0], cn.getClassName());
                    try {
                        DefaultLayoutParser.this.mPackageManager.getReceiverInfo(componentName, 0);
                        cn = componentName;
                    } catch (Exception unused2) {
                        Log.d(DefaultLayoutParser.TAG, "Can't find widget provider: " + componentName.getClassName());
                        return -1L;
                    }
                }
            }
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(DefaultLayoutParser.this.mContext);
            try {
                iAllocateAppWidgetId = DefaultLayoutParser.this.mAppWidgetHost.allocateAppWidgetId();
            } catch (RuntimeException e) {
                Log.e(DefaultLayoutParser.TAG, "Problem allocating appWidgetId", e);
            }
            if (!isPromised && !appWidgetManager.bindAppWidgetIdIfAllowed(iAllocateAppWidgetId, cn)) {
                Log.e(DefaultLayoutParser.TAG, "Unable to bind app widget id " + cn);
                DefaultLayoutParser.this.mAppWidgetHost.deleteAppWidgetId(iAllocateAppWidgetId);
                return -1L;
            }
            DefaultLayoutParser.this.mValues.put("appWidgetId", Integer.valueOf(iAllocateAppWidgetId));
            DefaultLayoutParser.this.mValues.put(LauncherSettings.Favorites.APPWIDGET_PROVIDER, cn.flattenToString());
            DefaultLayoutParser.this.mValues.put("_id", Long.valueOf(DefaultLayoutParser.this.mCallback.generateNewItemId()));
            if (isPromised) {
                DefaultLayoutParser.this.mValues.put(LauncherSettings.Favorites.RESTORED, (Integer) 71);
            }
            jInsertAndCheck = DefaultLayoutParser.this.mCallback.insertAndCheck(DefaultLayoutParser.this.mDb, DefaultLayoutParser.this.mValues);
            if (jInsertAndCheck < 0) {
                DefaultLayoutParser.this.mAppWidgetHost.deleteAppWidgetId(iAllocateAppWidgetId);
                return jInsertAndCheck;
            }
            if (!extras.isEmpty() && !isPromised) {
                Intent intent = new Intent(DefaultLayoutParser.ACTION_APPWIDGET_DEFAULT_WORKSPACE_CONFIGURE);
                intent.setComponent(cn);
                intent.putExtras(extras);
                intent.putExtra("appWidgetId", iAllocateAppWidgetId);
                DefaultLayoutParser.this.mContext.sendBroadcast(intent);
            }
            return jInsertAndCheck;
        }
    }

    class FullscreenItemParser implements AutoInstallsLayout.TagParser {
        FullscreenItemParser() {
        }

        @Override // com.android.launcher3.AutoInstallsLayout.TagParser
        public long parseAndAdd(XmlResourceParser parser) throws XmlPullParserException, IOException {
            return parseAndAdd(parser, null);
        }

        @Override // com.android.launcher3.AutoInstallsLayout.TagParser
        public long parseAndAdd(XmlPullParser parser, String sourcePackageName) throws XmlPullParserException, IOException {
            if (DefaultLayoutParser.this.mValues == null) {
                return -1L;
            }
            DefaultLayoutParser.this.mValues.put("screen", (Integer) (-301));
            DefaultLayoutParser.this.mValues.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, (Integer) 8);
            DefaultLayoutParser.this.mValues.put("title", DefaultLayoutParser.TAG_FULLSCREEN_ITEM);
            long jGenerateNewItemId = DefaultLayoutParser.this.mCallback.generateNewItemId();
            DefaultLayoutParser.this.mValues.put("_id", Long.valueOf(jGenerateNewItemId));
            DeviceProfile deviceProfile = LauncherAppState.getIDP(DefaultLayoutParser.this.mContext).portraitProfile;
            DefaultLayoutParser.this.mValues.put("spanX", Integer.valueOf(deviceProfile.inv.numColumns));
            DefaultLayoutParser.this.mValues.put("spanY", Integer.valueOf(deviceProfile.inv.numRows));
            String attributeValue = AutoInstallsLayout.getAttributeValue(parser, LauncherConst.EXTRA_CLASS_NAME);
            if (attributeValue == null) {
                return -1L;
            }
            if (attributeValue.startsWith("android.resource:")) {
                if (!LGHomeFeature.Config.FEATURE_USE_SMARTBULLETIN.getValue()) {
                    return -1L;
                }
                DefaultLayoutParser.this.mValues.put(LauncherSettings.BaseLauncherColumns.INTENT, attributeValue);
            } else {
                if (!attributeValue.startsWith("android.widget:") || !SBHomeDataBaseUtil.isEnabledQmemoPanel(DefaultLayoutParser.this.mContext) || DefaultLayoutParser.this.mAppWidgetHost == null) {
                    return -1L;
                }
                int iAllocateAppWidgetId = DefaultLayoutParser.this.mAppWidgetHost.allocateAppWidgetId();
                AppWidgetManager.getInstance(DefaultLayoutParser.this.mContext).bindAppWidgetId(iAllocateAppWidgetId, getComponentName(attributeValue));
                DefaultLayoutParser.this.mValues.put("appWidgetId", Integer.valueOf(iAllocateAppWidgetId));
                DefaultLayoutParser.this.mValues.put(LauncherSettings.BaseLauncherColumns.INTENT, attributeValue);
            }
            if (DefaultLayoutParser.this.mCallback.insertAndCheck(DefaultLayoutParser.this.mDb, DefaultLayoutParser.this.mValues) < 0) {
                return -1L;
            }
            return jGenerateNewItemId;
        }

        private ComponentName getComponentName(String resUri) {
            Uri uri = Uri.parse(resUri);
            return new ComponentName(uri.getHost(), uri.getPath().replace("/", ""));
        }
    }
}
