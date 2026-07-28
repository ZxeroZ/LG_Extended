package com.lge.launcher3.pageindicator;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.CellLayout;
import com.android.launcher3.PageIndicator;
import com.android.launcher3.PageIndicatorMarker;
import com.android.launcher3.Workspace;
import com.android.launcher3.dragndrop.DragLayer;
import com.lge.launcher3.R;
import com.lge.launcher3.adaptive.AdaptiveTextUtil;
import com.lge.launcher3.operator.GoogleNowManager;
import com.lge.launcher3.operator.VZWSideScreenManager;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class PageIndicatorExtension extends PageIndicator {
    private static final String TAG = "PageIndicatorExtension";
    private int mIsGoogleNowEnabled;
    public PageIndicatorListener mListener;
    public int mMakerPadding;
    public int[] mMarkerClickIndexArray;
    private SharedPreferences.OnSharedPreferenceChangeListener mPageIndicatorListener;
    public int mParentType;

    public PageIndicatorExtension(Context context) {
        this(context, null);
    }

    public PageIndicatorExtension(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicatorExtension(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mIsGoogleNowEnabled = -1;
        this.mMarkerClickIndexArray = new int[this.mMaxWindowSize];
    }

    public void setListener(PageIndicatorListener listener) {
        this.mListener = listener;
    }

    public void preUpdateMarkerClickListner() {
        updateMarkerClickListner(this.mWindowRange[0], this.mWindowRange[1]);
    }

    public void updateMarkerClickListner(int start, int end) {
        int size = this.mMarkers.size();
        setMarkerClickIndex();
        int i = 0;
        if (size <= this.mMaxWindowSize) {
            while (i < size) {
                PageIndicatorMarkerExtension pageIndicatorMarkerExtension = (PageIndicatorMarkerExtension) this.mMarkers.get(i);
                MarkerOnClickListener markerOnClickListener = new MarkerOnClickListener(i, this.mListener, this.mMarkerClickIndexArray);
                if (!pageIndicatorMarkerExtension.getIsAddIconMaker()) {
                    pageIndicatorMarkerExtension.setOnClickListener(markerOnClickListener);
                }
                i++;
            }
            return;
        }
        while (start < end) {
            PageIndicatorMarkerExtension pageIndicatorMarkerExtension2 = (PageIndicatorMarkerExtension) this.mMarkers.get(start);
            MarkerOnClickListener markerOnClickListener2 = new MarkerOnClickListener(i, this.mListener, this.mMarkerClickIndexArray);
            if (!pageIndicatorMarkerExtension2.getIsAddIconMaker()) {
                pageIndicatorMarkerExtension2.setOnClickListener(markerOnClickListener2);
            }
            i++;
            start++;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x0069  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void setMarkerClickIndex() {
        /*
            r11 = this;
            java.util.ArrayList<com.android.launcher3.PageIndicatorMarker> r0 = r11.mMarkers
            int r0 = r0.size()
            int r1 = r11.mMaxWindowSize
            r2 = 0
            r3 = 1
            if (r0 <= r1) goto L6e
            int[] r1 = r11.mMarkerClickIndexArray
            int r1 = r1.length
            int r1 = r1 / 2
            boolean r4 = r11.hasExtensionView()
            if (r4 == 0) goto L1a
            int r4 = r1 + (-1)
            goto L1b
        L1a:
            r4 = r1
        L1b:
            r5 = r2
        L1c:
            if (r5 >= r1) goto L32
            boolean r6 = r11.hasExtensionView()
            if (r6 == 0) goto L2b
            int[] r6 = r11.mMarkerClickIndexArray
            int r7 = r5 + (-1)
            r6[r5] = r7
            goto L2f
        L2b:
            int[] r6 = r11.mMarkerClickIndexArray
            r6[r5] = r5
        L2f:
            int r5 = r5 + 1
            goto L1c
        L32:
            int r5 = r11.mMaxWindowSize
            r6 = r0
            r7 = r3
        L36:
            int r8 = r0 - r1
            if (r6 <= r8) goto L51
            int[] r8 = r11.mMarkerClickIndexArray
            int r9 = r5 - r7
            boolean r10 = r11.hasExtensionView()
            if (r10 == 0) goto L48
            int r10 = r0 - r7
            int r10 = r10 - r3
            goto L4a
        L48:
            int r10 = r0 - r7
        L4a:
            r8[r9] = r10
            int r7 = r7 + 1
            int r6 = r6 + (-1)
            goto L36
        L51:
            int r6 = r11.mActiveMarkerIndex
            int[] r7 = r11.mMarkerClickIndexArray
            int r8 = r1 + (-1)
            r7 = r7[r8]
            if (r6 <= r7) goto L69
            int r6 = r11.mActiveMarkerIndex
            int[] r7 = r11.mMarkerClickIndexArray
            int r5 = r5 - r1
            r5 = r7[r5]
            if (r6 >= r5) goto L69
            int r4 = r11.mActiveMarkerIndex
            r7[r1] = r4
            goto L8e
        L69:
            int[] r5 = r11.mMarkerClickIndexArray
            r5[r1] = r4
            goto L8e
        L6e:
            r1 = r2
        L6f:
            if (r1 >= r0) goto L8e
            boolean r4 = r11.hasExtensionView()
            if (r4 == 0) goto L87
            if (r1 != 0) goto L80
            int[] r4 = r11.mMarkerClickIndexArray
            r5 = -401(0xfffffffffffffe6f, float:NaN)
            r4[r1] = r5
            goto L8b
        L80:
            int[] r4 = r11.mMarkerClickIndexArray
            int r5 = r1 + (-1)
            r4[r1] = r5
            goto L8b
        L87:
            int[] r4 = r11.mMarkerClickIndexArray
            r4[r1] = r1
        L8b:
            int r1 = r1 + 1
            goto L6f
        L8e:
            r1 = r2
        L8f:
            if (r1 >= r0) goto Le1
            int[] r4 = r11.mWindowRange
            r4 = r4[r2]
            if (r1 < r4) goto Lde
            int[] r4 = r11.mWindowRange
            r4 = r4[r3]
            int r4 = r4 - r3
            if (r1 <= r4) goto L9f
            goto Lde
        L9f:
            java.util.ArrayList<com.android.launcher3.PageIndicatorMarker> r4 = r11.mMarkers
            java.lang.Object r4 = r4.get(r1)
            com.lge.launcher3.pageindicator.PageIndicatorMarkerExtension r4 = (com.lge.launcher3.pageindicator.PageIndicatorMarkerExtension) r4
            boolean r5 = r11.hasExtensionView()
            if (r5 == 0) goto Lb9
            int[] r5 = r11.mWindowRange
            r5 = r5[r2]
            if (r1 != r5) goto Lb9
            java.lang.String r5 = ""
            r4.setContentDescription(r5)
            goto Lde
        Lb9:
            int[] r5 = r11.mWindowRange
            r5 = r5[r2]
            int r5 = r1 - r5
            android.content.Context r6 = r11.getContext()
            r7 = 2131821210(0x7f11029a, float:1.9275157E38)
            java.lang.String r6 = r6.getString(r7)
            java.lang.Object[] r7 = new java.lang.Object[r3]
            int[] r8 = r11.mMarkerClickIndexArray
            r5 = r8[r5]
            int r5 = r5 + r3
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r7[r2] = r5
            java.lang.String r5 = java.lang.String.format(r6, r7)
            r4.setContentDescription(r5)
        Lde:
            int r1 = r1 + 1
            goto L8f
        Le1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.pageindicator.PageIndicatorExtension.setMarkerClickIndex():void");
    }

    public void setTypePadding(int makerPadding) {
        int i = this.mMakerPadding;
        this.mMakerPadding = makerPadding;
        if (i != makerPadding) {
            for (PageIndicatorMarker pageIndicatorMarker : this.mMarkers) {
                pageIndicatorMarker.setPadding(pageIndicatorMarker.getPaddingLeft(), this.mMakerPadding, pageIndicatorMarker.getPaddingRight(), this.mMakerPadding);
            }
        }
    }

    public void setMarkerLongClicklistenr(View.OnLongClickListener listener) {
        Iterator<PageIndicatorMarker> it = this.mMarkers.iterator();
        while (it.hasNext()) {
            it.next().setOnLongClickListener(listener);
        }
    }

    @Override // com.android.launcher3.PageIndicator
    public void removeMarker(int index, boolean allowAnimations) {
        super.removeMarker(convertIndexWithExtensionView(index), allowAnimations);
        Iterator<PageIndicatorMarker> it = this.mMarkers.iterator();
        while (it.hasNext()) {
            it.next().setVisibility(this.mMarkers.size() > 1 ? 0 : 4);
        }
    }

    public PageIndicator.PageMarkerResources getExtensionMarker() {
        if (GoogleNowManager.isAvailable(getContext()) && GoogleNowManager.isAppEnabled()) {
            return GoogleNowManager.getMarker();
        }
        if (VZWSideScreenManager.isAvailable() && VZWSideScreenManager.isAppEnabled()) {
            return VZWSideScreenManager.getMarker();
        }
        return null;
    }

    public void updateMarkerToMatchScreen() {
        if (!(getParent() instanceof DragLayer) || this.mMarkerClickIndexArray.length == 0) {
            return;
        }
        DragLayer dragLayer = (DragLayer) getParent();
        int i = 0;
        for (int i2 = this.mWindowRange[0]; i2 < this.mWindowRange[1]; i2++) {
            if (i2 < this.mMarkers.size() && i < this.mMarkerClickIndexArray.length) {
                if (hasExtensionView() && getExtensionMarker() != null && i2 == this.mWindowRange[0]) {
                    updateMarker(i2, getExtensionMarker());
                } else {
                    updateMarker(i2, dragLayer.mLauncher.getWorkspace().getPageIndicatorMarker(this.mMarkerClickIndexArray[i]));
                }
                i++;
            }
        }
    }

    @Override // com.android.launcher3.PageIndicator
    protected void offsetWindowCenterTo(int activeIndex, boolean allowAnimations) {
        super.offsetWindowCenterTo(convertIndexWithExtensionView(activeIndex), allowAnimations);
        preUpdateMarkerClickListner();
        updateMarkerToMatchScreen();
    }

    @Override // com.android.launcher3.PageIndicator
    public void addMarker(int index, PageIndicator.PageMarkerResources marker, boolean allowAnimations) {
        if (index != Integer.MAX_VALUE && marker != GoogleNowManager.getMarker() && marker != VZWSideScreenManager.getMarker()) {
            index = convertIndexWithExtensionView(index);
        }
        int iMax = Math.max(0, Math.min(index, this.mMarkers.size()));
        PageIndicatorMarkerExtension pageIndicatorMarkerExtension = (PageIndicatorMarkerExtension) this.mLayoutInflater.inflate(R.layout.page_indicator_marker, (ViewGroup) this, false);
        pageIndicatorMarkerExtension.setMarkerDrawables(marker.activeId, marker.inactiveId);
        this.mMarkers.add(iMax, pageIndicatorMarkerExtension);
        offsetWindowCenterTo(this.mActiveMarkerIndex, allowAnimations);
        int dimension = (int) getResources().getDimension(R.dimen.device_profile_pageIndicator_padding);
        int i = this.mMakerPadding;
        pageIndicatorMarkerExtension.setPadding(dimension, i, dimension, i);
        pageIndicatorMarkerExtension.setColor(marker.mActiveColor, marker.mInactiveColor);
        pageIndicatorMarkerExtension.setMarkerResource(marker);
        if (VZWSideScreenManager.isAvailable() && VZWSideScreenManager.isAppEnabled()) {
            VZWSideScreenManager.setMarkerColor(marker.mActiveColor, marker.mInactiveColor);
            addVZWSideScreenMarker();
        }
        Iterator<PageIndicatorMarker> it = this.mMarkers.iterator();
        while (it.hasNext()) {
            it.next().setVisibility(this.mMarkers.size() > 1 ? 0 : 4);
        }
    }

    @Override // com.android.launcher3.PageIndicator
    public void updateMarker(int index, PageIndicator.PageMarkerResources marker) {
        if (this.mMarkers != null && index >= this.mMarkers.size()) {
            LGLog.e(TAG, "The index is an exceed value. index :" + index + ", Markers size :" + this.mMarkers.size());
            index = this.mMarkers.size() + (-1);
        }
        super.updateMarker(index, marker);
        PageIndicatorMarkerExtension pageIndicatorMarkerExtension = (PageIndicatorMarkerExtension) this.mMarkers.get(index);
        if (Utilities.isLGUI7_1()) {
            if (marker.mActiveColor != 0 && (getParent() instanceof DragLayer) && !((DragLayer) getParent()).mLauncher.getWorkspace().getState().equals(Workspace.State.NORMAL)) {
                int color = getContext().getResources().getColor(R.color.workspace_adaptive_color1);
                pageIndicatorMarkerExtension.setColor(color, color);
                return;
            }
            if (isGoogleNowEnabled() && GoogleNowManager.isAppEnabled()) {
                GoogleNowManager.setMarkerColor(marker.mActiveColor, marker.mInactiveColor);
            } else if (VZWSideScreenManager.isAvailable() && VZWSideScreenManager.isAppEnabled()) {
                VZWSideScreenManager.setMarkerColor(marker.mActiveColor, marker.mInactiveColor);
            }
            pageIndicatorMarkerExtension.setColor(marker.mActiveColor, marker.mInactiveColor);
            return;
        }
        pageIndicatorMarkerExtension.setColor(marker.mActiveColor, marker.mInactiveColor);
    }

    public void resetIsGoogleNowEnabled() {
        this.mIsGoogleNowEnabled = -1;
    }

    private boolean isGoogleNowEnabled() {
        if (this.mIsGoogleNowEnabled == -1) {
            this.mIsGoogleNowEnabled = GoogleNowManager.isAvailable(getContext()) ? 1 : 0;
        }
        return this.mIsGoogleNowEnabled == 1;
    }

    private int convertIndexWithExtensionView(int index) {
        return hasExtensionView() ? index + 1 : index;
    }

    private int revertIndexWithExtensionView(int index) {
        return hasExtensionView() ? index - 1 : index;
    }

    private boolean hasExtensionView() {
        if (!hasPreview() && (getParent() instanceof DragLayer)) {
            if (isGoogleNowEnabled() && GoogleNowManager.isAppEnabled()) {
                return true;
            }
            if (VZWSideScreenManager.isAvailable() && VZWSideScreenManager.isAppEnabled()) {
                return true;
            }
        }
        return false;
    }

    public void addGoogleNowMarker() {
        if ((getParent() instanceof DragLayer) && !existGoogleNowMarker()) {
            if (this.mActiveMarkerIndex >= 0) {
                this.mActiveMarkerIndex++;
            }
            addMarker(0, GoogleNowManager.getMarker(), true);
        }
    }

    public boolean existGoogleNowMarker() {
        return this.mMarkers != null && this.mMarkers.size() > 0 && ((PageIndicatorMarkerExtension) this.mMarkers.get(0)).getMarkerResource() == GoogleNowManager.getMarker();
    }

    public void removeGoogleNowMarker() {
        if ((getParent() instanceof DragLayer) && existGoogleNowMarker()) {
            if (this.mActiveMarkerIndex >= 0) {
                this.mActiveMarkerIndex--;
            }
            removeMarker(0, true);
        }
    }

    public void addVZWSideScreenMarker() {
        if ((getParent() instanceof DragLayer) && !existVZWSideScreenMarker()) {
            addMarker(0, VZWSideScreenManager.getMarker(), true);
        }
    }

    private boolean existVZWSideScreenMarker() {
        return this.mMarkers != null && this.mMarkers.size() > 0 && ((PageIndicatorMarkerExtension) this.mMarkers.get(0)).getMarkerResource() == VZWSideScreenManager.getMarker();
    }

    public void removeVZWSideScreenMarker() {
        if ((getParent() instanceof DragLayer) && existVZWSideScreenMarker()) {
            removeMarker(0, false);
        }
    }

    @Override // com.android.launcher3.PageIndicator
    public void addMarkers(ArrayList<PageIndicator.PageMarkerResources> markers, boolean allowAnimations) {
        super.addMarkers(markers, allowAnimations);
        Iterator<PageIndicatorMarker> it = this.mMarkers.iterator();
        while (it.hasNext()) {
            it.next().setVisibility(this.mMarkers.size() > 1 ? 0 : 4);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (Utilities.isLGUI7_1()) {
            this.mPageIndicatorListener = new SharedPreferences.OnSharedPreferenceChangeListener() { // from class: com.lge.launcher3.pageindicator.PageIndicatorExtension.1
                @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    int color;
                    if (SharedPreferencesManager.toKeyString(SharedPreferencesConst.AdaptiveTextKey.TEXT_COLOR).equals(key)) {
                        LGLog.i(PageIndicatorExtension.TAG, String.format("onSharedPreferenceChanged: (%s)", key));
                        if (DDTUtils.isAdditionalThemeApplied(PageIndicatorExtension.this.getContext())) {
                            color = 0;
                        } else if (AdaptiveTextUtil.isDarkColor(AdaptiveTextUtil.getAdaptiveTextColor(PageIndicatorExtension.this.getContext()))) {
                            color = PageIndicatorExtension.this.getContext().getResources().getColor(R.color.workspace_adaptive_color2);
                        } else {
                            color = PageIndicatorExtension.this.getContext().getResources().getColor(R.color.workspace_adaptive_color1);
                        }
                        for (int i = 0; i < PageIndicatorExtension.this.getChildCount(); i++) {
                            PageIndicatorMarkerExtension pageIndicatorMarkerExtension = (PageIndicatorMarkerExtension) PageIndicatorExtension.this.mMarkers.get(i);
                            if (PageIndicatorExtension.this.getParent() instanceof DragLayer) {
                                pageIndicatorMarkerExtension.getMarkerResource().mActiveColor = color;
                                pageIndicatorMarkerExtension.getMarkerResource().mInactiveColor = color;
                            }
                            PageIndicatorExtension.this.updateMarker(i, pageIndicatorMarkerExtension.getMarkerResource());
                        }
                    }
                }
            };
            SharedPreferencesManager.registerOnSharedPreferenceChangeListener(getContext(), 0, this.mPageIndicatorListener);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (Utilities.isLGUI7_1()) {
            SharedPreferencesManager.unregisterOnSharedPreferenceChangeListener(getContext(), 0, this.mPageIndicatorListener);
        }
    }

    public boolean hasPreview() {
        Workspace workspace;
        if (getParent() instanceof DragLayer) {
            DragLayer dragLayer = (DragLayer) getParent();
            if (dragLayer.mLauncher != null && (workspace = dragLayer.mLauncher.getWorkspace()) != null && (workspace.getChildAt(0) instanceof CellLayout) && ((CellLayout) workspace.getChildAt(0)).getMinusOneScreenPreview() != null) {
                return true;
            }
        }
        return false;
    }
}
