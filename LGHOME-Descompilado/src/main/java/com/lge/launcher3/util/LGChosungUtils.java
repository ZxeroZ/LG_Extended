package com.lge.launcher3.util;

/* JADX INFO: loaded from: classes.dex */
public class LGChosungUtils {
    private static final int HANGUL_BASE_UNIT = 588;
    private static final int HANGUL_BEGIN_UNICODE = 44032;
    private static final int HANGUL_END_UNICODE = 55203;
    private static final char[] HANGUL_INITIAL_SOUND = {12593, 12594, 12596, 12599, 12600, 12601, 12609, 12610, 12611, 12613, 12614, 12615, 12616, 12617, 12618, 12619, 12620, 12621, 12622};

    private static boolean isHangul(char c) {
        return HANGUL_BEGIN_UNICODE <= c && c <= HANGUL_END_UNICODE;
    }

    private static boolean isInitialSound(char search) {
        for (char c : HANGUL_INITIAL_SOUND) {
            if (c == search) {
                return true;
            }
        }
        return false;
    }

    private static char getInitialSound(char c) {
        return HANGUL_INITIAL_SOUND[(c - HANGUL_BEGIN_UNICODE) / HANGUL_BASE_UNIT];
    }

    public static int getHangulInitialSound(String value, String search) {
        if (value != null && search != null) {
            int length = value.length() - search.length();
            int length2 = search.length();
            if (length < 0) {
                return -1;
            }
            for (int i = 0; i <= length; i++) {
                int i2 = 0;
                while (i2 < length2) {
                    char cCharAt = search.charAt(i2);
                    char cCharAt2 = value.charAt(i + i2);
                    if (isInitialSound(cCharAt) && isHangul(cCharAt2)) {
                        if (getInitialSound(cCharAt2) != cCharAt) {
                            break;
                        }
                        i2++;
                    } else {
                        if (cCharAt2 != cCharAt) {
                            break;
                        }
                        i2++;
                    }
                }
                if (i2 == length2) {
                    return i;
                }
            }
        }
        return -1;
    }
}
