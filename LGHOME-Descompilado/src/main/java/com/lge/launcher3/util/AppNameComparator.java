package com.lge.launcher3.util;

import java.text.Collator;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class AppNameComparator {
    private static final int CHAR_TYPE_ENGLISH = 150;
    private static final int CHAR_TYPE_JAPANESE_HANJA_CJK = 140;
    private static final int CHAR_TYPE_JAPANESE_HIRAGANA_A = 40;
    private static final int CHAR_TYPE_JAPANESE_HIRAGANA_HA = 90;
    private static final int CHAR_TYPE_JAPANESE_HIRAGANA_KA = 50;
    private static final int CHAR_TYPE_JAPANESE_HIRAGANA_MA = 100;
    private static final int CHAR_TYPE_JAPANESE_HIRAGANA_NA = 80;
    private static final int CHAR_TYPE_JAPANESE_HIRAGANA_RA = 120;
    private static final int CHAR_TYPE_JAPANESE_HIRAGANA_SA = 60;
    private static final int CHAR_TYPE_JAPANESE_HIRAGANA_TA = 70;
    private static final int CHAR_TYPE_JAPANESE_HIRAGANA_WA_WO_N = 130;
    private static final int CHAR_TYPE_JAPANESE_HIRAGANA_YA = 110;
    private static final int CHAR_TYPE_KOREAN = 30;
    private static final int CHAR_TYPE_NUMBER = 20;
    private static final int CHAR_TYPE_OTHER = 160;
    private static final int CHAR_TYPE_SYMBOL = 10;
    private static Collator sCollator = Collator.getInstance();

    private static final int setCustomType(int charType) {
        if (charType == 30) {
            return CHAR_TYPE_OTHER;
        }
        if (charType == 20) {
            return 151;
        }
        if (charType == 10) {
            return 152;
        }
        return charType;
    }

    public static final int compare(String str1, String str2) {
        if (Locale.KOREA.equals(Locale.getDefault())) {
            int charType = getCharType(str1);
            int charType2 = getCharType(str2);
            if (40 <= charType && charType <= CHAR_TYPE_JAPANESE_HANJA_CJK) {
                charType = CHAR_TYPE_OTHER;
            }
            if (40 <= charType2 && charType2 <= CHAR_TYPE_JAPANESE_HANJA_CJK) {
                charType2 = CHAR_TYPE_OTHER;
            }
            if (charType < charType2) {
                return -1;
            }
            if (charType > charType2) {
                return 1;
            }
        } else if (Locale.JAPAN.equals(Locale.getDefault())) {
            int charType3 = getCharType(str1);
            int charType4 = getCharType(str2);
            int customType = setCustomType(charType3);
            int customType2 = setCustomType(charType4);
            if (customType < customType2) {
                return -1;
            }
            if (customType > customType2) {
                return 1;
            }
        }
        return sCollator.compare(str1, str2);
    }

    private static final int getCharType(String str) {
        if (str == null || str.length() <= 0) {
            return CHAR_TYPE_OTHER;
        }
        int iCodePointAt = str.codePointAt(0);
        if (iCodePointAt >= 48 && iCodePointAt <= 57) {
            return 20;
        }
        if (iCodePointAt >= 65 && iCodePointAt <= 90) {
            return 150;
        }
        if (iCodePointAt >= 97 && iCodePointAt <= 122) {
            return 150;
        }
        if (iCodePointAt < 127) {
            return 10;
        }
        if (iCodePointAt >= 44032 && iCodePointAt <= 55203) {
            return 30;
        }
        if (iCodePointAt >= 12593 && iCodePointAt <= 12686) {
            return 30;
        }
        if ((iCodePointAt >= 12353 && iCodePointAt < 12363) || iCodePointAt == 3094) {
            return 40;
        }
        if (iCodePointAt >= 12449 && iCodePointAt < 12459) {
            return 40;
        }
        if (iCodePointAt >= 65393 && iCodePointAt < 65398) {
            return 40;
        }
        if (iCodePointAt >= 65383 && iCodePointAt < 65388) {
            return 40;
        }
        if ((iCodePointAt >= 12363 && iCodePointAt < 12373) || iCodePointAt == 12437 || iCodePointAt == 12438) {
            return 50;
        }
        if ((iCodePointAt >= 12459 && iCodePointAt < 12469) || iCodePointAt == 12533 || iCodePointAt == 12534) {
            return 50;
        }
        if (iCodePointAt >= 65398 && iCodePointAt < 65403) {
            return 50;
        }
        if (iCodePointAt >= 12373 && iCodePointAt < 12383) {
            return 60;
        }
        if (iCodePointAt >= 12469 && iCodePointAt < 12479) {
            return 60;
        }
        if (iCodePointAt >= 65403 && iCodePointAt < 65408) {
            return 60;
        }
        if (iCodePointAt >= 12383 && iCodePointAt < 12394) {
            return 70;
        }
        if (iCodePointAt >= 12479 && iCodePointAt < 12490) {
            return 70;
        }
        if ((iCodePointAt >= 65408 && iCodePointAt < 65413) || iCodePointAt == 65391) {
            return 70;
        }
        if (iCodePointAt >= 12394 && iCodePointAt < 12399) {
            return 80;
        }
        if (iCodePointAt >= 12490 && iCodePointAt < 12495) {
            return 80;
        }
        if (iCodePointAt >= 65413 && iCodePointAt < 65418) {
            return 80;
        }
        if ((iCodePointAt >= 12399 && iCodePointAt < 12414) || ((iCodePointAt >= 12495 && iCodePointAt < 12510) || (iCodePointAt >= 65418 && iCodePointAt < 65423))) {
            return 90;
        }
        if (iCodePointAt >= 12414 && iCodePointAt < 12419) {
            return 100;
        }
        if (iCodePointAt >= 12510 && iCodePointAt < 12515) {
            return 100;
        }
        if (iCodePointAt >= 65423 && iCodePointAt < 65428) {
            return 100;
        }
        if (iCodePointAt >= 12419 && iCodePointAt < 12425) {
            return 110;
        }
        if (iCodePointAt >= 12515 && iCodePointAt < 12521) {
            return 110;
        }
        if (iCodePointAt >= 65428 && iCodePointAt < 65431) {
            return 110;
        }
        if (iCodePointAt >= 12425 && iCodePointAt < 12430) {
            return 120;
        }
        if (iCodePointAt >= 12521 && iCodePointAt < 12526) {
            return 120;
        }
        if (iCodePointAt < 65431 || iCodePointAt >= 65436) {
            return (iCodePointAt < 12430 || iCodePointAt >= 12435) ? ((iCodePointAt >= 12526 && iCodePointAt < 12532) || iCodePointAt == 65382 || iCodePointAt == 65436 || iCodePointAt == 65437) ? CHAR_TYPE_JAPANESE_HIRAGANA_WA_WO_N : (iCodePointAt < 13056 || iCodePointAt > 13311) ? (iCodePointAt < 13312 || iCodePointAt > 19893) ? (iCodePointAt < 19968 || iCodePointAt > 40959) ? CHAR_TYPE_OTHER : CHAR_TYPE_JAPANESE_HANJA_CJK : CHAR_TYPE_JAPANESE_HANJA_CJK : CHAR_TYPE_JAPANESE_HANJA_CJK : CHAR_TYPE_JAPANESE_HIRAGANA_WA_WO_N;
        }
        return 120;
    }
}
