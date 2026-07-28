package com.lge.launcher3.wing;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes2.dex */
public class SystemShortcutView extends LinearLayout {
    public ImageView mIconView;
    public TextView mTextView;

    public SystemShortcutView(Context context) {
        this(context, null, 0);
    }

    public SystemShortcutView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SystemShortcutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mIconView = (ImageView) findViewById(R.id.icon);
        this.mTextView = (TextView) findViewById(R.id.name);
    }
}
