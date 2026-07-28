package com.android.launcher3.widget;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.CancellationSignal;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.SimpleOnStylusPressListener;
import com.android.launcher3.StylusEventHelper;
import com.android.launcher3.WidgetPreviewLoader;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.dragndrop.LivePreviewWidgetCell;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.model.data.ItemInfo;
import com.lge.launcher3.R;
import com.lge.launcher3.widgettray.LGWidgetCell;
import com.lge.launcher3.widgettray.PreviewAppliable;

/* JADX INFO: loaded from: classes.dex */
public class WidgetCell extends LinearLayout implements View.OnLayoutChangeListener, PreviewAppliable {
    private static final boolean DEBUG = false;
    private static final int FADE_IN_DURATION_MS = 90;
    private static final float PREVIEW_SCALE = 0.8f;
    private static final String TAG = "WidgetCell";
    protected static final float WIDTH_SCALE = 3.2f;
    protected WidgetPreviewLoader.PreviewLoadRequest mActiveRequest;
    protected CancellationSignal mActiveRequestforBottomUp;
    private boolean mAnimatePreview;
    public int mCellSize;
    protected String mDimensionsFormatString;
    public Object mInfo;
    public WidgetItem mItem;
    protected BaseActivity mLauncher;
    protected int mPresetPreviewSize;
    private StylusEventHelper mStylusEventHelper;
    protected TextView mWidgetDims;
    protected WidgetImageView mWidgetImage;
    protected TextView mWidgetName;
    protected WidgetPreviewLoader mWidgetPreviewLoader;

    public WidgetCell(Context context) {
        this(context, null);
    }

    public WidgetCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WidgetCell(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mAnimatePreview = true;
        Resources resources = context.getResources();
        this.mLauncher = BaseActivity.fromContext(context);
        this.mStylusEventHelper = new StylusEventHelper(new SimpleOnStylusPressListener(this), this);
        this.mDimensionsFormatString = resources.getString(R.string.widget_dims_format);
        setContainerWidth();
        setWillNotDraw(false);
        setClipToPadding(false);
        setAccessibilityDelegate(LauncherAppState.getInstance(context).getAccessibilityDelegate());
    }

    protected void setContainerWidth() {
        int i = (int) (this.mLauncher.getDeviceProfile().cellWidthPx * WIDTH_SCALE);
        this.mCellSize = i;
        this.mPresetPreviewSize = (int) (i * 0.8f);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mWidgetImage = (WidgetImageView) findViewById(R.id.widget_preview);
        this.mWidgetName = (TextView) findViewById(R.id.widget_name);
        this.mWidgetDims = (TextView) findViewById(R.id.widget_dims);
    }

