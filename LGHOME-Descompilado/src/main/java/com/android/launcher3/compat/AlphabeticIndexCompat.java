package com.android.launcher3.compat;

import android.content.Context;
import com.android.launcher3.Utilities;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class AlphabeticIndexCompat extends BaseAlphabeticIndex {
    private static final String MID_DOT = "∙";
    private Method mAddLabelsMethod;
    private Object mAlphabeticIndex;
    private String mDefaultMiscLabel;
    private Method mGetBucketIndexMethod;
    private Method mGetBucketLabelMethod;
    private boolean mHasValidAlphabeticIndex;
    private Method mSetMaxLabelCountMethod;

    public AlphabeticIndexCompat(Context context) {
        try {
            Locale locale = context.getResources().getConfiguration().locale;
            Class<?> cls = Class.forName("libcore.icu.AlphabeticIndex");
            Constructor<?> constructor = cls.getConstructor(Locale.class);
            this.mAddLabelsMethod = cls.getDeclaredMethod("addLabels", Locale.class);
            this.mSetMaxLabelCountMethod = cls.getDeclaredMethod("setMaxLabelCount", Integer.TYPE);
            this.mGetBucketIndexMethod = cls.getDeclaredMethod("getBucketIndex", String.class);
            this.mGetBucketLabelMethod = cls.getDeclaredMethod("getBucketLabel", Integer.TYPE);
            this.mAlphabeticIndex = constructor.newInstance(locale);
            try {
                if (!locale.getLanguage().equals(Locale.ENGLISH.getLanguage())) {
                    this.mAddLabelsMethod.invoke(this.mAlphabeticIndex, Locale.ENGLISH);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (locale.getLanguage().equals(Locale.JAPANESE.getLanguage())) {
                this.mDefaultMiscLabel = "他";
            } else {
                this.mDefaultMiscLabel = MID_DOT;
            }
            this.mHasValidAlphabeticIndex = true;
        } catch (Exception unused) {
            this.mHasValidAlphabeticIndex = false;
        }
    }

    @Override // com.android.launcher3.compat.BaseAlphabeticIndex
    public void setMaxLabelCount(int count) {
        if (this.mHasValidAlphabeticIndex) {
            try {
                this.mSetMaxLabelCountMethod.invoke(this.mAlphabeticIndex, Integer.valueOf(count));
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        super.setMaxLabelCount(count);
    }

    public String computeSectionName(CharSequence cs) {
        String strTrim = Utilities.trim(cs);
        String bucketLabel = getBucketLabel(getBucketIndex(strTrim));
        if (!Utilities.trim(bucketLabel).isEmpty() || strTrim.length() <= 0) {
            return bucketLabel;
        }
        int iCodePointAt = strTrim.codePointAt(0);
        return Character.isDigit(iCodePointAt) ? "#" : Character.isLetter(iCodePointAt) ? this.mDefaultMiscLabel : MID_DOT;
    }

    @Override // com.android.launcher3.compat.BaseAlphabeticIndex
    protected int getBucketIndex(String s) {
        if (this.mHasValidAlphabeticIndex) {
            try {
                return ((Integer) this.mGetBucketIndexMethod.invoke(this.mAlphabeticIndex, s)).intValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.getBucketIndex(s);
    }

    @Override // com.android.launcher3.compat.BaseAlphabeticIndex
    protected String getBucketLabel(int index) {
        if (this.mHasValidAlphabeticIndex) {
            try {
                return (String) this.mGetBucketLabelMethod.invoke(this.mAlphabeticIndex, Integer.valueOf(index));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.getBucketLabel(index);
    }
}
