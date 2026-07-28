package com.lge.launcher3.initialguide;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class InitialGuidePageLayout extends LinearLayout {
    public static final String TAG = "InitialGuidePageLayout";

    public InitialGuidePageLayout(Context context) {
        this(context, null);
    }

    public InitialGuidePageLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InitialGuidePageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        TextView textView = (TextView) findViewById(R.id.initial_guide_page_title);
        ImageView imageView = (ImageView) findViewById(R.id.initial_guide_page_image);
        if (textView == null || imageView == null) {
            return;
        }
        Resources resources = getResources();
        setPadding(resources.getDimensionPixelSize(R.dimen.initial_guide_page_side_padding), resources.getDimensionPixelSize(R.dimen.initial_guide_page_top_padding), resources.getDimensionPixelSize(R.dimen.initial_guide_page_side_padding), getPaddingBottom());
    }
}
