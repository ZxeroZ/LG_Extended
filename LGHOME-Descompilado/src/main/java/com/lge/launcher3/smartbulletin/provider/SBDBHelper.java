package com.lge.launcher3.smartbulletin.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.lge.launcher3.smartbulletin.log.SBLog;
import com.lge.launcher3.smartbulletin.provider.SBContract;

/* JADX INFO: loaded from: classes.dex */
public class SBDBHelper extends SQLiteOpenHelper {
    private static final String CREATE_SMARTBULLETIN_NOTI_TABLE = "CREATE TABLE IF NOT EXISTS smartbulletin_noti (_id INTEGER PRIMARY KEY AUTOINCREMENT,time BIGINT,type TEXT,res TEXT,componentName TEXT)";
    private static final String CREATE_SMARTBULLETIN_TABLE = "CREATE TABLE IF NOT EXISTS smartbulletin (_id INTEGER PRIMARY KEY AUTOINCREMENT,componentName TEXT,positionX BIGINT,positionY BIGINT,spanX BIGINT,spanY BIGINT,appWidgetId BIGINT,isEnabled BIGINT)";
    private static final String DB_FILE_NAME = "smart_bulletin.db";
    private static final int DB_VERSION = 2;
    private static final String TAG = "SBDBHelper";
    private static Context sContext;

    public SBDBHelper(Context context) {
        super(context, DB_FILE_NAME, (SQLiteDatabase.CursorFactory) null, 2);
        sContext = context;
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        db.execSQL(CREATE_SMARTBULLETIN_TABLE);
        db.execSQL(CREATE_SMARTBULLETIN_NOTI_TABLE);
        db.setTransactionSuccessful();
        db.endTransaction();
        SBLog.i(TAG, "onCreate done");
        sendResetNotify();
    }

    private void sendResetNotify() {
        sContext.getContentResolver().notifyChange(SBContract.SmartBulletin.CONTENT_URI, null);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        SBLog.i(TAG, "onUpgrade: newVersion = " + newVersion + " oldVersion = " + oldVersion);
        if (oldVersion < 2) {
            db.beginTransaction();
            db.execSQL(CREATE_SMARTBULLETIN_NOTI_TABLE);
            db.setTransactionSuccessful();
            db.endTransaction();
            oldVersion = 2;
        }
        if (oldVersion != 2) {
            db.beginTransaction();
            db.execSQL("DROP TABLE IF EXISTS smartbulletin");
            db.execSQL("DROP TABLE IF EXISTS smartbulletin_noti");
            db.setTransactionSuccessful();
            db.endTransaction();
            onCreate(db);
        }
    }
}
