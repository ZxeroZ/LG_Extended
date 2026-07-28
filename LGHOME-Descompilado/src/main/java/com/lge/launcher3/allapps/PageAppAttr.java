package com.lge.launcher3.allapps;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.lge.launcher3.R;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class PageAppAttr extends AttrWrapper {
    private PageAppAttr(TypedArray ta) {
        this.typedArray = ta;
    }

    private PageAppAttr(AttributeSet attrs) {
        this.attrSet = attrs;
        initAttrNameTable();
    }

    @Override // com.lge.launcher3.allapps.AttrWrapper
    protected void initAttrNameTable() {
        this.attrNameTable = new HashMap();
        this.attrNameTable.put(1, "pagenumber");
        this.attrNameTable.put(2, "parentfoldernumber");
        this.attrNameTable.put(0, "appComponentName");
    }

    public static PageAppAttr from(Context context, AttributeSet attrs, boolean isCompiledRes) {
        if (isCompiledRes) {
            return new PageAppAttr(context.obtainStyledAttributes(attrs, R.styleable.pageApp));
        }
        return new PageAppAttr(attrs);
    }
}
