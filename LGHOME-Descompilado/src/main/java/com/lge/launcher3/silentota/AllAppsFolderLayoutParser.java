package com.lge.launcher3.silentota;

import android.content.ComponentName;
import android.content.Context;
import com.lge.launcher3.allapps.AllAppsDefaultItems;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsFolderLayoutParser extends AllAppsDefaultItems {
    private static final String LOG_TAG = "AllAppsFolderLayoutParser";
    private final Context mContext;
    private HashMap<ComponentName, String> mHashMap = new HashMap<>();

    public AllAppsFolderLayoutParser(Context context) {
        this.mContext = context;
    }

    /* JADX DEBUG: Don't trust debug lines info. Repeating lines: [153=5, 156=4] */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:108:0x0033 */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:113:0x0020 */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:121:0x002d */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:82:0x0189 */
    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:84:0x018c */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x017d, code lost:
    
        if (r6 == 0) goto L99;
     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x017f, code lost:
    
        r6.close();
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:25:0x0062 A[Catch: all -> 0x018f, IOException -> 0x0193, XmlPullParserException -> 0x01a3, TRY_ENTER, TRY_LEAVE, TryCatch #13 {IOException -> 0x0193, XmlPullParserException -> 0x01a3, all -> 0x018f, blocks: (B:3:0x0010, B:25:0x0062), top: B:115:0x0010 }] */
    /* JADX WARN: Removed duplicated region for block: B:92:0x019f A[Catch: IOException -> 0x0183, TRY_ENTER, TRY_LEAVE, TryCatch #12 {IOException -> 0x0183, blocks: (B:78:0x017f, B:92:0x019f, B:98:0x01af), top: B:117:0x0010 }] */
    /* JADX WARN: Removed duplicated region for block: B:98:0x01af A[Catch: IOException -> 0x0183, TRY_ENTER, TRY_LEAVE, TryCatch #12 {IOException -> 0x0183, blocks: (B:78:0x017f, B:92:0x019f, B:98:0x01af), top: B:117:0x0010 }] */
    /* JADX WARN: Type inference failed for: r3v1 */
    /* JADX WARN: Type inference failed for: r3v10 */
    /* JADX WARN: Type inference failed for: r3v11 */
    /* JADX WARN: Type inference failed for: r3v2 */
    /* JADX WARN: Type inference failed for: r3v23 */
    /* JADX WARN: Type inference failed for: r3v24 */
    /* JADX WARN: Type inference failed for: r3v3 */
    /* JADX WARN: Type inference failed for: r3v4 */
    /* JADX WARN: Type inference failed for: r3v5, types: [java.io.FileInputStream] */
    /* JADX WARN: Type inference failed for: r3v7, types: [java.io.FileInputStream] */
    /* JADX WARN: Type inference failed for: r3v8, types: [java.io.FileInputStream] */
    /* JADX WARN: Type inference failed for: r3v9 */
    /* JADX WARN: Type inference failed for: r6v1, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r6v10 */
    /* JADX WARN: Type inference failed for: r6v11, types: [java.io.FileInputStream] */
    /* JADX WARN: Type inference failed for: r6v13 */
    /* JADX WARN: Type inference failed for: r6v14 */
    /* JADX WARN: Type inference failed for: r6v15 */
    /* JADX WARN: Type inference failed for: r6v2 */
    /* JADX WARN: Type inference failed for: r6v4 */
    /* JADX WARN: Type inference failed for: r6v5 */
    /* JADX WARN: Type inference failed for: r6v6, types: [java.io.FileInputStream, java.io.InputStream] */
    /* JADX WARN: Type inference failed for: r6v7 */
    /* JADX WARN: Type inference failed for: r6v8 */
    /* JADX WARN: Type inference failed for: r6v9 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int parseLayout() throws java.lang.Throwable {
        /*
            r16 = this;
            r1 = r16
            java.lang.String r2 = "Got exception parsing pagemenuApps."
            android.util.SparseArray r0 = new android.util.SparseArray
            r0.<init>()
            java.util.HashMap<android.content.ComponentName, java.lang.String> r3 = r1.mHashMap
            r3.clear()
            r3 = 0
            r4 = 0
            android.content.Context r5 = r1.mContext     // Catch: java.lang.Throwable -> L18f java.io.IOException -> L193 org.xmlpull.v1.XmlPullParserException -> L1a3
            java.lang.String r6 = com.android.launcher3.LauncherAppState.getSharedPreferencesKey()     // Catch: java.lang.Throwable -> L18f java.io.IOException -> L193 org.xmlpull.v1.XmlPullParserException -> L1a3
            android.content.SharedPreferences r5 = r5.getSharedPreferences(r6, r4)     // Catch: java.lang.Throwable -> L18f java.io.IOException -> L193 org.xmlpull.v1.XmlPullParserException -> L1a3
            java.lang.String r6 = "launcher.cota.filepath_apps"
            java.lang.String r5 = r5.getString(r6, r3)     // Catch: java.lang.Throwable -> L18f java.io.IOException -> L193 org.xmlpull.v1.XmlPullParserException -> L1a3
            if (r5 == 0) goto L62
            boolean r6 = com.lge.launcher3.util.LGHomeFeature.isDisableEasyHome()     // Catch: java.io.IOException -> L5a org.xmlpull.v1.XmlPullParserException -> L5e java.lang.Throwable -> L1b3
            if (r6 == 0) goto L62
            java.io.FileInputStream r6 = new java.io.FileInputStream     // Catch: java.io.FileNotFoundException -> L32 java.io.IOException -> L5a org.xmlpull.v1.XmlPullParserException -> L5e java.lang.Throwable -> L1b3
            r6.<init>(r5)     // Catch: java.io.FileNotFoundException -> L32 java.io.IOException -> L5a org.xmlpull.v1.XmlPullParserException -> L5e java.lang.Throwable -> L1b3
            org.xmlpull.v1.XmlPullParser r5 = getXMLParserFrom(r6)     // Catch: java.io.FileNotFoundException -> L33 java.lang.Throwable -> L4b java.io.IOException -> L50 org.xmlpull.v1.XmlPullParserException -> L55
            goto L70
        L32:
            r6 = r3
        L33:
            java.lang.String r7 = com.lge.launcher3.silentota.AllAppsFolderLayoutParser.LOG_TAG     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L50 org.xmlpull.v1.XmlPullParserException -> L55
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L50 org.xmlpull.v1.XmlPullParserException -> L55
            r8.<init>()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L50 org.xmlpull.v1.XmlPullParserException -> L55
            java.lang.String r9 = "Couldn't find default_pagemenu file: "
            r8.append(r9)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L50 org.xmlpull.v1.XmlPullParserException -> L55
            r8.append(r5)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L50 org.xmlpull.v1.XmlPullParserException -> L55
            java.lang.String r5 = r8.toString()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L50 org.xmlpull.v1.XmlPullParserException -> L55
            com.lge.launcher3.util.LGLog.e(r7, r5, r4)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L50 org.xmlpull.v1.XmlPullParserException -> L55
            r5 = r3
            goto L70
        L4b:
            r0 = move-exception
            r2 = r0
            r3 = r6
            goto L1b5
        L50:
            r0 = move-exception
            r9 = r4
        L52:
            r3 = r6
            goto L196
        L55:
            r0 = move-exception
            r9 = r4
        L57:
            r3 = r6
            goto L1a6
        L5a:
            r0 = move-exception
            r9 = r4
            goto L196
        L5e:
            r0 = move-exception
            r9 = r4
            goto L1a6
        L62:
            android.content.Context r5 = r1.mContext     // Catch: java.lang.Throwable -> L18f java.io.IOException -> L193 org.xmlpull.v1.XmlPullParserException -> L1a3
            android.content.res.Resources r5 = r5.getResources()     // Catch: java.lang.Throwable -> L18f java.io.IOException -> L193 org.xmlpull.v1.XmlPullParserException -> L1a3
            r6 = 2132017162(0x7f14000a, float:1.9672595E38)
            android.content.res.XmlResourceParser r5 = r5.getXml(r6)     // Catch: java.lang.Throwable -> L18f java.io.IOException -> L193 org.xmlpull.v1.XmlPullParserException -> L1a3
            r6 = r3
        L70:
            if (r5 != 0) goto L7e
            if (r6 == 0) goto L7d
            r6.close()     // Catch: java.io.IOException -> L78
            goto L7d
        L78:
            r0 = move-exception
            r2 = r0
            r2.printStackTrace()
        L7d:
            return r4
        L7e:
            android.util.AttributeSet r7 = android.util.Xml.asAttributeSet(r5)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L50 org.xmlpull.v1.XmlPullParserException -> L55
            java.lang.String r8 = "pagemenuApps"
            com.android.internal.util.XmlUtils.beginDocument(r5, r8)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L50 org.xmlpull.v1.XmlPullParserException -> L55
            int r8 = r5.getDepth()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L50 org.xmlpull.v1.XmlPullParserException -> L55
            r9 = r4
        L8c:
            int r10 = r5.next()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            r11 = 3
            if (r10 != r11) goto L99
            int r12 = r5.getDepth()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            if (r12 <= r8) goto L17d
        L99:
            r12 = 1
            if (r10 == r12) goto L17d
            r13 = 2
            if (r10 == r13) goto La1
            goto L17a
        La1:
            java.lang.String r10 = r5.getName()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            java.lang.String r14 = r5.getAttributeNamespace(r4)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            int r14 = r14.length()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            if (r14 <= 0) goto Lb4
            java.lang.String r14 = r5.getAttributeNamespace(r4)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            goto Lb5
        Lb4:
            r14 = r3
        Lb5:
            java.lang.String r15 = "Carrier"
            boolean r15 = r15.equals(r10)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            if (r15 == 0) goto Lc8
            android.content.Context r15 = r1.mContext     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            boolean r15 = isAttributeCurrentCarrier(r15, r7, r4, r14)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            if (r15 != 0) goto Lc8
            com.lge.launcher3.util.Utilities.skipXmlTag(r5)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
        Lc8:
            java.lang.String r15 = "pageApp"
            boolean r15 = r15.equals(r10)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            r3 = -1
            if (r15 == 0) goto L113
            android.content.Context r10 = r1.mContext     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            com.lge.launcher3.allapps.PageAppAttr r10 = com.lge.launcher3.allapps.PageAppAttr.from(r10, r7, r4)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            r10.setNamespace(r14)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            int r3 = r10.getInt(r13, r3)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            java.lang.String r11 = r10.getString(r4)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            r10.recycle()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            boolean r10 = android.text.TextUtils.isEmpty(r11)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            if (r10 == 0) goto Lf7
            if (r6 == 0) goto Lf6
            r6.close()     // Catch: java.io.IOException -> Lf1
            goto Lf6
        Lf1:
            r0 = move-exception
            r2 = r0
            r2.printStackTrace()
        Lf6:
            return r9
        Lf7:
            if (r3 <= 0) goto L17a
            java.lang.Object r3 = r0.get(r3)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            com.android.launcher3.model.data.FolderInfo r3 = (com.android.launcher3.model.data.FolderInfo) r3     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            if (r3 == 0) goto L17a
            java.util.HashMap<android.content.ComponentName, java.lang.String> r10 = r1.mHashMap     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            android.content.ComponentName r11 = android.content.ComponentName.unflattenFromString(r11)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            java.lang.CharSequence r3 = r3.title     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            java.lang.String r3 = r3.toString()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            r10.put(r11, r3)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            int r9 = r9 + 1
            goto L17a
        L113:
            java.lang.String r15 = "pageFolder"
            boolean r10 = r15.equals(r10)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            if (r10 == 0) goto L17a
            android.content.Context r10 = r1.mContext     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            com.lge.launcher3.allapps.PageFolderAttr r10 = com.lge.launcher3.allapps.PageFolderAttr.from(r10, r7, r4)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            r10.setNamespace(r14)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            int r3 = r10.getInt(r11, r3)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            int r11 = r10.getInt(r13, r4)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            java.lang.String r12 = r10.getString(r12)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            r10.recycle()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            com.lge.launcher3.allapps.AllAppsFolderInfo r10 = new com.lge.launcher3.allapps.AllAppsFolderInfo     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            r10.<init>()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            android.content.Context r13 = r1.mContext     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            android.content.res.Resources r13 = r13.getResources()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            java.lang.String r14 = "string"
            android.content.Context r15 = r1.mContext     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            java.lang.String r15 = r15.getPackageName()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            int r13 = r13.getIdentifier(r12, r14, r15)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            if (r13 <= 0) goto L157
            android.content.Context r12 = r1.mContext     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            android.content.res.Resources r12 = r12.getResources()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            java.lang.String r12 = r12.getString(r13)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            goto L166
        L157:
            if (r12 != 0) goto L166
            android.content.Context r12 = r1.mContext     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            android.content.res.Resources r12 = r12.getResources()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            r13 = 2131820797(0x7f1100fd, float:1.927432E38)
            java.lang.String r12 = r12.getString(r13)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
        L166:
            long r13 = (long) r11     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            r10.id = r13     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            long r13 = (long) r3     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            r10.screenId = r13     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            r10.setTitle(r12)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            java.lang.Integer r3 = java.lang.Integer.valueOf(r11)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            int r3 = r3.intValue()     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
            r0.put(r3, r10)     // Catch: java.lang.Throwable -> L4b java.io.IOException -> L189 org.xmlpull.v1.XmlPullParserException -> L18c
        L17a:
            r3 = 0
            goto L8c
        L17d:
            if (r6 == 0) goto L1b2
            r6.close()     // Catch: java.io.IOException -> L183
            goto L1b2
        L183:
            r0 = move-exception
            r2 = r0
            r2.printStackTrace()
            goto L1b2
        L189:
            r0 = move-exception
            goto L52
        L18c:
            r0 = move-exception
            goto L57
        L18f:
            r0 = move-exception
            r2 = r0
            r3 = 0
            goto L1b5
        L193:
            r0 = move-exception
            r9 = r4
            r3 = 0
        L196:
            java.lang.String r5 = com.lge.launcher3.silentota.AllAppsFolderLayoutParser.LOG_TAG     // Catch: java.lang.Throwable -> L1b3
            int[] r4 = new int[r4]     // Catch: java.lang.Throwable -> L1b3
            com.lge.launcher3.util.LGLog.w(r5, r2, r0, r4)     // Catch: java.lang.Throwable -> L1b3
            if (r3 == 0) goto L1b2
            r3.close()     // Catch: java.io.IOException -> L183
            goto L1b2
        L1a3:
            r0 = move-exception
            r9 = r4
            r3 = 0
        L1a6:
            java.lang.String r5 = com.lge.launcher3.silentota.AllAppsFolderLayoutParser.LOG_TAG     // Catch: java.lang.Throwable -> L1b3
            int[] r4 = new int[r4]     // Catch: java.lang.Throwable -> L1b3
            com.lge.launcher3.util.LGLog.w(r5, r2, r0, r4)     // Catch: java.lang.Throwable -> L1b3
            if (r3 == 0) goto L1b2
            r3.close()     // Catch: java.io.IOException -> L183
        L1b2:
            return r9
        L1b3:
            r0 = move-exception
            r2 = r0
        L1b5:
            if (r3 == 0) goto L1c0
            r3.close()     // Catch: java.io.IOException -> L1bb
            goto L1c0
        L1bb:
            r0 = move-exception
            r3 = r0
            r3.printStackTrace()
        L1c0:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.silentota.AllAppsFolderLayoutParser.parseLayout():int");
    }

    public String getFolderName(ComponentName componentName) {
        return this.mHashMap.get(componentName);
    }
}
