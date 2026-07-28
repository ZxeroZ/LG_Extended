package com.lge.launcher3.help;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.lge.launcher3.R;
import com.lge.launcher3.util.TextUtils;

/* JADX INFO: loaded from: classes.dex */
public class HelpPageAdapter extends PagerAdapter {
    private HelpItemInfo mInfo;

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getItemPosition(Object object) {
        return -2;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    public HelpPageAdapter(HelpItemInfo info) {
        this.mInfo = info;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public Object instantiateItem(View pager, int position) {
        View viewInflate;
        Context context = pager.getContext();
        if (context == null || (viewInflate = LayoutInflater.from(context).inflate(R.layout.help_page, (ViewGroup) null)) == null) {
            return null;
        }
        HelpItem helpItemCreateItem = this.mInfo.createItem(position);
        TextView textView = (TextView) viewInflate.findViewById(R.id.initial_guide_page_title);
        if (textView != null) {
            textView.setText(helpItemCreateItem.mTitleResId);
        }
        ImageView imageView = (ImageView) viewInflate.findViewById(R.id.initial_guide_page_image);
        if (imageView != null) {
            imageView.setImageDrawable(context.getDrawable(helpItemCreateItem.mImageResId));
        }
        TextView textView2 = (TextView) viewInflate.findViewById(R.id.initial_guide_page_desc);
        if (textView2 != null) {
            textView2.setText(helpItemCreateItem.mDescResId);
            if (helpItemCreateItem.mDescSubResId != null) {
                LinearLayout linearLayout = (LinearLayout) viewInflate.findViewById(R.id.help_desc_sub_layout);
                linearLayout.setVisibility(0);
                for (int i : helpItemCreateItem.mDescSubResId) {
                    LinearLayout linearLayout2 = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.bullet_textview_layout_help, (ViewGroup) null);
                    if (linearLayout2 != null) {
                        TextView textView3 = (TextView) linearLayout2.findViewById(R.id.bullet_textview_bullet);
                        TextView textView4 = (TextView) linearLayout2.findViewById(R.id.bullet_textview_desc);
                        if (textView3 != null && textView4 != null) {
                            textView3.setText("  " + ((Object) textView3.getText()));
                            textView4.setText(i);
                            linearLayout2.setPadding(linearLayout2.getPaddingLeft(), TextUtils.getLineSpacing(textView4), linearLayout2.getPaddingRight(), linearLayout2.getPaddingBottom());
                            linearLayout.addView(linearLayout2);
                        }
                    }
                }
            }
        }
        viewInflate.setLayerType(2, null);
        ((ViewPager) pager).addView(viewInflate);
        return viewInflate;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public void destroyItem(View pager, int position, Object view) {
        ((ViewPager) pager).removeView((View) view);
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getCount() {
        return this.mInfo.size();
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public void finishUpdate(ViewGroup container) {
        super.finishUpdate(container);
    }
}
