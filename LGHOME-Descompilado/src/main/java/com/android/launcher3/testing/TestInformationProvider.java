package com.android.launcher3.testing;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import com.android.launcher3.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class TestInformationProvider extends ContentProvider {
    @Override // android.content.ContentProvider
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return null;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public Bundle call(String method, String arg, Bundle extras) {
        if (!Utilities.IS_RUNNING_IN_TEST_HARNESS) {
            return null;
        }
        TestInformationHandler testInformationHandlerNewInstance = TestInformationHandler.newInstance(getContext());
        testInformationHandlerNewInstance.init(getContext());
        return testInformationHandlerNewInstance.call(method);
    }
}
