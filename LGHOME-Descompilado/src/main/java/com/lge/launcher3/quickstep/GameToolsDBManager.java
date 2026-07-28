package com.lge.launcher3.quickstep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class GameToolsDBManager {
    private static final Uri CONTENT_URI;
    private static final Uri GAME_LIST_URI;
    private static final String GT_AUTHORITY = "com.lge.gametuner";
    private static final String TAG = "GameToolsDBManager";
    private static volatile GameToolsDBManager sInstance;
    private Context mContext;
    private ArrayList<String> mGameList = new ArrayList<>();
    private boolean mListening = false;
    private boolean mIsObserverRegistered = false;
    private final ContentObserver mGameDBObserver = new ContentObserver(new Handler()) { // from class: com.lge.launcher3.quickstep.GameToolsDBManager.1
        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            LGLog.d(GameToolsDBManager.TAG, "gameDB changed, loadList");
            GameToolsDBManager.this.loadList();
        }
    };
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.lge.launcher3.quickstep.GameToolsDBManager.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            action.hashCode();
            if (action.equals("android.intent.action.USER_UNLOCKED") || action.equals("android.intent.action.USER_SWITCHED")) {
                LGLog.d(GameToolsDBManager.TAG, "onReceive " + intent.getAction() + ", loadList");
                GameToolsDBManager.this.loadList();
            }
        }
    };

    static {
        Uri uri = Uri.parse("content://com.lge.gametuner/");
        CONTENT_URI = uri;
        GAME_LIST_URI = Uri.withAppendedPath(uri, "games");
        sInstance = null;
    }

    public static GameToolsDBManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (GameToolsDBManager.class) {
                if (sInstance == null) {
                    sInstance = new GameToolsDBManager(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    public GameToolsDBManager(Context context) {
        this.mContext = context;
        loadList();
    }

    public boolean isInDBList(String pkgName) {
        if (pkgName == null) {
            return false;
        }
        if (!this.mIsObserverRegistered) {
            registerContentObserver();
        }
        int size = this.mGameList.size();
        for (int i = 0; i < size; i++) {
            if (pkgName.equalsIgnoreCase(this.mGameList.get(i))) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadList() {
        if (!hasContentProvider(this.mContext, GT_AUTHORITY)) {
            LGLog.i(TAG, "Failed to find provider: com.lge.gametuner, do not load gamelist");
            return;
        }
        this.mGameList.clear();
        Cursor cursorQuery = null;
        try {
            try {
                cursorQuery = this.mContext.getContentResolver().query(GAME_LIST_URI, null, null, null, null);
                if (cursorQuery != null) {
                    cursorQuery.moveToFirst();
                    int columnIndex = cursorQuery.getColumnIndex("app_package_name");
                    while (!cursorQuery.isAfterLast()) {
                        LGLog.i(TAG, "loadList()  " + cursorQuery.getString(columnIndex) + "is added to GameList");
                        this.mGameList.add(cursorQuery.getString(columnIndex));
                        cursorQuery.moveToNext();
                    }
                }
            } catch (Exception e) {
                LGLog.i(TAG, "cursor error : " + e.getMessage());
                if (cursorQuery != null) {
                }
            }
        } finally {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            printGameList();
        }
    }

    private boolean hasContentProvider(final Context context, final String authority) {
        return context.getPackageManager().resolveContentProvider(authority, 0) != null;
    }

    private void registerContentObserver() {
        if (hasContentProvider(this.mContext, GT_AUTHORITY)) {
            this.mContext.getContentResolver().registerContentObserver(GAME_LIST_URI, true, this.mGameDBObserver, -1);
            this.mIsObserverRegistered = true;
            this.mGameDBObserver.onChange(true);
            return;
        }
        LGLog.i(TAG, "Failed to find provider: com.lge.gametuner, do not register observer");
    }

    private void printGameList() {
        if (this.mGameList != null) {
            LGLog.i(TAG, "============ game list ============");
            int size = this.mGameList.size();
            for (int i = 0; i < size; i++) {
                LGLog.i(TAG, "[ " + this.mGameList.get(i) + " ]");
            }
            LGLog.i(TAG, "=================================");
        }
    }
}
