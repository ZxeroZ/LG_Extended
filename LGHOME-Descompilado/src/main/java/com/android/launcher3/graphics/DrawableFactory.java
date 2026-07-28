package com.android.launcher3.graphics;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.os.UserHandle;
import android.util.ArrayMap;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.Utilities;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class DrawableFactory {
    private static final Object LOCK = new Object();
    private static final String TAG = "DrawableFactory";
    private static DrawableFactory sInstance;
    private Path mPreloadProgressPath;
    protected final UserHandle mMyUser = Process.myUserHandle();
    protected final ArrayMap<UserHandle, Bitmap> mUserBadges = new ArrayMap<>();

    public static DrawableFactory get(Context context) {
        DrawableFactory drawableFactory;
        synchronized (LOCK) {
            if (sInstance == null) {
                sInstance = (DrawableFactory) Utilities.getOverrideObject(DrawableFactory.class, context.getApplicationContext(), R.string.drawable_factory_class);
            }
            drawableFactory = sInstance;
        }
        return drawableFactory;
    }

    public FastBitmapDrawable newIcon(ItemInfoWithIcon info) {
        FastBitmapDrawable fastBitmapDrawable = new FastBitmapDrawable(info);
        fastBitmapDrawable.setIsDisabled(info.isDisabled());
        return fastBitmapDrawable;
    }

    public FastBitmapDrawable newIcon(com.android.launcher3.icons.BitmapInfo info, ActivityInfo target) {
        return new FastBitmapDrawable(info);
    }

    public Drawable getBadgeForUser(UserHandle user, Context context) {
        if (this.mMyUser.equals(user)) {
            return null;
        }
        Bitmap userBadge = getUserBadge(user, context);
        FastBitmapDrawable fastBitmapDrawable = new FastBitmapDrawable(userBadge);
        fastBitmapDrawable.setFilterBitmap(true);
        fastBitmapDrawable.setBounds(0, 0, userBadge.getWidth(), userBadge.getHeight());
        return fastBitmapDrawable;
    }

    protected synchronized Bitmap getUserBadge(UserHandle user, Context context) {
        Bitmap bitmap = this.mUserBadges.get(user);
        if (bitmap != null) {
            return bitmap;
        }
        Resources resources = context.getApplicationContext().getResources();
        int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.profile_badge_size);
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(dimensionPixelSize, dimensionPixelSize, Bitmap.Config.ARGB_8888);
        Drawable userBadgedDrawableForDensity = context.getPackageManager().getUserBadgedDrawableForDensity(new BitmapDrawable(resources, bitmapCreateBitmap), user, new Rect(0, 0, dimensionPixelSize, dimensionPixelSize), 0);
        if (userBadgedDrawableForDensity instanceof BitmapDrawable) {
            bitmapCreateBitmap = ((BitmapDrawable) userBadgedDrawableForDensity).getBitmap();
        } else {
            bitmapCreateBitmap.eraseColor(0);
            Canvas canvas = new Canvas(bitmapCreateBitmap);
            userBadgedDrawableForDensity.setBounds(0, 0, dimensionPixelSize, dimensionPixelSize);
            userBadgedDrawableForDensity.draw(canvas);
            canvas.setBitmap(null);
        }
        this.mUserBadges.put(user, bitmapCreateBitmap);
        return bitmapCreateBitmap;
    }
}
