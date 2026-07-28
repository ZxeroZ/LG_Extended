package com.android.launcher3.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

/* JADX INFO: loaded from: classes.dex */
public class NoLocaleSqliteContext extends ContextWrapper {
    public NoLocaleSqliteContext(Context context) {
        super(context);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        return super.openOrCreateDatabase(name, mode | 16, factory, errorHandler);
    }
}
