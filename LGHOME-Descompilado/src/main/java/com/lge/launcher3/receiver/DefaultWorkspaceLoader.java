package com.lge.launcher3.receiver;

import android.content.Context;
import android.text.TextUtils;
import com.lge.launcher3.R;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.util.DefaultWorkspaceChecker;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class DefaultWorkspaceLoader {
    public static int getIDFromCAList(Context context) {
        String[] stringArray = context.getResources().getStringArray(R.array.workspaceca);
        if (stringArray.length == 0) {
            LGLog.i("LGDefaultWorkspace", "workspaceCa List is Empty");
            return 0;
        }
        DefaultWorkspaceChecker.init(context);
        int id = 0;
        for (String str : stringArray) {
            id = getID(context, str);
            if (id != 0) {
                break;
            }
        }
        if (id == 0) {
            SharedPreferencesManager.putBoolean(context, 0, SharedPreferencesConst.WorkspaceCAKey.ISLOADING, false);
        }
        DefaultWorkspaceChecker.destory();
        return id;
    }

    public static String getLayoutNameFromCAList(Context context) {
        String[] stringArray = context.getResources().getStringArray(R.array.workspaceca);
        String layoutName = null;
        if (stringArray.length == 0) {
            LGLog.i("LGDefaultWorkspace", "workspaceCa List is Empty");
            return null;
        }
        DefaultWorkspaceChecker.init(context);
        for (String str : stringArray) {
            layoutName = getLayoutName(context, str);
            if (!TextUtils.isEmpty(layoutName)) {
                break;
            }
        }
        DefaultWorkspaceChecker.destory();
        return layoutName;
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x006b, code lost:
    
        if (r4 == null) goto L42;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x006d, code lost:
    
        com.lge.launcher3.util.LGLog.i("DefaultWorkspaceLoader", r12 + " loading");
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0087, code lost:
    
        if (com.lge.launcher3.util.LGHomeFeature.isEnableDefaultHome() == false) goto L23;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0089, code lost:
    
        r12 = "";
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x008f, code lost:
    
        if (com.lge.launcher3.util.LGHomeFeature.isDisableEasyHome() == false) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0091, code lost:
    
        r12 = "_allapps";
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0093, code lost:
    
        r12 = "_easyhome";
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0099, code lost:
    
        if (r12.equals("_allapps") == false) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x009f, code lost:
    
        if (com.lge.launcher3.util.LGHomeFeature.isLoadDefaultWorkspaceFile() == false) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x00a2, code lost:
    
        r1 = r12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x00c1, code lost:
    
        return r11.getResources().getIdentifier(r4 + r1, "xml", r11.getBasePackageName());
     */
    /* JADX WARN: Code restructure failed: missing block: B:42:?, code lost:
    
        return 0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static int getID(android.content.Context r11, java.lang.String r12) {
        /*
            java.lang.String r0 = " "
            java.lang.String r1 = ""
            java.lang.String r12 = r12.replaceAll(r0, r1)
            java.lang.String r0 = ","
            java.lang.String[] r0 = r12.split(r0)
            int r2 = r0.length
            r3 = 0
            r4 = 0
            r5 = r3
        L12:
            java.lang.String r6 = "DefaultWorkspaceLoader"
            if (r5 >= r2) goto L6b
            r7 = r0[r5]
            java.lang.String r8 = "="
            java.lang.String[] r8 = r7.split(r8)
            int r9 = r8.length
            r10 = 2
            if (r9 == r10) goto L41
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "It is incorrect :"
            r0.append(r2)
            r0.append(r12)
            java.lang.String r2 = " Error = "
            r0.append(r2)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            int[] r2 = new int[r3]
            com.lge.launcher3.util.LGLog.w(r6, r0, r2)
            goto L6b
        L41:
            r6 = r8[r3]
            java.lang.String r7 = "workspace"
            boolean r6 = r7.equals(r6)
            r7 = 1
            if (r6 == 0) goto L50
            r4 = r8[r7]
            goto L67
        L50:
            r6 = r8[r3]
            java.lang.String r6 = r6.toLowerCase()
            com.lge.launcher3.util.DefaultWorkspaceChecker$CheckingInterface r6 = com.lge.launcher3.util.DefaultWorkspaceChecker.getCheckClass(r6)
            if (r6 == 0) goto L6a
            if (r6 == 0) goto L67
            r7 = r8[r7]
            boolean r6 = r6.isMatching(r11, r7)
            if (r6 != 0) goto L67
            goto L6a
        L67:
            int r5 = r5 + 1
            goto L12
        L6a:
            return r3
        L6b:
            if (r4 == 0) goto Lc1
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r12)
            java.lang.String r12 = " loading"
            r0.append(r12)
            java.lang.String r12 = r0.toString()
            com.lge.launcher3.util.LGLog.i(r6, r12)
            boolean r12 = com.lge.launcher3.util.LGHomeFeature.isEnableDefaultHome()
            java.lang.String r0 = "_allapps"
            if (r12 == 0) goto L8b
            r12 = r1
            goto L95
        L8b:
            boolean r12 = com.lge.launcher3.util.LGHomeFeature.isDisableEasyHome()
            if (r12 == 0) goto L93
            r12 = r0
            goto L95
        L93:
            java.lang.String r12 = "_easyhome"
        L95:
            boolean r0 = r12.equals(r0)
            if (r0 == 0) goto La2
            boolean r0 = com.lge.launcher3.util.LGHomeFeature.isLoadDefaultWorkspaceFile()
            if (r0 == 0) goto La2
            goto La3
        La2:
            r1 = r12
        La3:
            android.content.res.Resources r12 = r11.getResources()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r4)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r11 = r11.getBasePackageName()
            java.lang.String r1 = "xml"
            int r3 = r12.getIdentifier(r0, r1, r11)
        Lc1:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.receiver.DefaultWorkspaceLoader.getID(android.content.Context, java.lang.String):int");
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x006c, code lost:
    
        if (r6 == null) goto L33;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x006e, code lost:
    
        com.lge.launcher3.util.LGLog.i("DefaultWorkspaceLoader", r13 + " loading");
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0088, code lost:
    
        if (com.lge.launcher3.util.LGHomeFeature.isEnableDefaultHome() == false) goto L23;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x008a, code lost:
    
        r12 = "";
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0090, code lost:
    
        if (com.lge.launcher3.util.LGHomeFeature.isDisableEasyHome() == false) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0092, code lost:
    
        r12 = "_allapps";
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0094, code lost:
    
        r12 = "_easyhome";
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x009a, code lost:
    
        if (r12.equals("_allapps") == false) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x00a0, code lost:
    
        if (com.lge.launcher3.util.LGHomeFeature.isLoadDefaultWorkspaceFile() == false) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x00a3, code lost:
    
        r1 = r12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00a5, code lost:
    
        r1 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x00b5, code lost:
    
        return r6 + r1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static java.lang.String getLayoutName(android.content.Context r12, java.lang.String r13) {
        /*
            java.lang.String r0 = " "
            java.lang.String r1 = ""
            java.lang.String r13 = r13.replaceAll(r0, r1)
            java.lang.String r0 = ","
            java.lang.String[] r0 = r13.split(r0)
            int r2 = r0.length
            r3 = 0
            r4 = 0
            r6 = r3
            r5 = r4
        L13:
            java.lang.String r7 = "DefaultWorkspaceLoader"
            if (r5 >= r2) goto L6c
            r8 = r0[r5]
            java.lang.String r9 = "="
            java.lang.String[] r9 = r8.split(r9)
            int r10 = r9.length
            r11 = 2
            if (r10 == r11) goto L42
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r0 = "It is incorrect :"
            r12.append(r0)
            r12.append(r13)
            java.lang.String r0 = " Error = "
            r12.append(r0)
            r12.append(r8)
            java.lang.String r12 = r12.toString()
            int[] r0 = new int[r4]
            com.lge.launcher3.util.LGLog.w(r7, r12, r0)
            goto L6c
        L42:
            r7 = r9[r4]
            java.lang.String r8 = "workspace"
            boolean r7 = r8.equals(r7)
            r8 = 1
            if (r7 == 0) goto L51
            r6 = r9[r8]
            goto L68
        L51:
            r7 = r9[r4]
            java.lang.String r7 = r7.toLowerCase()
            com.lge.launcher3.util.DefaultWorkspaceChecker$CheckingInterface r7 = com.lge.launcher3.util.DefaultWorkspaceChecker.getCheckClass(r7)
            if (r7 == 0) goto L6b
            if (r7 == 0) goto L68
            r8 = r9[r8]
            boolean r7 = r7.isMatching(r12, r8)
            if (r7 != 0) goto L68
            goto L6b
        L68:
            int r5 = r5 + 1
            goto L13
        L6b:
            return r3
        L6c:
            if (r6 == 0) goto La5
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r13)
            java.lang.String r13 = " loading"
            r12.append(r13)
            java.lang.String r12 = r12.toString()
            com.lge.launcher3.util.LGLog.i(r7, r12)
            boolean r12 = com.lge.launcher3.util.LGHomeFeature.isEnableDefaultHome()
            java.lang.String r13 = "_allapps"
            if (r12 == 0) goto L8c
            r12 = r1
            goto L96
        L8c:
            boolean r12 = com.lge.launcher3.util.LGHomeFeature.isDisableEasyHome()
            if (r12 == 0) goto L94
            r12 = r13
            goto L96
        L94:
            java.lang.String r12 = "_easyhome"
        L96:
            boolean r13 = r12.equals(r13)
            if (r13 == 0) goto La3
            boolean r13 = com.lge.launcher3.util.LGHomeFeature.isLoadDefaultWorkspaceFile()
            if (r13 == 0) goto La3
            goto La6
        La3:
            r1 = r12
            goto La6
        La5:
            r1 = r3
        La6:
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r6)
            r12.append(r1)
            java.lang.String r12 = r12.toString()
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.receiver.DefaultWorkspaceLoader.getLayoutName(android.content.Context, java.lang.String):java.lang.String");
    }
}
