package com.lge.launcher3.allapps;

import android.content.res.TypedArray;
import android.util.AttributeSet;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public abstract class AttrWrapper {
    private static String sXMLNamespace;
    protected Map<Integer, String> attrNameTable;
    protected AttributeSet attrSet;
    protected TypedArray typedArray;

    protected abstract void initAttrNameTable();

    public String getString(int attrId) {
        TypedArray typedArray = this.typedArray;
        if (typedArray != null) {
            return typedArray.getString(attrId);
        }
        AttributeSet attributeSet = this.attrSet;
        if (attributeSet != null) {
            return attributeSet.getAttributeValue(sXMLNamespace, this.attrNameTable.get(Integer.valueOf(attrId)));
        }
        return null;
    }

    public int getInt(int attrId, int defValue) {
        TypedArray typedArray = this.typedArray;
        if (typedArray != null) {
            return typedArray.getInt(attrId, defValue);
        }
        AttributeSet attributeSet = this.attrSet;
        if (attributeSet != null) {
            try {
                return Integer.valueOf(attributeSet.getAttributeValue(sXMLNamespace, this.attrNameTable.get(Integer.valueOf(attrId)))).intValue();
            } catch (NumberFormatException unused) {
            }
        }
        return defValue;
    }

    public void recycle() {
        TypedArray typedArray = this.typedArray;
        if (typedArray != null) {
            typedArray.recycle();
        }
    }

    public void setNamespace(final String namespace) {
        sXMLNamespace = namespace;
    }
}
