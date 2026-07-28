package com.lge.launcher3.concierge;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import com.android.launcher3.LauncherSettings;
import com.lge.launcher3.homesettings.SBHomeDataBaseUtil;

/* JADX INFO: loaded from: classes.dex */
public class ConciergeBoardNotificationReceiver extends BroadcastReceiver {
    private static final String ACTION = "com.lge.concierge.ACTION_GO_TO_CONCIERGE";
    private static final String CLASSNAME = "com.lge.concierge.ConciergeWidgetProvider";
    private static ConciergeBoardNotificationReceiver sNotiReceiver;
    private IWorkspaceMove mWorkspaceMove = null;

    public interface IWorkspaceMove {
        void gotoConcirergeBoard();
    }

    public static synchronized ConciergeBoardNotificationReceiver getInstance() {
        if (sNotiReceiver == null) {
            sNotiReceiver = new ConciergeBoardNotificationReceiver();
        }
        return sNotiReceiver;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        IWorkspaceMove iWorkspaceMove = this.mWorkspaceMove;
        if (iWorkspaceMove != null) {
            iWorkspaceMove.gotoConcirergeBoard();
        }
    }

    public void addWorkSpaceMoveInterface(IWorkspaceMove iw) {
        this.mWorkspaceMove = iw;
    }

    public static void registerReceiver(Context context) {
        context.registerReceiver(getInstance(), new IntentFilter(ACTION));
    }

    public static void unregisterReceiver(Context context) {
        context.unregisterReceiver(getInstance());
    }

    public static int isExistConciergeBoardScreenInDatabase(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursorQuery = contentResolver.query(LauncherSettings.Favorites.CONTENT_URI, null, "itemType=4 AND appWidgetProvider like '%com.lge.concierge.ConciergeWidgetProvider%'", null, null);
        if (cursorQuery == null) {
            return -1;
        }
        if (cursorQuery.getCount() == 0) {
            cursorQuery.close();
            return -1;
        }
        cursorQuery.moveToFirst();
        int i = cursorQuery.getInt(cursorQuery.getColumnIndex("screen"));
        cursorQuery.close();
        int screenIndex = toScreenIndex(contentResolver, i);
        if (screenIndex == -1) {
            return screenIndex;
        }
        if (SBHomeDataBaseUtil.existSmartBulletinItemInDataBase(context)) {
            screenIndex++;
        }
        return SBHomeDataBaseUtil.existQmemoPanelItemInDataBase(context) ? screenIndex + 1 : screenIndex;
    }

    public static int toScreenIndex(ContentResolver cr, int screenId) {
        Cursor cursorQuery = cr.query(LauncherSettings.WorkspaceScreens.CONTENT_URI, null, null, null, "screenRank ASC");
        int i = -1;
        if (cursorQuery == null) {
            return -1;
        }
        if (cursorQuery.getCount() == 0) {
            cursorQuery.close();
            return -1;
        }
        while (cursorQuery.moveToNext()) {
            i++;
            if (cursorQuery.getInt(cursorQuery.getColumnIndex("_id")) == screenId) {
                break;
            }
        }
        cursorQuery.close();
        return i;
    }
}
