package com.lge.launcher3.allapps;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.lge.launcher3.R;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class PageFolderAttr extends AttrWrapper {
    private PageFolderAttr(TypedArray ta) {
        this.typedArray = ta;
    }

    private PageFolderAttr(AttributeSet attrs) {
        this.attrSet = attrs;
        initAttrNameTable();
    }

    @Override // com.lge.launcher3.allapps.AttrWrapper
    protected void initAttrNameTable() {
        this.attrNameTable = new HashMap();
        this.attrNameTable.put(3, "folderpagenumber");
        this.attrNameTable.put(2, "foldernumber");
        this.attrNameTable.put(1, "foldername");
        this.attrNameTable.put(0, "foldercolortype");
        this.attrNameTable.put(4, "folderuneditable");
    }

    public static PageFolderAttr from(Context context, AttributeSet attrs, boolean isCompiledRes) {
        if (isCompiledRes) {
            return new PageFolderAttr(context.obtainStyledAttributes(attrs, R.styleable.pageFolder));
        }
        return new PageFolderAttr(attrs);
    }
}
