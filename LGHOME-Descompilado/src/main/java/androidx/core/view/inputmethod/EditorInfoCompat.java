package androidx.core.view.inputmethod;

import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import androidx.core.util.Preconditions;
import com.android.launcher3.AbstractFloatingView;

/* JADX INFO: loaded from: classes.dex */
public final class EditorInfoCompat {
    private static final String CONTENT_MIME_TYPES_INTEROP_KEY = "android.support.v13.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES";
    private static final String CONTENT_MIME_TYPES_KEY = "androidx.core.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES";
    private static final String CONTENT_SELECTION_END_KEY = "androidx.core.view.inputmethod.EditorInfoCompat.CONTENT_SELECTION_END";
    private static final String CONTENT_SELECTION_HEAD_KEY = "androidx.core.view.inputmethod.EditorInfoCompat.CONTENT_SELECTION_HEAD";
    private static final String CONTENT_SURROUNDING_TEXT_KEY = "androidx.core.view.inputmethod.EditorInfoCompat.CONTENT_SURROUNDING_TEXT";
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    public static final int IME_FLAG_FORCE_ASCII = Integer.MIN_VALUE;
    public static final int IME_FLAG_NO_PERSONALIZED_LEARNING = 16777216;
    static final int MAX_INITIAL_SELECTION_LENGTH = 1024;
    static final int MEMORY_EFFICIENT_TEXT_LENGTH = 2048;

    private static boolean isPasswordInputType(int inputType) {
        int i = inputType & AbstractFloatingView.TYPE_ALL;
        return i == 129 || i == 225 || i == 18;
    }

    public static void setContentMimeTypes(EditorInfo editorInfo, String[] contentMimeTypes) {
        if (Build.VERSION.SDK_INT >= 25) {
            editorInfo.contentMimeTypes = contentMimeTypes;
            return;
        }
        if (editorInfo.extras == null) {
            editorInfo.extras = new Bundle();
        }
        editorInfo.extras.putStringArray(CONTENT_MIME_TYPES_KEY, contentMimeTypes);
        editorInfo.extras.putStringArray(CONTENT_MIME_TYPES_INTEROP_KEY, contentMimeTypes);
    }

    public static String[] getContentMimeTypes(EditorInfo editorInfo) {
        if (Build.VERSION.SDK_INT >= 25) {
            String[] strArr = editorInfo.contentMimeTypes;
            return strArr != null ? strArr : EMPTY_STRING_ARRAY;
        }
        if (editorInfo.extras == null) {
            return EMPTY_STRING_ARRAY;
        }
        String[] stringArray = editorInfo.extras.getStringArray(CONTENT_MIME_TYPES_KEY);
        if (stringArray == null) {
            stringArray = editorInfo.extras.getStringArray(CONTENT_MIME_TYPES_INTEROP_KEY);
        }
        return stringArray != null ? stringArray : EMPTY_STRING_ARRAY;
    }

    public static void setInitialSurroundingText(EditorInfo editorInfo, CharSequence sourceText) {
        if (Build.VERSION.SDK_INT >= 30) {
            Impl30.setInitialSurroundingSubText(editorInfo, sourceText, 0);
        } else {
            setInitialSurroundingSubText(editorInfo, sourceText, 0);
        }
    }

    public static void setInitialSurroundingSubText(EditorInfo editorInfo, CharSequence subText, int subTextStart) {
        int i;
        int i2;
        Preconditions.checkNotNull(subText);
        if (Build.VERSION.SDK_INT >= 30) {
            Impl30.setInitialSurroundingSubText(editorInfo, subText, subTextStart);
            return;
        }
        if (editorInfo.initialSelStart > editorInfo.initialSelEnd) {
            i = editorInfo.initialSelEnd;
        } else {
            i = editorInfo.initialSelStart;
        }
        int i3 = i - subTextStart;
        if (editorInfo.initialSelStart > editorInfo.initialSelEnd) {
            i2 = editorInfo.initialSelStart;
        } else {
            i2 = editorInfo.initialSelEnd;
        }
        int i4 = i2 - subTextStart;
        int length = subText.length();
        if (subTextStart < 0 || i3 < 0 || i4 > length) {
            setSurroundingText(editorInfo, null, 0, 0);
            return;
        }
        if (isPasswordInputType(editorInfo.inputType)) {
            setSurroundingText(editorInfo, null, 0, 0);
        } else if (length <= 2048) {
            setSurroundingText(editorInfo, subText, i3, i4);
        } else {
            trimLongSurroundingText(editorInfo, subText, i3, i4);
        }
    }

    private static void trimLongSurroundingText(EditorInfo editorInfo, CharSequence subText, int selStart, int selEnd) {
        CharSequence charSequenceSubSequence;
        int i = selEnd - selStart;
        int i2 = i > 1024 ? 0 : i;
        int i3 = 2048 - i2;
        int iMin = Math.min(subText.length() - selEnd, i3 - Math.min(selStart, (int) (((double) i3) * 0.8d)));
        int iMin2 = Math.min(selStart, i3 - iMin);
        int i4 = selStart - iMin2;
        if (isCutOnSurrogate(subText, i4, 0)) {
            i4++;
            iMin2--;
        }
        if (isCutOnSurrogate(subText, (selEnd + iMin) - 1, 1)) {
            iMin--;
        }
        int i5 = iMin2 + i2 + iMin;
        if (i2 != i) {
            charSequenceSubSequence = TextUtils.concat(subText.subSequence(i4, i4 + iMin2), subText.subSequence(selEnd, iMin + selEnd));
        } else {
            charSequenceSubSequence = subText.subSequence(i4, i5 + i4);
        }
        int i6 = iMin2 + 0;
        setSurroundingText(editorInfo, charSequenceSubSequence, i6, i2 + i6);
    }

