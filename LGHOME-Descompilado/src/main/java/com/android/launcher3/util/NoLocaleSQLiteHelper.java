package com.android.launcher3.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

/* JADX INFO: loaded from: classes.dex */
public abstract class NoLocaleSQLiteHelper extends SQLiteOpenHelper {
    private static final boolean ATLEAST_P;

    static {
        ATLEAST_P = Build.VERSION.SDK_INT >= 28;
    }

    /* JADX WARN: Illegal instructions before constructor call */
    public NoLocaleSQLiteHelper(Context context, String name, int version) {
        boolean z = ATLEAST_P;
        super(z ? context : new NoLocalContext(context), name, (SQLiteDatabase.CursorFactory) null, version);
        if (z) {
            setOpenParams(new SQLiteDatabase.OpenParams.Builder().addOpenFlags(16).build());
        }
    }

    private static class NoLocalContext extends ContextWrapper {
        public NoLocalContext(Context base) {
            super(base);
        }

        @Override // android.content.ContextWrapper, android.content.Context
        public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
            return super.openOrCreateDatabase(name, mode | 16, factory, errorHandler);
        }
    }
}
