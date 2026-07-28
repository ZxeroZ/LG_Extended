package androidx.core.math;

/* JADX INFO: loaded from: classes.dex */
public class MathUtils {
    public static double clamp(double value, double min, double max) {
        return value < min ? min : value > max ? max : value;
    }

    public static float clamp(float value, float min, float max) {
        return value < min ? min : value > max ? max : value;
    }

    public static int clamp(int value, int min, int max) {
        return value < min ? min : value > max ? max : value;
    }

    public static long clamp(long value, long min, long max) {
        return value < min ? min : value > max ? max : value;
    }

    private MathUtils() {
    }
}
