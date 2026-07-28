package com.lge.launcher3.smartbulletin.view;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.CursorWindowAllocationException;
import com.lge.launcher3.memory.MemoryUtils;
import com.lge.launcher3.smartbulletin.lib.Notification;
import com.lge.launcher3.smartbulletin.log.SBLog;
import com.lge.launcher3.smartbulletin.provider.SBContract;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class SBNotiReceiver extends BroadcastReceiver {
    private static final String TAG = "SBNotiReceiver";
    private boolean mRegistered = false;

    SBNotiReceiver() {
    }

    public void registerNotiReceiver(Context context) {
        synchronized (this) {
            if (!this.mRegistered) {
                LGLog.d(TAG, "Register NotiReceiver");
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(Notification.SBNOTI_ADD_INTENT);
                intentFilter.addAction(Notification.SBNOTI_REMOVE_INTENT);
                context.registerReceiver(this, intentFilter);
                this.mRegistered = true;
            }
        }
    }

    public void unregisterNotiReceiver(Context context) {
        synchronized (this) {
            if (this.mRegistered) {
                LGLog.d(TAG, "Unregister NotiReceiver");
                try {
                    context.unregisterReceiver(this);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                this.mRegistered = false;
            }
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String stringExtra = intent.getStringExtra(Notification.SBNOTI_TYPE);
        String stringExtra2 = intent.getStringExtra(Notification.SBNOTI_RESOURCE_URI);
        String stringExtra3 = intent.getStringExtra("component_name");
        SBLog.d(TAG, "onReceive : intent: " + intent + " component: " + stringExtra3);
        if (!MemoryUtils.hasAvailableFileSystemMemory(null, false)) {
            LGLog.i(TAG, "Memory is full. so updateById or insertNotiDatabase is canceled.");
            return;
        }
        if (intent.getAction().equals(Notification.SBNOTI_REMOVE_INTENT)) {
            deleteNotiDatabase(context, stringExtra3);
            return;
        }
        if (!stringExtra.equals(Notification.SBNOTI_TYPE_ONCE) && !stringExtra.equals(Notification.SBNOTI_TYPE_ONGOING)) {
            SBLog.e(TAG, "onReceive : invalid type " + stringExtra);
            return;
        }
        if (stringExtra3 == null) {
            SBLog.e(TAG, "onReceive : invalid componentString " + stringExtra3);
            return;
        }
        if (intent.getAction().equals(Notification.SBNOTI_ADD_INTENT)) {
            int idByComponentName = getIdByComponentName(context, stringExtra3);
            if (idByComponentName != -1) {
                updateById(context, idByComponentName, stringExtra, stringExtra2, stringExtra3);
            } else {
                insertNotiDatabase(context, stringExtra, stringExtra2, stringExtra3);
            }
        }
    }

    private void insertNotiDatabase(Context context, String notiType, String resUri, String componentString) {
        SBLog.d(TAG, "insert noti");
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SBContract.SmartBulletin.NOTI_TIME, Long.valueOf(System.currentTimeMillis()));
        contentValues.put("componentName", componentString);
        contentValues.put(SBContract.SmartBulletin.NOTI_ICONRES, resUri);
        contentValues.put(SBContract.SmartBulletin.NOTI_TYPE, notiType);
        contentResolver.insert(SBContract.SmartBulletin.NOTI_URI, contentValues);
    }

    private static int updateById(Context context, int id, String notiType, String resUri, String componentString) {
        ContentResolver contentResolver = context.getContentResolver();
        SBLog.d(TAG, "updateById");
        ContentValues contentValues = new ContentValues();
        contentValues.put(SBContract.SmartBulletin.NOTI_TIME, Long.valueOf(System.currentTimeMillis()));
        contentValues.put(SBContract.SmartBulletin.NOTI_ICONRES, resUri);
        contentValues.put(SBContract.SmartBulletin.NOTI_TYPE, notiType);
        try {
            return contentResolver.update(SBContract.SmartBulletin.NOTI_URI, contentValues, "_id=\"" + id + "\"", null);
        } catch (CursorWindowAllocationException e) {
            e.printStackTrace();
            return -1;
        } catch (IllegalArgumentException e2) {
            e2.printStackTrace();
            return -1;
        } catch (SecurityException e3) {
            e3.printStackTrace();
            return -1;
        }
    }

    private static int getIdByComponentName(Context context, String componentName) {
        Cursor cursorQuery;
        try {
            cursorQuery = context.getContentResolver().query(SBContract.SmartBulletin.NOTI_URI, new String[]{"_id", "componentName"}, "componentName=?", new String[]{componentName}, null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            cursorQuery = null;
        } catch (SecurityException e2) {
            e2.printStackTrace();
            cursorQuery = null;
        }
        if (cursorQuery == null) {
            return -1;
        }
        int idByCursor = getIdByCursor(context, cursorQuery);
        cursorQuery.close();
        return idByCursor;
    }

    private static int getIdByCursor(Context context, Cursor c) {
        int columnIndexOrThrow;
        ArrayList arrayList = new ArrayList();
        arrayList.clear();
        try {
            columnIndexOrThrow = c.getColumnIndexOrThrow("_id");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            c.close();
            columnIndexOrThrow = -1;
        } catch (SecurityException e2) {
            e2.printStackTrace();
            c.close();
            columnIndexOrThrow = -1;
        }
        while (c.moveToNext()) {
            arrayList.add(Integer.valueOf(c.getInt(columnIndexOrThrow)));
        }
        if (arrayList.size() > 1) {
            SBLog.e(TAG, "Same componentName item is exist : size=" + arrayList.size());
        }
        if (arrayList.size() == 0) {
            return -1;
        }
        return ((Integer) arrayList.get(0)).intValue();
    }

    private void deleteNotiDatabase(Context context, String componentString) {
        SBLog.d(TAG, "delete noti");
        context.getContentResolver().delete(SBContract.SmartBulletin.NOTI_URI, "componentName = ?", new String[]{componentString});
    }
}
