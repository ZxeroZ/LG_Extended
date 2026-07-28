package com.lge.launcher3.backuprestore;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;

/* JADX INFO: loaded from: classes.dex */
public class BackupSizeUpdateProvider extends ContentProvider {
    private static final String TAG = "BackupSizeUpdateProvider";

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return null;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        return false;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        BackupRestoreImpl backupRestoreImpl = new BackupRestoreImpl(getContext().getApplicationContext());
        boolean zIsEnableBackupRestore = PackageUtils.isEnableBackupRestore(getContext());
        LGLog.i(TAG, "isEnableBackup is " + zIsEnableBackupRestore);
        if (!zIsEnableBackupRestore) {
            return toKiloBytes(0L);
        }
        return toKiloBytes(backupRestoreImpl.getDatabaseSize());
    }

    private int toKiloBytes(long bytes) {
        int iCeil = (int) Math.ceil(bytes / 1024.0f);
        LGLog.i(TAG, "LGHome5's backup size = " + iCeil);
        return iCeil;
    }
}