    public void clear() {
        this.mWidgetImage.animate().cancel();
        if (this.mWidgetImage.getBitmap() != null) {
            this.mWidgetImage.getBitmap().recycle();
        }
        this.mWidgetImage.setPaddingWidgetImageView(0);
        this.mWidgetImage.setBitmap(null, null);
        this.mWidgetName.setText((CharSequence) null);
        this.mWidgetDims.setText((CharSequence) null);
        WidgetPreviewLoader.PreviewLoadRequest previewLoadRequest = this.mActiveRequest;
        if (previewLoadRequest != null) {
            previewLoadRequest.cleanup();
            this.mActiveRequest = null;
        }
        CancellationSignal cancellationSignal = this.mActiveRequestforBottomUp;
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
            this.mActiveRequestforBottomUp = null;
        }
    }

    public void applyFromAppWidgetProviderInfo(LauncherAppWidgetProviderInfo info, WidgetPreviewLoader loader) {
        InvariantDeviceProfile idp = LauncherAppState.getIDP(getContext());
        this.mInfo = info;
        this.mWidgetName.setText(AppWidgetManagerCompat.getInstance(getContext()).loadLabel(info));
        this.mWidgetDims.setText(String.format(this.mDimensionsFormatString, Integer.valueOf(Math.min(info.getSpanX(Launcher.getLauncher(this.mLauncher)), idp.numColumns)), Integer.valueOf(Math.min(info.getSpanY(Launcher.getLauncher(this.mLauncher)), idp.numRows))));
        this.mWidgetPreviewLoader = loader;
    }

    public void applyFromResolveInfo(PackageManager pm, ResolveInfo info, WidgetPreviewLoader loader) {
        this.mInfo = info;
        this.mWidgetName.setText(info.loadLabel(pm));
        this.mWidgetDims.setText(String.format(this.mDimensionsFormatString, 1, 1));
        this.mWidgetPreviewLoader = loader;
    }

    public int[] getPreviewSize() {
        int i = this.mPresetPreviewSize;
        return new int[]{i, i};
    }

    @Override // com.lge.launcher3.widgettray.PreviewAppliable
    public void applyPreview(Bitmap bitmap) {
        applyPreview(bitmap, true);
    }

    public void applyPreview(Bitmap bitmap, boolean animate) {
        if (bitmap != null) {
            this.mWidgetImage.setBitmap(bitmap, null);
            if (this.mAnimatePreview) {
                this.mWidgetImage.setAlpha(0.0f);
                this.mWidgetImage.animate().alpha(1.0f).setDuration(90L);
            } else {
                this.mWidgetImage.setAlpha(1.0f);
            }
        }
    }

    public void ensurePreview() {
        if (this.mActiveRequest != null) {
            return;
        }
        int[] previewSize = getPreviewSize();
        Object obj = this.mInfo;
        if (obj == null) {
            this.mActiveRequest = this.mWidgetPreviewLoader.getPreview(this.mItem, previewSize[0], previewSize[1], this);
        } else {
            this.mActiveRequest = this.mWidgetPreviewLoader.getPreview(obj, previewSize[0], previewSize[1], this);
        }
    }

    public void ensurePreview(boolean animate) {
        if (this.mActiveRequestforBottomUp != null) {
            return;
        }
        WidgetPreviewLoader widgetPreviewLoader = this.mWidgetPreviewLoader;
        WidgetItem widgetItem = this.mItem;
        int i = this.mPresetPreviewSize;
        this.mActiveRequestforBottomUp = widgetPreviewLoader.getPreview(widgetItem, i, i, this, animate);
    }

    @Override // android.view.View.OnLayoutChangeListener
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        removeOnLayoutChangeListener(this);
        ensurePreview();
    }

    public int getActualItemWidth() {
        ItemInfo itemInfo = (ItemInfo) getTag();
        return Math.min(getPreviewSize()[0], itemInfo.spanX * this.mLauncher.getDeviceProfile().cellWidthPx);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        boolean zOnTouchEvent = super.onTouchEvent(ev);
        if (this.mStylusEventHelper.onMotionEvent(ev)) {
            return true;
        }
        return zOnTouchEvent;
    }

    protected String getTagToString() {
        return ((getTag() instanceof PendingAddWidgetInfo) || (getTag() instanceof PendingAddShortcutInfo)) ? getTag().toString() : "";
    }

    public void applyFromCellItem(WidgetItem item, WidgetPreviewLoader loader) {
        this.mItem = item;
        this.mWidgetName.setText(item.label);
        this.mWidgetName.setTextColor(getResources().getColor(R.color.bottomup_widget_text_color));
        this.mWidgetDims.setText(getContext().getString(R.string.widget_dims_format, Integer.valueOf(this.mItem.spanX), Integer.valueOf(this.mItem.spanY)));
        this.mWidgetDims.setContentDescription(getContext().getString(R.string.widget_dims_format, Integer.valueOf(this.mItem.spanX), Integer.valueOf(this.mItem.spanY)));
        this.mWidgetDims.setTextColor(getResources().getColor(R.color.bottomup_widget_text_color));
        this.mWidgetPreviewLoader = loader;
        if (item.activityInfo != null) {
            setTag(new PendingAddShortcutInfo(item.activityInfo));
        } else {
            setTag(new PendingAddWidgetInfo(Launcher.getLauncher(getContext()), item.widgetInfo, null));
        }
    }

    public WidgetImageView getWidgetView() {
        return this.mWidgetImage;
    }

    public void setAnimatePreview(boolean shouldAnimate) {
        this.mAnimatePreview = shouldAnimate;
    }

    @Override // android.view.View
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (!(this instanceof LGWidgetCell) && !(this instanceof LivePreviewWidgetCell)) {
            int i = this.mCellSize;
            params.height = i;
            params.width = i;
        }
        super.setLayoutParams(params);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return WidgetCell.class.getName();
    }

    public void setPaddingWidgetInBottomUp(int padding) {
        this.mWidgetImage.setPaddingWidgetImageView(padding);
    }

    public void setWidgetCellSize(int cellSize) {
        this.mCellSize = cellSize;
    }

    public void setHeightForBottomSheet() {
        getLayoutParams().height += this.mWidgetName.getLineHeight() + this.mWidgetDims.getLineHeight();
    }
}
