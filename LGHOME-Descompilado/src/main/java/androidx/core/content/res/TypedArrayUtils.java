package androidx.core.content.res;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class TypedArrayUtils {
    private static final String NAMESPACE = "http://schemas.android.com/apk/res/android";

    public static boolean hasAttribute(XmlPullParser parser, String attrName) {
        return parser.getAttributeValue(NAMESPACE, attrName) != null;
    }

    public static float getNamedFloat(TypedArray a, XmlPullParser parser, String attrName, int resId, float defaultValue) {
        return !hasAttribute(parser, attrName) ? defaultValue : a.getFloat(resId, defaultValue);
    }

    public static boolean getNamedBoolean(TypedArray a, XmlPullParser parser, String attrName, int resId, boolean defaultValue) {
        return !hasAttribute(parser, attrName) ? defaultValue : a.getBoolean(resId, defaultValue);
    }

    public static int getNamedInt(TypedArray a, XmlPullParser parser, String attrName, int resId, int defaultValue) {
        return !hasAttribute(parser, attrName) ? defaultValue : a.getInt(resId, defaultValue);
    }

    public static int getNamedColor(TypedArray a, XmlPullParser parser, String attrName, int resId, int defaultValue) {
        return !hasAttribute(parser, attrName) ? defaultValue : a.getColor(resId, defaultValue);
    }

    public static ComplexColorCompat getNamedComplexColor(TypedArray a, XmlPullParser parser, Resources.Theme theme, String attrName, int resId, int defaultValue) {
        if (hasAttribute(parser, attrName)) {
            TypedValue typedValue = new TypedValue();
            a.getValue(resId, typedValue);
            if (typedValue.type >= 28 && typedValue.type <= 31) {
                return ComplexColorCompat.from(typedValue.data);
            }
            ComplexColorCompat complexColorCompatInflate = ComplexColorCompat.inflate(a.getResources(), a.getResourceId(resId, 0), theme);
            if (complexColorCompatInflate != null) {
                return complexColorCompatInflate;
            }
        }
        return ComplexColorCompat.from(defaultValue);
    }

    public static ColorStateList getNamedColorStateList(TypedArray a, XmlPullParser parser, Resources.Theme theme, String attrName, int resId) {
        if (!hasAttribute(parser, attrName)) {
            return null;
        }
        TypedValue typedValue = new TypedValue();
        a.getValue(resId, typedValue);
        if (typedValue.type == 2) {
            throw new UnsupportedOperationException("Failed to resolve attribute at index " + resId + ": " + typedValue);
        }
        if (typedValue.type >= 28 && typedValue.type <= 31) {
            return getNamedColorStateListFromInt(typedValue);
        }
        return ColorStateListInflaterCompat.inflate(a.getResources(), a.getResourceId(resId, 0), theme);
    }

    private static ColorStateList getNamedColorStateListFromInt(TypedValue value) {
        return ColorStateList.valueOf(value.data);
    }

    public static int getNamedResourceId(TypedArray a, XmlPullParser parser, String attrName, int resId, int defaultValue) {
        return !hasAttribute(parser, attrName) ? defaultValue : a.getResourceId(resId, defaultValue);
    }

    public static String getNamedString(TypedArray a, XmlPullParser parser, String attrName, int resId) {
        if (hasAttribute(parser, attrName)) {
            return a.getString(resId);
        }
        return null;
    }

    public static TypedValue peekNamedValue(TypedArray a, XmlPullParser parser, String attrName, int resId) {
        if (hasAttribute(parser, attrName)) {
            return a.peekValue(resId);
        }
        return null;
    }

    public static TypedArray obtainAttributes(Resources res, Resources.Theme theme, AttributeSet set, int[] attrs) {
        if (theme == null) {
            return res.obtainAttributes(set, attrs);
        }
        return theme.obtainStyledAttributes(set, attrs, 0, 0);
    }

    public static boolean getBoolean(TypedArray a, int index, int fallbackIndex, boolean defaultValue) {
        return a.getBoolean(index, a.getBoolean(fallbackIndex, defaultValue));
    }

    public static Drawable getDrawable(TypedArray a, int index, int fallbackIndex) {
        Drawable drawable = a.getDrawable(index);
        return drawable == null ? a.getDrawable(fallbackIndex) : drawable;
    }

    public static int getInt(TypedArray a, int index, int fallbackIndex, int defaultValue) {
        return a.getInt(index, a.getInt(fallbackIndex, defaultValue));
    }

    public static int getResourceId(TypedArray a, int index, int fallbackIndex, int defaultValue) {
        return a.getResourceId(index, a.getResourceId(fallbackIndex, defaultValue));
    }

    public static String getString(TypedArray a, int index, int fallbackIndex) {
        String string = a.getString(index);
        return string == null ? a.getString(fallbackIndex) : string;
    }

    public static CharSequence getText(TypedArray a, int index, int fallbackIndex) {
        CharSequence text = a.getText(index);
        return text == null ? a.getText(fallbackIndex) : text;
    }

    public static CharSequence[] getTextArray(TypedArray a, int index, int fallbackIndex) {
        CharSequence[] textArray = a.getTextArray(index);
        return textArray == null ? a.getTextArray(fallbackIndex) : textArray;
    }

    public static int getAttr(Context context, int attr, int fallbackAttr) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.resourceId != 0 ? attr : fallbackAttr;
    }

    private TypedArrayUtils() {
    }
}
