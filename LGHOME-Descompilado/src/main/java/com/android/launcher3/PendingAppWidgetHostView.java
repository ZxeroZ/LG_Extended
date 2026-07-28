package com.android.launcher3;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.icons.IconCache;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class PendingAppWidgetHostView extends LauncherAppWidgetHostView implements View.OnClickListener {
    private static Resources.Theme sPreloaderTheme;
    private Drawable mCenterDrawable;
    private View.OnClickListener mClickListener;
    private View mDefaultView;
    private final boolean mDisabledForSafeMode;
    private boolean mDrawableSizeChanged;
    private Bitmap mIcon;
    private final Intent mIconLookupIntent;
    private final LauncherAppWidgetInfo mInfo;
    private Launcher mLauncher;
    private final TextPaint mPaint;
    private final Rect mRect;
    private Layout mSetupTextLayout;
    private final int mStartState;
    private Drawable mTopCornerDrawable;

    @Override // android.appwidget.AppWidgetHostView
    public void updateAppWidgetSize(Bundle newOptions, int minWidth, int minHeight, int maxWidth, int maxHeight) {
    }

    public PendingAppWidgetHostView(Context context, LauncherAppWidgetInfo info, boolean disabledForSafeMode) {
        super(context);
        this.mRect = new Rect();
        this.mLauncher = (Launcher) context;
        this.mInfo = info;
        this.mStartState = info.restoreStatus;
        this.mIconLookupIntent = new Intent().setComponent(info.providerName);
        this.mDisabledForSafeMode = disabledForSafeMode;
        TextPaint textPaint = new TextPaint();
        this.mPaint = textPaint;
        textPaint.setColor(-1);
        textPaint.setTextSize(TypedValue.applyDimension(0, this.mLauncher.getDeviceProfile().iconTextSizePx, getResources().getDisplayMetrics()));
        setBackgroundResource(R.drawable.quantum_panel_dark);
        setWillNotDraw(false);
    }

    @Override // com.android.launcher3.LauncherAppWidgetHostView, com.lge.lgewidgetlib.LgeAppWidgetHostView, android.appwidget.AppWidgetHostView
    protected View getDefaultView() {
        if (this.mDefaultView == null) {
            View viewInflate = this.mInflater.inflate(R.layout.appwidget_not_ready, (ViewGroup) this, false);
            this.mDefaultView = viewInflate;
            viewInflate.setOnClickListener(this);
            applyState();
        }
        return this.mDefaultView;
    }

    @Override // android.view.View
    public void setOnClickListener(View.OnClickListener l) {
        this.mClickListener = l;
    }

    @Override // com.android.launcher3.LauncherAppWidgetHostView
    public boolean isReinflateRequired() {
        return this.mStartState != this.mInfo.restoreStatus;
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mDrawableSizeChanged = true;
    }

    public void updateIcon(IconCache cache) {
        Bitmap icon = cache.getIcon(this.mIconLookupIntent, this.mInfo.user);
        if (this.mIcon == icon) {
            return;
        }
        this.mIcon = icon;
        Drawable drawable = this.mCenterDrawable;
        if (drawable != null) {
            drawable.setCallback(null);
            this.mCenterDrawable = null;
        }
        Bitmap bitmap = this.mIcon;
        if (bitmap != null) {
            if (this.mDisabledForSafeMode) {
                FastBitmapDrawable fastBitmapDrawableCreateIconDrawable = Launcher.createIconDrawable(bitmap, this.mLauncher.getDeviceProfile().iconSizePx);
                fastBitmapDrawableCreateIconDrawable.setState(FastBitmapDrawable.State.DISABLED);
                this.mCenterDrawable = fastBitmapDrawableCreateIconDrawable;
                this.mTopCornerDrawable = null;
            } else if (isReadyForClickSetup()) {
                this.mCenterDrawable = getResources().getDrawable(R.drawable.ic_setting);
                this.mTopCornerDrawable = new FastBitmapDrawable(this.mIcon);
            } else {
                if (sPreloaderTheme == null) {
                    Resources.Theme themeNewTheme = getResources().newTheme();
                    sPreloaderTheme = themeNewTheme;
                    themeNewTheme.applyStyle(R.style.PreloadIcon, true);
                }
                PreloadIconDrawable preloadIconDrawable = new PreloadIconDrawable(Launcher.createIconDrawable(this.mIcon, this.mLauncher.getDeviceProfile().iconSizePx), sPreloaderTheme);
                this.mCenterDrawable = preloadIconDrawable;
                preloadIconDrawable.setCallback(this);
                this.mTopCornerDrawable = null;
                applyState();
            }
            this.mDrawableSizeChanged = true;
        }
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable who) {
        return who == this.mCenterDrawable || super.verifyDrawable(who);
    }

    public void applyState() {
        Drawable drawable = this.mCenterDrawable;
        if (drawable != null) {
            drawable.setLevel(Math.max(this.mInfo.installProgress, 0));
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        View.OnClickListener onClickListener = this.mClickListener;
        if (onClickListener != null) {
            onClickListener.onClick(this);
        }
    }

    public boolean isReadyForClickSetup() {
        return (this.mInfo.restoreStatus & 2) == 0 && (this.mInfo.restoreStatus & 4) != 0;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.mCenterDrawable == null) {
            return;
        }
        DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
        if (this.mTopCornerDrawable == null) {
            if (this.mDrawableSizeChanged) {
                Drawable drawable = this.mCenterDrawable;
                int outset = drawable instanceof PreloadIconDrawable ? ((PreloadIconDrawable) drawable).getOutset() : 0;
                int iMin = Math.min(deviceProfile.iconSizePx + (outset * 2), Math.min((getWidth() - getPaddingLeft()) - getPaddingRight(), (getHeight() - getPaddingTop()) - getPaddingBottom()));
                this.mRect.set(0, 0, iMin, iMin);
                this.mRect.inset(outset, outset);
                this.mRect.offsetTo((getWidth() - this.mRect.width()) / 2, (getHeight() - this.mRect.height()) / 2);
                this.mCenterDrawable.setBounds(this.mRect);
                this.mDrawableSizeChanged = false;
            }
            this.mCenterDrawable.draw(canvas);
            return;
        }
        if (this.mDrawableSizeChanged) {
            int i = deviceProfile.iconSizePx;
            int paddingTop = getPaddingTop();
            int paddingBottom = getPaddingBottom();
            int paddingLeft = getPaddingLeft();
            int paddingRight = getPaddingRight();
            int width = (getWidth() - paddingLeft) - paddingRight;
            int height = (getHeight() - paddingTop) - paddingBottom;
            StaticLayout staticLayout = new StaticLayout(getResources().getText(R.string.gadget_setup_text), this.mPaint, width, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, true);
            this.mSetupTextLayout = staticLayout;
            if (staticLayout.getLineCount() == 1) {
                int iMin2 = Math.min(i, Math.min(width, height - this.mSetupTextLayout.getHeight()));
                this.mRect.set(0, 0, iMin2, iMin2);
                this.mRect.offsetTo((getWidth() - this.mRect.width()) / 2, (((getHeight() - this.mRect.height()) - this.mSetupTextLayout.getHeight()) - deviceProfile.iconDrawablePaddingPx) / 2);
                this.mTopCornerDrawable.setBounds(this.mRect);
                this.mRect.left = paddingLeft;
                Rect rect = this.mRect;
                rect.top = rect.bottom + deviceProfile.iconDrawablePaddingPx;
            } else {
                this.mSetupTextLayout = null;
                int iMin3 = Math.min(i, Math.min((getWidth() - paddingLeft) - paddingRight, (getHeight() - paddingTop) - paddingBottom));
                this.mRect.set(0, 0, iMin3, iMin3);
                this.mRect.offsetTo((getWidth() - this.mRect.width()) / 2, (getHeight() - this.mRect.height()) / 2);
                this.mCenterDrawable.setBounds(this.mRect);
                int iMin4 = Math.min(iMin3 / 2, Math.max(this.mRect.top - paddingTop, this.mRect.left - paddingLeft));
                this.mTopCornerDrawable.setBounds(paddingLeft, paddingTop, paddingLeft + iMin4, iMin4 + paddingTop);
            }
            this.mDrawableSizeChanged = false;
        }
        if (this.mSetupTextLayout == null) {
            this.mCenterDrawable.draw(canvas);
            this.mTopCornerDrawable.draw(canvas);
            return;
        }
        canvas.save();
        canvas.translate(this.mRect.left, this.mRect.top);
        this.mSetupTextLayout.draw(canvas);
        canvas.restore();
        this.mTopCornerDrawable.draw(canvas);
    }
}
