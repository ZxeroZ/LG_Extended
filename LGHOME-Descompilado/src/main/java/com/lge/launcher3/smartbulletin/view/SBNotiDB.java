package com.lge.launcher3.smartbulletin.view;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.CursorWindowAllocationException;
import android.os.Handler;
import com.lge.launcher3.smartbulletin.info.SBAppWidgetProviderInfo;
import com.lge.launcher3.smartbulletin.log.SBLog;
import com.lge.launcher3.smartbulletin.provider.SBContract;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class SBNotiDB extends ContentObserver {
    private static final Comparator<SBNoti> SBNOTI_INDEX_COMPARATOR = new Comparator<SBNoti>() { // from class: com.lge.launcher3.smartbulletin.view.SBNotiDB.1
        /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
        @Override // java.util.Comparator
        public int compare(SBNoti a, SBNoti b) {
            if (a == null || b == null) {
                return 0;
            }
            return a.mTime >= b.mTime ? -1 : 1;
        }
    };
    private static final String TAG = "SBNotiDB";
    private Context mContext;
    private Handler mHandler;
    private ArrayList<SBNoti> mNotiList;

    public interface INotiObserver {
        void syncNotiList();
    }

    public SBNotiDB(Context context, Handler handler) {
        super(handler);
        this.mHandler = handler;
        this.mNotiList = getNotiList(context);
        this.mContext = context;
        syncProvider();
    }

    public void registerObserver(Context context) {
        context.getContentResolver().registerContentObserver(SBContract.SmartBulletin.NOTI_URI, true, this);
    }

    public void unregisterObserver(Context context) {
        context.getContentResolver().unregisterContentObserver(this);
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        SBLog.d(TAG, "onChange() selfChange:" + selfChange);
        this.mNotiList = getNotiList(this.mContext);
        syncProvider();
        this.mHandler.sendEmptyMessage(1000);
    }

    private void syncProvider() {
        ArrayList<SBAppWidgetProviderInfo> allProvider = SBContract.SmartBulletin.getAllProvider(this.mContext);
        ArrayList<SBNoti> arrayList = new ArrayList(getNotiList());
        ArrayList arrayList2 = new ArrayList();
        if (arrayList.size() <= 0) {
            return;
        }
        for (SBNoti sBNoti : arrayList) {
            SBAppWidgetProviderInfo provider = getProvider(sBNoti.mComponentName, allProvider);
            if (provider == null || provider.mAppWidgetProviderInfo == null || !provider.mIsEnabled) {
                arrayList2.add(sBNoti);
            }
        }
        Iterator it = arrayList2.iterator();
        while (it.hasNext()) {
            removeNoti(this.mContext, (SBNoti) it.next());
        }
    }

    SBAppWidgetProviderInfo getProvider(String componentName, ArrayList<SBAppWidgetProviderInfo> list) {
        for (SBAppWidgetProviderInfo sBAppWidgetProviderInfo : list) {
            if (componentName.equals(sBAppWidgetProviderInfo.getComponentName().flattenToString())) {
                return sBAppWidgetProviderInfo;
            }
        }
        return null;
    }

    public ArrayList<SBNoti> getNotiList() {
        return this.mNotiList;
    }

    public int getNotiCount() {
        return this.mNotiList.size();
    }

    public boolean hasNoti(String componentName) {
        Iterator<SBNoti> it = this.mNotiList.iterator();
        while (it.hasNext()) {
            if (it.next().mComponentName.equals(componentName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEnableNoti(String componentName) {
        SBAppWidgetProviderInfo provider = getProvider(componentName, SBContract.SmartBulletin.getAllProvider(this.mContext));
        return provider != null && provider.mIsEnabled;
    }

    public void removeNoti(Context context, SBNoti noti) {
        if (noti == null) {
            return;
        }
        for (int i = 0; i < this.mNotiList.size(); i++) {
            SBNoti sBNoti = this.mNotiList.get(i);
            if (sBNoti.mComponentName.equals(noti.mComponentName) && sBNoti.mTime == noti.mTime && sBNoti.mNotiType.equals(noti.mNotiType)) {
                this.mNotiList.remove(sBNoti);
                removeNotiDatabase(context, noti);
                return;
            }
        }
    }

    public void removeInfo(Context context, SBAppWidgetProviderInfo info) {
        for (SBNoti sBNoti : this.mNotiList) {
            if (sBNoti.mComponentName.equals(info.getComponentName().flattenToString())) {
                removeNoti(context, sBNoti);
                return;
            }
        }
    }

    private static ArrayList<SBNoti> getNotiList(Context context) {
        Cursor cursorQuery;
        try {
            cursorQuery = context.getContentResolver().query(SBContract.SmartBulletin.NOTI_URI, null, null, null, null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            cursorQuery = null;
        } catch (SecurityException e2) {
            e2.printStackTrace();
            cursorQuery = null;
        }
        if (cursorQuery != null) {
            return getNotiListFromCursor(context, cursorQuery);
        }
        return new ArrayList<>();
    }

    private void removeNotiDatabase(Context context, SBNoti noti) {
        ContentResolver contentResolver = context.getContentResolver();
        try {
            contentResolver.delete(SBContract.SmartBulletin.NOTI_URI, "_id=\"" + noti.mID + "\"", null);
        } catch (CursorWindowAllocationException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
        } catch (SecurityException e3) {
            e3.printStackTrace();
        }
    }

    private static ArrayList<SBNoti> getNotiListFromCursor(Context context, Cursor c) {
        ArrayList<SBNoti> arrayList = new ArrayList<>();
        arrayList.clear();
        try {
            int columnIndexOrThrow = c.getColumnIndexOrThrow("_id");
            int columnIndexOrThrow2 = c.getColumnIndexOrThrow(SBContract.SmartBulletin.NOTI_TIME);
            int columnIndexOrThrow3 = c.getColumnIndexOrThrow(SBContract.SmartBulletin.NOTI_TYPE);
            int columnIndexOrThrow4 = c.getColumnIndexOrThrow(SBContract.SmartBulletin.NOTI_ICONRES);
            int columnIndexOrThrow5 = c.getColumnIndexOrThrow("componentName");
            while (c.moveToNext()) {
                int i = c.getInt(columnIndexOrThrow);
                SBNoti sBNoti = new SBNoti(c.getLong(columnIndexOrThrow2), c.getString(columnIndexOrThrow3), c.getString(columnIndexOrThrow4), c.getString(columnIndexOrThrow5));
                sBNoti.mID = i;
                arrayList.add(sBNoti);
            }
            c.close();
            Collections.sort(arrayList, SBNOTI_INDEX_COMPARATOR);
            return arrayList;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            c.close();
            return arrayList;
        } catch (SecurityException e2) {
            e2.printStackTrace();
            c.close();
            return arrayList;
        }
    }
}
