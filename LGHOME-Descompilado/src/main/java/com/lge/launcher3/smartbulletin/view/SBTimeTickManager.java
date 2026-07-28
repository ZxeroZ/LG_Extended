package com.lge.launcher3.smartbulletin.view;

import android.view.View;
import android.widget.ImageView;
import com.lge.launcher3.R;
import java.util.Calendar;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class SBTimeTickManager {
    private ImageView mBackgroundWallpaper;
    private SBCategoryLayout mCategoryLayout;
    private View mNoProviderView;
    private HashMap<CustomState, ItemRes> mItemMap = new HashMap<>();
    private CustomState mState = CustomState.STATE00;

    private enum CustomState {
        STATE00,
        STATE01,
        STATE02,
        STATE03,
        STATE04,
        STATE05
    }

    private class ItemRes {
        private int mMessageId;
        private int mWallpaperId;

        ItemRes(int messageId, int wallpaperId) {
            this.mMessageId = messageId;
            this.mWallpaperId = wallpaperId;
        }

        public int getMessageId() {
            return this.mMessageId;
        }

        public int getWallpaperId() {
            return this.mWallpaperId;
        }
    }

    public SBTimeTickManager(View noproviderview, SBCategoryLayout categoryLayout, ImageView wallpaper) {
        this.mCategoryLayout = null;
        this.mBackgroundWallpaper = null;
        this.mNoProviderView = null;
        this.mNoProviderView = noproviderview;
        this.mCategoryLayout = categoryLayout;
        this.mBackgroundWallpaper = wallpaper;
        initResource();
    }

    private void initResource() {
        this.mItemMap.put(CustomState.STATE01, new ItemRes(R.string.smartbulletin_header_greeting_message01, R.drawable.smartbulletin_bg_dawn));
        this.mItemMap.put(CustomState.STATE02, new ItemRes(R.string.smartbulletin_header_greeting_message02, R.drawable.smartbulletin_bg_morning));
        this.mItemMap.put(CustomState.STATE03, new ItemRes(R.string.smartbulletin_header_greeting_message03, R.drawable.smartbulletin_bg_afternoon));
        this.mItemMap.put(CustomState.STATE04, new ItemRes(R.string.smartbulletin_header_greeting_message04, R.drawable.smartbulletin_bg_evening));
        this.mItemMap.put(CustomState.STATE05, new ItemRes(R.string.smartbulletin_header_special_message, R.drawable.smartbulletin_bg_happynewyear));
    }

    private CustomState getCustomStateNow() {
        CustomState customState;
        Calendar calendar = Calendar.getInstance();
        int i = calendar.get(6);
        int i2 = calendar.get(11);
        if (i2 >= 0 && i2 < 6) {
            customState = CustomState.STATE01;
        } else if (i2 >= 6 && i2 < 12) {
            customState = CustomState.STATE02;
        } else if (i2 >= 18 && i2 < 24) {
            customState = CustomState.STATE04;
        } else {
            customState = CustomState.STATE03;
        }
        return i == 1 ? CustomState.STATE05 : customState;
    }

    public void updateHeaderMessage() {
        CustomState customStateNow = getCustomStateNow();
        if (customStateNow == this.mState) {
            return;
        }
        this.mState = customStateNow;
        if (this.mItemMap.isEmpty()) {
            return;
        }
        updateHeaderBackground();
    }

    public void updateHeaderBackground() {
        if (this.mCategoryLayout.getChildCount() == 0) {
            if (this.mItemMap.isEmpty()) {
                return;
            }
            this.mBackgroundWallpaper.setImageResource(this.mItemMap.get(this.mState).getWallpaperId());
            this.mNoProviderView.setVisibility(0);
            this.mCategoryLayout.setVisibility(8);
            return;
        }
        if (this.mItemMap.isEmpty()) {
            return;
        }
        this.mBackgroundWallpaper.setImageResource(this.mItemMap.get(this.mState).getWallpaperId());
        this.mNoProviderView.setVisibility(8);
        this.mCategoryLayout.setVisibility(0);
    }
}
