package com.lge.launcher3.initialguide;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.viewpager.widget.ViewPager;
import com.android.launcher3.Utilities;
import com.lge.content.LocalBroadcastManager;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class SwivelHomeGuideMainActivity extends Activity {
    public static final String TAG = "SwivelHomeGuideMainActivity";
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.lge.launcher3.initialguide.SwivelHomeGuideMainActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            LGLog.i(SwivelHomeGuideMainActivity.TAG, "onReceive() intent.getAction() = " + intent.getAction());
            String action = intent.getAction();
            action.hashCode();
            switch (action) {
                case "com.lge.launcher3.intent.action.swivel_guide_main_move_next_page":
                    SwivelHomeGuideMainActivity.this.moveNextPage();
                    break;
                case "com.lge.launcher3.intent.action.swivel_guide_main_finish":
                    SwivelHomeGuideMainActivity.this.finish();
                    break;
                case "com.lge.launcher3.intent.action.swivel_guide_main_move_previous_page":
                    SwivelHomeGuideMainActivity.this.movePreviousPage();
                    break;
            }
        }
    };
    private boolean isReceivedIntent = false;
    private ViewPager mPager = null;
    private SwivelHomeGuideMainPageInfoMananger mInfoManager = null;
    private int mPrevSelectedPageIndex = 0;
    private boolean mIsRtL = false;
    private int mSystembarsBehavior = 0;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String str = TAG;
        LGLog.i(str, "onCreate()");
        setContentView(R.layout.swivelhome_initial_guide_main);
        if (isInMultiWindowMode()) {
            Toast.makeText(this, R.string.cannot_open_in_multi_or_popup_window, 0).show();
            LGLog.i(str, "Skip to show the swivel home initial guide in multi window or popup window");
            finish();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SwivelHomeGuideManager.ACTION_SWIVEL_GUIDE_MAIN_MOVE_PREVIOUS_PAGE);
        intentFilter.addAction(SwivelHomeGuideManager.ACTION_SWIVEL_GUIDE_MAIN_MOVE_NEXT_PAGE);
        intentFilter.addAction(SwivelHomeGuideManager.ACTION_SWIVEL_GUIDE_MAIN_FINISH);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(this.mReceiver, intentFilter);
        if (!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            LGLog.i(str, "onCreate() Now Home is not Swing Home. call finish. ");
            finish();
            return;
        }
        WindowInsetsController insetsController = getWindow().getInsetsController();
        if (insetsController != null) {
            this.mSystembarsBehavior = insetsController.getSystemBarsBehavior();
            insetsController.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            insetsController.setSystemBarsBehavior(2);
        }
        if (Utilities.isRtl(getResources())) {
            this.mIsRtL = true;
        }
        this.mInfoManager = new SwivelHomeGuideMainPageInfoMananger(getApplicationContext());
        setupPager();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            return;
        }
        LGLog.i(TAG, "onResume() Now Home is not Swing Home. call finish. ");
        finish();
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        LGLog.d(TAG, "onPause()");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(SwivelHomeGuideManager.ACTION_SWIVEL_GUIDE_SUB_FINISH));
        finish();
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        LGLog.d(TAG, "onBackPressed()");
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        VideoView videoView;
        super.onDestroy();
        LGLog.i(TAG, "onDestroy()");
        if (this.mReceiver != null) {
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(this.mReceiver);
            this.mReceiver = null;
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(SwivelHomeGuideManager.ACTION_SWIVEL_GUIDE_SUB_FINISH));
        int pageCount = getPageCount();
        for (int i = 0; i < pageCount; i++) {
            ViewPager viewPager = this.mPager;
            if (viewPager != null && viewPager.getChildAt(i) != null && (videoView = (VideoView) this.mPager.getChildAt(i).findViewById(R.id.initial_guide_page_video_view)) != null) {
                videoView.stopPlayback();
            }
        }
        SwivelHomeGuideMainPageInfoMananger swivelHomeGuideMainPageInfoMananger = this.mInfoManager;
        if (swivelHomeGuideMainPageInfoMananger != null) {
            swivelHomeGuideMainPageInfoMananger.destroy();
            this.mInfoManager = null;
        }
        WindowInsetsController insetsController = getWindow().getInsetsController();
        if (insetsController != null) {
            insetsController.show(WindowInsets.Type.navigationBars());
            insetsController.setSystemBarsBehavior(this.mSystembarsBehavior);
        }
    }

    private void setupPager() {
        int pageCount = getPageCount();
        ViewPager viewPager = (ViewPager) findViewById(R.id.initial_guide_page);
        this.mPager = viewPager;
        viewPager.setAdapter(new InitialGuidePagerAdapter(getApplicationContext(), this.mInfoManager));
        if (this.mIsRtL) {
            int i = pageCount - 1;
            this.mPager.setCurrentItem(i, true);
            this.mPrevSelectedPageIndex = i;
        }
        this.mPager.setOffscreenPageLimit(pageCount - 1);
        this.mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: com.lge.launcher3.initialguide.SwivelHomeGuideMainActivity.2
            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int state) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrolled(int position, float positionOffest, int positionOffsetPixels) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int position) {
                int pageCount2 = SwivelHomeGuideMainActivity.this.getPageCount();
                LGLog.d(SwivelHomeGuideMainActivity.TAG, "onPageSelected() position = " + position + ", pageCount = " + pageCount2 + ", mPrevSelectedPageIndex = " + SwivelHomeGuideMainActivity.this.mPrevSelectedPageIndex + ", isReceivedIntent = " + SwivelHomeGuideMainActivity.this.isReceivedIntent + ", mIsRtL = " + SwivelHomeGuideMainActivity.this.mIsRtL);
                int i2 = pageCount2 - 1;
                if (position > i2) {
                    return;
                }
                if (!SwivelHomeGuideMainActivity.this.isReceivedIntent) {
                    if ((!SwivelHomeGuideMainActivity.this.mIsRtL && position > SwivelHomeGuideMainActivity.this.mPrevSelectedPageIndex) || (SwivelHomeGuideMainActivity.this.mIsRtL && position < SwivelHomeGuideMainActivity.this.mPrevSelectedPageIndex)) {
                        LocalBroadcastManager.getInstance(SwivelHomeGuideMainActivity.this.getApplicationContext()).sendBroadcast(new Intent(SwivelHomeGuideManager.ACTION_SWIVEL_GUIDE_SUB_MOVE_NEXT_PAGE));
                    } else {
                        LocalBroadcastManager.getInstance(SwivelHomeGuideMainActivity.this.getApplicationContext()).sendBroadcast(new Intent(SwivelHomeGuideManager.ACTION_SWIVEL_GUIDE_SUB_MOVE_PREVIOUS_PAGE));
                    }
                } else {
                    SwivelHomeGuideMainActivity.this.isReceivedIntent = false;
                }
                int i3 = SwivelHomeGuideMainActivity.this.mIsRtL ? i2 - position : position;
                SwivelHomeGuideMainActivity.this.controlVideo(i3, true);
                SwivelHomeGuideMainActivity.this.controlVideo(i3 - 1, false);
                SwivelHomeGuideMainActivity.this.controlVideo(i3 + 1, false);
                SwivelHomeGuideMainActivity.this.mPrevSelectedPageIndex = position;
            }
        });
    }

    private boolean isFirstPage(int position) {
        return position == (this.mIsRtL ? getPageCount() - 1 : 0);
    }

    private boolean isLastPage(int position) {
        return position == (this.mIsRtL ? 0 : getPageCount() - 1);
    }

    public int getPageCount() {
        SwivelHomeGuideMainPageInfoMananger swivelHomeGuideMainPageInfoMananger = this.mInfoManager;
        if (swivelHomeGuideMainPageInfoMananger != null) {
            return swivelHomeGuideMainPageInfoMananger.getPageCount();
        }
        return 0;
    }

    public void movePreviousPage() {
        int currentItem = this.mPager.getCurrentItem();
        if (isFirstPage(currentItem)) {
            return;
        }
        this.isReceivedIntent = true;
        this.mPager.setCurrentItem(this.mIsRtL ? currentItem + 1 : currentItem - 1, true);
    }

    public void moveNextPage() {
        int currentItem = this.mPager.getCurrentItem();
        if (isLastPage(currentItem)) {
            return;
        }
        this.isReceivedIntent = true;
        this.mPager.setCurrentItem(this.mIsRtL ? currentItem - 1 : currentItem + 1, true);
    }

    public void controlVideo(int position, boolean nowStart) {
        int pageCount = getPageCount();
        if (position < 0 || position > pageCount - 1) {
            LGLog.d(TAG, "controlVideo() can't control video. position = " + position + ", total page = " + pageCount);
            return;
        }
        ViewPager viewPager = this.mPager;
        if (viewPager == null || viewPager.getChildAt(position) == null) {
            LGLog.i(TAG, "controlVideo() can't control video. mPager = " + this.mPager);
            return;
        }
        VideoView videoView = (VideoView) this.mPager.getChildAt(position).findViewById(R.id.initial_guide_page_video_view);
        if (videoView == null) {
            LGLog.i(TAG, "controlVideo() can't control video. videoView = " + videoView);
            return;
        }
        if (nowStart) {
            videoView.seekTo(1);
            videoView.start();
        } else {
            videoView.pause();
        }
    }
}
