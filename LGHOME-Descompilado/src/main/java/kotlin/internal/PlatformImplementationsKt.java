package kotlin.internal;

import kotlin.KotlinVersion;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: PlatformImplementations.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000\u001e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0000\n\u0002\b\u0004\u001a \u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\u0005H\u0001\u001a\"\u0010\b\u001a\u0002H\t\"\n\b\u0000\u0010\t\u0018\u0001*\u00020\n2\u0006\u0010\u000b\u001a\u00020\nH\u0083\b¢\u0006\u0002\u0010\f\u001a\b\u0010\r\u001a\u00020\u0005H\u0002\"\u0010\u0010\u0000\u001a\u00020\u00018\u0000X\u0081\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"IMPLEMENTATIONS", "Lkotlin/internal/PlatformImplementations;", "apiVersionIsAtLeast", "", "major", "", "minor", "patch", "castToBaseType", "T", "", "instance", "(Ljava/lang/Object;)Ljava/lang/Object;", "getJavaVersion", "kotlin-stdlib"}, k = 2, mv = {1, 6, 0}, xi = 48)
public final class PlatformImplementationsKt {
    public static final PlatformImplementations IMPLEMENTATIONS;

    /* JADX WARN: Removed duplicated region for block: B:30:0x0137  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00a6 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    static {
        /*
            int r0 = getJavaVersion()
            java.lang.String r1 = "ClassCastException(\"Inst…baseTypeCL\").initCause(e)"
            java.lang.String r2 = ", base type classloader: "
            java.lang.String r3 = "Instance classloader: "
            java.lang.String r4 = "forName(\"kotlin.internal…entations\").newInstance()"
            r5 = 65544(0x10008, float:9.1847E-41)
            if (r0 < r5) goto La1
            java.lang.String r5 = "kotlin.internal.jdk8.JDK8PlatformImplementations"
            java.lang.Class r5 = java.lang.Class.forName(r5)     // Catch: java.lang.ClassNotFoundException -> L59
            java.lang.Object r5 = r5.newInstance()     // Catch: java.lang.ClassNotFoundException -> L59
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r5, r4)     // Catch: java.lang.ClassNotFoundException -> L59
            kotlin.internal.PlatformImplementations r5 = (kotlin.internal.PlatformImplementations) r5     // Catch: java.lang.ClassCastException -> L22 java.lang.ClassNotFoundException -> L59
            goto L13c
        L22:
            r6 = move-exception
            java.lang.Class r5 = r5.getClass()     // Catch: java.lang.ClassNotFoundException -> L59
            java.lang.ClassLoader r5 = r5.getClassLoader()     // Catch: java.lang.ClassNotFoundException -> L59
            java.lang.Class<kotlin.internal.PlatformImplementations> r7 = kotlin.internal.PlatformImplementations.class
            java.lang.ClassLoader r7 = r7.getClassLoader()     // Catch: java.lang.ClassNotFoundException -> L59
            java.lang.ClassCastException r8 = new java.lang.ClassCastException     // Catch: java.lang.ClassNotFoundException -> L59
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch: java.lang.ClassNotFoundException -> L59
            r9.<init>()     // Catch: java.lang.ClassNotFoundException -> L59
            java.lang.StringBuilder r9 = r9.append(r3)     // Catch: java.lang.ClassNotFoundException -> L59
            java.lang.StringBuilder r5 = r9.append(r5)     // Catch: java.lang.ClassNotFoundException -> L59
            java.lang.StringBuilder r5 = r5.append(r2)     // Catch: java.lang.ClassNotFoundException -> L59
            java.lang.StringBuilder r5 = r5.append(r7)     // Catch: java.lang.ClassNotFoundException -> L59
            java.lang.String r5 = r5.toString()     // Catch: java.lang.ClassNotFoundException -> L59
            r8.<init>(r5)     // Catch: java.lang.ClassNotFoundException -> L59
            java.lang.Throwable r6 = (java.lang.Throwable) r6     // Catch: java.lang.ClassNotFoundException -> L59
            java.lang.Throwable r5 = r8.initCause(r6)     // Catch: java.lang.ClassNotFoundException -> L59
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r5, r1)     // Catch: java.lang.ClassNotFoundException -> L59
            throw r5     // Catch: java.lang.ClassNotFoundException -> L59
        L59:
            java.lang.String r5 = "kotlin.internal.JRE8PlatformImplementations"
            java.lang.Class r5 = java.lang.Class.forName(r5)     // Catch: java.lang.ClassNotFoundException -> La1
            java.lang.Object r5 = r5.newInstance()     // Catch: java.lang.ClassNotFoundException -> La1
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r5, r4)     // Catch: java.lang.ClassNotFoundException -> La1
            kotlin.internal.PlatformImplementations r5 = (kotlin.internal.PlatformImplementations) r5     // Catch: java.lang.ClassCastException -> L6a java.lang.ClassNotFoundException -> La1
            goto L13c
        L6a:
            r6 = move-exception
            java.lang.Class r5 = r5.getClass()     // Catch: java.lang.ClassNotFoundException -> La1
            java.lang.ClassLoader r5 = r5.getClassLoader()     // Catch: java.lang.ClassNotFoundException -> La1
            java.lang.Class<kotlin.internal.PlatformImplementations> r7 = kotlin.internal.PlatformImplementations.class
            java.lang.ClassLoader r7 = r7.getClassLoader()     // Catch: java.lang.ClassNotFoundException -> La1
            java.lang.ClassCastException r8 = new java.lang.ClassCastException     // Catch: java.lang.ClassNotFoundException -> La1
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch: java.lang.ClassNotFoundException -> La1
            r9.<init>()     // Catch: java.lang.ClassNotFoundException -> La1
            java.lang.StringBuilder r9 = r9.append(r3)     // Catch: java.lang.ClassNotFoundException -> La1
            java.lang.StringBuilder r5 = r9.append(r5)     // Catch: java.lang.ClassNotFoundException -> La1
            java.lang.StringBuilder r5 = r5.append(r2)     // Catch: java.lang.ClassNotFoundException -> La1
            java.lang.StringBuilder r5 = r5.append(r7)     // Catch: java.lang.ClassNotFoundException -> La1
            java.lang.String r5 = r5.toString()     // Catch: java.lang.ClassNotFoundException -> La1
            r8.<init>(r5)     // Catch: java.lang.ClassNotFoundException -> La1
            java.lang.Throwable r6 = (java.lang.Throwable) r6     // Catch: java.lang.ClassNotFoundException -> La1
            java.lang.Throwable r5 = r8.initCause(r6)     // Catch: java.lang.ClassNotFoundException -> La1
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r5, r1)     // Catch: java.lang.ClassNotFoundException -> La1
            throw r5     // Catch: java.lang.ClassNotFoundException -> La1
        La1:
            r5 = 65543(0x10007, float:9.1845E-41)
            if (r0 < r5) goto L137
            java.lang.String r0 = "kotlin.internal.jdk7.JDK7PlatformImplementations"
            java.lang.Class r0 = java.lang.Class.forName(r0)     // Catch: java.lang.ClassNotFoundException -> Lef
            java.lang.Object r0 = r0.newInstance()     // Catch: java.lang.ClassNotFoundException -> Lef
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r0, r4)     // Catch: java.lang.ClassNotFoundException -> Lef
            r5 = r0
            kotlin.internal.PlatformImplementations r5 = (kotlin.internal.PlatformImplementations) r5     // Catch: java.lang.ClassCastException -> Lb8 java.lang.ClassNotFoundException -> Lef
            goto L13c
        Lb8:
            r5 = move-exception
            java.lang.Class r0 = r0.getClass()     // Catch: java.lang.ClassNotFoundException -> Lef
            java.lang.ClassLoader r0 = r0.getClassLoader()     // Catch: java.lang.ClassNotFoundException -> Lef
            java.lang.Class<kotlin.internal.PlatformImplementations> r6 = kotlin.internal.PlatformImplementations.class
            java.lang.ClassLoader r6 = r6.getClassLoader()     // Catch: java.lang.ClassNotFoundException -> Lef
            java.lang.ClassCastException r7 = new java.lang.ClassCastException     // Catch: java.lang.ClassNotFoundException -> Lef
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch: java.lang.ClassNotFoundException -> Lef
            r8.<init>()     // Catch: java.lang.ClassNotFoundException -> Lef
            java.lang.StringBuilder r8 = r8.append(r3)     // Catch: java.lang.ClassNotFoundException -> Lef
            java.lang.StringBuilder r0 = r8.append(r0)     // Catch: java.lang.ClassNotFoundException -> Lef
            java.lang.StringBuilder r0 = r0.append(r2)     // Catch: java.lang.ClassNotFoundException -> Lef
            java.lang.StringBuilder r0 = r0.append(r6)     // Catch: java.lang.ClassNotFoundException -> Lef
            java.lang.String r0 = r0.toString()     // Catch: java.lang.ClassNotFoundException -> Lef
            r7.<init>(r0)     // Catch: java.lang.ClassNotFoundException -> Lef
            java.lang.Throwable r5 = (java.lang.Throwable) r5     // Catch: java.lang.ClassNotFoundException -> Lef
            java.lang.Throwable r0 = r7.initCause(r5)     // Catch: java.lang.ClassNotFoundException -> Lef
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r0, r1)     // Catch: java.lang.ClassNotFoundException -> Lef
            throw r0     // Catch: java.lang.ClassNotFoundException -> Lef
        Lef:
            java.lang.String r0 = "kotlin.internal.JRE7PlatformImplementations"
            java.lang.Class r0 = java.lang.Class.forName(r0)     // Catch: java.lang.ClassNotFoundException -> L137
            java.lang.Object r0 = r0.newInstance()     // Catch: java.lang.ClassNotFoundException -> L137
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r0, r4)     // Catch: java.lang.ClassNotFoundException -> L137
            r5 = r0
            kotlin.internal.PlatformImplementations r5 = (kotlin.internal.PlatformImplementations) r5     // Catch: java.lang.ClassCastException -> L100 java.lang.ClassNotFoundException -> L137
            goto L13c
        L100:
            r4 = move-exception
            java.lang.Class r0 = r0.getClass()     // Catch: java.lang.ClassNotFoundException -> L137
            java.lang.ClassLoader r0 = r0.getClassLoader()     // Catch: java.lang.ClassNotFoundException -> L137
            java.lang.Class<kotlin.internal.PlatformImplementations> r5 = kotlin.internal.PlatformImplementations.class
            java.lang.ClassLoader r5 = r5.getClassLoader()     // Catch: java.lang.ClassNotFoundException -> L137
            java.lang.ClassCastException r6 = new java.lang.ClassCastException     // Catch: java.lang.ClassNotFoundException -> L137
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch: java.lang.ClassNotFoundException -> L137
            r7.<init>()     // Catch: java.lang.ClassNotFoundException -> L137
            java.lang.StringBuilder r3 = r7.append(r3)     // Catch: java.lang.ClassNotFoundException -> L137
            java.lang.StringBuilder r0 = r3.append(r0)     // Catch: java.lang.ClassNotFoundException -> L137
            java.lang.StringBuilder r0 = r0.append(r2)     // Catch: java.lang.ClassNotFoundException -> L137
            java.lang.StringBuilder r0 = r0.append(r5)     // Catch: java.lang.ClassNotFoundException -> L137
            java.lang.String r0 = r0.toString()     // Catch: java.lang.ClassNotFoundException -> L137
            r6.<init>(r0)     // Catch: java.lang.ClassNotFoundException -> L137
            java.lang.Throwable r4 = (java.lang.Throwable) r4     // Catch: java.lang.ClassNotFoundException -> L137
            java.lang.Throwable r0 = r6.initCause(r4)     // Catch: java.lang.ClassNotFoundException -> L137
            kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r0, r1)     // Catch: java.lang.ClassNotFoundException -> L137
            throw r0     // Catch: java.lang.ClassNotFoundException -> L137
        L137:
            kotlin.internal.PlatformImplementations r5 = new kotlin.internal.PlatformImplementations
            r5.<init>()
        L13c:
            kotlin.internal.PlatformImplementationsKt.IMPLEMENTATIONS = r5
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.internal.PlatformImplementationsKt.<clinit>():void");
    }

    private static final /* synthetic */ <T> T castToBaseType(Object obj) throws Throwable {
        try {
            Intrinsics.reifiedOperationMarker(1, "T");
            return (T) obj;
        } catch (ClassCastException e) {
            ClassLoader classLoader = obj.getClass().getClassLoader();
            Intrinsics.reifiedOperationMarker(4, "T");
            Throwable thInitCause = new ClassCastException("Instance classloader: " + classLoader + ", base type classloader: " + Object.class.getClassLoader()).initCause(e);
            Intrinsics.checkNotNullExpressionValue(thInitCause, "ClassCastException(\"Inst…baseTypeCL\").initCause(e)");
            throw thInitCause;
        }
    }

