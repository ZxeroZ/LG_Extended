package com.lge.launcher3.widgettray;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.launcher3.BaseActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.WidgetPreviewLoader;
import com.android.launcher3.widget.WidgetCell;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LGFeatureConfig;
import com.lge.launcher3.util.DDTUtils;

/* JADX INFO: loaded from: classes.dex */
public class LGWidgetCell extends WidgetCell {
    private static final boolean DEBUG = false;
    private static final String TAG = "LGWidgetCell";
    private SparseArray<WidgetPreviewLoader.PreviewLoadRequest> mRequests;
    private TextView mWidgetDebugText;
    protected LinearLayout mWidgetGroupImage;

    public LGWidgetCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mRequests = new SparseArray<>();
    }

    public LGWidgetCell(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mRequests = new SparseArray<>();
    }

    @Override // com.android.launcher3.widget.WidgetCell, android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mWidgetGroupImage = (LinearLayout) findViewById(R.id.widget_group_preview);
        this.mWidgetDebugText = (TextView) findViewById(R.id.widget_preview_debug_text);
    }

    @Override // com.android.launcher3.widget.WidgetCell
    public void applyFromAppWidgetProviderInfo(LauncherAppWidgetProviderInfo info, WidgetPreviewLoader loader) {
        super.applyFromAppWidgetProviderInfo(info, loader);
        if (LGFeatureConfig.sDebugWidgetSize) {
            float f = getContext().getResources().getDisplayMetrics().density;
            this.mWidgetDebugText.setText("mw:" + (info.minWidth / f) + "  \nmh:" + (info.minHeight / f) + "  \nmrw:" + (info.minResizeWidth / f) + "  \nmrh:" + (info.minResizeHeight / f) + "  \nresizeMode:" + info.resizeMode + "  ");
        }
    }

    @Override // com.android.launcher3.widget.WidgetCell
    protected void setContainerWidth() {
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        this.mCellSize = (int) (deviceProfile.cellWidthPx * 3.2f);
        int integer = getResources().getInteger(R.integer.widget_tray_col_port);
        if (deviceProfile.allowRotation && deviceProfile.isLandscape) {
            integer = getResources().getInteger(R.integer.widget_tray_col_land);
        }
        this.mPresetPreviewSize = deviceProfile.availableWidthPx / integer;
    }

    public Object getInfo() {
        return this.mInfo;
    }

    public Bitmap getPreview(Object info) {
        int[] previewSize = getPreviewSize();
        BaseActivity baseActivityFromContext = BaseActivity.fromContext(getContext());
        if (info instanceof LauncherAppWidgetProviderInfo) {
            return this.mWidgetPreviewLoader.generateWidgetPreview(baseActivityFromContext, (LauncherAppWidgetProviderInfo) info, previewSize[0], null, null);
        }
        return this.mWidgetPreviewLoader.generateShortcutPreview(baseActivityFromContext, (ResolveInfo) info, previewSize[0], previewSize[1], (Bitmap) null);
    }

    public void setWidgetName(String str) {
        if (this.mWidgetName != null) {
            this.mWidgetName.setText(str);
        }
    }

    public void setWidgetDims(String str) {
        if (this.mWidgetDims != null) {
            this.mWidgetDims.setText(str);
        }
    }

    public void ensureGroupItemPreview(int itemNum, Object info) {
        GroupItemPreview groupItemPreview;
        if (this.mRequests.get(itemNum) != null) {
            return;
        }
        int identifier = this.mLauncher.getResources().getIdentifier("widget_preview" + itemNum, "id", this.mLauncher.getPackageName());
        if (identifier == 0 || (groupItemPreview = (GroupItemPreview) findViewById(identifier)) == null) {
            return;
        }
        int[] previewSize = getPreviewSize();
        this.mRequests.put(itemNum, this.mWidgetPreviewLoader.getPreview(info, previewSize[0], previewSize[1], groupItemPreview));
        groupItemPreview.setBackgroundResource(R.drawable.btn_homescreen_set_wallpaper_normal);
        int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.group_widget_item_padding);
        groupItemPreview.setPaddingRelative(dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize);
    }

    @Override // com.android.launcher3.widget.WidgetCell
    public void clear() {
        super.clear();
        for (int size = this.mRequests.size() - 1; size >= 0; size--) {
            this.mRequests.valueAt(size).cleanup();
            this.mRequests.removeAt(size);
        }
    }

    public void applyFromSearchedInfo(String prefix, String body, String postfix) {
        TextView textView;
        if (prefix == null || body == null || postfix == null || (textView = (TextView) findViewById(R.id.widget_name)) == null) {
            return;
        }
        SpannableStringBuilder spannableStringBuilderAppend = new SpannableStringBuilder(prefix).append((CharSequence) body).append((CharSequence) postfix);
        int length = prefix.length();
        int length2 = body.length() + length;
        if (spannableStringBuilderAppend != null) {
            spannableStringBuilderAppend.setSpan(new ForegroundColorSpan(DDTUtils.getLGEColor(getContext(), "color_accent_ui")), length, length2, 0);
        }
        textView.setText(spannableStringBuilderAppend);
    }
}
