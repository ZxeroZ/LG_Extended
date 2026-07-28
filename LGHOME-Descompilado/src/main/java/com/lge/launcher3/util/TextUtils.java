package com.lge.launcher3.util;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.launcher3.BubbleTextView;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LGFeatureConfig;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class TextUtils {

    public enum MaxFontType {
        NORMAL,
        LIMITED
    }

    private static boolean isHangul(int code) {
        if (code < 44032 || code > 55203) {
            return code >= 12593 && code <= 12686;
        }
        return true;
    }

    public static float getFontScale(Context context, AttributeSet attrs, BubbleTextView textView, int displayType) {
        if (context == null) {
            return 1.0f;
        }
        Resources resources = context.getResources();
        float integer = resources.getInteger(R.integer.config_medium_font_scale_percent) / 100.0f;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attrs, R.styleable.BubbleTextView, 0, 0);
        int integer2 = typedArrayObtainStyledAttributes.getInteger(3, displayType);
        typedArrayObtainStyledAttributes.recycle();
        if (integer2 != displayType || !"VZW".equals(LGFeatureConfig.FEATURE_OPERATOR)) {
            return integer;
        }
        float f = resources.getConfiguration().fontScale;
        float integer3 = resources.getInteger(R.integer.config_large_font_scale_percent) / 100.0f;
        return f < integer ? integer : integer3 < f ? integer3 : f;
    }

    public static void setTextScaleX(TextView textView) {
        if (textView == null) {
            return;
        }
        float resizeValue = getResizeValue(textView);
        if (resizeValue > 0.0f) {
            textView.setTextScaleX(MathFunctionUtils.floorDigit(resizeValue, 2));
        }
    }

    public static void setScaledSize(Context context, TextView tv, MaxFontType maxFontType) {
        if (context == null || tv == null) {
            return;
        }
        float textSize = tv.getTextSize();
        float f = context.getResources().getConfiguration().fontScale;
        if (maxFontType == MaxFontType.LIMITED) {
            float integer = LGHomeResources.getInstance(context).getInteger("config_max_font_scale_percent", context.getResources().getInteger(R.integer.config_max_font_scale_percent)) / 100.0f;
            LGLog.i("TextSize", "font scale " + integer);
            if (f > integer) {
                f = integer;
            }
        }
        tv.setTextSize(0, textSize * f);
    }

    private static float getResizeValue(TextView textView) {
        int width = (textView.getWidth() - textView.getPaddingStart()) - textView.getPaddingEnd();
        String string = textView.getText().toString();
        TextPaint paint = textView.getPaint();
        int length = string.toString().length();
        resetScale(textView, string, paint);
        float fMeasureText = paint.measureText(string);
        float f = width;
        int iBreakText = paint.breakText(string, true, f, null);
        int iIndexOf = string.indexOf(10);
        if (fMeasureText < f || iBreakText <= 0 || iBreakText >= string.length() || iIndexOf > 0) {
            return -1.0f;
        }
        int iIndexOf2 = string.indexOf(32);
        int threshold = getThreshold(textView);
        int i = iBreakText - 1;
        if (length - iBreakText <= threshold && isHangul(string.codePointAt(i))) {
            return f / (fMeasureText + 1.0f);
        }
        if (textView.getMaxLines() == 1 || iIndexOf2 < 0) {
            return checkSinglineOrNoSpace(textView, width, i, iBreakText);
        }
        if (iIndexOf2 != i && iIndexOf2 + 1 != i && iIndexOf2 <= i + threshold) {
            if (iIndexOf2 > i) {
                return getTextViewScaleBySeparator(string, paint, i, iIndexOf2);
            }
            int spaceIndexNearByBreakPos = getSpaceIndexNearByBreakPos(string, i, iIndexOf2);
            int i2 = spaceIndexNearByBreakPos + 1;
            String str = new String(string.substring(i2));
            float fMeasureText2 = paint.measureText(str);
            int iBreakText2 = paint.breakText(str, true, f, null);
            if (fMeasureText2 > f && iBreakText2 > 0 && iBreakText2 < str.length()) {
                if ((length - i2) - iBreakText2 <= threshold) {
                    return setTextViewScaleByNewToken(width, string, paint, spaceIndexNearByBreakPos, true);
                }
                int iIndexOf3 = string.indexOf(32, i + 1);
                if (iIndexOf3 >= 0 && (iIndexOf3 - i) - 1 <= threshold) {
                    return setTextViewScaleByNewToken(width, string, paint, iIndexOf3, false);
                }
            }
        }
        return -1.0f;
    }

    private static float checkSinglineOrNoSpace(TextView textView, int realWidth, int breakIndex, int end) {
        String string = textView.getText().toString();
        TextPaint paint = textView.getPaint();
        int length = string.toString().length();
        int threshold = getThreshold(textView);
        int i = length - end;
        if (i > 1 && i <= threshold + 1) {
            int i2 = breakIndex + 1;
            if (paint.measureText(string, 0, i2) < realWidth) {
                end++;
                breakIndex = i2;
            }
        }
        if (length - end <= threshold) {
            return getTextViewScaleBySeparator(string, paint, breakIndex, -1);
        }
        return -1.0f;
    }

    private static int getThreshold(TextView textView) {
        Resources resources = textView.getResources();
        if (resources != null) {
            return resources.getInteger(R.integer.config_threshold_of_label_without_ellipsis);
        }
        return 2;
    }

    public static void resetScale(TextView textView, String text, Paint paint) {
        paint.setTextScaleX(1.0f);
        paint.setTextSize(textView.getTextSize());
        textView.setTextScaleX(1.0f);
        textView.setText(text);
    }

    private static float setTextViewScaleByNewToken(int realWidth, String text, Paint paint, int separator, boolean isNextLine) {
        String str;
        if (isNextLine) {
            str = new String(text.substring(separator + 1));
        } else {
            str = new String(text.substring(0, separator));
        }
        return getTextViewScaleBySeparator(str, paint, paint.breakText(str, true, realWidth, null), -1);
    }

    private static float getTextViewScaleBySeparator(String text, Paint paint, int breakIndex, int endIndex) {
        float fMeasureText;
        float fMeasureText2 = paint.measureText(text, 0, breakIndex);
        if (endIndex == -1) {
            fMeasureText = paint.measureText(text);
        } else {
            fMeasureText = paint.measureText(text, 0, endIndex);
        }
        return fMeasureText2 / (fMeasureText + 1.0f);
    }

    private static int getSpaceIndexNearByBreakPos(String text, int breakIndex, int spaceIdx) {
        int i = spaceIdx + 1;
        while (i < breakIndex) {
            int iIndexOf = text.indexOf(32, i);
            if (iIndexOf == -1) {
                break;
            }
            if (iIndexOf < breakIndex) {
                spaceIdx = iIndexOf;
            }
            i = iIndexOf + 1;
        }
        return spaceIdx;
    }

    public static boolean isRToLLanguage() {
        String language = Locale.getDefault().getLanguage();
        return language.equals("ar") || language.equals("fa") || language.equals("iw") || language.equals("ku");
    }

    public static int getLineSpacing(TextView textView) {
        if (textView == null) {
            return 0;
        }
        int lineHeight = textView.getLineHeight();
        Paint.FontMetrics fontMetrics = textView.getPaint().getFontMetrics();
        return lineHeight - ((int) (fontMetrics.bottom - fontMetrics.top));
    }
}
