package com.lge.launcher3.allapps;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.FocusIndicatorView;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsTextViewMngr {
    private View.OnClickListener mClickListener;
    private final Context mContext;
    public DeviceProfile mDeviceProfile;
    private FocusIndicatorView mFocusIndicatorView;
    private View.OnLongClickListener mLongClickListener;
    private View.OnTouchListener mTouchListener;

    public AllAppsTextViewMngr(Context context, ViewGroup rootView) {
        this.mContext = context;
        this.mDeviceProfile = ((AllAppsPagedView) rootView).mLauncher.getDeviceProfile();
    }

    public void setClickListener(View.OnClickListener ocl, View.OnLongClickListener olcl, View.OnTouchListener otl, FocusIndicatorView fiv) {
        this.mClickListener = ocl;
        this.mLongClickListener = olcl;
        this.mTouchListener = otl;
        this.mFocusIndicatorView = fiv;
    }

    public BubbleTextView createMenuTextView(AllAppsItemInfo itemInfo, Bitmap bitmap) {
        BubbleTextView bubbleTextView;
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            bubbleTextView = (BubbleTextView) LayoutInflater.from(this.mContext).inflate(R.layout.all_apps_paged_view_application_swivel_home, (ViewGroup) null, false);
        } else {
            bubbleTextView = (BubbleTextView) LayoutInflater.from(this.mContext).inflate(R.layout.all_apps_paged_view_application, (ViewGroup) null, false);
        }
        bubbleTextView.setOnLongClickListener(this.mLongClickListener);
        bubbleTextView.setOnTouchListener(this.mTouchListener);
        bubbleTextView.setOnClickListener(this.mClickListener);
        bubbleTextView.setOnFocusChangeListener(this.mFocusIndicatorView);
        bubbleTextView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.lge.launcher3.allapps.AllAppsTextViewMngr.1
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                ((BubbleTextView) v).highlightSearchText((AllAppsItemInfo) v.getTag());
            }
        });
        bubbleTextView.applyFromApplicationInfo(itemInfo);
        bubbleTextView.setCompoundDrawablePadding(this.mDeviceProfile.iconDrawablePaddingPx);
        itemInfo.itemView = bubbleTextView;
        return bubbleTextView;
    }
}