    /* JADX DEBUG: Class process forced to load method for inline: kotlin.text.StringsKt__StringsKt.indexOf$default(java.lang.CharSequence, char, int, boolean, int, java.lang.Object):int */
    private static final int getJavaVersion() {
        String property = System.getProperty("java.specification.version");
        if (property == null) {
            return 65542;
        }
        String str = property;
        int iIndexOf$default = StringsKt.indexOf$default((CharSequence) str, '.', 0, false, 6, (Object) null);
        if (iIndexOf$default < 0) {
            try {
                return Integer.parseInt(property) * 65536;
            } catch (NumberFormatException unused) {
                return 65542;
            }
        }
        int i = iIndexOf$default + 1;
        int iIndexOf$default2 = StringsKt.indexOf$default((CharSequence) str, '.', i, false, 4, (Object) null);
        if (iIndexOf$default2 < 0) {
            iIndexOf$default2 = property.length();
        }
        String strSubstring = property.substring(0, iIndexOf$default);
        Intrinsics.checkNotNullExpressionValue(strSubstring, "this as java.lang.String…ing(startIndex, endIndex)");
        String strSubstring2 = property.substring(i, iIndexOf$default2);
        Intrinsics.checkNotNullExpressionValue(strSubstring2, "this as java.lang.String…ing(startIndex, endIndex)");
        try {
            return (Integer.parseInt(strSubstring) * 65536) + Integer.parseInt(strSubstring2);
        } catch (NumberFormatException unused2) {
            return 65542;
        }
    }

    public static final boolean apiVersionIsAtLeast(int i, int i2, int i3) {
        return KotlinVersion.CURRENT.isAtLeast(i, i2, i3);
    }
}
