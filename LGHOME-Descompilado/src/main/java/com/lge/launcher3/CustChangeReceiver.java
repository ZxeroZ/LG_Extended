package com.lge.launcher3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.android.launcher3.LauncherFiles;
import com.lge.launcher3.util.LGLog;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class CustChangeReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        File file = new File(new File(context.getCacheDir().getParent()), "databases" + File.separator + LauncherFiles.LAUNCHER_DB);
        if (file.exists()) {
            file.delete();
            LGLog.w("CustChangedReceiver", "[LGE][SBP] CUST Changed Receiver call to Delete the LGHOME DB", new int[0]);
        } else {
            LGLog.w("CustChangedReceiver", "[LGE][SBP] DB file is not exists", new int[0]);
        }
        Intent intent2 = new Intent("com.lge.action.CUST_COMPLETE_INFO");
        intent2.setPackage("com.android.phone");
        intent2.putExtra("appIndex", 0);
        context.sendBroadcast(intent2);
    }
}
