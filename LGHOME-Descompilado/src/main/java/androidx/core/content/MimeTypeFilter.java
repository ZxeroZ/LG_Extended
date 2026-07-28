package androidx.core.content;

import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public final class MimeTypeFilter {
    private MimeTypeFilter() {
    }

    private static boolean mimeTypeAgainstFilter(String[] mimeTypeParts, String[] filterParts) {
        if (filterParts.length != 2) {
            throw new IllegalArgumentException("Ill-formatted MIME type filter. Must be type/subtype.");
        }
        if (filterParts[0].isEmpty() || filterParts[1].isEmpty()) {
            throw new IllegalArgumentException("Ill-formatted MIME type filter. Type or subtype empty.");
        }
        if (mimeTypeParts.length != 2) {
            return false;
        }
        if ("*".equals(filterParts[0]) || filterParts[0].equals(mimeTypeParts[0])) {
            return "*".equals(filterParts[1]) || filterParts[1].equals(mimeTypeParts[1]);
        }
        return false;
    }

    public static boolean matches(String mimeType, String filter) {
        if (mimeType == null) {
            return false;
        }
        return mimeTypeAgainstFilter(mimeType.split("/"), filter.split("/"));
    }

    public static String matches(String mimeType, String[] filters) {
        if (mimeType == null) {
            return null;
        }
        String[] strArrSplit = mimeType.split("/");
        for (String str : filters) {
            if (mimeTypeAgainstFilter(strArrSplit, str.split("/"))) {
                return str;
            }
        }
        return null;
    }

    public static String matches(String[] mimeTypes, String filter) {
        if (mimeTypes == null) {
            return null;
        }
        String[] strArrSplit = filter.split("/");
        for (String str : mimeTypes) {
            if (mimeTypeAgainstFilter(str.split("/"), strArrSplit)) {
                return str;
            }
        }
        return null;
    }

    public static String[] matchesMany(String[] mimeTypes, String filter) {
        if (mimeTypes == null) {
            return new String[0];
        }
        ArrayList arrayList = new ArrayList();
        String[] strArrSplit = filter.split("/");
        for (String str : mimeTypes) {
            if (mimeTypeAgainstFilter(str.split("/"), strArrSplit)) {
                arrayList.add(str);
            }
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }
}
