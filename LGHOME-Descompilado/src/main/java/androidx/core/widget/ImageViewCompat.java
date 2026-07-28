package androidx.core.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.ImageView;

/* JADX INFO: loaded from: classes.dex */
public class ImageViewCompat {
    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.widget.ImageView */
    /* JADX WARN: Multi-variable type inference failed */
    public static ColorStateList getImageTintList(ImageView view) {
        if (Build.VERSION.SDK_INT >= 21) {
            return view.getImageTintList();
        }
        if (view instanceof TintableImageSourceView) {
            return ((TintableImageSourceView) view).getSupportImageTintList();
        }
        return null;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.widget.ImageView */
    /* JADX WARN: Multi-variable type inference failed */
    public static void setImageTintList(ImageView view, ColorStateList tintList) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= 21) {
            view.setImageTintList(tintList);
            if (Build.VERSION.SDK_INT != 21 || (drawable = view.getDrawable()) == null || view.getImageTintList() == null) {
                return;
            }
            if (drawable.isStateful()) {
                drawable.setState(view.getDrawableState());
            }
            view.setImageDrawable(drawable);
            return;
        }
        if (view instanceof TintableImageSourceView) {
            ((TintableImageSourceView) view).setSupportImageTintList(tintList);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.widget.ImageView */
    /* JADX WARN: Multi-variable type inference failed */
    public static PorterDuff.Mode getImageTintMode(ImageView view) {
        if (Build.VERSION.SDK_INT >= 21) {
            return view.getImageTintMode();
        }
        if (view instanceof TintableImageSourceView) {
            return ((TintableImageSourceView) view).getSupportImageTintMode();
        }
        return null;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: android.widget.ImageView */
    /* JADX WARN: Multi-variable type inference failed */
    public static void setImageTintMode(ImageView view, PorterDuff.Mode mode) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= 21) {
            view.setImageTintMode(mode);
            if (Build.VERSION.SDK_INT != 21 || (drawable = view.getDrawable()) == null || view.getImageTintList() == null) {
                return;
            }
            if (drawable.isStateful()) {
                drawable.setState(view.getDrawableState());
            }
            view.setImageDrawable(drawable);
            return;
        }
        if (view instanceof TintableImageSourceView) {
            ((TintableImageSourceView) view).setSupportImageTintMode(mode);
        }
    }

    private ImageViewCompat() {
    }
}
