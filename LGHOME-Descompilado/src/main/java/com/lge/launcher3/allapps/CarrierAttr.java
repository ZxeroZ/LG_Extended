package com.lge.launcher3.allapps;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.lge.launcher3.R;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class CarrierAttr extends AttrWrapper {
    private CarrierAttr(TypedArray ta) {
        this.typedArray = ta;
    }

    private CarrierAttr(AttributeSet attrs) {
        this.attrSet = attrs;
        initAttrNameTable();
    }

    @Override // com.lge.launcher3.allapps.AttrWrapper
    protected void initAttrNameTable() {
        this.attrNameTable = new HashMap();
        this.attrNameTable.put(1, "Mcc");
        this.attrNameTable.put(2, "Mnc");
        this.attrNameTable.put(0, "Gid");
    }

    public static CarrierAttr from(Context context, AttributeSet attrs, boolean isCompiledRes) {
        if (isCompiledRes) {
            return new CarrierAttr(context.obtainStyledAttributes(attrs, R.styleable.Carrier));
        }
        return new CarrierAttr(attrs);
    }
}
