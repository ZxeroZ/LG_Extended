package com.lge.launcher3.allapps;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TabWidget;

/* JADX INFO: loaded from: classes.dex */
public class FocusOnlyTabWidget extends TabWidget {
    @Override // android.widget.TabWidget, android.view.View.OnFocusChangeListener
    public void onFocusChange(View v, boolean hasFocus) {
    }

    public FocusOnlyTabWidget(Context context) {
        super(context);
    }

    public FocusOnlyTabWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusOnlyTabWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public View getSelectedTab() {
        int tabCount = getTabCount();
        for (int i = 0; i < tabCount; i++) {
            View childTabViewAt = getChildTabViewAt(i);
            if (childTabViewAt != null && childTabViewAt.isSelected()) {
                return childTabViewAt;
            }
        }
        return null;
    }

    public int getChildTabIndex(View v) {
        int tabCount = getTabCount();
        for (int i = 0; i < tabCount; i++) {
            if (getChildTabViewAt(i) == v) {
                return i;
            }
        }
        return -1;
    }

    public void setCurrentTabToFocusedTab() {
        View childTabViewAt;
        int tabCount = getTabCount();
        int i = 0;
        while (true) {
            if (i >= tabCount) {
                childTabViewAt = null;
                i = -1;
                break;
            } else {
                childTabViewAt = getChildTabViewAt(i);
                if (childTabViewAt != null && childTabViewAt.hasFocus()) {
                    break;
                } else {
                    i++;
                }
            }
        }
        if (i > -1) {
            super.setCurrentTab(i);
            super.onFocusChange(childTabViewAt, true);
        }
    }

    public void superOnFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
    }
}
