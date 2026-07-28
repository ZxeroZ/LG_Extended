package androidx.core.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import androidx.collection.LruCache;
import androidx.core.content.res.FontResourcesParserCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.provider.FontsContractCompat;

/* JADX INFO: loaded from: classes.dex */
public class TypefaceCompat {
    private static final LruCache<String, Typeface> sTypefaceCache;
    private static final TypefaceCompatBaseImpl sTypefaceCompatImpl;

    static {
        if (Build.VERSION.SDK_INT >= 29) {
            sTypefaceCompatImpl = new TypefaceCompatApi29Impl();
        } else if (Build.VERSION.SDK_INT >= 28) {
            sTypefaceCompatImpl = new TypefaceCompatApi28Impl();
        } else if (Build.VERSION.SDK_INT >= 26) {
            sTypefaceCompatImpl = new TypefaceCompatApi26Impl();
        } else if (Build.VERSION.SDK_INT >= 24 && TypefaceCompatApi24Impl.isUsable()) {
            sTypefaceCompatImpl = new TypefaceCompatApi24Impl();
        } else if (Build.VERSION.SDK_INT >= 21) {
            sTypefaceCompatImpl = new TypefaceCompatApi21Impl();
        } else {
            sTypefaceCompatImpl = new TypefaceCompatBaseImpl();
        }
        sTypefaceCache = new LruCache<>(16);
    }

    private TypefaceCompat() {
    }

    public static Typeface findFromCache(Resources resources, int id, int style) {
        return sTypefaceCache.get(createResourceUid(resources, id, style));
    }

    private static String createResourceUid(final Resources resources, int id, int style) {
        return resources.getResourcePackageName(id) + "-" + id + "-" + style;
    }

    private static Typeface getSystemFontFamily(String familyName) {
        if (familyName == null || familyName.isEmpty()) {
            return null;
        }
        Typeface typefaceCreate = Typeface.create(familyName, 0);
        Typeface typefaceCreate2 = Typeface.create(Typeface.DEFAULT, 0);
        if (typefaceCreate == null || typefaceCreate.equals(typefaceCreate2)) {
            return null;
        }
        return typefaceCreate;
    }

    public static Typeface createFromResourcesFamilyXml(Context context, FontResourcesParserCompat.FamilyResourceEntry entry, Resources resources, int id, int style, ResourcesCompat.FontCallback fontCallback, Handler handler, boolean isRequestFromLayoutInflator) {
        Typeface typefaceCreateFromFontFamilyFilesResourceEntry;
        if (entry instanceof FontResourcesParserCompat.ProviderResourceEntry) {
            FontResourcesParserCompat.ProviderResourceEntry providerResourceEntry = (FontResourcesParserCompat.ProviderResourceEntry) entry;
            Typeface systemFontFamily = getSystemFontFamily(providerResourceEntry.getSystemFontFamilyName());
            if (systemFontFamily != null) {
                if (fontCallback != null) {
                    fontCallback.callbackSuccessAsync(systemFontFamily, handler);
                }
                return systemFontFamily;
            }
            boolean z = !isRequestFromLayoutInflator ? fontCallback != null : providerResourceEntry.getFetchStrategy() != 0;
            int timeout = isRequestFromLayoutInflator ? providerResourceEntry.getTimeout() : -1;
            typefaceCreateFromFontFamilyFilesResourceEntry = FontsContractCompat.requestFont(context, providerResourceEntry.getRequest(), style, z, timeout, ResourcesCompat.FontCallback.getHandler(handler), new ResourcesCallbackAdapter(fontCallback));
        } else {
            typefaceCreateFromFontFamilyFilesResourceEntry = sTypefaceCompatImpl.createFromFontFamilyFilesResourceEntry(context, (FontResourcesParserCompat.FontFamilyFilesResourceEntry) entry, resources, style);
            if (fontCallback != null) {
                if (typefaceCreateFromFontFamilyFilesResourceEntry != null) {
                    fontCallback.callbackSuccessAsync(typefaceCreateFromFontFamilyFilesResourceEntry, handler);
                } else {
                    fontCallback.callbackFailAsync(-3, handler);
                }
            }
        }
        if (typefaceCreateFromFontFamilyFilesResourceEntry != null) {
            sTypefaceCache.put(createResourceUid(resources, id, style), typefaceCreateFromFontFamilyFilesResourceEntry);
        }
        return typefaceCreateFromFontFamilyFilesResourceEntry;
    }

    public static Typeface createFromResourcesFontFile(Context context, Resources resources, int id, String path, int style) {
        Typeface typefaceCreateFromResourcesFontFile = sTypefaceCompatImpl.createFromResourcesFontFile(context, resources, id, path, style);
        if (typefaceCreateFromResourcesFontFile != null) {
            sTypefaceCache.put(createResourceUid(resources, id, style), typefaceCreateFromResourcesFontFile);
        }
        return typefaceCreateFromResourcesFontFile;
    }

    public static Typeface createFromFontInfo(Context context, CancellationSignal cancellationSignal, FontsContractCompat.FontInfo[] fonts, int style) {
        return sTypefaceCompatImpl.createFromFontInfo(context, cancellationSignal, fonts, style);
    }

    private static Typeface getBestFontFromFamily(final Context context, final Typeface typeface, final int style) {
        TypefaceCompatBaseImpl typefaceCompatBaseImpl = sTypefaceCompatImpl;
        FontResourcesParserCompat.FontFamilyFilesResourceEntry fontFamily = typefaceCompatBaseImpl.getFontFamily(typeface);
        if (fontFamily == null) {
            return null;
        }
        return typefaceCompatBaseImpl.createFromFontFamilyFilesResourceEntry(context, fontFamily, context.getResources(), style);
    }

    public static Typeface create(final Context context, final Typeface family, final int style) {
        Typeface bestFontFromFamily;
        if (context != null) {
            return (Build.VERSION.SDK_INT >= 21 || (bestFontFromFamily = getBestFontFromFamily(context, family, style)) == null) ? Typeface.create(family, style) : bestFontFromFamily;
        }
        throw new IllegalArgumentException("Context cannot be null");
    }

    public static void clearCache() {
        sTypefaceCache.evictAll();
    }

    public static class ResourcesCallbackAdapter extends FontsContractCompat.FontRequestCallback {
        private ResourcesCompat.FontCallback mFontCallback;

        public ResourcesCallbackAdapter(ResourcesCompat.FontCallback fontCallback) {
            this.mFontCallback = fontCallback;
        }

        @Override // androidx.core.provider.FontsContractCompat.FontRequestCallback
        public void onTypefaceRetrieved(Typeface typeface) {
            ResourcesCompat.FontCallback fontCallback = this.mFontCallback;
            if (fontCallback != null) {
                fontCallback.onFontRetrieved(typeface);
            }
        }

        @Override // androidx.core.provider.FontsContractCompat.FontRequestCallback
        public void onTypefaceRequestFailed(int reason) {
            ResourcesCompat.FontCallback fontCallback = this.mFontCallback;
            if (fontCallback != null) {
                fontCallback.onFontRetrievalFailed(reason);
            }
        }
    }
}
