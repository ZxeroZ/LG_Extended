package com.lge.launcher3.allapps;

import android.content.Context;
import android.util.AttributeSet;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGUsimInfo;
import java.io.InputStream;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsDefaultItems {
    private static final String LOG_TAG = "AllAppsDefaultItems";
    protected static final String TAG_CARRIER = "Carrier";
    protected static final String TAG_FOLDER = "pageFolder";
    protected static final String TAG_PAGEAPP = "pageApp";
    protected static final String TAG_PAGEMENU_APPS = "pagemenuApps";
    protected static final String THEME_RESOURCE_STRING_TYPE = "string";
    private static ArrayList<AllAppsItemInfo> sDefaultDBItems;
    private static int sDefaultFolderColor;
    private static boolean sIsDividePage;

    public static ArrayList<AllAppsItemInfo> getDefaultItems(Context context) {
        if (sDefaultDBItems == null) {
            sDefaultFolderColor = context.getResources().getInteger(R.integer.lg_default_folder_color_index);
            loadDefaultApps(context);
        }
        return sDefaultDBItems;
    }

    /* JADX DEBUG: Don't trust debug lines info. Repeating lines: [201=6, 204=4] */
    /* JADX DEBUG: Multi-variable search result rejected for r14v2, resolved type: java.lang.String */
    /* JADX DEBUG: Multi-variable search result rejected for r14v3, resolved type: java.lang.String */
    /* JADX DEBUG: Multi-variable search result rejected for r14v8, resolved type: java.lang.String */
    /* JADX WARN: Code restructure failed: missing block: B:148:?, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:149:?, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:151:?, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x00db, code lost:
    
        com.lge.launcher3.allapps.AllAppsDefaultItems.sIsDividePage = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x00dd, code lost:
    
        if (r7 == null) goto L148;
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x00df, code lost:
    
        r7.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:56:0x00e3, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:57:0x00e4, code lost:
    
        r0.printStackTrace();
     */
    /* JADX WARN: Code restructure failed: missing block: B:58:0x00e8, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x01c6, code lost:
    
        if (r7 == null) goto L151;
     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x01c8, code lost:
    
        r7.close();
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:102:0x01f2 A[Catch: IOException -> 0x01f6, TRY_ENTER, TRY_LEAVE, TryCatch #1 {IOException -> 0x01f6, blocks: (B:80:0x01c8, B:95:0x01e4, B:102:0x01f2), top: B:117:0x0019 }] */
    /* JADX WARN: Removed duplicated region for block: B:152:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:153:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0056 A[Catch: all -> 0x01d6, IOException -> 0x01da, XmlPullParserException -> 0x01e8, TRY_ENTER, TRY_LEAVE, TryCatch #11 {IOException -> 0x01da, XmlPullParserException -> 0x01e8, all -> 0x01d6, blocks: (B:6:0x0019, B:20:0x0056), top: B:117:0x0019 }] */
    /* JADX WARN: Removed duplicated region for block: B:95:0x01e4 A[Catch: IOException -> 0x01f6, TRY_ENTER, TRY_LEAVE, TryCatch #1 {IOException -> 0x01f6, blocks: (B:80:0x01c8, B:95:0x01e4, B:102:0x01f2), top: B:117:0x0019 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    static void loadDefaultApps(android.content.Context r16) throws java.lang.Throwable {
        /*
            r0 = r16
            java.lang.String r1 = "Got exception parsing pagemenuApps."
            java.lang.String r2 = "AllAppsDefaultItems"
            java.util.ArrayList<com.lge.launcher3.allapps.AllAppsItemInfo> r3 = com.lge.launcher3.allapps.AllAppsDefaultItems.sDefaultDBItems
            if (r3 == 0) goto Lb
            return
        Lb:
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            com.lge.launcher3.allapps.AllAppsDefaultItems.sDefaultDBItems = r3
            android.util.SparseArray r3 = new android.util.SparseArray
            r3.<init>()
            r4 = 0
            r5 = 0
            java.lang.String r6 = com.android.launcher3.LauncherAppState.getSharedPreferencesKey()     // Catch: java.lang.Throwable -> L1d6 java.io.IOException -> L1da org.xmlpull.v1.XmlPullParserException -> L1e8
            android.content.SharedPreferences r6 = r0.getSharedPreferences(r6, r5)     // Catch: java.lang.Throwable -> L1d6 java.io.IOException -> L1da org.xmlpull.v1.XmlPullParserException -> L1e8
            java.lang.String r7 = "launcher.cota.filepath_apps"
            java.lang.String r6 = r6.getString(r7, r4)     // Catch: java.lang.Throwable -> L1d6 java.io.IOException -> L1da org.xmlpull.v1.XmlPullParserException -> L1e8
            if (r6 == 0) goto L56
            boolean r7 = com.lge.launcher3.util.LGHomeFeature.isDisableEasyHome()     // Catch: java.io.IOException -> L50 org.xmlpull.v1.XmlPullParserException -> L53 java.lang.Throwable -> L1fc
            if (r7 == 0) goto L56
            java.io.FileInputStream r7 = new java.io.FileInputStream     // Catch: java.io.FileNotFoundException -> L39 java.io.IOException -> L50 org.xmlpull.v1.XmlPullParserException -> L53 java.lang.Throwable -> L1fc
            r7.<init>(r6)     // Catch: java.io.FileNotFoundException -> L39 java.io.IOException -> L50 org.xmlpull.v1.XmlPullParserException -> L53 java.lang.Throwable -> L1fc
            org.xmlpull.v1.XmlPullParser r6 = getXMLParserFrom(r7)     // Catch: java.io.FileNotFoundException -> L3a java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            goto L62
        L39:
            r7 = r4
        L3a:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r8.<init>()     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            java.lang.String r9 = "Couldn't find default_pagemenu file: "
            r8.append(r9)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r8.append(r6)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            java.lang.String r6 = r8.toString()     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            com.lge.launcher3.util.LGLog.e(r2, r6, r5)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r6 = r4
            goto L62
        L50:
            r0 = move-exception
            goto L1dc
        L53:
            r0 = move-exception
            goto L1ea
        L56:
            android.content.res.Resources r6 = r16.getResources()     // Catch: java.lang.Throwable -> L1d6 java.io.IOException -> L1da org.xmlpull.v1.XmlPullParserException -> L1e8
            r7 = 2132017162(0x7f14000a, float:1.9672595E38)
            android.content.res.XmlResourceParser r6 = r6.getXml(r7)     // Catch: java.lang.Throwable -> L1d6 java.io.IOException -> L1da org.xmlpull.v1.XmlPullParserException -> L1e8
            r7 = r4
        L62:
            if (r6 != 0) goto L70
            if (r7 == 0) goto L6f
            r7.close()     // Catch: java.io.IOException -> L6a
            goto L6f
        L6a:
            r0 = move-exception
            r1 = r0
            r1.printStackTrace()
        L6f:
            return
        L70:
            android.util.AttributeSet r8 = android.util.Xml.asAttributeSet(r6)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            java.lang.String r9 = "pagemenuApps"
            com.android.internal.util.XmlUtils.beginDocument(r6, r9)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            int r9 = r6.getDepth()     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
        L7d:
            int r10 = r6.next()     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r11 = 3
            if (r10 != r11) goto L8a
            int r12 = r6.getDepth()     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            if (r12 <= r9) goto L1c6
        L8a:
            r12 = 1
            if (r10 == r12) goto L1c6
            r13 = 2
            if (r10 == r13) goto L91
            goto L7d
        L91:
            java.lang.String r10 = r6.getName()     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            java.lang.String r14 = r6.getAttributeNamespace(r5)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            int r14 = r14.length()     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            if (r14 <= 0) goto La4
            java.lang.String r14 = r6.getAttributeNamespace(r5)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            goto La5
        La4:
            r14 = r4
        La5:
            java.lang.String r15 = "Carrier"
            boolean r15 = r15.equals(r10)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            if (r15 == 0) goto Lb6
            boolean r15 = isAttributeCurrentCarrier(r0, r8, r5, r14)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            if (r15 != 0) goto Lb6
            com.lge.launcher3.util.Utilities.skipXmlTag(r6)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
        Lb6:
            java.lang.String r15 = "pageApp"
            boolean r15 = r15.equals(r10)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r4 = -1
            if (r15 == 0) goto L142
            com.lge.launcher3.allapps.PageAppAttr r10 = com.lge.launcher3.allapps.PageAppAttr.from(r0, r8, r5)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r10.setNamespace(r14)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            int r11 = r10.getInt(r12, r4)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            int r4 = r10.getInt(r13, r4)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            java.lang.String r13 = r10.getString(r5)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r10.recycle()     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            boolean r10 = android.text.TextUtils.isEmpty(r13)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            if (r10 == 0) goto Le9
            com.lge.launcher3.allapps.AllAppsDefaultItems.sIsDividePage = r12     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            if (r7 == 0) goto Le8
            r7.close()     // Catch: java.io.IOException -> Le3
            goto Le8
        Le3:
            r0 = move-exception
            r1 = r0
            r1.printStackTrace()
        Le8:
            return
        Le9:
            if (r4 <= 0) goto L126
            java.lang.Object r10 = r3.get(r4)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            com.android.launcher3.model.data.FolderInfo r10 = (com.android.launcher3.model.data.FolderInfo) r10     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            if (r10 == 0) goto L126
            com.android.launcher3.ShortcutInfo r4 = new com.android.launcher3.ShortcutInfo     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r4.<init>()     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            long r11 = r10.id     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r4.container = r11     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            android.content.Intent r11 = new android.content.Intent     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            java.lang.String r12 = "android.intent.action.MAIN"
            r11.<init>(r12)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r4.intent = r11     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            android.content.Intent r11 = r4.intent     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            java.lang.String r12 = "android.intent.category.LAUNCHER"
            r11.addCategory(r12)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            android.content.Intent r11 = r4.intent     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            android.content.ComponentName r12 = android.content.ComponentName.unflattenFromString(r13)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r11.setComponent(r12)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            android.content.Intent r11 = r4.intent     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r12 = 270532608(0x10200000, float:3.1554436E-29)
            r11.setFlags(r12)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            java.util.ArrayList r10 = r10.getContents()     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r10.add(r4)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r4 = 0
            goto L7d
        L126:
            com.lge.launcher3.allapps.AllAppsItemInfo r10 = new com.lge.launcher3.allapps.AllAppsItemInfo     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r10.<init>()     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            long r11 = (long) r11     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r10.screenId = r11     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            long r11 = (long) r4     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r10.container = r11     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            android.content.ComponentName r4 = android.content.ComponentName.unflattenFromString(r13)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r10.componentName = r4     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r10.requiresDbUpdate = r5     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r10.itemType = r5     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            java.util.ArrayList<com.lge.launcher3.allapps.AllAppsItemInfo> r4 = com.lge.launcher3.allapps.AllAppsDefaultItems.sDefaultDBItems     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r4.add(r10)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            goto L1c2
        L142:
            java.lang.String r15 = "pageFolder"
            boolean r10 = r15.equals(r10)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            if (r10 == 0) goto L1c2
            com.lge.launcher3.allapps.PageFolderAttr r10 = com.lge.launcher3.allapps.PageFolderAttr.from(r0, r8, r5)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r10.setNamespace(r14)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            int r4 = r10.getInt(r11, r4)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            int r11 = r10.getInt(r13, r5)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            java.lang.String r12 = r10.getString(r12)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            int r13 = r10.getInt(r5, r5)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r14 = 4
            r10.getInt(r14, r5)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r10.recycle()     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            com.lge.launcher3.allapps.AllAppsFolderInfo r10 = new com.lge.launcher3.allapps.AllAppsFolderInfo     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r10.<init>()     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            android.content.res.Resources r14 = r16.getResources()     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            java.lang.String r15 = "string"
            java.lang.String r5 = r16.getPackageName()     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            int r5 = r14.getIdentifier(r12, r15, r5)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            if (r5 <= 0) goto L186
            android.content.res.Resources r12 = r16.getResources()     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            java.lang.String r12 = r12.getString(r5)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            goto L193
        L186:
            if (r12 != 0) goto L193
            android.content.res.Resources r5 = r16.getResources()     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r12 = 2131820797(0x7f1100fd, float:1.927432E38)
            java.lang.String r12 = r5.getString(r12)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
        L193:
            long r14 = (long) r11     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r10.id = r14     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            long r4 = (long) r4     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r10.screenId = r4     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r10.setTitle(r12)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            if (r13 <= 0) goto L1a8
            int r4 = com.lge.launcher3.folder.FolderColorUtil.getColorMax()     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            if (r13 >= r4) goto L1a8
            r10.changeFolderColor(r13)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            goto L1ad
        L1a8:
            int r4 = com.lge.launcher3.allapps.AllAppsDefaultItems.sDefaultFolderColor     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r10.changeFolderColor(r4)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
        L1ad:
            com.lge.launcher3.allapps.AllAppsItemInfo r4 = new com.lge.launcher3.allapps.AllAppsItemInfo     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r4.<init>(r10)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            java.util.ArrayList<com.lge.launcher3.allapps.AllAppsItemInfo> r5 = com.lge.launcher3.allapps.AllAppsDefaultItems.sDefaultDBItems     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r5.add(r4)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            java.lang.Integer r4 = java.lang.Integer.valueOf(r11)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            int r4 = r4.intValue()     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
            r3.put(r4, r10)     // Catch: java.lang.Throwable -> L1cc java.io.IOException -> L1d0 org.xmlpull.v1.XmlPullParserException -> L1d3
        L1c2:
            r4 = 0
            r5 = 0
            goto L7d
        L1c6:
            if (r7 == 0) goto L1fb
            r7.close()     // Catch: java.io.IOException -> L1f6
            goto L1fb
        L1cc:
            r0 = move-exception
            r1 = r0
            r4 = r7
            goto L1fe
        L1d0:
            r0 = move-exception
            r4 = r7
            goto L1dc
        L1d3:
            r0 = move-exception
            r4 = r7
            goto L1ea
        L1d6:
            r0 = move-exception
            r1 = r0
            r4 = 0
            goto L1fe
        L1da:
            r0 = move-exception
            r4 = 0
        L1dc:
            r3 = 0
            int[] r3 = new int[r3]     // Catch: java.lang.Throwable -> L1fc
            com.lge.launcher3.util.LGLog.w(r2, r1, r0, r3)     // Catch: java.lang.Throwable -> L1fc
            if (r4 == 0) goto L1fb
            r4.close()     // Catch: java.io.IOException -> L1f6
            goto L1fb
        L1e8:
            r0 = move-exception
            r4 = 0
        L1ea:
            r3 = 0
            int[] r3 = new int[r3]     // Catch: java.lang.Throwable -> L1fc
            com.lge.launcher3.util.LGLog.w(r2, r1, r0, r3)     // Catch: java.lang.Throwable -> L1fc
            if (r4 == 0) goto L1fb
            r4.close()     // Catch: java.io.IOException -> L1f6
            goto L1fb
        L1f6:
            r0 = move-exception
            r1 = r0
            r1.printStackTrace()
        L1fb:
            return
        L1fc:
            r0 = move-exception
            r1 = r0
        L1fe:
            if (r4 == 0) goto L209
            r4.close()     // Catch: java.io.IOException -> L204
            goto L209
        L204:
            r0 = move-exception
            r2 = r0
            r2.printStackTrace()
        L209:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.allapps.AllAppsDefaultItems.loadDefaultApps(android.content.Context):void");
    }

    protected static XmlPullParser getXMLParserFrom(InputStream inputStream) throws XmlPullParserException {
        XmlPullParserFactory xmlPullParserFactoryNewInstance = XmlPullParserFactory.newInstance();
        xmlPullParserFactoryNewInstance.setNamespaceAware(true);
        XmlPullParser xmlPullParserNewPullParser = xmlPullParserFactoryNewInstance.newPullParser();
        xmlPullParserNewPullParser.setInput(inputStream, null);
        return xmlPullParserNewPullParser;
    }

    public static boolean isAttributeCurrentCarrier(Context context, AttributeSet attrs, boolean isCompiledRes, String nameSpace) {
        LGUsimInfo lGUsimInfo = LGUsimInfo.getInstance(context);
        if (lGUsimInfo == null) {
            return false;
        }
        if (lGUsimInfo != null && lGUsimInfo.getSimState() != 5) {
            return false;
        }
        CarrierAttr carrierAttrFrom = CarrierAttr.from(context, attrs, isCompiledRes);
        carrierAttrFrom.setNamespace(nameSpace);
        String string = carrierAttrFrom.getString(1);
        String string2 = carrierAttrFrom.getString(2);
        String string3 = carrierAttrFrom.getString(0);
        carrierAttrFrom.recycle();
        return string != null && string2 != null && string3 != null && lGUsimInfo.equalComparatorMcc(string) && lGUsimInfo.equalComparatorMnc(string2) && lGUsimInfo.equalComparatorGIDWithLength(string3, string3.length());
    }

    static boolean isDividePage() {
        return sIsDividePage;
    }

    public static void clearDefaultItem() {
        ArrayList<AllAppsItemInfo> arrayList = sDefaultDBItems;
        if (arrayList != null) {
            arrayList.clear();
        }
        sDefaultDBItems = null;
    }
}