    public static CharSequence getInitialTextBeforeCursor(EditorInfo editorInfo, int length, int flags) {
        CharSequence charSequence;
        if (Build.VERSION.SDK_INT >= 30) {
            return Impl30.getInitialTextBeforeCursor(editorInfo, length, flags);
        }
        if (editorInfo.extras == null || (charSequence = editorInfo.extras.getCharSequence(CONTENT_SURROUNDING_TEXT_KEY)) == null) {
            return null;
        }
        int i = editorInfo.extras.getInt(CONTENT_SELECTION_HEAD_KEY);
        int iMin = Math.min(length, i);
        if ((flags & 1) != 0) {
            return charSequence.subSequence(i - iMin, i);
        }
        return TextUtils.substring(charSequence, i - iMin, i);
    }

    public static CharSequence getInitialSelectedText(EditorInfo editorInfo, int flags) {
        CharSequence charSequence;
        if (Build.VERSION.SDK_INT >= 30) {
            return Impl30.getInitialSelectedText(editorInfo, flags);
        }
        if (editorInfo.extras == null) {
            return null;
        }
        int i = editorInfo.initialSelStart > editorInfo.initialSelEnd ? editorInfo.initialSelEnd : editorInfo.initialSelStart;
        int i2 = editorInfo.initialSelStart > editorInfo.initialSelEnd ? editorInfo.initialSelStart : editorInfo.initialSelEnd;
        int i3 = editorInfo.extras.getInt(CONTENT_SELECTION_HEAD_KEY);
        int i4 = editorInfo.extras.getInt(CONTENT_SELECTION_END_KEY);
        int i5 = i2 - i;
        if (editorInfo.initialSelStart < 0 || editorInfo.initialSelEnd < 0 || i4 - i3 != i5 || (charSequence = editorInfo.extras.getCharSequence(CONTENT_SURROUNDING_TEXT_KEY)) == null) {
            return null;
        }
        if ((flags & 1) != 0) {
            return charSequence.subSequence(i3, i4);
        }
        return TextUtils.substring(charSequence, i3, i4);
    }

    public static CharSequence getInitialTextAfterCursor(EditorInfo editorInfo, int length, int flags) {
        CharSequence charSequence;
        if (Build.VERSION.SDK_INT >= 30) {
            return Impl30.getInitialTextAfterCursor(editorInfo, length, flags);
        }
        if (editorInfo.extras == null || (charSequence = editorInfo.extras.getCharSequence(CONTENT_SURROUNDING_TEXT_KEY)) == null) {
            return null;
        }
        int i = editorInfo.extras.getInt(CONTENT_SELECTION_END_KEY);
        int iMin = Math.min(length, charSequence.length() - i);
        if ((flags & 1) != 0) {
            return charSequence.subSequence(i, iMin + i);
        }
        return TextUtils.substring(charSequence, i, iMin + i);
    }

    private static boolean isCutOnSurrogate(CharSequence sourceText, int cutPosition, int policy) {
        if (policy == 0) {
            return Character.isLowSurrogate(sourceText.charAt(cutPosition));
        }
        if (policy != 1) {
            return false;
        }
        return Character.isHighSurrogate(sourceText.charAt(cutPosition));
    }

    private static void setSurroundingText(EditorInfo editorInfo, CharSequence surroundingText, int selectionHead, int selectionEnd) {
        if (editorInfo.extras == null) {
            editorInfo.extras = new Bundle();
        }
        editorInfo.extras.putCharSequence(CONTENT_SURROUNDING_TEXT_KEY, surroundingText != null ? new SpannableStringBuilder(surroundingText) : null);
        editorInfo.extras.putInt(CONTENT_SELECTION_HEAD_KEY, selectionHead);
        editorInfo.extras.putInt(CONTENT_SELECTION_END_KEY, selectionEnd);
    }

    static int getProtocol(EditorInfo editorInfo) {
        if (Build.VERSION.SDK_INT >= 25) {
            return 1;
        }
        if (editorInfo.extras == null) {
            return 0;
        }
        boolean zContainsKey = editorInfo.extras.containsKey(CONTENT_MIME_TYPES_KEY);
        boolean zContainsKey2 = editorInfo.extras.containsKey(CONTENT_MIME_TYPES_INTEROP_KEY);
        if (zContainsKey && zContainsKey2) {
            return 4;
        }
        if (zContainsKey) {
            return 3;
        }
        return zContainsKey2 ? 2 : 0;
    }

    @Deprecated
    public EditorInfoCompat() {
    }

    private static class Impl30 {
        private Impl30() {
        }

        static void setInitialSurroundingSubText(EditorInfo editorInfo, CharSequence sourceText, int subTextStart) {
            editorInfo.setInitialSurroundingSubText(sourceText, subTextStart);
        }

        static CharSequence getInitialTextBeforeCursor(EditorInfo editorInfo, int length, int flags) {
            return editorInfo.getInitialTextBeforeCursor(length, flags);
        }

        static CharSequence getInitialSelectedText(EditorInfo editorInfo, int flags) {
            return editorInfo.getInitialSelectedText(flags);
        }

        static CharSequence getInitialTextAfterCursor(EditorInfo editorInfo, int length, int flags) {
            return editorInfo.getInitialTextAfterCursor(length, flags);
        }
    }
}
