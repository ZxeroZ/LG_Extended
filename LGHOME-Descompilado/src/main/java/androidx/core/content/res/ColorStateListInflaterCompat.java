package androidx.core.content.res;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StateSet;
import android.util.TypedValue;
import android.util.Xml;
import androidx.core.R;
import androidx.core.view.ViewCompat;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public final class ColorStateListInflaterCompat {
    private static final ThreadLocal<TypedValue> sTempTypedValue = new ThreadLocal<>();

    private ColorStateListInflaterCompat() {
    }

    public static ColorStateList inflate(Resources resources, int resId, Resources.Theme theme) {
        try {
            return createFromXml(resources, resources.getXml(resId), theme);
        } catch (Exception e) {
            Log.e("CSLCompat", "Failed to inflate ColorStateList.", e);
            return null;
        }
    }

    public static ColorStateList createFromXml(Resources r, XmlPullParser parser, Resources.Theme theme) throws XmlPullParserException, IOException {
        int next;
        AttributeSet attributeSetAsAttributeSet = Xml.asAttributeSet(parser);
        do {
            next = parser.next();
            if (next == 2) {
                break;
            }
        } while (next != 1);
        if (next != 2) {
            throw new XmlPullParserException("No start tag found");
        }
        return createFromXmlInner(r, parser, attributeSetAsAttributeSet, theme);
    }

    public static ColorStateList createFromXmlInner(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        String name = parser.getName();
        if (!name.equals("selector")) {
            throw new XmlPullParserException(parser.getPositionDescription() + ": invalid color state list tag " + name);
        }
        return inflate(r, parser, attrs, theme);
    }

    private static ColorStateList inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        int depth;
        int color;
        int i = 1;
        int depth2 = parser.getDepth() + 1;
        int[][] iArr = new int[20][];
        int[] iArrAppend = new int[20];
        int i2 = 0;
        while (true) {
            int next = parser.next();
            if (next == i || ((depth = parser.getDepth()) < depth2 && next == 3)) {
                break;
            }
            if (next == 2 && depth <= depth2 && parser.getName().equals("item")) {
                TypedArray typedArrayObtainAttributes = obtainAttributes(r, theme, attrs, R.styleable.ColorStateListItem);
                int resourceId = typedArrayObtainAttributes.getResourceId(R.styleable.ColorStateListItem_android_color, -1);
                if (resourceId != -1 && !isColorInt(r, resourceId)) {
                    try {
                        color = createFromXml(r, r.getXml(resourceId), theme).getDefaultColor();
                    } catch (Exception unused) {
                        color = typedArrayObtainAttributes.getColor(R.styleable.ColorStateListItem_android_color, -65281);
                    }
                } else {
                    color = typedArrayObtainAttributes.getColor(R.styleable.ColorStateListItem_android_color, -65281);
                }
                float f = 1.0f;
                if (typedArrayObtainAttributes.hasValue(R.styleable.ColorStateListItem_android_alpha)) {
                    f = typedArrayObtainAttributes.getFloat(R.styleable.ColorStateListItem_android_alpha, 1.0f);
                } else if (typedArrayObtainAttributes.hasValue(R.styleable.ColorStateListItem_alpha)) {
                    f = typedArrayObtainAttributes.getFloat(R.styleable.ColorStateListItem_alpha, 1.0f);
                }
                typedArrayObtainAttributes.recycle();
                int attributeCount = attrs.getAttributeCount();
                int[] iArr2 = new int[attributeCount];
                int i3 = 0;
                for (int i4 = 0; i4 < attributeCount; i4++) {
                    int attributeNameResource = attrs.getAttributeNameResource(i4);
                    if (attributeNameResource != 16843173 && attributeNameResource != 16843551 && attributeNameResource != R.attr.alpha) {
                        int i5 = i3 + 1;
                        if (!attrs.getAttributeBooleanValue(i4, false)) {
                            attributeNameResource = -attributeNameResource;
                        }
                        iArr2[i3] = attributeNameResource;
                        i3 = i5;
                    }
                }
                int[] iArrTrimStateSet = StateSet.trimStateSet(iArr2, i3);
                iArrAppend = GrowingArrayUtils.append(iArrAppend, i2, modulateColorAlpha(color, f));
                iArr = (int[][]) GrowingArrayUtils.append(iArr, i2, iArrTrimStateSet);
                i2++;
            }
            i = 1;
        }
        int[] iArr3 = new int[i2];
        int[][] iArr4 = new int[i2][];
        System.arraycopy(iArrAppend, 0, iArr3, 0, i2);
        System.arraycopy(iArr, 0, iArr4, 0, i2);
        return new ColorStateList(iArr4, iArr3);
    }

    private static boolean isColorInt(Resources r, int resId) {
        TypedValue typedValue = getTypedValue();
        r.getValue(resId, typedValue, true);
        return typedValue.type >= 28 && typedValue.type <= 31;
    }

    private static TypedValue getTypedValue() {
        ThreadLocal<TypedValue> threadLocal = sTempTypedValue;
        TypedValue typedValue = threadLocal.get();
        if (typedValue != null) {
            return typedValue;
        }
        TypedValue typedValue2 = new TypedValue();
        threadLocal.set(typedValue2);
        return typedValue2;
    }

    private static TypedArray obtainAttributes(Resources res, Resources.Theme theme, AttributeSet set, int[] attrs) {
        if (theme == null) {
            return res.obtainAttributes(set, attrs);
        }
        return theme.obtainStyledAttributes(set, attrs, 0, 0);
    }

    private static int modulateColorAlpha(int color, float alphaMod) {
        return (color & ViewCompat.MEASURED_SIZE_MASK) | (Math.round(Color.alpha(color) * alphaMod) << 24);
    }
}
