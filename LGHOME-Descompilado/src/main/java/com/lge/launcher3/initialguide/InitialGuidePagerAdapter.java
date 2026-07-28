package com.lge.launcher3.initialguide;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.text.Html;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.android.launcher3.Utilities;
import com.lge.launcher3.R;
import com.lge.launcher3.initialguide.InitialGuidePageInfoMananger;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.TextUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class InitialGuidePagerAdapter extends PagerAdapter {
    public static final String TAG = "InitialGuidePagerAdapter";
    private static final Spannable.Factory spannableFactory = Spannable.Factory.getInstance();
    private Context mContext;
    private InitialGuidePageInfoMananger mPageInfoManager;

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getItemPosition(Object object) {
        return -2;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    public InitialGuidePagerAdapter(Context context, InitialGuidePageInfoMananger infoManager) {
        this.mContext = null;
        this.mPageInfoManager = null;
        this.mContext = context;
        this.mPageInfoManager = infoManager;
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public Object instantiateItem(View pager, int position) {
        InitialGuidePageLayout initialGuidePageLayout;
        InitialGuidePageInfoMananger initialGuidePageInfoMananger = this.mPageInfoManager;
        InitialGuidePageInfoMananger.PageInfo pageInfo = initialGuidePageInfoMananger != null ? initialGuidePageInfoMananger.getPageInfo(position) : null;
        if (pageInfo == null) {
            return null;
        }
        if (pageInfo.mPageType == InitialGuidePageInfoMananger.PageType.UX6_INITIAL_GUIDE_VZW) {
            initialGuidePageLayout = (InitialGuidePageLayout) LayoutInflater.from(this.mContext).inflate(R.layout.initial_guide_page, (ViewGroup) null);
        } else {
            if (pageInfo.mPageType == InitialGuidePageInfoMananger.PageType.SWIVEL_HOME_INITIAL_GUIDE_MAIN_INTRO || pageInfo.mPageType == InitialGuidePageInfoMananger.PageType.SWIVEL_HOME_INITIAL_GUIDE_MAIN_ADD_REARRANCE || pageInfo.mPageType == InitialGuidePageInfoMananger.PageType.SWIVEL_HOME_INITIAL_GUIDE_MAIN_SWING_MODE || pageInfo.mPageType == InitialGuidePageInfoMananger.PageType.SWIVEL_HOME_INITIAL_GUIDE_MAIN_SECOND_SCREEN) {
                InitialGuidePageLayout initialGuidePageLayout2 = (InitialGuidePageLayout) LayoutInflater.from(this.mContext).inflate(R.layout.swivel_home_initial_guide_page_main, (ViewGroup) null);
                setupVideo(pageInfo, initialGuidePageLayout2);
                initialGuidePageLayout2.setLayerType(2, null);
                ((ViewPager) pager).addView(initialGuidePageLayout2);
                return initialGuidePageLayout2;
            }
            if (pageInfo.mPageType == InitialGuidePageInfoMananger.PageType.SWIVEL_HOME_INITIAL_GUIDE_SUB_INTRO) {
                InitialGuidePageLayout initialGuidePageLayout3 = (InitialGuidePageLayout) LayoutInflater.from(this.mContext).inflate(R.layout.swivel_home_initial_guide_page_sub, (ViewGroup) null);
                TextView textView = (TextView) initialGuidePageLayout3.findViewById(R.id.initial_guide_page_title);
                TextView textView2 = (TextView) initialGuidePageLayout3.findViewById(R.id.initial_guide_page_desc_main);
                if (textView == null || textView2 == null) {
                    return null;
                }
                String string = this.mContext.getString(R.string.sp_swivel_homescreen_category_NORMAL);
                String string2 = this.mContext.getString(R.string.swing_mode);
                textView.setText(String.format(this.mContext.getString(pageInfo.mTitleResId.textId, string), new Object[0]));
                textView2.setText(String.format(this.mContext.getString(pageInfo.mDescMainResId.textId, string, string2, string), new Object[0]));
                initialGuidePageLayout3.setLayerType(2, null);
                ((ViewPager) pager).addView(initialGuidePageLayout3);
                return initialGuidePageLayout3;
            }
            if (pageInfo.mPageType == InitialGuidePageInfoMananger.PageType.SWIVEL_HOME_INITIAL_GUIDE_SUB_ADD_REARRANCE) {
                InitialGuidePageLayout initialGuidePageLayout4 = (InitialGuidePageLayout) LayoutInflater.from(this.mContext).inflate(R.layout.swivel_home_initial_guide_page_sub, (ViewGroup) null);
                TextView textView3 = (TextView) initialGuidePageLayout4.findViewById(R.id.initial_guide_page_title);
                TextView textView4 = (TextView) initialGuidePageLayout4.findViewById(R.id.initial_guide_page_desc_main);
                TextView textView5 = (TextView) initialGuidePageLayout4.findViewById(R.id.initial_guide_page_desc_sub_title_01);
                TextView textView6 = (TextView) initialGuidePageLayout4.findViewById(R.id.initial_guide_page_desc_sub_desc_01);
                TextView textView7 = (TextView) initialGuidePageLayout4.findViewById(R.id.initial_guide_page_desc_sub_title_02);
                TextView textView8 = (TextView) initialGuidePageLayout4.findViewById(R.id.initial_guide_page_desc_sub_desc_02);
                if (textView3 == null || textView4 == null || textView5 == null || textView6 == null || textView7 == null || textView8 == null) {
                    return null;
                }
                String string3 = this.mContext.getString(R.string.sp_swivel_homescreen_category_NORMAL);
                String string4 = this.mContext.getString(R.string.swing_main_screen);
                textView3.setText(pageInfo.mTitleResId.textId);
                textView4.setVisibility(8);
                textView5.setPadding(textView5.getPaddingLeft(), 0, textView5.getPaddingRight(), textView5.getPaddingBottom());
                textView5.setText(String.format(this.mContext.getString(pageInfo.mSubTitleResId_01.textId, string4), new Object[0]));
                textView5.setVisibility(0);
                textView6.setText(String.format(this.mContext.getString(pageInfo.mSubDescResId_01.textId, string3), new Object[0]));
                textView6.setVisibility(0);
                textView7.setText(String.format(this.mContext.getString(pageInfo.mSubTitleResId_02.textId, string4), new Object[0]));
                textView7.setVisibility(0);
                textView8.setText(pageInfo.mSubDescResId_02.textId);
                textView8.setVisibility(0);
                initialGuidePageLayout4.setLayerType(2, null);
                ((ViewPager) pager).addView(initialGuidePageLayout4);
                return initialGuidePageLayout4;
            }
            if (pageInfo.mPageType == InitialGuidePageInfoMananger.PageType.SWIVEL_HOME_INITIAL_GUIDE_SUB_SWING_MODE) {
                InitialGuidePageLayout initialGuidePageLayout5 = (InitialGuidePageLayout) LayoutInflater.from(this.mContext).inflate(R.layout.swivel_home_initial_guide_page_sub, (ViewGroup) null);
                TextView textView9 = (TextView) initialGuidePageLayout5.findViewById(R.id.initial_guide_page_title);
                TextView textView10 = (TextView) initialGuidePageLayout5.findViewById(R.id.initial_guide_page_desc_main);
                TextView textView11 = (TextView) initialGuidePageLayout5.findViewById(R.id.initial_guide_page_desc_sub_title_01);
                TextView textView12 = (TextView) initialGuidePageLayout5.findViewById(R.id.initial_guide_page_desc_sub_desc_01);
                TextView textView13 = (TextView) initialGuidePageLayout5.findViewById(R.id.initial_guide_page_desc_sub_title_02);
                TextView textView14 = (TextView) initialGuidePageLayout5.findViewById(R.id.initial_guide_page_desc_sub_desc_02);
                if (textView9 == null || textView10 == null || textView11 == null || textView12 == null || textView13 == null || textView14 == null) {
                    return null;
                }
                String string5 = this.mContext.getString(R.string.swing_mode);
                String string6 = this.mContext.getString(R.string.swing_main_screen);
                String string7 = this.mContext.getString(R.string.second_screen_title);
                String string8 = this.mContext.getString(R.string.dual_screen_app_pair_title);
                textView9.setText(pageInfo.mTitleResId.textId);
                textView10.setText(String.format(this.mContext.getString(pageInfo.mDescMainResId.textId, string5, string6, string7), new Object[0]));
                textView11.setText(pageInfo.mSubTitleResId_01.textId);
                textView11.setVisibility(0);
                textView12.setText(pageInfo.mSubDescResId_01.textId);
                textView12.setVisibility(0);
                textView13.setText(String.format(this.mContext.getString(pageInfo.mSubTitleResId_02.textId, string8), new Object[0]));
                textView13.setVisibility(0);
                textView14.setText(String.format(this.mContext.getString(pageInfo.mSubDescResId_02.textId, string7), new Object[0]));
                textView14.setVisibility(0);
                initialGuidePageLayout5.setLayerType(2, null);
                ((ViewPager) pager).addView(initialGuidePageLayout5);
                return initialGuidePageLayout5;
            }
            if (pageInfo.mPageType == InitialGuidePageInfoMananger.PageType.SWIVEL_HOME_INITIAL_GUIDE_SUB_SECOND_SCREEN) {
                InitialGuidePageLayout initialGuidePageLayout6 = (InitialGuidePageLayout) LayoutInflater.from(this.mContext).inflate(R.layout.swivel_home_initial_guide_page_sub, (ViewGroup) null);
                setupSecondScreenAppsSettingsButton(pageInfo, initialGuidePageLayout6);
                TextView textView15 = (TextView) initialGuidePageLayout6.findViewById(R.id.initial_guide_page_title);
                TextView textView16 = (TextView) initialGuidePageLayout6.findViewById(R.id.initial_guide_page_desc_main);
                Button button = (Button) initialGuidePageLayout6.findViewById(R.id.initial_guide_second_screen_apps_setting_button);
                if (textView15 == null || textView16 == null || button == null) {
                    return null;
                }
                textView15.setText(String.format(this.mContext.getString(pageInfo.mTitleResId.textId, this.mContext.getString(R.string.second_screen_title)), new Object[0]));
                textView16.setText(pageInfo.mDescMainResId.textId);
                initialGuidePageLayout6.setLayerType(2, null);
                ((ViewPager) pager).addView(initialGuidePageLayout6);
                return initialGuidePageLayout6;
            }
            initialGuidePageLayout = (InitialGuidePageLayout) LayoutInflater.from(this.mContext).inflate(R.layout.multi_window_initial_guide_page, (ViewGroup) null);
        }
        setupTitle(pageInfo, initialGuidePageLayout);
        setupImage(pageInfo, initialGuidePageLayout);
        setupDescriptionMain(pageInfo, initialGuidePageLayout);
        setupDescriptionSub(pageInfo, initialGuidePageLayout);
        setupDescriptionLast(pageInfo, initialGuidePageLayout);
        initialGuidePageLayout.setLayerType(2, null);
        ((ViewPager) pager).addView(initialGuidePageLayout);
        return initialGuidePageLayout;
    }

    private void setupTitle(InitialGuidePageInfoMananger.PageInfo pageInfo, InitialGuidePageLayout pageLayout) {
        TextView textView = (TextView) pageLayout.findViewById(R.id.initial_guide_page_title);
        if (textView == null) {
            return;
        }
        setTextFromResId(textView, pageInfo.mTitleResId);
    }

    private void setupImage(InitialGuidePageInfoMananger.PageInfo pageInfo, InitialGuidePageLayout pageLayout) {
        ImageView imageView = (ImageView) pageLayout.findViewById(R.id.initial_guide_page_image);
        if (imageView == null) {
            return;
        }
        imageView.setImageDrawable(this.mContext.getDrawable(pageInfo.mImageResId));
    }

    private void setupVideo(final InitialGuidePageInfoMananger.PageInfo pageInfo, InitialGuidePageLayout pageLayout) {
        final VideoView videoView = (VideoView) pageLayout.findViewById(R.id.initial_guide_page_video_view);
        if (videoView == null) {
            return;
        }
        videoView.setVideoURI(pageInfo.mUri);
        videoView.setAudioFocusRequest(0);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: com.lge.launcher3.initialguide.InitialGuidePagerAdapter.1
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mp) {
                LGLog.i(InitialGuidePagerAdapter.TAG, "onPrepared() pageInfo.mPageType = " + pageInfo.mPageType);
                if (pageInfo.mPageType == InitialGuidePageInfoMananger.PageType.SWIVEL_HOME_INITIAL_GUIDE_MAIN_INTRO) {
                    videoView.start();
                } else {
                    videoView.pause();
                }
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: com.lge.launcher3.initialguide.InitialGuidePagerAdapter.2
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer mp) {
                videoView.seekTo(0);
                videoView.start();
            }
        });
    }

    private void setupDescriptionMain(InitialGuidePageInfoMananger.PageInfo pageInfo, InitialGuidePageLayout pageLayout) {
        TextView textView = (TextView) pageLayout.findViewById(R.id.initial_guide_page_desc_main);
        if (textView == null) {
            return;
        }
        setTextFromResId(textView, pageInfo.mDescMainResId);
    }

    private void setupDescriptionLast(InitialGuidePageInfoMananger.PageInfo pageInfo, InitialGuidePageLayout pageLayout) {
        TextView textView = (TextView) pageLayout.findViewById(R.id.initial_guide_page_desc_last);
        if (textView == null) {
            return;
        }
        if (pageInfo.mDescLastResId.textId == -1) {
            textView.setText("");
        } else {
            setTextFromResId(textView, pageInfo.mDescLastResId);
        }
    }

    private void setupDescriptionSub(InitialGuidePageInfoMananger.PageInfo pageInfo, InitialGuidePageLayout pageLayout) {
        LinearLayout linearLayout;
        if (pageInfo.mDescSubResId == null || (linearLayout = (LinearLayout) pageLayout.findViewById(R.id.initial_guide_page_desc_sub_layout)) == null) {
            return;
        }
        linearLayout.setVisibility(0);
        for (int i : pageInfo.mDescSubResId) {
            LinearLayout linearLayout2 = (LinearLayout) LayoutInflater.from(this.mContext).inflate(R.layout.bullet_textview_layout, (ViewGroup) null);
            if (linearLayout2 != null) {
                TextView textView = (TextView) linearLayout2.findViewById(R.id.bullet_textview_bullet);
                TextView textView2 = (TextView) linearLayout2.findViewById(R.id.bullet_textview_desc);
                if (textView != null && textView2 != null) {
                    String str = "  " + ((Object) textView.getText());
                    if (Utilities.isRtl(this.mContext.getResources())) {
                        str = " " + ((Object) textView.getText()) + " ";
                    }
                    textView.setText(str);
                    textView2.setText(i);
                    linearLayout2.setPadding(linearLayout2.getPaddingLeft(), TextUtils.getLineSpacing(textView2), linearLayout2.getPaddingRight(), linearLayout2.getPaddingBottom());
                    linearLayout.addView(linearLayout2);
                }
            }
        }
    }

    private void setupSecondScreenAppsSettingsButton(InitialGuidePageInfoMananger.PageInfo pageInfo, InitialGuidePageLayout pageLayout) {
        Button button = (Button) pageLayout.findViewById(R.id.initial_guide_second_screen_apps_setting_button);
        if (button == null) {
            return;
        }
        button.setVisibility(0);
        button.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.initialguide.InitialGuidePagerAdapter.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();
                    intent.setClassName("com.android.settings", "com.android.settings.Settings$SubScreenAppSettingsActivity");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setFlags(268435456);
                    InitialGuidePagerAdapter.this.mContext.startActivity(intent);
                    InitialGuidePagerAdapter.this.mContext.sendBroadcast(new Intent(SwivelHomeGuideManager.ACTION_SWIVEL_GUIDE_MAIN_FINISH));
                    InitialGuidePagerAdapter.this.mContext.sendBroadcast(new Intent(SwivelHomeGuideManager.ACTION_SWIVEL_GUIDE_SUB_FINISH));
                } catch (ActivityNotFoundException e) {
                    LGLog.e(InitialGuidePagerAdapter.TAG, String.format("ActivityNotFoundException(%s)", e.getMessage()));
                }
            }
        });
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public void destroyItem(View pager, int position, Object view) {
        ((ViewPager) pager).removeView((View) view);
    }

    @Override // androidx.viewpager.widget.PagerAdapter
    public int getCount() {
        InitialGuidePageInfoMananger initialGuidePageInfoMananger = this.mPageInfoManager;
        if (initialGuidePageInfoMananger != null) {
            return initialGuidePageInfoMananger.getPageCount();
        }
        return 0;
    }

    public void setTextFromResId(TextView tv, InitialGuidePageInfoMananger.TextAndImageRes resId) {
        tv.setText(resId.textId);
        if (resId.imageId != -1) {
            if (resId.imageId2 != -1) {
                setTextWithImage2(tv, resId);
            } else {
                setTextWithImage(tv, resId);
            }
        }
    }

    private void setTextWithImage(TextView tv, InitialGuidePageInfoMananger.TextAndImageRes res) {
        String string = this.mContext.getResources().getString(res.textId, "<img src=\"add_icon1\"/>");
        String string2 = this.mContext.getResources().getString(res.textId, "icon");
        final Drawable drawable = this.mContext.getDrawable(res.imageId);
        if (drawable == null || string == null) {
            return;
        }
        tv.getTextSize();
        tv.getCurrentTextColor();
        tv.setText(Html.fromHtml(string, new Html.ImageGetter() { // from class: com.lge.launcher3.initialguide.InitialGuidePagerAdapter.4
            @Override // android.text.Html.ImageGetter
            public Drawable getDrawable(String source) {
                Drawable drawable2 = drawable;
                drawable2.setBounds(0, 0, drawable2.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }
        }, null));
        tv.setContentDescription(string2);
    }

    private void setTextWithImage2(TextView tv, InitialGuidePageInfoMananger.TextAndImageRes res) {
        boolean z;
        String string = this.mContext.getResources().getString(res.textId, "<img src=\"add_icon1\"/>", "<img src=\"add_icon2\"/>");
        CharSequence string2 = this.mContext.getResources().getString(res.textId, "icon", "icon");
        Drawable[] drawableArr = {this.mContext.getDrawable(res.imageId), this.mContext.getDrawable(res.imageId2)};
        if (drawableArr[0] == null || drawableArr[1] == null || string == null) {
            return;
        }
        Spannable spannableNewSpannable = spannableFactory.newSpannable(string);
        Pattern patternCompile = Pattern.compile("\\Q<img src=\"\\E([a-zA-Z0-9_]+?)\\Q\"/>\\E");
        tv.getTextSize();
        tv.getCurrentTextColor();
        Matcher matcher = patternCompile.matcher(spannableNewSpannable);
        int i = 0;
        while (matcher.find()) {
            for (ImageSpan imageSpan : (ImageSpan[]) spannableNewSpannable.getSpans(matcher.start(), matcher.end(), ImageSpan.class)) {
                if (spannableNewSpannable.getSpanStart(imageSpan) < matcher.start() || spannableNewSpannable.getSpanEnd(imageSpan) > matcher.end()) {
                    z = false;
                    break;
                }
                spannableNewSpannable.removeSpan(imageSpan);
            }
            z = true;
            int i2 = i + 1;
            Drawable drawable = drawableArr[i];
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            if (z) {
                spannableNewSpannable.setSpan(new ImageSpan(drawable), matcher.start(), matcher.end(), 33);
            }
            if (i2 >= 2) {
                break;
            } else {
                i = i2;
            }
        }
        tv.setText(spannableNewSpannable);
        tv.setContentDescription(string2);
    }
}
