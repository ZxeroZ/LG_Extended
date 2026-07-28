package com.lge.launcher3.util;

import android.content.ContentUris;
import android.net.Uri;

/* JADX INFO: loaded from: classes.dex */
public class SqlArguments {
    public final String[] args;
    public final String table;
    public final String where;

    public SqlArguments(Uri url, String where, String[] args) {
        if (url.getPathSegments().size() == 1) {
            this.table = url.getPathSegments().get(0);
            this.where = where;
            this.args = args;
            return;
        }
        if (url.getPathSegments().size() != 2) {
            throw new IllegalArgumentException("Invalid URI: " + url);
        }
        if (!android.text.TextUtils.isEmpty(where)) {
            throw new UnsupportedOperationException("WHERE clause not supported: " + url);
        }
        this.table = url.getPathSegments().get(0);
        this.where = "_id=" + ContentUris.parseId(url);
        this.args = null;
    }

    public SqlArguments(Uri url) {
        if (url.getPathSegments().size() == 1) {
            this.table = url.getPathSegments().get(0);
            this.where = null;
            this.args = null;
        } else {
            throw new IllegalArgumentException("Invalid URI: " + url);
        }
    }
}
