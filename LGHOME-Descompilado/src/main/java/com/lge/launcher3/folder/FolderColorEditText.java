package com.lge.launcher3.folder;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/* JADX INFO: loaded from: classes.dex */
public class FolderColorEditText extends EditText {
    private boolean menuContext;

    public FolderColorEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.menuContext = false;
    }

    public FolderColorEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.menuContext = false;
    }

    public FolderColorEditText(Context context) {
        super(context);
        this.menuContext = false;
    }

    @Override // android.widget.EditText, android.widget.TextView
    public boolean onTextContextMenuItem(int arg0) {
        if (arg0 == 16908330) {
            this.menuContext = true;
        }
        return super.onTextContextMenuItem(arg0);
    }

    public boolean isDictionaryCalled() {
        return this.menuContext;
    }

    public void setIsDictionaryCalled(boolean value) {
        this.menuContext = value;
    }
}
