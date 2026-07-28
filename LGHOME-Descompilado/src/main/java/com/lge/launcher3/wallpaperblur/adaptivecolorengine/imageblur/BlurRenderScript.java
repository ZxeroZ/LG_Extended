package com.lge.launcher3.wallpaperblur.adaptivecolorengine.imageblur;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class BlurRenderScript {
    private static final String TAG = "BlurRenderScript";

    public static Bitmap blur(Context context, Bitmap source, int radius) {
        if (radius > 25) {
            radius = 25;
        }
        RenderScript renderScriptCreate = RenderScript.create(context);
        LGLog.i(TAG, String.format("blur() : context(%s, %s, %s), radius(%d)", context, context.getApplicationContext(), renderScriptCreate.getApplicationContext(), Integer.valueOf(radius)));
        ScriptIntrinsicBlur scriptIntrinsicBlurCreate = ScriptIntrinsicBlur.create(renderScriptCreate, Element.U8_4(renderScriptCreate));
        Allocation allocationAllocateFromeBitmap = allocateFromeBitmap(renderScriptCreate, source);
        scriptIntrinsicBlurCreate.setInput(allocationAllocateFromeBitmap);
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(source);
        Allocation allocationAllocateFromeBitmap2 = allocateFromeBitmap(renderScriptCreate, bitmapCreateBitmap);
        scriptIntrinsicBlurCreate.setRadius(radius);
        scriptIntrinsicBlurCreate.forEach(allocationAllocateFromeBitmap2);
        allocationAllocateFromeBitmap2.copyTo(bitmapCreateBitmap);
        allocationAllocateFromeBitmap2.destroy();
        allocationAllocateFromeBitmap.destroy();
        scriptIntrinsicBlurCreate.destroy();
        renderScriptCreate.finish();
        renderScriptCreate.destroy();
        return bitmapCreateBitmap;
    }

    public static Bitmap blur(Context context, Bitmap source, int radius, int blurValue) {
        if (blurValue > 25) {
            blurValue = 25;
        }
        RenderScript renderScriptCreate = RenderScript.create(context);
        LGLog.i(TAG, String.format("blur() : context(%s, %s, %s), radius(%d)", context, context.getApplicationContext(), renderScriptCreate.getApplicationContext(), Integer.valueOf(blurValue)));
        ScriptIntrinsicBlur scriptIntrinsicBlurCreate = ScriptIntrinsicBlur.create(renderScriptCreate, Element.U8_4(renderScriptCreate));
        Allocation allocationAllocateFromeBitmap = allocateFromeBitmap(renderScriptCreate, source);
        scriptIntrinsicBlurCreate.setInput(allocationAllocateFromeBitmap);
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(source);
        Allocation allocationAllocateFromeBitmap2 = allocateFromeBitmap(renderScriptCreate, bitmapCreateBitmap);
        scriptIntrinsicBlurCreate.setRadius(blurValue);
        scriptIntrinsicBlurCreate.forEach(allocationAllocateFromeBitmap2);
        allocationAllocateFromeBitmap2.copyTo(bitmapCreateBitmap);
        allocationAllocateFromeBitmap2.destroy();
        allocationAllocateFromeBitmap.destroy();
        scriptIntrinsicBlurCreate.destroy();
        renderScriptCreate.finish();
        renderScriptCreate.destroy();
        return bitmapCreateBitmap;
    }

    private static Allocation allocateFromeBitmap(RenderScript renderScript, Bitmap source) {
        return Allocation.createFromBitmap(renderScript, source, Allocation.MipmapControl.MIPMAP_NONE, 131);
    }
}
