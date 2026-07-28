package com.lge.launcher3.smartbulletin.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.lge.launcher3.smartbulletin.log.SBLog;
import com.lge.launcher3.smartbulletin.provider.SBContract;

/* JADX INFO: loaded from: classes.dex */
public class SBProvider extends ContentProvider {
    private static final int ID_SMARTBULLETIN = 2;
    private static final int ID_SMARTBULLETIN_NOTI = 3;
    private static final String TAG = "SBProvider";
    public static final UriMatcher uriMatcher;
    SBDBHelper dbOpenHelper = null;

    static {
        UriMatcher uriMatcher2 = new UriMatcher(-1);
        uriMatcher = uriMatcher2;
        uriMatcher2.addURI(SBContract.AUTHORITY, SBContract.SmartBulletin.TABLE_NAME, 2);
        uriMatcher2.addURI(SBContract.AUTHORITY, SBContract.SmartBulletin.NOTI_TABLE, 3);
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        SBLog.v(TAG, "onCreate()");
        SBDBHelper sBDBHelper = new SBDBHelper(getContext());
        this.dbOpenHelper = sBDBHelper;
        return sBDBHelper != null;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues values) {
        long jInsert;
        SQLiteDatabase writableDatabase = this.dbOpenHelper.getWritableDatabase();
        int iMatch = uriMatcher.match(uri);
        if (iMatch == 2) {
            jInsert = writableDatabase.insert(SBContract.SmartBulletin.TABLE_NAME, null, values);
        } else if (iMatch == 3) {
            jInsert = writableDatabase.insert(SBContract.SmartBulletin.NOTI_TABLE, null, values);
        } else {
            SBLog.e(TAG, "insert uri error: uri = " + uri);
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if (jInsert > 0) {
            SBLog.i(TAG, "notify insert: uri = " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(uri, jInsert);
        }
        throw new SQLException("Failed to insert row into: " + uri);
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int iUpdate;
        SQLiteDatabase writableDatabase = this.dbOpenHelper.getWritableDatabase();
        int iMatch = uriMatcher.match(uri);
        if (iMatch == 2) {
            iUpdate = writableDatabase.update(SBContract.SmartBulletin.TABLE_NAME, values, selection, selectionArgs);
        } else if (iMatch == 3) {
            iUpdate = writableDatabase.update(SBContract.SmartBulletin.NOTI_TABLE, values, selection, selectionArgs);
        } else {
            SBLog.e(TAG, "update uri error: uri = " + uri);
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if (iUpdate != 0) {
            SBLog.i(TAG, "notify update: uri = " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return iUpdate;
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int iDelete;
        SQLiteDatabase writableDatabase = this.dbOpenHelper.getWritableDatabase();
        int iMatch = uriMatcher.match(uri);
        if (iMatch == 2) {
            iDelete = writableDatabase.delete(SBContract.SmartBulletin.TABLE_NAME, selection, selectionArgs);
        } else if (iMatch == 3) {
            iDelete = writableDatabase.delete(SBContract.SmartBulletin.NOTI_TABLE, selection, selectionArgs);
        } else {
            SBLog.e(TAG, "delete uri error: uri = " + uri);
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        if (iDelete != 0) {
            SBLog.i(TAG, "notify delete: uri = " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return iDelete;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase readableDatabase = this.dbOpenHelper.getReadableDatabase();
        int iMatch = uriMatcher.match(uri);
        if (iMatch == 2) {
            return readableDatabase.query(SBContract.SmartBulletin.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        }
        if (iMatch == 3) {
            return readableDatabase.query(SBContract.SmartBulletin.NOTI_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
        }
        SBLog.e(TAG, "query uri error: uri = " + uri);
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        int iMatch = uriMatcher.match(uri);
        if (iMatch == 2) {
            return "vnd.android.cursor.dir/com.lge.launcher3.smartbulletin.smartbulletin";
        }
        if (iMatch == 3) {
            return "vnd.android.cursor.dir/com.lge.launcher3.smartbulletin.smartbulletin_noti";
        }
        SBLog.e(TAG, "getType uri error: uri = " + uri);
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }
}
