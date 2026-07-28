package com.lge.launcher3.smartbulletin.view;

import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.lge.launcher3.R;
import com.lge.launcher3.smartbulletin.info.SBAppWidgetProviderInfo;
import com.lge.launcher3.util.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class SBNotiView extends ImageView {
    private static int sImageHeight;
    private static int sImagePadding;
    private static int sImageWidth;
    private SBNoti mNoti;

    public SBNotiView(Context context) {
        this(context, null);
    }

    public SBNotiView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SBNotiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Resources resources = getResources();
        sImageWidth = resources.getDimensionPixelSize(R.dimen.smartbulletin_noti_size);
        sImageHeight = resources.getDimensionPixelSize(R.dimen.smartbulletin_noti_size);
        sImagePadding = resources.getDimensionPixelSize(R.dimen.smartbulletin_noti_padding);
    }

    public void setNoti(SBNoti noti) {
        this.mNoti = noti;
    }

    public SBNoti getNoti() {
        return this.mNoti;
    }

    private static Drawable getNotiIcon(Context context, String resUri) {
        if (resUri != null && resUri.startsWith("android.resource://")) {
            Uri uri = Uri.parse(resUri);
            String path = uri.getPath();
            String host = uri.getHost();
            String[] strArrSplit = path.split("/");
            String str = strArrSplit[1];
            String str2 = strArrSplit[2];
            try {
                Resources resourcesForApplication = context.getPackageManager().getResourcesForApplication(host);
                return resourcesForApplication.getDrawable(resourcesForApplication.getIdentifier(str2, str, host));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setWidgetDrawableRes(SBAppWidgetProviderInfo info, String resUri) {
        Drawable notiIcon = getNotiIcon(getContext(), resUri);
        if (notiIcon == null) {
            AppWidgetProviderInfo appWidgetProviderInfo = info.mAppWidgetProviderInfo;
            String packageName = appWidgetProviderInfo.provider.getPackageName();
            if (appWidgetProviderInfo.icon > 0) {
                notiIcon = getContext().getPackageManager().getDrawable(packageName, appWidgetProviderInfo.icon, null);
            }
            if (notiIcon == null) {
                notiIcon = getContext().getResources().getDrawable(R.drawable.smartbulletin_ic_numberbadge);
            }
        }
        BitmapDrawable bitmapDrawable = (BitmapDrawable) Utilities.scaleBitmapDrawable(getContext(), notiIcon, sImageWidth, sImageHeight);
        if (bitmapDrawable != null) {
            setImageBitmap(getCircleBitmap(bitmapDrawable.getBitmap()));
        }
        setTag(info);
    }

    private static Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-12434878);
        int width = bitmap.getWidth() / 2;
        paint.setStrokeWidth(0.0f);
        float f = width;
        canvas.drawCircle(f, f, width - sImagePadding, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return bitmapCreateBitmap;
    }

    public boolean equalsType(String notiType) {
        SBNoti sBNoti;
        return (notiType == null || (sBNoti = this.mNoti) == null || !notiType.equals(sBNoti.mNotiType)) ? false : true;
    }
}
