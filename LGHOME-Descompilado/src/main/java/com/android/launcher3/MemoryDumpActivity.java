package com.android.launcher3;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;
import com.android.launcher3.MemoryTracker;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class MemoryDumpActivity extends Activity {
    private static final String TAG = "MemoryDumpActivity";

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /* JADX WARN: Not initialized variable reg: 6, insn: 0x0082: MOVE (r3 I:??[OBJECT, ARRAY]) = (r6 I:??[OBJECT, ARRAY]), block:B:34:0x0082 */
    /* JADX WARN: Removed duplicated region for block: B:44:0x0085 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.lang.String zipUp(java.util.ArrayList<java.lang.String> r9) throws java.lang.Throwable {
        /*
            r0 = 262144(0x40000, float:3.67342E-40)
            byte[] r1 = new byte[r0]
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.io.File r3 = android.os.Environment.getExternalStorageDirectory()
            r4 = 0
            r2[r4] = r3
            long r5 = java.lang.System.currentTimeMillis()
            java.lang.Long r3 = java.lang.Long.valueOf(r5)
            r5 = 1
            r2[r5] = r3
            java.lang.String r3 = "%s/hprof-%d.zip"
            java.lang.String r2 = java.lang.String.format(r3, r2)
            r3 = 0
            java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch: java.lang.Throwable -> L70 java.io.IOException -> L72
            r5.<init>(r2)     // Catch: java.lang.Throwable -> L70 java.io.IOException -> L72
            java.util.zip.ZipOutputStream r6 = new java.util.zip.ZipOutputStream     // Catch: java.lang.Throwable -> L70 java.io.IOException -> L72
            java.io.BufferedOutputStream r7 = new java.io.BufferedOutputStream     // Catch: java.lang.Throwable -> L70 java.io.IOException -> L72
            r7.<init>(r5)     // Catch: java.lang.Throwable -> L70 java.io.IOException -> L72
            r6.<init>(r7)     // Catch: java.lang.Throwable -> L70 java.io.IOException -> L72
            java.util.Iterator r9 = r9.iterator()     // Catch: java.io.IOException -> L6e java.lang.Throwable -> L81
        L33:
            boolean r5 = r9.hasNext()     // Catch: java.io.IOException -> L6e java.lang.Throwable -> L81
            if (r5 == 0) goto L6a
            java.lang.Object r5 = r9.next()     // Catch: java.io.IOException -> L6e java.lang.Throwable -> L81
            java.lang.String r5 = (java.lang.String) r5     // Catch: java.io.IOException -> L6e java.lang.Throwable -> L81
            java.io.BufferedInputStream r7 = new java.io.BufferedInputStream     // Catch: java.lang.Throwable -> L64
            java.io.FileInputStream r8 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L64
            r8.<init>(r5)     // Catch: java.lang.Throwable -> L64
            r7.<init>(r8)     // Catch: java.lang.Throwable -> L64
            java.util.zip.ZipEntry r8 = new java.util.zip.ZipEntry     // Catch: java.lang.Throwable -> L62
            r8.<init>(r5)     // Catch: java.lang.Throwable -> L62
            r6.putNextEntry(r8)     // Catch: java.lang.Throwable -> L62
        L51:
            int r5 = r7.read(r1, r4, r0)     // Catch: java.lang.Throwable -> L62
            if (r5 <= 0) goto L5b
            r6.write(r1, r4, r5)     // Catch: java.lang.Throwable -> L62
            goto L51
        L5b:
            r6.closeEntry()     // Catch: java.lang.Throwable -> L62
            r7.close()     // Catch: java.io.IOException -> L6e java.lang.Throwable -> L81
            goto L33
        L62:
            r9 = move-exception
            goto L66
        L64:
            r9 = move-exception
            r7 = r3
        L66:
            r7.close()     // Catch: java.io.IOException -> L6e java.lang.Throwable -> L81
            throw r9     // Catch: java.io.IOException -> L6e java.lang.Throwable -> L81
        L6a:
            r6.close()     // Catch: java.io.IOException -> L6d
        L6d:
            return r2
        L6e:
            r9 = move-exception
            goto L74
        L70:
            r9 = move-exception
            goto L83
        L72:
            r9 = move-exception
            r6 = r3
        L74:
            java.lang.String r0 = "MemoryDumpActivity"
            java.lang.String r1 = "error zipping up profile data"
            android.util.Log.e(r0, r1, r9)     // Catch: java.lang.Throwable -> L81
            if (r6 == 0) goto L80
            r6.close()     // Catch: java.io.IOException -> L80
        L80:
            return r3
        L81:
            r9 = move-exception
            r3 = r6
        L83:
            if (r3 == 0) goto L88
            r3.close()     // Catch: java.io.IOException -> L88
        L88:
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.MemoryDumpActivity.zipUp(java.util.ArrayList):java.lang.String");
    }

    public static void dumpHprofAndShare(final Context context, MemoryTracker tracker) throws Throwable {
        String str;
        StringBuilder sb = new StringBuilder();
        ArrayList arrayList = new ArrayList();
        int iMyPid = Process.myPid();
        int[] trackedProcesses = tracker.getTrackedProcesses();
        for (int i : Arrays.copyOf(trackedProcesses, trackedProcesses.length)) {
            MemoryTracker.ProcessMemInfo memInfo = tracker.getMemInfo(i);
            if (memInfo != null) {
                sb.append("pid ").append(i).append(":").append(" up=").append(memInfo.getUptime()).append(" pss=").append(memInfo.currentPss).append(" uss=").append(memInfo.currentUss).append("\n");
            }
            if (i == iMyPid) {
                String str2 = String.format("%s/launcher-memory-%d.ahprof", Environment.getExternalStorageDirectory(), Integer.valueOf(i));
                Log.v(TAG, "Dumping memory info for process " + i + " to " + str2);
                try {
                    Debug.dumpHprofData(str2);
                } catch (IOException e) {
                    Log.e(TAG, "error dumping memory:", e);
                }
                arrayList.add(str2);
            }
        }
        String strZipUp = zipUp(arrayList);
        if (strZipUp == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("application/zip");
        PackageManager packageManager = context.getPackageManager();
        intent.putExtra("android.intent.extra.SUBJECT", String.format("Launcher memory dump (%d)", Integer.valueOf(iMyPid)));
        try {
            str = packageManager.getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException unused) {
            str = "?";
        }
        sb.append("\nApp version: ").append(str).append("\nBuild: ").append(Build.DISPLAY).append("\n");
        intent.putExtra("android.intent.extra.TEXT", sb.toString());
        intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(strZipUp)));
        context.startActivity(intent);
    }

    @Override // android.app.Activity
    public void onStart() {
        super.onStart();
        startDump(this, new Runnable() { // from class: com.android.launcher3.MemoryDumpActivity.1
            @Override // java.lang.Runnable
            public void run() {
                MemoryDumpActivity.this.finish();
            }
        });
    }

    public static void startDump(final Context context) {
        startDump(context, null);
    }

    public static void startDump(final Context context, final Runnable andThen) {
        ServiceConnection serviceConnection = new ServiceConnection() { // from class: com.android.launcher3.MemoryDumpActivity.2
            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName className) {
            }

            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName className, IBinder service) throws Throwable {
                Log.v(MemoryDumpActivity.TAG, "service connected, dumping...");
                MemoryDumpActivity.dumpHprofAndShare(context, ((MemoryTracker.MemoryTrackerInterface) service).getService());
                context.unbindService(this);
                Runnable runnable = andThen;
                if (runnable != null) {
                    runnable.run();
                }
            }
        };
        Log.v(TAG, "attempting to bind to memory tracker");
        context.bindService(new Intent(context, (Class<?>) MemoryTracker.class), serviceConnection, 1);
    }
}
