package com.lge.launcher3.badge.appnotifier;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.NinePatchDrawable;
import com.lge.launcher3.R;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class AppNotifierBadgeDrawable extends BitmapDrawable {
    public static final String TAG = "AppNotifier.Drawable";

    public AppNotifierBadgeDrawable(Resources res, Builder builder) {
        super(res, builder.mBitmap);
    }

    public static class Builder {
        private static Typeface sFontType;
        private static Paint sTextPaint;
        private NinePatchDrawable mAppNotifierBadgeDrawable;
        private Bitmap mBitmap;

        static {
            try {
                sFontType = Typeface.createFromFile("/system/fonts/Roboto-Bold.ttf");
            } catch (Exception unused) {
                sFontType = Typeface.create(Typeface.DEFAULT, 1);
            }
            Paint paint = new Paint();
            sTextPaint = paint;
            paint.setAntiAlias(true);
            sTextPaint.setTextAlign(Paint.Align.CENTER);
            sTextPaint.setTypeface(sFontType);
        }

        public BitmapDrawable build(Context context, String strCount) {
            int lGEColor = Utilities.sWhite;
            try {
                lGEColor = DDTUtils.getLGEColor(context, "badge_number_text_color");
            } catch (Resources.NotFoundException unused) {
                LGLog.d(AppNotifierBadgeDrawable.TAG, "Failed to get color: badge_number_text_color");
            }
            sTextPaint.setColor(lGEColor);
            sTextPaint.setTextSize(context.getResources().getDimension(R.dimen.appnotifier_font_size));
            sTextPaint.setTypeface(Typeface.defaultFromStyle(1));
            int dimension = (int) context.getResources().getDimension(R.dimen.appnotifier_badge_size);
            if (this.mAppNotifierBadgeDrawable == null) {
                try {
                    this.mAppNotifierBadgeDrawable = (NinePatchDrawable) context.getResources().getDrawable(33685581, null);
                } catch (NoSuchFieldError e) {
                    this.mAppNotifierBadgeDrawable = (NinePatchDrawable) context.getResources().getDrawable(R.drawable.ic_homescreen_badge_normal, null);
                    LGLog.d(AppNotifierBadgeDrawable.TAG, e.toString());
                }
            }
            Rect rect = new Rect();
            if (strCount.length() > 0) {
                sTextPaint.getTextBounds(strCount, 0, strCount.length() - 1, rect);
            }
            int iWidth = strCount.length() < 2 ? dimension : rect.width() + dimension;
            this.mBitmap = Bitmap.createBitmap(context.getResources().getDisplayMetrics(), iWidth, dimension, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(this.mBitmap);
            NinePatchDrawable ninePatchDrawable = this.mAppNotifierBadgeDrawable;
            if (ninePatchDrawable != null) {
                ninePatchDrawable.setFilterBitmap(true);
                this.mAppNotifierBadgeDrawable.setDither(true);
                this.mAppNotifierBadgeDrawable.setBounds(0, 0, iWidth, dimension);
                this.mAppNotifierBadgeDrawable.draw(canvas);
            }
            canvas.drawText(strCount, this.mBitmap.getWidth() / 2, (this.mBitmap.getHeight() / 2) + context.getResources().getDimension(R.dimen.appnotifier_font_padding_top), sTextPaint);
            return new AppNotifierBadgeDrawable(context.getResources(), this);
        }
    }
}
