package com.lge.launcher3.allapps;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.SqlArguments;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsDBAdapter {
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "AllAppsDBAdapter";
    private static AllAppsDBAdapter me;
    private static AllAppsDBAdapter meForSwivel;
    private SQLiteDatabase db;
    private SQLiteOpenHelper mOpenHelper;

    private AllAppsDBAdapter() {
    }

    public static AllAppsDBAdapter getInstance(boolean isSwivel) {
        if (isSwivel) {
            if (meForSwivel == null) {
                meForSwivel = new AllAppsDBAdapter();
            }
            return meForSwivel;
        }
        if (me == null) {
            me = new AllAppsDBAdapter();
        }
        return me;
    }

    public static AllAppsDBAdapter getInstance(ContentResolver cr, Uri uri) {
        if (AllAppsDBProvider.isSwivelUri(uri)) {
            if (meForSwivel == null) {
                meForSwivel = new AllAppsDBAdapter();
            }
            return meForSwivel;
        }
        if (me == null) {
            me = new AllAppsDBAdapter();
        }
        return me;
    }

    public void setmSQLiteOpenHelper(SQLiteOpenHelper helper) {
        this.mOpenHelper = helper;
    }

    public void beginTransaction() {
        SQLiteDatabase database;
        if (this.mOpenHelper == null || (database = getDatabase()) == null) {
            return;
        }
        database.beginTransaction();
    }

    public void setTransactionSuccessful(boolean bSuccess) {
        if (bSuccess) {
            getDatabase().setTransactionSuccessful();
        }
    }

    public void endTransaction() {
        SQLiteDatabase database;
        if (this.mOpenHelper == null || (database = getDatabase()) == null) {
            return;
        }
        database.endTransaction();
    }

    public Cursor query(ContentResolver cr, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, String limit) {
        if (this.mOpenHelper == null) {
            return null;
        }
        SqlArguments sqlArguments = new SqlArguments(uri, selection, selectionArgs);
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        sQLiteQueryBuilder.setTables(sqlArguments.table);
        SQLiteDatabase database = getDatabase();
        if (database != null) {
            Cursor cursorQuery = sQLiteQueryBuilder.query(database, projection, sqlArguments.where, sqlArguments.args, null, null, sortOrder, limit);
            if (cursorQuery != null) {
                cursorQuery.setNotificationUri(cr, uri);
            }
            return cursorQuery;
        }
        LGLog.e(LOG_TAG, " db is Null");
        return null;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (this.mOpenHelper == null) {
            return -1;
        }
        SqlArguments sqlArguments = new SqlArguments(uri, selection, selectionArgs);
        return getDatabase().update(sqlArguments.table, values, sqlArguments.where, sqlArguments.args);
    }

    public Uri insert(Uri uri, ContentValues initialValues) {
        if (this.mOpenHelper == null) {
            return null;
        }
        long jInsert = getDatabase().insert(new SqlArguments(uri).table, null, initialValues);
        if (jInsert <= 0) {
            return null;
        }
        return ContentUris.withAppendedId(uri, jInsert);
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (this.mOpenHelper == null) {
            return -1;
        }
        SqlArguments sqlArguments = new SqlArguments(uri, selection, selectionArgs);
        return getDatabase().delete(sqlArguments.table, sqlArguments.where, sqlArguments.args);
    }

    public SQLiteDatabase getDatabase() {
        SQLiteOpenHelper sQLiteOpenHelper;
        SQLiteDatabase sQLiteDatabase = this.db;
        if ((sQLiteDatabase == null || !sQLiteDatabase.isOpen()) && (sQLiteOpenHelper = this.mOpenHelper) != null) {
            this.db = sQLiteOpenHelper.getWritableDatabase();
        }
        return this.db;
    }

    public String getTableName(Uri uri) {
        return new SqlArguments(uri).table;
    }
}
