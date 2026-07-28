package androidx.core.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.CancellationSignal;
import android.util.Log;
import androidx.core.content.res.FontResourcesParserCompat;
import androidx.core.provider.FontsContractCompat;
import com.android.launcher3.Workspace;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: classes.dex */
class TypefaceCompatBaseImpl {
    private static final int INVALID_KEY = 0;
    private static final String TAG = "TypefaceCompatBaseImpl";
    private ConcurrentHashMap<Long, FontResourcesParserCompat.FontFamilyFilesResourceEntry> mFontFamilies = new ConcurrentHashMap<>();

    private interface StyleExtractor<T> {
        int getWeight(T t);

        boolean isItalic(T t);
    }

    TypefaceCompatBaseImpl() {
    }

    private static <T> T findBestFont(T[] fonts, int style, StyleExtractor<T> extractor) {
        int i = (style & 1) == 0 ? 400 : Workspace.MULTI_TOUCH_EXPAND_COLLAPSE_DELAY;
        boolean z = (style & 2) != 0;
        T t = null;
        int i2 = Integer.MAX_VALUE;
        for (T t2 : fonts) {
            int iAbs = (Math.abs(extractor.getWeight(t2) - i) * 2) + (extractor.isItalic(t2) == z ? 0 : 1);
            if (t == null || i2 > iAbs) {
                t = t2;
                i2 = iAbs;
            }
        }
        return t;
    }

    private static long getUniqueKey(final Typeface typeface) {
        if (typeface == null) {
            return 0L;
        }
        try {
            Field declaredField = Typeface.class.getDeclaredField("native_instance");
            declaredField.setAccessible(true);
            return ((Number) declaredField.get(typeface)).longValue();
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Could not retrieve font from family.", e);
            return 0L;
        } catch (NoSuchFieldException e2) {
            Log.e(TAG, "Could not retrieve font from family.", e2);
            return 0L;
        }
    }

    protected FontsContractCompat.FontInfo findBestInfo(FontsContractCompat.FontInfo[] fonts, int style) {
        return (FontsContractCompat.FontInfo) findBestFont(fonts, style, new StyleExtractor<FontsContractCompat.FontInfo>() { // from class: androidx.core.graphics.TypefaceCompatBaseImpl.1
            /* JADX DEBUG: Method merged with bridge method: getWeight(Ljava/lang/Object;)I */
            @Override // androidx.core.graphics.TypefaceCompatBaseImpl.StyleExtractor
            public int getWeight(FontsContractCompat.FontInfo info) {
                return info.getWeight();
            }

            /* JADX DEBUG: Method merged with bridge method: isItalic(Ljava/lang/Object;)Z */
            @Override // androidx.core.graphics.TypefaceCompatBaseImpl.StyleExtractor
            public boolean isItalic(FontsContractCompat.FontInfo info) {
                return info.isItalic();
            }
        });
    }

    protected Typeface createFromInputStream(Context context, InputStream is) {
        File tempFile = TypefaceCompatUtil.getTempFile(context);
        if (tempFile == null) {
            return null;
        }
        try {
            if (TypefaceCompatUtil.copyToFile(tempFile, is)) {
                return Typeface.createFromFile(tempFile.getPath());
            }
            return null;
        } catch (RuntimeException unused) {
            return null;
        } finally {
            tempFile.delete();
        }
    }

    public Typeface createFromFontInfo(Context context, CancellationSignal cancellationSignal, FontsContractCompat.FontInfo[] fonts, int style) throws Throwable {
        InputStream inputStreamOpenInputStream;
        InputStream inputStream = null;
        if (fonts.length < 1) {
            return null;
        }
        try {
            inputStreamOpenInputStream = context.getContentResolver().openInputStream(findBestInfo(fonts, style).getUri());
            try {
                Typeface typefaceCreateFromInputStream = createFromInputStream(context, inputStreamOpenInputStream);
                TypefaceCompatUtil.closeQuietly(inputStreamOpenInputStream);
                return typefaceCreateFromInputStream;
            } catch (IOException unused) {
                TypefaceCompatUtil.closeQuietly(inputStreamOpenInputStream);
                return null;
            } catch (Throwable th) {
                th = th;
                inputStream = inputStreamOpenInputStream;
                TypefaceCompatUtil.closeQuietly(inputStream);
                throw th;
            }
        } catch (IOException unused2) {
            inputStreamOpenInputStream = null;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private FontResourcesParserCompat.FontFileResourceEntry findBestEntry(FontResourcesParserCompat.FontFamilyFilesResourceEntry entry, int style) {
        return (FontResourcesParserCompat.FontFileResourceEntry) findBestFont(entry.getEntries(), style, new StyleExtractor<FontResourcesParserCompat.FontFileResourceEntry>() { // from class: androidx.core.graphics.TypefaceCompatBaseImpl.2
            /* JADX DEBUG: Method merged with bridge method: getWeight(Ljava/lang/Object;)I */
            @Override // androidx.core.graphics.TypefaceCompatBaseImpl.StyleExtractor
            public int getWeight(FontResourcesParserCompat.FontFileResourceEntry entry2) {
                return entry2.getWeight();
            }

            /* JADX DEBUG: Method merged with bridge method: isItalic(Ljava/lang/Object;)Z */
            @Override // androidx.core.graphics.TypefaceCompatBaseImpl.StyleExtractor
            public boolean isItalic(FontResourcesParserCompat.FontFileResourceEntry entry2) {
                return entry2.isItalic();
            }
        });
    }

    public Typeface createFromFontFamilyFilesResourceEntry(Context context, FontResourcesParserCompat.FontFamilyFilesResourceEntry entry, Resources resources, int style) {
        FontResourcesParserCompat.FontFileResourceEntry fontFileResourceEntryFindBestEntry = findBestEntry(entry, style);
        if (fontFileResourceEntryFindBestEntry == null) {
            return null;
        }
        Typeface typefaceCreateFromResourcesFontFile = TypefaceCompat.createFromResourcesFontFile(context, resources, fontFileResourceEntryFindBestEntry.getResourceId(), fontFileResourceEntryFindBestEntry.getFileName(), style);
        addFontFamily(typefaceCreateFromResourcesFontFile, entry);
        return typefaceCreateFromResourcesFontFile;
    }

    public Typeface createFromResourcesFontFile(Context context, Resources resources, int id, String path, int style) {
        File tempFile = TypefaceCompatUtil.getTempFile(context);
        if (tempFile == null) {
            return null;
        }
        try {
            if (TypefaceCompatUtil.copyToFile(tempFile, resources, id)) {
                return Typeface.createFromFile(tempFile.getPath());
            }
            return null;
        } catch (RuntimeException unused) {
            return null;
        } finally {
            tempFile.delete();
        }
    }

    FontResourcesParserCompat.FontFamilyFilesResourceEntry getFontFamily(final Typeface typeface) {
        long uniqueKey = getUniqueKey(typeface);
        if (uniqueKey == 0) {
            return null;
        }
        return this.mFontFamilies.get(Long.valueOf(uniqueKey));
    }

    private void addFontFamily(final Typeface typeface, final FontResourcesParserCompat.FontFamilyFilesResourceEntry entry) {
        long uniqueKey = getUniqueKey(typeface);
        if (uniqueKey != 0) {
            this.mFontFamilies.put(Long.valueOf(uniqueKey), entry);
        }
    }
}
