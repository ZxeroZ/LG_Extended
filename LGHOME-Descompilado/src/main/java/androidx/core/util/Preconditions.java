package androidx.core.util;

import android.text.TextUtils;
import java.util.Locale;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class Preconditions {
    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgument(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    public static <T extends CharSequence> T checkStringNotEmpty(final T string) {
        if (TextUtils.isEmpty(string)) {
            throw new IllegalArgumentException();
        }
        return string;
    }

    public static <T extends CharSequence> T checkStringNotEmpty(final T string, final Object errorMessage) {
        if (TextUtils.isEmpty(string)) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
        return string;
    }

    public static <T extends CharSequence> T checkStringNotEmpty(final T string, final String messageTemplate, final Object... messageArgs) {
        if (TextUtils.isEmpty(string)) {
            throw new IllegalArgumentException(String.format(messageTemplate, messageArgs));
        }
        return string;
    }

    public static <T> T checkNotNull(T reference) {
        Objects.requireNonNull(reference);
        return reference;
    }

    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if (reference != null) {
            return reference;
        }
        throw new NullPointerException(String.valueOf(errorMessage));
    }

    public static void checkState(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    public static void checkState(final boolean expression) {
        checkState(expression, null);
    }

    public static int checkFlagsArgument(final int requestedFlags, final int allowedFlags) {
        if ((requestedFlags & allowedFlags) == requestedFlags) {
            return requestedFlags;
        }
        throw new IllegalArgumentException("Requested flags 0x" + Integer.toHexString(requestedFlags) + ", but only 0x" + Integer.toHexString(allowedFlags) + " are allowed");
    }

    public static int checkArgumentNonnegative(final int value, String errorMessage) {
        if (value >= 0) {
            return value;
        }
        throw new IllegalArgumentException(errorMessage);
    }

    public static int checkArgumentNonnegative(final int value) {
        if (value >= 0) {
            return value;
        }
        throw new IllegalArgumentException();
    }

    public static int checkArgumentInRange(int value, int lower, int upper, String valueName) {
        if (value < lower) {
            throw new IllegalArgumentException(String.format(Locale.US, "%s is out of range of [%d, %d] (too low)", valueName, Integer.valueOf(lower), Integer.valueOf(upper)));
        }
        if (value <= upper) {
            return value;
        }
        throw new IllegalArgumentException(String.format(Locale.US, "%s is out of range of [%d, %d] (too high)", valueName, Integer.valueOf(lower), Integer.valueOf(upper)));
    }

    private Preconditions() {
    }
}
