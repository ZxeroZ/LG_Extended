package androidx.core.util;

/* JADX INFO: loaded from: classes.dex */
public class DebugUtils {
    public static void buildShortClassTag(Object cls, StringBuilder out) {
        int iLastIndexOf;
        if (cls == null) {
            out.append("null");
            return;
        }
        String simpleName = cls.getClass().getSimpleName();
        if ((simpleName == null || simpleName.length() <= 0) && (iLastIndexOf = (simpleName = cls.getClass().getName()).lastIndexOf(46)) > 0) {
            simpleName = simpleName.substring(iLastIndexOf + 1);
        }
        out.append(simpleName);
        out.append('{');
        out.append(Integer.toHexString(System.identityHashCode(cls)));
    }

    private DebugUtils() {
    }
}
