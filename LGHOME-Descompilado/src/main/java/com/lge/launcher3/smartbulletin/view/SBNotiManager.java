package com.lge.launcher3.smartbulletin.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.lge.launcher3.smartbulletin.info.SBAppWidgetProviderInfo;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class SBNotiManager {
    private static SBNotiManager sNotiInstance;
    private SBNotiDB mNotiDB;
    private ISBNotiPanel mNotiInterface;
    private SBNotiReceiver mNotiReceiver = null;
    private Handler mHandler = new Handler() { // from class: com.lge.launcher3.smartbulletin.view.SBNotiManager.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what != 1000) {
                return;
            }
            SBNotiManager.this.mNotiInterface.updateNotiCountView();
        }
    };

    public interface ISBNotiPanel {
        void updateNotiCountView();
    }

    public void setSBNotiInterface(ISBNotiPanel notiInterface) {
        this.mNotiInterface = notiInterface;
    }

    public static SBNotiManager getInstance(Context context) {
        if (sNotiInstance == null) {
            sNotiInstance = new SBNotiManager(context.getApplicationContext());
        }
        return sNotiInstance;
    }

    private SBNotiManager(Context context) {
        this.mNotiDB = new SBNotiDB(context, this.mHandler);
    }

    public void registerNotiReceiver(Context context) {
        this.mNotiReceiver = new SBNotiReceiver();
        SBNotiDB sBNotiDB = this.mNotiDB;
        if (sBNotiDB != null) {
            sBNotiDB.registerObserver(context);
        }
        SBNotiReceiver sBNotiReceiver = this.mNotiReceiver;
        if (sBNotiReceiver != null) {
            sBNotiReceiver.registerNotiReceiver(context);
        }
    }

    public void unregisterNotiReceiver(Context context) {
        SBNotiReceiver sBNotiReceiver = this.mNotiReceiver;
        if (sBNotiReceiver != null) {
            sBNotiReceiver.unregisterNotiReceiver(context);
            this.mNotiReceiver = null;
        }
        SBNotiDB sBNotiDB = this.mNotiDB;
        if (sBNotiDB != null) {
            sBNotiDB.unregisterObserver(context);
            this.mNotiDB = null;
        }
        sNotiInstance = null;
    }

    public int getNotiCount() {
        SBNotiDB sBNotiDB = this.mNotiDB;
        if (sBNotiDB == null) {
            return 0;
        }
        int notiCount = sBNotiDB.getNotiCount();
        Iterator<SBNoti> it = this.mNotiDB.getNotiList().iterator();
        while (it.hasNext()) {
            if (!this.mNotiDB.isEnableNoti(it.next().mComponentName)) {
                notiCount--;
            }
        }
        return notiCount;
    }

    public void updateProviderLayout(SBCategoryLayout layout) {
        if (this.mNotiDB == null) {
            return;
        }
        int childCount = layout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            SBProviderLayout sBProviderLayout = (SBProviderLayout) layout.getChildAt(i);
            sBProviderLayout.removeAllBadge();
            for (SBNoti sBNoti : this.mNotiDB.getNotiList()) {
                if (this.mNotiDB.isEnableNoti(sBNoti.mComponentName)) {
                    sBProviderLayout.addBadge(sBNoti);
                }
            }
        }
    }

    public void removeOnceInDB(Context context, SBAppWidgetProviderInfo info) {
        SBNotiDB sBNotiDB = this.mNotiDB;
        if (sBNotiDB == null) {
            return;
        }
        sBNotiDB.removeInfo(context, info);
    }
}
