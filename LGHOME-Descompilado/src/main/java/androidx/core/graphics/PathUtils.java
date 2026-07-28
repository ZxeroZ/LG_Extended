package androidx.core.graphics;

import android.graphics.Path;
import android.graphics.PointF;
import java.util.ArrayList;
import java.util.Collection;

/* JADX INFO: loaded from: classes.dex */
public final class PathUtils {
    public static Collection<PathSegment> flatten(Path path) {
        return flatten(path, 0.5f);
    }

    public static Collection<PathSegment> flatten(final Path path, final float error) {
        float[] fArrApproximate = path.approximate(error);
        int length = fArrApproximate.length / 3;
        ArrayList arrayList = new ArrayList(length);
        for (int i = 1; i < length; i++) {
            int i2 = i * 3;
            int i3 = (i - 1) * 3;
            float f = fArrApproximate[i2];
            float f2 = fArrApproximate[i2 + 1];
            float f3 = fArrApproximate[i2 + 2];
            float f4 = fArrApproximate[i3];
            float f5 = fArrApproximate[i3 + 1];
            float f6 = fArrApproximate[i3 + 2];
            if (f != f4 && (f2 != f5 || f3 != f6)) {
                arrayList.add(new PathSegment(new PointF(f5, f6), f4, new PointF(f2, f3), f));
            }
        }
        return arrayList;
    }

    private PathUtils() {
    }
}
