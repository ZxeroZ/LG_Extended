package com.lge.launcher3.folder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import com.lge.launcher3.R;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class FolderColorUtil {
    private static int[] sFolderTextColor = {R.color.folder_text_color_01, R.color.folder_text_color_02, R.color.folder_text_color_03, R.color.folder_text_color_04, R.color.folder_text_color_05, R.color.folder_text_color_06, R.color.folder_text_color_07, R.color.folder_text_color_08, R.color.folder_text_color_09, R.color.folder_text_color_10, R.color.folder_text_color_11, R.color.folder_text_color_12};
    private static int[] sFolderBGColor = {R.color.folder_bg_color_01, R.color.folder_bg_color_02, R.color.folder_bg_color_03, R.color.folder_bg_color_04, R.color.folder_bg_color_05, R.color.folder_bg_color_06, R.color.folder_bg_color_07, R.color.folder_bg_color_08, R.color.folder_bg_color_09, R.color.folder_bg_color_10, R.color.folder_bg_color_11, R.color.folder_bg_color_12, R.color.folder_bg_color_13};
    private static WeakReference<Bitmap> sLauncherIconMask = null;

    public static int getColorMax() {
        return sFolderBGColor.length;
    }

    public static int getFolderTextColor(Context context, int index) {
        return context.getResources().getColor(sFolderTextColor[index], null);
    }

    public static int getFolderBGColor(Context context, int index) {
        return (index < 0 || index >= getColorMax()) ? index : context.getResources().getColor(sFolderBGColor[index], null);
    }

    public static Drawable getFolderColorDrawble(Context context, int index) {
        Drawable drawable = context.getResources().getDrawable(R.drawable.btn_homescreen_color_picker_edit_s_bg);
        drawable.setTint(getFolderBGColor(context, index));
        return drawable;
    }

    public static Bitmap getFolderIconMask(Context context) {
        WeakReference<Bitmap> weakReference = sLauncherIconMask;
        if (weakReference == null || weakReference.get() == null) {
            sLauncherIconMask = new WeakReference<>(BitmapFactory.decodeResource(context.getResources(), R.drawable.bg_homescreen_folder_icon_01));
        }
        return sLauncherIconMask.get();
    }

    public static void destoryFolderIconMask() {
        WeakReference<Bitmap> weakReference = sLauncherIconMask;
        if (weakReference != null) {
            weakReference.clear();
            sLauncherIconMask = null;
        }
    }

    public static Drawable getFolderDialogDrawble(Context context, int index) {
        Drawable drawable = context.getResources().getDrawable(R.drawable.list_color_picker_custom_color);
        drawable.setTint(getFolderBGColor(context, index));
        return drawable;
    }
}
